/*
 * $Id: ComplexRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;

import java.math.BigInteger;
import java.util.Random;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.StarRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.util.StringUtil;


/**
 * Generic Complex ring factory implementing the RingFactory interface.
 * Objects of this class are immutable.
 * @param <C> base type.
 * @author Heinz Kredel
 */
public class ComplexRing<C extends RingElem<C> >
             implements RingFactory<Complex<C>> {


    private final static Random random = new Random();


    private static final Logger logger = Logger.getLogger(ComplexRing.class);


    /** Complex class elements factory data structure. 
     */
    public final RingFactory<C> ring;


    /** The constructor creates a ComplexRing object.
     * @param ring factory for Complex real and imaginary parts.
     */
    public ComplexRing(RingFactory<C> ring) {
        this.ring = ring;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<Complex<C>> generators() {
        List<Complex<C>> g = new ArrayList<Complex<C>>(2);
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
        return ring.isFinite();
    }


    /** Copy Complex element c.
     * @param c Complex&lt;C&gt;.
     * @return a copy of c.
     */
    public Complex<C> copy(Complex<C> c) {
        return new Complex<C>( this, c.re, c.im );
    }


    /** Get the zero element.
     * @return 0 as Complex&lt;C&gt;.
     */
    public Complex<C> getZERO() {
        return new Complex<C>(this);
    }


    /** Get the one element.
     * @return 1 as Complex&lt;C&gt;.
     */
    public Complex<C> getONE() {
        return new Complex<C>(this,ring.getONE());
    }


    /** Get the i element.
     * @return i as Complex&lt;C&gt;.
     */
    public Complex<C> getIMAG() {
        return new Complex<C>(this,ring.getZERO(),ring.getONE());
    }


    /**
     * Query if this ring is commutative.
     * @return true.
     */
    public boolean isCommutative() {
        return ring.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true.
     */
    public boolean isAssociative() {
        return ring.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return true.
     */
    public boolean isField() {
        return ring.isField();
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /** Get a Complex element from a BigInteger.
     * @param a BigInteger.
     * @return a Complex&lt;C&gt;.
     */
    public Complex<C> fromInteger(BigInteger a) {
        return new Complex<C>( this, ring.fromInteger(a) );
    }


    /** Get a Complex element from a long.
     * @param a long.
     * @return a Complex&lt;C&gt;.
     */
    public Complex<C> fromInteger(long a) {
        return new Complex<C>( this, ring.fromInteger(a) );
    }


    /** Get the String representation.
     */
    @Override
    public String toString() {
        String s = "Complex[" + ring + "]";
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
        s.append("CR(");
        if ( ring instanceof RingElem ) {
            RingElem ri = (RingElem) ring;
            s.append(ri.toScriptFactory());
        } else {
            s.append(ring.toScript());
        }
        s.append(")");
        return s.toString();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") 
    public boolean equals(Object b) {
        if ( ! ( b instanceof ComplexRing ) ) {
           return false;
        }
        ComplexRing<C> a = null;
        try {
            a = (ComplexRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        if ( ! ring.equals( a.ring ) ) {
            return false;
        } else {
            return true;
        }
    }


    /** Hash code for this ComplexRing&lt;C&gt;.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ring.hashCode();
    }


    /** Complex number random.  
     * Random base numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @return R.
     */
    public Complex<C> random(int n) {
        return random(n,random);
//         C r = ring.random( n ).abs();
//         C i = ring.random( n ).abs();
//         return new Complex<C>(this, r, i ); 
    }


    /** Complex number random.  
     * Random base numbers A and B are generated using random(n). 
     * Then R is the complex number with real part A and imaginary part B. 
     * @param n such that 0 &le; A, B &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R.
     */
    public Complex<C> random(int n, Random rnd) {
        C r = ring.random( n, rnd );
        C i = ring.random( n, rnd );
        return new Complex<C>(this, r, i ); 
    }


    /** Parse complex number from string.
     * @param s String.
     * @return Complex<C> from s.
     */
    public Complex<C> parse(String s) {
        return new Complex<C>(this,s);
    }


    /** Parse complex number from Reader.
     * @param r Reader.
     * @return next Complex<C> from r.
     */
    public Complex<C> parse(Reader r) {
        return parse( StringUtil.nextString(r) );
    }

}
