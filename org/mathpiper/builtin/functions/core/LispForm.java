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
import org.mathpiper.lisp.printers.LispPrinter;

/**
 *
 * 
 */
public class LispForm extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        LispPrinter printer = new LispPrinter();
        printer.print(aStackTop, getTopOfStackPointer(aEnvironment, aStackTop), aEnvironment.iCurrentOutput, aEnvironment);
        aEnvironment.write("\n");
    }
}



/*
%mathpiper_docs,name="LispForm",categories="User Functions;Input/Output;Built In"
*CMD LispForm --- print an expression in LISP-format
*CORE
*CALL
	LispForm(expr)

*PARMS

{expr} -- expression to be printed in LISP-format

*DESC

Evaluates "expr", and prints it in LISP-format on the current
output. It is followed by a newline. The evaluated expression is also
returned.

This can be useful if you want to study the internal representation of
a certain expression.

*E.G. notest

In> LispForm(a+b+c);
	(+ (+ a b )c )
Result: a+b+c;
In> LispForm(2*I*b^2);
	(* (Complex 0 2 )(^ b 2 ))
Result: Complex(0,2)*b^2;

The first example shows how the expression {a+b+c} is
internally represented. In the second example, {2*I} is
first evaluated to {Complex(0,2)} before the expression
is printed.

*SEE LispRead, ViewList
%/mathpiper_docs
*/