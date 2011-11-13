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
public class Rule extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        org.mathpiper.lisp.Utility.newRule(aEnvironment, aStackTop, false);
    }
}



/*
%mathpiper_docs,name="Rule",categories="Programmer Functions;Programming;Built In"
*CMD Rule --- define a rewrite rule
*CORE
*CALL
	Rule("operator", arity, precedence, predicate) body
*PARMS

{"operator"} -- string, name of function

{arity}, {precedence} -- integers

{predicate} -- function returning boolean

{body} -- expression, body of rule

*DESC

Define a rule for the function "operator" with
"arity", "precedence", "predicate" and
"body". The "precedence" goes from low to high: rules with low precedence will be applied first.

The arity for a rules database equals the number of arguments. Different
rules databases can be built for functions with the same name but with
a different number of arguments.

Rules with a low precedence value will be tried before rules with a high value, so
a rule with precedence 0 will be tried before a rule with precedence 1.
%/mathpiper_docs
*/