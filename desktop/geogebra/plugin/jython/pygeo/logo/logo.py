from __future__ import division
import re

class Error(Exception):
    def __init__(self, msg):
        Exception.__init__(self, msg)
        self.traceback = []
    def add_traceback(self, proc, line):
        self.traceback.append((proc, line))
    def format_traceback(self):
        return "\n".join("In %s:\n\t%s" % info
                         for info in reversed(self.traceback))
    
class Interrupt(Error):
    def __init__(self, tag, output=None):
        Error.__init__(self, 'Interrupt')
        self.tag = tag
        self.output = output

class Output(Exception):
    def __init__(self, val=None):
        self.value = val


class TokeniserDefinition(object):
    
    def __init__(self, *rules):
        self.rules = [(name, re.compile(ptn), sep) for name, ptn, sep in rules]
    
    def __iter__(self):
        return iter(self.rules)
    
    def scanner(self, stream):
        return Tokeniser(self.rules, stream)


class Token(object):
    def __init__(self, toktype, strval, val=None):
        self.strval = strval
        self.toktype = toktype
        self.val = val


word_ptn = r"([^\s[\]()]|\\.)+"
logo_rules = TokeniserDefinition(
    (None, r'\s+', True),
    (None, r';.+', True),
    ('grouper', r'[[\]()]', True),
    ('infix', r'<=|>=|<>|[+\-*/<>=](?!%s)' % word_ptn, False),
    ('qword', '"(%s)?' % word_ptn, False),
    ('cword', ':(%s)?' % word_ptn, False),
    ('word', word_ptn, False),
)


class Tokeniser(object):

    def __init__(self, rules, stream):
        self.rules = rules
        self.stream = stream
        self.line = ''
        self.pos = 0
        self.separator = True

    def get_line(self):
        self.line = self.stream.next()
        self.separator = True
        self.pos = 0

    def match(self, toktype, ptn, sep):
        if not (self.separator or sep):
            return
        m = ptn.match(self.line, self.pos)
        if m is not None:
            self.pos = m.end()
            self.separator = sep
            if toktype:
                return Token(toktype, m.group(0))

    def __iter__(self):
        return self
    
    def next_in_line(self):
        for toktype, ptn, sep in self.rules:
            tok = self.match(toktype, ptn, sep)
            if tok is not None:
                return tok
    
    def next(self):
        while True:
            if self.pos == len(self.line):
                self.get_line()
            tok = self.next_in_line()
            if tok is not None:
                return tok


def decode_word(text):
    # TODO
    return text

def encode_word(word):
    # TODO
    return word


class LogoCompiler(object):
    
    def __init__(self, stream):
        self.stream = stream
        self.tokens = logo_rules.scanner(stream)

    def compile_line(self):
        prog = []
        self.tokens.get_line()
        for tok in iter(self.tokens.next_in_line, None):
            prog.append(self.compile_element(tok, prog))
        return List(prog)
    
    def compile(self):
        prog = []
        for tok in self.tokens:
            prog.append(self.compile_element(tok, prog))
        return List(prog)
    
    def compile_element(self, tok, context):
        if tok.toktype == 'infix':
            return self.compile_infix_operator(tok, context)
        elif tok.toktype == 'grouper':
            if tok.strval == '[':
                return self.compile_list()
            elif tok.strval == '(':
                return self.compile_group()
            else:
                raise Error("Unbalanced '%s'" % tok.strval)
        elif tok.toktype == 'qword':
            return QuotedWord(decode_word(tok.strval[1:]))
        elif tok.toktype == 'cword':
            return Variable(decode_word(tok.strval[1:]))
        elif tok.toktype == 'word':
            if tok.strval.lower() == 'to':
                return self.compile_function_def()
            else:
                return Word(decode_word(tok.strval))

    def compile_infix_operator(self, tok, context):
        op = infix_operators[tok.strval]
        if not context:
            return op
        for tok in self.tokens:
            right = self.compile_element(tok, [])
            break
        else:
            return op
        left = context.pop()
        return left.compose(op, right)
    
    def compile_function_def(self):
        args = []
        tok = self.tokens.next_in_line()
        if tok.toktype != 'word':
            raise Error("to must be followed by a word")
        name = decode_word(tok.strval)
        for tok in iter(self.tokens.next_in_line, None):
            if tok.toktype != 'cword':
                raise Error("must have variables here")
            args.append(decode_word(tok.strval[1:]))
        prog = []
        while True:
            self.tokens.get_line()
            line = []
            for tok in iter(self.tokens.next_in_line, None):
                if tok.strval.lower() == "end":
                    if line:
                        prog.append(List(line))
                    return ProcedureDef(name, args, prog)
                else:
                    line.append(self.compile_element(tok, line))
            prog.append(List(line))
    
    def compile_list(self):
        items = []
        for tok in self.tokens:
            if tok.toktype == 'grouper' and tok.strval ==']':
                return List(items)
            items.append(self.compile_element(tok, items))
        raise Error("List must end with ']'")

    def compile_group(self):
        items = []
        for tok in self.tokens:
            if tok.toktype == 'grouper' and tok.strval ==')':
                return Group(items)
            items.append(self.compile_element(tok, items))
        raise Error("Group must end with ')'")


