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

import javax.swing.JOptionPane;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;

/**
 *
 *
 */
public class TellUser extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "TellUser");

        Object argument = getArgumentPointer(aEnvironment, aStackTop, 1).car();

        LispError.check(argument instanceof String, "The argument to TellUser must be a string.", "INTERNAL", aStackTop, aEnvironment);

        String messageString = (String) argument;

        LispError.checkArgument(aEnvironment, aStackTop, messageString != null, 1, "TellUser");

        messageString = Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop, messageString);

        JOptionPane.showMessageDialog(null, messageString, "Message from MathPiper", JOptionPane.INFORMATION_MESSAGE);

        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }//end method.

}//end class.



/*
%mathpiper_docs,name="TellUser",categories="User Functions;Input/Output;Built In"
*CMD AskUser --- displays a message to the user in a dialog.
*CORE
*CALL
	TellUser(message)

*PARMS

{message} -- a message to display to the user

*DESC

This function allows a message to be displayed to the user.  The message will be
displayed in a GUI dialog box.

*SEE AskUser
%/mathpiper_docs
*/