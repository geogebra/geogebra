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
package org.mathpiper.lisp.printers;

import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.lisp.Operator;
import org.mathpiper.lisp.collections.OperatorMap;

public class MathPiperPrinter extends LispPrinter {

    StringBuilder spaces = new StringBuilder();
    public static int KMaxPrecedence = 60000;
    OperatorMap iPrefixOperators;
    OperatorMap iInfixOperators;
    OperatorMap iPostfixOperators;
    OperatorMap iBodiedOperators;
    char iPrevLastChar;
    Environment iCurrentEnvironment;

    //private List<Cons> visitedLists = new ArrayList<Cons>();
    public MathPiperPrinter(OperatorMap aPrefixOperators,
            OperatorMap aInfixOperators,
            OperatorMap aPostfixOperators,
            OperatorMap aBodiedOperators) {
        iPrefixOperators = aPrefixOperators;
        iInfixOperators = aInfixOperators;
        iPostfixOperators = aPostfixOperators;
        iBodiedOperators = aBodiedOperators;
        iPrevLastChar = 0;
    }

    @Override
    public void print(int aStackTop, ConsPointer aExpression,  MathPiperOutputStream aOutput, Environment aEnvironment) throws Exception {
        iCurrentEnvironment = aEnvironment;


        Print(aEnvironment, aStackTop, aExpression, aOutput, KMaxPrecedence);

        //visitedLists.clear();
    }

    @Override
    public void rememberLastChar(char aChar) {
        iPrevLastChar = aChar;
    }

