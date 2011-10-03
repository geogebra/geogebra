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
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class Postfix extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        int nrArguments = Utility.listLength(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 0));
        if (nrArguments == 2)
        {
            Utility.singleFix(0, aEnvironment, aStackTop, aEnvironment.iPostfixOperators);
        } else
        {
            Utility.multiFix(aEnvironment, aStackTop, aEnvironment.iPostfixOperators);
        }
    }
}



/*
%mathpiper_docs,name="Postfix",categories="User Functions;Built In"
*CMD Postfix --- define function syntax (postfix operator)
*CORE
*CALL
	Postfix("op")
	Postfix("op", precedence)

*PARMS

{"op"} -- string, the name of a function

{precedence} -- nonnegative integer (evaluated)

*DESC

Declares a special syntax for the function to be parsed as a bodied, infix, postfix,
or prefix operator.

"Postfix" functions must have one argument and are syntactically placed after their argument.

Function name can be any string but meaningful usage and readability would
require it to be either made up entirely of letters or entirely of non-letter
characters (such as "+", ":" etc.).
Precedence is optional (will be set to 0 by default).

*E.G.
In> todo

*SEE IsBodied, PrecedenceGet, Bodied, Infix, Prefix
%/mathpiper_docs
*/