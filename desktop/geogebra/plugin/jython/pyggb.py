# Make division novice-friendly :)
from __future__ import division, with_statement

# GeoGebra imports
from geogebra.plugin.jython import PythonScriptInterface

from geogebra.awt import Color
from geogebra.plugin.jython import PythonAPI as API

# Jython imports
import sys

# Python imports
from pygeo.gui import PythonWindow
from pygeo import objects


class Interface(PythonScriptInterface):
    
    def init(self, app):
        global api, ggbapi, pywindow, selection
        api = API(app)
        # Inject the api into objects.py
        # XXX There must be a better way!
        objects.api = api
        pywindow = self.pywin = PythonWindow(self)
        self.geo = objects.Geo()
        selection = self.selection = objects.Selection()
        sys.stdout = self.pywin
        ggbapi = api.getGgbApi()
        self.namespace = {
            'Color': Color,
            'Point': objects.Point,
            'Element': objects.Element,
            'Number': objects.Numeric,
            'Vector': objects.Vector,
            'Line': objects.Line,
            'Segment': objects.Segment,
            'Ray': objects.Ray,
            'Function': objects.Function,
            'Conic': objects.Conic,
            'Circle': objects.Circle,
            'Ellipse': objects.Ellipse,
            'Hyperbola': objects.Hyperbola,
            'Parabola': objects.Parabola,
            'Locus': objects.Locus,
            'Text': objects.Text,
            'Intersect': objects.Intersect,
            'ggbApplet': ggbapi,
            'geo': self.geo,
            'selection': self.selection,
            'pointlist': objects.pointlist,
            'interactive': interactive,
            'input': input,
            'debug': debug,
            'alert': alert,
        }
        self.namespace.update(objects.unary_functions)
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
        try:
            exec code in self.namespace
        except Exception, e:
            self.pywin.error("Runtime Error: " + str(e))
            raise
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


def debug(s):
    ggbapi.debug(s)
def alert(s):
    ggbapi.alert(s)
def input(s, t = ""):
    ret = ggbapi.prompt(s, t)
    if ret is None:
        return ""
    return ret

