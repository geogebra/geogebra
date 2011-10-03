/*
 * $Id: QuotientRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.application;

import java.util.Random;
//import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

//import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;

import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GCDFactory;

import edu.jas.util.StringUtil;


/**
 * Quotient ring factory based on GenPolynomial with RingElem interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */
public class QuotientRing<C extends GcdRingElem<C> > 
             implements RingFactory< Quotient<C> >  {

     private static final Logger logger = Logger.getLogger(QuotientRing.class);
     //private boolean debug = logger.isDebugEnabled();


    /** Polynomial ring of the factory. 
     */
    public final GenPolynomialRing<C> ring;


    /** GCD engine of the factory. 
     */
    public final GreatestCommonDivisor<C> engine;


    /** Use GCD of package edu.jas.ufd. 
     */
    public final boolean ufdGCD;


    /** The constructor creates a QuotientRing object 
     * from a GenPolynomialRing. 
     * @param r polynomial ring.
     */
    public QuotientRing(GenPolynomialRing<C> r) {
        this(r,true);
    }


    /** The constructor creates a QuotientRing object 
     * from a GenPolynomialRing. 
     * @param r polynomial ring.
     * @param ufdGCD flag, if syzygy or gcd based algorithm used for engine.
     */
    public QuotientRing(GenPolynomialRing<C> r, boolean ufdGCD) {
        ring = r;
        this.ufdGCD = ufdGCD;
        if ( ! ufdGCD ) {
           engine = null;
           return;
        }
        engine = GCDFactory.<C>getProxy( ring.coFac );
    }


    /** Divide.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return divide(n,d)
     */
    protected GenPolynomial<C> divide(GenPolynomial<C> n, GenPolynomial<C> d) {
        return PolyUtil.<C>basePseudoDivide(n,d);
    }


    /** Greatest common divisor.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return gcd(n,d)
     */
    protected GenPolynomial<C> gcd(GenPolynomial<C> n, GenPolynomial<C> d) {
        if ( ufdGCD ) {
           return engine.gcd(n,d);
        }
        return syzGcd(n,d);
    }


    /** Least common multiple.
     * Just for fun, is not efficient.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return lcm(n,d)
     */
    protected GenPolynomial<C> syzLcm(GenPolynomial<C> n, GenPolynomial<C> d) {
        List<GenPolynomial<C>> list;
        list = new ArrayList<GenPolynomial<C>>( 1 );
        list.add( n );
        Ideal<C> N = new Ideal<C>( ring, list, true );
        list = new ArrayList<GenPolynomial<C>>( 1 );
        list.add( d );
        Ideal<C> D = new Ideal<C>( ring, list, true );
        Ideal<C> L = N.intersect( D );
        if ( L.list.list.size() != 1 ) {
           throw new RuntimeException("lcm not uniqe");
        }
        GenPolynomial<C> lcm = L.list.list.get(0);
        return lcm;
    }


    /** Greatest common divisor.
     * Just for fun, is not efficient.
     * @param n first polynomial.
     * @param d second polynomial.
     * @return gcd(n,d)
     */
    protected GenPolynomial<C> syzGcd(GenPolynomial<C> n, GenPolynomial<C> d) {
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
        GenPolynomial<C> p = n.multiply(d);
        GenPolynomial<C> lcm = syzLcm(n,d);
        GenPolynomial<C> gcd = divide(p,lcm);
        return gcd;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
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
        List<GenPolynomial<C>> pgens = ring.generators();
        List<Quotient<C>> gens = new ArrayList<Quotient<C>>( pgens.size() );
        for ( GenPolynomial<C> p : pgens ) {
            Quotient<C> q = new Quotient<C>( this, p );
            gens.add(q);
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
        return "RF(" + ring.toScript() + ")";
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
        GenPolynomial<C> r = ring.random( n ).monic();
        GenPolynomial<C> s = ring.random( n ).monic();
        while ( s.isZERO() ) {
            s = ring.random( n ).monic();
        }
        return new Quotient<C>( this, r, s, false );
    }


    /**
     * Generate a random residum polynomial.
     * @param k bitsize of random coefficients.
     * @param l number of terms.
     * @param d maximal degree in each variable.
     * @param q density of nozero exponents.
     * @return a random residue polynomial.
     */
    public Quotient<C> random(int k, int l, int d, float q) {
        GenPolynomial<C> r = ring.random(k,l,d,q).monic();
        GenPolynomial<C> s = ring.random(k,l,d,q).monic();
        while ( s.isZERO() ) {
            s = ring.random( k,l,d,q ).monic();
        }
        return new Quotient<C>( this, r, s, false );
    }


    /** Quotient random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random residue element.
     */
    public Quotient<C> random(int n, Random rnd) {
        GenPolynomial<C> r = ring.random( n, rnd ).monic();
        GenPolynomial<C> s = ring.random( n, rnd ).monic();
        while ( s.isZERO() ) {
            s = ring.random( n, rnd ).monic();
        }
        return new Quotient<C>( this, r, s, false);
    }


    /** Parse Quotient from String.
     * Syntax: "{ polynomial | polynomial }" or "{ polynomial }" 
     * or " polynomial | polynomial " or " polynomial " 
     * @param s String.
     * @return Quotient from s.
     */
    public Quotient<C> parse(String s) {
        int i = s.indexOf("{");
        if ( i >= 0 ) {
           s = s.substring(i+1);
        }
        i = s.lastIndexOf("}");
        if ( i >= 0 ) {
           s = s.substring(0,i);
        }
        i = s.indexOf("|");
        if ( i < 0 ) {
           GenPolynomial<C> n = ring.parse( s );
           return new Quotient<C>( this, n );
        }
        String s1 = s.substring(0,i);
        String s2 = s.substring(i+1);
        GenPolynomial<C> n = ring.parse( s1 );
        GenPolynomial<C> d = ring.parse( s2 );
        return new Quotient<C>( this, n, d );
    }


    /** Parse Quotient from Reader.
     * @param r Reader.
     * @return next Quotient from r.
     */
    public Quotient<C> parse(Reader r) {
        String s = StringUtil.nextPairedString(r,'{','}');
        return parse( s );
    }


    /** Degree of extension field.
     * @return degree of this extension field, -1 for transcendental extension.
     */
    public long extensionDegree() {
        long degree = -1L;
        if ( ring.nvar <= 0 ) {
            degree = 0L;
        }
        return degree;
    }

}
