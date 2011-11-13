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
public class LispReadListed extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        Parser parser = new Parser(aEnvironment.iCurrentTokenizer,
                aEnvironment.iCurrentInput,
                aEnvironment);
        parser.iListed = true;
        // Read expression
        parser.parse(aStackTop, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="LispReadListed",categories="User Functions;Input/Output;Built In"
*CMD LispReadListed --- read expressions in LISP syntax
*CORE
*CALL
	LispReadListed()

*DESC

The function {LispReadListed} reads a LISP expression and returns
it in a list, instead of the form usual to MathPiper (expressions).
The result can be thought of as applying {FunctionToList} to {LispRead}.
The function {LispReadListed} is more useful for reading arbitrary LISP expressions, because the
first object in a list can be itself a list (this is never the case for MathPiper expressions where the first object in a list is always a function atom).

*E.G. notest

In> PipeFromString("(+ a b)")LispReadListed()
Result: {+,a,b};

*SEE PipeFromFile, PipeFromString, Read, ReadToken, LispForm, LispRead
%/mathpiper_docs
*/