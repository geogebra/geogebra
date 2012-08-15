package org.mathpiper.mpreduce.numbers;

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



import java.math.BigDecimal;

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.exceptions.ResourceException;

public class LispFloat extends LispNumber
{
    public double value;

    public LispFloat(int value)
    {
        this.value = (double)value;
    }

    public LispFloat(String value)
    {
        Double d = Double.valueOf(value);
        this.value = d.doubleValue();
    }

    public LispFloat(double value)
    {
        this.value = value;
    }

    public LispObject eval()
    {
        return this;
    }

    public void iprint() throws ResourceException
    {
        String s = trimTo(Jlisp.printprec);
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() > currentOutput.lineLength)
            currentOutput.println();
        currentOutput.print(s);
    }
    
    public void blankprint() throws ResourceException
    {
        String s = trimTo(Jlisp.printprec);
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        currentOutput.print(s);
    }


// The next method is something that I am reasonably upset about since
// I believe that Java ought to provide this sort of formatting but at
// present I can not see how and where it does it.
    String trimTo(int n) // trim to n significant figures
    {
        if (n < 1) n = 1;
        else if (n > 16) n = 16; // limit precision to sensible range
        String s = Double.toString(value);
//Jlisp.println("original = " + s);
        int len = s.length();
        char [] s1 = s.toCharArray();
// identify and remove any "-" sign
        boolean neg = false;
        if (s1[0] == '-')
        {   neg = true;
            for (int i=0; i<len-1; i++) s1[i] = s1[i+1];
            len--;
        }
//Jlisp.println("made +ve = " + new String(s1, 0, len));
// Extract an exponent if explicitly present
        int e = -1;
        for (int i=0; i<len; i++)
        {   if (s1[i] == 'E')
            {   e = i;
                break;
            }
        }
        int x = 0;
        if (e != -1)
        {   String exponent = new String(s1, e+1, len-e-1);
            x = Integer.parseInt(exponent);
            len = e;
        }
//Jlisp.println("exponent grabbed = " + new String(s1, 0, len) + " x= " + x);
// Locate the decimal point, remove it, adjust exponent
        e = -1;
        for (int i=0; i<len; i++)
        {   if (s1[i] == '.')
            {   e = i;
                break;
            }
        }
        if (e != -1)
        {   x -= (len - e - 1);
            for (int i=e; i<len-1; i++) s1[i] = s1[i+1];
            len--;
        }
//Jlisp.println("dot grabbed = " + new String(s1, 0, len) + " x= " + x);
// Remove leading zeros (eg if original had been formated 0.0012345
        while (len>0 && s1[0] == '0')
        {   for (int i=0; i<len-1; i++) s1[i] = s1[i+1];
            len--;
        }
//Jlisp.println("no leading zero = " + new String(s1, 0, len) + " x= " + x);
// Now the string s1 is digits of the number and x (an integer) is
// an exponent (based on s being viewed as an integer). len is the
// length of s1.  neg is the sign. Eg an approximation to +pi might have
// ended up as
//   s1 = "314158"
//   len = 6
//   neg = false
//   x = -5
// My next job is to round to n places... If I do not have that many
// present I do not have anything to do!
        if (n < len)
        {   char next = s1[n];       // thing to test against '5'
            x = x + len - n;
            len = n;
// Some people would round by detecting the case that the digits to be
// disarded were exactly 50000.. and in that mid-way case they might
// round to yield an even or odd result. At present I do the simple
// thing and round up on any fraction >= 0.5.
//Jlisp.println("truncated = " + new String(s1, 0, len) + " x= " + x);
            if (next >= '5')         // need to round up?
            {   char w = 'x';
                for (int k=len-1; k >= 0; k--)
                {   w = s1[k];
                    w = (char)(w == '9' ? '0' : w + 1);
                    s1[k] = w;
                    if (w != '0') break;
                }
// If the number being rounded (say to 5 places) had been 999997 then
// rounding converts it to (1)00000. The "1" would in natural cases go
// into s1[-1], but in this case I KNOW that all remaining digits in s1
// are "0" so I can safely put it in s1[0]! I then have to adjust the
// exponent to reflect the shift.
                if (w == '0')
                {   s1[0] = '1';
                    x++;
                }
            }
        }
//Jlisp.println("rounded = " + new String(s1, 0, len) + " x= " + x);
// Now I guess I can trim any trailing zeros
        while (len>0 && s1[len-1] == '0')
        {   len--;
            x++;
        }
//Jlisp.println("no trailing 0 = " + new String(s1, 0, len) + " x= " + x);
// Finally I can try to decide on a format to use (F or E style)
// and reconstruct the result as a string
        if (len == 0) return "0.0";  // easy special case!
        StringBuffer r = new StringBuffer();
        if (neg) r.append("-");
        if (x>n || x<(-len-2)) // use "E" style
        {   r.append(s1[0]);
            r.append(".");
            if (len == 1) r.append("0");
            else r.append(s1, 1, len-1);
            r.append("e");
            r.append(x+len-1);
        }
        else // use "F" style
        {   int left = len;
            if (len+x <= 0) r.append("0");
            else 
            {   if (x > 0)
                {   r.append(s1, 0, len);
                    while (x > 0)
                    {   r.append("0");
                        x--;
                    }
                    left = 0;
                }
                else
                {   r.append(s1, 0, len+x);
                    left -= (len+x);
                }
            }
            r.append(".");
            if (left == 0) r.append("0");
            else 
            {   while (len+x < 0)
                {   r.append("0");
                    len++;
                }
                r.append(s1, len+x, left);
            }
        }
//Jlisp.println("result = " + r.toString());
        return r.toString();
    }

    public double doubleValue()
    {
        return value;
    }

    public boolean lispequals(Object b)
    {
        if (!(b instanceof LispFloat)) return false;
        return value == ((LispFloat)b).value;
    }
    
    public boolean equals(Object b)
    {   if (!(b instanceof LispFloat)) return false;
        return value == ((LispFloat)b).value;
    }

    public int lisphashCode()
    {
        return (new Double(value)).hashCode();
    }
    
    public int hashCode()
    {
        return (new Double(value)).hashCode();
    }
    
    public void scan()
    {
        Object w = new Double(value);
        if (LispReader.objects.contains(w)) // seen before?
	{   if (!LispReader.repeatedObjects.containsKey(w))
	    {   LispReader.repeatedObjects.put(
	            w,
	            Environment.nil); // value is junk at this stage
	    }
	}
	else LispReader.objects.add(w);
    }
    

    public LispObject negate() throws Exception
    {
        return new LispFloat(-value);
    }

    public LispObject abs() throws Exception
    {
        if (value >= 0) return this;
        else return new LispFloat(-value);
    }

    public LispObject add(LispObject a) throws Exception
    {
        return new LispFloat(value + a.doubleValue());
    }

    public LispObject subtract(LispObject a) throws Exception
    {
        return new LispFloat(value - a.doubleValue());
    }

    public LispObject multiply(LispObject a) throws Exception
    {
        return new LispFloat(value * a.doubleValue());
    }

    public LispObject divide(LispObject a) throws Exception
    {
        return new LispFloat(value / a.doubleValue());
    }

    public LispObject remainder(LispObject a) throws Exception
    {
        return new LispFloat(value % a.doubleValue());
    }

    public LispObject expt(LispObject a) throws Exception
    {
// it is possible that I should delect cases where a is an integer
// and raise to a power using some alternative scheme, like repeated
// multiplication.
        return new LispFloat(Math.pow(value, a.doubleValue()));
    }

    public LispObject max(LispObject a) throws Exception
    {
        return (value >= a.doubleValue() ? this : a);
    }

    public LispObject min(LispObject a) throws Exception
    {
        return (value <= a.doubleValue() ? this : a);
    }

    public boolean eqn(LispObject a) throws Exception
    {
        return (value == a.doubleValue());
    }

    public boolean neqn(LispObject a) throws Exception
    {
        return (value != a.doubleValue());
    }

    public boolean ge(LispObject a) throws Exception
    {
        return (value > a.doubleValue());
    }

    public boolean geq(LispObject a) throws Exception
    {
        return (value >= a.doubleValue());
    }

    public boolean le(LispObject a) throws Exception
    {
        return (value < a.doubleValue());
    }

    public boolean leq(LispObject a) throws Exception
    {
        return (value <= a.doubleValue());
    }

    public LispObject add1() throws Exception
    {
        return new LispFloat(value + 1.0);
    }

    public LispObject sub1() throws Exception
    {
        return new LispFloat(value - 1.0);
    }

    public LispObject floor() throws Exception
    {
        BigDecimal w =
            new BigDecimal(value).setScale(0, BigDecimal.ROUND_FLOOR);
        return LispInteger.valueOf(w.toBigInteger());
    }

    public LispObject ceiling() throws Exception
    {
        BigDecimal w =
            new BigDecimal(value).setScale(0, BigDecimal.ROUND_CEILING);
        return LispInteger.valueOf(w.toBigInteger());
    }

    public LispObject round() throws Exception
    {
        BigDecimal w =
            new BigDecimal(value).setScale(0, BigDecimal.ROUND_HALF_EVEN);
        return LispInteger.valueOf(w.toBigInteger());
    }

    public LispObject truncate() throws Exception
    {
        BigDecimal w =
            new BigDecimal(value).setScale(0, BigDecimal.ROUND_DOWN);
        return LispInteger.valueOf(w.toBigInteger());
    }

    public LispObject fix() throws Exception
    {
        BigDecimal w =
            new BigDecimal(value).setScale(0, BigDecimal.ROUND_DOWN);
        return LispInteger.valueOf(w.toBigInteger());
    }

    public LispObject fixp() throws Exception
    {
        return Environment.nil;
    }

    public LispObject integerp() throws Exception
    {
        return Environment.nil;
    }

    public LispObject jfloat() throws Exception
    {
        return this;
    }

    public LispObject floatp() throws Exception
    {
        return Jlisp.lispTrue;
    }

    public LispObject minusp() throws Exception
    {
        return (value < 0.0) ? Jlisp.lispTrue : Environment.nil;
    }

    public LispObject plusp() throws Exception
    {
        return (value >= 0.0) ? Jlisp.lispTrue : Environment.nil;
    }

    public LispObject zerop() throws Exception
    {
        return (value == 0.0) ? Jlisp.lispTrue : Environment.nil;
    }

    public LispObject onep() throws Exception
    {
        return (value == 1.0) ? Jlisp.lispTrue : Environment.nil;
    }

    public LispObject addInteger(LispBigInteger a) throws Exception
    {
        return new LispFloat(a.value.doubleValue() + value);
    }

    public LispObject subtractInteger(LispBigInteger a) throws Exception
    {
        return new LispFloat(a.value.doubleValue() - value);
    }

    public LispObject multiplyInteger(LispBigInteger a) throws Exception
    {
        return new LispFloat(a.value.doubleValue() * value);
    }

    public LispObject divideInteger(LispBigInteger a) throws Exception
    {
        return new LispFloat(a.value.doubleValue() / value);
    }

    public LispObject remainderInteger(LispBigInteger a) throws Exception
    {
        return new LispFloat(a.value.doubleValue() % value);
    }

    public LispObject maxInteger(LispBigInteger a) throws Exception
    {
        if (a.value.doubleValue() >= value) return a;
        else return this;
    }

    public LispObject exptInteger(LispBigInteger a) throws Exception
    {
        return new LispFloat(Math.pow(a.doubleValue(), value));
    }

    public LispObject minInteger(LispBigInteger a) throws Exception
    {
        if (a.value.doubleValue() <= value) return a;
        else return this;
    }

    public boolean eqnInteger(LispBigInteger a) throws Exception
    {
        return (a.value.doubleValue() == value);
    }

    public boolean neqnInteger(LispBigInteger a) throws Exception
    {
        return (a.value.doubleValue() != value);
    }

    public boolean geInteger(LispBigInteger a) throws Exception
    {
        return (a.value.doubleValue() > value);
    }

    public boolean geqInteger(LispBigInteger a) throws Exception
    {
        return (a.value.doubleValue() >= value);
    }

    public boolean leInteger(LispBigInteger a) throws Exception
    {
        return (a.value.doubleValue() < value);
    }

    public boolean leqInteger(LispBigInteger a) throws Exception
    {
        return (a.value.doubleValue() <= value);
    }

    public LispObject addSmallInteger(LispSmallInteger a) throws Exception
    {
        return new LispFloat((double)a.value + value);
    }

    public LispObject subtractSmallInteger(LispSmallInteger a) throws Exception
    {
        return new LispFloat((double)a.value - value);
    }

    public LispObject multiplySmallInteger(LispSmallInteger a) throws Exception
    {
        return new LispFloat((double)a.value * value);
    }

    public LispObject divideSmallInteger(LispSmallInteger a) throws Exception
    {
        return new LispFloat((double)a.value / value);
    }

    public LispObject remainderSmallInteger(LispSmallInteger a) throws Exception
    {
        return new LispFloat((double)a.value % value);
    }

    public LispObject maxSmallInteger(LispSmallInteger a) throws Exception
    {
        if ((double)a.value >= value) return a;
        else return this;
    }

    public LispObject exptSmallInteger(LispSmallInteger a) throws Exception
    {
        return new LispFloat(Math.pow(a.doubleValue(), value));
    }

    public LispObject minSmallInteger(LispSmallInteger a) throws Exception
    {
        if ((double)a.value <= value) return a;
        else return this;
    }

    public boolean eqnSmallInteger(LispSmallInteger a) throws Exception
    {
        return ((double)a.value == value);
    }

    public boolean neqnSmallInteger(LispSmallInteger a) throws Exception
    {
        return ((double)a.value != value);
    }

    public boolean geSmallInteger(LispSmallInteger a) throws Exception
    {
        return ((double)a.value > value);
    }

    public boolean geqSmallInteger(LispSmallInteger a) throws Exception
    {
        return ((double)a.value >= value);
    }

    public boolean leSmallInteger(LispSmallInteger a) throws Exception
    {
        return ((double)a.value < value);
    }

    public boolean leqSmallInteger(LispSmallInteger a) throws Exception
    {
        return ((double)a.value <= value);
    }

}

// end of LispFloat.java
