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
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;

/**
 *
 * 
 */
public class ReadToken extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        MathPiperTokenizer tok = aEnvironment.iCurrentTokenizer;
        String result;
        result = tok.nextToken(aEnvironment, aStackTop, aEnvironment.iCurrentInput, aEnvironment.getTokenHash());

        if (result.length() == 0)
        {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(aEnvironment.iEndOfFileAtom.copy( aEnvironment, false));
            return;
        }
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, result));
    }
}



/*
%mathpiper_docs,name="ReadToken",categories="User Functions;Input/Output;Built In"
*CMD ReadToken --- read a token from current input
*CORE
*CALL
	ReadToken()

*DESC

Read a token from the current input, and return it unevaluated.
The returned object is a MathPiper atom (not a string).
When
the end of an input file is encountered, the token atom {EndOfFile} is returned.

A token is for computer languages what a word is for human languages:
it is the smallest unit in which a command can be divided, so that the
semantics (that is the meaning) of the command is in some sense a
combination of the semantics of the tokens. Hence {a := foo} consists of three tokens, namely {a}, {:=}, and {foo}.

The parsing of the string depends on the syntax of the language.
The part of the kernel that does the parsing is the "tokenizer".
MathPiper can parse its own syntax (the default tokenizer) or it can be instructed to parse XML or C++ syntax using the directives {DefaultTokenizer} or {XmlTokenizer}.
Setting a tokenizer is a global action that affects all {ReadToken} calls.

*E.G. notest

In> PipeFromString("a := Sin(x)") While \
	  ((tok := ReadToken()) != EndOfFile) \
	  Echo(tok);
	a
	:=
	Sin
	(
	x
	)
Result: True;

We can read some junk too:
In> PipeFromString("-$3")ReadToken();
Result: -$;
The result is an atom with the string representation {-$}.
MathPiper assumes that {-$} is an operator symbol yet to be defined.
The "{3}" will be in the next token.
(The results will be different if a non-default tokenizer is selected.)


*SEE PipeFromFile, PipeFromString, Read, LispRead, DefaultTokenizer
%/mathpiper_docs
*/