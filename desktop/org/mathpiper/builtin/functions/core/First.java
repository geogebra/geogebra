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
public class First extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.nth(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1), 1);
    }
}



/*
%mathpiper_docs,name="First",categories="User Functions;Lists (Operations);Built In"
*CMD First --- the first element of a list
*CORE
*CALL
	First(list)

*PARMS

{list} -- a list

*DESC

This function returns the first element of a list. If it is applied to
a general expression, it returns the first operand. An error is
returned if "list" is an atom.

*E.G.

In> First({a,b,c})
Result: a;
In> First(f(a,b,c));
Result: a;

*SEE Rest, Length
%/mathpiper_docs
*/