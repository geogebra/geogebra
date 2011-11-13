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

/**
 *
 *  
 */
public class FastLog extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigNumber x;
        x = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);
        double result = Math.log(x.toDouble());
        BigNumber z = new BigNumber(aEnvironment.getPrecision());
        z.setTo(result);
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(z));
    }
}//end class.




/*
%mathpiper,name="FastLog",categories="Programmer Functions;Built In"
*CMD FastLog --- double-precision natural logarithm
*CORE
*CALL
	FastLog(x)

*PARMS
{a} -- a number

*DESC
This function uses the Java math library. It
should be faster than the arbitrary precision version.

*SEE FastPower, FastArcSin

%/mathpiper_docs
*/
