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
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.cons.BuiltinObjectCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 *
 *  
 */
public class ArrayCreate extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer sizearg = new ConsPointer();
        sizearg.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        LispError.checkArgument(aEnvironment, aStackTop, sizearg.getCons() != null, 1, "ArrayCreate");
        LispError.checkArgument(aEnvironment, aStackTop, sizearg.car() instanceof String, 1, "ArrayCreate");

        int size = Integer.parseInt( (String) sizearg.car(), 10);

        ConsPointer initarg = new ConsPointer();
        initarg.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());

        Array array = new Array(aEnvironment, size, initarg.getCons());
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(BuiltinObjectCons.getInstance(aEnvironment, aStackTop, array));
    }
}//end class.



/*
%mathpiper_docs,name="ArrayCreate",categories="Programmer Functions;Native Objects;Built In"
*CMD ArrayCreate --- create array
*CORE
*CALL
	ArrayCreate(size,init)

*DESC
Creates an array with {size} elements, all initialized to the
value {init}.

%/mathpiper_docs
*/
