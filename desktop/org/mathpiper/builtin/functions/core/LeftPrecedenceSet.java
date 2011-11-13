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
public class LeftPrecedenceSet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        // Get operator
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "LeftPrecedenceSet");
        String orig =  (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "LeftPrecedenceSet");

        ConsPointer index = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, index, getArgumentPointer(aEnvironment, aStackTop, 2));
        LispError.checkArgument(aEnvironment, aStackTop, index.getCons() != null, 2, "LeftPrecedenceSet");
        LispError.checkArgument(aEnvironment, aStackTop, index.car() instanceof String, 2, "LeftPrecedenceSet");
        int ind = Integer.parseInt( (String) index.car(), 10);

        aEnvironment.iInfixOperators.setLeftPrecedence(aStackTop, Utility.getSymbolName(aEnvironment, orig), ind);
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="LeftPrecedenceSet",categories="User Functions;Built In"
*CMD LeftPrecedenceSet --- set operator precedence
*CORE
*CALL
	LeftPrecedenceSet("op",precedence)

*PARMS

{"op"} -- string, the name of a function

{precedence} -- nonnegative integer

*DESC

{"op"} should be an infix operator. This function call tells the
infix expression printer to bracket the left  hand side of
the expression if its precedence is larger than precedence.

This functionality was required in order to display expressions like {a-(b-c)}
correctly. Thus, {a+b+c} is the same as {a+(b+c)}, but {a-(b-c)} is not
the same as {a-b-c}.

Note that the left precedence of an infix operator does not affect the way MathPiper interprets expressions typed by the user. You cannot make MathPiper parse {a-b-c} as {a-(b-c)} unless you declare the operator "{-}" to be right-associative.

*SEE PrecedenceGet, LeftPrecedenceGet, RightPrecedenceGet, RightAssociativeSet, RightPrecedenceSet
%/mathpiper_docs
*/