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
import org.mathpiper.lisp.cons.SublistCons;

/**
 *
 *  
 */
public class FunctionToList extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).car() instanceof ConsPointer, 1, "FunctionToList");
        ConsPointer head = new ConsPointer();
        head.setCons(aEnvironment.iListAtom.copy( aEnvironment, false));
        head.cdr().setCons(((ConsPointer) getArgumentPointer(aEnvironment, aStackTop, 1).car()).getCons());
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(SublistCons.getInstance(aEnvironment,head.getCons()));
    }
}



/*
%mathpiper_docs,name="FunctionToList",categories="User Functions;Lists (Operations);Built In"
*CMD FunctionToList --- convert a function application to a list
*CORE
*CALL
	FunctionToList(expr)

*PARMS

{expr} -- expression to be converted

*DESC

The parameter "expr" is expected to be a compound object, i.e. not
an atom. It is evaluated and then converted to a list. The car entry
in the list is the top-level operator in the evaluated expression and
the other entries are the arguments to this operator. Finally, the
list is returned.

*E.G.

In> FunctionToList(Cos(x));
Result: {Cos,x};
In> FunctionToList(3*a);
Result: {*,3,a};

*SEE List, ListToFunction, IsAtom
%/mathpiper_docs
*/