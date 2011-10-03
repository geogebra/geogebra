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
package org.mathpiper.lisp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mathpiper.lisp.stacks.ArgumentStack;
import org.mathpiper.lisp.collections.DefFileMap;
import org.mathpiper.lisp.collections.MathPiperMap;
import org.mathpiper.lisp.collections.TokenMap;
import org.mathpiper.lisp.collections.OperatorMap;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.printers.LispPrinter;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.tokenizers.XmlTokenizer;
import org.mathpiper.io.InputStatus;

import org.mathpiper.io.InputDirectories;

import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;

import org.mathpiper.lisp.rulebases.MultipleArityRulebase;

import org.mathpiper.lisp.rulebases.MacroRulebase;


import org.mathpiper.lisp.rulebases.ListedRulebase;

import org.mathpiper.lisp.rulebases.SingleArityRulebase;

import org.mathpiper.lisp.rulebases.ListedMacroRulebase;

import org.mathpiper.lisp.printers.MathPiperPrinter;

import org.mathpiper.lisp.localvariables.LocalVariable;
import org.mathpiper.lisp.localvariables.LocalVariableFrame;

public final class Environment {

    public Evaluator iLispExpressionEvaluator = new LispExpressionEvaluator();
    private int iPrecision = 10;
    private TokenMap iTokenHash = new TokenMap();
    public Cons iTrueAtom;
    public final String iTrueString;
    public Cons iFalseAtom;
    public final String iFalseString;
    public Cons iEndOfFileAtom;
    public Cons iEndStatementAtom;
    public Cons iProgOpenAtom;
    public Cons iProgCloseAtom;
    public Cons iNthAtom;
    public Cons iComplexAtom;
    public Cons iBracketOpenAtom;
    public Cons iBracketCloseAtom;
    public Cons iListOpenAtom;
    public Cons iListCloseAtom;
    public Cons iCommaAtom;
    public Cons iListAtom;
    public Cons iSetAtom;
    public Cons iProgAtom;
    public OperatorMap iPrefixOperators = new OperatorMap(this);
    public OperatorMap iInfixOperators = new OperatorMap(this);
    public OperatorMap iPostfixOperators = new OperatorMap(this);
    public OperatorMap iBodiedOperators = new OperatorMap(this);
    public volatile int iEvalDepth = 0;
    public int iMaxEvalDepth = 10000;
    //TODO FIXME
    public ArgumentStack iArgumentStack;
    public LocalVariableFrame iLocalVariablesFrame;
    public boolean iSecure = false;
    public int iLastUniqueId = 1;
    public MathPiperOutputStream iCurrentOutput = null;
    public MathPiperOutputStream iInitialOutput = null;
    public LispPrinter iCurrentPrinter = null;
    public MathPiperInputStream iCurrentInput = null;
    public InputStatus iInputStatus = new InputStatus();
    public MathPiperTokenizer iCurrentTokenizer;
    public MathPiperTokenizer iDefaultTokenizer = new MathPiperTokenizer();
    public MathPiperTokenizer iXmlTokenizer = new XmlTokenizer();
    public MathPiperMap iGlobalState = new MathPiperMap();
    public MathPiperMap iUserRules = new MathPiperMap();
    MathPiperMap iBuiltinFunctions = new MathPiperMap();
    public Throwable iException = null;
    public DefFileMap iDefFiles = new DefFileMap();
    public InputDirectories iInputDirectories = new InputDirectories();
    public String iPrettyReaderName = null;
    public String iPrettyPrinterName = null;

