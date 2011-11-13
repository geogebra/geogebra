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

/**
 *
 *  
 */
public class Multiply extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigNumber x = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);
        BigNumber y = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 2);
        BigNumber z = new BigNumber(aEnvironment.getPrecision());
        z.multiply(x, y, aEnvironment.getPrecision());
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(z));
    }
}//end class.



/*
%mathpiper_docs,name="MultiplyN",categories="User Functions;Numeric;Built In"
*CMD MultiplyN --- multiply two numbers (arbitrary-precision math function)
*CORE
*CALL
	MultiplyN(x,y)

*DESC

This command performs the calculation of an elementary mathematical
function.  The arguments <i>must</i> be numbers.  The reason for the
postfix {N} is that the library needs to define equivalent non-numerical
functions for symbolic computations, such as {Exp}, {Sin}, etc.

Note that all xxxN functions accept integers as well as floating-point numbers.
The resulting values may be integers or floats.  If the mathematical result is an
exact integer, then the integer is returned.  For example, {Sqrt(25)} returns
the integer {5}, and {Power(2,3)} returns the integer {8}.  In such cases, the
integer result is returned even if the calculation requires more digits than set by
{BuiltinPrecisionSet}.  However, when the result is mathematically not an integer,
the functions return a floating-point result which is correct only to the current precision.

*E.G.
In>
Result:

%/mathpiper_docs
*/
