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

import org.mathpiper.builtin.Array;
import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class ArraySet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer evaluated = new ConsPointer();
        evaluated.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        BuiltinContainer gen = (BuiltinContainer) evaluated.car();
        LispError.checkArgument(aEnvironment, aStackTop, gen != null, 1, "ArraySet");
        LispError.checkArgument(aEnvironment, aStackTop, gen.typeName().equals("\"Array\""), 1, "ArraySet");

        ConsPointer sizearg = new ConsPointer();
        sizearg.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());

        LispError.checkArgument(aEnvironment, aStackTop, sizearg.getCons() != null, 2, "ArraySet");
        LispError.checkArgument(aEnvironment, aStackTop, sizearg.car() instanceof String, 2, "ArraySet");

        int size = Integer.parseInt( (String) sizearg.car(), 10);
        LispError.checkArgument(aEnvironment, aStackTop, size > 0 && size <= ((Array) gen).size(), 2, "ArraySet");

        ConsPointer obj = new ConsPointer();
        obj.setCons(getArgumentPointer(aEnvironment, aStackTop, 3).getCons());
        ((Array) gen).setElement(size, obj.getCons(), aStackTop, aEnvironment);
        Utility.putTrueInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop));
    }
}//end class.



/*
%mathpiper_docs,name="ArraySet",categories="Programmer Functions;Native Objects;Built In"
*CMD ArraySet --- set array element
*CORE
*CALL
	ArraySet(array,index,element)

*DESC
Sets the element at position index in the array passed to the value
passed in as argument to element. Arrays are treated
as base-one, so {index} set to 1 would set car element.

Arrays can also be accessed through the {[]} operators. So
{array[index] := element} would do the same as {ArraySet(array, index,element)}.

%/mathpiper_docs
*/
