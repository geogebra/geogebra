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
import org.mathpiper.lisp.Utility;

/**
 *
 * 
 */
public class DefaultDirectory extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "DefaultDirectory");
        String orig =  (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "DefaultDirectory");
        String oper = Utility.toNormalString(aEnvironment, aStackTop, orig);
        aEnvironment.iInputDirectories.add(oper);
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}


/*
%mathpiper_docs,name="DefaultDirectory",categories="User Functions;Built In"
*CMD DefaultDirectory --- add directory to path for MathPiper scripts
*CORE
*CALL
	DefaultDirectory(path)

*PARMS

{path} -- a string containing a full path where MathPiper script files reside

*DESC

When loading files, MathPiper is also allowed to
look in the folder "path". {path} will be prepended
to the file name before trying to load the file.
This means that "path" should end with a forward slash (under Unix-like
operating systems).

MathPiper car tries to load a file from the current
directory, and otherwise it tries to load from
directories defined with this function, in the
order they are defined. Note there will be at least one directory
specified at start-up time, defined during compilation. This
is the directory MathPiper searches for the initialization scripts
and standard scripts.

MathPiper allows you to configure a few things at startup. The file
{~/.mathpiperrc} is written in the MathPiper language and
will be executed when MapthPiper is run. This function
can be useful in the {~/.MathPiperrc} file.

*E.G.

In> DefaultDirectory("/home/user/myscripts/");
Result: True;

*SEE Load, Use, DefLoad, FindFile
%/mathpiper_docs
*/