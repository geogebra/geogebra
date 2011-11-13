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

import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Environment;

//Class for matching against a pattern variable.
public class VariablePatternParameterMatcher extends PatternParameterMatcher {
    //Index of variable in MathPiperPatternPredicateBase.iVariables.
    protected int iVarIndex;

    //Not used.
    protected String iString;


    public VariablePatternParameterMatcher(int aVarIndex) {
        iVarIndex = aVarIndex;
    }


    /**
     *Matches an expression against the pattern variable.
     *@param aEnvironment the underlying Lisp environment.
     *@param aExpression the expression to test.
     *@param arguments (input/output) actual values of the pattern variables for aExpression.
     *
     *If entry iVarIndex in arguments is still empty, the
     *pattern matches and aExpression is stored in this
     *entry. Otherwise, the pattern only matches if the entry equals
     *aExpression.
     */
    public boolean argumentMatches(Environment aEnvironment, int aStackTop, ConsPointer aExpression, ConsPointer[] arguments) throws Exception {

        if (arguments[iVarIndex].getCons() == null) {
            arguments[iVarIndex].setCons(aExpression.getCons());
            //Set var iVarIndex.
            return true;
        } else {
            if (Utility.equals(aEnvironment, aStackTop, aExpression, arguments[iVarIndex])) {
                //Matched var iVarIndex.
                return true;
            }
            return false;
        }

    }//end method.


    public String getType() {
        return "Variable";
    }


    @Override
    public String toString()
    {
        return "";
    }

};
