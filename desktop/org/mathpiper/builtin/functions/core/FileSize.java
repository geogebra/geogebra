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
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class FileSize extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer fnameObject = new ConsPointer();
        fnameObject.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        LispError.checkIsString(aEnvironment, aStackTop, fnameObject, 1, "FileSize");
        String fname = Utility.toNormalString(aEnvironment, aStackTop, (String) fnameObject.car());
        String hashedname = (String) aEnvironment.getTokenHash().lookUp(fname);

        long fileSize = 0;
        InputStatus oldstatus = new InputStatus(aEnvironment.iInputStatus);
        aEnvironment.iInputStatus.setTo(hashedname);
        try
        {
            // Open file
            MathPiperInputStream newInput = // new StdFileInput(hashedname, aEnvironment.iInputStatus);
                    Utility.openInputFile(aEnvironment, aEnvironment.iInputDirectories, hashedname, aEnvironment.iInputStatus);

            LispError.check(aEnvironment, aStackTop, newInput != null, LispError.FILE_NOT_FOUND, "FileSize");
            fileSize = newInput.startPtr().length();
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            aEnvironment.iInputStatus.restoreFrom(oldstatus);
        }
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "" + fileSize));
    }
}
