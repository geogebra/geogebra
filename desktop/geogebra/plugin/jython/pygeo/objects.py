# from geogebra.awt import Color
from geogebra.common.plugin import GeoClass
from geogebra.common.plugin import Operation as OP
from geogebra.common.plugin import EuclidianStyleConstants as STYLE

from java.lang import IndexOutOfBoundsException

from functools import partial
import time

from apiproxy import API, in_new_thread, in_main_thread

from generic import generic, specmethod, GenericMethods, GenericError, sign

LABEL_MODES = ["name", "name+value", "value", "caption"]

Number = (int, long, float)


class ElementFactory(object):

    def __init__(self, api):
        self.api = api
        self._cache = {}

    def get_element(self, geo):
        if geo is None:
            return None
        elif geo in self._cache:
            return self._cache[geo]
        else:
            eltype = geo2element.get(API.Geo.getType(geo), Element)
            element = eltype.fromgeo(self, geo)
            self._cache[geo] = element
            return element

    def get_element_by_name(self, name):
        geo = self.api.lookupLabel(name)
        return self.get_element(geo)

    def set_element_by_name(self, name, value):
        if not isinstance(value, Element):
            node = self.api.getNode(self.expression(value).expr)
            API.Expression.setNodeLabel(node, name)
            value = self.element(Expression(self, node))
        value.label = name

    def element(self, obj):
        if isinstance(obj, Element):
            return obj
        return self.get_element(self.expression(obj).__geo__())

    def expression(self, obj):
        if isinstance(obj, Number):
            return NumberExpression(self, obj)
        elif isinstance(obj, tuple) and len(obj) == 2:
            x, y = obj
            return VectorExpression(self, x, y)
        #elif isinstance(obj, ExpressionNode):
        #    return Expression.fromnode(obj)
        elif isinstance(obj, Expression):
            return obj
        elif isinstance(obj, Element):
            return Expression(self, obj.geo)
        else:
            raise TypeError("Can't convert object to Expression: %r" % obj)


class GeoNamespace(object):
    
    def __init__(self, factory):
        # Need to bypass __setattr__
        self.__dict__['_factory'] = factory
        self.__dict__['_api'] = factory.api
    
    def __getattr__(self, name):
        return self._factory.get_element_by_name(name)
    
    def __setattr__(self, name, value):
        self._factory.set_element_by_name(name, value)

    @staticmethod
    def _get_item_name(key):
        if isinstance(key, tuple) and len(key) == 2:
            column, row = key
            if isinstance(column, (int, long)):
                if 1 <= column <= 26:
                    column = chr(64 + column)
                else:
                    raise ValueError("Column index must be in the range 1-26")
            elif isinstance(column, basestring):
                column = column.upper()
                if not(len(column) == 1 or 'A' <= column <= 'Z'):
                    raise ValueError("Column must be in the range A-Z")
            else:
                raise TypeError("Column must be integer or letter")
            if isinstance(row, (int, long)):
                if not(1 <= row <= 100):
                    raise ValueError("Row must be in the range 1-100")
            else:
                raise TypeError("Row must be an integer")
            return "%s%s" % (column, row)
        elif isinstance(key, basestring):
            return key
        else:
            raise TypeError("Key must be a string or pair 'column, row'")

    def __getitem__(self, key):
        return getattr(self, self._get_item_name(key))

    def __setitem__(self, key, value):
        return setattr(self, self._get_item_name(key), value)
    
    @property
    def __points__(self):
        return map(
            self._factory.get_element,
            self._api.getGeos(GeoClass.POINT)
        )
    @property
    def __all__(self):
        return map(
            self._factory.get_element,
            self._api.getAllGeos()
        )


class FactoryProductAttribute(object):
    def __init__(self, name):
        self.name = name
    def __get__(self, obj, objtype=None):
        return getattr(obj._product_type, self.name)
    def __set__(self, obj, val):
        setattr(obj._product_type, self.name, val)
    def __delete__(self, obj):
        delattr(obj._product_type, self.name)


class FactoryProduct(object):
    def __init__(self, product_type, factory):
        self._product_type = product_type
        self._factory = factory
    def __call__(self, *args, **kwargs):
        return self._product_type(self._factory, *args, **kwargs)
    @property
    def all(self):
        return map(
            self._factory.get_element,
            self._factory.api.getGeos(self._product_type.GEOCLASS)
        )
    def __repr__(self):
        return "<Bound %s type>" % self._product_type.__name__
    onclick = FactoryProductAttribute("onclick")
    onupdate = FactoryProductAttribute("onupdate")
    onadd = FactoryProductAttribute("onadd")
    onremove = FactoryProductAttribute("onremove")
    onrename = FactoryProductAttribute("onrename")


