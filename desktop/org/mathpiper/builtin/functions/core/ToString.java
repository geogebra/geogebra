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

import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *
 */
public class ToString extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // Get operator
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "ToString");

        String orig = null;
        if(evaluated.car() instanceof String)
        {
                 orig = (String) evaluated.car();
        }
        else if(evaluated.car() instanceof BuiltinContainer)
        {
            BuiltinContainer container = (BuiltinContainer) evaluated.car();
            orig = container.getObject().toString();
        }

        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "ToString");

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, aEnvironment.getTokenHash().lookUpStringify(orig)));
    }
}



/*
%mathpiper_docs,name="ToString",categories="User Functions;String Manipulation;Built In"
*CMD ToString --- convert atom to string
*CORE
*CALL
	ToString(atom)

*PARMS

{atom} -- an atom

*DESC

{ToString} is the inverse of {ToAtom}: turns {atom} into {"atom"}.

*E.G.
In> ToString(a)
Result: "a";

*SEE ToAtom, ExpressionToString
%/mathpiper_docs
*/