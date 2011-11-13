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
import org.mathpiper.lisp.parsers.Parser;

/**
 *
 *  
 */
public class LispRead extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Parser parser = new Parser(aEnvironment.iCurrentTokenizer,
                aEnvironment.iCurrentInput,
                aEnvironment);
        // Read expression
        parser.parse(aStackTop, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="LispRead",categories="User Functions;Input/Output;Built In"
*CMD LispRead --- read expressions in LISP syntax
*CORE
*CALL
	LispRead()

*DESC

The function {LispRead} reads an expression in the LISP syntax from the current input, and returns
it unevaluated. When the end of an input file is encountered, the
special token atom {EndOfFile} is returned.

The MathPiper expression {a+b} is written in the LISP syntax as {(+ a b)}. The advantage of this syntax is that it is
less ambiguous than the infix operator grammar that MathPiper uses by
default.

*E.G. notest

In> PipeFromString("(+ a b)") LispRead();
Result: a+b;
In> PipeFromString("(List (Sin x) (- (Cos x)))") \
	  LispRead();
Result: {Sin(x),-Cos(x)};
In> PipeFromString("(+ a b)")LispRead()
Result: a+b;

*SEE PipeFromFile, PipeFromString, Read, ReadToken, LispForm, LispReadListed
%/mathpiper_docs
*/