class Thing(object):
    name = "thing"
    def print_(self):
        return self.show()
    def run(self, context):
        return self
    def compose(self, op, right):
        return InfixOperation(self, op, right)
    def group_evaluate(self, context, code):
        res = self.evaluate(context, code)
        for instr in code:
            raise Error("Don't know what to do with %s" % instr.show())
        return res
    def __str__(self):
        return self.show()
    def __nonzero__(self):
        raise ValueError
    def is_number(self):
        return False


class InfixOperation(Thing):
    
    def __init__(self, left, op, right):
        self.left = left
        self.op = op
        self.right = right
        self.precedence = op.precedence
    
    def compose(self, op, right):
        if self.precedence <= op.precedence:
            return InfixOperation(self, op, right)
        else:
            return InfixOperation(self.left, self.op,
                                  self.right.compose(op, right))
    def show(self):
        return "%s %s %s" % (self.left.show(),
                             self.op.show(), self.right.show())
    def run(self, context):
        no_code = iter(())
        return self.op.calculate(
            self.left.evaluate(context, no_code),
            self.right.evaluate(context, no_code)
        )

    def evaluate(self, context, code):
        return self.run(context)


class Word(Thing):
    name = "word"
    def __init__(self, word):
        self.word = word
        try:
            num = int(self.word)
        except ValueError:
            try:
                num = float(self.word)
            except ValueError:
                num = None
        self._number_value = num
    def run(self, context):
        try:
            return context.get_var(self.word)
        except KeyError:
            raise Error("%s has no value" % self.word)
    def evaluate(self, context, code, group=False):
        if self._number_value is not None:
            return number(self._number_value)
        try:
            fun = context.get_function(self.word)
        except KeyError:
            raise Error("I don't know how to %s" % self.word)
        return fun.evaluate(context, code, group)
    def group_evaluate(self, context, code):
        return self.evaluate(context, code, True)
    def show(self):
        return self.word
    def __len__(self):
        return len(self.word)
    def __repr__(self):
        return "<%s>" % self.word
    def __getitem__(self, i):
        return Word(self.word[i])
    def __eq__(self, other):
        return (isinstance(other, Word)
                and self.word.lower() == other.word.lower())
    def __nonzero__(self):
        if self.word.lower() == 'true':
            return True
        elif self.word.lower() == 'false':
            return False
        else:
            raise ValueError
    def is_number(self):
        return self._number_value is not None
    @property
    def int_value(self):
        if isinstance(self._number_value, (int, long)):
            return self._number_value
        else:
            raise ValueError
    @property
    def number_value(self):
        if self._number_value is None:
            raise ValueError
        return self._number_value
    def first(self):
        return self[0]
    def last(self):
        return self[-1]
    def butfirst(self):
        if self.word:
            return Word(self.word[1:])
        else:
            raise IndexError
    def butlast(self):
        if self.word:
            return Word(self.word[:-1])
        else:
            raise IndexError


