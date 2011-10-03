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
import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.builtin.JavaObject;
import org.mathpiper.builtin.javareflection.Invoke;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.BuiltinObjectCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.cons.NumberCons;

/**
 *
 *
 */
public class JavaNew extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        if (getArgumentPointer(aEnvironment, aStackTop, 1).car() instanceof ConsPointer) {

            ConsPointer subList = (ConsPointer) getArgumentPointer(aEnvironment, aStackTop, 1).car();
            ConsTraverser consTraverser = new ConsTraverser(aEnvironment, subList);

            //Skip past List type.
            consTraverser.goNext(aStackTop);

            Cons argumentCons = consTraverser.getPointer().getCons();

            if (argumentCons != null) {

                String fullyQualifiedClassName = (String) argumentCons.car();
                //Strip leading and trailing quotes.
                fullyQualifiedClassName = Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop, fullyQualifiedClassName);

                consTraverser.goNext(aStackTop);

                ArrayList argumentArrayList = new ArrayList();

                while (consTraverser.getCons() != null) {
                    argumentCons = consTraverser.getPointer().getCons();

                    Object argument = null;

                    if(argumentCons instanceof NumberCons)
                    {
                        NumberCons numberCons = (NumberCons) argumentCons;
                        BigNumber bigNumber = (BigNumber) numberCons.getNumber(aEnvironment.getPrecision(), aEnvironment);

                        if(bigNumber.isInteger())
                        {
                            argument = bigNumber.toInt();
                        }
                        else
                        {
                            argument = bigNumber.toDouble();
                        }
                    }
                    else
                    {
                        argument = argumentCons.car();


                        if (argument instanceof String) {
                            argument = Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop, (String)argument);
                        }

                        if(argument instanceof JavaObject)
                        {
                            argument = ((JavaObject)argument).getObject();
                        }
                    }//end if/else.

                    argumentArrayList.add(argument);

                    consTraverser.goNext(aStackTop);

                }//end while.

                Object[] argumentsArray = (Object[]) argumentArrayList.toArray(new Object[0]);

                Object o = Invoke.invokeConstructor(fullyQualifiedClassName, argumentsArray);

                JavaObject response = new JavaObject(o);

                //JavaObject response = JavaObject.instantiate(fullyQualifiedClassName, argumentsArray);
                //System.out.println("XXXXXXXXXXX: " + response);

                if (response == null) {
                    Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
                    return;
                } else {
                    getTopOfStackPointer(aEnvironment, aStackTop).setCons(BuiltinObjectCons.getInstance(aEnvironment, aStackTop, response));
                    return;
                }//end if/else.



            }//end if.

        }//end if.

        Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));

    }//end method.
}




/*
%mathpiper_docs,name="JavaNew",categories="Programmer Functions;Built In;Native Objects",access="experimental"
*CMD JavaNew --- instantiates a Java object
*CALL
    JavaNew(fullyQualifiedClassName, constructorParameter1, constructorParameter2, ...)

*PARMS
{fullyQualifiedClassName} -- (string) the fully qualified name of a Java class

{constructorParameters} -- zero or more parameters which will be sent to the constructor

*DESC
This function instantiates a Java object and then returns it as a result.

*E.G.
In> javaString := JavaNew("java.lang.String", "Hello")
Result: java.lang.String

In> javaString := JavaAccess(javaString, "toUpperCase")
Result: HELLO

*SEE JavaCall, JavaAccess, JavaToValue
%/mathpiper_docs
*/