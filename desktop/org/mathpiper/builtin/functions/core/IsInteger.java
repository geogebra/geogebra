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
public class IsInteger extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer result = new ConsPointer();
        result.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

//        LispError.check(result.type().equals("Number"), LispError.KLispErrInvalidArg);
        BigNumber num = (BigNumber) result.getCons().getNumber(aEnvironment.getPrecision(), aEnvironment);
        if (num == null)
        {
            Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
        } else
        {
            Utility.putBooleanInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop), num.isInteger());
        }
    }
}



/*
%mathpiper_docs,name="IsInteger",categories="User Functions;Predicates;Built In"
*CMD IsInteger --- test to see if a number is an integer
*CORE
*CALL
	IsInteger(expr)

*PARMS

{expr} -- expression to test

*DESC

This function tests whether "expr" is an integer number. There are two kinds
of numbers, integers (e.g. 6) and decimals (e.g. -2.75 or 6.0).
*E.G.

In> IsInteger(6);
Result: True;

In> IsInteger(3.25);
Result: False;

In> IsInteger(1/2);
Result: False;

In> IsInteger(3.2/10);
Result: False;

*SEE IsString, IsAtom, IsInteger, IsDecimal, IsPositiveNumber, IsNegativeNumber, IsNumber
%/mathpiper_docs
*/