class Boolean(Word):
    def __init__(self, val):
        self.val = val
        self.word = "true" if val else "false"
        self._number_value = None
    def __nonzero__(self):
        return self.val


class Number(Word):
    name = "number"
    def __init__(self, val):
        self.val = val
    def evaluate(self, context, code):
        return self
    def show(self):
        return str(self.val)
    def __repr__(self):
        return str(self.val)
    @property
    def word(self):
        return str(self.val)
    def is_number(self):
        return True
    @property
    def number_value(self):
        return self.val
    def run(self, context):
        return self
    def __eq__(self, other):
        return (isinstance(other, Word)
                and self.number_value == other.number_value)

class Int(Number):
    name = "integer"
    @property
    def int_value(self):
        return self.val

class Float(Number):
    name = "float"
    @property
    def int_value(self):
        val = int(self.val)
        if val == self.val:
            return val
        else:
            raise ValueError

def number(x):
    if isinstance(x, float):
        return Float(x)
    elif isinstance(x, (int, long)):
        return Int(x)
    else:
        raise ValueError


class InfixOperator(Word):
    def __init__(self, word, precedence, calculate):
        Word.__init__(self, word)
        self.precedence = precedence
        self._calculate = calculate
    def calculate(self, a, b):
        try:
            return self._calculate(a, b)
        except ValueError:
            raise Error("%s needs numeric arguments" % self.print_())

def infix_add(a, b):
    return number(a.number_value + b.number_value)

def infix_sub(a, b):
    return number(a.number_value - b.number_value)

def infix_mul(a, b):
    return number(a.number_value * b.number_value)

def infix_div(a, b):
    return number(a.number_value / b.number_value)

def infix_eq(a, b):
    if a == b:
        return Word('true')
    else:
        return Word('false')


infix_operators = {
    '+': InfixOperator('+', 2, infix_add),
    '-': InfixOperator('-', 2, infix_sub),
    '*': InfixOperator('*', 1, infix_mul),
    '/': InfixOperator('/', 1, infix_div),
    '=': InfixOperator('=', 3, infix_eq),
}

class List(Thing):
    name = "list"
    def __init__(self, lst):
        self.lst = lst
    def evaluate(self, context, code):
        return self
    def run_with_output(self, context):
        code = iter(self.lst)
        output = None
        for instr in code:
            if output is not None:
                raise Error("Don't know what to do with %s" % output.show())
            output = instr.evaluate(context, code)
        return output        
    def run(self, context):
        output = self.run_with_output(context)
        if output is not None:
            raise Error("You don't say what to do with %s" % output.show())
    def show(self):
        return '[%s]' % self.print_()
    def print_(self):
        return ' '.join(item.show() for item in self.lst)
    def __len__(self):
        return len(self.lst)
    def __repr__(self):
        return "<LIST %r>" % self.lst
    def __getitem__(self, i):
        return self.lst[i]
    def __eq__(self, other):
        return (isinstance(other, List)
                and all(i == j for i, j in zip(self.lst, other.lst)))
    def first(self):
        return self[0]
    def butfirst(self):
        if self.lst:
            return List(self.lst[1:])
        else:
            raise IndexError
    def last(self):
        return self[-1]
    def butlast(self):
        if self.lst:
            return List(self.lst[:-1])
        else:
            raise IndexError


class Group(Thing):
    def __init__(self, lst):
        self.lst = lst
    def evaluate(self, context, code):
        if not self.lst:
            raise Error("Empty group")
        code = iter(self.lst)
        return code.next().group_evaluate(context, code)
    def __eq__(self, other):
        return (isinstance(other, list)
                and all(i == j for i, j in zip(self.lst, other.lst)))
    

class QuotedWord(Thing):
    name = "quoted word"
    def __init__(self, word):
        self.word = word
    def evaluate(self, context, code):
        return Word(self.word)
    def show(self):
        return '"%s' % self.word
    def __repr__(self):
        return '<"%s>' % self.word