class MetaFactoryProduct(GenericMethods.__metaclass__):
    def __get__(self, obj, objtype=None):
        # return partial(self, obj)
        return FactoryProduct(self, obj)


class Element:
    __metaclass__ = MetaFactoryProduct
    @generic
    def __init__(self, factory, *args, **kwargs):
        self._factory = factory
        self._api = factory.api
        try:
            self.rawinit(*args)
        except TypeError:
            elements = [self._factory.element(arg) for arg in args]
            self.init(*elements)
        self._setattrs(kwargs)

    @generic
    def rawinit(self, *args):
        raise GenericError
    
    @generic
    def init(self, *args):
        raise GenericError
    
    #@specmethod.__init__
    #@sign(API.GeoElementClass)
    #def initfromgeo(self, geo):
    #    self.geo = geo
    
    @classmethod
    def fromgeo(cls, factory, geo):
        el = object.__new__(cls)
        el.geo = geo
        el._factory = factory
        el._api = factory.api
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
        API.Geo.remove(self.geo)

    def update(self):
        API.Geo.updateRepaint(self.geo)

    # property: typename
    def _gettypename(self):
        return API.Geo.getTypeString(self.geo)
    typename = property(_gettypename)
    
    # property: defined
    def _getdefined(self):
        return API.Geo.isDefined(self.geo)
    defined = property(_getdefined)

    # property: infinite
    def _getinfinite(self):
        return API.Geo.isInfinite(self.geo)
    infinite = property(_getinfinite)
    
    # property: free
    def _getfree(self):
        return API.Geo.isFree(self.geo)
    free = property(_getfree)
    
    # property: label 
    def _getlabel(self):
        return API.Geo.getLabel(self.geo)
    def _setlabel(self, label):
        API.Geo.setLabel(self.geo, label)
        API.Geo.updateRepaint(self.geo)
    label = property(_getlabel, _setlabel)

    # property: color
    def _getcolor(self):
        return API.Geo.getColor(self.geo)
    def _setcolor(self, col):
        API.Geo.setColor(self.geo, col)
        API.Geo.updateRepaint(self.geo)
    color = property(_getcolor, _setcolor)

    # property: opacity
    def _getopacity(self):
        return API.Geo.getAlpha(self.geo)
    def _setopacity(self, value):
        API.Geo.setAlpha(self.geo, float(value))
        self.update()
    opacity = property(_getopacity, _setopacity)
    
    # property: caption
    def _getcaption(self):
        return API.Geo.getCaption(self.geo)
    def _setcaption(self, value):
        API.Geo.setCaption(self.geo, value)
        API.Geo.updateRepaint(self.geo)
    caption = property(_getcaption, _setcaption)

    # property: label_mode
    def _getlabel_mode(self):
        return LABEL_MODES[API.Geo.getLabelMode(self.geo)]
    def _setlabel_mode(self, mode):
        try:
            mode = LABEL_MODES.index(mode)
            API.Geo.setLabelMode(self.geo, mode)
            self.update()
        except ValueError:
            raise ValueError("illegal label mode: %s", mode)
    label_mode = property(_getlabel_mode, _setlabel_mode)

    # property: label_color
    def _getlabel_color(self):
        return API.Geo.getLabelColor(self.geo)
    def _setlabel_color(self, color):
        API.Geo.setLabelColor(self.geo, color)
        API.Geo.updateRepaint(self.geo)
    label_color = property(_getlabel_color, _setlabel_color)
    
    # property: label_visible
    def _getlabel_visible(self):
        return API.Geo.isLabelVisible(self.geo)
    def _setlabel_visible(self, val):
        API.Geo.setLabelVisible(self.geo, bool(val))
        API.Geo.updateRepaint(self.geo)
    label_visible = property(_getlabel_visible, _setlabel_visible)
    
    # property: label_offset
    def _getlabel_offset(self):
        return tuple(API.Geo.getLabelOffset(self.geo))
    def _setlabel_offset(self, val):
        x, y = val
        API.Geo.setLabelOffset(self.geo, x, y)
        API.Geo.updateRepaint(self.geo)
    label_offset = property(_getlabel_offset, _setlabel_offset)
    
    # property: background_color
    def _getbgcolor(self):
        return API.Geo.getBackgroundColor(self.geo)
    def _setbgcolor(self, val):
        API.Geo.setBackgroundColor(self.geo, val)
    background_color = property(_getbgcolor, _setbgcolor)
    
    # property: visible
    def _getvisible(self):
        return API.Geo.isEuclidianVisible(self.geo)
    def _setvisible(self, value):
        API.Geo.setEuclidianVisible(self.geo, bool(value))
        API.Geo.updateRepaint(self.geo)
    visible = property(_getvisible, _setvisible)

    # property: algebra_visible
    def _getalgebra_visible(self):
        return API.Geo.isAlgebraVisible(self.geo)
    def _setalgebra_visible(self, value):
        API.Geo.setAlgebraVisible(self.geo, bool(value))
        API.Geo.updateRepaint(self.geo)
    algebra_visible = property(_getalgebra_visible, _setalgebra_visible)

    # property: auxiliary
    def _getauxiliary(self):
        return API.Geo.isAuxiliary(self.geo)
    def _setauxiliary(self, value):
        API.Geo.setAuxiliary(self.geo, bool(value))
    auxiliary = property(_getauxiliary, _setauxiliary)

    # property: trace
    def _gettrace(self):
        return API.Geo.getTrace(self.geo)
    def _settrace(self, value):
        API.Geo.setTrace(self.geo, bool(value))
        API.Geo.updateRepaint(self.geo)
    trace = property(_gettrace, _settrace)

    # property: layer
    def _getlayer(self):
        return API.Geo.getLayer(self.geo)
    def _setlayer(self, value):
        API.Geo.setLayer(self.geo, int(value))
        self.update()
    layer = property(_getlayer, _setlayer)
    
    # property: animating
    def _getanimating(self):
        return API.Geo.getAnimating(self.geo)
    def _setanimating(self, val):
        API.Geo.setAnimating(self.geo, bool(val))
    animating = property(_getanimating, _setanimating)
    
    def __geo__(self):
        return self.geo



