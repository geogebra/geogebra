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
import org.mathpiper.lisp.rulebases.MultipleArityRulebase;

/**
 *
 *  
 */
public class FindFunction extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        LispError.check(aEnvironment, aStackTop, aEnvironment.iSecure == false, LispError.SECURITY_BREACH);

        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // Get file name
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "FindFunction");
        String orig =  (String) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "FindFunction");
        String oper = Utility.toNormalString(aEnvironment, aStackTop, orig);

        MultipleArityRulebase multiUserFunc = aEnvironment.getMultipleArityRulebase(aStackTop, (String)aEnvironment.getTokenHash().lookUp(oper), false);

        String fileLocation =  "\"\"" ;
        
        if (multiUserFunc != null )
        {
            /*DefFile def = multiUserFunc.iFileToOpen;
            if (def != null)
            {
                getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, def.iFileName));
                return;
            }*/
            if(multiUserFunc.iFileLocation != null)
            {
                fileLocation = multiUserFunc.iFileLocation;
            }
            else
            {
               fileLocation = "Function is defined, but it has no body.";
            }

        }//end if

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, fileLocation));
    }//end method

}//end class.



/*
%mathpiper_docs,name="FindFunction",categories="User Functions;Built In"
*CMD FindFunction --- find the library file where a function is defined
*CORE
*CALL
	FindFunction(function)

*PARMS

{function} -- string, the name of a function

*DESC

This function is useful for quickly finding the file where a standard library
function is defined. It is likely to only be useful for developers. The
function {FindFunction} scans the {.def} files that were loaded at start-up.
This means that functions that are not listed in {.def} files will not be found with {FindFunction}.

*E.G.

In> FindFunction("Sum")
Result: "sums.rep/code.ys";
In> FindFunction("Integrate")
Result: "integrate.rep/code.ys";

*SEE Vi
%/mathpiper_docs
*/