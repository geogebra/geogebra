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

import org.mathpiper.lisp.stacks.UserStackInformation;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.SublistCons;
import java.util.*;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.exceptions.ReturnException;
import org.mathpiper.lisp.Evaluator;

/**
 * A function (usually mathematical) which is defined by one or more rules.
 * This is the basic class which implements functions.  Evaluation is done
 * by consulting a set of rewritng rules.  The body of the first rule that
 * matches is evaluated and its result is returned as the function's result.
 */
public class SingleArityRulebase extends Evaluator {
    // List of arguments, with corresponding iHold property.
    protected List<ParameterName> iParameters = new ArrayList(); //CArrayGrower<ParameterName>

    // List of rules, sorted on precedence.
    protected List<Rule> iBranchRules = new ArrayList();//CDeletingArrayGrower<BranchRuleBase*>
    
    // List of arguments
    ConsPointer iParameterList;
/// Abstract class providing the basic user function API.
/// Instances of this class are associated to the name of the function
/// via an associated hash table. When obtained, they can be used to
/// evaluate the function with some arguments.
    boolean iFenced = true;
    boolean showFlag = false;
    protected String functionType = "**** user rulebase";
    protected String functionName;
    protected Environment iEnvironment;


    /**
     * Constructor.
     *
     * @param aParameters linked list constaining the names of the arguments
     * @throws java.lang.Exception
     */
    public SingleArityRulebase(Environment aEnvironment, int aStackTop, ConsPointer aParametersPointer, String functionName) throws Exception {
        iEnvironment = aEnvironment;
        this.functionName = functionName;
        iParameterList = new ConsPointer();
        // iParameterList and #iParameters are set from \a aParameters.
        iParameterList.setCons(aParametersPointer.getCons());

        ConsPointer parameterPointer = new ConsPointer(aParametersPointer.getCons());

        while (parameterPointer.getCons() != null) {

            try {
                LispError.check(aEnvironment, aStackTop, parameterPointer.car() instanceof String, LispError.CREATING_USER_FUNCTION, "INTERNAL");
            } catch (EvaluationException ex) {
                if (ex.getFunctionName() == null) {
                    throw new EvaluationException(ex.getMessage() + " In function: " + this.functionName + ",  ", "none", -1, this.functionName);
                } else {
                    throw ex;
                }
            }//end catch.

            ParameterName parameter = new ParameterName((String) parameterPointer.car(), false);
            iParameters.add(parameter);
            parameterPointer.goNext(aStackTop, aEnvironment);
        }
    }


