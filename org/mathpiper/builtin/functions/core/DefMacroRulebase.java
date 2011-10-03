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
public class DefMacroRulebase extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        org.mathpiper.lisp.Utility.defMacroRulebase(aEnvironment, aStackTop, false);
    }
}



/*
%mathpiper_docs,name="DefMacroRulebase",categories="Programmer Functions;Programming;Built In"
*CMD DefMacroRulebase --- define a function as a macro
*CORE
*CALL
	DefMacroRulebase(name,params)

*PARMS

{name} -- string, name of a function

{params} -- list of arguments

*DESC

{DefMacroRulebase} is similar to {Rulebase}, with the difference that it declares a macro,
instead of a function.
After this call, rules can be defined for the function "{name}", but their interpretation will be different.

With the usual functions, the evaluation model is that of the <i>applicative-order model of
substitution</i>, meaning that first the arguments are evaluated, and then the function
is applied to the result of evaluating these arguments. The function is entered, and the
code inside the function can not access local variables outside of its own local variables.

With macros, the evaluation model is that of the <i>normal-order model of substitution</i>,
meaning that all occurrences of variables in an expression are first substituted into the
body of the macro, and only then is the resulting expression evaluated <i>in its
calling environment</i>. This is important, because then in principle a macro body
can access the local variables from the calling environment, whereas functions can not do that.

As an example, suppose there is a function {square}, which squares its argument, and a function
{add}, which adds its arguments. Suppose the definitions of these functions are:

	add(x,y) <-- x+y;
and
	square(x) <-- x*x;
In applicative-order mode (the usual way functions are evaluated), in the following expression
	add(square(2),square(3))
first the arguments to {add} get evaluated. So, first {square(2)} is evaluated.
To evaluate this, first {2} is evaluated, but this evaluates to itself. Then
the {square} function is applied to it, {2*2}, which returns 4. The same
is done for {square(3)}, resulting in {9}. Only then, after evaluating these two
arguments, {add} is applied to them, which is equivalent to
	add(4,9)
resulting in calling {4+9}, which in turn results in {13}.

In contrast, when {add} is a macro, the arguments to {add} are first
expanded. So
	add(square(2),square(3))
first expands to
	square(2) + square(3)
and then this expression is evaluated, as if the user had written it directly.
In other words, {square(2)} is not evaluated before the macro has been fully expanded.


Macros are useful for customizing syntax, and compilers can potentially
greatly optimize macros, as they can be inlined in the calling environment,
and optimized accordingly.

There are disadvantages, however. In interpreted mode, macros are slower,
as the requirement for substitution means that a new expression to be evaluated
has to be created on the fly. Also, when one of the parameters to the macro
occur more than once in the body of the macro, it is evaluated multiple times.

When defining transformation rules for macros, the variables to be substituted
need to be preceded by the {@} operator, similar to the back-quoting mechanism.
Apart from that, the two are similar, and all transformation rules can also be
applied to macros.

Macros can co-exist with functions with the same name but different arity.
For instance, one can have a function {foo(a,b)}
with two arguments, and a macro {foo(a,b,c)} with three arguments.


*E.G.

The following example defines a macro {myfor}, and shows one use, referencing
a variable {a} from the calling environment.

In> DefMacroRulebase("myfor",{init,pred,inc,body})
Result: True;
In> myfor(_init,_pred,_inc,_body)<--[@init;While(@pred)[@body;@inc;];True;];
Result: True;
In> a:=10
Result: 10;
In> myfor(i:=1,i<10,i++,Echo(a*i))
	10
	20
	30
	40
	50
	60
	70
	80
	90
Result: True;
In> i
Result: 10;

*SEE Rulebase, `, DefMacroRulebaseListed
%/mathpiper_docs
*/