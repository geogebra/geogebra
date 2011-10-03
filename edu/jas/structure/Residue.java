/*
 * $Id: Residue.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;


/**
 * Residue element based on RingElem residue.
 * Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 */
public class Residue<C extends RingElem<C> > 
             implements RingElem< Residue<C> > {

    private static final Logger logger = Logger.getLogger(Residue.class);
    private boolean debug = logger.isDebugEnabled();


    /** Residue class factory data structure. 
     */
    protected final ResidueRing<C> ring;


    /** Value part of the element data structure. 
     */
    protected final C val;


    /** Flag to remember if this residue element is a unit.
     * -1 is unknown, 1 is unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /** The constructor creates a Residue object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Residue(ResidueRing<C> r) {
        this( r, r.ring.getZERO(), 0 );
    }


    /** The constructor creates a Residue object 
     * from a ring factory and a ring element. 
     * @param r ring factory.
     * @param a ring element.
     */
    public Residue(ResidueRing<C> r, C a) {
        this( r, a, -1 );
    }


    /** The constructor creates a Residue object 
     * from a ring factory, a ring element and an indicator if a is a unit. 
     * @param r ring factory.
     * @param a ring element.
     * @param u isunit indicator, -1, 0, 1.
     */
    public Residue(ResidueRing<C> r, C a, int u) {
        ring = r;
        C v = a.remainder( ring.modul ); 
        if ( v.signum() < 0 ) {
            v = v.sum( ring.modul );
        }
        val = v;
        switch ( u ) {
        case 0:  isunit = u;
                 break;
        case 1:  isunit = u;
                 break;
        default: isunit = -1;
        }
        if ( val.isUnit() ) {
           isunit = 1;
        }
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ResidueRing<C> factory() {
        return ring;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Residue<C> clone() {
        return new Residue<C>( ring, val );
    }
   

    /** Is Residue zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.equals( ring.ring.getZERO() );
    }


    /** Is Residue one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.equals( ring.ring.getONE() );
    }


    /** Is Residue unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    @SuppressWarnings("unchecked")
     public boolean isUnit() {
        if ( isunit > 0 ) {
            return true;
        } 
        if ( isunit == 0 ) {
            return false;
        } 
        // val.isUnit() already tested
        // not jet known
        if (           val instanceof GcdRingElem 
             && ring.modul instanceof GcdRingElem ) {
           GcdRingElem v = (GcdRingElem)val;
           GcdRingElem m = (GcdRingElem)ring.modul;
           C gcd =  (C) v.gcd( m );
           if ( debug ) {
              logger.info("gcd = " + gcd);
           }
           boolean u = gcd.isONE();
           if ( u ) {
              isunit = 1;
           } else {
              isunit = 0;
           }
           return u;
        }
        // still unknown
        return false;
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
     public String toString() {
        return "Residue[ " + val.toString() 
                 + " mod " + ring.toString() + " ]";
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "Residue( " + val.toScript() 
                   + " , " + ring.toScript() + " )";
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


    /** Residue comparison.  
     * @param b Residue.
     * @return sign(this-b), 0 means that this and b are equivalent in this residue class ring.
     */
    //JAVA6only: @Override
    public int compareTo(Residue<C> b) {
        C v = b.val;
        if ( ! ring.equals( b.ring ) ) {
           v = v.remainder( ring.modul );
        }
        return val.compareTo( v );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     * @return true means that this and b are equivalent in this residue class ring.
     */
    @SuppressWarnings("unchecked") // not jet working
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof Residue ) ) {
           return false;
        }
        Residue<C> a = null;
        try {
            a = (Residue<C>) b;
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
       h = 37 * h + val.hashCode();
       return h;
    }


    /** Residue absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Residue<C> abs() {
        return new Residue<C>( ring, val.abs() );
    }


    /** Residue summation.
     * @param S Residue.
     * @return this+S.
     */
    public Residue<C> sum(Residue<C> S) {
        return new Residue<C>( ring, val.sum( S.val ) );
    }


    /** Residue negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Residue<C> negate() {
        return new Residue<C>( ring, val.negate() );
    }


    /** Residue signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /** Residue subtraction.
     * @param S Residue.
     * @return this-S.
     */
    public Residue<C> subtract(Residue<C> S) {
        return new Residue<C>( ring, val.subtract( S.val ) );
    }


    /** Residue division.
     * @param S Residue.
     * @return this/S.
     */
    public Residue<C> divide(Residue<C> S) {
        return multiply( S.inverse() );
    }


    /** Residue inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined. 
     */
    @SuppressWarnings("unchecked")
     public Residue<C> inverse() {
        if ( isunit == 0 ) {
           throw new RuntimeException("element not invertible (0) " + this);
        }
        if (           val instanceof GcdRingElem 
             && ring.modul instanceof GcdRingElem ) {
           GcdRingElem v = (GcdRingElem)val;
           GcdRingElem m = (GcdRingElem)ring.modul;
           C[] egcd =  (C[])v.egcd( m );
           if ( debug ) {
              logger.info("egcd = " + egcd[0] + ", f = " + egcd[1]);
           }
           if ( ! egcd[0].isONE() ) {
              isunit = 0;
              throw new RuntimeException("element not invertible (gcd)" + this);
           }
           isunit = 1;
           C x = egcd[1];
           return new Residue<C>( ring, x );
        }
        if ( val.isUnit() ) {
           C x = val.inverse();
           return new Residue<C>( ring, x );
        }
        System.out.println("isunit = " + isunit 
                       + ", isUnit() = " + this.isUnit() );
        throw new RuntimeException("element not invertible (!gcd)" + this);
    }


    /** Residue remainder.
     * @param S Residue.
     * @return this - (this/S)*S.
     */
    public Residue<C> remainder(Residue<C> S) {
        C x = val.remainder( S.val );
        return new Residue<C>( ring, x );
    }


    /** Residue multiplication.
     * @param S Residue.
     * @return this*S.
     */
    public Residue<C> multiply(Residue<C> S) {
        return new Residue<C>( ring, val.multiply( S.val ) );
    }


    /**
     * Greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Residue<C> gcd(Residue<C> b) {
        throw new RuntimeException("gcd not implemented " + this.getClass().getName());
    }


    /**
     * Extended greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public Residue<C>[] egcd(Residue<C> b) {
        throw new RuntimeException("egcd not implemented " + this.getClass().getName());
    }
 
}
