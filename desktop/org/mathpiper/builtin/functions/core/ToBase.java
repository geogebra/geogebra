/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:

package org.mathpiper.builtin.functions.core;

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class ToBase extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        // Get the base to convert to:
        // Evaluate car argument, and store getTopOfStackPointer in oper
        ConsPointer oper = new ConsPointer();
        oper.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        // check that getTopOfStackPointer is a number, and that it is in fact an integer
//        LispError.check(oper.type().equals("Number"), LispError.KLispErrInvalidArg);
        BigNumber num =(BigNumber) oper.getCons().getNumber(aEnvironment.getPrecision(), aEnvironment);
        LispError.checkArgument(aEnvironment, aStackTop, num != null, 1, "ToBase");
        // check that the base is an integer between 2 and 32
        LispError.checkArgument(aEnvironment, aStackTop, num.isInteger(), 1, "ToBase");

        // Get a short platform integer from the car argument
        int base = (int) (num.toLong());

        // Get the number to convert
        BigNumber x = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 2);

        // convert using correct base
        String str;
        str = x.numToString(aEnvironment.getPrecision(), base);
        // Get unique string from hash table, and create an atom from it.

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, aEnvironment.getTokenHash().lookUpStringify(str)));
    }
}


/*
%mathpiper_docs,name="ToBase",categories="User Functions;Numbers (Operations);Built In"
*CMD ToBase --- conversion of a number in decimal base to non-decimal base
*CORE
*CALL
	ToBase(base, number)

*PARMS

{base} -- integer, base to convert to/from

{number} -- integer, number to write out in a different base

{"string"} -- string representing a number in a different base

*DESC

In MathPiper, all numbers are written in decimal notation (base 10).
The two functions {FromBase}, {ToBase} convert numbers between base 10 and a different base.
Numbers in non-decimal notation are represented by strings.


*REM where is this p-adic capability? - sw
These functions use the p-adic expansion capabilities of the built-in
arbitrary precision math libraries.

Non-integer arguments are not supported.

*E.G.


Write the (decimal) number {255} in hexadecimal notation:

In> ToBase(16,255)
Result: "ff";

*SEE PAdicExpand,FromBase
%/mathpiper_docs
 */