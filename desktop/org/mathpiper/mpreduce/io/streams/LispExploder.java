package org.mathpiper.mpreduce.io.streams;

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
import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.symbols.Symbol;

public class LispExploder extends LispStream
{

    boolean asSymbols;

    public LispExploder(boolean n) // builds a list of all characters
                            // n true for symbols, false for numeric codes
    {
        super("<exploder>");
        asSymbols = n;
        exploded = Environment.nil;
    }

    public void flush()
    {
    }

    public void close()
    {
        exploded = Environment.nil;
    }

    public void print(String s) throws ResourceException
    {
        char [] v = s.toCharArray();
        for (int i=0; i<v.length; i++)
        {   char c = v[i];
            LispObject w;
            if (asSymbols)
            {   if ((int)c < 128) w = LispReader.chars[(int)c];
                else w = Symbol.intern(String.valueOf(c));
            }
            else w = LispInteger.valueOf((int)c);
            exploded = new Cons(w, exploded);
        }
    }

    public void println(String s) throws ResourceException
    {
        print(s);
        if (asSymbols) exploded = new Cons(LispReader.chars['\n'], exploded);
        else exploded = new Cons(LispInteger.valueOf('\n'), exploded);
    }

}

// end of LispExploder.java


