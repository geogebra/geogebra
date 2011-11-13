/*
 * $Id: BigDecimal.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.arith;

import java.util.Random;
import java.math.MathContext;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

//import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.util.StringUtil;


/**
 * BigDecimal class to make java.math.BigDecimal available with RingElem 
 * interface.
 * Objects of this class are immutable.
 * Experimental, use with care, compareTo is hacked.
 * @author Heinz Kredel
 * @see java.math.BigDecimal
 */

public final class BigDecimal implements GcdRingElem<BigDecimal>, 
                                         RingFactory<BigDecimal> {

    /** The data structure. 
      */
    public final java.math.BigDecimal val;


    private final static Random random = new Random();


    // should go to factory:
    public static final int DEFAULT_PRECISION = 50;
    public static final MathContext DEFAULT_CONTEXT = new MathContext(DEFAULT_PRECISION);
    public final MathContext context; 


    /** The constant 0.
     */
    public final static BigDecimal ZERO 
                 = new BigDecimal( java.math.BigDecimal.ZERO );


    /** The constant 1.
     */
    public final static BigDecimal ONE 
                 = new BigDecimal( java.math.BigDecimal.ONE );


    /**
     * Constructor for BigDecimal from math.BigDecimal.
     * @param a java.math.BigDecimal.
     */
    public BigDecimal(java.math.BigDecimal a) {
        this(a,DEFAULT_CONTEXT);
    }


    /**
     * Constructor for BigDecimal from math.BigDecimal.
     * @param a java.math.BigDecimal.
     * @param mc MathContext.
     */
    public BigDecimal(java.math.BigDecimal a, MathContext mc) {
        val = a;
        context = mc;
    }


    /**
     * Constructor for BigDecimal from long.
     * @param a long.
     */
    public BigDecimal(long a) {
        this(a,DEFAULT_CONTEXT);
    }


    /**
     * Constructor for BigDecimal from long and a context.
     * @param a long.
     * @param mc MathContext.
     */
    public BigDecimal(long a, MathContext mc) {
        this( new java.math.BigDecimal( String.valueOf(a) ), mc );
    }


    /**
     * Constructor for BigDecimal from double.
     * @param a double.
     */
    public BigDecimal(double a) {
        this(a,DEFAULT_CONTEXT);
    }


    /**
     * Constructor for BigDecimal from double and a context.
     * @param a double.
     * @param mc MathContext.
     */
    public BigDecimal(double a, MathContext mc) {
        this( new java.math.BigDecimal(a), mc );
    }


    /**
     * Constructor for BigDecimal from java.math.BigInteger.
     * @param a java.math.BigInteger.
     */
    public BigDecimal(java.math.BigInteger a) {
        this(a,DEFAULT_CONTEXT);
    }


    /**
     * Constructor for BigDecimal from java.math.BigInteger.
     * @param a java.math.BigInteger.
     * @param mc MathContext.
     */
    public BigDecimal(java.math.BigInteger a, MathContext mc) {
        this(new java.math.BigDecimal(a),mc);
    }


    /**
     * Constructor for BigDecimal from BigRational.
     * @param a edu.jas.arith.BigRational.
     */
    public BigDecimal(BigRational a) {
        this(a,DEFAULT_CONTEXT);
    }


    /**
     * Constructor for BigDecimal from BigRational.
     * @param a edu.jas.arith.BigRational.
     * @param mc MathContext.
     */
    public BigDecimal(BigRational a, MathContext mc) {
        this( (new java.math.BigDecimal(a.num)).divide(new java.math.BigDecimal(a.den),mc),mc);
    }


    /**
     * Constructor for BigDecimal from String.
     * @param s String.
     */
    public BigDecimal(String s) {
        this(s,DEFAULT_CONTEXT);
    }


    /**
     * Constructor for BigDecimal from String.
     * @param s String.
     * @param mc MathContext.
     */
    public BigDecimal(String s, MathContext mc) {
        this( new java.math.BigDecimal( s.trim() ), mc );
    }


    /**
     * Constructor for BigDecimal without parameters.
     */
    public BigDecimal() {
        this( java.math.BigDecimal.ZERO, DEFAULT_CONTEXT);
    }


    /** Get the value.
     * @return val java.math.BigDecimal.
    public java.math.BigDecimal getVal() {
        return val;
    }
     */


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigDecimal factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigDecimal> generators() {
        List<BigDecimal> g = new ArrayList<BigDecimal>(1);
        g.add( getONE() );
        return g;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     * <b>Note: </b> is actually finite but returns false.
     */
    public boolean isFinite() {
        return false;
    }


    /** Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
     public BigDecimal clone() {
        return new BigDecimal( val, context );
    }


    /** Copy BigDecimal element c.
     * @param c BigDecimal.
     * @return a copy of c.
     */
    public BigDecimal copy(BigDecimal c) {
        return new BigDecimal( c.val, c.context );
    }


    /** Get the zero element.
     * @return 0.
     */
    public BigDecimal getZERO() {
        return ZERO;
    }


    /** Get the one element.
     * @return 1.
     */
    public BigDecimal getONE() {
        return ONE;
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
     * Floating point number addition is not associative,
     * but multiplication is.
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


    /** Get a BigDecimal element from a math.BigDecimal.
     * @param a math.BigDecimal.
     * @return a as BigDecimal.
     */
    public BigDecimal fromInteger(java.math.BigInteger a) {
        return new BigDecimal( new java.math.BigDecimal(a), context );
    }


    /** Get a BigDecimal element from a math.BigDecimal.
     * @param a math.BigDecimal.
     * @return a as BigDecimal.
     */
    public static BigDecimal valueOf(java.math.BigDecimal a) {
        return new BigDecimal(a, DEFAULT_CONTEXT);
    }


    /** Get a BigDecimal element from long.
     * @param a long.
     * @return a as BigDecimal.
     */
    public BigDecimal fromInteger(long a) {
        return new BigDecimal(a, context);
    }


    /** Get a BigDecimal element from long.
     * @param a long.
     * @return a as BigDecimal.
     */
    public static BigDecimal valueOf(long a) {
        return new BigDecimal(a, DEFAULT_CONTEXT);
    }


    /** Is BigDecimal number zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.compareTo( java.math.BigDecimal.ZERO ) == 0;
    }


    /** Is BigDecimal number one.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.compareTo( java.math.BigDecimal.ONE ) == 0;
    }


    /** Is BigDecimal number unit.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return ( ! isZERO() );
    }

    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        //return val.toString() + "(ulp=" + val.ulp() + ")";
        return val.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return toString();
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return "DD()";
    }


    /** Compare to BigDecimal b.
     * Experimental, is hacked.
     * @param b BigDecimal.
     * @return 0 if abs(this-b) < epsilon, 
               1 if this > b,
              -1 if this < b.
    */
    //JAVA6only: @Override
    public int compareTo(BigDecimal b) {
        //return val.compareTo( b.val );
        java.math.BigDecimal s = val.subtract( b.val, context );
        java.math.BigDecimal u1 = val.ulp();
        java.math.BigDecimal u2 = b.val.ulp();
        int u = Math.min( u1.scale(), u2.scale() );
        //System.out.println("u = " + u + ", s = " + s);
        java.math.BigDecimal eps;
        if ( u <= 0 ) { 
           eps = u1.max( u2 ); 
        } else {
           eps = u1.min( u2 ); 
        }
        //eps = eps.movePointRight(1);
        //System.out.println("ctx = " + context);
        //System.out.println("eps = " + eps);
        int t = s.abs().compareTo( eps );
        if ( t < 1 ) {
           return 0;
        }
        return s.signum();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
     public boolean equals(Object b) {
        if ( ! ( b instanceof BigDecimal ) ) {
            return false;
        }
        BigDecimal bi = (BigDecimal)b;
        return val.equals( bi.val );
    }


    /** Hash code for this BigDecimal.
     * @see java.lang.Object#hashCode()
     */
    @Override
     public int hashCode() {
        return val.hashCode();
    }


    /** Absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public BigDecimal abs() {
        return new BigDecimal( val.abs(), context );
    }


    /* Negative value of this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public BigDecimal negate() {
        return new BigDecimal( val.negate(), context );
    }


    /** signum.
     * @see edu.jas.structure.RingElem#signum()
     */
    public int signum() {
        return val.signum();
    }


    /** BigDecimal subtract.
     * @param S BigDecimal.
     * @return this-S.
     */
    public BigDecimal subtract(BigDecimal S) {
        return new BigDecimal( val.subtract( S.val, context  ) );
    }


    /** BigDecimal divide.
     * @param S BigDecimal.
     * @return this/S.
     */
    public BigDecimal divide(BigDecimal S) {
        return new BigDecimal( val.divide( S.val, context ) );
    }


    /** Integer inverse.  R is a non-zero integer.  
        S=1/R if defined else 0. 
        * @see edu.jas.structure.RingElem#inverse()
        */
    public BigDecimal inverse() {
        return ONE.divide( this );
    }


    /** BigDecimal remainder.
     * @param S BigDecimal.
     * @return this - (this/S)*S.
     */
    public BigDecimal remainder(BigDecimal S) {
        return new BigDecimal( val.remainder( S.val, context ) );
    }


    /** BigDecimal compute quotient and remainder.
     * @param S BigDecimal.
     * @return BigDecimal[] { q, r } with q = this/S and r = rem(this,S).
     */
    public BigDecimal[] divideAndRemainder(BigDecimal S) {
        BigDecimal[] qr = new BigDecimal[2];
        java.math.BigDecimal[] C = val.divideAndRemainder( S.val, context );
        qr[0] = new BigDecimal( C[0] );
        qr[1] = new BigDecimal( C[1] );
        return qr;
    }


    /** BigDecimal greatest common divisor.
     * @param S BigDecimal.
     * @return gcd(this,S).
     */
    public BigDecimal gcd(BigDecimal S) {
     throw new RuntimeException("BigDecimal.gcd() not implemented");
        //return new BigDecimal( val.gcd( S.val ) );
    }


    /**
     * BigDecimal extended greatest common divisor.
     * @param S BigDecimal.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigDecimal[] egcd(BigDecimal S) {
     throw new RuntimeException("BigDecimal.egcd() not implemented");
    }


    /** BigDecimal random.
     * @param n such that 0 &le; val(r) &le; (2<sup>n</sup>-1).
     * 0 &le; exp(r) &le; (100-1).
     * @return r, a random BigDecimal.
     */
    public BigDecimal random(int n) {
        return random(n,random);
    }


    /** BigDecimal random.
     * @param n such that 0 &le; val(r) &le; (2<sup>n</sup>-1).
     * 0 &le; exp(r) &le; (100-1).
     * @param rnd is a source for random bits.
     * @return r, a random BigDecimal.
     */
    public BigDecimal random(int n, Random rnd) {
        return random(n,100,rnd);
    }


    /** BigDecimal random.
     * @param n such that 0 &le; val(r) &le; (2<sup>n</sup>-1).
     * @param e such that 0 &le; exp(r) &le; (e-1).
     * @return r, a random BigDecimal.
     */
    public BigDecimal random(int n, int e) {
        return random(n,e,random);
    }


    /** BigDecimal random.
     * @param n such that 0 &le; val(r) &le; (2<sup>n</sup>-1).
     * @param e such that 0 &le; exp(r) &le; (e-1).
     * @param rnd is a source for random bits.
     * @return r, a random BigDecimal.
     */
    public BigDecimal random(int n, int e, Random rnd) {
        java.math.BigInteger r = new java.math.BigInteger( n, rnd );
        if ( rnd.nextBoolean() ) {
           r = r.negate();
        }
        int scale = rnd.nextInt(e);
        if ( rnd.nextBoolean() ) {
           scale = -scale;
        }
        java.math.BigDecimal d = new java.math.BigDecimal(r,scale,context);
        return new BigDecimal( d, context );
    }


    /** BigDecimal multiply.
     * @param S BigDecimal.
     * @return this*S.
     */
    public BigDecimal multiply(BigDecimal S) {
        return new BigDecimal( val.multiply( S.val, context ) );
    }


    /** BigDecimal summation.
     * @param S BigDecimal.
     * @return this+S.
     */
    public BigDecimal sum(BigDecimal S) {
        return new BigDecimal( val.add( S.val, context ) );
    }


    /** BigDecimal parse from String.
     * @param s String.
     * @return Biginteger from s.
     */
    public BigDecimal parse(String s) {
        return new BigDecimal(s, context);
    }


    /** BigDecimal parse from Reader.
     * @param r Reader.
     * @return next Biginteger from r.
     */
    public BigDecimal parse(Reader r) {
        return parse( StringUtil.nextString(r) );
    }

}
