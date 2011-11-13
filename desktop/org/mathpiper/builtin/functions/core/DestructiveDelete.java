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
public class DestructiveDelete extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.delete(aEnvironment, aStackTop, true);
    }
}



/*
%mathpiper_docs,name="DestructiveDelete",categories="User Functions;Lists (Operations);Built In"
*CMD DestructiveDelete --- delete an element destructively from a list
*CORE
*CALL
	DestructiveDelete(list, n)

*PARMS

{list} -- list from which an element should be removed

{n} -- index of the element to remove

*DESC

This is the destructive counterpart of {Delete}. This
command yields the same result as the corresponding call to
{Delete}, but the original list is modified. So if a
variable is bound to "list", it will now be bound to the list with
the n-th entry removed.

Destructive commands run faster than their nondestructive counterparts
because the latter copy the list before they alter it.

*E.G.

In> lst := {a,b,c,d,e,f};
Result: {a,b,c,d,e,f};
In> Delete(lst, 4);
Result: {a,b,c,e,f};
In> lst;
Result: {a,b,c,d,e,f};
In> DestructiveDelete(lst, 4);
Result: {a,b,c,e,f};
In> lst;
Result: {a,b,c,e,f};

*SEE Delete, DestructiveInsert, DestructiveReplace
%/mathpiper_docs
*/