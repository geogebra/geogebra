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
public class Rest extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer first = new ConsPointer();
        Utility.tail(aEnvironment, aStackTop, first, getArgumentPointer(aEnvironment, aStackTop, 1));
        Utility.tail(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), first);
        ConsPointer head = new ConsPointer();
        head.setCons(aEnvironment.iListAtom.copy( aEnvironment, false));
        head.cdr().setCons(((ConsPointer) getTopOfStackPointer(aEnvironment, aStackTop).car()).getCons());
        ((ConsPointer) getTopOfStackPointer(aEnvironment, aStackTop).car()).setCons(head.getCons());
    }
}



/*
%mathpiper_docs,name="Rest",categories="User Functions;Lists (Operations);Built In"
*CMD Rest --- returns a list without its car element
*CORE
*CALL
	Rest(list)

*PARMS

{list} -- a list

*DESC

This function returns "list" without its car element.

*E.G.

In> Rest({a,b,c})
Result: {b,c};

*SEE First, Length
%/mathpiper_docs
*/