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
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;


/**
 *
 *
 */
public class ExpressionToString extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // Get operator
        LispError.checkArgument(aEnvironment, aStackTop, evaluated.getCons() != null, 1, "ExpressionToString");



        String expressionString = Utility.printMathPiperExpression(aStackTop, evaluated, aEnvironment, 0);
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "\"" + expressionString + "\""));


    }//end method.


}//end class.



/*
%mathpiper_docs,name="ExpressionToString",categories="User Functions;String Manipulation;Built In"
*CMD ExpressionToString --- convert an expression to a string
*CORE
*CALL
	ExpressionToString(expression)

*PARMS

{expression} -- a MathPiper expression

*DESC

This function converts a MathPiper expression into string form.

*E.G.

In> ExpressionToString(x^2)
Result> "x^2"

*SEE Atom, String
%/mathpiper_docs
*/
