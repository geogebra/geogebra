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
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class MaxEvalDepth extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer index = new ConsPointer();
        index.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        LispError.checkArgument(aEnvironment, aStackTop, index.getCons() != null, 1, "MaxEvalDepth");
        LispError.checkArgument(aEnvironment, aStackTop, index.car() instanceof String, 1, "MaxEvalDepth");

        int ind = Integer.parseInt( (String) index.car(), 10);
        aEnvironment.iMaxEvalDepth = ind;
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}

/*
%mathpiper_docs,name="MaxEvalDepth",categories="User Functions;Control Flow;Built In"
*CMD MaxEvalDepth --- set the maximum evaluation depth
*CORE
*CALL
	MaxEvalDepth(n)

*PARMS

{n} -- new maximum evaluation depth

*DESC

Use this command to set the maximum evaluation depth to the integer
"n". The default value is 1000. The function {MaxEvalDepth} returns {True}.

The point of having a maximum evaluation depth is to catch any
infinite recursion. For example, after the definition {f(x) := f(x)}, evaluating the expression {f(x)} would call {f(x)}, which would
call {f(x)}, etc. The interpreter will halt if
the maximum evaluation depth is reached and an error message will be printed. Also indirect recursion, e.g.
the pair of definitions {f(x) := g(x)} and {g(x) := f(x)}, will be caught.

*E.G. notest

An example of an infinite recursion, caught because the maximum
evaluation depth is reached.

In> f(x) := f(x)
Result: True;
In> f(x)
	Error on line 1 in file [CommandLine]
	Max evaluation stack depth reached.
	Please use MaxEvalDepth to increase the stack
	size as needed.

However, a long calculation may cause the maximum evaluation depth to
be reached without the presence of infinite recursion. The function {MaxEvalDepth} is meant for these cases.

In> 10 # g(0) <-- 1;
Result: True;
In> 20 # g(n_IsPositiveInteger) <-- \
	  2 * g(n-1);
Result: True;
In> g(1001);
	Error on line 1 in file [CommandLine]
	Max evaluation stack depth reached.
	Please use MaxEvalDepth to increase the stack
	size as needed.

In> MaxEvalDepth(10000);
Result: True;
In> g(1001);
Result: 21430172143725346418968500981200036211228096234
	1106721488750077674070210224987224498639675763139171
	6255189345835106293650374290571384628087196915514939
	7149607869135549648461970842149210124742283755908364
	3060929499671638825347975351183310878921541258291423
	92955373084335320859663305248773674411336138752;
%/mathpiper_docs
 */