    /**
     * Evaluate the function with the given arguments.
     * First, all arguments are evaluated by the evaluator associated
     * with aEnvironment, unless the iHold flag of the
     * corresponding parameter is true. Then a new LocalFrame is
     * constructed, in which the actual arguments are assigned to the
     * names of the formal arguments, as stored in iName. Then
     * all rules in <b>iRules</b> are tried one by one. The body of the
     * first rule that matches is evaluated, and the result is put in
     * aResult. If no rule matches, aResult will recieve a new
     * expression with evaluated arguments.
     * 
     * @param aResult (on output) the result of the evaluation
     * @param aEnvironment the underlying Lisp environment
     * @param aArguments the arguments to the function
     * @throws java.lang.Exception
     */
    public void evaluate(Environment aEnvironment, int aStackTop, ConsPointer aResult, ConsPointer aArgumentsPointer) throws Exception {
        int arity = arity();
        ConsPointer[] argumentsResultPointerArray = evaluateArguments(aEnvironment, aStackTop, aArgumentsPointer);

        // Create a new local variables frame that has the same fenced state as this function.
        aEnvironment.pushLocalFrame(fenced(), this.functionName);

        int beforeStackTop = -1;
        int beforeEvaluationDepth = -1;
        int originalStackTop = -1;

        try {

            // define the local variables.
            for (int parameterIndex = 0; parameterIndex < arity; parameterIndex++) {
                String variableName = ((ParameterName) iParameters.get(parameterIndex)).iName;
                // set the variable to the new value
                aEnvironment.newLocalVariable(variableName, argumentsResultPointerArray[parameterIndex].getCons(), aStackTop);
            }

            // walk the rules database, returning the evaluated result if the
            // predicate is true.
            int numberOfRules = iBranchRules.size();

            UserStackInformation userStackInformation = aEnvironment.iLispExpressionEvaluator.stackInformation();

            for (int ruleIndex = 0; ruleIndex < numberOfRules; ruleIndex++) {
                Rule thisRule = ((Rule) iBranchRules.get(ruleIndex));
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

                    try {
                        beforeStackTop = aEnvironment.iArgumentStack.getStackTopIndex();
                        beforeEvaluationDepth = aEnvironment.iEvalDepth;

                        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, aResult, thisRule.getBodyPointer()); //*** User function is called here.

                    } catch (ReturnException re) {
                        //todo:tk:note that user functions currently return their results in aResult, not on the stack.
                        int stackTopIndex = aEnvironment.iArgumentStack.getStackTopIndex();
                        ConsPointer resultPointer = BuiltinFunction.getTopOfStackPointer(aEnvironment, stackTopIndex - 1);

                        aResult.setCons(resultPointer.getCons());

                        aEnvironment.iArgumentStack.popTo(beforeStackTop, aStackTop, aEnvironment);
                        aEnvironment.iEvalDepth = beforeEvaluationDepth;

                    }

                    /*Leave trace code */
                    if (isTraced() && showFlag) {
                        ConsPointer argumentsPointer2 = new ConsPointer();
                        argumentsPointer2.setCons(SublistCons.getInstance(aEnvironment, aArgumentsPointer.getCons()));
                        String localVariables = aEnvironment.getLocalVariables(aStackTop);
                        Evaluator.traceShowLeave(aEnvironment, aResult, argumentsPointer2, functionType, localVariables);
                        argumentsPointer2.setCons(null);
                    }//end if.

                    return;
                }//end if matches.

                // If rules got inserted, walk back.
                while (thisRule != ((Rule) iBranchRules.get(ruleIndex)) && ruleIndex > 0) {
                    ruleIndex--;
                }
            }//end for.


            // No predicate was true: return a new expression with the evaluated
            // arguments.
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


