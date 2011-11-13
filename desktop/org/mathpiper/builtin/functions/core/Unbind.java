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

import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class Unbind extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        if (getArgumentPointer(aEnvironment, aStackTop, 1).car() instanceof ConsPointer) {

            ConsPointer subList = (ConsPointer) getArgumentPointer(aEnvironment, aStackTop, 1).car();
            
            ConsTraverser consTraverser = new ConsTraverser(aEnvironment, subList);
            consTraverser.goNext(aStackTop);
            int nr = 1;
            while (consTraverser.getCons() != null)
            {
                String variableName;
                variableName =  (String) consTraverser.car();
                LispError.checkArgument(aEnvironment, aStackTop, variableName != null, nr, "Unbind");
                aEnvironment.unbindVariable(aStackTop, variableName);
                consTraverser.goNext(aStackTop);
                nr++;
            }
        }
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="Unbind",categories="User Functions;Variables;Built In"
*CMD Unbind --- undo an assignment
*CORE
*CALL
	Unbind(var, ...)

*PARMS

{var} -- name of variable to be unbound

*DESC

All assignments made to the variables listed as arguments are
undone. From now on, all these variables remain unevaluated (until a
subsequent assignment is made). Also unbinds any metadata that may have
been set in an unbound variable.  If a * wildcard character is passed in
 as the variable name, all local and global variables are unbound.

*E.G.
In> a := 5;
Result> 5;

In> a^2;
Result> 25;

In> Unbind(a);
Result> True;

In> a^2;
Result> a^2;

In> Unbind(*)
Result> True

*SEE Bind, :=
%/mathpiper_docs
*/


