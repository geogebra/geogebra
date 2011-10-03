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

/**
 *
 *  
 */
public class IsLessThan extends BuiltinFunction
{

    LexLessThan compare = new LexLessThan();

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        compare.Compare(aEnvironment, aStackTop);
    }
}//end class.




/*
%mathpiper_docs,name="IsLessThan",categories="User Functions;Predicates;Built In"
*CMD IsLessThan --- comparison predicate
*CORE
*CALL
	IsLessThan(a,b)

*PARMS
{a}, {b} -- decimal numbers or strings
*DESC
Compare decimal numbers or strings (lexicographically).

*E.G.
In> IsLessThan(1,1)
Result: False;
In> IsLessThan("a","b")
Result: True;

*SEE IsGreaterThan, IsEqual
%/mathpiper_docs
*/
