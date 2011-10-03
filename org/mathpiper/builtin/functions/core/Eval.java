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

/**
 *
 * 
 */
public class Eval extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1));
    }
}//end class


/*
%mathpiper_docs,name="Eval",categories="User Functions;Control Flow;Built In"
*CMD Eval --- force evaluation of expression
*CORE
*CALL
	Eval(expr)

*PARMS

{expr} -- expression to evaluate

*DESC

This function explicitly requests an evaluation of the expression
"expr", and returns the result of this evaluation.

*E.G.

In> a := x;
Result: x;
In> x := 5;
Result: 5;
In> a;
Result: x;
In> Eval(a);
Result: 5;

The variable {a} is bound to {x},
and {x} is bound to 5. Hence evaluating {a} will give {x}. Only when an extra
evaluation of {a} is requested, the value 5 is
returned.

Note that the behavior would be different if we had exchanged the
assignments. If the assignment {a := x} were given
while {x} had the value 5, the variable {a} would also get the value 5 because the assignment
operator {:=} evaluates the right-hand side.

*SEE Hold, HoldArgument, :=
%/mathpiper_docs

 */
