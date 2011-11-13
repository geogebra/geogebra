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
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
public class ExceptionCatch extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        try
        {
            //Return the first argument.
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1));
        } catch (Throwable exception)
        {   //Return the second argument.
            //e.printStackTrace();
            Boolean interrupted = Thread.currentThread().interrupted(); //Clear interrupted condition.
            aEnvironment.iException = exception;
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 2));
            aEnvironment.iException = null;
        }
    }
}



/*
%mathpiper_docs,name="ExceptionCatch",categories="Programmer Functions;Error Reporting;Built In"
*CMD ExceptionCatch --- catches exceptions
*CORE
*CALL
	ExceptionCatch(expression, exceptionHandler)

*PARMS

{expression} -- expression to evaluate (causing potential error)

{exceptionHandler} -- expression which is evaluated to handle the exception

*DESC
ExceptionCatch evaluates its argument {expression} and returns the
result of evaluating {expression}. If an exception is thrown,
{errorHandler} is evaluated, returning its return value instead.

{ExceptionGet} can be used to obtain information about the caught exception.


 
*E.G.


In> ExceptionCatch(Check(1 = 2, "Test", "Throwing a test exception."), "This string is returned if an exception is thrown.");
Result: "This string is returned if an exception is thrown."




/%mathpiper,title="Example of how to use ExceptionCatch and ExceptionGet in test code (long version)."
[
  Local(exception);

  exception := False;

  ExceptionCatch(Check(1 = 2, "Test", "Throwing a test exception."), exception := True);

  Verify(exception, True);

];
/%/mathpiper

    /%output,preserve="false"
      Result: True
.   /%/output





/%mathpiper,title="Example of how to use ExceptionCatch and ExceptionGet in test code (short version)."

//ExceptionGet returns False if there is no exception or an association list if there is.
Verify( ExceptionCatch(Check(1 = 2, "Test", "Throwing a test exception."), ExceptionGet()) = False, False);

/%/mathpiper

    /%output,preserve="false"
      Result: True
.   /%/output





/%mathpiper,title="Example of how to handle a caught exception."

TestFunction(x) :=
[

    Check(IsInteger(x), "Argument", "The argument must be an integer.");

];




caughtException := ExceptionCatch(TestFunction(1.2), ExceptionGet());

Echo(caughtException);

NewLine();

Echo("Type: ", caughtException["type"]);

NewLine();

Echo("Message: ", caughtException["message"]);


/%/mathpiper

    /%output,preserve="false"
      Result: True

      Side Effects:
      {{"type","Argument"},{"message","The argument must be an integer."},{"exceptionObject",class org.mathpiper.exceptions.EvaluationException}}

      Type: Argument

      Message: The argument must be an integer.

.   /%/output

*SEE Check, ExceptionGet

%/mathpiper_docs
*/