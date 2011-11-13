/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mathpiper.builtin.functions.core;

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.NumberCons;
import org.mathpiper.lisp.cons.SublistCons;

/**
 *
 *
 */
public class RoundToN extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        BigNumber requestedPrecision = org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 2);



        Cons argument1 = getArgumentPointer(aEnvironment, aStackTop, 1).getCons();

        if(argument1 instanceof NumberCons)
        {

            BigNumber decimalToBeRounded = new BigNumber(org.mathpiper.lisp.Utility.getNumber(aEnvironment, aStackTop, 1));

            if(decimalToBeRounded.getPrecision() != requestedPrecision.toInt())
            {
                decimalToBeRounded.setPrecision(requestedPrecision.toInt());
            }

            getTopOfStackPointer(aEnvironment, aStackTop).setCons(new org.mathpiper.lisp.cons.NumberCons(decimalToBeRounded));

            return;

        }
        else if (argument1 instanceof SublistCons)
        {
            ConsPointer consPointer = new ConsPointer(argument1);

            consPointer.goSub(aStackTop, aEnvironment);

            String functionName = ((String) consPointer.car());

            if(functionName.equals("Complex"))
            {
                consPointer.goNext(aStackTop, aEnvironment);

                BigNumber realPart = (BigNumber) ((NumberCons) (consPointer.getCons())).getNumber(aEnvironment.getPrecision(), aEnvironment);

                if(realPart.getPrecision() != requestedPrecision.toInt())
                {
                    realPart.setPrecision(requestedPrecision.toInt());
                }//end if.

                consPointer.goNext(aStackTop, aEnvironment);

                BigNumber imaginaryPart = (BigNumber) ((NumberCons) (consPointer.getCons())).getNumber(aEnvironment.getPrecision(), aEnvironment);

                if(imaginaryPart.getPrecision() != requestedPrecision.toInt())
                {
                    imaginaryPart.setPrecision(requestedPrecision.toInt());
                }//end if.



                Cons complexAtomCons = AtomCons.getInstance(aEnvironment, aStackTop, "Complex");

                Cons realNumberCons = new NumberCons(realPart);

                complexAtomCons.cdr().setCons(realNumberCons);

                Cons imaginaryNumberCons = new NumberCons(imaginaryPart);

                realNumberCons.cdr().setCons(imaginaryNumberCons);

                Cons complexSublistCons = SublistCons.getInstance(aEnvironment, complexAtomCons);

                getTopOfStackPointer(aEnvironment, aStackTop).setCons(complexSublistCons);
                
                return;
                
            }//end if.


        }//end else.

        LispError.raiseError("The first argument must be a number.", "RoundToN", aStackTop, aEnvironment);

    }//end method.


}//end class.



/*
%mathpiper_docs,name="RoundToN",categories="User Functions;Numeric;Built In"
*CMD RoundToN --- rounds a decimal number to a given precision
*CORE
*CALL
	RoundToN(decimalNumber, precision)

*PARMS
{decimalNumber} -- a decimal number to be rounded
{precision} -- precision to round the number to

*DESC

This command rounds a decimal number to a given precision.

*E.G.
In> RoundToN(7.57809824,2)
Result> 7.6

%/mathpiper_docs
*/
