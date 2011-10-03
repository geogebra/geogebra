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
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class DefaultTokenizer extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        aEnvironment.iCurrentTokenizer = aEnvironment.iDefaultTokenizer;
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="DefaultTokenizer",categories="User Functions;Input/Output;Built In"
*CMD DefaultTokenizer --- select the default syntax tokenizer for parsing the input
*CORE
*CALL
	DefaultTokenizer()

*DESC

A "tokenizer" is an internal routine in the kernel that parses the input into MathPiper expressions.
This affects all input typed in by a user at the prompt and also the input redirected from files or strings using {PipeFromFile} and {FromString} and read using {Read} or {ReadToken}.

The MathPiper environment currently supports some experimental tokenizers for
various syntaxes. {DefaultTokenizer} switches to the tokenizer used for
default MathPiper syntax.
Note that setting the tokenizer is a global side effect.
One typically needs
to switch back to the default tokenizer when finished reading the special syntax.

Care needs to be taken when kernel errors are raised during a non-default tokenizer operation (as with any global change in the environment).
Errors need to be
caught with the {TrapError} function. The error handler code should re-instate
the default tokenizer,
or else the user will be unable to continue the session
(everything a user types will be parsed using a non-default tokenizer).


*E.G. notest

In>


*SEE OMRead, TrapError, XmlExplodeTag, ReadToken, PipeFromFile, FromString, XmlTokenizer
%/mathpiper_docs
*/