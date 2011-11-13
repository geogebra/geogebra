/*
 * $Id: LocalRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;

import java.util.Random;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Local ring factory based on RingElem principal ideal.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class LocalRing<C extends RingElem<C> > 
             implements RingFactory< Local<C> >  {

     private static final Logger logger = Logger.getLogger(LocalRing.class);
     //private boolean debug = logger.isDebugEnabled();


    /** Ideal generator for localization. 
     */
    protected final C ideal;


    /** Ring factory. 
     */
    protected final RingFactory<C> ring;


    /** Indicator if this ring is a field.
     */
    protected int isField = -1; // initially unknown


    /** The constructor creates a LocalRing object 
     * from a RingFactory and a RingElem. 
     * @param i localization ideal generator.
     */
    public LocalRing(RingFactory<C> r, C i) {
        ring = r;
        if ( i == null ) {
           throw new RuntimeException("ideal may not be null");
        }
        ideal = i;
        if ( ideal.isONE() ) {
           throw new RuntimeException("ideal may not be 1");
        }
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return ring.isFinite();
    }


    /** Copy Local element c.
     * @param c
     * @return a copy of c.
     */
    public Local<C> copy(Local<C> c) {
        return new Local<C>( c.ring, c.num, c.den, true );
    }


    /** Get the zero element.
     * @return 0 as Local.
     */
    public Local<C> getZERO() {
        return new Local<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as Local.
     */
    public Local<C> getONE() {
        return new Local<C>( this, ring.getONE() );
    }


    /**  Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<Local<C>> generators() {
        List<? extends C> rgens = ring.generators();
        List<Local<C>> gens = new ArrayList<Local<C>>( rgens.size()-1 );
        for ( C c: rgens ) {
            if ( !c.isONE() ) {
                gens.add( new Local<C>(this,c) );
            }
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
        // ??
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /** Get a Local element from a BigInteger value.
     * @param a BigInteger.
     * @return a Local.
     */
    public Local<C> fromInteger(java.math.BigInteger a) {
        return new Local<C>( this, ring.fromInteger(a) );
    }


    /** Get a Local element from a long value.
     * @param a long.
     * @return a Local.
     */
    public Local<C> fromInteger(long a) {
        return new Local<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Local[ " 
                + ideal.toString() + " ]";
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "LocalRing(" + ideal.toScript() + ")";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof LocalRing ) ) {
           return false;
        }
        LocalRing<C> a = null;
        try {
            a = (LocalRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        if ( ! ring.equals( a.ring ) ) {
            return false;
        }
        return ideal.equals( a.ideal );
    }


    /** Hash code for this local ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 37 * h + ideal.hashCode();
       return h;
    }


    /** Local random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random residue element.
     */
    public Local<C> random(int n) {
      C r = ring.random( n );
      C s = ring.random( n );
      s = s.remainder( ideal );
      while ( s.isZERO() ) {
          logger.debug("zero was in ideal");
          s = ring.random( n );
          s = s.remainder( ideal );
      }
      return new Local<C>( this, r, s, false );
    }


    /** Local random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Local<C> random(int n, Random rnd) {
      C r = ring.random( n, rnd );
      C s = ring.random( n, rnd );
      s = s.remainder( ideal );
      while ( s.isZERO() ) {
          logger.debug("zero was in ideal");
          s = ring.random( n, rnd );
          s = s.remainder( ideal );
      }
      return new Local<C>( this, r, s, false );
    }


    /** Parse Local from String.
     * @param s String.
     * @return Local from s.
     */
    public Local<C> parse(String s) {
        C x = ring.parse( s );
        return new Local<C>( this, x );
    }


    /** Parse Local from Reader.
     * @param r Reader.
     * @return next Local from r.
     */
    public Local<C> parse(Reader r) {
        C x = ring.parse( r );
        return new Local<C>( this, x );
    }

}
