# Make division novice-friendly :)
from __future__ import division, with_statement

# GeoGebra imports
from geogebra.common.plugin import GeoClass
from geogebra.common.plugin import Operation as OP
from geogebra.common.plugin import EuclidianStyleConstants as STYLE
from geogebra.plugin.jython import PythonScriptInterface
from geogebra.awt import Color
from geogebra.plugin.jython import PythonAPI as API

# Java imports
from javax.swing import (
    JFrame, JPanel, JTextArea, JScrollPane, BoxLayout, JButton, JList,
    DefaultListModel, ListCellRenderer, BorderFactory, JTextPane,
    JMenuBar, JMenu, JMenuItem, JFileChooser,
    KeyStroke,
    JTabbedPane,
)
from javax.swing.text import StyleContext, StyleConstants, SimpleAttributeSet, TabStop, TabSet
from javax.swing.event import DocumentListener

from java.awt import Toolkit, Component, BorderLayout, Color as awtColor, GridLayout, Font
from java.awt.event import KeyListener, ActionListener, KeyEvent, ActionEvent

# Jython imports
import sys, re

# Python imports
from generic import generic, specmethod, GenericMethods, GenericError, sign

try:
    from javax.swing.filechooser import FileNameExtensionFilter
except ImportError:
    # This is Java < 6: reimplement this class in Python
    from javax.swing.filechooser import FileFilter
    
    class FileNameExtensionFilter(FileFilter):
        def __init__(self, description, extensions):
            self._description = description
            self.extensions = ["." + ext for ext in extensions]
        def getDescription(self):
            return self._description
        def accept(self, file):
            if file.isDirectory():
                return True
            name = file.name
            return any(name.endswith(ext) for ext in self.extensions)


Number = (int, long, float)


class Interface(PythonScriptInterface):
    def init(self, app):
        global api, ggbapi, pywindow, selection
        pywindow = self.pywin = PythonWindow()
        self.geo = Geo()
        selection = self.selection = Selection()
        sys.stdout = self.pywin
        api = API(app)
        ggbapi = api.getGgbApi()
        self.namespace = {
            'Color': Color,
            'Point': Point,
            'Element': Element,
            'Number': Numeric,
            'Vector': Vector,
            'Line': Line,
            'Segment': Segment,
            'Ray': Ray,
            'Function': Function,
            'Conic': Conic,
            'Circle': Circle,
            'Ellipse': Ellipse,
            'Hyperbola': Hyperbola,
            'Parabola': Parabola,
            'Locus': Locus,
            'Text': Text,
            'ggbApplet': ggbapi,
            'Intersect': Intersect,
            'geo': self.geo,
            'selection': self.selection,
            'pointlist': pointlist,
            'interactive': interactive,
            'input': input,
            'debug': debug,
            'alert': alert,
        }
        self.namespace.update(unary_functions)
        self.handling_event = False
        self.pywin.add("*** Python Interface initialised ***\n", "output")

    def execute(self, script):
        try:
            exec script in self.namespace
        except Exception, e:
            self.pywin.error("Runtime Error: " + str(e))
            raise        
    def handleEvent(self, evt_type, target):
        target = API.Geo(target)
        if self.handling_event:
            return
        element = self.geo._get_element(target)
        try:
            action = getattr(element, "on" + evt_type)
        except AttributeError:
            return
        try:
            self.handling_event = True
            action(element)
        except Exception:
            self.pywin.error("Error while handling event '%s' on '%r'"
                           % (evt_type, target))
            raise
        finally:
            self.handling_event = False
    def notifySelected(self, geo, add):
        geo = API.Geo(geo)
        if self.selection_listener:
            el = self.geo._get_element(geo)
            self.selection_listener.send(el, add)
            if self.selection_listener.done:
                self.remove_selection_listener()
        else:
            # We shouldn't get here :)
            self.remove_selection_listener()
    def toggleWindow(self):
        self.pywin.toggle_visibility()
    def isWindowVisible(self):
        return self.pywin.frame.visible
    def set_selection_listener(self, sl):
        api.startSelectionListener()
        self.selection_listener = sl
    def remove_selection_listener(self):
        api.stopSelectionListener();
        self.selection_listener = None
    def compileinteractive(self, source):
        try:
            return compile(source, "<pyggb>", "single")
        except SyntaxError, e:
            if e.msg in (r"no viable alternative at input '<EOF>'",
                         r"mismatched input '\n' expecting INDENT"):
                return "continue"
            else:
                self.pywin.error("Syntax Error: " + e.msg)
                return "error"
    def compilemodule(self, source):
        try:
            return compile(source, "<pyggb>", "exec")
        except SyntaxError, e:
            self.pywin.error("Syntax Error: " + e.msg)
            return "error"
    def run(self, code):
        #stdout = sys.stdout
        #f = Out()
        #sys.stdout = f
        try:
            exec code in self.namespace
        except Exception, e:
            self.pywin.error("Runtime Error: " + str(e))
            raise
        #finally:
        #    sys.stdout = stdout
        #    if f.value.endswith('\n'):
        #        f.value = f.value[:-1]
        #    if f.value:
        #        self.pywin.add(f.value, "output")
        return "OK"