    void Print(Environment aEnvironment, int aStackTop, ConsPointer aExpression, MathPiperOutputStream aOutput, int iPrecedence) throws Exception {

        LispError.lispAssert(aExpression.getCons() != null, aEnvironment, aStackTop);

        String functionOrOperatorName;
        if (aExpression.car() instanceof String) {
            functionOrOperatorName = (String) aExpression.car();
            boolean bracket = false;
            if (iPrecedence < KMaxPrecedence &&
                    functionOrOperatorName.charAt(0) == '-' &&
                    (MathPiperTokenizer.isDigit(functionOrOperatorName.charAt(1)) || functionOrOperatorName.charAt(1) == '.')) {
                //Code for (-1)/2 .
                bracket = true;
            }
            if (bracket) {
                WriteToken(aOutput, "(");
            }
            WriteToken(aOutput, functionOrOperatorName);
            if (bracket) {
                WriteToken(aOutput, ")");
            }
            return;
        }

        if (aExpression.car() instanceof BuiltinContainer) {
            //TODO display genericclass
            WriteToken(aOutput, ((BuiltinContainer) aExpression.car()).getObject().getClass().toString());
            return;
        }

        ConsPointer subList = (ConsPointer) aExpression.car();

        LispError.check(aEnvironment, aStackTop, subList != null, LispError.UNPRINTABLE_TOKEN, "INTERNAL");

        if (subList.getCons() == null) {
            WriteToken(aOutput, "( )");
        } else {
            int length = Utility.listLength(aEnvironment, aStackTop, subList);
            functionOrOperatorName = (String) subList.car();
            Operator prefix = (Operator) iPrefixOperators.lookUp(functionOrOperatorName);
            Operator infix = (Operator) iInfixOperators.lookUp(functionOrOperatorName);
            Operator postfix = (Operator) iPostfixOperators.lookUp(functionOrOperatorName);
            Operator bodied = (Operator) iBodiedOperators.lookUp(functionOrOperatorName);
            Operator operator = null;

            if (length != 2) {
                prefix = null;
                postfix = null;
            }
            if (length != 3) {
                infix = null;
            }
            if (prefix != null) {
                operator = prefix;
            }
            if (postfix != null) {
                operator = postfix;
            }
            if (infix != null) {
                operator = infix;
            }

            if (operator != null) {
                ConsPointer left = null;
                ConsPointer right = null;

                if (prefix != null) {
                    right = subList.cdr();
                } else if (infix != null) {
                    left = subList.cdr();
                    right = subList.cdr().cdr();
                } else if (postfix != null) {
                    left = subList.cdr();
                }

                if (iPrecedence < operator.iPrecedence) {
                    WriteToken(aOutput, "(");
                } else {
                    //Vladimir?    aOutput.write(" ");
                }

                if (left != null) {

                    if (functionOrOperatorName.equals("/") && Utility.functionType(left).equals("/")) {
                        //Code for In> Hold((3/2)/(1/2)) Result> (3/2)/(1/2) .
                        WriteToken(aOutput, "(");
                    }//end if.

                    Print(aEnvironment, aStackTop, left, aOutput, operator.iLeftPrecedence);

                    if (functionOrOperatorName.equals("/") && Utility.functionType(left).equals("/")) {
                        //Code for In> Hold((3/2)/(1/2)) Result> (3/2)/(1/2) .
                        WriteToken(aOutput, ")");
                    }//end if.
                }

                boolean addSpaceAroundInfixOperator = 
                    left != null && right != null && // is this really an infix operator?
                    functionOrOperatorName.length() > 1;// no spaces around +,-,*,/ etc

                if (addSpaceAroundInfixOperator)
                    WriteToken(aOutput, "  ");

                WriteToken(aOutput, functionOrOperatorName);

                if (addSpaceAroundInfixOperator)
                    WriteToken(aOutput, "  ");

                if (right != null) {

                    if (functionOrOperatorName.equals("/") && Utility.functionType(right).equals("/")) {
                        //Code for In> Hold((3/2)/(1/2)) Result> (3/2)/(1/2) .
                        WriteToken(aOutput, "(");
                    }//end if.

                    if (functionOrOperatorName.equals("Not")) {//Todo:tk:perhaps a more general way should be found to place a space after a prefix operator.
                        WriteToken(aOutput, " ");
                    }//end if.

                    Print(aEnvironment, aStackTop, right, aOutput, operator.iRightPrecedence);

                    if (functionOrOperatorName.equals("/") && Utility.functionType(right).equals("/")) {
                        //Code for In> Hold((3/2)/(1/2)) Result> (3/2)/(1/2) .
                        WriteToken(aOutput, ")");
                    }//end if.
                }

                if (iPrecedence < operator.iPrecedence) {
                    WriteToken(aOutput, ")");
                }

            } else {

                ConsTraverser consTraverser = new ConsTraverser(aEnvironment, subList.cdr());

               /*
                   Removing complex number output notation formatting until the problem with Solve(x^3 - 2*x - 7 == 0,x) is resolved.
                
                   if (functionOrOperatorName == iCurrentEnvironment.iComplexAtom.car()) {

                    Print(consTraverser.getPointer(), aOutput, KMaxPrecedence);

                    consTraverser.goNext(); //Point to second argument.

                    if (!consTraverser.car().toString().startsWith("-")) {
                        WriteToken(aOutput, "+");
                    }

                    Print(consTraverser.getPointer(), aOutput, KMaxPrecedence);

                    WriteToken(aOutput, "*I");

                } else */
                if (functionOrOperatorName == iCurrentEnvironment.iListAtom.car()) {

                    /*
                    Cons atomCons = (Cons) subList.getCons();
                    if (visitedLists.contains(atomCons)) {
                    WriteToken(aOutput, "{CYCLE_LIST}");
                    return;

                    } else {

                    visitedLists.add(atomCons);*/

                    WriteToken(aOutput, "{");

                    while (consTraverser.getCons() != null) {
                        Print(aEnvironment, aStackTop, consTraverser.getPointer(), aOutput, KMaxPrecedence);
                        consTraverser.goNext(aStackTop);
                        if (consTraverser.getCons() != null) {
                            WriteToken(aOutput, ",");
                        }
                    }//end while.

                    WriteToken(aOutput, "}");

                    // }//end else.
                } else if (functionOrOperatorName == iCurrentEnvironment.iProgAtom.car()) // Program block brackets.
                {
                    WriteToken(aOutput, "[");
                    aOutput.write("\n");
                    spaces.append("    ");

                    while (consTraverser.getCons() != null) {
                        aOutput.write(spaces.toString());
                        Print(aEnvironment, aStackTop, consTraverser.getPointer(), aOutput, KMaxPrecedence);
                        consTraverser.goNext(aStackTop);
                        WriteToken(aOutput, ";");
                        aOutput.write("\n");
                    }

                    WriteToken(aOutput, "]");
                    aOutput.write("\n");
                    spaces.delete(0, 4);
                } else if (functionOrOperatorName == iCurrentEnvironment.iNthAtom.car()) {
                    Print(aEnvironment, aStackTop, consTraverser.getPointer(), aOutput, 0);
                    consTraverser.goNext(aStackTop);
                    WriteToken(aOutput, "[");
                    Print(aEnvironment, aStackTop,  consTraverser.getPointer(), aOutput, KMaxPrecedence);
                    WriteToken(aOutput, "]");
                } else {
                    boolean bracket = false;
                    if (bodied != null) {
                        //printf("%d > %d\n",iPrecedence, bodied.iPrecedence);
                        if (iPrecedence < bodied.iPrecedence) {
                            bracket = true;
                        }
                    }
                    if (bracket) {
                        WriteToken(aOutput, "(");
                    }
                    if (functionOrOperatorName != null) {
                        WriteToken(aOutput, functionOrOperatorName); //Print function name.
                    } else {
                        Print(aEnvironment, aStackTop, subList, aOutput, 0);
                    }
                    WriteToken(aOutput, "("); //Print the opening parenthese of the function argument list.

                    ConsTraverser counter = new ConsTraverser(aEnvironment, consTraverser.getPointer());
                    int nr = 0;

                    while (counter.getCons() != null) { //Count arguments.
                        counter.goNext(aStackTop);
                        nr++;
                    }

                    if (bodied != null) {
                        nr--;
                    }
                    while (nr-- != 0) {
                        Print(aEnvironment, aStackTop, consTraverser.getPointer(), aOutput, KMaxPrecedence); //Print argument.

                        consTraverser.goNext(aStackTop);

                        if (nr != 0) {
                            WriteToken(aOutput, ","); //Print the comma which is between arguments.
                        }
                    }//end while.

                    WriteToken(aOutput, ")");

                    if (consTraverser.getCons() != null) {
                        Print(aEnvironment, aStackTop, consTraverser.getPointer(), aOutput, bodied.iPrecedence);
                    }

                    if (bracket) {
                        WriteToken(aOutput, ")"); //Print the closing parenthese of the function argument list.
                    }
                }
            }
        }//end sublist if.
    }

    void WriteToken(MathPiperOutputStream aOutput, String aString) throws Exception {
        /*if (MathPiperTokenizer.isAlNum(iPrevLastChar) && (MathPiperTokenizer.isAlNum(aString.charAt(0)) || aString.charAt(0)=='_'))
        {
        aOutput.write(" ");
        }
        else if (MathPiperTokenizer.isSymbolic(iPrevLastChar) && MathPiperTokenizer.isSymbolic(aString.charAt(0)))
        {
        aOutput.write(" ");
        }*/
        aOutput.write(aString);
        rememberLastChar(aString.charAt(aString.length() - 1));
    }
}
