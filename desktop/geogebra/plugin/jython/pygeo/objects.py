from geogebra.awt import Color
from geogebra.common.plugin import GeoClass
from geogebra.common.plugin import Operation as OP
from geogebra.common.plugin import EuclidianStyleConstants as STYLE
from geogebra.plugin.jython import PythonAPI as API
api = API.getInstance()

from generic import generic, specmethod, GenericMethods, GenericError, sign

LABEL_MODES = ["name", "name+value", "value", "caption"]

Number = (int, long, float)

class Element(GenericMethods):

    @generic
    def __init__(self, *args, **kwargs):
        try:
            self.rawinit(*args)
        except TypeError:
            elements = [element(arg) for arg in args]
            self.init(*elements)
        self._setattrs(kwargs)

    @generic
    def rawinit(self, *args):
        raise GenericError
    
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

    def _setattrs(self, kwargs):
        for key, value in kwargs.iteritems():
            setattr(self, key, value)
        
    def __repr__(self):
        return "<%s>" % self.geo
    def __str__(self):
        if self.label is not None:
            return self.label
        else:
            return "(unnamed)"

    def remove(self):
        self.geo.remove()
    
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

    # property: algebra_visible
    def _getalgebra_visible(self):
        return self.geo.isAlgebraVisible()
    def _setalgebra_visible(self, value):
        self.geo.setAlgebraVisible(bool(value))
        self.geo.updateRepaint()
    algebra_visible = property(_getalgebra_visible, _setalgebra_visible)

    # property: auxiliary
    def _getauxiliary(self):
        return self.geo.isAuxiliary()
    def _setauxiliary(self, value):
        self.geo.setAuxiliary(bool(value))
    auxiliary = property(_getauxiliary, _setauxiliary)

    # property: trace
    def _gettrace(self):
        return self.geo.getTrace()
    def _settrace(self, value):
        self.geo.setTrace(bool(value))
        self.geo.updateRepaint()
    trace = property(_gettrace, _settrace)
    
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

    def _setvalue(self, val):
        self.geo.numericValue = val
        self.geo.updateRepaint()
    value = property(Expression._getvalue, _setvalue)


class Angle(Numeric):
    pass


class Boolean(ExpressionElement, Expression):
    pass


class VectorOrPoint(ExpressionElement, Expression):

    @specmethod.init
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
    @specmethod.rawinit
    @sign(VectorThing)
    def initfromexpr(self, e):
        self.geo = api.geoVector(expr(e).expr)
        self._setattrs(kwargs)
    @specmethod.rawinit
    @sign(Number, Number)
    def initfromnumbercoords(self, x, y):
        self.geo = api.geoVector(float(x), float(y))

        
class Point(VectorOrPoint):
    @specmethod.rawinit
    @sign(VectorThing)
    def initfromexpr(self, e):
        self.geo = api.geoPoint(expr(e).expr)


    @specmethod.rawinit
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
    def __init__(self, value, **kwargs):
        self.geo = api.geoText(value)
        self.geo.updateRepaint()
        for k, v in kwargs.iteritems():
            setattr(self, k, v)

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


class Button(Element):
    pass


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
            self.geo = api.geoFunction(f(Expression(x)).expr, x)
        else:
            xs = [api.variableExpression(v) for v in varnames[:nargs]]
            self.geo = api.geoFunctionNVar(f(*map(Expression, xs)).expr, xs)
    
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


class List(Element):
    def init(self, *args):
        self.geo = api.geoList([arg.geo for arg in args])


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

class Geo(object):
    _map = {
        API.GeoVectorClass: Vector,
        API.GeoPointClass: Point,
        API.GeoNumericClass: Numeric,
        API.GeoAngleClass: Angle,
        API.GeoBooleanClass: Boolean,
        API.GeoFunctionClass: Function,
        API.GeoTextClass: Text,
        API.GeoLineClass: Line,
        API.GeoSegmentClass: Segment,
        API.GeoRayClass: Ray,
        API.GeoConicClass: Conic,
        API.GeoLocusClass: Locus,
        API.GeoButtonClass: Button,
        API.GeoListClass: List,
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
            eltype = cls._map.get(geo.getType(), Element)
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


