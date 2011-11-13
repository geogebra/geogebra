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
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class PrettyReaderSet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        int nrArguments = Utility.listLength(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 0));
        if (nrArguments == 1)
        {
            aEnvironment.iPrettyReaderName = null;
        } else
        {
            LispError.check(aEnvironment, aStackTop, nrArguments == 2, LispError.WRONG_NUMBER_OF_ARGUMENTS);
            ConsPointer oper = new ConsPointer();
            oper.setCons(getArgumentPointer(aEnvironment, aStackTop, 0).getCons());
            oper.goNext(aStackTop, aEnvironment);
            LispError.checkIsString(aEnvironment, aStackTop, oper, 1, "PrettyReaderSet");
            aEnvironment.iPrettyReaderName = (String) oper.car();
        }
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="PrettyReaderSet",categories="User Functions;Built In"
*CMD PrettyReaderSet --- set routine to use as pretty-reader

*CORE

*CALL
	PrettyReaderSet(reader)
	PrettyReaderSet()

*PARMS

{reader} -- a string containing the name of a function that can read an expression from current input.


*DESC

This function sets up the function reader to read in the input on
the command line. This can be reset to the internal reader with {PrettyReaderSet()} (when no argument is given, the system returns to the default).

Currently implemented PrettyReaders are: {LispRead}, {OMRead}.

MathPiper allows you to configure a few things at startup. The file
{~/.mathpiperrc} is written in the MathPiper language and
will be executed when MapthPiper is run. This function
can be useful in the {~/.MathPiperrc} file.

*E.G.

In> Taylor(x,0,5)Sin(x)
Result: x-x^3/6+x^5/120
In> PrettyReaderSet("LispRead")
Result: True
In> (Taylor x 0 5 (Sin x))
Result: x-x^3/6+x^5/120

*SEE Read, LispRead, OMRead, PrettyPrinterSet, PrettyPrinterGet, PrettyReaderGet
%/mathpiper_docs
*/