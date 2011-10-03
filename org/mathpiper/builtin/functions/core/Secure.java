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

/**
 *
 *  
 */
public class Secure extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        boolean prevSecure = aEnvironment.iSecure;
        aEnvironment.iSecure = true;
        try
        {
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), getArgumentPointer(aEnvironment, aStackTop, 1));
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            aEnvironment.iSecure = prevSecure;
        }
    }
}



/*
%mathpiper_docs,name="Secure",categories="User Functions;Built In"
*CMD Secure --- guard the host OS
*CORE
*CALL
	Secure(body)

*PARMS

{body} -- expression

*DESC

{Secure} evaluates {body} in a "safe" environment, where files cannot be opened
and system calls are not allowed. This can help protect the system
when e.g. a script is sent over the
Internet to be evaluated on a remote computer, which is potentially unsafe.

*SEE SystemCall
%/mathpiper_docs
*/