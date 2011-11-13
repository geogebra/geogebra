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
public class Infix extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.multiFix(aEnvironment, aStackTop, aEnvironment.iInfixOperators);
    }
}



/*
%mathpiper_docs,name="Infix",categories="User Functions;Built In"
*CMD Infix --- define function syntax (infix operator)
*CORE
*CALL
	Infix("op")
	Infix("op", precedence)

*PARMS

{"op"} -- string, the name of a function

{precedence} -- nonnegative integer (evaluated)

*DESC

Declares a special syntax for the function to be parsed as an  infix operator.

"Infix" functions must have two arguments and are syntactically placed between their arguments.
Names of infix functions can be arbitrary, although for reasons of readability they are usually made of non-alphabetic characters.

Function name can be any string but meaningful usage and readability would
require it to be either made up entirely of letters or entirely of non-letter
characters (such as "+", ":" etc.).
Precedence is optional (will be set to 0 by default).

*E.G.
In> Infix("##", 5)
Result: True;
In> a ## b ## c
Result: a##b##c;

*SEE IsBodied, PrecedenceGet, Bodied, Postfix, Prefix
%/mathpiper_docs
*/