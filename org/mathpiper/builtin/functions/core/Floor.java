/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mathpiper.builtin.functions.core;

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;

/**
 *
 *  
 */
public class Floor extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {
        BigNumber x = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1);
        BigNumber z = new BigNumber(aEnvironment.getPrecision());
        z.floor(x);
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(z));
    }

}//end class.

/*
%mathpiper_docs,name="FloorN",categories="User Functions;Numeric;Built In"
 *CMD FloorN --- largest integer not larger than x (arbitrary-precision math function)
 *CORE
 *CALL
FloorN(x)

 *DESC

This command performs the calculation of an elementary mathematical
function.  The arguments <i>must</i> be numbers.  The reason for the
postfix {N} is that the library needs to define equivalent non-numerical
functions for symbolic computations, such as {Exp}, {Sin}, etc.

Note that all xxxN functions accept integers as well as floating-point numbers.
The resulting values may be integers or floats.  If the mathematical result is an
exact integer, then the integer is returned.  For example, {Sqrt(25)} returns
the integer {5}, and {Power(2,3)} returns the integer {8}.  In such cases, the
integer result is returned even if the calculation requires more digits than set by
{BuiltinPrecisionSet}.  However, when the result is mathematically not an integer,
the functions return a floating-point result which is correct only to the current precision.

 *E.G.
In>
Result>

%/mathpiper_docs
 */
