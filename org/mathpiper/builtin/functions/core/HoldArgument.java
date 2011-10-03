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
public class HoldArgument extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        // Get operator
        LispError.checkArgument(aEnvironment, aStackTop, getArgumentPointer(aEnvironment, aStackTop, 1).getCons() != null, 1, "HoldArgument");
        String orig =  (String) getArgumentPointer(aEnvironment, aStackTop, 1).car();
        LispError.checkArgument(aEnvironment, aStackTop, orig != null, 1, "HoldArgument");

        // The arguments
        String tohold =  (String) getArgumentPointer(aEnvironment, aStackTop, 2).car();
        LispError.checkArgument(aEnvironment, aStackTop, tohold != null, 2, "HoldArgument");
        aEnvironment.holdArgument(aStackTop, Utility.getSymbolName(aEnvironment, orig), tohold, aEnvironment);
        // Return true
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}



/*
%mathpiper_docs,name="HoldArgument",categories="Programmer Functions;Programming;Built In"
*CMD HoldArgument --- mark argument as not evaluated
*CORE
*CALL
	HoldArgument("operator",parameter)

*PARMS

{"operator"} -- string, name of a function

{parameter} -- atom, symbolic name of parameter

*DESC
Specify that parameter should
not be evaluated before used. This will be
declared for all arities of "operator", at the moment
this function is called, so it is best called
after all {Rulebase} calls for this operator.
"operator" can be a string or atom specifying the
function name.

The {parameter} must be an atom from the list of symbolic
arguments used when calling {Rulebase}.

*SEE Rulebase, HoldArgumentNumber, RulebaseArgumentsList
%/mathpiper_docs
*/