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
public class TraceRule extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        aEnvironment.write("Function not yet implemented : LispTraceRule");////TODO fixme

        throw new EvaluationException("Function not yet supported",aEnvironment.iInputStatus.fileName(), aEnvironment.iCurrentInput.iStatus.lineNumber());
    }
}



/*
%mathpiper_docs,name="TraceRule",categories="User Functions;Control Flow;Built In",access="private"
*CMD TraceRule --- turn on tracing for a particular function
*CORE
*CALL
	TraceRule(template) expr

*PARMS

{template} -- template showing the operator to trace

{expr} -- expression to evaluate with tracing on

*DESC

The tracing facility is turned on for subexpressions of the form
"template", and the expression "expr" is evaluated. The template
"template" is an example of the function to trace on. Specifically, all
subexpressions with the same top-level operator and arity as "template"
are shown. The subexpressions are displayed before (indicated with {TrEnter}) and after ({TrLeave})
evaluation. In between, the arguments are shown before and after
evaluation ({TrArg}). Only functions defined in
scripts can be traced.

This is useful for tracing a function that is called from within
another function. This way you can see how your function behaves
in the environment it is used in.

*E.G. notest

In> TraceRule(x+y) 2+3*5+4;
	    TrEnter(2+3*5+4);
	      TrEnter(2+3*5);
	          TrArg(2, 2);
	          TrArg(3*5, 15);
	      TrLeave(2+3*5, 17);
	        TrArg(2+3*5, 17);
	        TrArg(4, 4);
	    TrLeave(2+3*5+4, 21);
Result: 21;

*SEE TraceStack, TraceExp
%/mathpiper_docs
*/