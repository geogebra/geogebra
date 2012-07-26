from __future__ import division, with_statement
from logo import LogoCompiler, List, Thing, Word, Output, Builtin, Error, number, QuotedWord, Boolean, Int, Interrupt
import random
from functools import wraps
from geogebra.awt import GColorD as Color

builtin_list = []

def decorator_withargs(decf):
    @wraps(decf)
    def decorator(*args, **kwargs):
        def decorated(f):
            return decf(f, *args, **kwargs)
        return decorated
    return decorator

@decorator_withargs
def builtin(f, *sig, **kwargs):
    b =  Builtin(f, sig, **kwargs)
    builtin_list.append(b)
    return f

@decorator_withargs
def builtin1_n(f, sig, **kwargs):
    b = Builtin(f, (sig,), **kwargs)
    builtin_list.append(b)
    def fn(context, code, args):
        for arg in args:
            f(context, code, arg)
    b.set_group_eval(fn)
    return f

@decorator_withargs
def builtin2_n(f, sig, **kwargs):
    def f2(context, code, t1, t2):
        return f(context, code, (t1, t2))
    f2.__name__ = f.__name__
    b = Builtin(f2, (sig, sig), **kwargs)
    builtin_list.append(b)
    b.set_group_eval(f)
    return f

@decorator_withargs
def builtin_predicate(f, *sig, **kwargs):
    @builtin(*sig, **kwargs)
    @wraps(f)
    def bool_f(*args, **kwargs):
        return Boolean(f(*args, **kwargs))
    return f

#
# 2.1 Constructors
#

# word
@builtin2_n(Word)
def word(context, code, args):
    try:
        return Word(''.join(w.word for w in args))
    except AttributeError:
        raise Error("word wants words as inputs")

# list
@builtin2_n(Thing, name="list")
def list_(context, code, args):
    return List(args)

# sentence
@builtin2_n(Thing)
def sentence(context, code, args):
    s = []
    for thing in args:
        if isinstance(thing, List):
            s.extend(thing)
        else:
            s.append(thing)
    return List(s)

# fput
@builtin(Thing, List)
def fput(context, code, item, lst):
    result = [item]
    result.extend(lst.list)
    return List(result)

# lput
@builtin(Thing, List)
def lput(context, code, item, lst):
    result = list(lst.list)
    result.append(item)
    return List(result)

# array
# mdarray
# listtoarray
# arraytolist

# combine
@builtin(Thing, Thing)
def combine(context, code, th1, th2):
    try:
        if isinstance(th2, Word):
            return word(context, code, th1, th2)
        elif isinstance(th2, List):
            return fput(context, code, th1, th2)
    except Error:
        pass
    raise Error("I can't combine %s and %s" % (th1, th2))

# reverse
@builtin(List)
def reverse(context, code, lst):
    return List(lst.list[::-1])

# gensym


#
# 2.2 Data selectors
#

#first
@builtin(Thing)
def first(context, code, arg):
    try:
        return arg.first()
    except IndexError:
        raise Error("%s has no first element" % arg)

# firsts

# last
@builtin(Thing)
def last(context, code, arg):
    try:
        return arg.last()
    except IndexError:
        raise Error("%s has no last element" % arg)

# butfirst
@builtin(Thing)
def butfirst(context, code, arg):
    try:
        return arg.butfirst()
    except IndexError:
        raise Error("butfirst doesn't like %s as input" % arg)

# butfirsts

# butlast
@builtin(Thing)
def butlast(context, code, arg):
    try:
        return arg.butlast()
    except IndexError:
        raise Error("butfirst doesn't like %s as input" % arg)

# item
@builtin(Word, Thing)
def item(context, code, index, arg):
    try:
        i = index.int_value - 1
        if i < 0:
            raise ValueError
        return arg[i]
    except ValueError:
        raise Error("item can't use %s as index" % index)
    except IndexError:
        raise Error("There is no item with index %s in %s" % (index, arg))

# mditem

# pick
@builtin(List)
def pick(context, code, items):
    return random.choice(items.list)

