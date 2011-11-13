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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class SystemCall extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "SystemCall");
        String orig = (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "SystemCall");
        String oper = Utility.toNormalString(aEnvironment, aStackTop, orig);
        String ls_str;
        Process ls_proc = Runtime.getRuntime().exec(oper);
        // getCons its output (your input) stream
        BufferedReader ls_in = new BufferedReader(new InputStreamReader(ls_proc.getInputStream()));

        while ((ls_str = ls_in.readLine()) != null)
        {
            aEnvironment.write(ls_str);
            aEnvironment.write("\n");
        }
    }
}


/*
%mathpiper_docs,name="SystemCall",categories="User Functions;Control Flow;Built In"
*CMD SystemCall --- pass a command to the shell
*CORE
*CALL
	SystemCall(str)

*PARMS

{str} -- string containing the command to call

*DESC

The command contained in the string "str" is executed by the
underlying operating system (OS).
The return value of {SystemCall} is {True} or {False} according to the exit code of the command.

The {SystemCall} function is not allowed in the body of the {Secure} command and will lead to an error.

*E.G. notest

In a UNIX environment, the command {SystemCall("ls")} would print the contents of the current directory.

In> SystemCall("ls")
	AUTHORS
	COPYING
	ChangeLog
... (truncated to save space)
Result: True;

The standard UNIX command {test} returns success or failure depending on conditions.
For example, the following command will check if a directory exists:

In> SystemCall("test -d scripts/")
Result: True;

Check that a file exists:
In> SystemCall("test -f COPYING")
Result: True;
In> SystemCall("test -f nosuchfile.txt")
Result: False;

*SEE Secure
%/mathpiper_docs
*/
