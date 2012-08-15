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

import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.symbols.Symbol;

public class TracedFunction extends LispFunction
{
    Symbol name;
    public LispFunction fn;
    static int traceDepth = 0;
        
    public TracedFunction(Symbol name, LispFunction fn)
    {
        this.name = name;
        this.fn = fn;
    }
    
    void indent() throws ResourceException
    {
        for (int i=0; i<traceDepth; i++)
	    Jlisp.traceprint(" ");
    }
    
    public LispObject op0() throws Exception
    {
        indent();
        Jlisp.traceprint("Calling "); name.tracePrint();
	Jlisp.traceprintln(" with 0 args");
	traceDepth++;
        LispObject r = fn.op0();
	traceDepth--;
	indent();
	name.tracePrint(); Jlisp.traceprint(" = ");
	r.tracePrint(); Jlisp.traceprintln();
        return r;
    }

    public LispObject op1(LispObject a1) throws Exception
    {
        indent();
        Jlisp.traceprint("Calling "); name.tracePrint();
	Jlisp.traceprintln();
	indent();
	Jlisp.traceprint("Arg1: "); a1.tracePrint();
	Jlisp.traceprintln();
	traceDepth++;
        LispObject r = fn.op1(a1);
	traceDepth--;
	indent();
	name.tracePrint(); Jlisp.traceprint(" = ");
	r.tracePrint(); Jlisp.traceprintln();
        return r;
    }

    public LispObject op2(LispObject a1, LispObject a2) throws Exception
    {
        indent();
        Jlisp.traceprint("Calling "); name.tracePrint();
	Jlisp.traceprintln();
	indent();
	Jlisp.traceprint("Arg1: "); a1.tracePrint();
	Jlisp.traceprintln();
	indent();
	Jlisp.traceprint("Arg2: "); a2.tracePrint();
	Jlisp.traceprintln();
	traceDepth++;
        LispObject r = fn.op2(a1, a2);
	traceDepth--;
	indent();
	name.tracePrint(); Jlisp.traceprint(" = ");
	r.tracePrint(); Jlisp.traceprintln();
        return r;
    }

    public LispObject opn(LispObject [] args) throws Exception
    {
        indent();
        Jlisp.traceprint("Calling "); name.tracePrint();
	Jlisp.traceprintln();
	for (int i=0; i<args.length; i++)
	{   indent();
	    Jlisp.traceprint("Arg" + i + ": ");
	    args[i].tracePrint();
	    Jlisp.traceprintln();
	}
	traceDepth++;
        LispObject r = fn.opn(args);
	traceDepth--;
	indent();
	name.tracePrint(); Jlisp.traceprint(" = ");
	r.tracePrint(); Jlisp.traceprintln();
        return r;
    }

    public void print() throws ResourceException
    {
        Jlisp.print("Traced:");
	name.print();
    }

    public void print(int n) throws ResourceException
    {
        Jlisp.print("Traced:");
	name.print(n);
    }
    
// If you take a checkpoint image and some functions are traced then
// in the dump the fact of tracing is thrown away and when an image is
// re-loaded the functions will not be traced any more. I could have
// saved trace info if I had wanted but this is MARGINALLY easier and
// perhaps in some ways nicer?
    
    public void scan()
    {
        fn.scan();
    }
    

}

// End of TracedFunction.java


