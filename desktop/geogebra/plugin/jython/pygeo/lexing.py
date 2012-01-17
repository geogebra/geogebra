"""
This module aims to reproduce the functionality of C flex in python.
"""

from itertools import *
from copy import copy

__all__ = ['Error', 'TokenType', 'Token', 'SimpleToken', 'Lexer', 'nowrap',
           'rule', 'silent', 'istart', 'xstart']


class Error(Exception): pass

# --------------------------------------------------
# Implementation details
# --------------------------------------------------

class State:
    def __init__(self, **kwargs):
        for key, val in kwargs.iteritems():
            setattr(self, key, val)


class MultiScanner:
    "Similar to re.Scanner but with start conditions"
    def __init__(self, lexicons, init_state=None, flags=0):
        # All the regexp magic below is copied from re.Scanner from
        # the standard library.
        import sre_compile
        import sre_parse
        from sre_constants import BRANCH, SUBPATTERN
        if init_state is None:
            init_state = State()
        if not hasattr(init_state, 'start'):
            init_state.start = None
        self.init_state = init_state
        self.lexicons = lexicons
        self.scanners = {}
        for start, lexicon in lexicons.iteritems():
            # combine phrases into a compound pattern
            p, a = [], []
            s = sre_parse.Pattern()
            s.flags = flags
            for phrase, action in lexicon:
                p.append(sre_parse.SubPattern(s, [
                            (SUBPATTERN, (len(p)+1,
                                          sre_parse.parse(phrase, flags))),
                            ]))
                a.append(action)
            p = sre_parse.SubPattern(s, [(BRANCH, (None, p))])
            s.groups = len(p)
            self.scanners[start] = sre_compile.compile(p).match, a
    def scan(self, string, pos=0):
        state = copy(self.init_state)
        scanners = self.scanners
        while True:
            start = state.start
            match, actions = scanners[start]
            m = match(string, pos)
            if m is None:
                if pos == len(string): return
                raise Error('Scanning error at %s' % pos)
            action = actions[m.lastindex-1]
            if action is None:
                pos = m.end()
                continue
            state.match = m
            state.start = start
            result = action(state, m.group())
            pos = m.end()
            if result is None:
                continue
            yield result


class ScannerMaker(object):
    "Helper class for Lexer"
    def __init__(self, istart, xstart, init_state=None):
        self.init_state = init_state
        istart.append(None)
        self.istart = tuple(istart)
        self.start = self.istart + tuple(xstart)
        self.scanners = dict((s, []) for s in self.start)
    def add_rule(self, regexp, action, start=None):
        # print "+rule %s -- start %s" %(regexp, start)
        if not start:
            start = self.istart
        for i in start:
            self.scanners[i].append((regexp, action))
    def get_scanner(self):
        # print self.scanners
        return MultiScanner(self.scanners, self.init_state)
    def change_start(self, process, start):
        if start in self.start:
            def wrapped(state, val):
                res = process(state, val)
                state.start = start
                return res
            return wrapped
        raise Error("Unknown start condition '%s'" % start)


class StartCondition(object):
    def __init__(self, inclusive, name=None):
        self.inclusive = inclusive
        self.name = name
    def __rshift__(self, other):
        return StartList((self,)) >> other
    def __rrshift__(self, other):
        return rule(other) >> self
    def __repr__(self):
        return "<start condition %s>" % self.name


class StartList(object):
    def __init__(self, start_seq):
        self.start_seq = start_seq
    def __rshift__(self, other):
        if isinstance(other, StartList):
            return StartList(self.start_seq + other.start_seq)
        elif other is None or isinstance(other, StartCondition):
            return StartList(self.start_seq + (other,))
        else:
            r = rule(other)
            r.start_seq += self.start_seq
            return r


class Rule(object):
    def __init__(self, regexp, silent=False):
        self.regexp = regexp
        self.silent = silent
        self.start_seq = ()
    def __rshift__(self, new_start):
        self.new_start = new_start
        return self
    def __str__(self):
        return self.regexp


# --------------------------------------------------
# Public objects
# --------------------------------------------------

# Token classes

class Token(object):
    """Token object as yielded by the Lexer.scan() generator function.  It
    has three useful attributes:

    toktype: the token type (of class Lexer.tokentype);
    
    val: the token's value (equal to strval unless the token was
      processed in the tokeniser);

    strval: the token's string value, usually the verbatim string
      chunk that the token was made from.
    """
    def __init__(self, toktype, val, strval=None):
        self.toktype = toktype
        self.val = val
        if strval is None:
            self.strval = str(val)
        else:
            self.strval = strval
    def __repr__(self):
        return "<token %s: %s>" % (self.toktype, self.val)
    def __str__(self):
        return self.strval


class TokenType(object):
    """Default value for tokentype."""
    def __init__(self, name):
        self.name = name
    def __repr__(self):
        return "<tokentype %s>" % (self.name)
    def __str__(self):
        return self.name
    def token(self, val, strval=None):
        "Create and return a new token"
        return Token(self, val, strval)
    def wrap(self, process):
        "Wrap a process function"
        TokenType = type(self)
        def wrapped(state, val):
            return self.token(process(state, val), val)
        return wrapped

class SimpleToken(object):
    """A token which is its own token type.  Can be useful for @nowrap'ed
    process functions.  Its name, val and strval are all equalTODO:
    more detail"""
    def __init__(self, name):
        self.toktype = self
        self.val = name
        self.strval = name
        self.name = name
    def __repr__(self):
        return "<simpletoken %s>" % self.val
    def __str__(self):
        return self.strval


# Main class

