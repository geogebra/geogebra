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

import java.io.FileOutputStream;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.io.StandardFileOutputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 * 
 */
public class PipeToFile extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.check(aEnvironment, aStackTop, aEnvironment.iSecure == false, LispError.SECURITY_BREACH);

        ConsPointer evaluated = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, evaluated, getArgumentPointer(aEnvironment, aStackTop, 1));

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "PipeToFile");
        String orig = (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "PipeToFile");
        String oper = Utility.toNormalString(aEnvironment, aStackTop, orig);

        // Open file for writing
        FileOutputStream localFP = new FileOutputStream(oper, true);

        LispError.check(aEnvironment, aStackTop, localFP != null, LispError.FILE_NOT_FOUND);

        StandardFileOutputStream newStream = new StandardFileOutputStream(localFP);

        MathPiperOutputStream originalStream = aEnvironment.iCurrentOutput;

        aEnvironment.iCurrentOutput = newStream;

        try
        {
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 2));
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            localFP.flush();
            localFP.close();
            aEnvironment.iCurrentOutput = originalStream;
        }
    }
}



/*
%mathpiper_docs,name="PipeToFile",categories="User Functions;Input/Output;Built In"
*CMD PipeToFile --- connect current output to a file
*CORE
*CALL
	PipeToFile(name) body

*PARMS

{name} -- string, the name of the file to write the result to

{body} -- expression to be evaluated

*DESC

The current output is connected to the file "name". Then the expression
"body" is evaluated. Everything that the commands in "body" print
to the current output, ends up in the file "name". Finally, the
file is closed and the result of evaluating "body" is returned.

If the file is opened again, the new information will be appended to the
existing information in the file.

*E.G. notest

Here is how one can create a file with C code to evaluate an expression:

In> PipeToFile("expr1.c") WriteString(CForm(Sqrt(x-y)*Sin(x)) );
Result> True;

The file {expr1.c} was created in the current working directory and it
contains the line sqrt(x-y)*sin(x)


As another example, take a look at the following command:

In> [ Echo("Result:");  PrettyForm(Taylor(x,0,9) Sin(x)); ];
Result:

	     3    5      7       9
	    x    x      x       x
	x - -- + --- - ---- + ------
	    6    120   5040   362880

Result> True;

Now suppose one wants to send the output of this command to a
file. This can be achieved as follows:

In> PipeToFile("out") [ Echo("Result:");  PrettyForm(Taylor(x,0,9) Sin(x)); ];
Result> True;

After this command the file {out} contains:


Result:

	     3    5      7       9
	    x    x      x       x
	x - -- + --- - ---- + ------
	    6    120   5040   362880


*SEE PipeFromFile, PipeToString, Echo, Write, WriteString, PrettyForm, Taylor
%/mathpiper_docs
*/