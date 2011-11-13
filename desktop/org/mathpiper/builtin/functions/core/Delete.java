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
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
public class Delete extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.delete(aEnvironment, aStackTop, false);
    }
}



/*
%mathpiper_docs,name="Delete",categories="User Functions;Lists (Operations);Built In"
*CMD Delete --- delete an element from a list
*CORE
*CALL
	Delete(list, n)

*PARMS

{list} -- list from which an element should be removed

{n} -- index of the element to remove

*DESC

This command deletes the n-th element from "list". The first
parameter should be a list, while "n" should be a positive integer
less than or equal to the length of "list". The entry with index
"n" is removed (the first entry has index 1), and the resulting list
is returned.

*E.G.

In> Delete({a,b,c,d,e,f}, 4);
Result: {a,b,c,e,f};

*SEE DestructiveDelete, Insert, Replace
%/mathpiper_docs
*/