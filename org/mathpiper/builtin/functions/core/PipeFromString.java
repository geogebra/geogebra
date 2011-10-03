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
import org.mathpiper.io.StringInputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 * 
 */
public class PipeFromString extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, evaluated, getArgumentPointer(aEnvironment, aStackTop, 1));

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "PipeFromString");
        String orig =  (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "PipeFromString");
        String oper = Utility.toNormalString(aEnvironment, aStackTop, orig);

        InputStatus oldstatus = aEnvironment.iInputStatus;
        aEnvironment.iInputStatus.setTo("String");
        StringInputStream newInput = new StringInputStream(new StringBuffer(oper), aEnvironment.iInputStatus);

        MathPiperInputStream previous = aEnvironment.iCurrentInput;
        aEnvironment.iCurrentInput = newInput;
        try
        {
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
%mathpiper_docs,name="PipeFromString",categories="User Functions;Input/Output;Built In"
*CMD PipeFromString --- connect current input to a string
*CORE
*CALL
	PipeFromString(str) body;

*PARMS

{str} -- a string containing the text to parse

{body} -- expression to be evaluated

*DESC

The commands in "body" are executed, but everything that is read
from the current input is now read from the string "str". The
result of "body" is returned.

*E.G.

In> PipeFromString("2+5; this is never read") \
	  res := Read();
Result: 2+5;
In> PipeFromString("2+5; this is never read") \
	  res := Eval(Read());
Result: 7;

*SEE PipeToString, PipeFromFile, Read, ReadToken
%/mathpiper_docs
*/