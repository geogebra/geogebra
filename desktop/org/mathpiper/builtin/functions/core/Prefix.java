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
public class Prefix extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.multiFix(aEnvironment, aStackTop, aEnvironment.iPrefixOperators);
    }
}



/*
%mathpiper_docs,name="Prefix",categories="User Functions;Built In"
*CMD Prefix --- define function syntax (prefix operator)
*CORE
*CALL
	Prefix("op")
	Prefix("op", precedence)

*PARMS

{"op"} -- string, the name of a function

{precedence} -- nonnegative integer (evaluated)

*DESC

"Prefix" functions must have one argument and are syntactically placed before their argument.

Function name can be any string but meaningful usage and readability would
require it to be either made up entirely of letters or entirely of non-letter
characters (such as "+", ":" etc.).
Precedence is optional (will be set to 0 by default).

*E.G.
In> YY x := x+1;
	CommandLine(1) : Error parsing expression

In> Prefix("YY", 2)
Result: True;
In> YY x := x+1;
Result: True;
In> YY YY 2*3
Result: 12;

Note that, due to a current parser limitation, a function atom that is declared prefix cannot be used by itself as an argument.

In> YY
	CommandLine(1) : Error parsing expression

*SEE IsBodied, PrecedenceGet, Bodied, Infix, Postfix
%/mathpiper_docs
*/