interface = Interface()

class TestSelectionListener(object):
    def __init__(self):
        self.geos = []
        self.done = False
    def __nonzero__(self):
        return not self.done
    def send(self, geo, add):
        self.geos.append(geo)
        if len(self.geos) == 3:
            self.done = True

class interactive(object):
    def __init__(self, f):
        self.objs = []
        self.done = False
        self.f = f
        self.nargs = f.func_code.co_argcount
        interface.set_selection_listener(self)
    def __nonzero__(self):
        return not self.done
    def send(self, obj, add):
        self.objs.append(obj)
        if len(self.objs) == self.nargs:
            self.done = True
            self.f(*self.objs)

LABEL_MODES = ["name", "name+value", "value", "caption"]

class Element(GenericMethods):

    @generic
    def __init__(self, *args, **kwargs):
        elements = [element(arg) for arg in args]
        self.init(*elements)
        for key, value in kwargs.iteritems():
            setattr(self, key, value)

    @generic
    def init(self, *args):
        raise GenericError
    
    @specmethod.__init__
    @sign(API.Geo)
    def initfromgeo(self, geo):
        self.geo = geo
    
    @classmethod
    def fromgeo(cls, geo):
        el = object.__new__(cls)
        el.geo = geo        
        return el

    def __repr__(self):
        return "<%s>" % self.geo
    def __str__(self):
        if self.label is not None:
            return self.label
        else:
            return "(unnamed)"
    
    # property: label 
    def _getlabel(self):
        return self.geo.getLabel()
    def _setlabel(self, label):
        self.geo.setLabel(label)
        self.geo.updateRepaint()
    label = property(_getlabel, _setlabel)

    # property: color
    def _getcolor(self):
        return self.geo.getColor()
    def _setcolor(self, col):
        self.geo.setColor(col)
        self.geo.updateRepaint()
    color = property(_getcolor, _setcolor)

    # property: caption
    def _getcaption(self):
        return self.geo.getCaption()
    def _setcaption(self, value):
        self.geo.setCaption(value)
        self.geo.updateRepaint()
    caption = property(_getcaption, _setcaption)

    # property: label_mode
    def _getlabel_mode(self):
        return LABEL_MODES[self.geo.getLabelMode()]
    def _setlabel_mode(self, mode):
        try:
            mode = LABEL_MODES.index(mode)
            self.geo.setLabelMode(mode)
        except ValueError:
            raise ValueError("illegal label mode: %s", mode)
    label_mode = property(_getlabel_mode, _setlabel_mode)

    # property: label_color
    def _getlabel_color(self):
        return self.geo.getLabelColor()
    def _setlabel_color(self, color):
        self.geo.setLabelColor(color)
        self.geo.updateRepaint()
    label_color = property(_getlabel_color, _setlabel_color)
    
    # property: label_visible
    def _getlabel_visible(self):
        return self.geo.isLabelVisible()
    def _setlabel_visible(self, val):
        self.geo.setLabelVisible(bool(val))
    label_visible = property(_getlabel_visible, _setlabel_visible)
    
    # property: background_color
    def _getbgcolor(self):
        return self.geo.getBackgroundColor()
    def _setbgcolor(self, val):
        self.geo.setBackgroundColor(val)
    background_color = property(_getbgcolor, _setbgcolor)
    
    # property: visible
    def _getvisible(self):
        return self.geo.isEuclidianVisible()
    def _setvisible(self, value):
        self.geo.setEuclidianVisible(bool(value))
        self.geo.updateRepaint()
    visible = property(_getvisible, _setvisible)
    
    def __geo__(self):
        return self.geo


def expr(obj):
    if isinstance(obj, Number):
        return NumberExpression(obj)
    elif isinstance(obj, tuple) and len(obj) == 2:
        x, y = obj
        return VectorExpression(x, y)
    #elif isinstance(obj, ExpressionNode):
    #    return Expression.fromnode(obj)
    elif isinstance(obj, Expression):
        return obj
    else:
        raise TypeError("Can't convert object to Expression: %r" % obj)


def element(obj):
    if isinstance(obj, Element):
        return obj
    return Geo._get_element(expr(obj).__geo__())


