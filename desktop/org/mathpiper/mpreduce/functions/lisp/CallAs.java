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
import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.symbols.Symbol;

public class CallAs extends LispFunction
{

public LispObject body;
int nargs;

public CallAs(int nIn, LispObject target, int nPass)
{
    body = target;
    nargs = (nIn<<4) + nPass;
}

public CallAs(int packed)
{
    nargs = packed;
}

public void print() throws ResourceException
{
    print(0);
}

public void print(int fg) throws ResourceException
{   Jlisp.print("#CALL" + (nargs & 0xf) + "as" +
                          ((nargs>>4) & 0xf) + "<");
    body.print(fg);
    Jlisp.print(">");
}

public LispObject op0() throws Exception
{
    if (((nargs>>4) & 0xf) != 0)
        error("Call with wrong number of arguments", body);
    return ((Symbol)body).fn.op0();
}

public LispObject op1(LispObject a1) throws Exception
{
    if (((nargs>>4) & 0xf) != 1)
        error("Call with wrong number of arguments", body);
    if ((nargs & 0xf) == 0) return ((Symbol)body).fn.op0();
    else return ((Symbol)body).fn.op1(a1);
}

public LispObject op2(LispObject a1, LispObject a2) throws Exception
{
    if (((nargs>>4) & 0xf) != 2)
        error("Call with wrong number of arguments", body);
    switch ((nargs & 0xf))
    {
case 0: return ((Symbol)body).fn.op0();
case 1: return ((Symbol)body).fn.op1(a1);
default:return ((Symbol)body).fn.op2(a1, a2);
    }
}

public LispObject opn(LispObject [] args) throws Exception
{
    if (((nargs>>4) & 0xf) != args.length) 
        error("Call with wrong number of arguments", body);
    switch ((nargs & 0xf))
    {
case 0: return ((Symbol)body).fn.op0();
case 1: return ((Symbol)body).fn.op1(args[0]);
case 2: return ((Symbol)body).fn.op2(args[0], args[1]);
default:return ((Symbol)body).fn.opn(
            new LispObject [] { args[0], args[1], args[2] });
    }
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
    
    public void dump() throws Exception
    {
        Object w = LispReader.repeatedObjects.get(this);
	if (w != null &&
	    w instanceof Integer) putSharedRef(w); // processed before
	else
	{   if (w != null) // will be used again sometime
	    {   LispReader.repeatedObjects.put(
	            this,
		    new Integer(LispReader.sharedIndex++));
		Jlisp.odump.write(X_STORE);
            }
            Jlisp.odump.write(X_CALLAS);
            Jlisp.odump.write(nargs);
            LispReader.stack.push(body);
	}
    }
    
}

// End of CallAs.java

