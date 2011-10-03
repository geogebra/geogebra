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
public class Replace extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Utility.replace(aEnvironment, aStackTop, false);
    }
}



/*
%mathpiper_docs,name="Replace",categories="User Functions;Lists (Operations);Built In"
*CMD Replace --- replace an entry in a list

*CORE

*CALL
	Replace(list, n, expr)

*PARMS

{list} -- list of which an entry should be replaced

{n} -- index of entry to replace

{expr} -- expression to replace the n-th entry with

*DESC

The n-th entry of "list" is replaced by the expression
"expr". This is equivalent to calling {Delete} and
{Insert} in sequence. To be precise, the expression
{Replace(list, n, expr)} has the same result as the
expression {Insert(Delete(list, n), n, expr)}.

*E.G.

In> Replace({a,b,c,d,e,f}, 4, x);
Result: {a,b,c,x,e,f};

*SEE Delete, Insert, DestructiveReplace
%/mathpiper_docs
*/