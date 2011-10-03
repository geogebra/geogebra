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
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class IsNumber extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer result = new ConsPointer();
        result.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        Utility.putBooleanInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop), result.getCons().getNumber(aEnvironment.getPrecision(), aEnvironment) != null);
    }
}



/*
%mathpiper_docs,name="IsNumber",categories="User Functions;Predicates;Built In"
*CMD IsNumber --- test for a number
*CORE
*CALL
	IsNumber(expr)

*PARMS

{expr} -- expression to test

*DESC

This function tests whether "expr" is a number. There are two kinds
of numbers, integers (e.g. 6) and reals (e.g. -2.75 or 6.0). Note that a
complex number is represented by the {Complex}
function, so {IsNumber} will return {False}.  The value {False} will be returned
for all expressions which are lists, but the user should be especially aware of expression
lists which might appear to be numbers, such as those returned by Hold(-1) (see below).
 

*E.G.
In> IsNumber(6);
Result: True;

In> IsNumber(3.25);
Result: True;

In> IsNumber(I);
Result: False;

In> IsNumber(-1)
Result: True

In> LispForm(-1)
Result: -1
Side Effects:
-1

In> Hold(-1)
Result: -1

In> IsNumber(Hold(-1))
Result: False

In> LispForm(Hold(-1))
Result: -1
Side Effects:
(- 1 )

In> IsNumber("duh");
Result: False;

*SEE IsAtom, IsString, IsInteger, IsDecimal, IsPositiveNumber, IsNegativeNumber, Complex
%/mathpiper_docs
*/