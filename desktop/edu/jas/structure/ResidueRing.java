/*
 * $Id: ResidueRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.Reader;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Residue ring factory based on RingElem and RingFactory module.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class ResidueRing<C extends RingElem<C> > 
             implements RingFactory< Residue<C> >  {

    private static final Logger logger = Logger.getLogger(ResidueRing.class);
    //private boolean debug = logger.isDebugEnabled();


    /** Ring element for reduction. 
     */
    protected final C modul;


    /** Ring factory. 
     */
    protected final RingFactory<C> ring;


    /** Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /** The constructor creates a ResidueRing object 
     * from an ring factory and a modul. 
     * @param r ring factory.
     * @param m modul.
     */
    public ResidueRing(RingFactory<C> r, C m) {
        ring = r;
        if ( m.isZERO() ) {
           throw new RuntimeException("modul may not be null");
        }
        if ( m.isONE() ) {
           logger.warn("modul is one");
        }
        if ( m.signum() < 0 ) {
           m = m.negate();
        }
        modul = m; 
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        throw new RuntimeException("not implemented");
        //return ring.isFinite();
    }


    /** Copy Residue element c.
     * @param c
     * @return a copy of c.
     */
    public Residue<C> copy(Residue<C> c) {
        return new Residue<C>( c.ring, c.val );
    }


    /** Get the zero element.
     * @return 0 as Residue.
     */
    public Residue<C> getZERO() {
        return new Residue<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as Residue.
     */
    public Residue<C> getONE() {
        Residue<C> one = new Residue<C>( this, ring.getONE() );
        if ( one.isZERO() ) {
           logger.warn("one is zero, so all residues are 0");
        }
        return one;
    }


    /**  Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<Residue<C>> generators() {
        List<? extends C> rgens = ring.generators();
        List<Residue<C>> gens = new ArrayList<Residue<C> >( rgens.size() );
        for ( C c: rgens ) {
             gens.add( new Residue<C>(this,c) );
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
     * @return false.
     */
    public boolean isField() {
        if ( isField > 0 ) { 
           return true;
        }
        if ( isField == 0 ) { 
           return false;
        }
        // ideal is prime ?
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /** Get a Residue element from a BigInteger value.
     * @param a BigInteger.
     * @return a Residue.
     */
    public Residue<C> fromInteger(java.math.BigInteger a) {
        return new Residue<C>( this, ring.fromInteger(a) );
    }


    /** Get a Residue element from a long value.
     * @param a long.
     * @return a Residue.
     */
    public Residue<C> fromInteger(long a) {
        return new Residue<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
     public String toString() {
        return "Residue[ " 
                + modul.toString() + " ]";
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "ResidueRing(" + modul.toScript() + ")";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof ResidueRing ) ) {
           return false;
        }
        ResidueRing<C> a = null;
        try {
            a = (ResidueRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        if ( ! ring.equals( a.ring ) ) {
            return false;
        }
        return modul.equals( a.modul );
    }


    /** Hash code for this residue ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 37 * h + modul.hashCode();
       return h;
    }


    /** Residue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Residue<C> random(int n) {
      C x = ring.random( n );
      // x = x.sum( ring.getONE() );
      return new Residue<C>( this, x );
    }


    /** Residue random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Residue<C> random(int n, Random rnd) {
      C x = ring.random( n, rnd );
      // x = x.sum( ring.getONE() );
      return new Residue<C>( this, x);
    }


    /** Parse Residue from String.
     * @param s String.
     * @return Residue from s.
     */
    public Residue<C> parse(String s) {
        C x = ring.parse( s );
        return new Residue<C>( this, x );
    }


    /** Parse Residue from Reader.
     * @param r Reader.
     * @return next Residue from r.
     */
    public Residue<C> parse(Reader r) {
        C x = ring.parse( r );
        return new Residue<C>( this, x );
    }

}
