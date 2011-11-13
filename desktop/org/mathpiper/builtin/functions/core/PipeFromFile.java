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
import org.mathpiper.io.InputStatus;
import org.mathpiper.lisp.Environment;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 * 
 */
public class PipeFromFile extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.check(aEnvironment, aStackTop, aEnvironment.iSecure == false, LispError.SECURITY_BREACH);
        ConsPointer evaluated = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, evaluated, getArgumentPointer(aEnvironment, aStackTop, 1));

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "PipeFromFile");
        String orig =  (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "PipeFromFile");

        String hashedname = aEnvironment.getTokenHash().lookUpUnStringify(orig);

        InputStatus oldstatus = aEnvironment.iInputStatus;
        MathPiperInputStream previous = aEnvironment.iCurrentInput;
        try
        {
            aEnvironment.iInputStatus.setTo(hashedname);
            MathPiperInputStream input = // new StdFileInput(hashedname, aEnvironment.iInputStatus);
                    Utility.openInputFile(aEnvironment, aEnvironment.iInputDirectories, hashedname, aEnvironment.iInputStatus);
            aEnvironment.iCurrentInput = input;
            // Open file
            LispError.check(aEnvironment, aStackTop, input != null, LispError.FILE_NOT_FOUND);

            // Evaluate the body
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 2));
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            aEnvironment.iCurrentInput = previous;
            aEnvironment.iInputStatus.restoreFrom(oldstatus);
        }
    //Return the getTopOfStackPointer
    }
}



/*
%mathpiper_docs,name="PipeFromFile",categories="User Functions;Input/Output;Built In"
*CMD PipeFromFile --- connect current input to a file
*CORE
*CALL
	PipeFromFile(name) body

*PARMS

{name} - string, the name of the file to read

{body} - expression to be evaluated

*DESC

The current input is connected to the file "name". Then the expression
"body" is evaluated. If some functions in "body" try to read
from current input, they will now read from the file "name". Finally, the
file is closed and the result of evaluating "body" is returned.

*E.G. notest

Suppose that the file {foo} contains

	2 + 5;

Then we can have the following dialogue:

In> PipeFromFile("foo") res := Read();
Result: 2+5;
In> PipeFromFile("foo") res := ReadToken();
Result: 2;

*SEE PipeToFile, FromString, Read, ReadToken
%/mathpiper_docs
*/