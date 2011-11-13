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
package org.mathpiper.lisp.parametermatchers;

import org.mathpiper.builtin.BigNumber;

import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Environment;

/// Class for matching an expression to a given number.
public class NumberPatternParameterMatcher extends PatternParameterMatcher {

    protected BigNumber iNumber;


    public NumberPatternParameterMatcher(BigNumber aNumber) {
        iNumber = aNumber;
    }


    public boolean argumentMatches(Environment aEnvironment, int aStackTop, ConsPointer aExpression, ConsPointer[] arguments) throws Exception {

        BigNumber bigNumber = (BigNumber) aExpression.getCons().getNumber(aEnvironment.getPrecision(), aEnvironment);
        
        if (bigNumber != null) {
            return iNumber.equals(bigNumber);
        }
        
        return false;
    }


    public String getType() {
        return "Number";
    }


    @Override
    public String toString()
    {
        return this.iNumber.toString();
    }

}
