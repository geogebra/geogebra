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
public class XmlTokenizer extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        aEnvironment.iCurrentTokenizer = aEnvironment.iXmlTokenizer;
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="XmlTokenizer",categories="User Functions;Input/Output;Built In"
*CMD XmlTokenizer --- select an XML syntax tokenizer for parsing the input
*CORE
*CALL
	XmlTokenizer()

*DESC

A "tokenizer" is an internal routine in the kernel that parses the input into MathPiper expressions.
This affects all input typed in by a user at the prompt and also the input redirected from files or strings using {PipeFromFile} and {FromString} and read using {Read} or {ReadToken}.

The MathPiper environment currently supports some experimental tokenizers for
various syntaxes. {XmlTokenizer} switches to an XML syntax.
Note that setting the tokenizer is a global side effect.
One typically needs
to switch back to the default tokenizer when finished reading the special syntax.

Care needs to be taken when kernel errors are raised during a non-default tokenizer operation (as with any global change in the environment).
Errors need to be
caught with the {TrapError} function. The error handler code should re-instate
the default tokenizer,
or else the user will be unable to continue the session
(everything a user types will be parsed using a non-default tokenizer).

When reading XML syntax, the supported formats are the same as those of {XmlExplodeTag}.
The parser does not validate anything in the XML input.
After an XML token has been read in, it can be converted into an
MathPiper expression with {XmlExplodeTag}.
Note that when reading XML, any plain text between tags is returned as one token.
Any malformed XML will be treated as plain text.


*E.G. notest

In> [XmlTokenizer(); q:=ReadToken(); \
	  DefaultTokenizer();q;]
	<a>Result: <a>;

Note that:
*	1. after switching to {XmlTokenizer} the {In>} prompt disappeared; the user typed {<a>} and the {Result:} prompt with the resulting expression appeared.
*	2. The resulting expression is an atom with the string representation {<a>};
it is <i>not</i> a string.

*SEE OMRead, TrapError, XmlExplodeTag, ReadToken, PipeFromFile, FromString, DefaultTokenizer
%/mathpiper_docs
*/