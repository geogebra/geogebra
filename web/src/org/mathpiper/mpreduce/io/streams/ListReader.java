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
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.symbols.Symbol;

public class ListReader extends LispStream
{

    public ListReader(LispObject data)
    {
        super("<read from list>");
        inputData = data;
        needsPrompt = false;
        escaped = false;
        this.allowOctal = allowOctal;
        nextChar = -2;
    }

    public int read() throws Exception
    {
        if (inputData.atom) return -1;
        LispObject w = inputData.car;
        inputData = inputData.cdr;
        if (w instanceof LispString)
            return (int)((LispString)w).string.charAt(0);
        else if (w instanceof Symbol)
            return (int)((Symbol)w).pname.charAt(0);
        else if (w instanceof LispInteger)
            return w.intValue();
        else return -1;
    }

    public void close()
    {
        inputData = Environment.nil;
    }

}

// end of ListReader.java