# remove
@builtin(Thing, List)
def remove(context, code, th, lst):
    return List([x for x in lst if x != th])

# remdup

# quoted
@builtin(Thing)
def quoted(context, code, th):
    if isinstance(th, Word):
        return QuotedWord(th.word)
    else:
        return th


#
# 2.3 Data mutators
#

# setitem
# mdsetitem
# .setfirst
# .setbf
# .setitem
# push
# pop
# queue
# dequeue


#
# 2.4 Predicates
#

# wordp
@builtin_predicate(Thing)
def wordp(context, code, th):
    return isinstance(th, Word)

# listp
@builtin_predicate(Thing)
def listp(context, code, th):
    return isinstance(th, List)

# arrayp

# emptyp
@builtin_predicate(Thing)
def emptyp(context, code, th):
    return not th

# equalp
@builtin_predicate(Thing, Thing)
def equalp(context, code, th1, th2):
    return th1 == th2

# notequalp
@builtin_predicate(Thing, Thing)
def notequalp(context, code, th1, th2):
    return th1 != th2

# beforep
@builtin_predicate(Word, Word)
def beforep(context, code, w1, w2):
    return w1.word < w2.word

# .eq
@builtin_predicate(Thing, Thing, name=".eq")
def _dot_eq(context, code, th1, th2):
    return th1 is th2

# memberp
@builtin_predicate(Thing, Thing)
def memberp(context, code, th1, th2):
    if isinstance(th2, List):
        return th1 in th2.lst
    elif isinstance(th1, Word) and isinstance(th2, Word):
        return len(th1.word) == 1 and th1.word in th2.word
    return False

# substringp
@builtin_predicate(Thing, Thing)
def substringp(context, code, th1, th2):
    if isinstance(th1, Word) and isinstance(th2, Word):
        return th1.word in th2.word
    return False

# numberp
@builtin_predicate(Thing)
def numberp(context, code, th):
    return th.is_number()

# vbarredp
# backslashedp


#
# 2.5 Queries
#

# count
@builtin(Thing)
def count(context, code, th):
    try:
        return len(th)
    except TypeError:
        raise Error("count doesn't like %s" % th)

# ascii
# rawascii
# char
# member
# lowercase
# uppercase
# standout
# parse
# runparse


#
# 3.1 Transmitters
#

# print
@builtin1_n(Thing, name='print')
def print_(context, code, th):
    print th.print_()

# type

# show
@builtin1_n(Thing)
def show(context, code, th):
    print th.show()


#
# 3.2 Receivers
#


#
# 8.1 Control
#

# run
# XXX does not output anything
@builtin(List)
def run(context, code, prog):
    return prog.run_with_output(context)

# runresult
@builtin(List)
def runlist(context, code, prog):
    result = prog.run_with_output(context)
    if result is None:
        return List([])
    else:
        return List([result])

# repeat
@builtin(Word, List)
def repeat(context, code, n, prog):
    try:
        n = n.int_value
    except ValueError:
        raise Error("REPEAT doesn't like %s as first input" % n.show())
    for i in range(1, n + 1):
        context.def_var('__repcount__', i)
        prog.run(context)

# forever
@builtin(List)
def forever(context, code, prog):
    for i in count(1):
        context.def_var('__repcount__', i)
        prog.run(context)


#repcount
@builtin()
def repcount(context, code):
    try:
        i = context.get_var('__repcount__')
    except KeyError:
        i = -1
    return Int(i)

# if
@builtin(Thing, List, name="if")
def if_(context, code, cond, true_action):
    try:
        truth = bool(cond)
    except ValueError:
        raise Error("First input to IF must be TRUE or FALSE")
    if truth:
        return true_action.run_with_output(context)

# ifelse
@builtin(Thing, List, List)
def ifelse(context, code, cond, true_action, false_action):
    try:
        action = true_action if cond else false_action
    except ValueError:
        raise Error("First input to IFELSE must be TRUE or FALSE")
    return action.run_with_output(context)