    public Environment(MathPiperOutputStream aCurrentOutput/*TODO FIXME*/) throws Exception {
        iCurrentTokenizer = iDefaultTokenizer;
        iInitialOutput = aCurrentOutput;
        iCurrentOutput = aCurrentOutput;
        iCurrentPrinter = new MathPiperPrinter(iPrefixOperators, iInfixOperators, iPostfixOperators, iBodiedOperators);

        iTrueAtom = new AtomCons((String)getTokenHash().lookUp("True"));
        iTrueString = (String) iTrueAtom.car();
        iFalseAtom = new AtomCons((String)getTokenHash().lookUp("False"));
        iFalseString = (String) iFalseAtom.car();
        iEndOfFileAtom = new AtomCons((String)getTokenHash().lookUp("EndOfFile"));
        iEndStatementAtom = new AtomCons((String)getTokenHash().lookUp(";"));
        iProgOpenAtom = new AtomCons((String)getTokenHash().lookUp("["));
        iProgCloseAtom = new AtomCons((String)getTokenHash().lookUp("]"));
        iNthAtom = new AtomCons((String)getTokenHash().lookUp("Nth"));
        iComplexAtom = new AtomCons((String)getTokenHash().lookUp("Complex"));
        iBracketOpenAtom = new AtomCons((String)getTokenHash().lookUp("("));
        iBracketCloseAtom = new AtomCons((String)getTokenHash().lookUp(")"));
        iListOpenAtom = new AtomCons((String)getTokenHash().lookUp("{"));
        iListCloseAtom = new AtomCons((String)getTokenHash().lookUp("}"));
        iCommaAtom = new AtomCons((String)getTokenHash().lookUp(","));
        iListAtom = new AtomCons((String)getTokenHash().lookUp("List"));
        iSetAtom = new AtomCons((String)getTokenHash().lookUp("Set"));
        iProgAtom = new AtomCons((String)getTokenHash().lookUp("Prog"));

        iArgumentStack = new ArgumentStack(this, 50000 /*TODO FIXME*/);
        //org.mathpiper.builtin.Functions mc = new org.mathpiper.builtin.Functions();
        //mc.addCoreFunctions(this);

        //System.out.println("Classpath: " + System.getProperty("java.class.path"));
        
    }

    public TokenMap getTokenHash() {
        return iTokenHash;
    }

    public MathPiperMap getGlobalState() {
        return iGlobalState;
    }

    public MathPiperMap getUserFunctions() {
        return iUserRules;
    }

    public MathPiperMap getBuiltinFunctions() {
        return iBuiltinFunctions;
    }

    public int getPrecision() {
        return iPrecision;
    }

    public void setPrecision(int aPrecision) throws Exception {
        iPrecision = aPrecision;    // getPrecision in decimal digits
    }

    public void setGlobalVariable(int aStackTop, String aVariable, ConsPointer aValue, boolean aGlobalLazyVariable) throws Exception {
        ConsPointer localVariable = getLocalVariable(aStackTop, aVariable);
        if (localVariable != null) {
            localVariable.setCons(aValue.getCons());
            return;
        }
        GlobalVariable globalVariable = new GlobalVariable(this,aValue);
        iGlobalState.setAssociation(globalVariable, aVariable);
        if (aGlobalLazyVariable) {
            globalVariable.setEvalBeforeReturn(true);
        }
    }

    public void getGlobalVariable(int aStackTop, String aVariable, ConsPointer aResult) throws Exception {
        aResult.setCons(null);
        ConsPointer localVariable = getLocalVariable(aStackTop, aVariable);
        if (localVariable != null) {
            aResult.setCons(localVariable.getCons());
            return;
        }
        GlobalVariable globalVariable = (GlobalVariable) iGlobalState.lookUp(aVariable);
        if (globalVariable != null) {
            if (globalVariable.iEvalBeforeReturn) {
                iLispExpressionEvaluator.evaluate(this, aStackTop, aResult, globalVariable.iValue);
                globalVariable.iValue.setCons(aResult.getCons());
                globalVariable.iEvalBeforeReturn = false;
                return;
            } else {
                aResult.setCons(globalVariable.iValue.getCons());
                return;
            }
        }
    }


    public ConsPointer getLocalVariable(int aStackTop, String aVariable) throws Exception {
        LispError.check(this, aStackTop, iLocalVariablesFrame != null, LispError.INVALID_STACK, "INTERNAL");
        //    check(iLocalsList.iFirst != null,INVALID_STACK);
        LocalVariable localVariable = iLocalVariablesFrame.iFirst;

        while (localVariable != null) {
            if (localVariable.iVariable.equals(aVariable)) {
                return localVariable.iValue;
            }
            localVariable = localVariable.iNext;
        }
        return null;
    }//end method.



    public void unbindAllLocalVariables(int aStackTop) throws Exception{
        LispError.check(this, aStackTop, iLocalVariablesFrame != null, LispError.INVALID_STACK, "INTERNAL");

        LocalVariable localVariable = iLocalVariablesFrame.iFirst;

        while (localVariable != null) {
            localVariable.iValue.setCons(null);
            localVariable = localVariable.iNext;
        }
        
    }//end method.


