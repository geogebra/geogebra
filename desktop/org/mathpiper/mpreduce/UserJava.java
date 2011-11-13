package org.mathpiper.mpreduce;

import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.exceptions.ResourceException;


// This is to demonstrate how user Java code can be called from Jlisp.
// You may edit this file to put in arbitrary Java definitions in the
// various methods (which had better be public static and named as shown 
// here). 
//
// From within Jlisp (and hence REDUCE) the function USERJAVA will then
// call the method from here that corresponds to the relevant number of
// arguments. This class MUST be called "UserJava" but it is loaded
// dynamically when Jlisp is running and when the first use of it is
// attempted. So the class file must be somewhere that the default Java
// classloader will look. But this file does NOT need to be present when
// Jlisp is built.
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


public class UserJava
{
    public static LispObject op0()
    {
        return new LispString("Sample");
    }

    public static LispObject op1(LispObject a) throws ResourceException
    {
        return new Cons(a, a);
    }

    public static LispObject op2(LispObject a, LispObject b) throws ResourceException
    {
        return new Cons(b, a);
    }

    public static LispObject opn(LispObject [] a) throws ResourceException
    {
        LispObject r = Environment.nil;
        for (int i=0; i<a.length; i++)
            r = new Cons(a[i], r);
        return r;
    }
}