class Lexer(object):
    """Subclass this class in order to define a new lexer.  Here is a
    simple example.
    
    class Calc(Lexer):
        
        order = 'real', 'integer' # 123.45 is a float!
        separators = 'sep', 'operator', 'bind'
        
        sep = silent(r'\s') # This won't produce tokens
        integer = r'[0-9]+'
        real = r'[0-9]+\.[0-9]*'
        variable = r'[a-z]+'
        operator = r'[+-/*]'
        bind = r'='
        
        def process_integer(state, val): return int(val)
        
        def process_real(state, val): return float(val)
    
    Token types are created automatically:

    >>> Calc.variable
    <tokentype variable>
    >>> print Calc.variable
    variable

    Tokens can be created if needed (but usually just obtained through
    scan():

    >>> varx = Calc.variable.token('x')
    >>> varx
    <token variable: x>
    >>> print varx
    x

    Use the scan() method to tokenise a string.  It returns an
    iterator over all the tokens in the string. 
    
    >>> list(Calc.scan('a = 3.0*2'))
    [<token variable: a>, <token bind: =>, <token real: 3.0>,
    <token operator: *>, <token integer: 2>]
    
    The val attribute of real tokens is a float thanks to the
    process_real() function.

    OPTIONAL ATTRIBUTES

    tokentype: a type to create tokens.  The default is
      lexing.Tokentype and if overriden it should inherit from it or
      implement the same interface

    init_state: the value of the state object when tokenising starts.
      This state object is passed on to process functions and it
      should be possible to change its attributes.  By default it is
      an empty instance of lexing.State.

    separators: a sequence of names of tokens which are separators.
      If not defined, all tokens are considered separators.  A
      non-separator token must be surrounded by separators.

    order: a sequence of token names in order of priority.  If two
      token types match the current position, then the first one in
      this list will be chosen.

    DEFINING RULES FOR TOKENS

    To create a new token type, simply define a class attribute whose
    value is a regular expression recognising that token. E.g.

        ident = r'[_a-zA-Z][_a-zA-Z0-9]*'
        number = r'[0-9]+'

    If you want number token to have an integer value rather than a
    string, define the process_number function.

        def process_number(state, val):
            return int(val)

    Some tokens (e.g. comments) need not be processed at all. Wrap
    their rules in the silent() function.  E.g.

       comment = silent(r'#.*')

    START CONDITIONS

    Use the functions istart() and xstart() to define inclusive and
    exclusive start conditions (with the same meaning as in GNU flex).
    If you want to add a start condition to a token rule, write COND
    >> rule.  If you want a token rule to change the current start
    condition, write rule >> COND (None if you want to clear the start
    condition).  E.g.

        STRING = xstart()

        start_string = r'"' >> STRING
        string_body = STRING >> r'[^"]*'
        end_string = STRING >> '"' >> None
    
    TOKEN OBJECTS

    The token objects yielded by the scan() method have a number of
    useful attributes: 

    val and strval.  By default they are both equal to the string
      recognised by the token, but val can be changed if a
      process_token() function was defined (see example above).

    toktype.  This is the token's type.  Token types are created
      automatically at the creation of the Lexer subclass, and are
      accessible as class attributes.  Each token type has a name
      attribute.
    """
    class __metaclass__(type):
        def __new__(cls, name, bases, attrs):
            tokentype = attrs.pop('tokentype', TokenType)
            init_state = attrs.pop('init_state', State())
            start = {True:[], False:[]}
            separators = attrs.pop('separators', ())
            order = list(attrs.pop('order', ()))
            if separators:
                start_sep = xstart()
                attrs['__SEP__'] = start_sep
            rules = []
            for key, value in attrs.items():
                if isinstance(value, StartCondition):
                    value.name = key
                    start[value.inclusive].append(value)
                elif key[0] != '_' and isinstance(value, (basestring, Rule)):
                    attrs[key] = r = rule(value)
                    if separators:
                        if key in separators:
                            r = start_sep >> None >> r
                            if not hasattr(r, 'new_start'):
                                r = r >> None
                        else:
                            if not hasattr(r, 'new_start'):
                                r = r >> start_sep
                    r.name = key
                    rules.append(r)
            scanner_maker = ScannerMaker(start[True], start[False], init_state)
            def rule_priority(rule, default=len(order)):
                try:
                    return order.index(rule.name)
                except ValueError:
                    return default
            rules.sort(key=rule_priority)
            for r in rules:
                if r.silent:
                    default_process = lambda state, val: None
                else:
                    default_process = lambda state, val: val
                process = attrs.pop('process_' + r.name, default_process)
                if getattr(process, 'wrap', True):
                    if not r.silent:
                        toktype = tokentype(r.name)
                        process = toktype.wrap(process)
                        attrs[r.name] = toktype
                    if hasattr(r, 'new_start'):
                        process = scanner_maker.change_start(process,
                                                             r.new_start)
                scanner_maker.add_rule(r.regexp, process, r.start_seq)
            attrs['scanner'] = scanner = scanner_maker.get_scanner()
            attrs['scan'] = scanner.scan
            return type.__new__(cls, name, bases, attrs)


# Modifier functions to be used in the definition of a lexer.
# (see Lexer.__doc__)

def nowrap(f):
    "Decorator to prevent the lexer for wrapping a process function"
    f.wrap = False
    return f


def rule(obj):
    "Ensure obj is a Rule object (got to check when this is useful)."
    if isinstance(obj, Rule): return obj
    return Rule(obj)

def silent(r):
    "make a rule 'silent', i.e. it will not produce tokens"
    r = rule(r)
    r.silent = True
    return r

def xstart():
    "Return a new exclusive start condition"
    return StartCondition(False)

def istart():
    "Return a new inclusive start condition"
    return StartCondition(True)


