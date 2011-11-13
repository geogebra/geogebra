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
public class Insert extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.insert(aEnvironment, aStackTop, false);
    }
}



/*
%mathpiper_docs,name="Insert",categories="User Functions;Lists (Operations);Built In"
*CMD Insert --- insert an element into a list
*CORE
*CALL
	Insert(list, n, expr)

*PARMS

{list} -- list in which "expr" should be inserted

{n} -- index at which to insert

{expr} -- expression to insert in "list"

*DESC

The expression "expr" is inserted just before the n-th entry in
"list". The first parameter "list" should be a list, while "n"
should be a positive integer less than or equal to the length of
"list" plus one. The expression "expr" is placed between the
entries in "list" with entries "n-1" and "n". There are two
border line cases: if "n" is 1, the expression "expr" is placed in
front of the list (just as by the {:} operator); if "n"
equals the length of "list" plus one, the expression "expr" is
placed at the end of the list (just as by {Append}). In any
case, the resulting list is returned.

*E.G.

In> Insert({a,b,c,d}, 4, x);
Result: {a,b,c,x,d};
In> Insert({a,b,c,d}, 5, x);
Result: {a,b,c,d,x};
In> Insert({a,b,c,d}, 1, x);
Result: {x,a,b,c,d};

*SEE DestructiveInsert, :, Append, Delete, Remove
%/mathpiper_docs
*/