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
import org.mathpiper.exceptions.BreakException;
import org.mathpiper.lisp.Environment;

/**
 *
 *
 */
public class Break extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
         throw new BreakException();
    }
    
}//end class.



/*
%mathpiper_docs,name="Break",categories="User Functions;Control Flow;Built In"
*CMD Break --- break out of a loop
*CORE
*CALL

    Break()

*DESC

If Break is executed inside of a While, Until, For, or ForEach loop, it will
cause the loop to be exited.

*E.G.

/%mathpiper

x := 1;

While(x <= 10)
[
    Echo(x);

    If(x = 5, Break());

    x++;
];

/%/mathpiper

    /%output,preserve="false"
      Result: True

      Side Effects:
      1
      2
      3
      4
      5
.   /%/output

*SEE While, Until, For, ForEach, Continue
%/mathpiper_docs
*/