    public String getLocalVariables(int aStackTop) throws Exception {
        LispError.check(this, aStackTop, iLocalVariablesFrame != null, LispError.INVALID_STACK, "INTERNAL");
        //    check(iLocalsList.iFirst != null,INVALID_STACK);

        LocalVariable localVariable = iLocalVariablesFrame.iFirst;

        StringBuilder localVariablesStringBuilder = new StringBuilder();

        localVariablesStringBuilder.append("Local variables: ");

        while (localVariable != null) {
            localVariablesStringBuilder.append(localVariable.iVariable);

            localVariablesStringBuilder.append(" -> ");

            String value = localVariable.iValue.toString();
            if(value != null)
            {
                localVariablesStringBuilder.append(value.trim().replace("  ","").replace("\n", "") );
            }
            else
            {
                localVariablesStringBuilder.append("unbound");
            }//end else.

            localVariablesStringBuilder.append(", ");

            localVariable = localVariable.iNext;
        }//end while.

        return localVariablesStringBuilder.toString();

    }//end method.


    public String dumpLocalVariablesFrame(int aStackTop) throws Exception {

        LispError.check(this, aStackTop, iLocalVariablesFrame != null, LispError.INVALID_STACK, "INTERNAL");

        LocalVariableFrame localVariableFramePointer = iLocalVariablesFrame;

        StringBuilder stringBuilder = new StringBuilder();



        int functionPositionIndex = 0;

        //int functionBaseIndex = 0;

        while (localVariableFramePointer != null) {

            String functionName = localVariableFramePointer.getFunctionName();


            if(functionPositionIndex == 0)
            {
                stringBuilder.append("\n\n========================================= Start Of User Function Stack Trace\n");
            }
            else
            {
                stringBuilder.append("-----------------------------------------\n");
            }


            stringBuilder.append(functionPositionIndex++ + ": ");
            stringBuilder.append(functionName);
            stringBuilder.append("\n");

            LocalVariable localVariable = localVariableFramePointer.iFirst;


            //stringBuilder.append("Local variables: ");


            while (localVariable != null) {


                stringBuilder.append("   " + functionPositionIndex++ + ": -> ");

                stringBuilder.append(localVariable.iVariable);

                stringBuilder.append(" = ");

                ConsPointer valuePointer = localVariable.iValue;

                String valueString = Utility.printMathPiperExpression(aStackTop, valuePointer, this, -1);

                stringBuilder.append(valueString);

                stringBuilder.append("\n");




                /*if(value != null)
                {
                    localVariablesStringBuilder.append(value.trim().replace("  ","").replace("\n", "") );
                }
                else
                {
                    localVariablesStringBuilder.append("unbound");
                }//end else.


                localVariablesStringBuilder.append(", ");*/

                localVariable = localVariable.iNext;
            }//end while.

            localVariableFramePointer = localVariableFramePointer.iNext;

        }//end while

        stringBuilder.append("========================================= End Of User Function Stack Trace\n\n");

        return stringBuilder.toString();




        /*StringBuilder stringBuilder = new StringBuilder();

        int functionBaseIndex = 0;

        int functionPositionIndex = 0;


        while (functionBaseIndex <= aStackTop) {

            if(functionBaseIndex == 0)
            {
                stringBuilder.append("\n\n========================================= Start Of Stack Trace\n");
            }
            else
            {
                stringBuilder.append("-----------------------------------------\n");
            }

            ConsPointer consPointer = getElement(functionBaseIndex, aStackTop, aEnvironment);

            int argumentCount = Utility.listLength(aEnvironment, aStackTop, consPointer);

            ConsPointer argumentPointer = new ConsPointer();

            Object car = consPointer.getCons().car();

            ConsPointer consTraverser = new ConsPointer( consPointer.getCons());

            stringBuilder.append(functionPositionIndex++ + ": ");
            stringBuilder.append(Utility.printMathPiperExpression(aStackTop, consTraverser, aEnvironment, -1));
            stringBuilder.append("\n");

            consTraverser.goNext(aStackTop, aEnvironment);

            while(consTraverser.getCons() != null)
            {
                stringBuilder.append("   " + functionPositionIndex++ + ": ");
                stringBuilder.append("-> " + Utility.printMathPiperExpression(aStackTop, consTraverser, aEnvironment, -1));
                stringBuilder.append("\n");

                consTraverser.goNext(aStackTop, aEnvironment);
            }


            functionBaseIndex = functionBaseIndex + argumentCount;

        }//end while.

        stringBuilder.append("========================================= End Of User Function Stack Trace\n\n");

        return stringBuilder.toString();*/

    }//end method.

