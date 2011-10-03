package org.mathpiper.mpreduce.functions.builtin;

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


// The implementations here are intended to avoid total stupidity about
// overflow and accuracy, but are not striving for the very best 
// least-significant-bit results.

public class MyMath
{

public static double acosh(double a)
{
    double a1 = a - 1.0;
    if (a1 == 0.0) return 1.0;
    else if (a1 < 0.0) return (0.0/0.0); // a NaN
// The series shown here was developed as a Chebychev approximation
// to the function acosh(x)/sqrt(x-1) near x=1. The cut-off at x-1=0.1
// is somewhat arbitrary, but at that point the simple formula used for
// large x only (?) loses a few bits when computing a*a-1 and the argument
// for the logarithm is 1.55 (far enough from 1.0 that I think that is
// not a danger).
    else if (a1 < 0.1)
    {   double w = 
           (((((((-0.00013247403176210292*a1+
           0.00038027261161096525)*a1-
           0.00098848023694688735)*a1+
           0.0026854001707801832)*a1-
           0.0078918165513171978)*a1+
           0.026516504292612476)*a1-
           0.1178511301977528)*a1+
           1.4142135623730950);
        return Math.sqrt(a1)*w;
    }
    else return Math.log(a + Math.sqrt(a*a - 1.0));
}

public static double acoth(double a)
{
    return atanh(1.0/a);
}

public static double acsch(double a)
{
    return asinh(1.0/a);
}

public static double asech(double a)
{
    return acosh(1.0/a);
}

public static double asinh(double a)
{
    if (a >= 0.0) return Math.log(a + Math.sqrt(1.0 + a*a));
    else return -Math.log(-a + Math.sqrt(1.0 + a*a));
}

public static double atanh(double a)
{
    return 0.5*Math.log((1.0+a)/(1.0-a)); 
}

public static double cosh(double a)
{
    if (a < 0.0) a = -a;
// no cancellation worries for small argument
    if (a < 20.0)
    {   double ea = Math.exp(a);
        return (ea + 1.0/ea)/2.0;
    }
// if exp(-a) is tiny compared with exp(a) I can ignore it
    else if (a < 700) return Math.exp(a)/2.0;
// avoid premature overflow in extreme cases
    else return Math.exp(a - Math.log(2.0));
}

public static double coth(double a)
{
    return 1.0/tanh(a);
}

public static double csch(double a)
{
    return 1.0/sinh(a);
}

public static double sech(double a)
{
    return 1.0/cosh(a);
}

public static double sinh(double a)
{
// for small arguments I use the series expansion to avoid cancellation
    double aa = Math.abs(a);
    if (aa < 0.35)
    {   double a2 = a*a;
        double r = (((((a2/110.0 + 1.0)*a2/72.0 + 1.0)*a2/42.0 +
	           1.0)*a2/20.0 + 1.0)*a2/6.0 + 1.0)*a;
	return r;  
    }
// for medium arguments the full formula can be used
    else if (aa < 20.0) return (Math.exp(a) - Math.exp(-a))/2.0;
// for |a| > 20 I can use a simplified version
    else if (aa < 700.0) aa = Math.exp(aa)/2.0;
// fially for very large args I must avoid premature overflow
    else aa = Math.exp(aa - Math.log(2.0));
    if (a < 0.0) return -aa;
    else return aa;
}

public static double tanh(double a)
{
    double aa = Math.abs(a);
// for small argument I will first range reduce and then use a simple
// powert series.
    if (aa < 0.40)
    {   int n = 0;
        while (aa >= 0.05)
	{   aa = aa/2.0;
	    n++;
	}
	double a2 = aa*aa;
	double r = ((((62.0/2835.0*a2 - 17.0/315)*a2 + 2.0/15.0)*a2 -
	               1.0/3.0)*a2 + 1.0)*aa;
	while (n != 0)
	{   r = 2.0*r/(1 + r*r);
	    n--;
	}
	if (a < 0) return -r;
        else return r;
    }
// for large enough argument the value will be +1 or -1 to within accuracy
// limits
    else if (aa >= 20.0)
    {   if (a < 0.0) return -1.0;
        else return 1.0;
    }
// for intermediate ranges I can use the normal formula, secure that there
// will be no overflow, underflow or serious cancellation
    double ea = Math.exp(a);
    double ema = 1.0/ea;
    return (ea-ema)/(ea+ema);
}

}

// end of MyMath.java