class Expression(GenericMethods):
    @generic
    def __init__(self, expr):
        self.expr = expr

    @classmethod
    def fromnode(self, node):
        val = node.evaluate()
        if val.isNumber():
            return NumberExpression(node)
        elif val.isVector():
            return VectorExpression(node)
        elif val.isBoolean():
            return BooleanExpression(node)
        else:
            # TODO add other types of expressions
            return Expression(node)
    
    def __hash__(self):
        return hash(self.geo)

    def getnode(self):
        return api.nodeExpression(self.expr)
    
    def __repr__(self):
        return "<%s>" % self.expr
    
    def _binop(self, other, opcode, reverse=False):
        try:
            self = expr(self)
            other = expr(other)
        except TypeError:
            return NotImplemented
        if reverse:
            self, other = other, self
        x = self.expr
        y = other.expr
        node = api.nodeExpression(x, opcode, y)
        return self.fromnode(node)
    
    # Arithmetic operators
    def __add__(self, other):
        return self._binop(other, OP.PLUS)
    def __radd__(self, other):
        return self._binop(other, OP.PLUS, True)
    def __mul__(self, other):
        return self._binop(other, OP.MULTIPLY)
    def __rmul__(self, other):
        return self._binop(other, OP.MULTIPLY, True)
    def __sub__(self, other):
        return self._binop(other, OP.MINUS)
    def __rsub__(self, other):
        return self._binop(other, OP.MINUS, True)
    def __truediv__(self, other):
        return self._binop(other, OP.DIVIDE)
    def __rtruediv__(self, other):
        return self._binop(other, OP.DIVIDE, True)
    def __pow__(self, other):
        return self._binop(other, OP.POWER)
    def __rpow__(self, other):
        return self._binop(other, OP.POWER, True)
    def __neg__(self):
        return self._binop(expr(-1), OP.MULTIPLY, self)
    def __pos__(self):
        return self

    # Comparisons
    def __lt__(self, other):
        return self._binop(other, OP.LESS)
    def __le__(self, other):
        return self._binop(other, OP.LESS_EQUAL)
    def __gt__(self, other):
        return self._binop(other, OP.GREATER)
    def __ge__(self, other):
        return self._binop(other, OP.GREATER_EQUAL)
    def __eq__(self, other):
        return self._binop(other, OP.EQUAL_BOOLEAN)
    def __neq__(self, other):
        return self._binop(other, OP.NOT_EQUAL)

    def _getvalue(self):
        val = self.expr.evaluate()
        if val.isNumber():
            return val.getNumber()
        elif val.isBoolean():
            return val.getBoolean()
        elif val.isVector():
            return tuple(val.getCoords())
        else:
            # TODO cover more types
            raise TypeError(type(val))
    value = property(_getvalue)

    def __geo__(self):
        return api.getGeo(self.expr)


Expressionable = (Number, tuple, Expression)


class NumberExpression(Expression):
    @specmethod.__init__
    @sign(Number)
    def initfromnumber(self, x):
        self.expr = api.numberExpression(float(x))
    
    def __float__(self):
        return self.value

NumberThing = (Number, NumberExpression)


class VectorExpression(Expression):
    @specmethod.__init__
    @sign(NumberThing, NumberThing)
    def initfromcoords(self, x, y):
        x, y = expr(x), expr(y)
        self.expr = api.vectorExpression(x.expr, y.expr)


VectorThing = (tuple, VectorExpression)

class BooleanExpression(Expression):
    # Can this go into Expression?
    def __nonzero__(self):
        return self.value


unary_functions_data = [
    ('cos', OP.COS),
    ('sin', OP.SIN),
    ('tan', OP.TAN),
    ('exp', OP.EXP),
    ('log', OP.LOG),
    ('arccos acos', OP.ARCCOS),
    ('arcsin asin', OP.ARCSIN),
    ('arctan atan', OP.ARCTAN),
    ('sqrt', OP.SQRT),
    ('abs', OP.ABS),
    # TODO Add some more...
]

def unary_factory(name, opcode):
    def f(e):
        return Expression(api.nodeExpression(expr(e).expr, opcode))
    f.__name__ = name
    return f

unary_functions = {}
for names, opcode in unary_functions_data:
    names = names.split()
    f = unary_factory(names[0], opcode)
    for name in names:
        unary_functions[name] = f

binary_functions_data = [
    ('arctan2 atan2', OP.ARCTAN2),
]

# TODO create binary_functions

class ExpressionElement(Element):
    def _getexpr(self):
        return self.geo
    expr = property(_getexpr)
    

class Numeric(ExpressionElement, NumberExpression):

    @specmethod.__init__
    @sign(Number)
    def initfromnumber(self, val):
        self.geo = api.geoNumber(float(val))
    
    @specmethod.__init__
    @sign(Expression)
    def initfromexpr(self, e):
        self.geo = api.geoNumber(e.expr)
    
    def __float__(self):
        return self.geo.value
    

class Boolean(ExpressionElement, Expression):
    pass