class Expression:

    __metaclass__ = MetaFactoryProduct
    
    @generic
    def __init__(self, factory, expr):
        self._factory = factory
        self._api = factory.api
        self.expr = expr
    
    @classmethod
    def fromnode(cls, factory, node):
        val = API.Expression.evaluate(node)
        if API.Expression.isNumber(val):
            return NumberExpression(factory, node)
        elif API.Expression.isVector(val):
            return VectorExpression(factory, node)
        elif API.Expression.isBoolean(val):
            return BooleanExpression(factory, node)
        else:
            # TODO add other types of expressions
            # Fall back to generic expression type
            return Expression(factory, node)
    
    def __hash__(self):
        return hash(self.geo)

    def getnode(self):
        return self._api.nodeExpression(self.expr)
    
    def __repr__(self):
        return "<%s>" % self.expr
    
    def _binop(self, other, opcode, reverse=False):
        try:
            self = self._factory.expression(self)
            other = self._factory.expression(other)
        except TypeError:
            return NotImplemented
        if reverse:
            self, other = other, self
        x = self.expr
        y = other.expr
        node = self._api.nodeExpression(x, opcode, y)
        return self.fromnode(self._factory, node)
    
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
        m1 = self._factory.expression(-1)
        return self._binop(m1, OP.MULTIPLY, self)
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
        val = API.Expression.evaluate(self.expr)
        if API.Expression.isNumber(val):
            return API.Expression.getNumber(val)
        elif API.Expression.isBoolean(val):
            return API.Expression.getBoolean(val)
        elif API.Expression.isVector(val):
            return tuple(API.Expression.getCoords(val))
        else:
            # TODO cover more types
            raise TypeError(type(val))
    value = property(_getvalue)

    def __geo__(self):
        return self._api.getGeo(self.expr)


Expressionable = (Number, tuple, Expression)


class NumberExpression(Expression):
    @specmethod.__init__
    @sign(ElementFactory, Number)
    def initfromnumber(self, factory, x):
        expr = factory.api.numberExpression(float(x))
        Expression.__init__(self, factory, expr)
    
    def __float__(self):
        return self.value

NumberThing = (Number, NumberExpression)


class VectorExpression(Expression):
    @specmethod.__init__
    @sign(ElementFactory, NumberThing, NumberThing)
    def initfromcoords(self, factory, x, y):
        x, y = factory.expression(x), factory.expression(y)
        expr = factory.api.vectorExpression(x.expr, y.expr)
        Expression.__init__(self, factory, expr)


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

