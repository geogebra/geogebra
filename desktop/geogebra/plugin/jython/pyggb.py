# Make division novice-friendly :)
from __future__ import division, with_statement

# FLAT
# from geogebra.plugin.jython import PythonFlatAPI as API
from geogebra.plugin.jython import PythonScriptInterface
from java.lang import Exception as JavaException

from collections import defaultdict
import sys, time
# FLAT
from pygeo import objects_flat as objects
from pygeo.apiproxy import (
    API, APIProxy, start_new_thread, run_in_main_thread,
    in_new_thread, in_main_thread,
)

from geogebra.awt import Color

class Interface(PythonScriptInterface):
    
    def init(self, raw_api):
        global selection
        self.api = api = APIProxy(raw_api)
        self.pywin = None
        factory = self.factory = objects.ElementFactory(api)
        functions = Functions(api, factory)
        self.geo = objects.GeoNamespace(self.factory)
        #selection = self.selection = objects.Selection()
        self.namespace = {
            'Color': Color,
            # Below we must assume that the GgbApi is already created.
            'ggbApplet': APIProxy(raw_api.getGgbApi()),
            'geo': self.geo,
            #'selection': self.selection,
            #'interactive': interactive,
            'input': functions.input,
            'debug': functions.debug,
            'alert': functions.alert,
            'command': functions.command,
            'sleep': time.sleep,
            'in_new_thread': in_new_thread,
            'in_main_thread': in_main_thread,
            'start_new_thread': start_new_thread,
            'run_in_main_thread': run_in_main_thread,
            '__factory__': factory,
            '__api__': api,
        }
        for obj in objects.__objects__:
            self.namespace[obj.__name__] = getattr(factory, obj.__name__)
        self.namespace['Intersect'] = factory.Intersect
        self.namespace.update(objects.unary_functions(self.factory))
        self.handling_event = False
        self.event_listeners = defaultdict(list)

    def execute(self, script):
        self.run(script)
        
    def handleEvent(self, evt_type, target):
        for listener in self.event_listeners[evt_type]:
            listener(evt_type, target)
        # target = API.Geo(target)
        if self.handling_event:
            return
        element = self.factory.get_element(target)
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
        # geo = API.Geo(geo)
        if self.selection_listener:
            el = self.factory.get_element(geo)
            self.selection_listener.send(el, add)
            if self.selection_listener.done:
                self.remove_selection_listener()
        else:
            # We shouldn't get here :)
            self.remove_selection_listener()
    
    def toggleWindow(self):
        if self.pywin is None:
            from pygeo import gui
            self.pywin = gui.PythonWindow(self.api)
        self.pywin.toggle_visibility()
    
    def isWindowVisible(self):
        return self.pywin is not None and self.pywin.frame.visible

    def setEventListener(self, geo, evt, code):
        # geo = API.Geo(geo)
        el = self.factory.get_element(geo)
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
        self.run(self.api.getInitScript())

    def getCurrentInitScript(self):
        if self.pywin is None:
            return None
        return self.pywin.get_current_script()
    
    def addEventListener(self, evt, listener):
        self.event_listeners[evt].append(listener)
    
    def removeEventListener(self, evt, listener):
        self.event_listeners[evt].remove(listener)
        
    def set_selection_listener(self, sl):
        self.api.startSelectionListener()
        self.selection_listener = sl
    
    def remove_selection_listener(self):
        self.api.stopSelectionListener();
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
        except SyntaxError:
            try:
                code = self.compilemodule(source)
            except SyntaxError, e:
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


class Functions(object):
    def __init__(self, api, factory):
        self.api = api
        self.factory = factory
    @property
    def ggbapi(self):
        return self.api.getGgbApi()
    def input(self, s, t=""):
        return self.ggbapi.prompt(s, t) or ""
    def alert(self, s):
        self.ggbapi.alert(s)
    def debug(self, s):
        self.ggbapi.debug(s)
    def command(self, cmd, *args):
        try:
            if not args:
                geos = self.api.evalCommand(cmd)
            else:
                args = [self.factory.expression(arg).expr for arg in args]
                geos = self.api.evalCommand(cmd, args)
        except JavaException:
            raise ValueError
        if geos is None:
            return None
        return map(self.factory.get_element, geos)


# This is to update the atime of the modules I want to keep in the jython jar
def do_protected_imports():
    print "protected imports"
    import random, collections, bisect, functools, pprint, itertools

# do_protected_imports()
