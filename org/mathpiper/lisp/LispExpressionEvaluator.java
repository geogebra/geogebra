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

import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.builtin.BuiltinFunctionEvaluator;
import org.mathpiper.lisp.rulebases.MultipleArityRulebase;

import org.mathpiper.lisp.rulebases.SingleArityRulebase;

/**
 *  The basic evaluator for Lisp expressions.
 *
 */
public class LispExpressionEvaluator extends Evaluator {

    /**
     * <p>
     * First, the evaluation depth is checked. An error is raised if the maximum evaluation
     * depth is exceeded.  The next step is the actual evaluation.  aExpression is a
     * Cons, so we can distinguish three cases:</p>
     * <ol>
     * <li value="1"><p>
     * If  aExpression is a string starting with " , it is
     * simply copied in aResult. If it starts with another
     * character (this includes the case where it represents a
     * number), the environment is checked to see whether a
     * variable with this name exists. If it does, its value is
     * copied in aResult, otherwise aExpression is copied.</p>
     *
     * <li value="2"><p>
     * If aExpression is a list, the head of the list is
     * examined. If the head is not a string. ApplyFast()
     * is called. If the head is a string, it is checked against
     * the core commands (if there is a check, the corresponding
     * evaluator is called). Then it is checked agaist the list of
     * user function with getRulebase().   Again, the
     * corresponding evaluator is called if there is a check. If
     * all fails, ReturnUnEvaluated() is called.</p>
     * <li value="3"><p>
     * Otherwise (ie. if aExpression is a getJavaObject object), it is
     * copied in aResult.</p>
     * </ol>
     *
     * <p>
     * Note: The result of this operation must be a unique (copied)
     * element! Eg. its Next might be set...</p>
     *
     * @param aEnvironment  the Lisp environment, in which the evaluation should take place
     * @param aResult             the result of the evaluation
     * @param aExpression     the expression to evaluate
     * @throws java.lang.Exception
     */
    public void evaluate(Environment aEnvironment, int aStackTop, ConsPointer aResult, ConsPointer aExpression) throws Exception {

        LispError.lispAssert(aExpression.getCons() != null, aEnvironment, aStackTop);
        synchronized (aEnvironment) {
            aEnvironment.iEvalDepth++;
            if (aEnvironment.iEvalDepth >= aEnvironment.iMaxEvalDepth) {
                /* if (aEnvironment.iEvalDepth > aEnvironment.iMaxEvalDepth + 20) {
                LispError.check(aEnvironment, aStackTop, aEnvironment.iEvalDepth < aEnvironment.iMaxEvalDepth, LispError.USER_INTERRUPT, "INTERNAL");
                } else {*/
                LispError.check(aEnvironment, aStackTop, aEnvironment.iEvalDepth < aEnvironment.iMaxEvalDepth, LispError.MAXIMUM_RECURSE_DEPTH_REACHED, "INTERNAL");
                // }
            }

            if (Thread.interrupted()) {
                LispError.raiseError("User halted calculation.", "", aStackTop, aEnvironment);
            }
        }



        // evaluate an atom: find the bound value (treat it as a variable)
        if (aExpression.car() instanceof String) {
            String str = (String) aExpression.car();
            if (str.charAt(0) == '\"') {
                aResult.setCons(aExpression.getCons().copy(aEnvironment, false));
                aEnvironment.iEvalDepth--;
                return;
            }

            ConsPointer val = new ConsPointer();
            aEnvironment.getGlobalVariable(aStackTop, str, val);
            if (val.getCons() != null) {
                aResult.setCons(val.getCons().copy(aEnvironment, false));
                aEnvironment.iEvalDepth--;
                return;
            }
            aResult.setCons(aExpression.getCons().copy(aEnvironment, false));
            aEnvironment.iEvalDepth--;
            return;
        }
        {


            if (aExpression.car() instanceof ConsPointer) {
                ConsPointer subList = (ConsPointer) aExpression.car();
                Cons head = subList.getCons();
                if (head != null) {

                    String functionName;

                    if (head.car() instanceof String) {

                        functionName = (String) head.car();

                        //Built-in function handler.
                        BuiltinFunctionEvaluator builtinInFunctionEvaluator = (BuiltinFunctionEvaluator) aEnvironment.getBuiltinFunctions().lookUp(functionName);
                        if (builtinInFunctionEvaluator != null) {
                            builtinInFunctionEvaluator.evaluate(aEnvironment, aStackTop, aResult, subList);
                            aEnvironment.iEvalDepth--;
                            return;
                        }

                        //User function handler.
                        SingleArityRulebase userFunction;
                        userFunction = getUserFunction(aEnvironment, aStackTop, subList);
                        if (userFunction != null) {
                            userFunction.evaluate(aEnvironment, aStackTop, aResult, subList);
                            aEnvironment.iEvalDepth--;
                            return;
                        }


                    } else {
                        //Pure function handler.
                        ConsPointer operator = new ConsPointer();
                        ConsPointer args2 = new ConsPointer();
                        operator.setCons(subList.getCons());
                        args2.setCons(subList.cdr().getCons());
                        Utility.applyPure(aStackTop, operator, args2, aResult, aEnvironment);
                        aEnvironment.iEvalDepth--;
                        return;
                    }
                    //printf("**** Undef: %s\n",head.String().String());


                    /*  todo:tk: This code is for experimenting with having non-existent functions throw an exception when they are called.
                    if (functionName.equals("_")) {
                        Utility.returnUnEvaluated(aStackTop, aResult, subList, aEnvironment);
                        aEnvironment.iEvalDepth--;
                        return;
                    } else {
                        LispError.raiseError("The function " + functionName + " is not defined.\n", null, aStackTop, aEnvironment );
                    }*/


                    Utility.returnUnEvaluated(aStackTop, aResult, subList, aEnvironment);

                    aEnvironment.iEvalDepth--;

                    return;




                }
            }
            aResult.setCons(aExpression.getCons().copy(aEnvironment, false));
        }
        aEnvironment.iEvalDepth--;
    }