def unary_factory(factory, name, opcode):
    def f(e):
        return Expression(
            factory,
            factory.api.nodeExpression(factory.expression(e).expr, opcode))
    f.__name__ = name
    return f

def unary_functions(factory):
    unary_functions = {}
    for names, opcode in unary_functions_data:
        names = names.split()
        f = unary_factory(factory, names[0], opcode)
        for name in names:
            unary_functions[name] = f
    return unary_functions

binary_functions_data = [
    ('arctan2 atan2', OP.ARCTAN2),
]

# TODO create binary_functions

class ExpressionElement(Element):
    def _getexpr(self):
        return self.geo
    expr = property(_getexpr)
        

class Numeric(ExpressionElement, NumberExpression):

    GEOCLASS = GeoClass.NUMERIC
    
    # TODO Tidy this up
    @specmethod.__init__
    @sign(ElementFactory, Number)
    def initfromnumber(self, factory, val):
        self.geo = factory.api.geoNumber(float(val))
        self._factory = factory
        self._api = factory.api
    
    # TODO Tidy this up
    @specmethod.__init__
    @sign(ElementFactory, Expression)
    def initfromexpr(self, factory, e):
        self.geo = factory.api.geoNumber(e.expr)
        self._factory = factory
        self._api = factory.api
    
    def __float__(self):
        return self.geo.value

    def _setvalue(self, val):
        API.Geo.setNumericValue(self.geo, val)
        API.Geo.updateRepaint(self.geo)
    value = property(Expression._getvalue, _setvalue)


class Angle(Numeric):
    
    GEOCLASS = GeoClass.ANGLE


class Boolean(ExpressionElement, Expression):
    
    GEOCLASS = GeoClass.BOOLEAN


class VectorOrPoint(ExpressionElement, Expression):

    @specmethod.init
    @sign(NumberThing, NumberThing)
    def initfromexprcoords(self, xe, ye):
        ve = VectorExpression(self._factory, xe, ye)
        self.initfromexpr(ve)
        
    def setcoords(self, x, y):
        API.Geo.setCoords(self.geo, float(x), float(y), 1.0)
        API.Geo.updateRepaint(self.geo)
    def _getcoords(self):
        return (self.x, self.y)
    def _setcoords(self, coords):
        self.setcoords(*coords)
    coords = property(_getcoords, _setcoords)
    
    def _getx(self):
        try:
            return self._x
        except AttributeError:
            self._x = NumberExpression(
                self._factory,
                self._api.xCoordExpression(self.geo)
            )
            return self._x
    def _setx(self, x):
            self.setcoords(x, self.y.value)
    x = property(_getx, _setx)

    def _gety(self):
        try:
            return self._y
        except AttributeError:
            self._y = NumberExpression(
                self._factory,
                self._api.yCoordExpression(self.geo)
            )
            return self._y
    def _sety(self, y):
            self.setcoords(self.x.value, y)
    y = property(_gety, _sety)


class Vector(VectorOrPoint):

    GEOCLASS = GeoClass.VECTOR
    
    @specmethod.rawinit
    @sign(VectorThing)
    def initfromexpr(self, e):
        self.geo = self._api.geoVector(self._factory.expression(e).expr)

    @specmethod.rawinit
    @sign(Number, Number)
    def initfromnumbercoords(self, x, y):
        self.geo = self._api.geoVector(float(x), float(y))

        
class Point(VectorOrPoint):
    
    GEOCLASS = GeoClass.POINT
    
    @specmethod.rawinit
    @sign((VectorThing, Vector))
    def initfromexpr(self, e):
        e = self._factory.expression(e).expr
        self.geo = self._api.geoPoint(e)

    @specmethod.rawinit
    @sign(Number, Number)
    def initfromnumbercoords(self, x, y):
        self.geo = self._api.geoPoint(float(x), float(y))

    # property: point_size
    def _get_point_size(self):
        return API.Geo.getPointSize(self.geo)
    def _set_point_size(self, size):
        API.Geo.setPointSize(self.geo, int(size))
        self.update()
    point_size = property(_get_point_size, _set_point_size)


@Vector.init.spec
@sign(Vector, Point, Point)
def initfrompoints(self, p, q):
    self.geo = self._api.geoVector(p.geo, q.geo);

