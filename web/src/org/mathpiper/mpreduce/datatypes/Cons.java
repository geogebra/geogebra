package org.mathpiper.mpreduce.datatypes;

//
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


// A "cons" is an ordered pair. In ML terms it would be
// a bit like ('a * 'b)

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.exceptions.ProgEvent;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.builtin.Fns;
import org.mathpiper.mpreduce.functions.lisp.Macro;
import org.mathpiper.mpreduce.special.Specfn;
import org.mathpiper.mpreduce.symbols.Symbol;

public class Cons extends LispObject
{

    public static int consCount = 0;
    static int consCountDown = 1000000;

// The left and right parts of a pair are called
//                CAR and CDR

    public Cons()
    {
        super(null, null);
    }

    public Cons(LispObject car, LispObject cdr) throws ResourceException {
        super(car, cdr);
        consCount++;
        if (--consCountDown < 0) {
            consCountDown = 1000000;
            ResourceException.space_now++;
            if (ResourceException.space_limit > 0
                    && ResourceException.space_limit < ResourceException.space_now)
                         {   if (Jlisp.headline)
 	  	                 {   Jlisp.errprintln();
 	  	                     Jlisp.errprintln("+++ space usage limit exceeded");
 	  	                 }
                throw new ResourceException("space");
            }
        }
    }

// Function calls are written as lists (fn a1 a2 ...)
    public LispObject eval() throws Exception
    {
        if(Jlisp.interruptEvaluation == true)
        {
                Jlisp.interruptEvaluation = false;
                Jlisp.error("Evaluation Interrupted.");
        }
                int n = 0;
        try         // So I can display a backtrace of my own
        {   Symbol fname = null;
            if (car instanceof Symbol)
            {   fname = (Symbol)car;
                if (fname.fn instanceof Macro)
                {   LispObject r = fname.fn.op1(this); // use 1-arg version
                    return r.eval();
                }
                else if (fname.special != null)
                {   return fname.special.op(cdr);
                }
            }
            LispObject a;
            for (a=cdr; !a.atom; a = a.cdr) n++;
            if (fname != null)
            {   switch (n)
                {
            case 0: return fname.fn.op0();
            case 1: a = cdr.car.eval();
                    if (Specfn.progEvent != Specfn.NONE) return Environment.nil;
                    return fname.fn.op1(a);
            case 2: a = cdr.car.eval();
                    if (Specfn.progEvent != Specfn.NONE) return Environment.nil;
                    LispObject b = cdr.cdr.car.eval();
                    if (Specfn.progEvent != Specfn.NONE) return Environment.nil;
                    return fname.fn.op2(a, b);
            default:
                    LispObject [] args = new LispObject [n];
                    n = 0;
                    for (a=cdr;
                         !a.atom;
                         a = a.cdr)
                    {   args[n++] = a.car.eval();
                        if (Specfn.progEvent != Specfn.NONE) return Environment.nil;
                    }
                    return fname.fn.opn(args);
                }
            }
            LispObject [] args = new LispObject [n];
            n = 0;
            for (a=cdr;
                 !a.atom;
                 a = a.cdr) args[n++] = a.car;
// Now the head of the list is not a symbol. The only
// other legal possibility is that it is a lambda-expression,
// so I should look for (lambda vars body ...)
            if (!car.atom)
            {   for (int i=0; i<n; i++)
                {   args[i] = args[i].eval();
                    if (Specfn.progEvent != Specfn.NONE) return Environment.nil;
                }
                for (int i=0; i<n; i++) Fns.args[i] = args[i];
                args = null;
                return Fns.applyInner(car, n);
            }
            else return Jlisp.error("unknown form of expression for evaluation");
        }
        catch (ProgEvent e)
        {   throw e;
        }
        catch (Exception e)
        {   if (Jlisp.backtrace)
            {   Jlisp.errprint("Evaluating: ");
                this.errPrint();
                Jlisp.errprintln();
            }
            throw e;
        }
    }

// Lists print as (a b c ... )
// and if a list ends in NIL then it is displayed with
// just a ")" at the end, otherwise the final atom is
// shown after a "."