    public void unbindVariable(int aStackTop, String aVariableName) throws Exception {

        if(aVariableName.equals("*"))
        {
            this.unbindAllLocalVariables(aStackTop);


            //Unbind global variables
            Set<String> keySet = new HashSet(iGlobalState.getMap().keySet());

            for(String key : keySet)
            {
                if(!key.startsWith("$") 
			&& !key.equals("I") 
			&& !key.equals("%") 
			&& !key.equals("geogebra"))
                {
                    //Do not unbind private variables (which are those which start with a $) or the other listed variables.
                    iGlobalState.release(key);
                }
            }
        }
        else
        {
            //Unbind local variable.
            ConsPointer localVariable = getLocalVariable(aStackTop, aVariableName);
            if (localVariable != null) {
                localVariable.setCons(null);
                return;
            }

            iGlobalState.release(aVariableName);
        }//end else.

    }

    public void newLocalVariable(String aVariable, Cons aValue, int aStackTop) throws Exception {
        LispError.lispAssert(iLocalVariablesFrame != null, this, aStackTop);
        iLocalVariablesFrame.add(new LocalVariable(this, aVariable, aValue));
    }

    public void pushLocalFrame(boolean aFenced, String functionName) {
        if (aFenced) {
            LocalVariableFrame newLocalVariableFrame = new LocalVariableFrame(iLocalVariablesFrame, null, functionName);
            iLocalVariablesFrame = newLocalVariableFrame;
        } else {
            LocalVariableFrame newLocalVariableFrame = new LocalVariableFrame(iLocalVariablesFrame, iLocalVariablesFrame.iFirst, functionName);
            iLocalVariablesFrame = newLocalVariableFrame;
        }
    }

    public void popLocalFrame(int aStackTop) throws Exception {
        LispError.lispAssert(iLocalVariablesFrame != null, this, aStackTop);
        LocalVariableFrame nextLocalVariableFrame = iLocalVariablesFrame.iNext;
        iLocalVariablesFrame.delete();
        iLocalVariablesFrame = nextLocalVariableFrame;
    }

    public int getUniqueId() {
        return iLastUniqueId++;
    }

    public void holdArgument(int aStackTop, String aOperator, String aVariable, Environment aEnvironment) throws Exception {
        MultipleArityRulebase multipleArityUserFunc = (MultipleArityRulebase) iUserRules.lookUp(aOperator);
        LispError.check(this, aStackTop, multipleArityUserFunc != null, LispError.INVALID_ARGUMENT, "INTERNAL");
        multipleArityUserFunc.holdArgument(aVariable, aStackTop, aEnvironment);
    }

    public void retractRule(String aOperator, int aArity, int aStackTop, Environment aEnvironment) throws Exception {
        MultipleArityRulebase multipleArityUserFunc = (MultipleArityRulebase) iUserRules.lookUp(aOperator);
        if (multipleArityUserFunc != null) {
            multipleArityUserFunc.deleteRulebaseEntry(aArity, aStackTop, aEnvironment);
        }
    }

    public SingleArityRulebase getRulebase(int aStackTop, ConsPointer aArguments) throws Exception {
        MultipleArityRulebase multipleArityUserFunc = (MultipleArityRulebase) iUserRules.lookUp( (String) aArguments.car());
        if (multipleArityUserFunc != null) {
            int arity = Utility.listLength(this, aStackTop, aArguments) - 1;
            return multipleArityUserFunc.getUserFunction(arity, aStackTop, this);
        }
        return null;
    }

    public SingleArityRulebase getRulebase(String aName, int aArity, int aStackTop) throws Exception {
        MultipleArityRulebase multipleArityUserFunc = (MultipleArityRulebase) iUserRules.lookUp(aName);
        if (multipleArityUserFunc != null) {
            return multipleArityUserFunc.getUserFunction(aArity, aStackTop, this);
        }
        return null;
    }

    public void unfenceRule(int aStackTop, String aOperator, int aArity) throws Exception {
        MultipleArityRulebase multiUserFunc = (MultipleArityRulebase) iUserRules.lookUp(aOperator);

        LispError.check(this, aStackTop, multiUserFunc != null, LispError.INVALID_ARGUMENT, "INTERNAL");
        SingleArityRulebase userFunc = multiUserFunc.getUserFunction(aArity, aStackTop, this);
        LispError.check(this, aStackTop, userFunc != null, LispError.INVALID_ARGUMENT, "INTERNAL");
        userFunc.unFence();
    }