class VectorOrPoint(ExpressionElement, Expression):

    @specmethod.__init__
    @sign(NumberThing, NumberThing)
    def initfromexprcoords(self, xe, ye):
        ve = VectorExpression(xe, ye)
        self.initfromexpr(ve)
        
    def setcoords(self, x, y):
        self.geo.setCoords(float(x), float(y), 1.0)
        self.geo.updateRepaint()
    def _getcoords(self):
        return (self.x, self.y)
    def _setcoords(self, coords):
        self.setcoords(*coords)
    coords = property(_getcoords, _setcoords)
    
    def _getx(self):
        try:
            return self._x
        except AttributeError:
            self._x = NumberExpression(api.xCoordExpression(self.geo))
            return self._x
    def _setx(self, x):
            self.setcoords(x, self.y.value)
    x = property(_getx, _setx)

    def _gety(self):
        try:
            return self._y
        except AttributeError:
            self._y = NumberExpression(api.yCoordExpression(self.geo))
            return self._y
    def _sety(self, y):
            self.setcoords(self.x.value, y)
    y = property(_gety, _sety)


class Vector(VectorOrPoint):
    @specmethod.__init__
    @sign(VectorThing)
    def initfromexpr(self, e):
        self.geo = api.geoVector(expr(e).expr)

    @specmethod.__init__
    @sign(Number, Number)
    def initfromnumbercoords(self, x, y):
        self.geo = api.geoVector(float(x), float(y))

        
class Point(VectorOrPoint):
    @specmethod.__init__
    @sign(VectorThing)
    def initfromexpr(self, e):
        self.geo = api.geoPoint(expr(e).expr)


    @specmethod.__init__
    @sign(Number, Number)
    def initfromnumbercoords(self, x, y):
        self.geo = api.geoPoint(float(x), float(y))


@Vector.init.spec
@sign(Vector, Point, Point)
def initfrompoints(self, p, q):
    self.geo = api.geoVector(p.geo, q.geo);

@Vector.init.spec
@sign(Vector, Point)
def initfrompoint(self, p):
    self.geo = api.geoVector(p.geo);

@Point.init.spec
@sign(Point, Point)
def initfrompoint(self, p):
    self.geo = p.geo


class Path(Element):

    _line_types = {
        'full': STYLE.LINE_TYPE_FULL,
        'short-dash': STYLE.LINE_TYPE_DASHED_SHORT,
        'long-dash': STYLE.LINE_TYPE_DASHED_LONG,
        'dot': STYLE.LINE_TYPE_DASHED_DOTTED,
        'dash-dot': STYLE.LINE_TYPE_DOTTED,
    }
    _rev_line_types = dict((v, k) for k, v in _line_types.iteritems())
    
    def _get_thickness(self):
        return self.geo.lineThickness
    def _set_thickness(self, val):
        self.geo.lineThickness = val
        self.geo.updateRepaint()
    thickness = property(_get_thickness, _set_thickness)

    def _get_linetype(self):
        return self._rev_line_types[self.geo.lineType]
    def _set_linetype(self, val):
        try:
            self.geo.lineType = self._line_types[val]
        except KeyError:
            raise ValueError("Unknown line type: %r" % val)
        self.geo.updateRepaint()
    linetype = property(_get_linetype, _set_linetype)


class Line(Path):

    @specmethod.init
    @sign(Point, Point)
    def initfrom2points(self, p, q):
        self.geo = api.geoLinePP(p.geo, q.geo)

    @specmethod.init
    @sign(Point, Vector)
    def initfrompointandvector(self, p, v):
        self.geo = api.geoLinePV(p.geo, v.geo)

    
    def __contains__(self, p):
        if isinstance(p, Point):
            return self.geo.isOnPath(p.geo, 1e-5)
        else:
            return False
    
    def _getdirection(self):
        return Vector.fromgeo(api.geoLineDirection(self.geo))
    direction = property(_getdirection)

@Line.init.spec
@sign(Line, Point, Line)
def initfrompointandline(self, p, l):
    return api.geoLinePL(p.geo, l.geo)


class Segment(Line, ExpressionElement, NumberExpression):
    @specmethod.init
    @sign(Point, Point)
    def initfrompoints(self, p, q):
        self.geo = api.geoSegment(p.geo, q.geo)

    # property: startpoint    
    def _getstartpoint(self):
        return Geo._get_element(self.geo.getStartPoint())
    def _setstartpoint(self, p):
        self.geo.setStartPoint(element(p).geo)
    startpoint = property(_getstartpoint, _setstartpoint)
    
    # property: endpoint
    def _getendpoint(self):
        return Geo._get_element(self.geo.getEndPoint())
    def _setendpoint(self):
        self.geo.setEndPoint(element(p).geo)
    endpoint = property(_getendpoint, _setendpoint)


class Ray(Line):
    @specmethod.init
    @sign(Point, Point)
    def initfrompoints(self, p, q):
        self.geo = api.geoRayPP(p.geo, q.geo)


