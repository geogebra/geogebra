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
import org.mathpiper.io.MathPiperOutputStream;

/**
 *
 *  
 */
public class PipeToStdout extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        MathPiperOutputStream previous = aEnvironment.iCurrentOutput;
        aEnvironment.iCurrentOutput = aEnvironment.iInitialOutput;
        try
        {
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1));
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            aEnvironment.iCurrentOutput = previous;
        }
    }
}



/*
%mathpiper_docs,name="PipeToStdout",categories="User Functions;Input/Output;Built In"
*CMD PipeToStdout --- select initial output stream for output
*CORE
*CALL
	PipeToStdout() body

*PARMS

{body} -- expression to be evaluated

*DESC

When using {PipeToString} or {PipeToFile}, it might happen that something needs to be
written to the standard default initial output (typically the screen). {PipeToStdout} can be used to select this stream.

*E.G.

In> PipeToString()[Echo("aaaa");PipeToStdout()Echo("bbbb");];
	bbbb
Result: "aaaa
	"

*SEE PipeToString, PipeToFile
%/mathpiper_docs
*/