/*
 * $Id: PrimitiveElement.java 3169 2010-06-05 10:44:44Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;

import edu.jas.structure.GcdRingElem;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;


/**
 * Container for primitive elements.
 * @author Heinz Kredel
 */
public class PrimitiveElement<C extends GcdRingElem<C>> implements Serializable {


    /**
     * The primitive element.
     */
    public final  AlgebraicNumberRing<C> primitiveElem;


    /**
     * The representation of the first algebraic element in the new ring.
     */
    public final AlgebraicNumber<C> A;


    /**
     * The representation of the second algebraic element in the new ring.
     */
    public final AlgebraicNumber<C> B;


    /**
     * The first algebraic ring.
     */
    public final  AlgebraicNumberRing<C> Aring;


    /**
     * The second algebraic ring.
     */
    public final  AlgebraicNumberRing<C> Bring;


    /**
     * Constructor not for use.
     */
    protected PrimitiveElement() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param pe the primitive element
     * @param A the first element.
     * @param B the second element.
     */
    protected PrimitiveElement(AlgebraicNumberRing<C> pe, AlgebraicNumber<C> A, AlgebraicNumber<C> B) {
        this(pe, A, B, null, null);
    }


    /**
     * Constructor.
     * @param pe the primitive element
     * @param A the first element.
     * @param B the second element.
     */
    protected PrimitiveElement(AlgebraicNumberRing<C> pe, AlgebraicNumber<C> A, AlgebraicNumber<C> B,
                               AlgebraicNumberRing<C> ar, AlgebraicNumberRing<C> br ) {
        primitiveElem = pe;
        this.A = A;
        this.B = B;
        this.Aring = ar;
        this.Bring = br;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("[");
        s.append(primitiveElem.toString());
        s.append(", " + A.toString());
        s.append(", " + B.toString());
        if (Aring != null) {
            s.append(", " + Aring.toString());
        }
        if (Bring != null) {
            s.append(", " + Bring.toString());
        }
        return s + "]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("(");
        s.append(primitiveElem.toScript());
        s.append(", " + A.toScript());
        s.append(", " + B.toScript());
        if (Aring != null) {
            s.append(", " + Aring.toScript());
        }
        if (Bring != null) {
            s.append(", " + Bring.toScript());
        }
        return s + ")";
    }

}
