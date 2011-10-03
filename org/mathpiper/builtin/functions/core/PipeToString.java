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
import org.mathpiper.io.StringOutputStream;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.io.MathPiperOutputStream;

/**
 *
 *  
 */
public class PipeToString extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        StringBuffer oper = new StringBuffer();
        StringOutputStream newOutput = new StringOutputStream(oper);
        MathPiperOutputStream previous = aEnvironment.iCurrentOutput;
        aEnvironment.iCurrentOutput = newOutput;
        try
        {
            // Evaluate the body
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1));

            //Return the getTopOfStackPointer
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, aEnvironment.getTokenHash().lookUpStringify(oper.toString())));
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
%mathpiper_docs,name="PipeToString",categories="User Functions;Input/Output;Built In"
*CMD PipeToString --- connect current output to a string
*CORE
*CALL
	PipeToString() body

*PARMS

{body} -- expression to be evaluated

*DESC

The commands in "body" are executed. Everything that is printed on
the current output, by {Echo} for instance, is
collected in a string and this string is returned.

*E.G.

In> str := PipeToString() [ WriteString(  \
	  "The square of 8 is "); Write(8^2); ];
Result: "The square of 8 is  64";

*SEE PipeFromFile, PipeToString, Echo, Write, WriteString
%/mathpiper_docs
*/