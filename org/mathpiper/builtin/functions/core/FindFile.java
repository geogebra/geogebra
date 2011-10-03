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
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class FindFile extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.check(aEnvironment, aStackTop, aEnvironment.iSecure == false, LispError.SECURITY_BREACH);

        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "FindFile");
        String orig = (String)  evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "FindFile");
        String oper = Utility.toNormalString(aEnvironment, aStackTop, orig);

        String filename = Utility.findFile(oper, aEnvironment.iInputDirectories);
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, aEnvironment.getTokenHash().lookUpStringify(filename)));
    }
}



/*
%mathpiper_docs,name="FindFile",categories="User Functions;Input/Output;Built In"
*CMD FindFile --- find a file in the current path
*CORE
*CALL
	FindFile(name)

*PARMS

{name} -- string, name of the file or directory to find

*DESC

The result of this command is the full path to the file that would be
opened when the command {Load(name)} would be
invoked. This means that the input directories are subsequently
searched for a file called "name". If such a file is not found, {FindFile} returns an empty string.

{FindFile("")} returns the name of the default directory (the car one on the search path).

*SEE Load, DefaultDirectory
%/mathpiper_docs
*/