# test
@builtin(Thing)
def test(context, code, cond):
    try:
        context.def_var('__test__', bool(cond))
    except ValueError:
        raise Error("TEST input must be TRUE or FALSE")

# iftrue
@builtin(List)
def iftrue(context, code, action):
    try:
        truth = context.get_var('__test__')
    except KeyError:
        raise Error("IFTRUE needs to follow a TEST")
    if truth:
        return action.run_with_output(context)

# iffalse
@builtin(List)
def iffalse(context, code, action):
    try:
        truth = context.get_var('__test__')
    except KeyError:
        raise Error("IFFALSE needs to follow a TEST")
    if not truth:
        return action.run_with_output(context)

# stop
@builtin()
def stop(context, code):
    raise Output()

# output
@builtin(Thing, name="output")
def output(context, code, val):
    raise Output(val)

# catch
@builtin(Word, List)
def catch(context, code, tag, prog):
    try:
        prog.run(context)
    except Interrupt, e:
        if e.tag == tag:
            return e.output
        raise
    except Error, e:
        if tag.word.lower() == "error":
            return
        raise

# throw
@builtin(Word)
def throw(context, code, tag):
    raise Interrupt(tag)

def group_throw(context, code, args):
    tag, output = args
    raise Interrupt(tag, output)

builtin_list[-1].set_group_eval(group_throw)

# error

# pause
# continue
# wait
# bye
# .maybeoutput
# goto
# tag
# ignore
# `
# for
# do.while
# do.while
# while
# do.until
# until
# case
# cond

@builtin2_n(Word, name='sum')
def sum_(context, code, args):
    try:
        return number(sum(x.number_value for x in args))
    except ValueError:
        raise Error("sum needs numbers as inputs")

@builtin(Word)
def load(context, code, filename):
    with open(filename.word, "r") as stream:
        code = LogoCompiler(stream).compile()
        code.run(context)


# Turtle motion

@builtin(alias="pu")
def penup(context, code):
    context.root.turtle.pen_down = False

@builtin(alias="pd")
def pendown(context, code):
    context.root.turtle.pen_down = True

@builtin(Word, alias="fd")
def forward(context, code, w):
    context.root.turtle.forward(w.number_value)

@builtin(Word, alias="bk")
def back(context, code, w):
    context.root.turtle.back(w.number_value)

@builtin(Word, alias="lt")
def left(context, code, w):
    context.root.turtle.turn_left(w.number_value)

@builtin(Word, alias="rt")
def right(context, code, w):
    context.root.turtle.turn_right(w.number_value)

@builtin(Word)
def setpensize(context, code, w):
    context.root.turtle.pen_thickness = w.number_value

@builtin(Word)
def setpencolor(context, code, w):
    try:
        color = getattr(Color, w.word.upper())
    except AttributeError:
        raise Error("I don't know the color %s" % w.word)
    context.root.turtle.pen_color = color

@builtin(Word)
def setspeed(context, code, w):
    context.root.turtle.speed = w.number_value

#def turtle_command(logo_name, turtle_name=None):
#    if turtle_name is None:
#        turtle_name = logo_name
#    turtle_cmd = getattr(turtle, turtle_name)
#    @builtin(name=logo_name)
#    def logo_cmd(context, code):
#        turtle_cmd()
#
#for cmd in "pendown pd penup pu".split():
#    turtle_command(cmd)
#
#turtle_command("clean", "clear")
#turtle_command("clearscreen", "reset")
#
#def turtle_command_number(logo_name, turtle_name=None):
#    if turtle_name is None:
#        turtle_name = logo_name
#    turtle_cmd = getattr(turtle, turtle_name)
#    @builtin(Word, name=logo_name)
#    def logo_cmd(context, code, arg):
#        turtle_cmd(arg.number_value)
#
#for cmd in 'forward fd back bk left right setx sety setheading seth'.split():
#    turtle_command_number(cmd)
#
#turtle_command_number("setpensize", "pensize")
#turtle_command_number("setspeed", "speed")