    public MultipleArityRulebase getMultipleArityRulebase(int aStackTop, String aOperator, boolean create) throws Exception {
        // Find existing multiuser func.  Todo:tk:a user function name is added to the list even if a non-existing function
        // is being executed or looked for by FindFunction();
        MultipleArityRulebase multipleArityUserFunction = (MultipleArityRulebase) iUserRules.lookUp(aOperator);

        // If none exists, add one to the user functions list
        if (multipleArityUserFunction == null && create == true) {
            MultipleArityRulebase newMultipleArityUserFunction = new MultipleArityRulebase();
            iUserRules.setAssociation(newMultipleArityUserFunction, aOperator);
            multipleArityUserFunction = (MultipleArityRulebase) iUserRules.lookUp(aOperator);
            LispError.check(this, aStackTop, multipleArityUserFunction != null, LispError.CREATING_USER_FUNCTION, "INTERNAL");
        }
        return multipleArityUserFunction;
    }

    public void defineRulebase(int aStackTop, String aOperator, ConsPointer aParametersPointer, boolean aListed) throws Exception {
        MultipleArityRulebase multipleArityUserFunction = getMultipleArityRulebase(aStackTop, aOperator, true);

        // add an operator with this arity to the multiuserfunc.
        SingleArityRulebase newBranchingRulebase;
        if (aListed) {
            newBranchingRulebase = new ListedRulebase(this, aStackTop, aParametersPointer, aOperator);
        } else {
            newBranchingRulebase = new SingleArityRulebase(this, aStackTop, aParametersPointer, aOperator);
        }
        multipleArityUserFunction.addRulebaseEntry(this, aStackTop, newBranchingRulebase);
    }

    public void defineRule(int aStackTop, String aOperator, int aArity, int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception {
        // Find existing multiuser rule.
        MultipleArityRulebase multipleArityRulebase = (MultipleArityRulebase) iUserRules.lookUp(aOperator);
        LispError.check(this, aStackTop, multipleArityRulebase != null, LispError.CREATING_RULE, "INTERNAL");

        // Get the specific user function with the right arity
        SingleArityRulebase rulebase = (SingleArityRulebase) multipleArityRulebase.getUserFunction(aArity, aStackTop, this);
        LispError.check(this, aStackTop, rulebase != null, LispError.CREATING_RULE, "INTERNAL");

        // Declare a new evaluation rule
        if (Utility.isTrue(this, aPredicate, aStackTop)) {
            //        printf("FastPredicate on %s\n",aOperator->String());
            rulebase.defineAlwaysTrueRule(aStackTop, aPrecedence, aBody);
        } else {
            rulebase.defineSometimesTrueRule(aStackTop, aPrecedence, aPredicate, aBody);
        }
    }

    public void defineMacroRulebase(int aStackTop, String aFunctionName, ConsPointer aParameters, boolean aListed) throws Exception {
        MultipleArityRulebase multipleArityRulebase = getMultipleArityRulebase(aStackTop, aFunctionName, true);

        MacroRulebase newMacroRulebase;

        if (aListed) {
            newMacroRulebase = new ListedMacroRulebase(this, aStackTop, aParameters, aFunctionName);
        } else {
            newMacroRulebase = new MacroRulebase(this, aStackTop, aParameters, aFunctionName);
        }
        multipleArityRulebase.addRulebaseEntry(this, aStackTop, newMacroRulebase);
    }

    public void defineRulePattern(int aStackTop, String aOperator, int aArity, int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) throws Exception {
        // Find existing multiuser rulebase.
        MultipleArityRulebase multipleArityRulebase = (MultipleArityRulebase) iUserRules.lookUp(aOperator);
        LispError.check(this, aStackTop, multipleArityRulebase != null, LispError.CREATING_RULE, "INTERNAL");

        // Get the specific user function with the right arity
        SingleArityRulebase rulebase = multipleArityRulebase.getUserFunction(aArity, aStackTop, this);
        LispError.check(this, aStackTop, rulebase != null, LispError.CREATING_RULE, "INTERNAL");

        // Declare a new evaluation rule
        rulebase.definePattern(aStackTop, aPrecedence, aPredicate, aBody);
    }

    /**
     * Write data to the current output.
     * @param aString
     * @throws java.lang.Exception
     */
    public void write(String aString) throws Exception {
        iCurrentOutput.write(aString);
    }



    public void resetArgumentStack(int aStackTop) throws Exception
    {
        this.iArgumentStack.reset(aStackTop, this);
    }//end method.




}//end class.

