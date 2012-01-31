# Make division novice-friendly :)
from __future__ import division, with_statement

from geogebra.plugin.jython import PythonAPI as API, PythonScriptInterface

from collections import defaultdict
import sys

api = API.getInstance()
ggbapi = api.ggbApi

class Interface(PythonScriptInterface):
    
    def init(self):
        global selection

        # GeoGebra imports

        from geogebra.awt import Color

        # Python imports
        from pygeo import objects

        self.pywin = None
        self.geo = objects.Geo()
        selection = self.selection = objects.Selection()
        self.namespace = {
            'Color': Color,
            'Point': objects.Point,
            'Element': objects.Element,
            'Number': objects.Numeric,
            'Angle': objects.Angle,
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
            'Button': objects.Button,
            'List': objects.List,
            'Intersect': objects.Intersect,
            'ggbApplet': ggbapi,
            'geo': self.geo,
            'selection': self.selection,
            'pointlist': objects.pointlist,
            'interactive': interactive,
            'input': input,
            'debug': debug,
            'alert': alert,
            '__api__': api,
        }
        self.namespace.update(objects.unary_functions)
        self.handling_event = False
        self.event_listeners = defaultdict(list)

    def execute(self, script):
        self.run(script)
        
    def handleEvent(self, evt_type, target):
        for listener in self.event_listeners[evt_type]:
            listener(evt_type, target)
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
            sys.stderr.write("Error while handling event '%s' on '%r'\n"
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
        if self.pywin is None:
            from pygeo import gui
            self.pywin = gui.PythonWindow()
        self.pywin.toggle_visibility()
    
    def isWindowVisible(self):
        return self.pywin is not None and self.pywin.frame.visible

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

    def reset(self):
        if self.pywin is not None:
            self.pywin.reset()
        self.run(api.initScript)
    
    def addEventListener(self, evt, listener):
        self.event_listeners[evt].append(listener)
    
    def removeEventListener(self, evt, listener):
        self.event_listeners[evt].remove(listener)
        
    def set_selection_listener(self, sl):
        api.startSelectionListener()
        self.selection_listener = sl
    
    def remove_selection_listener(self):
        api.stopSelectionListener();
        self.selection_listener = None

    def format_source(self, source):
        if isinstance(source, basestring):
            return source.replace("$", "geo.")
        else:
            return source
    def compileinteractive(self, source):
        source = self.format_source(source)
        return compile(source, "<pyggb>", "single")
    
    def compilemodule(self, source):
        source = self.format_source(source)
        return compile(source, "<pyggb>", "exec")

    def compile_im(self, source):
        source = self.format_source(source)
        try:
            code = self.compileinteractive(source)
        except SyntaxError, e:
            try:
                code = self.compilemodule(source)
            except SyntaxError:
                raise e
        return code
    
    def run(self, code):
        code = self.format_source(code)
        exec code in self.namespace


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
