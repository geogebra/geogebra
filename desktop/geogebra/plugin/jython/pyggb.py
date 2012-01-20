# Make division novice-friendly :)
from __future__ import division, with_statement

# Jython imports
import sys, traceback

from geogebra.plugin.jython import PythonAPI as API
from geogebra.plugin.jython import PythonScriptInterface

class Interface(PythonScriptInterface):
    
    def init(self, app):
        global api, ggbapi, pywindow, selection

        from pygeo.gui import PythonWindow
        pywindow = self.pywin = PythonWindow(self)
        
        # GeoGebra imports

        from geogebra.awt import Color

        # Python imports
        from pygeo import objects

        api = API(app)
        # Inject the api into objects.py
        # XXX There must be a better way!
        objects.api = api
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
            self.pywin.error(traceback.format_exc())
            raise
        
    def handleEvent(self, evt_type, target):
        if evt_type in ("add", "remove", "rename"):
            self.pywin.update_geos(api.allGeos)
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

    def setEventListener(self, geo, evt, code):
        geo = API.Geo(geo)
        el = self.geo._get_element(geo)
        if not code.strip():
            # print "deleting %s listener for %s" % (evt, el)
            try:
                delattr(el, "on" + evt)
            except AttributeError:
                pass
            return
        code = "\n".join("\t" + line for line in code.split("\n"))
        # print "setting %s listener for %s" % (evt, el)
        try:
            exec "def __f__(self):\n%s" % code in self.namespace 
            setattr(el, "on" + evt, self.namespace['__f__'])
            del self.namespace['__f__']
        except SyntaxError:
            pass
    
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
            self.pywin.error(traceback.format_exc())
            return "error"
    
    def compilemodule(self, source):
        try:
            return compile(source, "<pyggb>", "exec")
        except SyntaxError, e:
            self.pywin.error(traceback.format_exc())
            return "error"
    
    def run(self, code):
        try:
            exec code in self.namespace
        except Exception, e:
            self.pywin.error(traceback.format_exc())
            raise
        return "OK"

    def format_tb(self):
        return 

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


# This is to update the atime of the modules I want to keep in the jython jar
def do_protected_imports():
    print "protected imports"
    import random, collections, bisect, functools, pprint, itertools

# do_protected_imports()
