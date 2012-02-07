package org.mathpiper.mpreduce.numbers;

//

import java.math.BigInteger;

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




public abstract class LispInteger extends LispNumber
{

    public static LispInteger valueOf(int value)
    {
        if (value <= LispSmallInteger.MAX &&
            value >= LispSmallInteger.MIN)
            return LispSmallInteger.preAllocated[value - LispSmallInteger.MIN];
        else if (value <= 0x3fffffff &&
            value >= -0x40000000) return new LispSmallInteger(value);
        else return new LispBigInteger(BigInteger.valueOf((long)value));
    }

    public static LispInteger valueOf(long value)
    {
        if (value <= LispSmallInteger.MAX &&
            value >= LispSmallInteger.MIN)
            return LispSmallInteger.preAllocated[
                       (int)(value - LispSmallInteger.MIN)];
        else if (value <= 0x3fffffffL &&
            value >= -0x40000000L) return new LispSmallInteger((int)value);
        else return new LispBigInteger(BigInteger.valueOf(value));
    }

    public static LispInteger valueOf(BigInteger value)
    {
        if (value.bitLength() <= 31)
        {   int n = value.intValue();
            if (n <= LispSmallInteger.MAX &&
                n >= LispSmallInteger.MIN)
                return LispSmallInteger.preAllocated[n - LispSmallInteger.MIN];
            else if (n <= 0x3fffffff &&
                     n >= -0x40000000) return new LispSmallInteger(n);
        }
        return new LispBigInteger(value);
    }


}

// End of LispInteger.java


