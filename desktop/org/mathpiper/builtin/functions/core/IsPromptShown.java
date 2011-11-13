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
public class IsPromptShown extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        aEnvironment.write("Function not yet implemented : LispIsPromptShown");//TODO FIXME

        throw new EvaluationException("Function not yet supported",aEnvironment.iInputStatus.fileName(), aEnvironment.iCurrentInput.iStatus.lineNumber());
    }
}//end class.



/*
%mathpiper_docs,name="IsPromptShown",categories="Programmer Functions;Built In"
*CMD IsPromptShown --- test for the MathPiper prompt option
*CORE
*CALL
	IsPromptShown()

*DESC
Returns {False} if MathPiper has been started with the option to suppress the prompt, and {True} otherwise.

%/mathpiper_docs
*/
