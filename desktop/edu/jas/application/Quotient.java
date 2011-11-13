/*
 * $Id: Quotient.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.application;


import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;

import edu.jas.kern.PrettyPrint;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;


/**
 * Quotient, i.e. rational function, based on GenPolynomial with RingElem
 * interface. Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class Quotient<C extends GcdRingElem<C>> implements GcdRingElem<Quotient<C>> {


    private static final Logger logger = Logger.getLogger(Quotient.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Quotient class factory data structure.
     */
    public final QuotientRing<C> ring;


    /**
     * Numerator part of the element data structure.
     */
    public final GenPolynomial<C> num;


    /**
     * Denominator part of the element data structure.
     */
    public final GenPolynomial<C> den;


    /**
     * The constructor creates a Quotient object from a ring factory.
     * @param r ring factory.
     */
    public Quotient(QuotientRing<C> r) {
        this(r, r.ring.getZERO());
    }


    /**
     * The constructor creates a Quotient object from a ring factory and a
     * numerator polynomial. The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator polynomial.
     */
    public Quotient(QuotientRing<C> r, GenPolynomial<C> n) {
        this(r, n, r.ring.getONE(), true);
    }


    /**
     * The constructor creates a Quotient object from a ring factory and a
     * numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public Quotient(QuotientRing<C> r, GenPolynomial<C> n, GenPolynomial<C> d) {
        this(r, n, d, false);
    }


    /**
     * The constructor creates a Quotient object from a ring factory and a
     * numerator and denominator polynomial.
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    protected Quotient(QuotientRing<C> r, GenPolynomial<C> n, GenPolynomial<C> d, boolean isred) {
        if (d == null || d.isZERO()) {
            throw new RuntimeException("denominator may not be zero");
        }
        ring = r;
        if (d.signum() < 0) {
            n = n.negate();
            d = d.negate();
        }
        if (!isred) {
            // must reduce to lowest terms
            GenPolynomial<C> gcd = ring.gcd(n, d);
            if (false || debug) {
                logger.info("gcd = " + gcd);
            }
            //GenPolynomial<C> gcd = ring.ring.getONE();
            if (!gcd.isONE()) {
                //logger.info("gcd = " + gcd);
                n = ring.divide(n, gcd);
                d = ring.divide(d, gcd);
            }
        }
        C lc = d.leadingBaseCoefficient();
        if ( !lc.isONE() && lc.isUnit() ) {
            lc = lc.inverse();
            n  = n.multiply(lc);
            d  = d.multiply(lc);
        }
        num = n;
        den = d;
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public QuotientRing<C> factory() {
        return ring;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Quotient<C> clone() {
        return new Quotient<C>(ring, num, den, true);
    }


    /**
     * Is Quotient zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /**
     * Is Quotient one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.equals(den);
    }


    /**
     * Is Quotient a unit.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (num.isZERO()) {
            return false;
        } else {
            return true;
        }
    }


    /** Is Qoutient a constant. 
     * @return true, if this has constant numerator and denominator, else false.
     */
    public boolean isConstant() {
        return num.isConstant() && den.isConstant();
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (PrettyPrint.isTrue()) {
            String s = "{ " + num.toString(ring.ring.getVars());
            if (!den.isONE()) {
                s += " | " + den.toString(ring.ring.getVars());
            }
            return s + " }";
        } else {
            return "Quotient[ " + num.toString() + " | " + den.toString() + " ]";
        }
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        if ( den.isONE() ) {
            return num.toScript();
        } else {
            return num.toScript() + " / " + den.toScript();
        }
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
     * Quotient comparison.
     * @param b Quotient.
     * @return sign(this-b).
     */
    //JAVA6only: @Override
    public int compareTo(Quotient<C> b) {
        if (b == null || b.isZERO()) {
            return this.signum();
        }
        if (this.isZERO()) {
            return -b.signum();
        }
        int s1 = num.signum();
        int s2 = b.num.signum();
        int t = (s1 - s2) / 2;
        if (t != 0) {
            return t;
        }
        GenPolynomial<C> r = num.multiply(b.den);
        GenPolynomial<C> s = den.multiply(b.num);
        //GenPolynomial<C> x = r.subtract( s );
        //return x.signum();
        return r.compareTo(s);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    // not jet working
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof Quotient)) {
            return false;
        }
        Quotient<C> a = null;
        try {
            a = (Quotient<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        //return ( 0 == compareTo( a ) );
        return num.equals(a.num) && den.equals(a.den);
    }


    /**
     * Hash code for this local.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = ring.hashCode();
        h = 37 * h + num.hashCode();
        h = 37 * h + den.hashCode();
        return h;
    }


    /**
     * Quotient absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Quotient<C> abs() {
        return new Quotient<C>(ring, num.abs(), den, true);
    }


    /**
     * Quotient summation.
     * @param S Quotient.
     * @return this+S.
     */
    public Quotient<C> sum(Quotient<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        GenPolynomial<C> n;
        if (den.isONE() && S.den.isONE()) {
            n = num.sum(S.num);
            return new Quotient<C>(ring, n);
        }
        if (den.isONE()) {
            n = num.multiply(S.den);
            n = n.sum(S.num);
            return new Quotient<C>(ring, n, S.den, true);
        }
        if (S.den.isONE()) {
            n = S.num.multiply(den);
            n = n.sum(num);
            return new Quotient<C>(ring, n, den, true);
        }
        GenPolynomial<C> d;
        GenPolynomial<C> sd;
        GenPolynomial<C> g;
        g = ring.gcd(den, S.den);
        if (g.isONE()) {
            d = den;
            sd = S.den;
        } else {
            d = ring.divide(den, g);
            sd = ring.divide(S.den, g);
        }
        n = num.multiply(sd);
        n = n.sum(d.multiply(S.num));
        if (n.isZERO()) {
            return ring.getZERO();
        }
        GenPolynomial<C> f;
        GenPolynomial<C> dd;
        dd = den;
        if (!g.isONE()) {
            f = ring.gcd(n, g);
            if (!f.isONE()) {
                n = ring.divide(n, f);
                dd = ring.divide(den, f);
            }
        }
        d = dd.multiply(sd);
        return new Quotient<C>(ring, n, d, true);
    }


    /**
     * Quotient negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Quotient<C> negate() {
        return new Quotient<C>(ring, num.negate(), den, true);
    }


    /**
     * Quotient signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /**
     * Quotient subtraction.
     * @param S Quotient.
     * @return this-S.
     */
    public Quotient<C> subtract(Quotient<C> S) {
        return sum(S.negate());
    }


    /**
     * Quotient division.
     * @param S Quotient.
     * @return this/S.
     */
    public Quotient<C> divide(Quotient<C> S) {
        return multiply(S.inverse());
    }


    /**
     * Quotient inverse.
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this.
     */
    public Quotient<C> inverse() {
        return new Quotient<C>(ring, den, num, true);
    }


    /**
     * Quotient remainder.
     * @param S Quotient.
     * @return this - (this/S)*S.
     */
    public Quotient<C> remainder(Quotient<C> S) {
        if (num.isZERO()) {
            throw new RuntimeException("element not invertible " + this);
        }
        return ring.getZERO();
    }


    /**
     * Quotient multiplication.
     * @param S Quotient.
     * @return this*S.
     */
    public Quotient<C> multiply(Quotient<C> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (num.isZERO()) {
            return this;
        }
        if (S.isONE()) {
            return this;
        }
        if (this.isONE()) {
            return S;
        }
        GenPolynomial<C> n;
        if (den.isONE() && S.den.isONE()) {
            n = num.multiply(S.num);
            return new Quotient<C>(ring, n);
        }
        GenPolynomial<C> g;
        GenPolynomial<C> d;
        if (den.isONE()) {
            g = ring.gcd(num, S.den);
            n = ring.divide(num, g);
            d = ring.divide(S.den, g);
            n = n.multiply(S.num);
            return new Quotient<C>(ring, n, d, true);
        }
        if (S.den.isONE()) {
            g = ring.gcd(S.num, den);
            n = ring.divide(S.num, g);
            d = ring.divide(den, g);
            n = n.multiply(num);
            return new Quotient<C>(ring, n, d, true);
        }
        GenPolynomial<C> f;
        GenPolynomial<C> sd;
        GenPolynomial<C> sn;
        g = ring.gcd(num, S.den);
        n = ring.divide(num, g);
        sd = ring.divide(S.den, g);
        f = ring.gcd(den, S.num);
        d = ring.divide(den, f);
        sn = ring.divide(S.num, f);
        n = n.multiply(sn);
        d = d.multiply(sd);
        return new Quotient<C>(ring, n, d, true);
    }


    /**
     * Quotient multiplication by GenPolynomial.
     * @param b GenPolynomial<C>.
     * @return this*b.
     */
    public Quotient<C> multiply(GenPolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        GenPolynomial<C> gcd = ring.gcd(b, den);
        GenPolynomial<C> d = den;
        if (!gcd.isONE()) {
            b = ring.divide(b, gcd);
            d = ring.divide(d, gcd);
        }
        if (this.isONE()) {
            return new Quotient<C>(ring, b, d, true);
        }
        GenPolynomial<C> n = num.multiply(b);
        return new Quotient<C>(ring, n, d, true);
    }


    /**
     * Quotient multiplication by coefficient.
     * @param b coefficient.
     * @return this*b.
     */
    public Quotient<C> multiply(C b) {
        if (b == null || b.isZERO()) {
            return ring.getZERO();
        }
        if (num.isZERO()) {
            return this;
        }
        if (b.isONE()) {
            return this;
        }
        GenPolynomial<C> n = num.multiply(b);
        return new Quotient<C>(ring, n, den, true);
    }


    /**
     * Quotient monic.
     * @return this with monic value part.
     */
    public Quotient<C> monic() {
        if (num.isZERO()) {
            return this;
        }
        C lbc = num.leadingBaseCoefficient();
        if (!lbc.isUnit()) {
            return this;
        }
        lbc = lbc.inverse();
        lbc = lbc.abs();
        GenPolynomial<C> n = num.multiply(lbc);
        GenPolynomial<C> d = den.multiply(lbc);
        return new Quotient<C>(ring, n, d, true);
    }


    /**
     * Greatest common divisor. 
     * @param b other element.
     * @return gcd(this,b).
     */
    public Quotient<C> gcd(Quotient<C> b) {
        if (b == null || b.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return b;
        }
        return ring.getONE();
    }


    /**
     * Extended greatest common divisor. 
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    @SuppressWarnings("unchecked")
    public Quotient<C>[] egcd(Quotient<C> b) {
        Quotient<C>[] ret = (Quotient<C>[]) new Quotient[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (b == null || b.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (this.isZERO()) {
            ret[0] = b;
            return ret;
        }
        GenPolynomial<C> two = ring.ring.fromInteger(2);
        ret[0] = ring.getONE();
        ret[1] = (this.multiply(two)).inverse();
        ret[2] = (b.multiply(two)).inverse();
        return ret;
    }
}
