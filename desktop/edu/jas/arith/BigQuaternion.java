/*
 * $Id: BigQuaternion.java 3211 2010-07-05 12:54:22Z kredel $
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
 * BigQuaternion class based on BigRational implementing the RingElem 
 * interface and with the familiar MAS static method names.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public final class BigQuaternion implements StarRingElem<BigQuaternion>, 
                                            GcdRingElem<BigQuaternion>,
                                            RingFactory<BigQuaternion> {

    /** Real part of the data structure. 
     */
    public final BigRational re;  // real part

    /** Imaginary part i of the data structure. 
     */
    public final BigRational im;  // i imaginary part

    /** Imaginary part j of the data structure. 
     */
    public final BigRational jm;  // j imaginary part

    /** Imaginary part k of the data structure. 
     */
    public final BigRational km;  // k imaginary part


    private final static Random random = new Random();


    private static final Logger logger = Logger.getLogger(BigQuaternion.class);


    private final boolean debug = logger.isDebugEnabled();



    /** Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     * @param k BigRational.
     */
    public BigQuaternion(BigRational r, BigRational i, BigRational j, BigRational k) {
        re = r;
        im = i;
        jm = j;
        km = k;
    }


    /** Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     */
    public BigQuaternion(BigRational r, BigRational i, BigRational j) {
        this(r,i,j,BigRational.ZERO);
    }


    /** Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     */
    public BigQuaternion(BigRational r, BigRational i) {
        this(r,i,BigRational.ZERO);
    }


    /** Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     */
    public BigQuaternion(BigRational r) {
        this(r,BigRational.ZERO);
    }


    /** Constructor for a BigQuaternion from BigComplex.
     * @param r BigComplex.
     */
    public BigQuaternion(BigComplex r) {
        this(r.re,r.im);
    }


    /** Constructor for a BigQuaternion from long.
     * @param r long.
     */
    public BigQuaternion(long r) {
        this(new BigRational(r),BigRational.ZERO);
    }


    /** Constructor for a BigQuaternion with no arguments.
     */
    public BigQuaternion() {
        this(BigRational.ZERO);
    }


    /** The BigQuaternion string constructor accepts the
     * following formats:
     * empty string, "rational", or "rat i rat j rat k rat"
     * with no blanks around i, j or k if used as polynoial coefficient.
     * @param s String.
     * @throws NumberFormatException
     */
    public BigQuaternion(String s) throws NumberFormatException {
        if ( s == null || s.length() == 0) {
            re = BigRational.ZERO;
            im = BigRational.ZERO;
            jm = BigRational.ZERO;
            km = BigRational.ZERO;
            return;
        } 
        s = s.trim();
        int r = s.indexOf("i") + s.indexOf("j") + s.indexOf("k");
        if ( r == -3 ) {
            re = new BigRational(s);
            im = BigRational.ZERO;
            jm = BigRational.ZERO;
            km = BigRational.ZERO;
            return;
        }

        int i = s.indexOf("i");
        String sr = "";
        if ( i > 0 ) {
            sr = s.substring(0,i);
        } else if ( i < 0 ) {
            throw new NumberFormatException("BigQuaternion missing i");
        }
        String si = "";
        if ( i < s.length() ) {
            s = s.substring(i+1,s.length());
        }
        int j = s.indexOf("j");
        if ( j > 0 ) {
            si = s.substring(0,j);
        } else if ( j < 0 ) {
            throw new NumberFormatException("BigQuaternion missing j");
        }
        String sj = "";
        if ( j < s.length() ) {
            s = s.substring(j+1,s.length());
        }
        int k = s.indexOf("k");
        if ( k > 0 ) {
            sj = s.substring(0,k);
        } else if ( k < 0 ) {
            throw new NumberFormatException("BigQuaternion missing k");
        }
        String sk = "";
        if ( k < s.length() ) {
            s = s.substring(k+1,s.length());
        }
        sk = s;

        re = new BigRational( sr.trim() );
        im = new BigRational( si.trim() );
        jm = new BigRational( sj.trim() );
        km = new BigRational( sk.trim() );
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigQuaternion factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigQuaternion> generators() {
        List<BigQuaternion> g = new ArrayList<BigQuaternion>(4);
        g.add( getONE() );
        g.add( I );
        g.add( J );
        g.add( K );
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
     public BigQuaternion clone() {
        return new BigQuaternion( re, im, jm, km );
    }


    /** Copy BigQuaternion element c.
     * @param c BigQuaternion.
     * @return a copy of c.
     */
    public BigQuaternion copy(BigQuaternion c) {
        return new BigQuaternion( c.re, c.im, c.jm, c.km );
    }


    /** Get the zero element.
     * @return 0 as BigQuaternion.
     */
    public BigQuaternion getZERO() {
        return ZERO;
    }


    /** Get the one element.
     * @return q as BigQuaternion.
     */
    public BigQuaternion getONE() {
        return ONE;
    }


    /**
     * Query if this ring is commutative.
     * @return false.
     */
    public boolean isCommutative() {
        return false;
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


    /** Get a BigQuaternion element from a BigInteger.
     * @param a BigInteger.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(BigInteger a) {
        return new BigQuaternion( new BigRational(a) );
    }


    /** Get a BigQuaternion element from a long.
     * @param a long.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(long a) {
        return new BigQuaternion( new BigRational( a ) );
    }


    /** The constant 0. 
     */
    public static final BigQuaternion ZERO = 
        new BigQuaternion();


    /** The constant 1.
     */
    public static final BigQuaternion ONE = 
        new BigQuaternion(BigRational.ONE);


    /** The constant i. 
     */
    public static final BigQuaternion I = 
        new BigQuaternion(BigRational.ZERO, BigRational.ONE);


    /** The constant j. 
     */
    public static final BigQuaternion J = 
        new BigQuaternion(BigRational.ZERO, 
                          BigRational.ZERO,
                          BigRational.ONE);


    /** The constant k. 
     */
    public static final BigQuaternion K = 
        new BigQuaternion(BigRational.ZERO,
                          BigRational.ZERO,
                          BigRational.ZERO,
                          BigRational.ONE);


    /** Get the real part. 
     * @return re.
     */
    public BigRational getRe() { return re; }


    /** Get the imaginary part im.
     * @return im.
     */
    public BigRational getIm() { return im; }


    /** Get the imaginary part jm.
     * @return jm.
     */
    public BigRational getJm() { return jm; }


    /** Get the imaginary part km.
     * @return km.
     */
    public BigRational getKm() { return km; }


    /** Get the string representation.
     * Is compatible with the string constructor.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = "" + re;
        int i = im.compareTo( BigRational.ZERO );
        int j = jm.compareTo( BigRational.ZERO );
        int k = km.compareTo( BigRational.ZERO );
        if ( debug ) {
            logger.debug("compareTo "+im+" ? 0 = "+i);
            logger.debug("compareTo "+jm+" ? 0 = "+j);
            logger.debug("compareTo "+km+" ? 0 = "+k);
        }
        if ( i == 0 && j == 0 && k == 0 ) return s;
        s += "i" + im;
        s += "j" + jm;
        s += "k" + km;
        return s;
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer();
        boolean i = im.isZERO();
        boolean j = jm.isZERO();
        boolean k = km.isZERO();
        if ( i && j && k ) {
            if ( re.isZERO() ) {
                return "0 ";
            }
            if ( !re.isONE() ) {
                s.append(re.toScript()+"*");
            }
            s.append("oneQ ");
            return s.toString();
        }
        if ( !re.isZERO() ) {
            if ( !re.isONE() ) {
                s.append(re.toScript()+"*");
            }
            s.append("oneQ ");
        } 
        if ( !i ) {
            if ( s.length() > 0 ) {
                s.append("+ ");
            }
            if ( !im.isONE() ) {
                s.append(im.toScript()+"*");
            }
            s.append("IQ ");
        } 
        if ( !j ) {
            if ( s.length() > 0 ) {
                s.append("+ ");
            }
            if ( !jm.isONE() ) {
               s.append(jm.toScript()+"*");
            }
            s.append("JQ ");
        } 
        if ( !k ) {
            if ( s.length() > 0 ) {
                s.append("+ ");
            }
            if ( !km.isONE() ) {
                s.append(km.toScript()+"*");
            }
            s.append("KQ ");
        } 
        return s.toString();
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return "Quat()";
    }


    /** Is Quaternion number zero.
     * @param A BigQuaternion.
     * @return true if A is 0, else false. 
     */
    public static boolean isQZERO(BigQuaternion A) {
        if ( A == null ) return false;
        return A.isZERO();
    }


    /** Is BigQuaternion number zero.
     * @return true if this is 0, else false. 
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return    re.equals( BigRational.ZERO )
            && im.equals( BigRational.ZERO )
            && jm.equals( BigRational.ZERO )
            && km.equals( BigRational.ZERO );
    }


    /** Is BigQuaternion number one.     
     * @param A is a quaternion number.
     * @return true if A is 1, else false.
     */
    public static boolean isQONE(BigQuaternion A) {
        if ( A == null ) return false;
        return A.isONE();
    }


    /** Is BigQuaternion number one.
     * @see edu.jas.structure.RingElem#isONE()
     * @return true if this is 1, else false.
     */
    public boolean isONE() {
        return    re.equals( BigRational.ONE )
               && im.equals( BigRational.ZERO )
               && jm.equals( BigRational.ZERO )
               && km.equals( BigRational.ZERO );
    }


    /** Is BigQuaternion imaginary one.
     * @return true if this is i, else false.
     */
    public boolean isIMAG() {
        return    re.equals( BigRational.ZERO )
               && im.equals( BigRational.ONE )
               && jm.equals( BigRational.ZERO )
               && km.equals( BigRational.ZERO );
    }


    /** Is BigQuaternion unit element.
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
        if ( ! ( b instanceof BigQuaternion ) ) return false;
        BigQuaternion B = (BigQuaternion) b;
        return    re.equals( B.re ) 
               && im.equals( B.im )
               && jm.equals( B.jm )
               && km.equals( B.km );
    }


    /** Hash code for this BigQuaternion.
     * @see java.lang.Object#hashCode()
     */
    @Override
     public int hashCode() {
        int h;
        h  = 37 * re.hashCode();
        h += 37 * im.hashCode();
        h += 37 * jm.hashCode();
        h += 37 * km.hashCode();
        return h;
    }


    /** Since quaternion numbers are unordered, 
     * we use lexicographical order of re, im, jm and km.
     * @param b BigQuaternion.
     * @return 0 if b is equal to this, 1 if this is greater b and -1 else.
     */
    //JAVA6only: @Override
    public int compareTo(BigQuaternion b) {
        int s = re.compareTo(b.re);
        if ( s != 0 ) {
            return s;
        }
        s = im.compareTo(b.im);
        if ( s != 0 ) {
            return s;
        }
        s = jm.compareTo(b.jm);
        if ( s != 0 ) {
            return s;
        }
        return km.compareTo(b.km);
    }


    /** Since quaternion numbers are unordered, 
     * we use lexicographical order of re, im, jm and km.
     * @return 0 if this is equal to 0;
     *         1 if re > 0, or re == 0 and im > 0, or ...;
     *        -1 if re < 0, or re == 0 and im < 0, or ...
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
        int s = re.signum();
        if ( s != 0 ) {
            return s;
        }
        s = im.signum();
        if ( s != 0 ) {
            return s;
        }
        s = jm.signum();
        if ( s != 0 ) {
            return s;
        }
        return km.signum();
    }


    /* arithmetic operations: +, -, -
     */

    /** BigQuaternion summation.
     * @param B BigQuaternion.
     * @return this+B.
     */
    public BigQuaternion sum(BigQuaternion B) {
        return new BigQuaternion( re.sum(B.re), 
                                  im.sum(B.im), 
                                  jm.sum(B.jm), 
                                  km.sum(B.km) );
    }


    /** Quaternion number sum. 
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return A+B.
     */
    public static BigQuaternion QSUM(BigQuaternion A, BigQuaternion B) {
        if ( A == null ) return null;
        return A.sum(B);
    }


    /**Quaternion number difference. 
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return A-B.
     */
    public static BigQuaternion QDIF(BigQuaternion A, BigQuaternion B) {
        if ( A == null ) return null;
        return A.subtract(B);
    }


    /** BigQuaternion subtraction.
     * @param B BigQuaternion. 
     * @return this-B.
     */
    public BigQuaternion subtract(BigQuaternion B) {
        return new BigQuaternion( re.subtract(B.re), 
                                  im.subtract(B.im),
                                  jm.subtract(B.jm),
                                  km.subtract(B.km) );
    }


    /** Quaternion number negative.  
     * @param A is a quaternion number
     * @return -A.
     */
    public static BigQuaternion QNEG(BigQuaternion A) {
        if ( A == null ) return null;
        return A.negate();
    }


    /** BigQuaternion number negative.  
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigQuaternion negate() {
        return new BigQuaternion( re.negate(), 
                                  im.negate(),
                                  jm.negate(),
                                  km.negate() );
    }


    /** Quaternion number conjugate. 
     * @param A is a quaternion number.
     * @return the quaternion conjugate of A.
     */
    public static BigQuaternion QCON(BigQuaternion A) {
        if ( A == null ) return null;
        return A.conjugate();
    }


    /* arithmetic operations: conjugate, absolute value 
     */

    /** BigQuaternion conjugate.
     * @return conjugate(this).
     */
    public BigQuaternion conjugate() {
        return new BigQuaternion( re, 
                                  im.negate(), 
                                  jm.negate(),
                                  km.negate() );
    }


    /** Quaternion number norm.  
     * @see edu.jas.structure.StarRingElem#norm()
     * @return ||this||.
     */
    public BigQuaternion norm() {
        // this.conjugate().multiply(this);
        BigRational v = re.multiply(re);
        v = v.sum( im.multiply(im) );
        v = v.sum( jm.multiply(jm) );
        v = v.sum( km.multiply(km) );
        return new BigQuaternion( v );
    }


    /** Quaternion number absolute value.  
     * @see edu.jas.structure.RingElem#abs()
     * @return |this|^2.
     * Note: The square root is not jet implemented.
     */
    public BigQuaternion abs() {
        BigQuaternion n = norm();
        logger.error("abs() square root missing");
        // n = n.sqrt();
        return n;
    }


    /** Quaternion number absolute value.    
     * @param A is a quaternion number.
     * @return the absolute value of A, a rational number.
     * Note: The square root is not jet implemented.
     */
    public static BigRational QABS(BigQuaternion A) {
        if ( A == null ) return null;
        return A.abs().re;
    }


    /** Quaternion number product.
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return A*B.
     */
    public static BigQuaternion QPROD(BigQuaternion A, BigQuaternion B) {
        if ( A == null ) return null;
        return A.multiply(B);
    }


    /* arithmetic operations: *, inverse, / 
     */

    /** BigQuaternion multiply.
     * @param B BigQuaternion.
     * @return this*B.
     */
    public BigQuaternion multiply(BigQuaternion B) {
        BigRational r = re.multiply(B.re);
        r = r.subtract(im.multiply(B.im));
        r = r.subtract(jm.multiply(B.jm));
        r = r.subtract(km.multiply(B.km));
        BigRational i = re.multiply(B.im);
        i = i.sum( im.multiply(B.re) );
        i = i.sum( jm.multiply(B.km) );
        i = i.subtract( km.multiply(B.jm) );

        BigRational j = re.multiply(B.jm);
        j = j.subtract( im.multiply(B.km) );
        j = j.sum( jm.multiply(B.re) );
        j = j.sum( km.multiply(B.im) );

        BigRational k = re.multiply(B.km);
        k = k.sum( im.multiply(B.jm) );
        k = k.subtract( jm.multiply(B.im) );
        k = k.sum( km.multiply(B.re) );

        return new BigQuaternion( r, i, j, k );
    }


    /** Quaternion number inverse.  
     * @param A is a non-zero quaternion number.
     * @return S with S * A = 1.
     */
    public static BigQuaternion QINV(BigQuaternion A) {
        if ( A == null ) return null;
        return A.inverse();
    }


    /** BigQuaternion inverse.
     * @return S with S * this = 1.
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigQuaternion inverse() {
        BigRational a = norm().re.inverse();
        return new BigQuaternion( re.multiply(a), 
                                  im.multiply(a.negate()), 
                                  jm.multiply(a.negate()), 
                                  km.multiply(a.negate()) ); 
    }


    /** BigQuaternion remainder.
     * @param S BigQuaternion.
     * @return 0.
     */
    public BigQuaternion remainder(BigQuaternion S) {
        if ( S.isZERO() ) {
            throw new RuntimeException("division by zero");
        }
        return ZERO;
    }


    /** Quaternion number quotient.
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return R/S.
     */
    public static BigQuaternion QQ(BigQuaternion A, BigQuaternion B) {
        if ( A == null ) return null;
        return A.divide(B);
    }


    /** BigQuaternion divide.
     * @param b BigQuaternion.
     * @return this/b.
     */
    public BigQuaternion divide(BigQuaternion b) {
        return this.multiply( b.inverse() );
    }


    /** BigQuaternion divide.
     * @param b BigRational.
     * @return this/b.
     */
    public BigQuaternion divide(BigRational b) {
        BigRational bi = b.inverse();
        return new BigQuaternion( re.multiply(bi),
                                  im.multiply(bi),
                                  jm.multiply(bi),
                                  km.multiply(bi) );
    }


    /** BigQuaternion random.
     * Random rational numbers A, B, C and D are generated using random(n). 
     * Then R is the quaternion number with real part A and 
     * imaginary parts B, C and D. 
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n) {
        return random( n, random );
    }


    /** BigQuaternion random.
     * Random rational numbers A, B, C and D are generated using RNRAND(n). 
     * Then R is the quaternion number with real part A and 
     * imaginary parts B, C and D. 
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n, Random rnd) {
        BigRational r = BigRational.ONE.random( n, rnd );
        BigRational i = BigRational.ONE.random( n, rnd );
        BigRational j = BigRational.ONE.random( n, rnd );
        BigRational k = BigRational.ONE.random( n, rnd );
        return new BigQuaternion( r, i, j, k );
    }


    /** Quaternion number, random.
     * Random rational numbers A, B, C and D are generated using RNRAND(n). 
     * Then R is the quaternion number with real part A and 
     * imaginary parts B, C and D. 
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
     */
    public static BigQuaternion QRAND(int n) {
        return ONE.random( n, random);
    }


    /** Parse quaternion number from String.
     * @param s String.
     * @return BigQuaternion from s.
     */
    public BigQuaternion parse(String s) {
        return new BigQuaternion(s);
    }


    /** Parse quaternion number from Reader.
     * @param r Reader.
     * @return next BigQuaternion from r.
     */ 
    public BigQuaternion parse(Reader r) {
        return parse( StringUtil.nextString(r) );
    }


    /** Quaternion number greatest common divisor.  
     * @param S BigQuaternion.
     * @return gcd(this,S).
     */
    public BigQuaternion gcd(BigQuaternion S) {
        if ( S == null || S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        return ONE;
    }


    /**
     * BigQuaternion extended greatest common divisor.
     * @param S BigQuaternion.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigQuaternion[] egcd(BigQuaternion S) {
        BigQuaternion[] ret = new BigQuaternion[3];
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
        BigQuaternion half = new BigQuaternion(new BigRational(1,2));
        ret[0] = ONE;
        ret[1] = this.inverse().multiply(half);
        ret[2] = S.inverse().multiply(half);
        return ret;
    }

}
