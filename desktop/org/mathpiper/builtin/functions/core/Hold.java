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
 *  tkosan
 */
public class Hold extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons().copy( aEnvironment, false));
    }
}

/*
%mathpiper_docs,name="Hold",categories="Programmer Functions;Programming;Built In"
*CMD Hold --- keep expression unevaluated
*CORE
*CALL
	Hold(expr)

*PARMS

{expr} -- expression to keep unevaluated

*DESC

The expression "expr" is returned unevaluated. This is useful to
prevent the evaluation of a certain expression in a context in which
evaluation normally takes place.

The function {ListToFunction()} also leaves its result unevaluated.
 Both functions stop the process of evaluation (no more rules will be applied).

*E.G.

In> Echo({ Hold(1+1), "=", 1+1 });
Result: True
Side Effects:
{1+1,"=",2} 

*SEE Eval, HoldArgument, ListToFunction
%/mathpiper_docs
*/
