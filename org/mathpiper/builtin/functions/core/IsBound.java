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
public class IsBound extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        
        if (getArgumentPointer(aEnvironment, aStackTop, 1).car() instanceof String)
        {
            String str =  (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
            ConsPointer val = new ConsPointer();
            aEnvironment.getGlobalVariable(aStackTop, str, val);
            if (val.getCons() != null)
            {
                Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
                return;
            }
        }
        Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="IsBound",categories="User Functions;Predicates;Built In"
*CMD IsBound --- test for a bound variable
*CORE
*CALL
	IsBound(var)

*PARMS

{var} -- variable to test

*DESC

This function tests whether the variable "var" is bound, i.e. whether
it has been assigned a value. The argument "var" is not evaluated.

*E.G.

In> IsBound(x);
Result: False;
In> x := 5;
Result: 5;
In> IsBound(x);
Result: True;

*SEE IsAtom
%/mathpiper_docs
*/