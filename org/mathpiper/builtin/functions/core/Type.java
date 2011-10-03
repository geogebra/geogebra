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
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;


/**
 *
 *  
 */
public class Type extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        ConsPointer evaluated = new ConsPointer();

        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        String functionType = Utility.functionType(evaluated);

        if (functionType.equals("")) {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "\"\""));
        } else {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, aEnvironment.getTokenHash().lookUpStringify(functionType)));
        }
    }//end method.


}//end class.



/*
%mathpiper_docs,name="Type",categories="User Functions;Lists (Operations);Built In"
*CMD Type --- return the type of an expression
*CORE
*CALL
	Type(expr)

*PARMS

{expr} -- expression to examine

*DESC

The type of the expression "expr" is represented as a string and
returned. So, if "expr" is a list, the string {"List"} is returned. In general, the top-level
operator of "expr" is returned. If the argument "expr" is an atom,
the result is the empty string {""}.

*E.G.

In> Type({a,b,c});
Result: "List";
In> Type(a*(b+c));
Result: "*";
In> Type(123);
Result: "";

*SEE IsAtom, ArgumentsCount
%/mathpiper_docs
*/
