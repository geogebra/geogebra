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
package org.mathpiper.lisp.rulebases;

import org.mathpiper.lisp.*;
import java.util.*;

/**
 * Holds a set of {@link SingleArityRulebase} which are associated with one function name.
 * A specific SingleArityRulebase can be selected by providing its name.  The
 * name of the file in which the function is defined can also be specified.
 */
public class MultipleArityRulebase {

    /// Set of SingleArityRulebase's provided by this MultipleArityRulebase.
    List<SingleArityRulebase> iFunctions = new ArrayList();//
    /// File to read for the definition of this function.
    public DefFile iFileToOpen;
    public String iFileLocation;


    public MultipleArityRulebase() {
        iFileToOpen = null;
    }


    /**
     *Return user function with given arity.
     */
    public SingleArityRulebase getUserFunction(int aArity, int aStackTop, Environment aEnvironment) throws Exception {
        int ruleIndex;
        //Find function body with the right arity
        int numberOfRules = iFunctions.size();
        for (ruleIndex = 0; ruleIndex < numberOfRules; ruleIndex++) {
            LispError.lispAssert(iFunctions.get(ruleIndex) != null, aEnvironment, aStackTop);

            if (((SingleArityRulebase) iFunctions.get(ruleIndex)).isArity(aArity)) {
                return (SingleArityRulebase) iFunctions.get(ruleIndex);
            }
        }

        // If function not found, just unaccept!
        // User-defined function not found! Returning null
        return null;

    }//end method.


    /**
     * Specify that some argument should be held.
     */
    public void holdArgument(String aVariable, int aStackTop, Environment aEnvironment) throws Exception {
        int ruleIndex;
        for (ruleIndex = 0; ruleIndex < iFunctions.size(); ruleIndex++) {
            LispError.lispAssert(iFunctions.get(ruleIndex) != null, aEnvironment, aStackTop);
            ((SingleArityRulebase) iFunctions.get(ruleIndex)).holdArgument(aVariable);
        }
    }//end method.


    /**
     *Add another SingleArityRulebase to #iFunctions.
     */
    public void addRulebaseEntry(Environment aEnvironment, int aStackTop, SingleArityRulebase aNewFunction) throws Exception {
        int ruleIndex;
        //Find function body with the right arity
        int numberOfRules = iFunctions.size();
        for (ruleIndex = 0; ruleIndex < numberOfRules; ruleIndex++) {
            LispError.lispAssert(((SingleArityRulebase) iFunctions.get(ruleIndex)) != null, aEnvironment, aStackTop);
            LispError.lispAssert(aNewFunction != null, aEnvironment, aStackTop);
            LispError.check(aEnvironment, aStackTop, !((SingleArityRulebase) iFunctions.get(ruleIndex)).isArity(aNewFunction.arity()), LispError.ARITY_ALREADY_DEFINED, "INTERNAL");
            LispError.check(aEnvironment, aStackTop, !aNewFunction.isArity(((SingleArityRulebase) iFunctions.get(ruleIndex)).arity()), LispError.ARITY_ALREADY_DEFINED, "INTERNAL");
        }
        iFunctions.add(aNewFunction);
    }//end method.


    /**
     *Delete user function with given arity.  If arity is -1 then delete all functions regardless of arity.
     */
    public void deleteRulebaseEntry(int aArity, int aStackTop, Environment aEnvironment) throws Exception {
        if (aArity == -1) //Retract all functions regardless of arity.
        {
            iFunctions.clear();
            return;
        }//end if.

        int ruleIndex;
        //Find function body with the right arity
        int numberOfRules = iFunctions.size();
        for (ruleIndex = 0; ruleIndex < numberOfRules; ruleIndex++) {
            LispError.lispAssert(((SingleArityRulebase) iFunctions.get(ruleIndex)) != null, aEnvironment, aStackTop);

            if (((SingleArityRulebase) iFunctions.get(ruleIndex)).isArity(aArity)) {
                iFunctions.remove(ruleIndex);
                return;
            }
        }
    }//end method.


    public Iterator getFunctions() {
        return this.iFunctions.iterator();
    }

}
