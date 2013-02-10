# Make division novice-friendly :)
from __future__ import division, with_statement

# FLAT
# from geogebra.plugin.jython import PythonFlatAPI as API
from geogebra.plugin.jython import PythonScriptInterface
from java.lang import Exception as JavaException

from org.rosuda.REngine.Rserve import RConnection;

from collections import defaultdict
import sys, time, traceback

# FLAT
from pygeo import objects
from pygeo.apiproxy import (
    API, APIProxy, start_new_thread, run_in_main_thread,
    in_new_thread, in_main_thread,
)
from pygeo.pylexing import pythonify

from geogebra.awt import GColorD as Color

class Interface(PythonScriptInterface):
    
    def init(self, raw_api):
        self.api = APIProxy(raw_api)
        self.ggbApi = APIProxy(raw_api.getGgbApi())
        self.pywin = None
        self.reinit()
    
    def reinit(self):
        global selection
        api = self.api
        factory = self.factory = objects.ElementFactory(api)
        functions = Functions(api, factory)
        self.geo = objects.GeoNamespace(self.factory)
        selection = self.selection = objects.Selection(self.factory)
        self.init_namespace = {
            'RConnection': RConnection,
            'Color': Color,
            # Below we must assume that the GgbApi is already created.
            'ggbApplet': self.ggbApi,
            'geo': self.geo,
            'selection': self.selection,
            #'interactive': interactive,
            'input': functions.input,
            'debug': functions.debug,
            'alert': functions.alert,
            'dialog': functions.dialog,
            'command': functions.command,
            'pyfunc': functions.pyfunc,
            'sleep': time.sleep,
            'in_new_thread': in_new_thread,
            'in_main_thread': in_main_thread,
            'start_new_thread': start_new_thread,
            'run_in_main_thread': run_in_main_thread,
            'Element': objects.Element,
            '__factory__': factory,
            '__api__': api,
        }
        for obj in objects.__objects__:
            self.init_namespace[obj.__name__] = getattr(factory, obj.__name__)
        self.init_namespace['Intersect'] = factory.Intersect
        self.init_namespace.update(objects.unary_functions(self.factory))
        self.reset_namespace()
        self.handling_event = False
        self.event_listeners = defaultdict(list)

    def reset_namespace(self):
        self.namespace = {}
        self.namespace.update(self.init_namespace)
    
    def execute(self, script):
        self.run(script)
    
    def show_traceback(self, header, skip_lines=None):
        sys.stderr.write(header)
        if self.pywin is not None:
            self.pywin.error(header)
            tb_lines = traceback.format_exception(*sys.exc_info())
            if skip_lines is not None:
                tb_lines = tb_lines[skip_lines:]
            self.pywin.error("".join(tb_lines))
    
    def handleEvent(self, evt_type, target):
        # if ... return and try ... finally are hacks to try to fix #1520
        # I can't run ATM so...
        label = API.Geo.getLabel(target)
        # print "1.", evt_type, label
        if label is not None:
            for listener in self.event_listeners[evt_type]:
                try:
                    listener(evt_type, target)
                except Exception:
                    self.show_traceback("Error while running listener for %s" % evt_type)
        if self.handling_event:
            return
        cached = target in self.factory._cache
        # print "2.", evt_type, label, cached
        if cached or (label and evt_type == 'add'):
            element = self.factory.get_element(target)
            try:
                action = getattr(element, "on" + evt_type)
            except AttributeError:
                if evt_type == 'add' and not cached:
                    del self.factory._cache[target]
                return
        else:
            return
        if hasattr(action, 'im_func'):
            action = action.im_func
        # print "3.", evt_type, element.label
        try:
            self.handling_event = True
            action(element)
        except Exception:
            header = "Error while handling event '%s' on '%r'\n" % (evt_type, target)
            self.show_traceback(header, 2)
            raise
        finally:
            if evt_type == 'remove':
                del self.factory._cache[target]
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
    
    def get_pywin(self):
        if self.pywin is None:
            from pygeo import gui
            self.pywin = gui.PythonWindow(self.api)
        return self.pywin
    
    def toggleWindow(self):
        self.get_pywin().toggle_visibility()
    
    def isWindowVisible(self):
        return False
        return self.pywin is not None and self.pywin.frame.visible

    def setEventHandler(self, geo, evt, code):
        el = self.factory.get_element(geo)
        print "Setting event listener for", el.label
        if not code.strip():
            try:
                delattr(el, "on" + evt)
            except AttributeError:
                pass
            return
        code = pythonify(code)
        code = "\n".join("\t" + line for line in code.split("\n"))
        code = "def __handle_event__(self=__el__):\n%s" % code
        try:
            self.namespace['__el__'] = el
            handler_def = compile(code, "<%s %s>" % (evt, el.label), 'exec')
            exec handler_def in self.namespace
            setattr(el, "on" + evt, self.namespace['__handle_event__'])
            del self.namespace['__handle_event__']
        except SyntaxError:
            header = "Error while compiling '%s' action for '%s'" % (evt, geo)
            self.show_traceback(header)
            raise
        finally:
            del self.namespace['__el__']

    def removeEventHandler(self, geo, evt):
        if geo not in self.factory._cache:
            return
        el = self.factory.get_element(geo)
        print "Removing event listener for", el.label
        try:
            delattr(el, "on" + evt)
        except AttributeError:
            pass
    
    def reset(self):
        # Make sure all objects are fresh
        reload(objects)
        # Refresh the namespace and stuff as well
        self.reinit()
        # Reset the Python Window if it's up
        if self.pywin is not None:
            self.pywin.reset()
    
    def runInitScript(self):
        # Run the init script and run ggbOnInit() if it exists
        self.run(self.api.getInitScript() +
                 "if 'ggbOnInit' in globals():"
                 "    ggbOnInit()"
        )
    
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
            return pythonify(source)
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

    def getComponent(self):
        return self.get_pywin().getComponent()
    def getMenuBar(self):
        return self.get_pywin().getMenuBar  ()


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