            /* Trace code */
            if (isTraced() && showFlag) {
                ConsPointer argumentsPointer3 = new ConsPointer();
                argumentsPointer3.setCons(SublistCons.getInstance(aEnvironment, aArgumentsPointer.getCons()));
                String localVariables = aEnvironment.getLocalVariables(aStackTop);
                Evaluator.traceShowLeave(aEnvironment, aResult, argumentsPointer3, functionType, localVariables);
                argumentsPointer3.setCons(null);
            }

        } catch (EvaluationException ex) {

            //ex.printStackTrace();//todo:tk:uncomment for debugging.

            if (ex.getFunctionName() == null) {
                throw new EvaluationException(ex.getMessage() + " In function: " + this.functionName + ",  ", "none", -1, this.functionName);
            } else {
                throw ex;
            }
        } finally {
            aEnvironment.popLocalFrame(aStackTop);
        }
    }


    protected ConsPointer[] evaluateArguments(Environment aEnvironment, int aStackTop, ConsPointer aArgumentsPointer) throws Exception {
        int arity = arity();
        int parameterIndex;

        /*Enter trace code*/
        if (isTraced()) {
            ConsPointer argumentsPointer = new ConsPointer();
            argumentsPointer.setCons(SublistCons.getInstance(aEnvironment, aArgumentsPointer.getCons()));
            String traceFunctionName = "";
            if (argumentsPointer.car() instanceof ConsPointer) {
                ConsPointer sub = (ConsPointer) argumentsPointer.car();
                if (sub.car() instanceof String) {
                    traceFunctionName = (String) sub.car();
                }
            }//end function.
            if (Evaluator.isTraceFunction(traceFunctionName)) {
                showFlag = true;
                Evaluator.traceShowEnter(aEnvironment, argumentsPointer, functionType);
            } else {
                showFlag = false;
            }//
            argumentsPointer.setCons(null);
        }

        ConsPointer argumentsTraverser = new ConsPointer(aArgumentsPointer.getCons());

        //Strip the function name from the head of the list.
        argumentsTraverser.goNext(aStackTop, aEnvironment);

        //Creat an array which holds pointers to each argument.
        ConsPointer[] argumentsResultPointerArray;
        if (arity == 0) {
            argumentsResultPointerArray = null;
        } else {
            LispError.lispAssert(arity > 0, aEnvironment, aStackTop);
            argumentsResultPointerArray = new ConsPointer[arity];
        }

        // Walk over all arguments, evaluating them as necessary ********************************************************
        for (parameterIndex = 0; parameterIndex < arity; parameterIndex++) {

            argumentsResultPointerArray[parameterIndex] = new ConsPointer();

            LispError.check(aEnvironment, aStackTop, argumentsTraverser.getCons() != null, LispError.WRONG_NUMBER_OF_ARGUMENTS, "INTERNAL");

            if (((ParameterName) iParameters.get(parameterIndex)).iHold) {
                //If the parameter is on hold, don't evaluate it and place a copy of it in argumentsPointerArray.
                argumentsResultPointerArray[parameterIndex].setCons(argumentsTraverser.getCons().copy(aEnvironment, false));
            } else {
                //If the parameter is not on hold:

                //Verify that the pointer to the arguments is not null.
                LispError.check(aEnvironment, aStackTop, argumentsTraverser != null, LispError.WRONG_NUMBER_OF_ARGUMENTS, "INTERNAL");

                //Evaluate each argument and place the result into argumentsResultPointerArray[i];
                aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, argumentsResultPointerArray[parameterIndex], argumentsTraverser);
            }
            argumentsTraverser.goNext(aStackTop, aEnvironment);
        }//end for.

        /*Argument trace code */
        if (isTraced() && argumentsResultPointerArray != null && showFlag) {
            //ConsTraverser consTraverser2 = new ConsTraverser(aArguments);
            //ConsPointer traceArgumentPointer = new ConsPointer(aArgumentsPointer.getCons());

            //ConsTransverser traceArgumentPointer new ConsTraverser(this.iParameterList);
            ConsPointer traceParameterPointer = new ConsPointer(this.iParameterList.getCons());

            //traceArgumentPointer.goNext();
            for (parameterIndex = 0; parameterIndex < argumentsResultPointerArray.length; parameterIndex++) {
                Evaluator.traceShowArg(aEnvironment, traceParameterPointer, argumentsResultPointerArray[parameterIndex]);

                traceParameterPointer.goNext(aStackTop, aEnvironment);
            }//end for.
        }//end if.

        return argumentsResultPointerArray;

    }//end method.


    /**
     * Put an argument on hold.
     * The \c iHold flag of the corresponding argument is setCons. This
     * implies that this argument is not evaluated by evaluate().
     * 
     * @param aVariable name of argument to put un hold
     */
    public void holdArgument(String aVariable) {
        int i;
        int nrc = iParameters.size();
        for (i = 0; i < nrc; i++) {
            if (((ParameterName) iParameters.get(i)).iName.equals(aVariable)) {
                ((ParameterName) iParameters.get(i)).iHold = true;
            }
        }
    }


    /**
     * Return true if the arity of the function equals \a aArity.
     * 
     * @param aArity
     * @return true of the arities match.
     */
    public boolean isArity(int aArity) {
        return (arity() == aArity);
    }


    /**
     * Return the arity (number of arguments) of the function.
     *
     * @return the arity of the function
     */
    public int arity() {
        return iParameters.size();
    }


    /**
     *  Add a PredicateRule to the list of rules.
     * See: insertRule()
     * 
     * @param aPrecedence
     * @param aPredicate
     * @param aBody
     * @throws java.lang.Exception
     */
    public void defineSometimesTrueRule(int aStackTop, int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception {
        // New branching rule.
        PredicateRule newRule = new PredicateRule(iEnvironment, aPrecedence, aPredicate, aBody);
        LispError.check(iEnvironment, aStackTop, newRule != null, LispError.CREATING_RULE, "INTERNAL");

        insertRule(aPrecedence, newRule);
    }


    /**
     * Add a TrueRule to the list of rules.
     * See: insertRule()
     * 
     * @param aPrecedence
     * @param aBody
     * @throws java.lang.Exception
     */
    public void defineAlwaysTrueRule(int aStackTop, int aPrecedence, ConsPointer aBody) throws Exception {
        // New branching rule.
        PredicateRule newRule = new TrueRule(iEnvironment, aPrecedence, aBody);
        LispError.check(iEnvironment, aStackTop, newRule != null, LispError.CREATING_RULE, "INTERNAL");

        insertRule(aPrecedence, newRule);
    }


    /**
     *  Add a PatternRule to the list of rules.
     *  See: insertRule()
     * 
     * @param aPrecedence
     * @param aPredicate
     * @param aBody
     * @throws java.lang.Exception
     */
    public void definePattern(int aStackTop, int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception {
        // New branching rule.
        PatternRule newRule = new PatternRule(iEnvironment, aStackTop, aPrecedence, aPredicate, aBody);
        LispError.check(iEnvironment, aStackTop, newRule != null, LispError.CREATING_RULE, "INTERNAL");

        insertRule(aPrecedence, newRule);
    }


    /**
     * Insert any Rule object in the list of rules.
     * This function does the real work for defineAlwaysTrueRule() and
     * definePattern(): it inserts the rule in <b>iRules</b>, while
     * keeping it sorted. The algorithm is O(log n), where
     * n denotes the number of rules.
     * 
     * @param aPrecedence
     * @param newRule
     */
    void insertRule(int aNewRulePrecedence, Rule aNewRule) {
        // Find place to insert
        int low, high, mid;
        low = 0;
        high = iBranchRules.size();

        // Constant time: find out if the precedence is before any of the
        // currently defined rules or past them.
        if (high > 0) {
            if (((Rule) iBranchRules.get(0)).getPrecedence() > aNewRulePrecedence) {
                mid = 0;
                // Insert it
                iBranchRules.add(mid, aNewRule);
                return;
            }
            if (((Rule) iBranchRules.get(high - 1)).getPrecedence() < aNewRulePrecedence) {
                mid = high;
                // Insert it
                iBranchRules.add(mid, aNewRule);
                return;
            }
        }

        // Otherwise, O(log n) search algorithm for place to insert
        while(true) {
            if (low >= high) {
                //Insert it.
                mid = low;
                iBranchRules.add(mid, aNewRule);
                return;
            }


            mid = (low + high) >> 1;

            Rule existingRule = (Rule) iBranchRules.get(mid);

            int existingRulePrecedence = existingRule.getPrecedence();

            if (existingRulePrecedence > aNewRulePrecedence) {
                high = mid;
            } else if (existingRulePrecedence < aNewRulePrecedence) {
                low = (++mid);
            } else {

                //existingRule.
                //Insert it.
                iBranchRules.add(mid, aNewRule);
                return;
            }
        }
    }


    /**
     * Return the argument list, stored in #iParameterList.
     * 
     * @return a ConsPointer
     */
    public ConsPointer argList() {
        return iParameterList;
    }


    public Iterator getRules() {
        return iBranchRules.iterator();
    }


    public Iterator getParameters() {
        return iParameters.iterator();
    }


    public void unFence() {
        iFenced = false;
    }


    public boolean fenced() {
        return iFenced;
    }

}//end class.

