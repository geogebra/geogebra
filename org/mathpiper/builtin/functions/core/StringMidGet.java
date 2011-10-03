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
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class StringMidGet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 3).getCons());
        LispError.checkIsString(aEnvironment, aStackTop, evaluated, 3, "StringMidGet");
        String orig = (String) evaluated.car();

        ConsPointer index = new ConsPointer();
        index.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        LispError.checkArgument(aEnvironment, aStackTop, index.getCons() != null, 1, "StringMidGet");
        LispError.checkArgument(aEnvironment, aStackTop, index.car() instanceof String, 1, "StringMidGet");
        int from = Integer.parseInt( (String) index.car(), 10);
        LispError.checkArgument(aEnvironment, aStackTop, from > 0, 1, "StringMidGet");

        index.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        LispError.checkArgument(aEnvironment, aStackTop, index.getCons() != null, 2, "StringMidGet");
        LispError.checkArgument(aEnvironment, aStackTop, index.car() instanceof String, 2, "StringMidGet");
        int count = Integer.parseInt( (String) index.car(), 10);


        String str = "\"" + orig.substring(from, from + count) + "\"";
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, str));
    }
}



/*
%mathpiper_docs,name="StringMidGet",categories="User Functions;String Manipulation;Built In"
*CMD StringMidGet --- retrieve a substring
*CORE
*CALL
	StringMidGet(index,length,string)

*PARMS

{index} -- index of substring to get

{length} -- length of substring to get

{string} -- string to get substring from

*DESC

{StringMidGet} returns a part of a string. Substrings can also be
accessed using the {[]} operator.

*E.G.

In> StringMidGet(3,2,"abcdef")
Result: "cd";
In> "abcdefg"[2 .. 4]
Result: "bcd";

*SEE StringMidSet, Length
%/mathpiper_docs
*/