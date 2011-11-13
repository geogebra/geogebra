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

import java.util.ArrayList;
import java.util.List;
import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.builtin.JavaObject;
import org.mathpiper.builtin.javareflection.Invoke;
import org.mathpiper.builtin.javareflection.JavaField;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.BuiltinObjectCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.cons.NumberCons;
import org.mathpiper.lisp.cons.SublistCons;

/**
 *
 *
 */
public class JavaCall extends BuiltinFunction {

    //private StandardFileOutputStream out = new StandardFileOutputStream(System.out);
    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        if (getArgumentPointer(aEnvironment, aStackTop, 1).car() instanceof ConsPointer) {

            ConsPointer subList = (ConsPointer) getArgumentPointer(aEnvironment, aStackTop, 1).car();
            ConsTraverser consTraverser = new ConsTraverser(aEnvironment, subList);

            //Skip past List type.
            consTraverser.goNext(aStackTop);

            //Obtain the Java object to call.
            Cons argumentCons = consTraverser.getPointer().getCons();

            BuiltinContainer builtinContainer = null;

            if (argumentCons != null) {




                if (argumentCons.car() instanceof String) {
                    String firstArgumentString = (String) argumentCons.car();
                    //Strip leading and trailing quotes.
                    firstArgumentString = Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop,firstArgumentString);
                    Object clas = Class.forName(firstArgumentString);
                    builtinContainer = new JavaObject(clas);
                } else if (argumentCons.car() instanceof BuiltinContainer) {
                    builtinContainer = (BuiltinContainer) argumentCons.car();
                }//end else.


                if (builtinContainer != null) {


                    consTraverser.goNext(aStackTop);
                    argumentCons = consTraverser.getPointer().getCons();
                    String methodName = (String) argumentCons.car();
                    //Strip leading and trailing quotes.
                    methodName = Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop, methodName);

                    consTraverser.goNext(aStackTop);

                    ArrayList argumentArrayList = new ArrayList();

                    while (consTraverser.getCons() != null) {
                        argumentCons = consTraverser.getPointer().getCons();

                        Object argument = null;

                        if (argumentCons instanceof NumberCons) {
                            NumberCons numberCons = (NumberCons) argumentCons;
                            BigNumber bigNumber = (BigNumber) numberCons.getNumber(aEnvironment.getPrecision(), aEnvironment);

                            if (bigNumber.isInteger()) {
                                argument = bigNumber.toInt();
                            } else {
                                argument = bigNumber.toDouble();
                            }
                        } else if (argumentCons instanceof AtomCons) {
                            String string = (String) ((AtomCons) argumentCons).car();
                            if (string != null) {

                                if (Utility.isString(string)) { //MathPiper string.
                                    argument = Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop, (String) string);
                                } else { //Atom.
                                    if (string.equals("True")) {
                                        argument = Boolean.TRUE;
                                    }//end if.

                                    if (string.equals("False")) {
                                        argument = Boolean.FALSE;
                                    }//end if.
                                }//end if/else.

                            }//end if.
                        } else {
                            argument = argumentCons.car();


                            if (argument instanceof JavaObject) {
                                argument = ((JavaObject) argument).getObject();
                            }

                        }//end if/else.


                        argumentArrayList.add(argument);

                        consTraverser.goNext(aStackTop);

                    }//end while.


                    Object[] argumentsArray = (Object[]) argumentArrayList.toArray(new Object[0]);

                    Object targetObject = builtinContainer.getObject();

                    Object returnObject = null;
                            
                    if(targetObject instanceof Class)
                    { 
                        try
                        {
                            returnObject = Invoke.invokeStatic((Class) targetObject, methodName, argumentsArray);
                        }
                        catch(Exception e1)
                        {
                            try
                            {
                                returnObject = JavaField.getField((Class) targetObject, methodName, true).get(null);
                            }
                            catch(Exception e2)
                            {
                                LispError.raiseError("Method or field " + methodName + " does not exist.", "", -2, null);
                            }
                        }
                    }
                    else
                    {
                        returnObject = Invoke.invokeInstance(targetObject, methodName, argumentsArray, true);
                    }

                    if (returnObject instanceof List) {
                        Cons listCons = Utility.iterableToList(aEnvironment, aStackTop, (List) returnObject);

                        getTopOfStackPointer(aEnvironment, aStackTop).setCons(SublistCons.getInstance(aEnvironment, listCons));
                    } else {
                        JavaObject response = new JavaObject(returnObject);
                        if (response == null || response.getObject() == null) {
                            Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
                            return;
                        }
                        getTopOfStackPointer(aEnvironment, aStackTop).setCons(BuiltinObjectCons.getInstance(aEnvironment, aStackTop, response));
                    }


                    return;

                }//end if.

            }//end if.

        }//end if.

        Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));

    }//end method.
}




/*
%mathpiper_docs,name="JavaCall",categories="Programmer Functions;Built In;Native Objects",access="experimental"
*CMD JavaCall --- calls a method on a Java object and returns the result as a Java object
*CALL
    JavaCall(javaObject, methodName, methodParameter1, methodParameter2, ...)

*PARMS
{javaObject} -- a Java object

{methodName} -- the name of a method to call on the Java object (it can be either a string or an atom)

{methodParameters} -- zero or more parameters which will be sent to the method

*DESC
This function calls a method on {javaObject} and returns the result as a Java object.  The returned Java object
can be converted into a MathPiper data structure by passing it to JavaToValue, or in can be passed
to JavaCall or JavaAccess for further processing.

*E.G.
In> javaString := JavaNew("java.lang.String", "Hello")
Result: java.lang.String

In> javaString := JavaCall(javaString, "replace", "e", "o")
Result: java.lang.String

In> JavaToValue(javaString)
Result: Hollo

In> JavaAccess(javaString, "charAt", 0)
Result: H

*SEE JavaNew, JavaAccess, JavaToValue
%/mathpiper_docs
*/