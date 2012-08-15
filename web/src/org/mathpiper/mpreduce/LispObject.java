package org.mathpiper.mpreduce;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2011.
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


// Lisp has a single inclusive data-type, which I call
// LispObject here. It has sub-types that are symbols,
// numbers, strings and lists. Here I give just a few
// methods (eg print and eval) that may be used on anything.


import java.math.BigInteger;

import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.io.streams.LispOutputString;
import org.mathpiper.mpreduce.io.streams.LispStream;
import org.mathpiper.mpreduce.numbers.LispBigInteger;
import org.mathpiper.mpreduce.numbers.LispSmallInteger;

public abstract class LispObject extends Object
{
    public boolean atom;   // true if it is atomic
    public LispObject car; // car and cdr fields to reduce number of expensive casts!
    public LispObject cdr;

    public LispObject()
    {
        car = cdr = null;
        atom = true;
    }

// The following constructor is ONLY intended for use via a call
//       super(car, cdr);
// in the constructor for the "Cons" sub-class.

    public LispObject(LispObject car, LispObject cdr)
    {
        atom = false;
        this.car = car; 
        this.cdr = cdr;
    }

    public static final int printEscape      = 1; // flags to pass to print(n)
    public static final int printBinary      = 2;
    // (decimal is the default)       = 4
    public static final int printOctal       = 8;
    public static final int printHex         = 16;
    public static final int printLower       = 32;
    public static final int printUpper       = 64;
    public static final int noLineBreak      = 128;

    public void print() throws ResourceException
    {
        currentOutput = (LispStream)Jlisp.lit[Lit.std_output].car/*value*/;
        currentFlags = 0;
        iprint();
    }

    public void print(int flags) throws ResourceException
    {
        currentOutput = (LispStream)Jlisp.lit[Lit.std_output].car/*value*/;
        currentFlags = flags;
        iprint();
    }

// real printing will usually be done by iprint where the current output
// stream and format flags can be accessed via static variables.

    public static LispStream currentOutput;
    public static int currentFlags;

    abstract public void iprint() throws ResourceException;
    abstract public void blankprint() throws ResourceException; // print but with whitespace before it

    public void errPrint() throws ResourceException // print to error output stream
    {
        currentOutput = (LispStream)Jlisp.lit[Lit.err_output].car/*value*/;
        currentFlags = printEscape;
        iprint();
    }

    public void tracePrint() throws ResourceException // print to trace output stream
    {
        currentOutput = (LispStream)Jlisp.lit[Lit.tr_output].car/*value*/;
        currentFlags = printEscape;
        iprint();
    }

// Codes for use in my (custom) serialisation format.

// I make special provision for references to 64 things. I will use
// this for the first 48 things used at all and the 16 most recent ones.

    public static final int X_REFn     = 0x00;

    public static final int X_BREAK1   = 0x40;

// The next bunch are optimisations for common cases when the
// length code is short. The length code is folded into the main byte. Thus
// (eg) symbols whose name is from 0 to 15 characters long are dealt with
// especially neatly.

    public static final int X_SYMn     = 0x40; // symbol with 0 to 15 chars
    public static final int X_UNDEFn   = 0x50; // symbol (0-15), not a function
    public static final int X_GENSYMn  = 0x60; // gensym with 0 to 15 bytes
    public static final int X_LIST     = 0x70; // list with 0 to 15 items: (LIST) = NIL
    public static final int X_LISTX    = 0x80; // like (LIST* ..) with 1-16 items then tail
    public static final int X_INTn     = 0x90; // integer with 0 to 15 bytes
    public static final int X_STRn     = 0xa0; // string, 0 to 15 chars

    public static final int X_BREAK2   = 0xb0;

    public static final int X_REF      = 0xb0; // refer to a previously mentioned item
    public static final int X_REFBACK  = 0xb4; // (only 1 and 2 byte versions used)
    public static final int X_INT      = 0xb8; // LispBigInteger represented by an array
    public static final int X_STR      = 0xbc; // Strings
    public static final int X_SYM      = 0xc0; // Symbol with given name
    public static final int X_UNDEF    = 0xc4; // Symbol (not a function)
    public static final int X_UNDEF1   = 0xc8; // disembodied undefined function
    public static final int X_GENSYM   = 0xcc; // a gensym or other uninterned name
    public static final int X_BPS      = 0xd0; // "binary code" ha ha ha.
    public static final int X_VEC      = 0xd4; // a Lisp vector
// perhaps X_INT with a short-enough operand could be used for X_FIXNUM
// as a rationalisation here.
    public static final int X_FIXNUM   = 0xd8; // 1, 2, 3 or 4-byte small integer

//  0xdc spare at present

    public static final int X_BREAK3   = 0xe0;

// The final collection of codes are all one-byte incidental ones and
// the amount of any associated data is implicit in them. Eg X_DOUBLE will
// be followed by 8 bytes that represent a double-precision floating point
// value. X_FNAME is followed by a single length byte (n) then n characters.

