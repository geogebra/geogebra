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
import org.mathpiper.exceptions.BreakException;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.AtomCons;

/**
 *
 *  
 */
public class AskUser extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "AskUser");


        Object argument = getArgumentPointer(aEnvironment, aStackTop, 1).car();

        LispError.check(argument instanceof String, "The argument to AskUser must be a string.", "INTERNAL", aStackTop, aEnvironment);

        String messageString = (String) argument;

        LispError.checkArgument(aEnvironment, aStackTop, messageString != null, 1, "AskUser");


        messageString = Utility.stripEndQuotesIfPresent(aEnvironment, aStackTop, messageString);

        String userInputString = JOptionPane.showInputDialog(null, messageString, "Message from MathPiper", JOptionPane.INFORMATION_MESSAGE);

        if(userInputString == null)
        {
            throw new BreakException();
        }//end method.

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "\"" + userInputString + "\""));
    }//end method.

}//end class.



/*
%mathpiper_docs,name="AskUser",categories="User Functions;Input/Output;Built In"
*CMD AskUser --- displays an input dialog to the user
*CORE
*CALL
	AskUser(message)

*PARMS

{message} -- a message which indicates what kind of input to enter

*DESC

This function allows information to be obtained from the user in the
form of a string.  A GUI dialog box will be displayed which the user
can use to enter their input.  If the user selects the cancel button,
the Break() function will be executed.

*SEE TellUser
%/mathpiper_docs
*/