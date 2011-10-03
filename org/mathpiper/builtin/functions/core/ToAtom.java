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
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class ToAtom extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // Get operator
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "ToAtom");
        String orig =  (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "ToAtom");
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, aEnvironment.getTokenHash().lookUpUnStringify(orig)));
    }
}




/*
%mathpiper_docs,name="ToAtom",categories="User Functions;String Manipulation"
*CMD ToAtom --- convert string to atom
*CORE
*CALL
	ToAtom("string")

*PARMS

{"string"} -- a string

*DESC

Returns an atom with the string representation given
as the evaluated argument. Example: {ToAtom("foo");} returns
{foo}.


*E.G.

In> ToAtom("a")
Result: a;

*SEE ToString, ExpressionToString
%/mathpiper_docs
*/