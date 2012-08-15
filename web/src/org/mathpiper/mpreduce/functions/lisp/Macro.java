package org.mathpiper.mpreduce.functions.lisp;

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


// A Lisp macro is really very much like an ordinary
// function with exactly one argument. It is the way that the
// interpreted processes it that makes it different. Well actually because
// of views on Common Lisp compatibility the function that is a macro
// has one essential argument and one optional one (which I never use!)

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.builtin.Fns;

public class Macro extends LispFunction
{
    public LispObject body;

    public void iprint() throws ResourceException
    {
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 7 > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print("[Macro:");
        body.blankprint();
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 1 > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print("]");
    }
											    
    public void blankprint() throws ResourceException
    {
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 7 >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        currentOutput.print("[Macro:");
        body.blankprint();
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 1 > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print("]");
    }
											    
    public Macro()
    {
    }
    
    public Macro(LispObject def) throws Exception
    {
        body = new Cons(Jlisp.lit[Lit.lambda], def);
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
	else LispReader.objects.add(this);
        LispReader.stack.push(body);
    }
    
    
    public LispObject op1(LispObject arg1) throws Exception
    {
        Fns.args[0] = arg1;
        return Fns.applyInner(body, 1);
    }

    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        Fns.args[0] = arg1;
        Fns.args[1] = arg2;
        return Fns.applyInner(body, 2);
    }

}

// End of Macro.java