class Text(Element):
    def __init__(self, value, location=None):
        self.geo = api.geoText(value)
        if isinstance(location, Point):
            self.geo.setTextOrigin(location.geo)
        elif location is not None:
            raise TypeError
        self.geo.updateRepaint()

    def _setorigin(self, point):
        if isinstance(point, Point):
            self.geo.setTextOrigin(point.geo)
        else:
            raise TypeError
        self.geo.updateRepaint()
    def _getorigin(self):
        return Point.fromgeo(self.geo.getTextOrigin())
    def _delorigin(self):
        self.geo.removeTextOrigin()
        self.geo.updateRepaint()
    origin = property(_getorigin, _setorigin, _delorigin)


class Conic(Path):

    @classmethod
    def fromgeo(cls, geo):
        if geo.keepsType():
            conictype = cls._conic_types.get(geo.getTypeString(), cls)
        else:
            conictype = cls
        el = object.__new__(conictype)
        el.geo = geo
        return el
    
    @specmethod.init
    @sign(Point, Point, Point, Point, Point)
    def initfrom5points(self, *points):
        geos = [p.geo for p in points]
        self.geo = api.geoConic(geos)

    #@specmethod.init
    #@sign(Numeric, Numeric, Numeric, Numeric, Numeric, Numeric)
    #def initfrom6coeffs(self, *coeffs):
    #    geos = [c.geo for c in coeffs]
    #    self.geo = _kernel.Conic(None, geos)


