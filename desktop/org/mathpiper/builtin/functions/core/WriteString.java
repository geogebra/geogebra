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
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;

/**
 *
 * 
 */
public class WriteString extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "WriteString");
        String str = (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, str != null, 1, "WriteString");
        LispError.checkArgument(aEnvironment, aStackTop, str.charAt(0) == '\"', 1, "WriteString");
        LispError.checkArgument(aEnvironment, aStackTop, str.charAt(str.length() - 1) == '\"', 1, "WriteString");

        int i = 1;
        int nr = str.length() - 1;
        //((*str)[i] != '\"')
        for (i = 1; i < nr; i++)
        {
            aEnvironment.iCurrentOutput.putChar(str.charAt(i));
        }
        // pass last printed character to the current printer
        aEnvironment.iCurrentPrinter.rememberLastChar(str.charAt(nr - 1));  // hacky hacky

        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="WriteString",categories="User Functions;Input/Output;Built In"
*CMD WriteString --- low-level printing routine for strings
*CORE
*CALL
	WriteString(string)

*PARMS

{string} -- the string to be printed

*DESC

The expression "string" is evaluated and written to the current
output without quotation marks. The argument should be a
string. WriteString always returns True.

*E.G. notest

In> Write("Hello, world!");
	"Hello, world!"Result: True;
In> WriteString("Hello, world!");
	Hello, world!Result: True;

This example clearly shows the difference between Write and
WriteString. Note that Write and WriteString do not write a newline,
so the {Result:} prompt immediately follows the output.

*SEE Echo, Write
%/mathpiper_docs
*/