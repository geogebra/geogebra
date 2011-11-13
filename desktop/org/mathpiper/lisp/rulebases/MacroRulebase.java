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

import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.stacks.UserStackInformation;
import org.mathpiper.lisp.behaviours.BackQuoteSubstitute;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Evaluator;
import org.mathpiper.lisp.LispExpressionEvaluator;
import org.mathpiper.lisp.cons.SublistCons;

public class MacroRulebase extends SingleArityRulebase {

    public MacroRulebase(Environment aEnvironment, int aStackTop, ConsPointer aParameters, String functionName) throws Exception {
        super(aEnvironment, aStackTop, aParameters, functionName);
        ConsTraverser parameterTraverser = new ConsTraverser(aEnvironment, aParameters);
        int i = 0;
        while (parameterTraverser.getCons() != null) {

            //LispError.check(parameterTraverser.car() != null, LispError.CREATING_USER_FUNCTION);
            try {
                LispError.check(aEnvironment, aStackTop, parameterTraverser.car() instanceof String, LispError.CREATING_USER_FUNCTION, "INTERNAL");
            } catch (EvaluationException ex) {
                if (ex.getFunctionName() == null) {
                    throw new EvaluationException(ex.getMessage() + " In function: " + this.functionName + ",  ", "none", -1, this.functionName);
                } else {
                    throw ex;
                }
            }//end catch.


            ((ParameterName) iParameters.get(i)).iHold = true;
            parameterTraverser.goNext(aStackTop);
            i++;
        }
        //Macros are all unfenced.
        unFence();

        this.functionType = "macro";
    }


    @Override
    public void evaluate(Environment aEnvironment, int aStackTop, ConsPointer aResult, ConsPointer aArgumentsPointer) throws Exception {
        int arity = arity();
        ConsPointer[] argumentsResultPointerArray = evaluateArguments(aEnvironment, aStackTop, aArgumentsPointer);



        ConsPointer substitutedBodyPointer = new ConsPointer();

        //Create a new local variable frame that is unfenced (false = unfenced).
        aEnvironment.pushLocalFrame(false, this.functionName);

        try {
            // define the local variables.
            for (int parameterIndex = 0; parameterIndex < arity; parameterIndex++) {
                String variable = ((ParameterName) iParameters.get(parameterIndex)).iName;

                // set the variable to the new value
                aEnvironment.newLocalVariable(variable, argumentsResultPointerArray[parameterIndex].getCons(), aStackTop);
            }

            // walk the rules database, returning the evaluated result if the
            // predicate is true.
            int numberOfRules = iBranchRules.size();
            UserStackInformation userStackInformation = aEnvironment.iLispExpressionEvaluator.stackInformation();
            for (int ruleIndex = 0; ruleIndex < numberOfRules; ruleIndex++) {
                Rule thisRule = ((Rule) iBranchRules.get(ruleIndex));
                //TODO remove            CHECKPTR(thisRule);
                LispError.lispAssert(thisRule != null, aEnvironment, aStackTop);

                userStackInformation.iRulePrecedence = thisRule.getPrecedence();

                boolean matches = thisRule.matches(aEnvironment, aStackTop, argumentsResultPointerArray);

                if (matches) {
                    /* Rule dump trace code. */
                    if (isTraced() && showFlag) {
                        ConsPointer argumentsPointer = new ConsPointer();
                        argumentsPointer.setCons(SublistCons.getInstance(aEnvironment, aArgumentsPointer.getCons()));
                        String ruleDump = org.mathpiper.lisp.Utility.dumpRule(aStackTop, thisRule, aEnvironment, this);
                        Evaluator.traceShowRule(aEnvironment, argumentsPointer, ruleDump);
                    }
                    userStackInformation.iSide = 1;

                    BackQuoteSubstitute backQuoteSubstitute = new BackQuoteSubstitute(aEnvironment);

                    ConsPointer originalBodyPointer = thisRule.getBodyPointer();
                    Utility.substitute(aEnvironment, aStackTop, substitutedBodyPointer, originalBodyPointer, backQuoteSubstitute);
                    //              aEnvironment.iLispExpressionEvaluator.Eval(aEnvironment, aResult, thisRule.body());
                    break;
                }

                // If rules got inserted, walk back
                while (thisRule != ((Rule) iBranchRules.get(ruleIndex)) && ruleIndex > 0) {
                    ruleIndex--;
                }
            }
        } catch (EvaluationException ex) {
            if (ex.getFunctionName() == null) {
                throw new EvaluationException(ex.getMessage() + " In function: " + this.functionName + ",  ", "none", -1, this.functionName);
            } else {
                throw ex;
            }
        } finally {
            aEnvironment.popLocalFrame(aStackTop);
        }



        if (substitutedBodyPointer.getCons() != null) {
            //Note:tk:substituted body must be evaluated after the local frame has been popped.
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, aResult, substitutedBodyPointer);
        } else // No predicate was true: return a new expression with the evaluated
        // arguments.
        {
            ConsPointer full = new ConsPointer();
            full.setCons(aArgumentsPointer.getCons().copy(aEnvironment, false));
            if (arity == 0) {
                full.cdr().setCons(null);
            } else {
                full.cdr().setCons(argumentsResultPointerArray[0].getCons());
                for (int parameterIndex = 0; parameterIndex < arity - 1; parameterIndex++) {
                    argumentsResultPointerArray[parameterIndex].cdr().setCons(argumentsResultPointerArray[parameterIndex + 1].getCons());
                }
            }
            aResult.setCons(SublistCons.getInstance(aEnvironment, full.getCons()));
        }
        //FINISH:

        /*Leave trace code */
        if (isTraced() && showFlag) {
            ConsPointer tr = new ConsPointer();
            tr.setCons(SublistCons.getInstance(aEnvironment, aArgumentsPointer.getCons()));
            String localVariables = aEnvironment.getLocalVariables(aStackTop);
            LispExpressionEvaluator.traceShowLeave(aEnvironment, aResult, tr, "macro", localVariables);
            tr.setCons(null);
        }

    }

}
