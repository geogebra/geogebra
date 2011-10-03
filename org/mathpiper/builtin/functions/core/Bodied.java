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
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
public class Bodied extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.multiFix(aEnvironment, aStackTop, aEnvironment.iBodiedOperators);
    }
}



/*
%mathpiper_docs,name="Bodied",categories="Programmer Functions;Programming;Built In"
*CMD Bodied --- define function syntax (bodied function)
*CORE
*CALL
	Bodied("op", precedence)

*PARMS

{"op"} -- string, the name of a function

{precedence} -- nonnegative integer (evaluated)

*DESC

Declares a special syntax for the function to be parsed as a bodied operator.

"Bodied" functions have all arguments except the first one inside parentheses and the last argument outside, for example:
	For(pre, condition, post) statement;
Here the function {For} has 4 arguments and the last argument is placed outside the parentheses.
The {precedence} of a "bodied" function refers to how tightly the last argument is bound to the parentheses.
This makes a difference when the last argument contains other operators.
For example, when taking the derivative
	D(x) Sin(x)+Cos(x)
both {Sin} and {Cos} are under the derivative because the bodied function {D} binds less tightly than the infix operator "{+}".

Function name can be any string but meaningful usage and readability would
require it to be either made up entirely of letters or entirely of non-letter
characters (such as "+", ":" etc.).
Precedence is optional (will be set to 0 by default).

*E.G.
In> todo

*SEE IsBodied, PrecedenceGet, Infix, Postfix, Prefix
%/mathpiper_docs
*/