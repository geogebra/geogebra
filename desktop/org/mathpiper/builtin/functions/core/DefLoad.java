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
public class DefLoad extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.check(aEnvironment, aStackTop, aEnvironment.iSecure == false, LispError.SECURITY_BREACH);

        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "DefLoad");
        String orig =  (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "DefLoad");

        Utility.loadDefFile(aEnvironment, aStackTop, orig);
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="DefLoad",categories="User Functions;Input/Output;Built In"
*CMD DefLoad --- load a {.def} file
*CORE
*CALL
	DefLoad(name)

*PARMS

{name} -- string, name of the file (without {.def} suffix)

*DESC

The suffix {.def} is appended to "name" and the
file with this name is loaded. It should contain a list of functions,
terminated by a closing brace \} (the end-of-list delimiter). This
tells the system to load the file "name" as soon as the user calls
one of the functions named in the file (if not done so already). This
allows for faster startup times, since not all of the rules databases
need to be loaded, just the descriptions on which files to load for
which functions.

*SEE Load, Use, DefaultDirectory
%/mathpiper_docs
*/