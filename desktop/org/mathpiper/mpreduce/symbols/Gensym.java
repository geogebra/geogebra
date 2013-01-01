package org.mathpiper.mpreduce.symbols;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2011.
//

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.functions.lisp.Undefined;

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

public class Gensym extends Symbol
{
    public String nameBase = "G";
    public static int gensymCounter = 0;
    public int myNumber = -1;

    public Gensym(String name)
    {
        pname = null;
        nameBase = name;
        car/*value*/ = Jlisp.lit[Lit.undefined];
        cdr/*plist*/ = Environment.nil;
        fn = new Undefined(name);
        special = null;
        myNumber = -1;
    }

    public void completeName()
    {   if (pname != null) return;
        pname = nameBase + (myNumber = gensymCounter++);
    }

    public void dump() throws Exception
    {
        Object w = LispReader.repeatedObjects.get(this);
	if (w != null &&
	    w instanceof Integer)
	    putSharedRef(w); // processed before
	else
	{   if (w != null) // will be used again sometime
	    {   LispReader.repeatedObjects.put(
	            this,
		    new Integer(LispReader.sharedIndex++));
		Jlisp.odump.write(X_STORE);
            }
	    byte [] rep = nameBase.getBytes("UTF8");
	    int length = rep.length;
	    putPrefix2(length, X_GENSYMn, X_GENSYM);
	    for (int i=0; i<length; i++)
	        Jlisp.odump.write(rep[i]);
            Jlisp.odump.write(myNumber & 0xff);
            Jlisp.odump.write((myNumber >> 8) & 0xff);
            Jlisp.odump.write((myNumber >> 16) & 0xff);
            Jlisp.odump.write((myNumber >> 24) & 0xff);
	    if (Jlisp.descendSymbols)	
	    {   LispReader.stack.push(car/*value*/);
	        LispReader.stack.push(cdr/*plist*/);
	        LispReader.stack.push(special);
	        LispReader.stack.push(fn);
	    }
	}
    }

}

// end of Gensym.java

