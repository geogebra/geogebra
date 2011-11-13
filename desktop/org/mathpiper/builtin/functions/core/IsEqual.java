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
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class IsEqual extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated1 = new ConsPointer();
        evaluated1.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        ConsPointer evaluated2 = new ConsPointer();
        evaluated2.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());

        Utility.putBooleanInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop),
                Utility.equals(aEnvironment, aStackTop, evaluated1, evaluated2));
    }
}//end class.




/*
%mathpiper_docs,name="IsEqual",categories="User Functions;Built In"
*CMD IsEqual --- check equality
*CORE
*CALL
	IsEqual(a,b)

*DESC
Compares evaluated {a} and {b} recursively
(stepping into expressions). So "IsEqual(a,b)" returns
"True" if the expressions would be printed exactly
the same, and "False" otherwise.

*SEE GreaterThan, IsLessThan

%/mathpiper_docs
*/
