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

/**
 *
 *  
 */
public class CurrentLine extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "" + aEnvironment.iInputStatus.lineNumber()));
    }
}



/*
%mathpiper_docs,name="CurrentLine",categories="Programmer Functions;Error Reporting;Built In"
*CMD CurrentLine --- return current line number on input
*CORE
*CALL
	CurrentLine()

*DESC

The function {CurrentLine} returns a string
with the current line of the input file.

These functions are most useful in batch file calculations, where
there is a need to determine at which line an error occurred.
One can define a function

	tst() := Echo({CurrentFile(),CurrentLine()});
which can then be inserted into the input file at various places,
to see how far the interpreter reaches before an error occurs.

*SEE Echo, CurrentFile

%/mathpiper_docs
*/