    SingleArityRulebase getUserFunction(Environment aEnvironment, int aStackTop, ConsPointer subList) throws Exception {
        Cons head = subList.getCons();
        SingleArityRulebase userFunc = null;

        userFunc = (SingleArityRulebase) aEnvironment.getRulebase(aStackTop, subList);
        if (userFunc != null) {
            return userFunc;
        } else if (head.car() instanceof String) {
            MultipleArityRulebase multiUserFunc = aEnvironment.getMultipleArityRulebase(aStackTop, (String) head.car(), true);
            if (multiUserFunc.iFileToOpen != null) {
                DefFile def = multiUserFunc.iFileToOpen;

                if (DEBUG) {
                    /*Show loading... */

                    if (VERBOSE_DEBUG) {
                        /*char buf[1024];
                        #ifdef HAVE_VSNPRINTF
                        snprintf(buf,1024,"Debug> Loading file %s for function %s\n",def.iFileName.c_str(),head.String().c_str());
                        #else
                        sprintf(buf,      "Debug> Loading file %s for function %s\n",def.iFileName.c_str(),head.String().c_str());
                        #endif
                        aEnvironment.write(buf);*/
                        if (TRACE_TO_STANDARD_OUT) {
                            System.out.print("Debug> Loading file" + def.iFileName + " for function " + head.car() + "\n");
                        } else {
                            aEnvironment.write("Debug> Loading file" + def.iFileName + " for function " + head.car() + "\n");
                        }

                        int debugBreakpoint = 0;
                    }
                }



                multiUserFunc.iFileToOpen = null;
                Utility.loadScriptOnce(aEnvironment, aStackTop, def.iFileName);

                if (DEBUG) {
                    //extern int VERBOSE_DEBUG;
                    if (VERBOSE_DEBUG) {
                        /*
                        char buf[1024];
                        #ifdef HAVE_VSNPRINTF
                        snprintf(buf,1024,"Debug> Finished loading file %s\n",def.iFileName.c_str());
                        #else
                        sprintf(buf,      "Debug> Finished loading file %s\n",def.iFileName.c_str());
                        #endif*/

                        if (TRACE_TO_STANDARD_OUT) {
                            System.out.print("Debug> Finished loading file " + def.iFileName + "\n");
                        } else {
                            aEnvironment.write("Debug> Finished loading file " + def.iFileName + "\n");
                        }

                    }
                }
            }
            userFunc = aEnvironment.getRulebase(aStackTop, subList);
        }
        return userFunc;
    }//end method.
    /*
    void TracedStackEvaluator::PushFrame()
    {
    UserStackInformation *op = NEW UserStackInformation;
    objs.Append(op);
    }

    void TracedStackEvaluator::PopFrame()
    {
    LISPASSERT (objs.Size() > 0);
    if (objs[objs.Size()-1])
    {
    delete objs[objs.Size()-1];
    objs[objs.Size()-1] = null;
    }
    objs.Delete(objs.Size()-1);
    }

    void TracedStackEvaluator::ResetStack()
    {
    while (objs.Size()>0)
    {
    PopFrame();
    }
    }

    UserStackInformation& TracedStackEvaluator::StackInformation()
    {
    return *(objs[objs.Size()-1]);
    }

    TracedStackEvaluator::~TracedStackEvaluator()
    {
    ResetStack();
    }

    void TracedStackEvaluator::ShowStack(Environment aEnvironment, LispOutput& aOutput)
    {
    LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);

    LispInt i;
    LispInt from=0;
    LispInt upto = objs.Size();

    for (i=from;i<upto;i++)
    {
    LispChar str[20];
    #ifdef YACAS_DEBUG
    aEnvironment.write(objs[i].iFileName);
    aEnvironment.write("(");
    InternalIntToAscii(str,objs[i].iLine);
    aEnvironment.write(str);
    aEnvironment.write(") : ");
    aEnvironment.write("Debug> ");
    #endif
    InternalIntToAscii(str,i);
    aEnvironment.write(str);
    aEnvironment.write(": ");
    aEnvironment.CurrentPrinter().Print(objs[i].iOperator, *aEnvironment.CurrentOutput(),aEnvironment);

    LispInt internal;
    internal = (null != aEnvironment.CoreCommands().LookUp(objs[i].iOperator.String()));
    if (internal)
    {
    aEnvironment.write(" (Internal function) ");
    }
    else
    {
    if (objs[i].iRulePrecedence>=0)
    {
    aEnvironment.write(" (Rule # ");
    InternalIntToAscii(str,objs[i].iRulePrecedence);
    aEnvironment.write(str);
    if (objs[i].iSide)
    aEnvironment.write(" in body) ");
    else
    aEnvironment.write(" in pattern) ");
    }
    else
    aEnvironment.write(" (User function) ");
    }
    if (!!objs[i].iExpression)
    {
    aEnvironment.write("\n      ");
    if (aEnvironment.iEvalDepth>(aEnvironment.iMaxEvalDepth-10))
    {
    LispString expr;
    PrintExpression(expr, objs[i].iExpression,aEnvironment,60);
    aEnvironment.write(expr.c_str());
    }
    else
    {
    LispPtr getSubList = objs[i].iExpression.SubList();
    if (!!getSubList && !!getSubList)
    {
    LispString expr;
    LispPtr out(objs[i].iExpression);
    PrintExpression(expr, out,aEnvironment,60);
    aEnvironment.write(expr.c_str());
    }
    }
    }
    aEnvironment.write("\n");
    }
    }

    void TracedStackEvaluator::Eval(Environment aEnvironment, ConsPointer aResult,
    ConsPointer aExpression)
    {
    if (aEnvironment.iEvalDepth>=aEnvironment.iMaxEvalDepth)
    {
    ShowStack(aEnvironment, *aEnvironment.CurrentOutput());
    CHK2(aEnvironment.iEvalDepth<aEnvironment.iMaxEvalDepth,
    MAXIMUM_RECURSE_DEPTH_REACHED);
    }

    LispPtr getSubList = aExpression.SubList();
    LispString * str = null;
    if (getSubList)
    {
    Cons head = getSubList;
    if (head)
    {
    str = head.String();
    if (str)
    {
    PushFrame();
    UserStackInformation& st = StackInformation();
    st.iOperator = (LispAtom::New(aEnvironment,str.c_str()));
    st.iExpression = (aExpression);
    #ifdef YACAS_DEBUG
    if (aExpression.iFileName)
    {
    st.iFileName = aExpression.iFileName;
    st.iLine = aExpression.iLine;
    }
    #endif
    }
    }
    }
    BasicEvaluator::Eval(aEnvironment, aResult, aExpression);
    if (str)
    {
    PopFrame();
    }
    }

    void TracedEvaluator::Eval(Environment aEnvironment, ConsPointer aResult,
    ConsPointer aExpression)
    {
    if(!aEnvironment.iDebugger) RaiseError("Internal error: debugging failing");
    if(aEnvironment.iDebugger.Stopped()) RaiseError("");

    REENTER:
    errorStr.ResizeTo(1); errorStr[0] = '\0';
    LispTrap(aEnvironment.iDebugger.Enter(aEnvironment, aExpression),errorOutput,aEnvironment);
    if(aEnvironment.iDebugger.Stopped()) RaiseError("");
    if (errorStr[0])
    {
    aEnvironment.write(errorStr.c_str());
    aEnvironment.iEvalDepth=0;
    goto REENTER;
    }

    errorStr.ResizeTo(1); errorStr[0] = '\0';
    LispTrap(BasicEvaluator::Eval(aEnvironment, aResult, aExpression),errorOutput,aEnvironment);

    if (errorStr[0])
    {
    aEnvironment.write(errorStr.c_str());
    aEnvironment.iEvalDepth=0;
    aEnvironment.iDebugger.Error(aEnvironment);
    goto REENTER;
    }

    if(aEnvironment.iDebugger.Stopped()) RaiseError("");

    aEnvironment.iDebugger.Leave(aEnvironment, aResult, aExpression);
    if(aEnvironment.iDebugger.Stopped()) RaiseError("");
    }

    YacasDebuggerBase::~YacasDebuggerBase()
    {
    }

    void DefaultDebugger::Start()
    {
    }

    void DefaultDebugger::Finish()
    {
    }

    void DefaultDebugger::Enter(Environment aEnvironment,
    ConsPointer aExpression)
    {
    LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);
    iTopExpr = (aExpression.Copy());
    LispPtr result;
    defaultEval.Eval(aEnvironment, result, iEnter);
    }

    void DefaultDebugger::Leave(Environment aEnvironment, ConsPointer aResult,
    ConsPointer aExpression)
    {
    LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);
    LispPtr result;
    iTopExpr = (aExpression.Copy());
    iTopResult = (aResult);
    defaultEval.Eval(aEnvironment, result, iLeave);
    }

    LispBoolean DefaultDebugger::Stopped()
    {
    return iStopped;
    }

    void DefaultDebugger::Error(Environment aEnvironment)
    {
    LispLocalEvaluator local(aEnvironment,NEW BasicEvaluator);
    LispPtr result;
    defaultEval.Eval(aEnvironment, result, iException);
    }


     */

}//end class.

