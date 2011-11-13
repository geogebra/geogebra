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
import org.mathpiper.io.InputStatus;
import org.mathpiper.io.StringOutputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.AtomCons;

/**
 *
 *  
 */
public class PatchString extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {
        String unpatchedString = 
            (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, unpatchedString != null, 2, "PatchString");
        
        InputStatus oldStatus = new InputStatus(aEnvironment.iInputStatus);
        aEnvironment.iInputStatus.setTo("STRING");
        
        StringBuffer resultBuffer = new StringBuffer();
        StringOutputStream resultStream = new StringOutputStream(resultBuffer);
        
        Utility.doPatchString(unpatchedString, resultStream, aEnvironment, aStackTop);
        
        aEnvironment.iInputStatus.restoreFrom(oldStatus);
        
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, resultBuffer.toString()));
    }


}//end class.



/*
%mathpiper_docs,name="PatchString",categories="User Functions;String Manipulation;Built In"
*CMD PatchString --- execute commands between {<?} and {?>} in strings
*CORE
*CALL
	PatchString(string)

*PARMS

{string} -- a string to patch

*DESC

This function does the same as PatchLoad, but it works on a string
in stead of on the contents of a text file. See PatchLoad for more
details.

*E.G.
In> PatchString("Two plus three is <?Write(2+3);?> ");
Result: "Two plus three is 5 ";

*SEE PatchLoad
%/mathpiper_docs
*/