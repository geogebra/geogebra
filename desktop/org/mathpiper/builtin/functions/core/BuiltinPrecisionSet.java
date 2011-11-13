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

import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class BuiltinPrecisionSet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer index = new ConsPointer();
        index.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        LispError.checkArgument(aEnvironment, aStackTop, index.getCons() != null, 1, "BuiltinPrecisionSet");
        LispError.checkArgument(aEnvironment, aStackTop, index.car() instanceof String, 1, "BuiltinPrecisionSet");

        int ind = Integer.parseInt( (String) index.car(), 10);
        LispError.checkArgument(aEnvironment, aStackTop, ind > 0, 1, "BuiltinPrecisionSet");
        aEnvironment.setPrecision(ind);
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="BuiltinPrecisionSet",categories="Programmer Functions;Numerical (Arbitrary Precision);Built In"
*CMD BuiltinPrecisionSet --- set the precision
*CORE
*CALL
	BuiltinPrecisionSet(n)

*PARMS

{n} -- integer, new value of precision

*DESC

This command sets the number of decimal digits to be used in calculations.
All subsequent floating point operations will allow for
at least {n} digits of mantissa.

This is not the number of digits after the decimal point.
For example, {123.456} has 3 digits after the decimal point and 6 digits of mantissa.
The number {123.456} is adequately computed by specifying {BuiltinPrecisionSet(6)}.

The call {BuiltinPrecisionSet(n)} will not guarantee that all results are precise to {n} digits.

When the precision is changed, all variables containing previously calculated values
remain unchanged.
The {BuiltinPrecisionSet} function only makes all further calculations proceed with a different precision.

Also, when typing floating-point numbers, the current value of {BuiltinPrecisionSet} is used to implicitly determine the number of precise digits in the number.

*E.G.

In> BuiltinPrecisionSet(10)
Result: True;
In> N(Sin(1))
Result: 0.8414709848;
In> BuiltinPrecisionSet(20)
Result: True;
In> x:=N(Sin(1))
Result: 0.84147098480789650665;

The value {x} is not changed by a {BuiltinPrecisionSet()} call:

In> [ BuiltinPrecisionSet(10); x; ]
Result: 0.84147098480789650665;

The value {x} is rounded off to 10 digits after an arithmetic operation:

In> x+0.
Result: 0.8414709848;

In the above operation, {0.} was interpreted as a number which is precise to 10 digits (the user does not need to type {0.0000000000} for this to happen).
So the result of {x+0.} is precise only to 10 digits.

*SEE BuiltinPrecisionGet, N

%/mathpiper_docs
*/

