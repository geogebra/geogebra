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
import org.mathpiper.lisp.parsers.MathPiperParser;

/**
 *
 * 
 */
public class Read extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        MathPiperParser parser = new MathPiperParser(aEnvironment.iCurrentTokenizer,
                aEnvironment.iCurrentInput,
                aEnvironment,
                aEnvironment.iPrefixOperators,
                aEnvironment.iInfixOperators,
                aEnvironment.iPostfixOperators,
                aEnvironment.iBodiedOperators);
        // Read expression
        parser.parse(aStackTop, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="Read",categories="User Functions;Input/Output;Built In"
*CMD Read --- read an expression from current input
*CORE
*CALL
	Read()

*DESC

Read an expression from the current input, and return it unevaluated. When
the end of an input file is encountered, the token atom {EndOfFile} is returned.

*E.G.

In> PipeFromString("2+5;") Read();
Result: 2+5;
In> PipeFromString("") Read();
Result: EndOfFile;

*SEE PipeFromFile, PipeFromString, LispRead, ReadToken, Write
%/mathpiper_docs
*/