class Variable(Thing):
    name = "variable"
    def __init__(self, word):
        self.word = word
    def evaluate(self, context, code):
        try:
            return context.get_var(self.word)
        except KeyError:
            raise Error("%s has no value" % self.word)
    def show(self):
        return ':%s' % self.word
    def __repr__(self):
        return "<:%s>" % self.word


class Procedure(Thing):
    name = "procedure"
    def __init__(self, context, name, args, prog):
        self.name = name
        self.context = context
        self.args = args
        self.prog = prog
    def evaluate(self, context, code, group=False):
        fun_context = Context(self.context)
        for arg in self.args:
            try:
                instr = code.next()
            except StopIteration:
                raise Error("Not enough arguments for %s" % self.name)
            value = instr.evaluate(context, code)
            fun_context.def_var(arg, value)
        if group:
            for instr in code:
                raise Error("Too many arguments for %s" % self.name)
        try:
            for line in self.prog:
                line.run(fun_context)
        except Output, output:
            return output.value
        except Error, error:
            error.add_traceback(self.name, line.print_())
            raise error
    def __repr__(self):
        return "<FUN %r %r>" % (self.args, self.prog)


class ProcedureDef(Thing):
    def __init__(self, name, args, prog):
        self.name = name
        self.args = args
        self.prog = prog
    def evaluate(self, context, code):
        proc = Procedure(context, self.name, self.args, self.prog)
        context.set_function(self.name, proc)
    def __repr__(self):
        return "<DEF %s %r %r>" % (self.name, self.args, self.prog)


class Context(object):
    def __init__(self, parent):
        self.parent = parent
        self.root = parent.root
        self.wordspace = parent.wordspace
        self.varspace = {}
        self.result = None
    def get_var(self, name):
        name = name.lower()
        if name in self.varspace:
            return self.varspace[name]
        else:
            return self.parent.get_var(name)
    def def_var(self, name, value=None):
        name = name.lower()
        self.varspace[name] = value
    def set_var(self, name, value):
        name = name.lower()
        if name in self.varspace:
            self.varspace[name] = value
        else:
            self.parent.set_var()
    def get_function(self, name):
        return self.wordspace[name.lower()]
    def set_function(self, name, value, silent=False):
        self.wordspace[name.lower()] = value
        if not silent:
            print "Procedure %s defined" % value.name


class RootContext(Context):
    def __init__(self):
        self.wordspace = {}
        self.varspace = {}
        self.result = None
        self.root = self
    def get_var(self, name):
        name = name.lower()
        return self.varspace[name]
    def set_var(self, name, value):
        name = name.lower()
        if name in self.varspace:
            self.varspace[name] = value
        else:
            raise KeyError
    def add_builtin(self, builtin):
        self.set_function(builtin.name, builtin, silent=True)
        for alias in builtin.aliases:
            self.set_function(alias, builtin, silent=True)


class Builtin(object):
    def __init__(self, run, sig, name=None, group_run=None, alias=None):
        self.f = run
        self.sig = sig
        self.name = name or run.__name__
        self.group_f = group_run
        self.aliases = [] if alias is None else alias.split()
    def call(self, context, code, args):
        if len(args) != len(self.sig):
            if self.group_f is not None:
                return self.group_f(context, code, args)
            else:
                raise Error("Wrong number of arguments for %s" % self.name)
        for arg, tp in zip(args, self.sig):
            if not isinstance(arg, tp):
                msg = "%s argument %s must be a %s" % (
                    self.name, arg.show(), tp.name)
                raise Error(msg)
        return self.f(context, code, *args)
    def evaluate(self, context, code, group=False):
        args = []
        if group:
            for instr in code:
                args.append(instr.evaluate(context, code))
        else:
            for tp in self.sig:
                args.append(code.next().evaluate(context, code))
        return self.call(context, code, args)
    def set_group_eval(self, f):
        self.group_f = f
        return f


Error.tag = Word("error")
