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
public class RulebaseListed extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        org.mathpiper.lisp.Utility.rulebase(aEnvironment, aStackTop, true);
    }
}



/*
%mathpiper_docs,name="RulebaseListed",categories="Programmer Functions;Programming;Built In"
*CMD RulebaseListed --- define function with variable number of arguments
*CORE
*CALL
	RulebaseListed("name", params)

*PARMS

{"name"} -- string, name of function

{params} -- list of arguments to function

*DESC

The command {RulebaseListed} defines a new function. It essentially works the
same way as {Rulebase}, except that it declares a new function with a variable
number of arguments. The list of parameters {params} determines the smallest
number of arguments that the new function will accept. If the number of
arguments passed to the new function is larger than the number of parameters in
{params}, then the last argument actually passed to the new function will be a
list containing all the remaining arguments.

A function defined using {RulebaseListed} will appear to have the arity equal
to the number of parameters in the {param} list, and it can accept any number
of arguments greater or equal than that. As a consequence, it will be impossible to define a new function with the same name and with a greater arity.

The function body will know that the function is passed more arguments than the
length of the {param} list, because the last argument will then be a list. The
rest then works like a {Rulebase}-defined function with a fixed number of
arguments. Transformation rules can be defined for the new function as usual.


*E.G.

The definitions

	RulebaseListed("f",{a,b,c})
	10 # f(_a,_b,{_c,_d}) <--
	  Echo({"four args",a,b,c,d});
	20 # f(_a,_b,c_IsList) <--
	  Echo({"more than four args",a,b,c});
	30 # f(_a,_b,_c) <-- Echo({"three args",a,b,c});
give the following interaction:

In> f(A)
Result: f(A);
In> f(A,B)
Result: f(A,B);
In> f(A,B,C)
	three args A B C
Result: True;
In> f(A,B,C,D)
	four args A B C D
Result: True;
In> f(A,B,C,D,E)
	more than four args A B {C,D,E}
Result: True;
In> f(A,B,C,D,E,E)
	more than four args A B {C,D,E,E}
Result: True;

The function {f} now appears to occupy all arities greater than 3:

In> Rulebase("f", {x,y,z,t});
	CommandLine(1) : Rule base with this arity
	  already defined


*SEE Rulebase, Retract, Echo
%/mathpiper_docs
*/