    public void iprint() throws ResourceException
    {
        LispObject x = this;
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 1 > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print("(");
        if (x.car == null) 
        {   if ((currentFlags & noLineBreak) == 0 &&
                currentOutput.column + 6 > currentOutput.lineLength)
                currentOutput.println();
            currentOutput.print("<null>");
        }
        else x.car.iprint();
        x = x.cdr;
        while (x != null && !x.atom)
        {   if (car == null)
            {   if ((currentFlags & noLineBreak) == 0 &&
                    currentOutput.column + 6 >= currentOutput.lineLength)
                    currentOutput.println();
                else currentOutput.print(" ");
                currentOutput.print("<null>");
            }
            else x.car.blankprint();
            x = x.cdr;
        }
        if (x != Environment.nil)
        {   if ((currentFlags & noLineBreak) == 0 &&
                currentOutput.column + 1 >= currentOutput.lineLength)
                currentOutput.println();
            else currentOutput.print(" ");
            currentOutput.print(".");
            if (x == null)
            {   if ((currentFlags & noLineBreak) == 0 &&
                    currentOutput.column + 6 >= currentOutput.lineLength)
                    currentOutput.println();
                else currentOutput.print(" ");
                currentOutput.print("<null>");
            }
            else x.blankprint();
        }
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 1 > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print(")");
    }

    public void blankprint() throws ResourceException
    {
        if (currentOutput.column + 1 >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        iprint();
    }

    public LispObject copy()
    {
        LispObject a = this;
        LispObject r = Environment.nil;
        while (!a.atom) {
            int re = ResourceException.space_limit;
            ResourceException.space_limit = -1;
            try {
                r = new Cons(a.car.copy(), r);
            } catch (ResourceException e) {   // Because I reset space_limit this can never happen!
            }
            ResourceException.space_limit = re;
            a = a.cdr;
        }
        while (!r.atom)
        {   LispObject w = r;
            r = r.cdr;
            w.cdr = a;
            a = w;
        }
        return a;
    }

    public boolean lispequals(Object b)
    {
        if (b == this) return true;
        else if (!(b instanceof Cons)) return false;
        LispObject a1 = this, b1 = (LispObject)b;
        for (;;)
        {   LispObject p1 = a1.car, q1 = b1.car;
            if (!p1.lispequals(q1)) return false;
            p1 = a1.cdr;
            q1 = b1.cdr;
            if (p1 == q1) return true;
            if (p1.atom) return p1.lispequals(q1);
            if (q1.atom) return false;
            a1 = p1;
            b1 = q1;
        }
    }

  // The idea used to hash Cons cells here is to accept that I have to
  // drop through and do a recursive tree walk. But very deep trees
  // and especially looped up structures would be a MENACE. So I truncate
  // the search at a depth of "100" where each CAR direction link costs
  // 10 and each CDR direction link costs only 1. The expectation is that
  // this limits the total cost to O(1000) - bad but tolerable. When I
  // exceed the limit I must hand back a fixed value. I use crude and
  // not-thought-out arithmetic to combine hash-values of components.
  // Note that if a tree contains vectors I will need to limit recursion
  // through them too.
    public int lisphashCode()
    {
        return lisphashCode(this, 100);
    }

    public int lisphashCode(LispObject a, int n)
    {   int r = 9990;
        while (n >= 0 && !a.atom)
        {   n--;
            LispObject ca = a;
            if (!ca.car.atom)
                r = 169*r - lisphashCode(ca.car, n-10);
            else r = 11213*r + ca.car.lisphashCode();
            a = ca.cdr;
        }
        if (n < 0) return r + 212215;
        else if (a instanceof LispVector)
            return ((LispVector)a).lisphashCode(n-3)*0xfade0ff - r;
        else return a.lisphashCode()*0xDe5ade + r;
    }

    public void scan()
    {
        if (LispReader.objects.contains(this)) // seen before?
        {   if (!LispReader.repeatedObjects.containsKey(this))
            {   LispReader.repeatedObjects.put(
                    this,
                    Environment.nil); // value is junk at this stage
            }
        }
        else
        {   LispReader.objects.add(this);
            LispReader.stack.push(cdr);
            LispReader.stack.push(car);
        }
    }



}

// End of Cons.java

