/*
 * $Id: Local.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;

import edu.jas.kern.PrettyPrint;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;


/**
 * Local ring element based on GenPolynomial with RingElem interface.
 * Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 * @fix Not jet working because of monic GBs.
 */
public class Local<C extends GcdRingElem<C> > 
             implements RingElem< Local<C> > {


  private static final Logger logger = Logger.getLogger(Local.class);
  private boolean debug = logger.isDebugEnabled();


    /** Local class factory data structure. 
     */
    public final LocalRing<C> ring;


    /** Numerator part of the element data structure. 
     */
    protected final GenPolynomial<C> num;


    /** Denominator part of the element data structure. 
     */
    protected final GenPolynomial<C> den;


    /** Flag to remember if this local element is a unit.
     * -1 is unknown, 1 is unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown



    /** The constructor creates a Local object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Local(LocalRing<C> r) {
        this( r, r.ring.getZERO() );
    }


    /** The constructor creates a Local object 
     * from a ring factory and a numerator polynomial. 
     * The denominator is assumed to be 1.
     * @param r ring factory.
     * @param n numerator polynomial.
     */
    public Local(LocalRing<C> r, GenPolynomial<C> n) {
        this( r, n, r.ring.getONE(), true );
    }


    /** The constructor creates a Local object 
     * from a ring factory and a numerator and denominator polynomial. 
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     */
    public Local(LocalRing<C> r, 
                 GenPolynomial<C> n, GenPolynomial<C> d) {
        this(r,n,d,false);
    }


    /** The constructor creates a Local object 
     * from a ring factory and a numerator and denominator polynomial. 
     * @param r ring factory.
     * @param n numerator polynomial.
     * @param d denominator polynomial.
     * @param isred true if gcd(n,d) == 1, else false.
     */
    protected Local(LocalRing<C> r, 
                    GenPolynomial<C> n, GenPolynomial<C> d,
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
        GenPolynomial<C> p = ring.ideal.normalform( d );
        if ( p == null || p.isZERO() ) {
           throw new RuntimeException("denominator may not be in ideal");
        }
        //d = p; cant do this
        // must reduce to lowest terms
        //GenPolynomial<C> gcd = ring.ring.getONE();
        GenPolynomial<C> gcd = gcd( n, d );
        if ( true || debug ) {
           logger.info("gcd = " + gcd);
        }
        if ( gcd.isONE() ) {
           num = n;
           den = d;
        } else {
           // d not in ideal --> gcd not in ideal 
           //p = ring.ideal.normalform( gcd );
           //if ( p == null || p.isZERO() ) { // todo: find nonzero factor
           //   num = n;
           //   den = d;
           //} else {
              num = n.divide( gcd );
              den = d.divide( gcd );
           //}
        }
    }


    /** Least common multiple.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return lcm(n,d)
     */
    protected GenPolynomial<C> lcm(GenPolynomial<C> n, GenPolynomial<C> d) {
        GenPolynomial<C> lcm = ring.engine.lcm(n,d);
        //* Just for fun, is not efficient.
        //List<GenPolynomial<C>> list;
        //list = new ArrayList<GenPolynomial<C>>( 1 );
        //list.add( n );
        //Ideal<C> N = new Ideal<C>( ring.ring, list, true );
        //list = new ArrayList<GenPolynomial<C>>( 1 );
        //list.add( d );
        //Ideal<C> D = new Ideal<C>( ring.ring, list, true );
        //Ideal<C> L = N.intersect( D );
        //if ( L.list.list.size() != 1 ) {
        //   throw new RuntimeException("lcm not uniqe");
        //}
        //GenPolynomial<C> lcm = L.list.list.get(0);
        return lcm;
    }


    /** Greatest common divisor.
     * Just for fun, is not efficient.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return gcd(n,d)
     */
    protected GenPolynomial<C> gcd(GenPolynomial<C> n, GenPolynomial<C> d) {
        if ( n.isZERO() ) {
           return d;
        }
        if ( d.isZERO() ) {
           return n;
        }
        if ( n.isONE() ) {
           return n;
        }
        if ( d.isONE() ) {
           return d;
        }
        //GenPolynomial<C> p = n.multiply(d);
        //GenPolynomial<C> lcm = lcm(n,d);
        //GenPolynomial<C> gcd = p.divide(lcm);
        GenPolynomial<C> gcd = ring.engine.gcd(n,d);
        return gcd;
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public LocalRing<C> factory() {
        return ring;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
     public Local<C> clone() {
        return new Local<C>( ring, num, den, true );
    }
   

    /** Is Local zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return num.isZERO();
    }


    /** Is Local one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return num.equals( den );
    }


    /** Is Local unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( isunit > 0 ) {
            return true;
        } 
        if ( isunit == 0 ) {
            return false;
        } 
        // not jet known
        if ( num.isZERO() ) {
           isunit = 0;
           return false;
        }
        GenPolynomial<C> p = ring.ideal.normalform( num );
        boolean u = ( p != null && ! p.isZERO() );
        if ( u ) {
           isunit = 1;
        } else {
           isunit = 0;
        }
        return ( u );
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
     public String toString() {
        if ( PrettyPrint.isTrue() ) {
           return num.toString( ring.ring.getVars() ) 
                  + "///" + den.toString( ring.ring.getVars() );
        } else {
           return "Local[ " + num.toString() 
                    + " / " + den.toString() + " ]";
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


    /** Local comparison.  
     * @param b Local.
     * @return sign(this-b).
     */
    //JAVA6only: @Override
    public int compareTo(Local<C> b) {
        if ( b == null || b.isZERO() ) {
            return this.signum();
        }
        GenPolynomial<C> r = num.multiply( b.den );
        GenPolynomial<C> s = den.multiply( b.num );
        GenPolynomial<C> x = r.subtract( s );
        return x.signum();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked") // not jet working
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof Local ) ) {
           return false;
        }
        Local<C> a = null;
        try {
            a = (Local<C>) b;
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


    /** Local absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Local<C> abs() {
        return new Local<C>( ring, num.abs(), den, true );
    }


    /** Local summation.
     * @param S Local.
     * @return this+S.
     */
    public Local<C> sum(Local<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        GenPolynomial<C> n = num.multiply( S.den );
        n = n.sum( den.multiply( S.num ) ); 
        GenPolynomial<C> d = den.multiply( S.den );
        return new Local<C>( ring, n, d, false );
    }


    /** Local negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Local<C> negate() {
        return new Local<C>( ring, num.negate(), den, true );
    }


    /** Local signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return num.signum();
    }


    /** Local subtraction.
     * @param S Local.
     * @return this-S.
     */
    public Local<C> subtract(Local<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        GenPolynomial<C> n = num.multiply( S.den );
        n = n.subtract( den.multiply( S.num ) ); 
        GenPolynomial<C> d = den.multiply( S.den );
        return new Local<C>( ring, n, d, false );
    }


    /** Local division.
     * @param S Local.
     * @return this/S.
     */
    public Local<C> divide(Local<C> S) {
        return multiply( S.inverse() );
    }


    /** Local inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined. 
     */
    public Local<C> inverse() {
        if ( isONE() ) {
           return this;
        }
        if ( isUnit() ) {
           return new Local<C>( ring, den, num, true );
        }
        throw new RuntimeException("element not invertible " + this);
    }


    /** Local remainder.
     * @param S Local.
     * @return this - (this/S)*S.
     */
    public Local<C> remainder(Local<C> S) {
        if ( num.isZERO() ) {
           throw new RuntimeException("element not invertible " + this);
        }
        if ( S.isUnit() ) {
           return ring.getZERO(); 
        } else {
           throw new RuntimeException("remainder not implemented" + S);
        }
    }


    /** Local multiplication.
     * @param S Local.
     * @return this*S.
     */
    public Local<C> multiply(Local<C> S) {
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
        GenPolynomial<C> n = num.multiply( S.num );
        GenPolynomial<C> d = den.multiply( S.den );
        return new Local<C>( ring, n, d, false );
    }

 
    /** Local monic.
     * @return this with monic value part.
     */
    public Local<C> monic() {
        if ( num.isZERO() ) {
           return this;
        }
        C lbc = num.leadingBaseCoefficient();
        lbc = lbc.inverse();
        GenPolynomial<C> n = num.multiply( lbc );
        GenPolynomial<C> d = den.multiply( lbc );
        return new Local<C>( ring, n, d, true );
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Local<C> gcd(Local<C> b) {
        GenPolynomial<C> x = ring.engine.gcd( num, b.num );
        GenPolynomial<C> y = ring.engine.gcd( den, b.den );
        return new Local<C>( ring, x, y, true );
        // * <b>Note: </b>Not implemented, throws RuntimeException.
        // throw new RuntimeException("gcd not implemented " + this.getClass().getName());
    }


    /**
     * Extended greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public Local<C>[] egcd(Local<C> b) {
        throw new RuntimeException("egcd not implemented " + this.getClass().getName());
    }

}
