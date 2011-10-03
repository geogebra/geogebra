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
public class DestructiveInsert extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.insert(aEnvironment, aStackTop, true);
    }
}



/*
%mathpiper_docs,name="DestructiveInsert",categories="User Functions;Lists (Operations);Built In"
*CMD DestructiveInsert --- insert an element destructively into a list
*CORE
*CALL
	DestructiveInsert(list, n, expr)

*PARMS

{list} -- list in which "expr" should be inserted

{n} -- index at which to insert

{expr} -- expression to insert in "list"

*DESC

This is the destructive counterpart of {Insert}. This
command yields the same result as the corresponding call to
{Insert}, but the original list is modified. So if a
variable is bound to "list", it will now be bound to the list with
the expression "expr" inserted.

Destructive commands run faster than their nondestructive counterparts
because the latter copy the list before they alter it.

*E.G.

In> lst := {a,b,c,d};
Result: {a,b,c,d};
In> Insert(lst, 2, x);
Result: {a,x,b,c,d};
In> lst;
Result: {a,b,c,d};
In> DestructiveInsert(lst, 2, x);
Result: {a,x,b,c,d};
In> lst;
Result: {a,x,b,c,d};

*SEE Insert, DestructiveDelete, DestructiveReplace
%/mathpiper_docs
*/