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
import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class Check extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer pred = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, pred, getArgumentPointer(aEnvironment, aStackTop, 1));
        if (!Utility.isTrue(aEnvironment, pred, aStackTop))
        {
            ConsPointer type = new ConsPointer();
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, type, getArgumentPointer(aEnvironment, aStackTop, 2));
            LispError.checkIsString(aEnvironment, aStackTop, type, 2, "Check");
            
            
            
            ConsPointer message = new ConsPointer();
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, message, getArgumentPointer(aEnvironment, aStackTop, 3));
            LispError.checkIsString(aEnvironment, aStackTop, message, 3, "Check");



            throw new EvaluationException( Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop, (String) type.car()), Utility.toNormalString(aEnvironment, aStackTop, (String) message.car()), aEnvironment.iInputStatus.fileName(), aEnvironment.iCurrentInput.iStatus.lineNumber(), "Check");
        }
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(pred.getCons());
    }
}



/*
%mathpiper_docs,name="Check",categories="Programmer Functions;Error Reporting;Built In"
*CMD Check --- throw an exception if a predicate expression returns False
*CORE
*CALL
	Check(predicate, "exceptionType", "exceptionMessage")

*PARMS

{predicate} -- expression returning {True} or {False}

{"exceptionType"} -- string which indicates the type of the exception

{"exceptionMessage"} -- string which holds the exception message

*DESC
If {predicate} does not evaluate to {True},
the current operation will be stopped and an exception will be thrown.
This facility can be used to assure that some condition
is satisfied during evaluation of expressions.

Exceptions that are thrown by this function can be caught by the {ExceptionCatch} function.



*E.G.

In> Check(IsInteger(2.3), "Argument", "The argument must be an integer.")
Result: Exception
Exception: The argument must be an integer.


*SEE ExceptionCatch, ExceptionGet

%/mathpiper_docs
*/