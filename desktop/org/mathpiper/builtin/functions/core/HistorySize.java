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
import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
public class HistorySize extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        aEnvironment.write("Function not yet implemented : LispHistorySize");//TODO FIXME

        throw new EvaluationException("Function not yet supported",aEnvironment.iInputStatus.fileName(), aEnvironment.iCurrentInput.iStatus.lineNumber());
    }
}



/*
%mathpiper_docs,name="HistorySize",categories="User Functions;Built In"
*CMD HistorySize --- set size of history file
*CORE
*CALL
	HistorySize(n)

*PARMS

{n} -- number of lines to store in history file

*DESC

When exiting, MathPiper saves the command line history to a
file {~/.MathPiper_history}. By default it will
save the last 1024 lines. The default can be
overridden with this function. Passing -1 tells the system to save <i>all</i>
lines.

MathPiper allows you to configure a few things at startup. The file
{~/.mathpiperrc} is written in the MathPiper language and
will be executed when MapthPiper is run. This function
can be useful in the {~/.MathPiperrc} file.

*E.G.

In> HistorySize(200)
Result: True;
In> quit

*SEE quit
%/mathpiper_docs
*/