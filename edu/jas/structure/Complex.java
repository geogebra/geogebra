/*
 * $Id: Complex.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;


import org.apache.log4j.Logger;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;


/**
 * Generic Complex class implementing the RingElem interface. Objects of this
 * class are immutable.
 * @param <C> base type.
 * @author Heinz Kredel
 */
public class Complex<C extends RingElem<C>> implements StarRingElem<Complex<C>>, GcdRingElem<Complex<C>> {


    private static final Logger logger = Logger.getLogger(Complex.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Complex class factory data structure.
     */
    public final ComplexRing<C> ring;


    /**
     * Real part of the data structure.
     */
    protected final C re;


    /**
     * Imaginary part of the data structure.
     */
    protected final C im;


    /**
     * The constructor creates a Complex object from two C objects as real and
     * imaginary part.
     * @param ring factory for Complex objects.
     * @param r real part.
     * @param i imaginary part.
     */
    public Complex(ComplexRing<C> ring, C r, C i) {
        this.ring = ring;
        re = r;
        im = i;
    }


    /**
     * The constructor creates a Complex object from a C object as real part,
     * the imaginary part is set to 0.
     * @param r real part.
     */
    public Complex(ComplexRing<C> ring, C r) {
        this(ring, r, ring.ring.getZERO());
    }


    /**
     * The constructor creates a Complex object from a long element as real
     * part, the imaginary part is set to 0.
     * @param r real part.
     */
    public Complex(ComplexRing<C> ring, long r) {
        this(ring, ring.ring.fromInteger(r));
    }


    /**
     * The constructor creates a Complex object with real part 0 and imaginary
     * part 0.
     */
    public Complex(ComplexRing<C> ring) {
        this(ring, ring.ring.getZERO());
    }


    /**
     * The constructor creates a Complex object from a String representation.
     * @param s string of a Complex.
     * @throws NumberFormatException
     */
    public Complex(ComplexRing<C> ring, String s) throws NumberFormatException {
        this.ring = ring;
        if (s == null || s.length() == 0) {
            re = ring.ring.getZERO();
            im = ring.ring.getZERO();
            return;
        }
        s = s.trim();
        int i = s.indexOf("i");
        if (i < 0) {
            re = ring.ring.parse(s);
            im = ring.ring.getZERO();
            return;
        }
        //logger.warn("String constructor not done");
        String sr = "";
        if (i > 0) {
            sr = s.substring(0, i);
        }
        String si = "";
        if (i < s.length()) {
            si = s.substring(i + 1, s.length());
        }
        //int j = sr.indexOf("+");
        re = ring.ring.parse(sr.trim());
        im = ring.ring.parse(si.trim());
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ComplexRing<C> factory() {
        return ring;
    }


    /**
     * Get the real part.
     * @return re.
     */
    public C getRe() {
        return re;
    }


    /**
     * Get the imaginary part.
     * @return im.
     */
    public C getIm() {
        return im;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Complex<C> clone() {
        return new Complex<C>(ring, re, im);
    }


    /**
     * Get the String representation.
     */
    @Override
    public String toString() {
        String s = re.toString();
        //logger.info("compareTo "+im+" ? 0 = "+i);
        if (im.isZERO()) {
            return s;
        }
        s += "i" + im;
        return s;
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer();
        if (im.isZERO()) {
            s.append(re.toScript());
        } else {
            s.append("");
            if (!re.isZERO()) {
                s.append(re.toScript());
                s.append(" + ");
            }
            if (im.isONE()) {
                s.append("I");
            } else {
                s.append(im.toScript()).append(" * I");
            }
            s.append("");
        }
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return ring.toScript();
    }


    /**
     * Is Complex number zero.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return re.isZERO() && im.isZERO();
    }


    /**
     * Is Complex number one.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return re.isONE() && im.isZERO();
    }


    /**
     * Is Complex imaginary one.
     * @return If this is i then true is returned, else false.
     */
    public boolean isIMAG() {
        return re.isZERO() && im.isONE();
    }


    /**
     * Is Complex unit element.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if (isZERO()) {
            return false;
        }
        if (ring.isField()) {
            return true;
        }
        return norm().re.isUnit();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof Complex)) {
            return false;
        }
        Complex<C> bc = null;
        try {
            bc = (Complex<C>) b;
        } catch (ClassCastException e) {
        }
        if (bc == null) {
            return false;
        }
        if (!ring.equals(bc.ring)) {
            return false;
        }
        return re.equals(bc.re) && im.equals(bc.im);
    }


    /**
     * Hash code for this Complex.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * re.hashCode() + im.hashCode();
    }


    /**
     * Since complex numbers are unordered, we use lexicographical order of re
     * and im.
     * @return 0 if this is equal to b; 1 if re > b.re, or re == b.re and im >
     *         b.im; -1 if re < b.re, or re == b.re and im < b.im
     */
    //JAVA6only: @Override
    public int compareTo(Complex<C> b) {
        int s = re.compareTo(b.re);
        if (s != 0) {
            return s;
        }
        return im.compareTo(b.im);
    }


    /**
     * Since complex numbers are unordered, we use lexicographical order of re
     * and im.
     * @return 0 if this is equal to 0; 1 if re > 0, or re == 0 and im > 0; -1
     *         if re < 0, or re == 0 and im < 0
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
        int s = re.signum();
        if (s != 0) {
            return s;
        }
        return im.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /**
     * Complex number summation.
     * @param B a Complex<C> number.
     * @return this+B.
     */
    public Complex<C> sum(Complex<C> B) {
        return new Complex<C>(ring, re.sum(B.re), im.sum(B.im));
    }


    /**
     * Complex number subtract.
     * @param B a Complex<C> number.
     * @return this-B.
     */
    public Complex<C> subtract(Complex<C> B) {
        return new Complex<C>(ring, re.subtract(B.re), im.subtract(B.im));
    }


    /**
     * Complex number negative.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Complex<C> negate() {
        return new Complex<C>(ring, re.negate(), im.negate());
    }


    /* arithmetic operations: conjugate, absolut value 
     */

    /**
     * Complex number conjugate.
     * @return the complex conjugate of this.
     */
    public Complex<C> conjugate() {
        return new Complex<C>(ring, re, im.negate());
    }


    /**
     * Complex number norm.
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public Complex<C> norm() {
        // this.conjugate().multiply(this);
        C v = re.multiply(re);
        v = v.sum(im.multiply(im));
        return new Complex<C>(ring, v);
    }


    /**
     * Complex number absolute value.
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|^2. <b>Note:</b> The square root is not jet implemented.
     */
    public Complex<C> abs() {
        Complex<C> n = norm();
        logger.error("abs() square root missing");
        // n = n.sqrt();
        return n;
    }


    /* arithmetic operations: *, inverse, / 
     */


    /**
     * Complex number product.
     * @param B is a complex number.
     * @return this*B.
     */
    public Complex<C> multiply(Complex<C> B) {
        return new Complex<C>(ring, re.multiply(B.re).subtract(im.multiply(B.im)), re.multiply(B.im).sum(
                im.multiply(B.re)));
    }


    /**
     * Complex number inverse.
     * @return S with S*this = 1, if it is defined.
     * @see edu.jas.structure.RingElem#inverse()
     */
    public Complex<C> inverse() {
        C a = norm().re.inverse();
        return new Complex<C>(ring, re.multiply(a), im.multiply(a.negate()));
    }


    /**
     * Complex number remainder.
     * @param S is a complex number.
     * @return 0.
     */
    public Complex<C> remainder(Complex<C> S) {
        if (ring.isField()) {
            return ring.getZERO();
        } else {
            return divideAndRemainder(S)[1];
        }
    }


    /**
     * Complex number divide.
     * @param B is a complex number, non-zero.
     * @return this/B.
     */
    public Complex<C> divide(Complex<C> B) {
        if (ring.isField()) {
            return this.multiply(B.inverse());
        } else {
            return divideAndRemainder(B)[0];
        }
    }


    /**
     * Complex number quotient and remainder.
     * @param S Complex.
     * @return Complex[] { q, r } with q = this/S and r = rem(this,S).
     */
    @SuppressWarnings("unchecked")
    public Complex<C>[] divideAndRemainder(Complex<C> S) {
        Complex<C>[] ret = (Complex<C>[]) new Complex[2];
        C n = S.norm().re;
        Complex<C> Sp = this.multiply(S.conjugate()); // == this*inv(S)*n
        C qr = Sp.re.divide(n);
        C rr = Sp.re.remainder(n);
        C qi = Sp.im.divide(n);
        C ri = Sp.im.remainder(n);
        C rr1 = rr;
        C ri1 = ri;
        if (rr.signum() < 0) {
            rr = rr.negate();
        }
        if (ri.signum() < 0) {
            ri = ri.negate();
        }
        C one = n.factory().fromInteger(1);
        if (rr.sum(rr).compareTo(n) > 0) { // rr > n/2
            if (rr1.signum() < 0) {
                qr = qr.subtract(one);
            } else {
                qr = qr.sum(one);
            }
        }
        if (ri.sum(ri).compareTo(n) > 0) { // ri > n/2
            if (ri1.signum() < 0) {
                qi = qi.subtract(one);
            } else {
                qi = qi.sum(one);
            }
        }
        Sp = new Complex<C>(ring, qr, qi);
        Complex<C> Rp = this.subtract(Sp.multiply(S));
        if (debug && n.compareTo(Rp.norm().re) < 0) {
            System.out.println("n = " + n);
            System.out.println("qr   = " + qr);
            System.out.println("qi   = " + qi);
            System.out.println("rr   = " + rr);
            System.out.println("ri   = " + ri);
            System.out.println("rr1  = " + rr1);
            System.out.println("ri1  = " + ri1);
            System.out.println("this = " + this);
            System.out.println("S    = " + S);
            System.out.println("Sp   = " + Sp);
            BigInteger tr = (BigInteger) (Object) this.re;
            BigInteger ti = (BigInteger) (Object) this.im;
            BigInteger sr = (BigInteger) (Object) S.re;
            BigInteger si = (BigInteger) (Object) S.im;
            BigComplex tc = new BigComplex(new BigRational(tr), new BigRational(ti));
            BigComplex sc = new BigComplex(new BigRational(sr), new BigRational(si));
            BigComplex qc = tc.divide(sc);
            System.out.println("qc   = " + qc);
            BigDecimal qrd = new BigDecimal(qc.getRe());
            BigDecimal qid = new BigDecimal(qc.getIm());
            System.out.println("qrd  = " + qrd);
            System.out.println("qid  = " + qid);
            throw new RuntimeException("QR norm not decreasing " + Rp + ", " + Rp.norm());
        }
        ret[0] = Sp;
        ret[1] = Rp;
        return ret;
    }


    /**
     * Complex number greatest common divisor.
     * @param S Complex<C>.
     * @return gcd(this,S).
     */
    public Complex<C> gcd(Complex<C> S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        if (ring.isField()) {
            return ring.getONE();
        }
        Complex<C> a = this;
        Complex<C> b = S;
        if (a.re.signum() < 0) {
            a = a.negate();
        }
        if (b.re.signum() < 0) {
            b = b.negate();
        }
        while (!b.isZERO()) {
            if (debug) {
                logger.info("norm(b), a, b = " + b.norm() + ", " + a + ", " + b);
            }
            Complex<C>[] qr = a.divideAndRemainder(b);
            if (qr[0].isZERO()) {
                System.out.println("a = " + a);
            }
            a = b;
            b = qr[1];
        }
        if (a.re.signum() < 0) {
            a = a.negate();
        }
        return a;
    }


    /**
     * Complex extended greatest common divisor.
     * @param S Complex<C>.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @SuppressWarnings("unchecked")
    public Complex<C>[] egcd(Complex<C> S) {
        Complex<C>[] ret = (Complex<C>[]) new Complex[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (this.isZERO()) {
            ret[0] = S;
            return ret;
        }
        if (ring.isField()) {
            Complex<C> half = new Complex<C>(ring, ring.ring.fromInteger(1).divide(ring.ring.fromInteger(2)));
            ret[0] = ring.getONE();
            ret[1] = this.inverse().multiply(half);
            ret[2] = S.inverse().multiply(half);
            return ret;
        }
        Complex<C>[] qr;
        Complex<C> q = this;
        Complex<C> r = S;
        Complex<C> c1 = ring.getONE();
        Complex<C> d1 = ring.getZERO();
        Complex<C> c2 = ring.getZERO();
        Complex<C> d2 = ring.getONE();
        Complex<C> x1;
        Complex<C> x2;
        while (!r.isZERO()) {
            if (debug) {
                logger.info("norm(r), q, r = " + r.norm() + ", " + q + ", " + r);
            }
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
        if (q.re.signum() < 0) {
            q = q.negate();
            c1 = c1.negate();
            c2 = c2.negate();
        }
        ret[0] = q;
        ret[1] = c1;
        ret[2] = c2;
        return ret;
    }

}