class Circle(Conic):
    
    @specmethod.init
    @sign(Point, Point)
    def initfromcentreandpoint(self, p, q):
        self.geo = api.geoCircleCP(p.geo, q.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfrom3points(self, p, q, r):
        
        self.geo = api.geoCirclePPP(p.geo, q.geo, r.geo)

    @specmethod.init
    @sign(Point, Segment)
    def initfromcentreandsegment(self, c, s):
        self.geo = api.geoCircleCS(c.geo, s.geo)

    @specmethod.init
    @sign(Point, Numeric)
    def initfromcentreandradius(self, c, r):
        self.geo = api.geoCirclCRe(c.geo, element(r).geo)


class Ellipse(Conic):
    
    @specmethod.init
    @sign(Point, Point, Numeric)
    def initfromfociandhalfmajoraxis(self, p, q, a):
        self.geo = api.geoEllipseFFA(p.geo, q.geo, a.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfromfociandpoint(self, p, q, r):
        self._geo = api.geoEllipseFFP(p.geo, q.geo, r.geo)


class Hyperbola(Conic):
    
    @specmethod.init
    @sign(Point, Point, Numeric)
    def initfromfociandhalfmajoraxis(self, p, q, a):
        self.geo = api.geoHyperbolaFFA(p.geo, q.geo, a.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfromfociandpoint(self, p, q, r):
        self._geo = api.geoHyperbolaFFP(p.geo, q.geo, r.geo)


class Parabola(Conic):
    
    @specmethod.init
    @sign(Point, Line)
    def initfromfocusanddirectrix(self, p, l):
        self._geo = api.geoParabola(p.geo, l.geo)


Conic._conic_types = {
    'Circle': Circle,
    'Parabola': Parabola,
    'Hyperbola': Hyperbola,
    'Ellipse': Ellipse,
}
    

class Locus(Path):
    
    def get_point(self, param):
        if param < 0 or param > 1:
            raise ValueError("param must be between 0 and 1")
        return api.geoPointOnPath(self.geo, element(param).geo)


class ImplicitPoly(Path):

    pass


class Function(Element):
    
    def __init__(self, f):
        self.nargs = nargs = f.func_code.co_argcount
        self.varnames = varnames = f.func_code.co_varnames
        if nargs == 0:
            raise ValueError("function must have at least one variable")
        if nargs == 1:
            x = api.variableExpression(varnames[0])
            self.geo = api.geoFunction(f(x).expr, x)
        else:
            xs = [api.variableExpression(v) for v in varnames[:nargs]]
            self.geo = api.geoFunctionNVar(f(*xs).expr, xs)
    
    def __call__(self, *args):
        args = map(expr, args)

    def _getimplicitcurve(self):
        if self.nargs != 2:
            raise AttributeError
        else:
            geo = api.geoImplicitPoly(self.geo)
            return ImplicitPoly(geo)
    implicitcurve = property(_getimplicitcurve)

class Intersect(GenericMethods):
    
    def __init__(self, *args):
        elements = [element(x) for x in args]
        geos = self.find(*elements)
        self.intersections = map(Geo._get_element, geos)

    @generic
    def find(self, *args):
        raise GenericError

    @specmethod.find
    @sign(Line, Line)
    def twolines(self, l1, l2):
        return [api.intersectLines(l1.geo, l2.geo)]

    @specmethod.find
    @sign(Line, Conic)
    def lineconic(self, l, c):
        return api.intersectLineConic(l.geo, c.geo)

    @specmethod.find
    @sign(Conic, Line)
    def conicline(self, c, l):
        return self.lineconic(l, c)

    @specmethod.find
    @sign(Conic, Conic)
    def twoconics(self, c1, c2):
        return api.intersectConics(c1.geo, c2.geo)
    
    def __repr__(self):
        return "[%s]" % ", ".join(map(repr, self.intersections))

    def __getitem__(self, i):
        return self.intersections[i]

    def __iter__(self):
        for i in self.intersections:
            yield i


class Selection(object):
    @property
    def all(self):
        return map(Geo._get_element, api.getSelectedGeos())
    
    def filter(self, eltype):
        return [x for x in self.all if isinstance(x, eltype)]
    
    @property
    def points(self):
        return self.filter(Point)

    @property
    def lines(self):
        return self.filter(Line)
    
    @property
    def segments(self):
        return self.filter(Segment)
    
    @property
    def vectors(self):
        return self.filter(Vector)


def pointlist():
    return map(Geo._get_element, api.getGeos(GeoClass.POINT))

def debug(s):
    ggbapi.debug(s)
def alert(s):
    ggbapi.alert(s)
def input(s, t = ""):
    ret = ggbapi.prompt(s, t)
    if ret is None:
        return ""
    return ret

class Geo(object):
    _map = {
        API.GeoVectorClass: Vector,
        API.GeoPointClass: Point,
        API.GeoNumericClass: Numeric,
        API.GeoBooleanClass: Boolean,
        API.GeoFunctionClass: Function,
        API.GeoTextClass: Text,
        API.GeoLineClass: Line,
        API.GeoSegmentClass: Segment,
        API.GeoRayClass: Ray,
        API.GeoConicClass: Conic,
        API.GeoLocusClass: Locus,
    }
    _revmap = dict((v, k) for k, v in _map.iteritems())
    _cache = {}
    @classmethod
    def _get_element(cls, geo):
        if geo is None:
            return None
        elif geo in cls._cache:
            return cls._cache[geo]
        else:
            eltype = cls._map.get(geo.getType())
            if eltype is None:
                return None
            element = eltype.fromgeo(geo)
            cls._cache[geo] = element
            return element
    def __getattr__(self, name):
        geo = api.lookupLabel(name)
        return self._get_element(geo)
    def __setattr__(self, name, value):
        if not isinstance(value, Element):
            node = expr(value).getnode()
            node.nodeLabel = name
            value = element(Expression(node))
        value.label = name
    @property
    def points(self):
        return map(Geo._get_element, api.getGeos(GeoClass.POINT))


class MyListCellRenderer(ListCellRenderer):
    colormap = {
        "input": awtColor.BLACK,
        "output": awtColor.BLUE,
        "error": awtColor.RED
    }
    font = Font("Monospaced", Font.PLAIN, 12)
    def getListCellRendererComponent(self, lst, value, index, isSelected, cellHasFocus):
        text = value["text"]
        renderer = JTextArea(text=text)
        renderer.foreground = self.colormap[value["type"]]
        renderer.font = self.font
        if isSelected:
            renderer.background = awtColor.YELLOW
        return renderer


class OutputPane(object):
    def __init__(self):
        self.textpane = JTextPane()
        self.doc = self.textpane.getStyledDocument()
        self.textpane.editable = False
        style_context = StyleContext.getDefaultStyleContext()
        default_style = style_context.getStyle(StyleContext.DEFAULT_STYLE)
        parent_style = self.doc.addStyle("parent", default_style)
        StyleConstants.setFontFamily(parent_style, "Monospaced")
        input_style = self.doc.addStyle("input", parent_style)
        output_style = self.doc.addStyle("output", parent_style)
        StyleConstants.setForeground(output_style, awtColor.BLUE)
        error_style = self.doc.addStyle("error", parent_style)
        StyleConstants.setForeground(error_style, awtColor.RED)
        
        # Do a dance to set tab size
        font = Font("Monospaced", Font.PLAIN, 12)
        self.textpane.setFont(font)
        fm = self.textpane.getFontMetrics(font)
        tabw = float(fm.stringWidth(" "*4))
        tabs = [
            TabStop(tabw*i, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE)
            for i in xrange(1, 51)
        ]
        attr_set = style_context.addAttribute(
            SimpleAttributeSet.EMPTY,
            StyleConstants.TabSet,
            TabSet(tabs)
        )
        self.textpane.setParagraphAttributes(attr_set, False)
        #Dance done!
        
    def addtext(self, text, style="input", ensure_newline=False):
        doclen = self.doc.length
        if ensure_newline and self.doc.getText(doclen - 1, 1) != '\n':
            text = '\n' + text
        self.doc.insertString(self.doc.length, text, self.doc.getStyle(style))
        # Scroll down
        self.textpane.setCaretPosition(self.doc.length)


class FileLoader(ActionListener):
    def __init__(self, pywin):
        self.fc = JFileChooser()
        self.fc.fileFilter = FileNameExtensionFilter("Python Files", ["py"])
        self.pywin = pywin
        self.filename = None
    def actionPerformed(self, evt):
        if evt.actionCommand == 'load':
            res = self.fc.showOpenDialog(self.pywin.frame)
            if res == JFileChooser.APPROVE_OPTION:
                self.filename = self.fc.selectedFile.absolutePath
                self.pywin.reload_menuitem.text = "Reload " + self.filename
                self.pywin.reload_menuitem.enabled = True
            else:
                self.filename = None
        if self.filename:
            print "*** Loading", self.filename, "***"
            try:
                execfile(self.filename, interface.namespace)
            except Exception, e:
                self.pywin.outputpane.addtext(str(e) + '\n', 'error')


class InputHistory(object):
    
    class OutOfBounds(Exception):
        pass
    
    def __init__(self):
        self.history = []
        self.history_pos = 0
    def append(self, input):
        self.history.append(input)
        self.reset_position()
    def reset_position(self):
        self.history_pos = 0
    def back(self):
        if self.history_pos < len(self.history):
            self.history_pos += 1
            return self.history[-self.history_pos]
        else:
            raise self.OutOfBounds
    def forward(self):
        if self.history_pos > 1:
            self.history_pos -= 1
            return self.history[-self.history_pos]
        else:
            raise self.OutOfBounds
        

class InputArea(KeyListener):
    def __init__(self):
        self.component = JTextArea(
            border=BorderFactory.createEmptyBorder(5, 5, 5, 5),
            tabSize=4,
            font=Font("Monospaced", Font.PLAIN, 12)
        )
        self.component.addKeyListener(self)
        self.nocheck = LockManager()

    def _getinput(self):
        return self.component.text
    def _setinput(self, input):
        self.component.text = input
    input = property(_getinput, _setinput)
    
    # Implementation of KeyListener
    def keyPressed(self, evt):
        pass
    def keyReleased(self, evt):
        pass
    def keyTyped(self, evt):
        if evt.keyChar == '\n':
            text = self.input
            offset = self.component.caretPosition - 1
            indent = None
            if offset:
                lines = text[:offset].rsplit('\n', 1)
                if len(lines) == 1:
                    line = text[:offset]
                else:
                    line = lines[1]
                indent = re.match('\\s*', line).group(0)
                if text[offset - 1] == ':':
                    indent += '\t'
            if indent:
                self.input = text[:offset + 1] + indent + text[offset + 1:]
                self.component.caretPosition = offset + len(indent) + 1


class InteractiveInput(InputArea):
    def __init__(self, checks_disabled, runcode):
        self.checks_disabled = checks_disabled
        self.runcode = runcode
        InputArea.__init__(self)
    def keyTyped(self, evt):
        with self.checks_disabled:
            InputArea.keyTyped(self, evt)
            if evt.keyChar != '\n':
                return
            text = self.input
            lines = self.input.split("\n")
            if (
                len(lines) == 2 and not lines[1] or
                len(lines) > 2 and not lines[-1].strip() and not lines[-2].strip()
            ):
                res = self.runcode(text)
                if res:
                    self.input = ""


class LockManager(object):
    def __init__(self):
        self.lock = False
    def __enter__(self):
        self.lock = True
        return self
    def __exit__(self, type, value, traceback):
        self.lock = False
    def __nonzero__(self):
        return self.lock


class PythonWindow(KeyListener, DocumentListener, ActionListener):
    def __init__(self):
        self.frame = JFrame("Python Window")

        tabs = JTabbedPane()

        interactive_pane = JPanel(BorderLayout())
        script_pane = JPanel(BorderLayout())
        
        scrollpane = JScrollPane()
        inputPanel = JPanel()
        inputPanel.layout = GridLayout(1, 1)
        self.check_disabled = LockManager()
        self.input = InteractiveInput(self.check_disabled, self.runcode)
        self.input.component.document.addDocumentListener(self)
        inputPanel.add(self.input.component)
        self.outputpane = OutputPane()
        scrollpane.viewport.view = self.outputpane.textpane
        interactive_pane.add(scrollpane, BorderLayout.CENTER)
        interactive_pane.add(inputPanel, BorderLayout.PAGE_END)

        self.script_area = script_area = InputArea()
        script_pane.add(script_area.component, BorderLayout.CENTER)
        
        tabs.addTab("Interactive", interactive_pane)
        tabs.addTab("Script", script_pane)
        
        self.frame.add(tabs)
        self.frame.size = 500, 600
        self.frame.visible = False
        self.component = None
        self.make_menubar()
        self.history = InputHistory()
    def make_menubar(self):
        shortcut = Toolkit.getDefaultToolkit().menuShortcutKeyMask
        menubar = JMenuBar()
        filemenu = JMenu("File")
        loader = FileLoader(self)
        menubar.add(filemenu)
        item = JMenuItem("Load Python File", actionCommand="load")
        item.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcut)
        item.addActionListener(loader)
        filemenu.add(item)
        item = JMenuItem("Reload Python File",
            enabled=False, actionCommand="reload"
        )
        item.addActionListener(loader)
        item.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, shortcut)
        self.reload_menuitem = item
        filemenu.add(item)
        
        navmenu = JMenu("Navigation")
        menubar.add(navmenu)
        
        item = JMenuItem("Previous Input", actionCommand="up")
        item.addActionListener(self)
        item.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_UP,
            ActionEvent.ALT_MASK
        )
        navmenu.add(item)
        
        item = JMenuItem("Next Input", actionCommand="down")
        item.addActionListener(self)
        item.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_DOWN,
            ActionEvent.ALT_MASK
        )
        navmenu.add(item)

        item = JMenuItem("Run Script", actionCommand="runscript")
        item.addActionListener(self)
        item.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_E,
            shortcut
        )
        navmenu.add(item)
        
        self.frame.setJMenuBar(menubar)
    def toggle_visibility(self):
        self.frame.visible = not self.frame.visible
    def add_component(self, c):
        self.remove_component()
        self.frame.add(c, BorderLayout.PAGE_START)
        self.component = c
    def remove_component(self):
        if self.component is not None:
            self.frame.remove(self.component)
            self.component = None
    def add(self, text, type="input"):
        self.outputpane.addtext(text, type)
        self.frame.validate()
    def error(self, text):
        self.outputpane.addtext(text, "error", ensure_newline=True)
    def write(self, text):
        self.add(text, "output")
    def runcode(self, source, interactive=True):
        if not source.strip():
            return True
        processed_source = source.replace("$", "geo.")
        code = interface.compileinteractive(processed_source)
        if code in ("continue", "error"):
            code = interface.compilemodule(processed_source)
            if code == "error":
                return False
        source = source.strip()
        if interactive:
            self.history.append(source)
            self.current_text = ""
            self.outputpane.addtext(source +'\n', "input", ensure_newline=True)
        interface.run(code)
        return True
    def run(self):
        source = self.input.text
        if not source.strip():
            self.input.text = ""
            return
        processed_source = source.replace("$", "geo.")
        code = interface.compileinteractive(processed_source)
        if code in ("continue", "error"):
            code = interface.compilemodule(processed_source)
            if code == "error":
                return
        source = source.strip()
        self.history.append(source)
        self.outputpane.addtext(source +'\n', "input", ensure_newline=True)
        result = interface.run(code)
        if result == "OK":
            self.input.text = ""
    
    # Implementation of KeyListener
    def keyPressed(self, evt):
        pass
    def keyReleased(self, evt):
        pass
    def keyTyped(self, evt):
        if evt.keyChar == '\n':
            text = self.input.text
            if text.endswith('\n\n'):
                self.run()
                return
            t = text.rstrip()
            if '\n' not in t and not t.endswith(':'):
                self.run()
                return
            offset = self.input.caretPosition - 1
            indent = None
            if offset:
                lines = text[:offset].rsplit('\n', 1)
                if len(lines) == 1:
                    line = text[:offset]
                else:
                    line = lines[1]
                indent = re.match('\\s*', line).group(0)
                if len(indent) == len(line):
                    # No non-whitespace on this line
                    if len(text) == offset + 1:
                        self.run()
                        return
                elif text[offset - 1] == ':':
                    indent += '\t'
            if indent:
                with self.check_disabled:
                    self.input.text = text[:offset + 1] + indent + text[offset + 1:]
                self.input.caretPosition = offset + len(indent) + 1

    # Implementation of DocumentListener
    def update_current_text(self):
        if not self.check_disabled:
            self.current_text = self.input.input
            self.history.reset_position()
    def changedUpdate(self, evt):
        self.update_current_text()
    def insertUpdate(self, evt):
        self.update_current_text()
    def removeUpdate(self, evt):
        self.update_current_text()

    # Implementation of ActionListener
    def actionPerformed(self, evt):
        getattr(self, "action_" + evt.actionCommand)(evt)
    def action_up(self, evt):
        """Move back in history"""
        try:
            with self.check_disabled:
                self.input.input = self.history.back()
        except InputHistory.OutOfBounds:
            pass
    def action_down(self, evt):
        """Move forward in history"""
        try:
            with self.check_disabled:
                self.input.input = self.history.forward()
        except InputHistory.OutOfBounds:
            self.input.input = self.current_text
            self.history.reset_position()
    def action_runscript(self, evt):
        """Run script"""
        self.runcode(self.script_area.input, interactive=False)
