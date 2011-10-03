/*
 * $Id: BigComplex.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.arith;

import java.math.BigInteger;
import java.util.Random;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

//import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.StarRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.util.StringUtil;


/**
 * BigComplex class based on BigRational implementing the RingElem
 * interface and with the familiar SAC static method names.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public final class BigComplex implements StarRingElem<BigComplex>,
                                         GcdRingElem<BigComplex>, 
                                         RingFactory<BigComplex> {

    /** Real part of the data structure. 
      */
    protected final BigRational re;

    /** Imaginary part of the data structure. 
      */
    protected final BigRational im;

    private final static Random random = new Random();

    private static final Logger logger = Logger.getLogger(BigComplex.class);


    /** The constructor creates a BigComplex object 
     * from two BigRational objects real and imaginary part. 
     * @param r real part.
     * @param i imaginary part.
     */
    public BigComplex(BigRational r, BigRational i) {
        re = r;
        im = i;
    }


    /** The constructor creates a BigComplex object 
     * from a BigRational object as real part, 
     * the imaginary part is set to 0. 
     * @param r real part.
     */
    public BigComplex(BigRational r) {
        this(r,BigRational.ZERO);
    }


    /** The constructor creates a BigComplex object 
     * from a long element as real part, 
     * the imaginary part is set to 0. 
     * @param r real part.
     */
    public BigComplex(long r) {
        this(new BigRational(r),BigRational.ZERO);
    }


    /** The constructor creates a BigComplex object 
     * with real part 0 and imaginary part 0. 
     */
    public BigComplex() {
        this(BigRational.ZERO);
    }


    /** The constructor creates a BigComplex object 
     * from a String representation.
     * @param s string of a BigComplex.
     * @throws NumberFormatException
     */
    public BigComplex(String s) throws NumberFormatException {
        if ( s == null || s.length() == 0) {
            re = BigRational.ZERO;
            im = BigRational.ZERO;
            return;
        } 
        s = s.trim();
        int i = s.indexOf("i");
        if ( i < 0 ) {
           re = new BigRational( s );
           im = BigRational.ZERO;
           return;
        }
        //logger.warn("String constructor not done");
        String sr = "";
        if ( i > 0 ) {
            sr = s.substring(0,i);
        }
        String si = "";
        if ( i < s.length() ) {
            si = s.substring(i+1,s.length());
        }
        //int j = sr.indexOf("+");
        re = new BigRational( sr.trim() );
        im = new BigRational( si.trim() );
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigComplex factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigComplex> generators() {
        List<BigComplex> g = new ArrayList<BigComplex>(2);
        g.add( getONE() );
        g.add( getIMAG() );
        return g;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
    }


    /** Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public BigComplex clone() {
        return new BigComplex( re, im );
    }


    /** Copy BigComplex element c.
     * @param c BigComplex.
     * @return a copy of c.
     */
    public BigComplex copy(BigComplex c) {
        return new BigComplex( c.re, c.im );
    }


    /** Get the zero element.
     * @return 0 as BigComplex.
     */
    public BigComplex getZERO() {
        return ZERO;
    }


    /** Get the one element.
     * @return 1 as BigComplex.
     */
    public BigComplex getONE() {
        return ONE;
    }


    /** Get the i element.
     * @return i as BigComplex.
     */
    public BigComplex getIMAG() {
        return I;
    }


    /**
     * Query if this ring is commutative.
     * @return true.
     */
    public boolean isCommutative() {
        return true;
    }


    /**
     * Query if this ring is associative.
     * @return true.
     */
    public boolean isAssociative() {
        return true;
    }


    /**
     * Query if this ring is a field.
     * @return true.
     */
    public boolean isField() {
        return true;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return java.math.BigInteger.ZERO;
    }


    /** Get a BigComplex element from a BigInteger.
     * @param a BigInteger.
     * @return a BigComplex.
     */
    public BigComplex fromInteger(BigInteger a) {
        return new BigComplex( new BigRational(a) );
    }


    /** Get a BigComplex element from a long.
     * @param a long.
     * @return a BigComplex.
     */
    public BigComplex fromInteger(long a) {
        return new BigComplex( new BigRational( a ) );
    }


    /** The constant 0.
     */
    public static final BigComplex ZERO = 
           new BigComplex();


    /** The constant 1.
     */
    public static final BigComplex ONE = 
           new BigComplex(BigRational.ONE);


    /** The constant i. 
     */
    public static final BigComplex I = 
           new BigComplex(BigRational.ZERO,BigRational.ONE);


    /** Get the real part. 
     * @return re.
     */
    public BigRational getRe() { return re; }


    /** Get the imaginary part. 
     * @return im.
     */
    public BigRational getIm() { return im; }


    /** Get the String representation.
     */
    @Override
    public String toString() {
        String s = "" + re;
        int i = im.compareTo( BigRational.ZERO );
        //logger.info("compareTo "+im+" ? 0 = "+i);
        if ( i == 0 ) return s;
        s += "i" + im;
        return s;
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case: re or re+im*i 
        // was (re,im) or (re,) 
        StringBuffer s = new StringBuffer();
        boolean iz = im.isZERO();
        if ( iz ) {
            s.append(re.toScript());
            return s.toString();
        }
        boolean rz = re.isZERO();
        if ( rz ) {
            if ( !im.isONE() ) {
                if ( im.signum() > 0 ) { 
                    s.append(im.toScript()+"*");
                } else {
                    s.append("-");
                    BigRational ii = im.negate();
                    if ( !ii.isONE() ) {
                        s.append(ii.toScript()+"*");
                    }
                }
            }
        } else {
            s.append(re.toScript());
            if ( im.signum() > 0 ) {
                s.append("+");
                if ( !im.isONE() ) {
                   s.append(im.toScript()+"*");
                }
            } else {
                s.append("-");
                BigRational ii = im.negate();
                if ( !ii.isONE() ) {
                    s.append(ii.toScript()+"*");
                }
            }
        }
        s.append("I");
        return s.toString();
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return "CC()";
    }


   /** Complex number zero. 
     * @param A is a complex number. 
     * @return If A is 0 then true is returned, else false.
     */
    public static boolean isCZERO(BigComplex A) {
      if ( A == null ) return false;
      return A.isZERO();
    }


   /** Is Complex number zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ZERO );
    }


    /** Complex number one.  
     * @param A is a complex number.  
     * @return If A is 1 then true is returned, else false. 
     */
    public static boolean isCONE(BigComplex A) {
      if ( A == null ) return false;
      return A.isONE();
    }


    /** Is Complex number one.  
     * @return If this is 1 then true is returned, else false. 
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return    re.equals( BigRational.ONE )
               && im.equals( BigRational.ZERO );
    }


    /** Is Complex imaginary one.  
     * @return If this is i then true is returned, else false. 
     */
    public boolean isIMAG() {
        return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ONE );
    }


    /** Is Complex unit element.
     * @return If this is a unit then true is returned, else false. 
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return ( ! isZERO() );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof BigComplex ) ) {
           return false;
        }
        BigComplex bc = (BigComplex) b;
        return    re.equals( bc.re ) 
               && im.equals( bc.im );
    }


    /** Hash code for this BigComplex.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * re.hashCode() + im.hashCode();
    }


    /** Since complex numbers are unordered, 
     * we use lexicographical order of re and im.
     * @return 0 if this is equal to b;
     *         1 if re > b.re, or re == b.re and im > b.im;
     *        -1 if re < b.re, or re == b.re and im < b.im
     */
    //JAVA6only: @Override
    public int compareTo(BigComplex b) {
        int s = re.compareTo( b.re );
        if ( s != 0 ) { 
            return s;
        }
        return im.compareTo( b.im );
    }


    /** Since complex numbers are unordered, 
     * we use lexicographical order of re and im.
     * @return 0 if this is equal to 0;
     *         1 if re > 0, or re == 0 and im > 0;
     *        -1 if re < 0, or re == 0 and im < 0
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
      int s = re.signum();
      if ( s != 0 ) {
          return s;
      }
      return im.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /** Complex number summation.  
     * @param B a BigComplex number.
     * @return this+B.
     */
    public BigComplex sum(BigComplex B) {
        return new BigComplex( re.sum( B.re ), 
                               im.sum( B.im ) );
    }

    /** Complex number sum.  
     * @param A and B are complex numbers.  
     * @return A+B. 
     */
    public static BigComplex CSUM(BigComplex A, BigComplex B) {
        if ( A == null ) return null;
        return A.sum(B);
    }


    /** Complex number difference.  
     * @param A and B are complex numbers.  
     * @return A-B.
     */
    public static BigComplex CDIF(BigComplex A, BigComplex B) {
        if ( A == null ) return null;
        return A.subtract(B);
    }


    /** Complex number subtract.  
     * @param B a BigComplex number.
     * @return this-B.
     */
    public BigComplex subtract(BigComplex B) {
        return new BigComplex( re.subtract( B.re ), 
                               im.subtract( B.im ) );
    }


    /** Complex number negative.  
     * @param A is a complex number.  
     * @return -A
     */
    public static BigComplex CNEG(BigComplex A) {
        if ( A == null ) return null;
        return A.negate();
    }


    /** Complex number negative.  
     * @return -this. 
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigComplex negate() {
        return new BigComplex( re.negate(), 
                               im.negate());
    }


    /** Complex number conjugate.  
     * @param A is a complex number. 
     * @return the complex conjugate of A. 
     */
    public static BigComplex CCON(BigComplex A) {
        if ( A == null ) return null;
        return A.conjugate();
    }


    /* arithmetic operations: conjugate, absolut value 
     */

    /** Complex number conjugate.  
     * @return the complex conjugate of this. 
     */
    public BigComplex conjugate() {
        return new BigComplex(re, im.negate());
    }


    /** Complex number norm.  
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public BigComplex norm() {
        // this.conjugate().multiply(this);
        BigRational v = re.multiply(re);
        v = v.sum( im.multiply(im) );
        return new BigComplex( v );
    }


    /** Complex number absolute value.  
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|^2.
     * Note: The square root is not jet implemented.
     */
    public BigComplex abs() {
        BigComplex n = norm();
        logger.error("abs() square root missing");
        // n = n.sqrt();
        return n;
    }


    /** Complex number absolute value.  
     * @param A is a complex number.  
     * @return the absolute value of A, a rational number. 
     * Note: The square root is not jet implemented.
     */
    public static BigRational CABS(BigComplex A) {
      if ( A == null ) return null;
      return A.abs().re;
    }


    /** Complex number product.  
     * @param A and B are complex numbers.  
     * @return A*B.
     */
    public static BigComplex CPROD(BigComplex A, BigComplex B) {
      if ( A == null ) return null;
      return A.multiply(B);
    }


    /* arithmetic operations: *, inverse, / 
     */


    /** Complex number product.  
     * @param B is a complex number.  
     * @return this*B.
     */
    public BigComplex multiply(BigComplex B) {
        return new BigComplex(
               re.multiply(B.re).subtract(im.multiply(B.im)),
               re.multiply(B.im).sum(im.multiply(B.re)) );
    }


    /** Complex number inverse.  
     * @param A is a non-zero complex number.  
     * @return S with S*A = 1. 
     */
    public static BigComplex CINV(BigComplex A) {
        if ( A == null ) return null;
        return A.inverse();
    }


    /** Complex number inverse.  
     * @return S with S*this = 1. 
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigComplex inverse() {
        BigRational a = norm().re.inverse();
        return new BigComplex( re.multiply(a), 
                               im.multiply(a.negate()) ); 
    }


    /** Complex number inverse.  
     * @param S is a complex number.  
     * @return 0. 
     */
    public BigComplex remainder(BigComplex S) {
        if ( S.isZERO() ) {
           throw new RuntimeException("division by zero");
        }
        return ZERO;
    }


    /** Complex number quotient.  
     * @param A and B are complex numbers, B non-zero.
     * @return A/B. 
     */
    public static BigComplex CQ(BigComplex A, BigComplex B) {
        if ( A == null ) return null;
        return A.divide(B);
    }


    /** Complex number divide.
     * @param B is a complex number, non-zero.
     * @return this/B. 
     */
    public BigComplex divide (BigComplex B) {
        return this.multiply( B.inverse() );
    }


    /** Complex number, random.  
     * Random rational numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return R.
     */
    public BigComplex random(int n) {
        return random(n,random);
    }


    /** Complex number, random.  
     * Random rational numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R.
     */
    public BigComplex random(int n, Random rnd) {
        BigRational r = BigRational.ONE.random( n, rnd );
        BigRational i = BigRational.ONE.random( n, rnd );
        return new BigComplex( r, i ); 
    }


    /** Complex number, random.  
     * Random rational numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return R.
     */
    public static BigComplex CRAND(int n) {
        return ONE.random(n,random);
    }


    /** Parse complex number from string.
     * @param s String.
     * @return BigComplex from s.
     */
    public BigComplex parse(String s) {
        return new BigComplex(s);
    }


    /** Parse complex number from Reader.
     * @param r Reader.
     * @return next BigComplex from r.
     */
    public BigComplex parse(Reader r) {
        return parse( StringUtil.nextString(r) );
    }


    /** Complex number greatest common divisor.  
     * @param S BigComplex.
     * @return gcd(this,S).
     */
    public BigComplex gcd(BigComplex S) {
        if ( S == null || S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        return ONE;
    }


    /**
     * BigComplex extended greatest common divisor.
     * @param S BigComplex.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigComplex[] egcd(BigComplex S) {
        BigComplex[] ret = new BigComplex[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if ( S == null || S.isZERO() ) {
            ret[0] = this;
            return ret;
        }
        if ( this.isZERO() ) {
            ret[0] = S;
            return ret;
        }
        BigComplex half = new BigComplex(new BigRational(1,2));
        ret[0] = ONE;
        ret[1] = this.inverse().multiply(half);
        ret[2] = S.inverse().multiply(half);
        return ret;
    }

}
