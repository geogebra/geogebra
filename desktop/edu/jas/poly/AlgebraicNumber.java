/*
 * $Id: AlgebraicNumber.java 3212 2010-07-05 12:54:49Z kredel $
 */

package edu.jas.poly;


import edu.jas.structure.RingFactory;
import edu.jas.kern.PrettyPrint;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * Algebraic number class based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class AlgebraicNumber<C extends GcdRingElem<C>> 
             implements GcdRingElem<AlgebraicNumber<C>> {


    /**
     * Ring part of the data structure.
     */
    public final AlgebraicNumberRing<C> ring;


    /**
     * Value part of the element data structure.
     */
    public final GenPolynomial<C> val;


    /**
     * Flag to remember if this algebraic number is a unit. -1 is unknown, 1 is
     * unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /**
     * The constructor creates a AlgebraicNumber object from AlgebraicNumberRing
     * modul and a GenPolynomial value.
     * @param r ring AlgebraicNumberRing<C>.
     * @param a value GenPolynomial<C>.
     */
    public AlgebraicNumber(AlgebraicNumberRing<C> r, GenPolynomial<C> a) {
        ring = r; // assert r != 0
        val = a.remainder(ring.modul); //.monic() no go
        if (val.isZERO()) {
            isunit = 0;
        }
        if (ring.isField()) {
            isunit = 1;
        }
    }


    /**
     * The constructor creates a AlgebraicNumber object from a GenPolynomial
     * object module.
     * @param r ring AlgebraicNumberRing<C>.
     */
    public AlgebraicNumber(AlgebraicNumberRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * Get the value part.
     * @return val.
     */
    public GenPolynomial<C> getVal() {
        return val;
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public AlgebraicNumberRing<C> factory() {
        return ring;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public AlgebraicNumber<C> clone() {
        return new AlgebraicNumber<C>(ring, val);
    }


    /**
     * Is AlgebraicNumber zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.equals(ring.ring.getZERO());
    }


    /**
     * Is AlgebraicNumber one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.equals(ring.ring.getONE());
    }


    /**
     * Is AlgebraicNumber unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (isunit > 0) {
            return true;
        }
        if (isunit == 0) {
            return false;
        }
        // not jet known
        if (val.isZERO()) {
            isunit = 0;
            return false;
        }
        if (ring.isField()) {
            isunit = 1;
            return true;
        }
        boolean u = val.gcd(ring.modul).isUnit();
        if (u) {
            isunit = 1;
        } else {
            isunit = 0;
        }
        return (u);
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (PrettyPrint.isTrue()) {
            return val.toString(ring.ring.vars);
        } else {
            return "AlgebraicNumber[ " + val.toString() + " ]";
        }
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return val.toScript();
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * AlgebraicNumber comparison.
     * @param b AlgebraicNumber.
     * @return sign(this-b).
     */
    //JAVA6only: @Override
    public int compareTo(AlgebraicNumber<C> b) {
        int s = 0;
        if ( ring.modul != b.ring.modul ) { // avoid compareTo if possible
           s = ring.modul.compareTo( b.ring.modul );
        }
        if ( s != 0 ) {
            return s;
        }
        return val.compareTo(b.val);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    // not jet working
    public boolean equals(Object b) {
        if (!(b instanceof AlgebraicNumber)) {
            return false;
        }
        AlgebraicNumber<C> a = null;
        try {
            a = (AlgebraicNumber<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        if ( !ring.equals( a.ring ) ) {
            return false;
        }
        return (0 == compareTo(a));
    }


    /**
     * Hash code for this AlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * val.hashCode() + ring.hashCode();
    }


    /**
     * AlgebraicNumber absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public AlgebraicNumber<C> abs() {
        return new AlgebraicNumber<C>(ring, val.abs());
    }


    /**
     * AlgebraicNumber summation.
     * @param S AlgebraicNumber.
     * @return this+S.
     */
    public AlgebraicNumber<C> sum(AlgebraicNumber<C> S) {
        return new AlgebraicNumber<C>(ring, val.sum(S.val));
    }


    /**
     * AlgebraicNumber summation.
     * @param c coefficient.
     * @return this+c.
     */
    public AlgebraicNumber<C> sum(GenPolynomial<C> c) {
        return new AlgebraicNumber<C>(ring, val.sum(c));
    }


    /**
     * AlgebraicNumber summation.
     * @param c polynomial.
     * @return this+c.
     */
    public AlgebraicNumber<C> sum(C c) {
        return new AlgebraicNumber<C>(ring, val.sum(c));
    }


    /**
     * AlgebraicNumber negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public AlgebraicNumber<C> negate() {
        return new AlgebraicNumber<C>(ring, val.negate());
    }


    /**
     * AlgebraicNumber signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /**
     * AlgebraicNumber subtraction.
     * @param S AlgebraicNumber.
     * @return this-S.
     */
    public AlgebraicNumber<C> subtract(AlgebraicNumber<C> S) {
        return new AlgebraicNumber<C>(ring, val.subtract(S.val));
    }


    /**
     * AlgebraicNumber division.
     * @param S AlgebraicNumber.
     * @return this/S.
     */
    public AlgebraicNumber<C> divide(AlgebraicNumber<C> S) {
        return multiply(S.inverse());
    }


    /**
     * AlgebraicNumber inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S = 1/this if defined.
     */
    public AlgebraicNumber<C> inverse() {
        try {
            return new AlgebraicNumber<C>(ring, val.modInverse(ring.modul));
        } catch (NotInvertibleException e) {
            throw new NotInvertibleException("val = " + val + ", modul = " + ring.modul + ", gcd = " + val.gcd(ring.modul));
        }
    }


    /**
     * AlgebraicNumber remainder.
     * @param S AlgebraicNumber.
     * @return this - (this/S)*S.
     */
    public AlgebraicNumber<C> remainder(AlgebraicNumber<C> S) {
        if ( S == null || S.isZERO()) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        if ( S.isONE()) {
           return ring.getZERO();
        }
        if ( S.isUnit() ) {
           return ring.getZERO();
        }
        GenPolynomial<C> x = val.remainder(S.val);
        return new AlgebraicNumber<C>(ring, x);
    }


    /**
     * AlgebraicNumber multiplication.
     * @param S AlgebraicNumber.
     * @return this*S.
     */
    public AlgebraicNumber<C> multiply(AlgebraicNumber<C> S) {
        GenPolynomial<C> x = val.multiply(S.val);
        return new AlgebraicNumber<C>(ring, x);
    }


    /**
     * AlgebraicNumber multiplication.
     * @param c coefficient.
     * @return this*c.
     */
    public AlgebraicNumber<C> multiply(C c) {
        GenPolynomial<C> x = val.multiply(c);
        return new AlgebraicNumber<C>(ring, x);
    }


    /**
     * AlgebraicNumber multiplication.
     * @param c polynomial.
     * @return this*c.
     */
    public AlgebraicNumber<C> multiply(GenPolynomial<C> c) {
        GenPolynomial<C> x = val.multiply(c);
        return new AlgebraicNumber<C>(ring, x);
    }


    /**
     * AlgebraicNumber monic.
     * @return this with monic value part.
     */
    public AlgebraicNumber<C> monic() {
        return new AlgebraicNumber<C>(ring, val.monic());
    }


    /**
     * AlgebraicNumber greatest common divisor.
     * @param S AlgebraicNumber.
     * @return gcd(this,S).
     */
    public AlgebraicNumber<C> gcd(AlgebraicNumber<C> S) {
        if (S.isZERO()) {
            return this;
        }
        if (isZERO()) {
            return S;
        }
        if (isUnit() || S.isUnit()) {
            return ring.getONE();
        }
        return new AlgebraicNumber<C>(ring, val.gcd(S.val));
    }


    /**
     * AlgebraicNumber extended greatest common divisor.
     * @param S AlgebraicNumber.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public AlgebraicNumber<C>[] egcd(AlgebraicNumber<C> S) {
        AlgebraicNumber<C>[] ret = new AlgebraicNumber[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (isZERO()) {
            ret[0] = S;
            return ret;
        }
        if (this.isUnit() || S.isUnit()) {
            ret[0] = ring.getONE();
            if (this.isUnit() && S.isUnit()) {
                AlgebraicNumber<C> half = ring.fromInteger(2).inverse();
                ret[1] = this.inverse().multiply(half);
                ret[2] = S.inverse().multiply(half);
                return ret;
            }
            if (this.isUnit()) {
                // oder inverse(S-1)?
                ret[1] = this.inverse();
                ret[2] = ring.getZERO();
                return ret;
            }
            // if ( S.isUnit() ) {
            // oder inverse(this-1)?
            ret[1] = ring.getZERO();
            ret[2] = S.inverse();
            return ret;
            //}
        }
        //System.out.println("this = " + this + ", S = " + S);
        GenPolynomial<C>[] qr;
        GenPolynomial<C> q = this.val;
        GenPolynomial<C> r = S.val;
        GenPolynomial<C> c1 = ring.ring.getONE();
        GenPolynomial<C> d1 = ring.ring.getZERO();
        GenPolynomial<C> c2 = ring.ring.getZERO();
        GenPolynomial<C> d2 = ring.ring.getONE();
        GenPolynomial<C> x1;
        GenPolynomial<C> x2;
        while (!r.isZERO()) {
            qr = q.divideAndRemainder(r);
            q = qr[0];
            x1 = c1.subtract(q.multiply(d1));
            x2 = c2.subtract(q.multiply(d2));
            c1 = d1;
            c2 = d2;
            d1 = x1;
            d2 = x2;
            q = r;
            r = qr[1];
        }
        //System.out.println("q = " + q + "\n c1 = " + c1 + "\n c2 = " + c2);
        ret[0] = new AlgebraicNumber<C>(ring, q);
        ret[1] = new AlgebraicNumber<C>(ring, c1);
        ret[2] = new AlgebraicNumber<C>(ring, c2);
        return ret;
    }

}
