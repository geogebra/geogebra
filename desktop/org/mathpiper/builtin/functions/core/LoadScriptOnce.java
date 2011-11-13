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
public class LoadScriptOnce extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "LoadScriptOnce");
        String orig = (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "LoadScriptOnce");

        Utility.loadScriptOnce(aEnvironment, aStackTop, orig);
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}


/*
%mathpiper_docs,name="LoadScriptOnce",categories="User Functions;Control Flow;Input/Output;Built In"
*CMD LoadScriptOnce --- load a script file (but not twice)
*CORE
*CALL
	LoadScriptOnce(name)

*PARMS

{name} -- name of the script file to load

*DESC

If the file "name" has been loaded before, either by an earlier call
to {LoadScriptOnce} or via the {DefLoad}
mechanism, nothing happens. Otherwise all expressions in the file are
read and evaluated. {LoadScriptOnce} always returns {True}.

The purpose of this function is to make sure that the file will at
least have been loaded, but is not loaded twice.

*SEE LoadScript, DefLoad, DefaultDirectory
%/mathpiper_docs
*/
