__metaclass__ = type

__all__ = ['generic', 'sign', 'specmethod', 'GenericMethods']

def specialises(s, t):
    return s.specialises(t)

# avoid imports from types: we want to be self-contained
class Dummy:
    def f(self): pass

MethodType = type(Dummy().f)
FunctionType = type(lambda:0)
BuiltinFunctionType = type(isinstance)

del Dummy


class GenericError(TypeError):
    pass

class Generic:
    def __init__(self, default, defsig, callsig):
        self.default = default
        self.name = default.__name__
        self.defsig = defsig
        self.callsig = callsig
        self.specs = {}  # specialisations indexed by signature
        self._sro = []   # sro = Specialisation Resolution Order
        self._cache = {} # cache of specialisations by call signature
    def getsro(self, sig):
        try:
            return self._cache[sig]
        except KeyError:
            sro = [self.specs[u] for u in self._sro if specialises(sig, u)]
            self._cache[sig] = sro
            return sro
    def __call__(self, *args, **kwargs):
        # Implements function calls
        sig = self.callsig(args, kwargs)
        sro = self.getsro(sig)
        for spec in sro:
            try:
                return spec(*args, **kwargs)
            except GenericError:
                continue
        try:
            return self.default(*args, **kwargs)
        except GenericError:
            msg = "Unknown signature %s(%s)" % (self.name, sig)
            raise GenericError(msg)
    def __get__(self, obj, objtype=None):
        # Implements method calls 
        return MethodType(self, obj, objtype)
    def spec(self, f, defcls=None):
        sig = self.defsig(f, defcls)
        self._cache = {} # We can't trust the cache anymore
        sro = self._sro
        for i, u in enumerate(sro):
            if sig.specialises(u):
                if sig == u:
                    sro[i] = sig
                else:
                    sro.insert(i, sig)
                break
        else:
            sro.append(sig)
        self.specs[sig] = f
        return f

def sign(*sig, **kwsig):
    def deco(f):
        f.sig = sig
        f.kwsig = kwsig
        return f
    return deco


class ArgdictSignature(dict):
    def specialises(self, other):
        for x, t in other.items():
            u = self.get(x)
            if u is None or not issubclass(self[x], t):
                return False
        return True 
    @classmethod
    def fromdef(cls, f, defcls=None):
        sig = list(f.sig)
        if defcls is not None:
            sig.insert(0, defcls)
        s = cls(enumerate(sig))
        s.update(f.kwsig)
        return s
    @classmethod
    def fromcall(cls, args, kwargs):
        s = cls(enumerate(map(type, args)))
        s.update((x, type(v)) for (x, v) in kwargs.items())
        return s
    # This is needed so that objects can be used as dictionary keys
    def __hash__(self):
        try:
            return self._hashvalue
        except AttributeError:
            self._hashvalue = hash(frozenset(self.items()))
            return self._hashvalue
    def __str__(self):
        pos = []
        kw = []
        for key, val in self.iteritems():
            if isinstance(key, int):
                pos.append((key, val))
            else:
                kw.append((key, val))
        pos.sort()
        kw.sort()
        pos_strs = [v.__name__ for k,v in pos]
        kw_strs = ["%s=%s" % (k, v.__name__) for k,v in kw]
        return ", ".join(pos_strs + kw_strs)


def generic(*args, **kwargs):
    raise GenericError()

generic = Generic(generic, ArgdictSignature.fromdef, ArgdictSignature.fromcall)

@generic.spec
@sign(FunctionType)
def default_generic(f):
    return Generic(f, ArgdictSignature.fromdef, ArgdictSignature.fromcall)

@generic.spec
@sign(BuiltinFunctionType)
def default_generic(f):
    return Generic(f, ArgdictSignature.fromdef, ArgdictSignature.fromcall)

@generic.spec
@sign(sigtype=type)
def generic_withsigtype(sigtype):
    return lambda f: Generic(f, sigtype.fromdef, sigtype.fromcall)

specialises = generic(specialises)

class SubclassSignature(tuple):
    def specialises(self, other, issubclass=issubclass):
        return all(map(issubclass, self, other))
    @classmethod
    def fromdef(cls, f, defcls):
        return cls(f.sig)
    @classmethod
    def fromcall(cls, args, kwargs):
        return cls(map(type, args))

issubclass = generic(sigtype=SubclassSignature)(issubclass)

@issubclass.spec
@sign(tuple, object)
def tuple_issubclass(ts, u):
    return all(issubclass(t, u) for t in ts)


class SpecMethod:
    def __init__(self, method_name, f):
        self.method_name = method_name
        self.f = f
    def register(self, defcls):
        getattr(defcls, self.method_name).spec(self.f, defcls)

class SpecMethodDecoratorMaker:
    def __getattribute__(self, attr):
        def decorator(f):
            return SpecMethod(attr, f)
        return decorator

specmethod = SpecMethodDecoratorMaker()

class GenericMethods:
    class __metaclass__(type):
        def __init__(self, name, bases, attrs):
            for name, attr in attrs.iteritems():
                if isinstance(attr, SpecMethod):
                    attr.register(self)
                    setattr(self, name, attr.f)

## def Continued(cls):
##     class Continued:
##         class __metaclass__(type):
##             def __new__(meta, name, bases, attrs):
##                 for name, attr in attrs.iteritems():
##                     if name not in ('__module__', '__metaclass__'):
##                         setattr(cls, name, attr)
##                 type(cls).__init__(cls, name, bases, attrs)
##                 return cls
##     return Continued
