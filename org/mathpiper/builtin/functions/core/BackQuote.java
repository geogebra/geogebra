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
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class BackQuote extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        org.mathpiper.lisp.behaviours.BackQuoteSubstitute behaviour = new org.mathpiper.lisp.behaviours.BackQuoteSubstitute(aEnvironment);
        ConsPointer result = new ConsPointer();
        Utility.substitute(aEnvironment, aStackTop, result, getArgumentPointer(aEnvironment, aStackTop, 1), behaviour);
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), result);
    }
}



/*
%mathpiper_docs,name="`",categories="Operators"
*A {`}
*CMD Backquoting --- macro expansion (LISP-style backquoting)
*CORE
*CALL
	`(expression)

*PARMS

{expression} -- expression containing "{@var}" combinations to substitute the value of variable "{var}"

*DESC

Backquoting is a macro substitution mechanism. A backquoted {expression}
is evaluated in two stages: first, variables prefixed by {@} are evaluated
inside an expression, and second, the new expression is evaluated.

To invoke this functionality, a backquote {`} needs to be placed in front of
an expression. Parentheses around the expression are needed because the
backquote binds tighter than other operators.

The expression should contain some variables (assigned atoms) with the special
prefix operator {@}. Variables prefixed by {@} will be evaluated even if they
are inside function arguments that are normally not evaluated (e.g. functions
declared with {HoldArgument}). If the {@var} pair is in place of a function name,
e.g. "{@f(x)}", then at the first stage of evaluation the function name itself
is replaced, not the return value of the function (see example); so at the
second stage of evaluation, a new function may be called.

One way to view backquoting is to view it as a parametric expression
generator. {@var} pairs get substituted with the value of the variable {var}
even in contexts where nothing would be evaluated. This effect can be also
achieved using {ListToFunction} and {Hold} but the resulting code is much more
difficult to read and maintain.

This operation is relatively slow since a new expression is built
before it is evaluated, but nonetheless backquoting is a powerful mechanism
that sometimes allows to greatly simplify code.

*E.G.

This example defines a function that automatically evaluates to a number as
soon as the argument is a number (a lot of functions  do this only when inside
a {N(...)} section).

In> Decl(f1,f2) := \
In>   `(@f1(x_IsNumber) <-- N(@f2(x)));
Result: True;
In> Decl(nSin,Sin)
Result: True;
In> Sin(1)
Result: Sin(1);
In> nSin(1)
Result: 0.8414709848;

This example assigns the expression {func(value)} to variable {var}. Normally
the first argument of {Bind} would be unevaluated.

In> SetF(var,func,value) := \
In>     `(Bind(@var,@func(@value)));
Result: True;
In> SetF(a,Sin,x)
Result: True;
In> a
Result: Sin(x);


*SEE MacroBind, MacroLocal, MacroRulebase, Hold, HoldArgument, DefMacroRulebase, MacroExpand
%/mathpiper_docs
*/