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
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class GetExactBits extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigNumber numberToCheck = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);
        BigNumber numberToReturn = new BigNumber(aEnvironment.getPrecision());
        numberToReturn.setTo(
                (numberToCheck.isInteger())
                ? numberToCheck.bitCount() // for integers, return the bit count
                : Utility.digitsToBits((long) (numberToCheck.getPrecision()), 10) // for floats, return the getPrecision
                );
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(numberToReturn));
    }
}



/*
%mathpiper_docs,name="GetExactBitsN",categories="Programmer Functions;Numerical (Arbitrary Precision);Built In"
*CMD GetExactBitsN --- manipulate precision of floating-point numbers
*CORE
*CALL
	GetExactBitsN(x)

*PARMS
{x} -- an expression evaluating to a floating-point number


*DESC
Each floating-point number in MathPiper has an internal precision counter that stores the number of exact bits in the mantissa.
The number of exact bits is automatically updated after each arithmetic operation to reflect the gain or loss of precision due to round-off.
The functions {GetExactBitsN} queries the precision flags of individual number objects.

{GetExactBitsN(x)} returns an integer number $n$ such that {x} represents a real number in the interval [$x*(1-2^(-n))$, $x*(1+2^(-n))$] if $x!=0$ and in the interval [$-2^(-n)$, $2^(-n)$] if $x=0$.
The integer $n$ is always nonnegative unless {x} is zero (a "floating zero").
A floating zero can have a negative value of the number $n$ of exact bits.

This function is only meaningful for floating-point numbers.
(All integers are always exact.)
For integer {x}, the function {GetExactBitsN} returns the bit count of {x}.

*REM FIXME - these examples currently do not work because of bugs

*E.G.
The default precision of 10 decimals corresponds to 33 bits:
In> GetExactBitsN(1000.123)
Result: 33;
In> x:=SetExactBits(10., 20)
Result: 10.;
In> GetExactBitsN(x)
Result: 20;
Prepare a "floating zero" representing an interval [-4, 4]:
In> x:=SetExactBits(0., -2)
Result: 0.;
In> x=0
Result: True;

*SEE BuiltinPrecisionSet, BuiltinPrecisionGet, SetExactBitsN
%/mathpiper_docs
*/