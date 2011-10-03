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
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class ListToFunction extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "ListToFunction");
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).car() instanceof ConsPointer, 1, "ListToFunction");
        Cons atom = ((ConsPointer) getArgumentPointer(aEnvironment, aStackTop, 1).car()).getCons();
        LispError.checkArgument(aEnvironment, aStackTop, atom != null, 1, "ListToFunction");
        LispError.checkArgument(aEnvironment, aStackTop, atom.car() == aEnvironment.iListAtom.car(), 1, "ListToFunction");
        Utility.tail(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1));
    }
}



/*
%mathpiper_docs,name="ListToFunction",categories="User Functions;Lists (Operations);Built In"
*CMD ListToFunction --- convert a list to a function application
*CORE
*CALL
	ListToFunction(list)

*PARMS

{list} -- list to be converted

*DESC

This command converts a list to a function application. The car
entry of "list" is treated as a function atom, and the following entries
are the arguments to this function. So the function referred to in the
car element of "list" is applied to the other elements.

Note that "list" is evaluated before the function application is
formed, but the resulting expression is left unevaluated. The functions {ListToFunction()} and {Hold()} both stop the process of evaluation.

*E.G.

In> ListToFunction({Cos, x});
Result: Cos(x);
In> ListToFunction({f});
Result: f();
In> ListToFunction({Taylor,x,0,5,Cos(x)});
Result: Taylor(x,0,5)Cos(x);
In> Eval(%);
Result: 1-x^2/2+x^4/24;

*SEE List, FunctionToList, Hold
%/mathpiper_docs
*/