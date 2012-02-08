from geogebra.plugin.jython import PythonFlatAPI as PythonAPI
import threading
from functools import partial

from java.lang import Runnable
from javax.swing import SwingUtilities

__all__ = [
    'API', 'APIProxy', 'asynchronous',
    'start_new_thread', 'run_in_main_thread',
    'in_new_thread', 'in_main_thread',
]

thread_locals = threading.local()

class Runner(Runnable):
    def __init__(self, f, args=(), kwargs={}):
        self.f = f
        self.args = args
        self.kwargs = kwargs
    def run(self):
        self.result = self.f(*self.args, **self.kwargs)

def invoke(f, *args, **kwargs):
    runner = Runner(f, args, kwargs)
    SwingUtilities.invokeAndWait(runner)
    return runner.result

class APIProxy(object):
    def __init__(self, api):
        self._api = api
    def __getattr__(self, attr):
        if asynchronous():
            return partial(invoke, getattr(self._api, attr))
        else:
            return getattr(self._api, attr)

def asynchronous(value=None):
    if value is None:
        try:
            return thread_locals.async
        except AttributeError:
            thread_locals.async = False
            return False
    else:
        thread_locals.async = value
        return value

def start_new_thread(f, *args, **kwargs):
    """
    Run f(*args, **kwargs) in a new thread, sychronizing API calls.
    """
    def run():
        asynchronous(True)
        f(*args, **kwargs)
    thread = threading.Thread(target=run)
    thread.start()
    return thread

def run_in_main_thread(f, *args, **kwargs):
    """
    Run f(*args) in the main thread and wait for it to return.

    This is useful when main API calls are needed as they will all be
    executed together, thus speeding things up.
    """
    if asynchronous():
        return f(*args, **kwargs)
    else:
        return invoke(f, *args, **kwargs)

def in_new_thread(f):
    """
    Return a function which will start a new thread to run f.
    """
    def runner(*args, **kwargs):
        return start_new_thread(f, *args, **kwargs)
    return runner

def in_main_thread(f):
    def runner(*args, **kwargs):
        return run_in_main_thread(f, *args, **kwargs)
    return runner

API = APIProxy(PythonAPI)
API.Geo = APIProxy(PythonAPI.Geo)
API.Expression = APIProxy(PythonAPI.Expression)
