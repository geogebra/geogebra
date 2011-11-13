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

import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.builtin.JavaObject;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.SublistCons;

/**
 *
 *
 */
public class JavaToValue extends BuiltinFunction {

    //private StandardFileOutputStream out = new StandardFileOutputStream(System.out);
    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        Object argument = getArgumentPointer(aEnvironment, aStackTop, 1).car();

        if (argument instanceof JavaObject) {
            String atomValue = "";

            JavaObject javaObject = (JavaObject) argument;

            Object object = javaObject.getObject();

            if (object != null) {

                if (object instanceof java.lang.Boolean) {
                    if (((Boolean) object).booleanValue() == true) {
                        atomValue = "True";
                    } else {
                        atomValue = "False";
                    }
                } else if (object instanceof String[]) {
                    
                    String[] stringArray = (String[]) object;

                    Cons listAtomCons = aEnvironment.iListAtom.copy(aEnvironment, false);

                    Cons sublistCons = SublistCons.getInstance(aEnvironment, listAtomCons);

                    ConsPointer consPointer = new ConsPointer(listAtomCons);

                    for(String javaString : stringArray)
                    {
                        Cons atomCons = AtomCons.getInstance(aEnvironment, aStackTop, Utility.toMathPiperString(aEnvironment, aStackTop, javaString));

                        consPointer.cdr().setCons(atomCons);

                        consPointer.goNext(aStackTop, aEnvironment);
                    }//end for.

                    getTopOfStackPointer(aEnvironment, aStackTop).setCons(sublistCons);
                    
                    return;

                } else {
                    atomValue = (String) javaObject.getObject().toString().trim();
                }

                getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, atomValue));

                return;
            }
        } else {
            LispError.raiseError("The argument must be a JavaObject.", "JavaToValue", aStackTop, aEnvironment);
        }


        Utility.putFalseInPointer(aEnvironment, null);
    }//end method.
}//end class.





/*
%mathpiper_docs,name="JavaToValue",categories="Programmer Functions;Built In;Native Objects",access="experimental"
*CMD JavaToValue --- converts a Java object into a MathPiper data structure
*CALL
    JavaToValue(javaObject)

*PARMS
{javaObject} -- a Java object

*DESC
This function is used to convert a Java object into a MathPiper data structure.  It is typically 
used with JavaCall.

*E.G.
In> javaString := JavaNew("java.lang.String", "Hello")
Result: java.lang.String

In> JavaToValue(javaString)
Result: Hello

*SEE JavaCall, JavaAccess, JavaNew
%/mathpiper_docs
*/