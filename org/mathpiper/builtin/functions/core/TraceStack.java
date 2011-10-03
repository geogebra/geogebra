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
import org.mathpiper.exceptions.EvaluationException;
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
public class TraceStack extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        aEnvironment.write("Function not yet implemented : TraceStack");////TODO fixme

        throw new EvaluationException("Function not yet supported",aEnvironment.iInputStatus.fileName(), aEnvironment.iCurrentInput.iStatus.lineNumber());
    }
}



/*
%mathpiper_docs,name="TraceStack",categories="User Functions;Control Flow;Built In",access="private"
*CMD TraceStack --- show calling stack after an error occurs
*CORE
*CALL
	TraceStack(expression)

*PARMS

{expression} -- an expression to evaluate

*DESC

TraceStack shows the calling stack after an error occurred.
It shows the last few items on the stack, not to flood the screen.
These are usually the only items of interest on the stack.
This is probably by far the most useful debugging function in
MathPiper. It shows the last few things it did just after an error
was generated somewhere.

For each stack frame, it shows if the function evaluated was a
built-in function or a user-defined function, and for the user-defined
function, the number of the rule it is trying whether it was evaluating
the pattern matcher of the rule, or the body code of the rule.

This functionality is not offered by default because it slows
down the evaluation code.

*E.G. notest

Here is an example of a function calling itself recursively,
causing MathPiper to flood its stack:

In> f(x):=f(Sin(x))
Result: True;
In> TraceStack(f(2))
	Debug> 982 :  f (Rule # 0 in body)
	Debug> 983 :  f (Rule # 0 in body)
	Debug> 984 :  f (Rule # 0 in body)
	Debug> 985 :  f (Rule # 0 in body)
	Debug> 986 :  f (Rule # 0 in body)
	Debug> 987 :  f (Rule # 0 in body)
	Debug> 988 :  f (Rule # 0 in body)
	Debug> 989 :  f (Rule # 0 in body)
	Debug> 990 :  f (Rule # 0 in body)
	Debug> 991 :  f (Rule # 0 in body)
	Debug> 992 :  f (Rule # 0 in body)
	Debug> 993 :  f (Rule # 0 in body)
	Debug> 994 :  f (Rule # 0 in body)
	Debug> 995 :  f (User function)
	Debug> 996 :  Sin (Rule # 0 in pattern)
	Debug> 997 :  IsList (Internal function)
	Error on line 1 in file [CommandLine]
	Max evaluation stack depth reached.
	Please use MaxEvalDepth to increase the stack
	size as needed.

*SEE TraceExp, TraceRule
%/mathpiper_docs
*/