@Vector.init.spec
@sign(Vector, Point)
def initfrompoint(self, p):
    self.geo = self._api.geoVector(p.geo);

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
        return API.Geo.getLineThickness()
    def _set_thickness(self, val):
        API.Geo.setLineThickness(self.geo, val)
        API.Geo.updateRepaint(self.geo)
    thickness = property(_get_thickness, _set_thickness)

    def _get_linetype(self):
        return self._rev_line_types[self.geo.lineType]
    def _set_linetype(self, val):
        try:
            API.Geo.setLineType(self.geo, self._line_types[val])
        except KeyError:
            raise ValueError("Unknown line type: %r" % val)
        API.Geo.updateRepaint(self.geo)
    linetype = property(_get_linetype, _set_linetype)


class Line(Path):
    
    GEOCLASS = GeoClass.LINE
    
    @specmethod.init
    @sign(Point, Point)
    def initfrom2points(self, p, q):
        self.geo = self._api.geoLinePP(p.geo, q.geo)

    @specmethod.init
    @sign(Point, Vector)
    def initfrompointandvector(self, p, v):
        self.geo = self._api.geoLinePV(p.geo, v.geo)

    
    def __contains__(self, p):
        if isinstance(p, Point):
            return API.Geo.isOnPath(self.geo, p.geo, 1e-5)
        else:
            return False
    
    def _getdirection(self):
        return self._factory.get_element(self._api.geoLineDirection(self.geo))
    direction = property(_getdirection)

@Line.init.spec
@sign(Line, Point, Line)
def initfrompointandline(self, p, l):
    return self._api.geoLinePL(p.geo, l.geo)


class Segment(Line, ExpressionElement, NumberExpression):
    
    GEOCLASS = GeoClass.SEGMENT
    
    @specmethod.init
    @sign(Point, Point)
    def initfrompoints(self, p, q):
        self.geo = self._api.geoSegment(p.geo, q.geo)

    # property: startpoint    
    def _getstartpoint(self):
        return self._factory.get_element(API.Geo.getStartPoint(self.geo))
    def _setstartpoint(self, p):
        API.Geo.setStartPoint(self.geo, self._factory.element(p).geo)
    startpoint = property(_getstartpoint, _setstartpoint)
    
    # property: endpoint
    def _getendpoint(self):
        return self._factory.get_element(API.Geo.getEndPoint(self.geo))
    def _setendpoint(self, p):
        API.Geo.setEndPoint(self.geo, self._factory.element(p).geo)
    endpoint = property(_getendpoint, _setendpoint)


class Ray(Line):
    
    GEOCLASS = GeoClass.RAY
    
    @specmethod.init
    @sign(Point, Point)
    def initfrompoints(self, p, q):
        self.geo = self._api.geoRayPP(p.geo, q.geo)


class Axis(Element):
    
    GEOCLASS = GeoClass.AXIS
    
    def _getvisible(self):
        return self._api.isAxisVisible(self.geo)
    def _setvisible(self, visible):
        self._api.setAxisVisible(self.geo, bool(visible))
        self._api.refreshViews()
    visible = property(_getvisible, _setvisible)


class Poly(ExpressionElement, NumberExpression):

    @property
    def boundary(self):
        return self._factory.get_element(API.Geo.getPolygonBoundary(self.geo))

    @property
    def points(self):
        try:
            return self._points
        except AttributeError:
            points = map(self._factory.get_element,
                         API.Geo.getPolygonPoints(self.geo))
            self._points = points
            return points

    def __getitem__(self, index):
        return self.points[index]

    def __len__(self):
        return len(self.points)


class Polygon(Poly):

    GEOCLASS = GeoClass.POLYGON
    
    def rawinit(self, pointlist):
        el = self._factory.element
        self.geo = self._api.geoPolygon([el(pt).geo for pt in pointlist])
    
    def init(self, *points):
        self.rawinit(points)
    
    def __len__(self):
        return API.Geo.getPolygonSize(self.geo)

    @property
    def edges(self):
        return map(self._factory.get_element,
                   API.Geo.getPolygonEdges(self.geo))
    
    @property
    def area(self):
        return self.value

    @property
    def directed_area(self):
        return API.Geo.getPolygonDirectedArea(self.geo)
    

class PolyLine(Poly):
    
    GEOCLASS = GeoClass.POLYLINE
    
    def rawinit(self, pointlist):
        el = self._factory.element
        self.geo = self._api.geoPolyLine([el(pt).geo for pt in pointlist])

    def init(self, *points):
        self.rawinit(points)


