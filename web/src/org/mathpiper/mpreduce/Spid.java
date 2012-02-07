package org.mathpiper.mpreduce;

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


import org.mathpiper.mpreduce.exceptions.ResourceException;

// This is an object that the user should NEVER get directly hold of
// but which may be used internally as a marker.

public class Spid extends LispObject
{
    public int tag;
    public int data;   // NB NB NB   the field not saved in checkpoint files

    public static final int FBIND    = 1;  // free bindings on stack in bytecode
    public static final int NOARG    = 2;  // "no argument" after &opt
    public static final int DEFINMOD = 3;  // introduces bytecode def in fasl file

    public static final Spid fbind = new Spid(FBIND);
    public static final Spid noarg = new Spid(NOARG);

    public Spid(int tag)
    {
        this.tag = tag & 0xff;
        data = 0;
    }

    public Spid(int tag, int data)
    {
        this.tag = tag & 0xff;
        this.data = data;
    }

    public LispObject eval()
    {
        return this;
    }

    public void iprint() throws ResourceException
    {
        String s = "#SPID" + tag;
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print(s);
    }

    public void blankprint() throws ResourceException
    {
        String s = "#SPID" + tag;
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        currentOutput.print(s);
    }

    public void scan()
    {
        Object w = new Integer(tag);
        if (LispReader.objects.contains(w)) // seen before?
	{   if (!LispReader.repeatedObjects.containsKey(w))
	    {   LispReader.repeatedObjects.put(
	            w,
	            Environment.nil); // value is junk at this stage
	    }
	}
	else LispReader.objects.add(w);
    }
    


}

