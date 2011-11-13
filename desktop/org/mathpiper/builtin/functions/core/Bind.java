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
public class Bind extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.setVar(aEnvironment, aStackTop, false, false);
    }
}



/*
%mathpiper_docs,name="Bind",categories="User Functions;Variables;Built In"
*CMD Bind --- assignment
*CORE
*CALL
	Bind(var, exp)

*PARMS

{var} -- variable which should be assigned

{exp} -- expression to assign to the variable

*DESC

The expression "exp" is evaluated and assigned it to the variable
named "var". The first argument is not evaluated. The value True
is returned.

The statement {Bind(var, exp)} is equivalent to {var := exp}, but the {:=} operator
has more uses, e.g. changing individual entries in a list.

*E.G.

In> Bind(a, Sin(x)+3);
Result: True;
In> a;
Result: Sin(x)+3;

*SEE Unbind, :=
%/mathpiper_docs
*/