class Text(Element):
    
    GEOCLASS = GeoClass.TEXT
    
    @specmethod.rawinit
    @sign(basestring)
    def initfromstring(self, value):
        self.geo = self._api.geoText(value)
        API.Geo.updateRepaint(self.geo)

    # property: origin
    def _setorigin(self, point):
        if not isinstance(point, Point):
            try:
                point = self._factory.Point(point)
            except Exception:
                raise TypeError("text.origin must be a point")
        API.Geo.setTextOrigin(self.geo, point.geo)
        API.Geo.updateRepaint(self.geo)
    def _getorigin(self):
        geo = API.Geo.getTextOrigin(self.geo)
        if geo is None:
            self._setorigin((0, 0))
            self.update()
            return self._getorigin()
        return self._factory.get_element(geo)
    def _delorigin(self):
        API.Geo.removeTextOrigin(self.geo)
        API.Geo.updateRepaint(self.geo)
    origin = property(_getorigin, _setorigin, _delorigin)

    # property: text
    def _gettext(self):
        return API.Geo.getTextString(self.geo)
    def _settext(self, text):
        API.Geo.setTextString(self.geo, text)
        self.update()
    text = property(_gettext, _settext)

    # property: latex
    def _getlatex(self):
        return API.Geo.isLatex(self.geo)
    def _setlatex(self, value):
        API.Geo.setLatex(self.geo, bool(value))
        self.update()
    latex = property(_getlatex, _setlatex)


class Button(Element):
    
    GEOCLASS = GeoClass.BUTTON


class TextField(Button):
    
    GEOCLASS = GeoClass.TEXTFIELD

    def _gettext(self):
        return API.Geo.getText(self.geo)
    def _settext(self, text):
        API.Geo.setText(self.geo, text)
        self.update()
    text = property(_gettext, _settext)


class Conic(Path):
    
    GEOCLASS = GeoClass.CONIC

    @classmethod
    def fromgeo(cls, factory, geo):
        if API.Geo.keepsType(geo):
            conictype = cls._conic_types.get(API.Geo.getTypeString(geo), cls)
        else:
            conictype = cls
        el = object.__new__(conictype)
        el.geo = geo
        el._factory = factory
        el._api = factory.api
        return el
    
    @specmethod.init
    @sign(Point, Point, Point, Point, Point)
    def initfrom5points(self, *points):
        geos = [p.geo for p in points]
        self.geo = self._api.geoConic(geos)

    #@specmethod.init
    #@sign(Numeric, Numeric, Numeric, Numeric, Numeric, Numeric)
    #def initfrom6coeffs(self, *coeffs):
    #    geos = [c.geo for c in coeffs]
    #    self.geo = _kernel.Conic(None, geos)


