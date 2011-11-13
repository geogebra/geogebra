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
import org.mathpiper.exceptions.ContinueException;
import org.mathpiper.lisp.Environment;

/**
 *
 *
 */
public class Continue extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
         throw new ContinueException();
    }

}//end class.



/*
%mathpiper_docs,name="Continue",categories="User Functions;Control Flow;Built In"
*CMD Continue --- skips executing the remainder of code in this loop iteration and begins the next iteration
*CORE
*CALL

    Continue()

*DESC

If Continue is executed inside of a While, Until, For, or ForEach loop, all the code between
the continue command and the end of the loop will be skipped and the next loop iteration
will be started.

*E.G.

/%mathpiper

x := 0;

While(x < 8)
[
    x++;

    If(x = 5, Continue());

    Echo(x);

];

/%/mathpiper

    /%output,preserve="false"
      Result: True

      Side Effects:
      1
      2
      3
      4
      6
      7
      8
.   /%/output

*SEE While, Until, For, ForEach, Break
%/mathpiper_docs
*/