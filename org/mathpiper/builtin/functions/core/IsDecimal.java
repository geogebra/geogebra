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
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *
 */
public class IsDecimal extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer result = new ConsPointer();
        result.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        Object cons = result.getCons().getNumber(aEnvironment.getPrecision(), aEnvironment);

        BigNumber bigNumber;
        if(cons instanceof BigNumber)
        {
            bigNumber = (BigNumber) cons;

            Utility.putBooleanInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop),  bigNumber.isDecimal());
        }
        else
        {
            Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
        }


    }
}



/*
%mathpiper_docs,name="IsDecimal",categories="User Functions;Predicates;Built In"
*CMD IsDecimal --- test to see if a number is a decimal
*CORE
*CALL
	IsDecimal(expr)

*PARMS

{expr} -- expression to test

*DESC

This function tests whether "expr" is a decimal number. There are two kinds
of numbers, integers (e.g. 6) and decimals (e.g. -2.75 or 6.0).
*E.G.

In> IsDecimal(3.25);
Result: True;

In> IsDecimal(6);
Result: False;

In> IsDecimal(1/2);
Result: False;

In> IsDecimal(3.2/10);
Result: False;

*SEE IsString, IsAsom, IsInteger, IsPositiveNumber, IsNegativeNumber, IsNumber
%/mathpiper_docs
*/