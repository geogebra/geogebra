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


import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.exceptions.ResourceException;

public class LispVector extends LispObject
{
    public LispObject [] vec;

    public LispVector(int n)
    {
        vec = new LispObject [n];
        for (int i=0; i<n; i++) vec[i] = Environment.nil;
    }

    public LispVector(LispObject [] v)
    {
        vec = v;
    }

    public LispObject eval()
    { 
        return this; 
    }

    public void iprint() throws ResourceException
    {
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 1 > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print("[");
        if (vec.length == 0)
	{   if ((currentFlags & noLineBreak) == 0 &&
                currentOutput.column + 1 > currentOutput.lineLength)
                currentOutput.println();
            currentOutput.print("]");
            return;
        }
        if (vec[0] == null)
        {   if ((currentFlags & noLineBreak) == 0 &&
                currentOutput.column + 1 > currentOutput.lineLength)
                currentOutput.println();
            currentOutput.print(".");
        }
        else vec[0].iprint();
        for (int i=1; i<vec.length; i++)
	{   if (vec[i] == null)
            {   if ((currentFlags & noLineBreak) == 0 &&
                    currentOutput.column + 1 >= currentOutput.lineLength)
                    currentOutput.println();
                else currentOutput.print(" ");
                currentOutput.print(".");
            }
            else vec[i].blankprint();
        }
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 1 > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print("]");
    }

    public void blankprint() throws ResourceException
    {
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + 1 >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        iprint();
    }

    public boolean lispequals(Object b)
    {
        if (!(b instanceof LispVector)) return false;
        if (b == this) return true;
        else if (this == LispReader.obvector || b == LispReader.obvector) return false;
        LispVector vb = (LispVector)b;
        if (vec.length != vb.vec.length) return false;
        for (int i=0; i<vec.length; i++)
            if (!vec[i].lispequals(vb.vec[i])) return false;
        return true;
    }

    public int lisphashCode()
    {
        return lisphashCode(100);
    }

    public int lisphashCode(int n)
    {
        int r = 19937;
        for (int i=0; i<vec.length; i++) 
	{   LispObject b = vec[i];
            if (b == null) r = 54321*r;
            else if (!b.atom)
                r = 169*r + ((Cons)b).lisphashCode(b, n-10);
            else r = 0x8040201*r + b.lisphashCode();
        }
        return r;  
    }

    public void scan()
    {
        if (this == LispReader.obvector) return;
        if (LispReader.objects.contains(this)) // seen before?
	{   if (!LispReader.repeatedObjects.containsKey(this))
	    {   LispReader.repeatedObjects.put(
	            this,
	            Environment.nil); // value is junk at this stage
	    }
	}
	else LispReader.objects.add(this);
	for (int i=0; i<vec.length; i++)
	    LispReader.stack.push(vec[i]);
    }
    


}

// end of LispVector.java

