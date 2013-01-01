package org.mathpiper.mpreduce.functions.functionwithenvironment;

//

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.Spid;
import org.mathpiper.mpreduce.datatypes.Cons;

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
public class ByteOpt extends Bytecode
{

// nargs is inherited from Bytecode.
//  treated here as (flags/nopts/nargs) in 2:8:8 bits

// flags & 1     use Spid.noarg not nil as default
// flags & 2     use a &rest argument
//(flags & 4)    (marks a CallAs...)

// The code here seems pretty messy and sordid. Perhaps I can think
// harder some-time and write a cleaned up version!

public ByteOpt(byte [] b, LispObject [] e, int w, int o, int fg)
{
    bytecodes = b;
    env = e;
    nargs = w + (o<<8) + (fg<<16);
}

public ByteOpt(int packed)
{
    bytecodes = null;
    env = new LispObject [0];
    nargs = packed;
}

public LispObject op0() throws Exception
{
    if ((nargs & 0xff) > 0) error("not enough arguments");
    int spsave = sp;
    LispObject r;
    for (int i = 0; i<((nargs>>8)&0xff); i++)
        stack[++sp] = (nargs & 0x10000) != 0 ? (LispObject)Spid.noarg : (LispObject)Environment.nil;
    if ((nargs & 0x20000) != 0) stack[++sp] = Environment.nil;
    try
    {   r = interpret(2);
    }
    finally
    {   sp = spsave;
    }
    return r;
}

public LispObject op1(LispObject a1) throws Exception
{
    if ((nargs & 0xff) > 1) error("not enough arguments");
    int spsave = sp;
    if ((nargs & 0xff) == 0 && ((nargs>>8)&0xff) == 0)
    {   if ((nargs & 0x20000)==0) error("too many args");
        stack[++sp] = new Cons(a1, Environment.nil); // all in the &rest arg
    }
    else
    {   stack[++sp] = a1;
        for (int i = 0; i<(nargs & 0xff)+((nargs>>8)&0xff)-1; i++)
            stack[++sp] = (nargs & 0x10000) != 0 ? (LispObject)Spid.noarg : (LispObject)Environment.nil;
        if ((nargs & 0x20000) != 0) stack[++sp] = Environment.nil;
    }
    LispObject r;
    try
    {   r = interpret(2);
    }
    finally
    {   sp = spsave;
    }
    return r;
}

public LispObject op2(LispObject a1, LispObject a2) throws Exception
{
    if ((nargs & 0xff) > 2) error("not enough arguments");
    int spsave = sp;
    switch ((nargs & 0xff)+((nargs>>8)&0xff))
    {
case 0: if ((nargs & 0x20000)==0) error("too many args");
        stack[++sp] = new Cons(a1, new Cons(a2, Environment.nil));
        break;
case 1: if ((nargs & 0x20000)==0) error("too many args");
        stack[++sp] = a1; // will be either needed or optional
        stack[++sp] = new Cons(a2, Environment.nil);
        break;
case 2: stack[++sp] = a1;
        stack[++sp] = a2;
        if ((nargs & 0x20000)!=0) stack[++sp] = Environment.nil;
        break;
default:stack[++sp] = a1;
        stack[++sp] = a2;
        for (int i = 0; i<(nargs & 0xff)+((nargs>>8)&0xff)-2; i++)
            stack[++sp] = (nargs & 0x10000) != 0 ? (LispObject)Spid.noarg : (LispObject)Environment.nil;
        if ((nargs & 0x20000) != 0) stack[++sp] = Environment.nil;
    }
    LispObject r;
    try
    {   r = interpret(2);
    }
    finally
    {   sp = spsave;
    }
    return r;
}

public LispObject opn(LispObject [] args) throws Exception
{
// @@@
    error("byteopt call with 3 or more args not yet implemented, sorry");
    return Environment.nil;
}

}

// End of ByteOpt.java

