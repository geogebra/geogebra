/*
 * $Id: QuotientRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.Reader;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
//import edu.jas.structure.GcdRingElem;


/**
 * Quotient ring factory using RingElem and RingFactory.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class QuotientRing<C extends RingElem<C> > 
             implements RingFactory< Quotient<C> >  {

     private static final Logger logger = Logger.getLogger(QuotientRing.class);
     private boolean debug = logger.isDebugEnabled();


    /** Ring factory of this factory. 
     */
    public final RingFactory< C > ring;


    /** The constructor creates a QuotientRing object 
     * from a RingFactory. 
     * @param r ring factory.
     */
    public QuotientRing(RingFactory<C> r) {
        ring = r;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return ring.isFinite();
    }


    /** Copy Quotient element c.
     * @param c
     * @return a copy of c.
     */
    public Quotient<C> copy(Quotient<C> c) {
        return new Quotient<C>( c.ring, c.num, c.den, true );
    }


    /** Get the zero element.
     * @return 0 as Quotient.
     */
    public Quotient<C> getZERO() {
        return new Quotient<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as Quotient.
     */
    public Quotient<C> getONE() {
        return new Quotient<C>( this, ring.getONE() );
    }

    
    /**  Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<Quotient<C>> generators() {
        List<? extends C> rgens = ring.generators();
        List<Quotient<C>> gens = new ArrayList<Quotient<C>>( rgens.size() );
        for ( C c: rgens ) {
             gens.add( new Quotient<C>(this,c) );
        }
        return gens;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return ring.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return ring.isAssociative();
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
        return ring.characteristic();
    }


    /** Get a Quotient element from a BigInteger value.
     * @param a BigInteger.
     * @return a Quotient.
     */
    public Quotient<C> fromInteger(java.math.BigInteger a) {
        return new Quotient<C>( this, ring.fromInteger(a) );
    }


    /** Get a Quotient element from a long value.
     * @param a long.
     * @return a Quotient.
     */
    public Quotient<C> fromInteger(long a) {
        return new Quotient<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
     public String toString() {
        return "Quotient[ " 
                + ring.toString() + " ]";
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "QuotientRing(" + ring.toScript() + ")";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof QuotientRing ) ) {
           return false;
        }
        QuotientRing<C> a = null;
        try {
            a = (QuotientRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ring.equals( a.ring );
    }


    /** Hash code for this quotient ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       return h;
    }


    /** Quotient random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Quotient<C> random(int n) {
        C r = ring.random( n );
        C s = ring.random( n );
        while ( s.isZERO() ) {
            s = ring.random( n );
        }
        return new Quotient<C>( this, r, s, false );
    }


    /** Quotient random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Quotient<C> random(int n, Random rnd) {
        C r = ring.random( n, rnd );
        C s = ring.random( n, rnd );
        while ( s.isZERO() ) {
            s = ring.random( n, rnd );
        }
        return new Quotient<C>( this, r, s, false);
    }


    /** Parse Quotient from String.
     * @param s String.
     * @return Quotient from s.
     */
    public Quotient<C> parse(String s) {
        C x = ring.parse( s );
        return new Quotient<C>( this, x );
    }


    /** Parse Quotient from Reader.
     * @param r Reader.
     * @return next Quotient from r.
     */
    public Quotient<C> parse(Reader r) {
        C x = ring.parse( r );
        if ( debug ) {
           logger.debug("x = " + x);
        }
        return new Quotient<C>( this, x );
    }

}
