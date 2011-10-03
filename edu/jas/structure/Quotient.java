/*
 * $Id: Quotient.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;


import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;


/**
 * Quotient element based on RingElem pairs.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class Quotient<C extends RingElem<C> > 
             implements RingElem< Quotient<C> > {

    private static final Logger logger = Logger.getLogger(Quotient.class);
    private boolean debug = logger.isDebugEnabled();


    /** Quotient class factory data structure. 
     */
    public final QuotientRing<C> ring;


    /** Numerator part of the element data structure. 
     */
    public final C num;


    /** Denominator part of the element data structure. 
     */
    public final C den;


    /** The constructor creates a Quotient object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Quotient(QuotientRing<C> r) {
        this( r, r.ring.getZERO() );
    }


    /** The constructor creates a Quotient object 
     * from a ring factory and a numerator element. 
     * The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator.
     */
    public Quotient(QuotientRing<C> r, C n) {
        this( r, n, r.ring.getONE(), true );
    }


    /** The constructor creates a Quotient object 
     * from a ring factory and a numerator and denominator element. 
     * @param r ring factory.
     * @param n numerator.
     * @param d denominator.
     */
    public Quotient(QuotientRing<C> r, 
                    C n, C d) {
        this(r,n,d,false);
    }


    /** The constructor creates a Quotient object 
     * from a ring factory and a numerator and denominator element. 
     * @param r ring factory.
     * @param n numerator.
     * @param d denominator.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    @SuppressWarnings("unchecked")
    protected Quotient(QuotientRing<C> r, 
                       C n, C d,
                       boolean isred) {
        if ( d == null || d.isZERO() ) {
           throw new RuntimeException("denominator may not be zero");
        }
        ring = r;
        if ( d.signum() < 0 ) {
           n = n.negate();
           d = d.negate();
        }
        if ( isred ) {
           num = n;
           den = d;
           return;
        }
        // must reduce to lowest terms
        if ( n instanceof GcdRingElem && d instanceof GcdRingElem ) {
           GcdRingElem ng = (GcdRingElem)n;
           GcdRingElem dg = (GcdRingElem)d;
           C gcd =  (C) ng.gcd( dg );
           if ( debug ) {
              logger.info("gcd = " + gcd);
           }
           //RingElem<C> gcd = ring.ring.getONE();
           if ( gcd.isONE() ) {
              num = n;
              den = d;
           } else {
              num = n.divide( gcd );
              den = d.divide( gcd );
           }
     // } else { // univariate polynomial?
        } else {
           if ( true || debug ) {
              logger.info("gcd = ????");
           }
           num = n;
           den = d;
        }
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public QuotientRing<C> factory() {
        return ring;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Quotient<C> clone() {
        return new Quotient<C>( ring, num, den, true );
    }
   

    /** Is Quotient zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /** Is Quotient one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.equals( den );
    }


    /** Is Quotient unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( num.isZERO() ) {
           return false;
        } else {
           return true;
        }
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Quotient[ " + num.toString() 
                    + " / " + den.toString() + " ]";
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "Quotient( " + num.toScript() 
                    + " , " + den.toScript() + " )";
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


    /** Quotient comparison.  
     * @param b Quotient.
     * @return sign(this-b).
     */
    //JAVA6only: @Override
    public int compareTo(Quotient<C> b) {
        if ( b == null || b.isZERO() ) {
            return this.signum();
        }
        C r = num.multiply( b.den );
        C s = den.multiply( b.num );
        C x = r.subtract( s );
        return x.signum();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked") // not jet working
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof Quotient ) ) {
           return false;
        }
        Quotient<C> a = null;
        try {
            a = (Quotient<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ( 0 == compareTo( a ) );
    }


    /** Hash code for this local.
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


    /** Quotient absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Quotient<C> abs() {
        return new Quotient<C>( ring, num.abs(), den, true );
    }


    /** Quotient summation.
     * @param S Quotient.
     * @return this+S.
     */
    public Quotient<C> sum(Quotient<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        C n = num.multiply( S.den );
        n = n.sum( den.multiply( S.num ) ); 
        C d = den.multiply( S.den );
        return new Quotient<C>( ring, n, d, false );
    }


    /** Quotient negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Quotient<C> negate() {
        return new Quotient<C>( ring, num.negate(), den, true );
    }


    /** Quotient signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /** Quotient subtraction.
     * @param S Quotient.
     * @return this-S.
     */
    public Quotient<C> subtract(Quotient<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        C n = num.multiply( S.den );
        n = n.subtract( den.multiply( S.num ) ); 
        C d = den.multiply( S.den );
        return new Quotient<C>( ring, n, d, false );
    }


    /** Quotient division.
     * @param S Quotient.
     * @return this/S.
     */
    public Quotient<C> divide(Quotient<C> S) {
        return multiply( S.inverse() );
    }


    /** Quotient inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this. 
     */
    public Quotient<C> inverse() {
        return new Quotient<C>( ring, den, num, true );
    }


    /** Quotient remainder.
     * @param S Quotient.
     * @return this - (this/S)*S.
     */
    public Quotient<C> remainder(Quotient<C> S) {
        if ( num.isZERO() ) {
           throw new RuntimeException("element not invertible " + this);
        }
        return ring.getZERO();
    }


    /** Quotient multiplication.
     * @param S Quotient.
     * @return this*S.
     */
    public Quotient<C> multiply(Quotient<C> S) {
        if ( S == null || S.isZERO() ) {
           return S;
        }
        if ( num.isZERO() ) {
           return this;
        }
        if ( S.isONE() ) {
           return this;
        }
        if ( this.isONE() ) {
           return S;
        }
        C n = num.multiply( S.num );
        C d = den.multiply( S.den );
        return new Quotient<C>( ring, n, d, false );
    }

 
    /** Quotient monic.
     * @return this with monic value part.
     */
    public Quotient<C> monic() {
        logger.info("monic not implemented");
        return this;
    }


    /**
     * Greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Quotient<C> gcd(Quotient<C> b) {
        throw new RuntimeException("gcd not implemented " + this.getClass().getName());
    }


    /**
     * Extended greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public Quotient<C>[] egcd(Quotient<C> b) {
        throw new RuntimeException("egcd not implemented " + this.getClass().getName());
    }

}
