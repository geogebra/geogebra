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
import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.BuiltinObjectCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.SublistCons;

/**
 *
 *  
 */
public class ExceptionGet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        if(aEnvironment.iException == null)
        {
            Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
        }
        else
        {
            Throwable exception = aEnvironment.iException;

            String type = null;

            String message = null;

            if(exception instanceof EvaluationException)
            {
               EvaluationException evaluationException = (EvaluationException) exception;

               type = evaluationException.getType();
            }
            else
            {
                type = exception.getClass().getName();
            }

            message = exception.getMessage();


            JavaObject exceptionObject = new JavaObject(exception);

            
            
            //Create type association list.
            Cons typeListAtomCons = aEnvironment.iListAtom.copy(aEnvironment, false);

            Cons typeNameAtomCons = AtomCons.getInstance(aEnvironment, aStackTop, "\"type\"");

            Cons typeValueValueAtomCons = AtomCons.getInstance(aEnvironment, aStackTop, Utility.toMathPiperString(aEnvironment, aStackTop, type));

            typeListAtomCons.cdr().setCons(typeNameAtomCons);

            typeNameAtomCons.cdr().setCons(typeValueValueAtomCons);

            Cons typeSublistCons = SublistCons.getInstance(aEnvironment, typeListAtomCons);




            //Create message association list.
            Cons messageListAtomCons = aEnvironment.iListAtom.copy(aEnvironment, false);

            Cons messageNameAtomCons = AtomCons.getInstance(aEnvironment, aStackTop, "\"message\"");

            Cons messageValueValueAtomCons = AtomCons.getInstance(aEnvironment, aStackTop, Utility.toMathPiperString(aEnvironment, aStackTop, message));

            messageListAtomCons.cdr().setCons(messageNameAtomCons);

            messageNameAtomCons.cdr().setCons(messageValueValueAtomCons);

            Cons messageSublistCons = SublistCons.getInstance(aEnvironment, messageListAtomCons);



            //Create exception object association list.
            Cons exceptionObjectListAtomCons = aEnvironment.iListAtom.copy(aEnvironment, false);

            Cons exceptionObjectNameAtomCons = AtomCons.getInstance(aEnvironment, aStackTop, "\"exceptionObject\"");

            Cons exceptionObjectValueValueAtomCons = BuiltinObjectCons.getInstance(aEnvironment, aStackTop, exceptionObject);

            exceptionObjectListAtomCons.cdr().setCons(exceptionObjectNameAtomCons);

            exceptionObjectNameAtomCons.cdr().setCons(exceptionObjectValueValueAtomCons);

            Cons exceptionObjectSublistCons = SublistCons.getInstance(aEnvironment, exceptionObjectListAtomCons);



            //Create result list.
            typeSublistCons.cdr().setCons(messageSublistCons);

            messageSublistCons.cdr().setCons(exceptionObjectSublistCons);

            //exceptionSublistCons.cdr().setCons(xxxSublistCons);

            //xxxSublistCons.cdr().setCons(yyySublistCons);

            Cons resultListAtomCons = aEnvironment.iListAtom.copy(aEnvironment, false);

            resultListAtomCons.cdr().setCons(typeSublistCons);

            Cons resultSublistCons = SublistCons.getInstance(aEnvironment, resultListAtomCons);




            getTopOfStackPointer(aEnvironment, aStackTop).setCons(resultSublistCons);

        }
    }
}



/*
%mathpiper_docs,name="ExceptionGet",categories="Programmer Functions;Built In"
*CMD ExceptionGet --- returns the exception object which was thrown.
*CORE
*CALL
	ExceptionGet()

*DESC

ExceptionGet is designed to be used in the {exceptionHandler} argument of {ExceptionCatch} and it
 returns an association list which contains information about the caught exception.  If {ExceptionGet} is
 evaluated outside of {ExceptionCatch}, it always returns {False};
{ExceptionCatch} and {ExceptionGet} are used in combination to write
an exception handler.

*E.G.

In> ExceptionGet()
Result: False


 
In> ExceptionCatch(Check(1 = 2, "Test", "Throwing a test exception."), Echo(ExceptionGet()))
Result: True
Side Effects:
{{"type","Test"},{"message","Throwing a test exception."},{"exceptionObject",class org.mathpiper.exceptions.EvaluationException}}

*SEE Check, ExceptionCatch

%/mathpiper_docs
*/