class Circle(Conic):
    
    @specmethod.init
    @sign(Point, Point)
    def initfromcentreandpoint(self, p, q):
        self.geo = self._api.geoCircleCP(p.geo, q.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfrom3points(self, p, q, r):
        self.geo = self._api.geoCirclePPP(p.geo, q.geo, r.geo)

    @specmethod.init
    @sign(Point, Segment)
    def initfromcentreandsegment(self, c, s):
        self.geo = self._api.geoCircleCS(c.geo, s.geo)

    @specmethod.init
    @sign(Point, Numeric)
    def initfromcentreandradius(self, c, r):
        self.geo = self._api.geoCircleCR(
            c.geo,
            self._factory.element(r).geo
        )


class Ellipse(Conic):
    
    @specmethod.init
    @sign(Point, Point, Numeric)
    def initfromfociandhalfmajoraxis(self, p, q, a):
        self.geo = self._api.geoEllipseFFA(p.geo, q.geo, a.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfromfociandpoint(self, p, q, r):
        self.geo = self._api.geoEllipseFFP(p.geo, q.geo, r.geo)


class Hyperbola(Conic):
    
    @specmethod.init
    @sign(Point, Point, Numeric)
    def initfromfociandhalfmajoraxis(self, p, q, a):
        self.geo = self._api.geoHyperbolaFFA(p.geo, q.geo, a.geo)

    @specmethod.init
    @sign(Point, Point, Point)
    def initfromfociandpoint(self, p, q, r):
        self.geo = self._api.geoHyperbolaFFP(p.geo, q.geo, r.geo)


class Parabola(Conic):
    
    @specmethod.init
    @sign(Point, Line)
    def initfromfocusanddirectrix(self, p, l):
        self.geo = self._api.geoParabola(p.geo, l.geo)


Conic._conic_types = {
    'Circle': Circle,
    'Parabola': Parabola,
    'Hyperbola': Hyperbola,
    'Ellipse': Ellipse,
}
    

class Locus(Path):
    
    GEOCLASS = GeoClass.LOCUS
    
    def get_point(self, param):
        if param < 0 or param > 1:
            raise ValueError("param must be between 0 and 1")
        return self._api.geoPointOnPath(self.geo, self._factory.element(param).geo)


class ImplicitPoly(Path):
    
    GEOCLASS = GeoClass.IMPLICIT_POLY


class Function(Element):
    
    # TODO Split Function into Function and FunctionNVar
    GEOCLASS = GeoClass.FUNCTION
    
    def __init__(self, factory, f):
        self._factory = factory
        self._api = factory.api
        nargs = f.func_code.co_argcount
        self.varnames = varnames = f.func_code.co_varnames
        if nargs == 0:
            raise ValueError("function must have at least one variable")
        if nargs == 1:
            x = self._api.variableExpression(varnames[0])
            f_expr = f(factory.Expression(x)).expr
            self.geo = self._api.geoFunction(f_expr, x)
        else:
            xs = [self._api.variableExpression(v) for v in varnames[:nargs]]
            self.geo = self._api.geoFunctionNVar(
                f(*map(factory.Expression, xs)).expr, xs)
    
    def __call__(self, *args):
        args = map(self._factory.expression, args)

    def _getarity(self):
        if isinstance(self.geo, API.GeoFunctionClass):
            return 1
        else:
            return API.Geo.getFunctionArity(self.geo)
    arity = property(_getarity)
    
    def _getimplicitcurve(self):
        if self.arity != 2:
            raise AttributeError
        else:
            geo = self._api.geoImplicitPoly(self.geo)
            return self._factory.get_element(geo)
    implicitcurve = property(_getimplicitcurve)


class Turtle(Element):

    GEOCLASS = GeoClass.TURTLE
    
    def __init__(self, factory):
        self._factory = factory
        self._api = factory.api
        self.geo = self._api.geoTurtle()
        self.running = True
        self.animating = True
        # turtle_driver.add_turtle(self)

    # Turtle driving commands

    # property: pen_color
    def _get_pen_color(self):
        return API.Geo.getTurtlePenColor(self.geo)
    def _set_pen_color(self, c):
        API.Geo.setTurtlePenColor(self.geo, c)
    pen_color = property(_get_pen_color, _set_pen_color)

    # property: pen_thickness
    def _get_pen_thickness(self):
        return API.Geo.getTurtlePenThickness(self.geo)
    def _set_pen_thickness(self, val):
        return API.Geo.setTurtlePenThickness(self.geo, int(val))
    pen_thickness = property(_get_pen_thickness, _set_pen_thickness)

    # property: angle
    def _get_angle(self):
        return API.Geo.getTurtleAngle(self.geo)
    def _set_angle(self, val):
        API.Geo.setTurtleAngle(self.geo, float(val))
    angle = property(_get_angle, _set_angle)

    # property: position
    def _get_position(self):
        return self._factory.get_element(API.Geo.getTurtlePosition(self.geo))
    def _set_position(self, point):
        if isinstance(point, tuple) and len(point) == 2:
            x, y = point
            API.Geo.setTurtlePosition(self.geo, float(x), float(y))
        if not isinstance(point, Point):
            try:
                point = self._factory.Point(point)
            except Exception:
                raise TypeError("turtle.position must be a point")
        API.Geo.setTurtlePosition(self.geo, point.geo)
    position = property(_get_position, _set_position)

    # property: pen_down
    def _get_pen_down(self):
        return API.Geo.isTurtlePenDown(self.geo)
    def _set_pen_down(self, val):
        API.Geo.setTurtlePenDown(self.geo, bool(val))
    pen_down = property(_get_pen_down, _set_pen_down)
    
    # property: speed
    def _get_speed(self):
        return API.Geo.getTurtleSpeed(self.geo)
    def _set_speed(self, value):
        API.Geo.setTurtleSpeed(self.geo, float(value))
    speed = property(_get_speed, _set_speed)
    
    # property: history
    def _get_history(self):
        return list(API.Geo.getTurtleHistory(self.geo))
    history = property(_get_history)
    
    def turn_left(self, angle):
        API.Geo.turtleTurn(self.geo, float(angle))
        return self

    def turn_right(self, angle):
        API.Geo.turtleTurn(self.geo, -float(angle))
        return self

    def forward(self, d):
        API.Geo.turtleForward(self.geo, float(d))
        return self

    def clear(self):
        API.Geo.clearTurtle(self.geo)

    def rewind(self):
        API.Geo.rewindTurtle(self.geo)

    def step(self, dt = 0.0):
        API.Geo.stepTurtle(self.geo, float(dt))

    # Short commands
    fd = forward
    tl = turn_left
    tr = turn_right
    

class TurtleDriver(object):
    def __init__(self, frequency=20):
        self.frequency = frequency
        self.sleep_time = 1.0/frequency
        self.turtles = []
        self.run()
    def add_turtle(self, turtle):
        if turtle not in self.turtles:
            self.turtles.append(turtle)
    @in_new_thread
    def run(self):
        t0 = time.time()
        while True:
            time.sleep(self.sleep_time)
            t1 = time.time()
            if self.turtles:
                self.step_turtles((t1 - t0)*self.frequency)
            t0 = t1
    @in_main_thread
    def step_turtles(self, dt):
        for turtle in self.turtles:
            if turtle.running:
                turtle.step(dt)
        
# turtle_driver = TurtleDriver()


class Intersect(object):

    __metaclass__ = MetaFactoryProduct
    
    def __init__(self, factory, *args):
        self._factory = factory
        self._api = factory.api
        elements = [factory.element(x) for x in args]
        geos = self.find(*elements)
        self.intersections = map(self._factory.get_element, geos)

    @generic
    def find(self, *args):
        raise GenericError

    @specmethod.find
    @sign(Line, Line)
    def twolines(self, l1, l2):
        return [self._api.intersectLines(l1.geo, l2.geo)]

    @specmethod.find
    @sign(Line, Conic)
    def lineconic(self, l, c):
        return self._api.intersectLineConic(l.geo, c.geo)

    @specmethod.find
    @sign(Conic, Line)
    def conicline(self, c, l):
        return self.lineconic(l, c)

    @specmethod.find
    @sign(Conic, Conic)
    def twoconics(self, c1, c2):
        return self._api.intersectConics(c1.geo, c2.geo)
    
    def __repr__(self):
        return "[%s]" % ", ".join(map(repr, self.intersections))

    def __getitem__(self, i):
        return self.intersections[i]

    def __iter__(self):
        for i in self.intersections:
            yield i


class List(Element):
    
    GEOCLASS = GeoClass.LIST
    
    def init(self, *args):
        el = self._factory.element
        self.geo = self._api.geoList([el(arg).geo for arg in args])

    def __getitem__(self, index):
        if index < 0:
            index = len(self) - index
        try:
            return API.Geo.getListItem(self.geo, index)
        except IndexOutOfBoundsException:
            raise IndexError

    def __delitem__(self, index):
        API.Geo.removeListItem(self.geo, index)
        self.update()

    def __len__(self):
        return API.Geo.getListLength(self.geo)

    def append(self, item):
        API.Geo.appendToList(self.geo, self._factory.element(item).geo)
        self.update()
        
    def clear(self):
        API.Geo.clearList(self.geo)
        self.update()


class Selection(object):

    def __init__(self, factory):
        self._factory = factory
        self._api = factory.api
    @property
    def all(self):
        return map(self._factory.get_element, self._api.getSelectedGeos())
    
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


__objects__ = [
    Vector, Point,
    Numeric, Angle, Boolean,
    Line, Segment, Ray, Polygon, PolyLine,
    Conic, Circle, Parabola, Ellipse, Hyperbola,
    Locus, Function, ImplicitPoly,
    Button, List, Text, TextField,
    Axis,
    Turtle,
]

__expressions__ = [
    Expression,
    NumberExpression, BooleanExpression, VectorExpression
]

geo2element = {}
for obj in __objects__:
    setattr(ElementFactory, obj.__name__, obj)
    try:
        geo_class = getattr(API, 'Geo%sClass' % obj.__name__)
        geo2element[geo_class] = obj
    except AttributeError:
        pass

# Special case of GeoFunctionNVar
geo2element[API.GeoFunctionNVarClass] = Function

for obj in __expressions__:
    setattr(ElementFactory, obj.__name__, obj)

ElementFactory.Intersect = Intersect
del obj