    public static final int X_NULL     = 0xe0; // empty cell (ie Java null)
    public static final int X_DOUBLE   = 0xe1; // double-precision number
    public static final int X_STREAM   = 0xe2; // an open file (not dumpable)
    public static final int X_FNAME    = 0xe3; // built-in function
    public static final int X_SPECFN   = 0xe4; // built-in special form
    public static final int X_STORE    = 0xe5; // the next item will be re-used
    public static final int X_HASH     = 0xe6; // EQ hash
    public static final int X_HASH1    = 0xe7; // EQL hash (not used)
    public static final int X_HASH2    = 0xe8; // EQUAL hash
    public static final int X_HASH3    = 0xe9; // EQUALS hash (not used)
    public static final int X_HASH4    = 0xea; // EQUALP hash (not used)
    public static final int X_ENDHASH  = 0xeb; // end of data for hash table
    public static final int X_AUTOLOAD = 0xec; // autoloading fn def
    public static final int X_SPID     = 0xed; // internal marker
    public static final int X_DEFINMOD = 0xee; // "define-in-module" in fasl files
    public static final int X_INTERP   = 0xef; // interpreted code
    public static final int X_MACRO    = 0xf0; // interpreted macro
    public static final int X_CALLAS   = 0xf1; // simple tail-call object
    public static final int X_RECENT   = 0xf2; // used in FASL but not checkpoints
    public static final int X_RECENT1  = 0xf3; // used in FASL but not checkpoints
    public static final int X_OBLIST   = 0xf4; // oblist vector

// 0xf2 to 0xff spare at present...

    abstract public void scan();






    public boolean lispequals(Object a)
    {
        return this.equals(a);
    }

    public LispObject eval() throws Exception
    {
        return this;
    }

    public LispObject copy()
    {
        return this;
    }

    public int lisphashCode()
    {
        return this.hashCode();
    }

    public double doubleValue() throws Exception
    {
        Jlisp.error("Number needed", this);
        return 0.0;  // never reached!
    }

    public int intValue() throws Exception
    {
        Jlisp.error("Number needed", this);
        return 0;    // never reached!
    }

    public BigInteger bigIntValue() throws Exception
    {
        Jlisp.error("Number needed", this);
        return null;    // never reached!
    }

    public LispObject negate() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject ash(int n) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject ash1(int n) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject rightshift(int n) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject add1() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject sub1() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject floor() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject ceiling() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject round() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject truncate() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject evenp() throws Exception
    {
        return Environment.nil;
    }

    public LispObject oddp() throws Exception
    {
        return Environment.nil;
    }

    public LispObject fix() throws Exception
    {
        return Jlisp.error("Number needed", this);
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
        return Jlisp.error("Number needed", this);
    }

    public LispObject floatp() throws Exception
    {
        return Environment.nil;
    }

    public LispObject minusp() throws Exception
    {
        return Environment.nil;
    }

    public LispObject plusp() throws Exception
    {
        return Environment.nil;
    }

    public LispObject zerop() throws Exception
    {
        return Environment.nil;
    }

    public LispObject onep() throws Exception
    {
        return Environment.nil;
    }

    public LispObject abs() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject msd() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject lsd() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject not() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modMinus() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modRecip() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject safeModRecip() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject reduceMod() throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject add(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject subtract(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject multiply(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject divide(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject remainder(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject quotientAndRemainder(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject mod(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject expt(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject max(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject min(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject and(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject or(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject xor(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject gcd(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject lcm(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modAdd(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modSubtract(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modMultiply(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modDivide(LispObject a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modExpt(int n) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public boolean eqn(LispObject a) throws Exception
    {
        return (this == a);
    }

    public boolean neqn(LispObject a) throws Exception
    {
        return (this != a);
    }

    public boolean ge(LispObject a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean geq(LispObject a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean le(LispObject a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean leq(LispObject a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }


    public LispObject addInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject subtractInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject multiplyInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject divideInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject remainderInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject quotientAndRemainderInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject exptInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject maxInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject minInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject andInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject orInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject xorInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject gcdInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject lcmInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modAddInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modSubtractInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modMultiplyInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modDivideInteger(LispBigInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public boolean eqnInteger(LispBigInteger a) throws Exception
    {
        return false;
    }

    public boolean neqnInteger(LispBigInteger a) throws Exception
    {
        return true;
    }

    public boolean geInteger(LispBigInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean geqInteger(LispBigInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean leInteger(LispBigInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean leqInteger(LispBigInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public LispObject addSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject subtractSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject multiplySmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject divideSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject remainderSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject quotientAndRemainderSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject exptSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject maxSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject minSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject andSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject orSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject xorSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject gcdSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject lcmSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modAddSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modSubtractSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modMultiplySmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public LispObject modDivideSmallInteger(LispSmallInteger a) throws Exception
    {
        return Jlisp.error("Number needed", this);
    }

    public boolean eqnSmallInteger(LispSmallInteger a) throws Exception
    {
        return false;
    }

    public boolean neqnSmallInteger(LispSmallInteger a) throws Exception
    {
        return true;
    }

    public boolean geSmallInteger(LispSmallInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean geqSmallInteger(LispSmallInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean leSmallInteger(LispSmallInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }

    public boolean leqSmallInteger(LispSmallInteger a) throws Exception
    {
        Jlisp.error("Number needed", this);
        return false;
    }


    public String toString()
    {
         LispStream originalOutput = this.currentOutput;

         LispStream stringStream = new LispOutputString();

         this.currentOutput = stringStream;

         try
         {
         //Print object information into a string.
         iprint();
        }
         catch(ResourceException e)
         {
             
         }

         this.currentOutput = originalOutput;

         return stringStream.toString();

    }//end method.

}

// End of LispObject.java

