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

import java.math.BigDecimal;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.printers.MathPiperPrinter;

/**
 *
 *  
 */
public class Time extends BuiltinFunction
{

     private Time()
    {
    }

    public Time(Environment aEnvironment)
    {
        aEnvironment.iBodiedOperators.setOperator(MathPiperPrinter.KMaxPrecedence, "Time");
    }//end constructor.

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        BigDecimal startTime = new BigDecimal(System.nanoTime());

        ConsPointer res = new ConsPointer();

        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, res, getArgumentPointer(aEnvironment, aStackTop, 1));

        BigDecimal endTime = new BigDecimal(System.nanoTime());

        BigDecimal timeDiff;

        timeDiff = endTime.subtract(startTime);

        timeDiff = timeDiff.movePointLeft(9);
        
        getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "" + timeDiff));
    }
}



/*
%mathpiper_docs,name="Time",categories="User Functions;Built In;Input/Output"
*CMD Time --- measure the time taken by an evaluation
*CORE
*CALL
	Time() expr
*PARMS
{expr} -- any expression
*DESC
The function {Time() expr} evaluates the expression {expr} and returns the time needed for the evaluation.
The result is returned as a floating-point number of seconds.
The value of the expression {expr} is lost.

The result is the "user time" as reported by the OS, not the real ("wall clock") time.
Therefore, any CPU-intensive processes running alongside MathPiper will not significantly affect the result of {Time}.

*E.G.
In> Time() Simplify((a*b)/(b*a))
Result: 0.09;

*SEE EchoTime, SystemTimer

%/mathpiper_docs
*/