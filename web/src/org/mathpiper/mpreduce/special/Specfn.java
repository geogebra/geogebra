package org.mathpiper.mpreduce.special;

//

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.lisp.Interpreted;
import org.mathpiper.mpreduce.functions.lisp.Macro;
import org.mathpiper.mpreduce.functions.lisp.Undefined;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.symbols.Symbol;

// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2000.
//

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
 *                            also contributions from Vijay Chauhan, 2002 *
 *                                                                        *
 * Redistribution and use in source and binary forms, with or without     *
 * modification, are permitted provided that the following conditions are *
 * met:                                                                   *
 *                                                                        *
 *     * Redistributions of source code must retain the relevant          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer.                                                      *
 *     * Redistributions in binary form must reproduce the above          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer in the documentation and/or other materials provided  *
 *       with the distribution.                                           *
 *                                                                        *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
 * COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
 * DAMAGE.                                                                *
 *************************************************************************/

public class Specfn
{

    public static int        progEvent = 0;
    public static LispObject progData  = null;

    public static final int NONE   = 0;
    public static final int GOTO   = 1;
    public static final int RETURN = 2;


    public Object [][] specials =
    {
        {"cond",     new CondSpecial()},
        {"quote",    new QuoteSpecial()},
        {"function", new FunctionSpecial()},
        {"lambda",   new LambdaSpecial()},
        {"defun",    new DefunSpecial()},
        {"de",       new DefunSpecial()},
        {"dm",       new DmSpecial()},
        {"go",       new GoSpecial()},
        {"setq",     new SetqSpecial()},
        {"if",       new IfSpecial()},
        {"when",     new WhenSpecial()},
        {"unless",   new UnlessSpecial()},
        {"block",    new BlockSpecial()},
        {"~let",     new LetSpecial()},
        {"let*",     new LetStarSpecial()},
        {"prog",     new ProgSpecial()},
        {"and",      new AndSpecial()},
        {"or",       new OrSpecial()},
        {"plus",     new PlusSpecial()},
        {"times",    new TimesSpecial()},
        {"list",     new ListSpecial()},
        {"list*",    new ListStarSpecial()},
        {"declare",  new DeclareSpecial()},
    };



// (quote xx) evaluates to just xx

class QuoteSpecial extends SpecialFunction
{
    public LispObject op(LispObject args)
    {
        if (args.atom) return Environment.nil;
        else return args.car;
    }
}

// in an ideal world (function xxx) would create a closure.
// however my Lisp implementation is shallow-bound and so this is
// not a (convenient) option. I therefore make it a synonym for
// quote.

class FunctionSpecial extends SpecialFunction
{
    public LispObject op(LispObject args)
    {
        if (args.atom) return Environment.nil;
        else return args.car;
    }
}

// (lambda (x) A B C) evaluates to itself, as a minor convenience

class LambdaSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws ResourceException
    {
        return new Cons(Jlisp.lit[Lit.lambda], args);
    }
}

// (cond (p1 e1)        if p1 then e1
//       (p2 e2)        else if p2 then e2
//       (p3 e3) )      else if p3 then e3
//                      else nil

class CondSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {   while (!args.atom)
        {   LispObject a = args;  // ((p1 e1) ...) 
            LispObject x = a.car; // (p1 e1)
            args = a.cdr;
            if (x.atom) continue;
            LispObject predicate = x.car;   // p1
            LispObject consequent = x.cdr;  // (e1)
            predicate = predicate.eval();
            if (progEvent != NONE) return Environment.nil;
            if (predicate != Environment.nil)
            {   LispObject r = Environment.nil;
                while (!consequent.atom)
                {   LispObject cc = consequent;
                    r = cc.car.eval();
                    if (progEvent != NONE) return Environment.nil;
                    consequent = cc.cdr;
                }
                return r;
            }
        }
        return Environment.nil;
    }
}

class IfSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        if (args.atom) return Environment.nil;
        LispObject c1 = args;     // car is the predicate
        if (c1.cdr.atom)
        {   c1.car.eval();        // degenerate case (IF p)
            return Environment.nil;
        }
        LispObject c2 = c1.cdr; // car is the consequent
        c1 = c1.car.eval();
        if (progEvent != NONE) return Environment.nil;
        if (c1 != Environment.nil) return c2.car.eval();
        args = c2.cdr;
        LispObject r = Environment.nil;
        while (!args.atom)
        {   c2 = args;
            r = c2.car.eval();
            if (progEvent != NONE) return Environment.nil;
            args = c2.cdr;
        }
        return r;
    }
}

class WhenSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        if (args.atom) return Environment.nil;
        LispObject c = args;
        if (c.car.eval() == Environment.nil) return Environment.nil;
        if (progEvent != NONE) return Environment.nil;
        args = c.cdr;
        LispObject r = Environment.nil;
        while (!args.atom)
        {   c = args;
            r = c.car.eval();
            if (progEvent != NONE) return Environment.nil;
            args = c.cdr;
        }
        return r;   
    }
}

class UnlessSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        if (args.atom) return Environment.nil;
        LispObject c = args;
        if (c.car.eval() != Environment.nil) return Environment.nil;
        if (progEvent != NONE) return Environment.nil;
        args = c.cdr;
        LispObject r = Environment.nil;
        while (!args.atom)
        {   c = args;
            r = c.car.eval();
            if (progEvent != NONE) return Environment.nil;
            args = c.cdr;
        }
        return r;   
    }
}

// (defun name (a1 a2 a3) body-of-function(with implied progn))

class DefunSpecial extends SpecialFunction
{
    public LispObject op(LispObject a) throws Exception
    {
        if (a.atom) return Environment.nil;  // (de) with no args at all!
        Symbol name = (Symbol)(a.car);
        name.fn = new Interpreted(a.cdr);
        if (Jlisp.lit[Lit.starcomp].car/*value*/ != Environment.nil &&
            !(((Symbol)Jlisp.lit[Lit.compile]).fn instanceof Undefined))
        {   a = new Cons(name , Environment.nil);
            ((Symbol)Jlisp.lit[Lit.compile]).fn.op1(a);
        }
        return name;
    }
}

class DmSpecial extends SpecialFunction
{
    public LispObject op(LispObject a) throws Exception
    {
        if (a.atom) return Environment.nil;
        Symbol name = (Symbol)a.car;
        name.fn = new Macro(a.cdr);
        if (Jlisp.lit[Lit.starcomp].car/*value*/ != Environment.nil &&
            !(((Symbol)Jlisp.lit[Lit.compile]).fn instanceof Undefined))
        {   a = new Cons(name , Environment.nil);
            ((Symbol)Jlisp.lit[Lit.compile]).fn.op1(a);
        }
        return name;
    }
}

class SetqSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        LispObject value = Environment.nil;
        Symbol name;
        while (!args.atom)
        {   name = (Symbol)args.car;
            args = args.cdr;
            if (!args.atom)
            {   value = args.car.eval();
                if (progEvent != NONE) return Environment.nil;
                args = args.cdr;
            }
            else value = Environment.nil;
            name.car/*value*/ = value;
        }
        return value;
    }
}

class BlockSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {    return error("BLOCK not implemented yet");
    }
}

class LetSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {    return error("LET not implemented yet");
    }
}

class LetStarSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {    return error("LET* not implemented yet");
    }
}


class GoSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {    
        if (args.atom) 
            return error("go called without an argument");
        progEvent = GOTO;
        progData = args.car;
        return Environment.nil;
    }
}

class ProgSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        if (args.atom) return Environment.nil; // (PROG)
        LispObject bvl = args.car;
        args = args.cdr;
        if (args.atom) return Environment.nil; // (PROG (v))
        int nvars = 0;
        LispObject bvlsave = bvl;
        while (!bvl.atom)
        {   LispObject w = bvl;
// I really want to check that all the items are symbols here because
// I want the code that saves away values to be guaranteed to succeed
// so that I can be certain not to leave a mess because of things crashing
// part way through
            if (!(w.car instanceof Symbol))
                return error("non-symbol in variable list for prog");
            bvl = w.cdr;
            nvars++;
        }
        LispObject [] save = new LispObject [nvars];
        bvl = bvlsave;
        for (int i=0; i<nvars; i++)
        {   LispObject w = bvl;
            Symbol name = (Symbol)(w.car);
            bvl = w.cdr;
            save[i] = name.car/*value*/;
            name.car/*value*/ = Environment.nil;
        }
        try
        {   LispObject pc = args;
            while (!pc.atom)
            {   LispObject s = pc.car;
                pc = pc.cdr;
                if (!s.atom)
                {   
                    s.eval();
                    switch (progEvent)
                    {
                case RETURN:
                        s = progData;
                        progEvent = NONE;
                        progData = Environment.nil;
                        return s;
                case GOTO:
                        pc = args;
                        while (!pc.atom)
                        {   if (pc.car == progData) break;
                            pc = pc.cdr;
                        }
                        progEvent = NONE;
                        progData = Environment.nil;
                        if (!pc.atom) pc = pc.cdr;
                        else return error("label not found in GO");
                        continue;
                default:
                        continue;
                    }
                }
            }
        }
        finally
        {   bvl = bvlsave;
// Here I restore variables in the same left to right order
// that I saved them. This causes a mess if a name is used
// twice in the list! But working backwards seems unnecessarily
// hard to do (without wasting too much time & space).
            for (int i=0; i<nvars; i++)
            {   LispObject w = bvl;
                Symbol name = (Symbol)(w.car);
                bvl = w.cdr;
                name.car/*value*/ = save[i];
            }
        }
// If the prog block terminates by dropping off the end I
// will give back the result NIL.
        return Environment.nil;
    }
}

class AndSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        LispObject r = Jlisp.lispTrue;
        while (!args.atom)
        {   r = args.car.eval();
            if (progEvent != NONE || r == Environment.nil) break;
            args = args.cdr;
        }
        return r;
    }
}

class OrSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        LispObject r = Environment.nil;
        while (!args.atom)
        {   r = args.car.eval();
            if (progEvent != NONE || r != Environment.nil) break;
            args = args.cdr;
        }
        return r;
    }
}

class PlusSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        if (args.atom) return LispInteger.valueOf(0);
        LispObject r = args.car.eval();
        args = args.cdr;
        while (!args.atom)
        {   r = r.add(args.car.eval());
            args = args.cdr;
        }
        return r;
    }
}

class TimesSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        if (args.atom) return LispInteger.valueOf(1);
        LispObject r = args.car.eval();
        args = args.cdr;
        while (!args.atom)
        {   r = r.multiply(args.car.eval());
            args = args.cdr;
        }
        return r;
    }
}

class ListSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        LispObject r = Environment.nil;
        while (!args.atom)
        {   r = new Cons(args.car.eval(), r);
            args = args.cdr;
        }
        args = Environment.nil;
        while (!r.atom)
        {   LispObject w = r;
            r = r.cdr;
            w.cdr = args;
            args = w;
        }
        return args;
    }
}

class ListStarSpecial extends SpecialFunction
{
    public LispObject op(LispObject args) throws Exception
    {
        if (args.atom) return error("list* with no args");
        LispObject r = Environment.nil;
        while (!args.atom)
        {   r = new Cons(args.car.eval(), r);
            args = args.cdr;
        }
        args = r.car;
        r = r.cdr;
        while (!r.atom)
        {   LispObject w = r;
            r = r.cdr;
            w.cdr = args;
            args = w;
        }
        return args;
    }
}

// (declare ...) is ignored if it ever gets to be evaluated.

class DeclareSpecial extends SpecialFunction
{
    public LispObject op(LispObject args)
    {
        return Environment.nil;
    }
}

}

// End of Specfn.java

