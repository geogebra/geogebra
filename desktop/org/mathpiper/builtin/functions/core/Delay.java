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

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;

/**
 *
 *
 */
public class Delay extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigNumber milliseconds = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);

        Thread.sleep(milliseconds.toLong());

        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}//end class.



/*
%mathpiper_docs,name="Delay",categories="User Functions;Built In;Input/Output"
*CMD Delay --- delays execution of a program for a specified number of milliseconds
*CORE
*CALL
	Delay(ms)

 *PARAMS
 {ms} -- the number of milliseconds to delay

*DESC

This function delays execution of a program for the specified number of milliseconds.
The delay can be terminated by pressing the "Halt Calculation" button.

*E.G.
In> Delay(1000)
Result: True

%/mathpiper_docs
*/
