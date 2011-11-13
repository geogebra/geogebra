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
public class Version extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "\"" + org.mathpiper.Version.version + "\""));
    }
}



/*
%mathpiper_docs,name="Version",categories="User Functions;Built In"
*CMD Version --- show version of MathPiper
*CORE
*CALL
	Version()

*DESC

The function {Version()} returns a string representing the version of the currently running MathPiper interpreter.

*E.G. notest

In> Version()
Result: "1.0.48rev3";
In> IsLessThan(Version(), "1.0.47")
Result: False;
In> GreaterThan(Version(), "1.0.47")
Result: True;

The last two calls show that the {IsLessThan} and {GreaterThan}
functions can be used for comparing version numbers. This
method is only guaranteed, however, if the version is always expressed
in the form {d.d.dd} as above.

*REM
Note that on the Windows platforms the output may be different:
In> Version()
Result: "Windows-latest";

*SEE IsLessThan, GreaterThan
%/mathpiper_docs
*/