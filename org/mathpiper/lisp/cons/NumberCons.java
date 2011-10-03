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
package org.mathpiper.lisp.cons;

import org.mathpiper.lisp.*;
import org.mathpiper.builtin.BigNumber;
import org.mathpiper.exceptions.EvaluationException;


/**
 * Holds a single number.
 *  
 */
public class NumberCons extends Cons {
    /* Note: Since NumberCons is a LispAtom, shouldn't it extend LispAtom instead of Cons? tk
     */

    /// number object; NULL if not yet converted from string
    private BigNumber iCarBigNumber;
    /// string representation in decimal; NULL if not yet converted from BigNumber
    private String iCarStringNumber;
    private ConsPointer iCdr;

    /**
     * Construct a number from either a BigNumber or a String.
     *
     * @param aNumber
     * @param aString
     */
    public NumberCons(BigNumber aNumber, String aString) throws Exception {
        super();
        iCarStringNumber = aString;
        iCarBigNumber = aNumber;
        iCdr = new ConsPointer();
    }

    /**
     * Construct a number from a BigNumber.
     * @param aNumber
     */
    public NumberCons(BigNumber aNumber) throws Exception {
        super();
        iCarStringNumber = null;
        iCarBigNumber = aNumber;
        iCdr = new ConsPointer();
    }

    /**
     * Construct a number from a decimal string representation and the specified number of decimal digits.
     *
     * @param aString a number in decimal format
     * @param aBasePrecision the number of decimal digits for the number
     */
    public NumberCons(String aString, int aBasePrecision) throws Exception {
        super();
        //(also create a number object).
        iCarStringNumber = aString;
        iCarBigNumber = null;  // purge whatever it was.
        iCdr = new ConsPointer();

    // create a new BigNumber object out of iString, set its precision in digits
    //TODO FIXME enable this in the end    NumberCons(aBasePrecision);
    }

    public Cons copy( Environment aEnvironment, boolean aRecursed) throws Exception  {

        NumberCons numberCons = new NumberCons(iCarBigNumber, iCarStringNumber);

        numberCons.setMetadataMap(this.getMetadataMap());
        
        return numberCons;
    }

    /*public Object car() {
        return iCarBigNumber;
    }*/





    /**
     * Return a string representation of the number in decimal format with the maximum decimal precision allowed by the inherent accuracy of the number.
     *
     * @return string representation of the number
     * @throws java.lang.Exception
     */
    public Object car() throws Exception {
        if (iCarStringNumber == null) {
            //LispError.lispAssert(iCarBigNumber != null, aEnvironment, aStackTop);  // either the string is null or the number but not both.

            if(iCarBigNumber == null) throw new EvaluationException("Internal error in NumberCons.","",-1);

            iCarStringNumber = iCarBigNumber.numToString(0/*TODO FIXME*/, 10);
        // export the current number to string and store it as NumberCons::iString
        }
        return iCarStringNumber;
    }

    @Override
    public String toString() {
        String stringRepresentation = null;
        try {
            stringRepresentation = (String) car();

        } catch (Exception e) {
            e.printStackTrace();  //Todo:fixme.
        }
        return stringRepresentation;

    }

    
    /**
     * Returns a BigNumber which has at least the specified precision.
     *
     * @param aPrecision
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public Object getNumber(int aPrecision, Environment aEnvironment) throws Exception {
        /// If necessary, will create a BigNumber object out of the stored string, at given precision (in decimal?)
        if (iCarBigNumber == null) {  // create and store a BigNumber out of the string representation.
            
            //LispError.lispAssert(iCarStringNumber != null, aEnvironment, aStackTop);

            if(iCarStringNumber == null) throw new EvaluationException("Internal error in NumberCons.","",-1);

            String str;
            str = iCarStringNumber;
            // aBasePrecision is in digits, not in bits, ok
            iCarBigNumber = new BigNumber(str, aPrecision, 10/*TODO FIXME BASE10*/);
        } // check if the BigNumber object has enough precision, if not, extend it
        // (applies only to floats). Note that iNumber->GetPrecision() might be < 0
        else if (!iCarBigNumber.isInteger() && iCarBigNumber.getPrecision() < aPrecision) {
            if (iCarStringNumber != null) {// have string representation, can extend precision
                iCarBigNumber.setTo(iCarStringNumber, aPrecision, 10);
            } else {
                // do not have string representation, cannot extend precision!
            }
        }

        return iCarBigNumber;
    }


    

    public ConsPointer cdr() {
        return iCdr;
    }//end method.


    public int type()
    {
        return Utility.NUMBER;
    }//end method.

}//end class.
