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

/**
 *
 *  
 */
public class BuiltinPrecisionGet extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        // decimal getPrecision
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "" + aEnvironment.getPrecision()));
    }
}



/*
%mathpiper_docs,name="BuiltinPrecisionGet",categories="Programmer Functions;Numerical (Arbitrary Precision);Built In"
*CMD BuiltinPrecisionGet --- get the current precision
*CORE
*CALL
	BuiltinPrecisionGet()

*DESC

This command returns the current precision, as set by {BuiltinPrecisionSet}.

*E.G.

In> BuiltinPrecisionGet();
Result: 10;
In> BuiltinPrecisionSet(20);
Result: True;
In> BuiltinPrecisionGet();
Result: 20;

*SEE BuiltinPrecisionSet, N

%/mathpiper_docs
*/