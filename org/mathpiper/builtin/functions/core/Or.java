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
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.SublistCons;

/**
 *
 *  
 */
public class Or extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer nogos = new ConsPointer();
        int nrnogos = 0;

        ConsPointer evaluated = new ConsPointer();

        ConsTraverser consTraverser = new ConsTraverser(aEnvironment, (ConsPointer) getArgumentPointer(aEnvironment, aStackTop, 1).car());
        consTraverser.goNext(aStackTop);
        while (consTraverser.getCons() != null)
        {
            aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, evaluated, consTraverser.getPointer());
            if (Utility.isTrue(aEnvironment, evaluated, aStackTop))
            {
                Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
                return;
            } else if (!Utility.isFalse(aEnvironment, evaluated, aStackTop))
            {
                ConsPointer ptr = new ConsPointer();
                nrnogos++;

                ptr.setCons(evaluated.getCons().copy( aEnvironment, false));
                ptr.cdr().setCons(nogos.getCons());
                nogos.setCons(ptr.getCons());
            }
            consTraverser.goNext(aStackTop);
        }

        if (nogos.getCons() != null)
        {
            if (nrnogos == 1)
            {
                getTopOfStackPointer(aEnvironment, aStackTop).setCons(nogos.getCons());
            } else
            {
                ConsPointer ptr = new ConsPointer();

                Utility.reverseList(aEnvironment, ptr, nogos);
                nogos.setCons(ptr.getCons());

                ptr.setCons(getArgumentPointer(aEnvironment, aStackTop, 0).getCons().copy( aEnvironment, false));
                ptr.cdr().setCons(nogos.getCons());
                nogos.setCons(ptr.getCons());
                getTopOfStackPointer(aEnvironment, aStackTop).setCons(SublistCons.getInstance(aEnvironment,nogos.getCons()));
            }
        //aEnvironment.CurrentPrinter().Print(getTopOfStackPointer(aEnvironment, aStackTop), *aEnvironment.CurrentOutput());
        } else
        {
            Utility.putFalseInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
        }
    }
}



/*
%mathpiper_docs,name="Or",categories="User Functions;Predicates;Built In"
*CMD Or --- logical disjunction
*CORE
*CALL
	a1 Or a2
Precedence:
*EVAL PrecedenceGet("Or")
	Or(a1, a2, a3, ..., aN)

*PARMS

{a}1, ..., {a}N -- boolean expressions (may evaluate to {True} or {False})

*DESC

This function returns {True} if an argument is encountered
that is true (scanning from left to right). The
{Or} operation is "lazy", i.e. it returns {True} as soon as a {True} argument
is found (from left to right). If an argument other than {True} or
{False} is encountered, an unevaluated {Or} expression is returned with all
arguments that didn't evaluate to {True} or {False} yet.
 {And(...)} and {Or(...)} do also exist, defined in the script
library. You can redefine them as infix operators yourself, so you have the
choice of precedence. In the standard scripts they are in fact declared as
infix operators, so you can write {expr1 And expr}.

*E.G.

In> True Or False
Result: True;
In> False Or a
Result: Or(a);
In> Or(False,a,b,True)
Result: True;

*SEE And, Not
%/mathpiper_docs
*/