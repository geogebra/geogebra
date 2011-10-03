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
public class If extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        int nrArguments = Utility.listLength(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 0));
        LispError.check(aEnvironment, aStackTop, nrArguments == 3 || nrArguments == 4, LispError.WRONG_NUMBER_OF_ARGUMENTS);

        ConsPointer predicate = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, predicate, getArgumentPointer(aEnvironment, aStackTop, 1));

        if (Utility.isTrue(aEnvironment, predicate, aStackTop))
        {
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 0), 2));
        } else
        {
            LispError.checkArgument(aEnvironment, aStackTop, Utility.isFalse(aEnvironment, predicate, aStackTop), 1, "If");
            if (nrArguments == 4)
            {
                aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 0), 3));
            } else
            {
                Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
            }
        }
    }
}


/*
%mathpiper_docs,name="If",categories="User Functions;Control Flow;Built In"
*CMD If --- branch point
*CORE
*CALL
	If(pred, then)
	If(pred, then, else)

*PARMS

{pred} -- predicate to test

{then} -- expression to evaluate if "pred" is {True}

{else} -- expression to evaluate if "pred" is {False}

*DESC

This command implements a branch point. The predicate "pred" is
evaluated, which should result in either {True} or {False}. In the first case, the expression "then" is
evaluated and returned. If the predicate yields {False}, the expression "else" (if present) is evaluated and
returned. If there is no "else" branch (i.e. if the first calling
sequence is used), the {If} expression returns {False}.

*E.G.

The sign function is defined to be 1 if its argument is positive and
-1 if its argument is negative. A possible implementation is
In> mysign(x) := If (IsPositiveReal(x), 1, -1);
Result: True;
In> mysign(Pi);
Result: 1;
In> mysign(-2.5);
Result: -1;
Note that this will give incorrect results, if "x" cannot be
numerically approximated.
In> mysign(a);
Result: -1;
Hence a better implementation would be
In> mysign(_x)_IsNumber(N(x)) <-- If \
	  (IsPositiveReal(x), 1, -1);
Result: True;
%/mathpiper_docs
*/
