/**************************************************************************
 * Copyright (C) 2011 Ted Kosan                                           *
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

package org.mathpiper.mpreduce;

import java.math.BigInteger;

import org.mathpiper.mpreduce.functions.builtin.Fns1;
import org.mathpiper.mpreduce.functions.builtin.Fns2;
import org.mathpiper.mpreduce.functions.builtin.Fns3;
import org.mathpiper.mpreduce.functions.builtin.MPReduceFunctions;
import org.mathpiper.mpreduce.special.Specfn;
import org.mathpiper.mpreduce.symbols.Symbol;


public class Environment {
    public static Symbol nil;
    public static Symbol lispTrue;
    public static LispObject[] lit = new LispObject[Lit.names.length];
    public static BigInteger bigModulus = BigInteger.ONE;
    public static int modulus = 1;
    public static int printprec = 15;
    public static boolean descendSymbols;
    public static boolean specialNil;

    static Fns1 fns1 = new Fns1();
    static Fns2 fns2 = new Fns2();
    static Fns3 fns3 = new Fns3();
    static MPReduceFunctions mpreduceFunctions = new MPReduceFunctions();
    static Specfn specfn = new Specfn();



}