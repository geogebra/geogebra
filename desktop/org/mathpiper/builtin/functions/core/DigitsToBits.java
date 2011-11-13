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
import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class DigitsToBits extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigNumber x = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);
        BigNumber y = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 2);
        long result = 0;  // initialize just in case

        if (x.isInteger() && x.isSmall() && y.isInteger() && y.isSmall())
        {
            // bits_to_digits uses unsigned long, see numbers.h
            int base = (int) y.toDouble();
            result = Utility.digitsToBits((long) (x.toDouble()), base);
        } else
        {
            throw new EvaluationException("BitsToDigits: error: arguments (" + x.toDouble() + ", " + y.toDouble() + ") must be small integers",aEnvironment.iInputStatus.fileName(), aEnvironment.iCurrentInput.iStatus.lineNumber());
        }
        BigNumber z = new BigNumber(aEnvironment.getPrecision());
        z.setTo((long) result);
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(z));
    }
}