from javax.swing import JOptionPane

message_types = {
    'error': JOptionPane.ERROR_MESSAGE,
    'info': JOptionPane.INFORMATION_MESSAGE,
    'warning': JOptionPane.WARNING_MESSAGE,
    'question': JOptionPane.QUESTION_MESSAGE,
    'plain': JOptionPane.PLAIN_MESSAGE
}

option_types = {
    'yes/no': JOptionPane.YES_NO_OPTION,
    'yes/no/cancel': JOptionPane.YES_NO_CANCEL_OPTION,
    'ok/cancel': JOptionPane.OK_CANCEL_OPTION,
    'default': JOptionPane.DEFAULT_OPTION
}

class Functions(object):
    def __init__(self, api, factory):
        self.api = api
        self.factory = factory
    @property
    def ggbapi(self):
        return self.api.getGgbApi()
    def dialog(self, message, options=None,
                      default=0, title="GeoGebra", message_type='question'):
        message_type = message_types[message_type]
        if options is None:
            return JOptionPane.showMessageDialog(
                self.api.appFrame,
                message, title, message_type
            )
        if isinstance(options, basestring):
            if options in option_types:
                option_type = option_types[options]
                return JOptionPane.showConfirmDialog(
                    self.api.appFrame,
                    message, title, option_type, message_type
                )
            options = options.split("/")
        default = options[default]
        return JOptionPane.showOptionDialog(
            self.api.appFrame,
            message, title, 0, message_type, None, options, default
        )
    def input(self, message, options=None,
                     default=0, title="GeoGebra", message_type='question'):
        message_type = message_types[message_type]
        if options is not None:
            if isinstance(options, basestring):
                options = options.split("/")
            default = options[default]
        return JOptionPane.showInputDialog(
           self.api.appFrame,
            message, title, message_type, None, options, default or ""
        )
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
    def pyfunc(self, f):
        geo = self.api.newPythonFunction(f)
        return self.factory.get_element(geo)


# This is to update the atime of the modules I want to keep in the jython jar
def do_protected_imports():
    print "protected imports"
    import random, collections, bisect, functools, pprint, itertools

# do_protected_imports()
