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
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class Factorial extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons().getNumber(0, aEnvironment) != null, 1, "Factorial");
        ConsPointer arg = getArgumentPointer(aEnvironment, aStackTop, 1);

        //TODO fixme I am sure this can be optimized still
//        LispError.check(arg.type().equals("Number"), LispError.INVALID_ARGUMENT);
        int nr = (int) ((BigNumber) arg.getCons().getNumber(0, aEnvironment)).toLong();
        LispError.check(aEnvironment, aStackTop, nr >= 0, LispError.INVALID_ARGUMENT, "Factorial");
        BigNumber fac = new BigNumber( "1", 10, 10);
        int i;
        for (i = 2; i <= nr; i++)
        {
            BigNumber m = new BigNumber( "" + i, 10, 10);
            m.multiply(fac, m, 0);
            fac = m;
        }
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(fac));
    }
}
