/*
 * $Id: AlgebraicNumberRing.java 3212 2010-07-05 12:54:49Z kredel $
 */

package edu.jas.poly;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.Reader;

//import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;


/**
 * Algebraic number factory class based on GenPolynomial with RingElem 
 * interface.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public class AlgebraicNumberRing<C extends GcdRingElem<C> > 
              implements RingFactory< AlgebraicNumber<C> >  {


    /** Ring part of the factory data structure. 
     */
    public final GenPolynomialRing<C> ring;


    /** Module part of the factory data structure. 
     */
    public final GenPolynomial<C> modul;


    /** Indicator if this ring is a field
     */
    protected int isField = -1; // initially unknown


    /** The constructor creates a AlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     */
    public AlgebraicNumberRing(GenPolynomial<C> m) {
        ring = m.ring;
        modul = m; // assert m != 0
        if ( ring.nvar > 1 ) {
            throw new IllegalArgumentException("only univariate polynomials allowed");
        }
    }


    /** The constructor creates a AlgebraicNumber factory object 
     * from a GenPolynomial objects module. 
     * @param m module GenPolynomial<C>.
     * @param isField indicator if m is prime.
     */
    public AlgebraicNumberRing(GenPolynomial<C> m, boolean isField) {
        ring = m.ring;
        modul = m; // assert m != 0
        this.isField = ( isField ? 1 :  0 );
        if ( ring.nvar > 1 ) {
            throw new IllegalArgumentException("only univariate polynomials allowed");
        }
    }


    /** Get the module part. 
     * @return modul.
     */
    public GenPolynomial<C> getModul() {
        return modul;
    }


    /** Copy AlgebraicNumber element c.
     * @param c algebraic number to copy.
     * @return a copy of c.
     */
    public AlgebraicNumber<C> copy(AlgebraicNumber<C> c) {
        return new AlgebraicNumber<C>( this, c.val );
    }


    /** Get the zero element.
     * @return 0 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getZERO() {
        return new AlgebraicNumber<C>( this, ring.getZERO() );
    }


    /**  Get the one element.
     * @return 1 as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getONE() {
        return new AlgebraicNumber<C>( this, ring.getONE() );
    }

    
    /**  Get the generating element.
     * @return alpha as AlgebraicNumber.
     */
    public AlgebraicNumber<C> getGenerator() {
        return new AlgebraicNumber<C>( this, ring.univariate(0) );
    }


    /**  Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<AlgebraicNumber<C>> generators() {
        List<GenPolynomial<C>> gc = ring.generators();
        List<AlgebraicNumber<C>> gens = new ArrayList<AlgebraicNumber<C>>( gc.size() );
        for ( GenPolynomial<C> g : gc ) {
            gens.add( new AlgebraicNumber<C>( this, g ) );
        }
        return gens;
    }

    
    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return ring.coFac.isFinite();
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
     * @return true if modul is prime, else false.
     */
    public boolean isField() {
        if ( isField > 0 ) { 
           return true;
        }
        if ( isField == 0 ) { 
           return false;
        }
        if ( ! ring.coFac.isField() ) {
           isField = 0;
           return false;
        }
        return false;
    }


    /**
     * Set field property of this ring.
     * @param field true, if this ring is determined to be a field,
                    false, if it is determined that it is not a field.
     */
    public void setField(boolean field) {
        if ( isField > 0 && field ) { 
           return;
        }
        if ( isField == 0 && ! field ) { 
           return;
        }
        if ( field ) {
           isField = 1;
        } else {
           isField = 0;
        }
        return;
    }


    /**
     * Get the internal field indicator.
     * @return internal field indicator.
     */
    public int getField() {
        return isField;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return ring.characteristic();
    }


    /** Get a AlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new AlgebraicNumber<C>( this, ring.fromInteger(a) );
    }


    /** Get a AlgebraicNumber element from a long value.
     * @param a long.
     * @return a AlgebraicNumber.
     */
    public AlgebraicNumber<C> fromInteger(long a) {
        return new AlgebraicNumber<C>( this, ring.fromInteger(a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AlgebraicNumberRing[ " 
              + modul.toString() + " | isField="
              + isField + " :: "
              + ring.toString() + " ]";
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "AN(" + modul.toScript() + ")";
        //return "AN(" + modul.toScript() + "," + isField + "," + ring.toScript() + ")";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object b) {
        if ( ! ( b instanceof AlgebraicNumberRing ) ) {
            return false;
        }
        AlgebraicNumberRing<C> a = null;
        try {
            a = (AlgebraicNumberRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return modul.equals( a.modul );
    }


    /** Hash code for this AlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * modul.hashCode() + ring.hashCode();
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n) {
        GenPolynomial<C> x = ring.random( n ).monic();
        return new AlgebraicNumber<C>(this, x);
    }


    /** AlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public AlgebraicNumber<C> random(int n, Random rnd) {
        GenPolynomial<C> x = ring.random( n, rnd ).monic();
        return new AlgebraicNumber<C>(this, x);
    }


    /** Parse AlgebraicNumber from String.
     * @param s String.
     * @return AlgebraicNumber from s.
     */
    public AlgebraicNumber<C> parse(String s) {
        GenPolynomial<C> x = ring.parse( s );
        return new AlgebraicNumber<C>( this, x );
    }


    /** Parse AlgebraicNumber from Reader.
     * @param r Reader.
     * @return next AlgebraicNumber from r.
     */
    public AlgebraicNumber<C> parse(Reader r) {
        GenPolynomial<C> x = ring.parse( r );
        return new AlgebraicNumber<C>( this, x );
    }


    /** AlgebraicNumber chinese remainder algorithm.  
     * Assert deg(c.modul) >= deg(a.modul) 
     * and c.modul * a.modul = this.modul.
     * @param c AlgebraicNumber.
     * @param ci inverse of c.modul in ring of a.
     * @param a other AlgebraicNumber.
     * @return S, with S mod c.modul == c and S mod a.modul == a. 
     */
    public AlgebraicNumber<C>
           chineseRemainder(AlgebraicNumber<C> c, 
                            AlgebraicNumber<C> ci, 
                            AlgebraicNumber<C> a) {
        if ( true ) { // debug
            if ( c.ring.modul.compareTo( a.ring.modul ) < 1 ) {
               System.out.println("AlgebraicNumber error " + c + ", " + a);
           }
        }
        AlgebraicNumber<C> b = new AlgebraicNumber<C>( a.ring, c.val ); 
                              // c mod a.modul
                              // c( tbcf(a.modul) ) if deg(a.modul)==1
        AlgebraicNumber<C> d = a.subtract( b ); // a-c mod a.modul
        if ( d.isZERO() ) {
           return new AlgebraicNumber<C>( this, c.val );
        }
        b = d.multiply( ci ); // b = (a-c)*ci mod a.modul
        // (c.modul*b)+c mod this.modul = c mod c.modul = 
        // (c.modul*ci*(a-c)+c) mod a.modul = a mod a.modul
        GenPolynomial<C> s = c.ring.modul.multiply( b.val );
        s = s.sum( c.val );
        return new AlgebraicNumber<C>( this, s );
    }


    /** AlgebraicNumber interpolation algorithm.  
     * Assert deg(c.modul) >= deg(A.modul) 
     * and c.modul * A.modul = this.modul.
     * Special case with deg(A.modul) == 1.
     * Similar algorithm as chinese remainder algortihm.
     * @param c AlgebraicNumber.
     * @param ci inverse of (c.modul)(a) in ring of A.
     * @param am trailing base coefficient of modul of other AlgebraicNumber A.
     * @param a value of other AlgebraicNumber A.
     * @return S, with S(c) == c and S(A) == a.
     */
    public AlgebraicNumber<C>
           interpolate(AlgebraicNumber<C> c, 
                       C ci, 
                       C am,
                       C a) {
        C b = PolyUtil.<C>evaluateMain( ring.coFac /*a*/, c.val, am ); 
                              // c mod a.modul
                              // c( tbcf(a.modul) ) if deg(a.modul)==1
        C d = a.subtract( b ); // a-c mod a.modul
        if ( d.isZERO() ) {
           return new AlgebraicNumber<C>( this, c.val );
        }
        b = d.multiply( ci ); // b = (a-c)*ci mod a.modul
        // (c.modul*b)+c mod this.modul = c mod c.modul = 
        // (c.modul*ci*(a-c)+c) mod a.modul = a mod a.modul
        GenPolynomial<C> s = c.ring.modul.multiply( b );
        s = s.sum( c.val );
        return new AlgebraicNumber<C>( this, s );
    }


    /** Depth of extension field tower.
     * @return number of nested algebraic extension fields
     */
    @SuppressWarnings("unchecked")
    public int depth() {
        AlgebraicNumberRing<C> arr = this;
        int depth = 1;
        RingFactory<C> cf = arr.ring.coFac;
        if ( cf instanceof AlgebraicNumberRing ) {
             arr = (AlgebraicNumberRing<C>) (Object) cf;
             depth += arr.depth();
        }
        return depth;
    }


    /** Degree of extension field.
     * @return degree of this algebraic extension field
     */
    public long extensionDegree() {
        long degree = modul.degree(0);
        return degree;
    }


    /** Total degree of nested extension fields.
     * @return degree of tower of algebraic extension fields
     */
    @SuppressWarnings("unchecked")
    public long totalExtensionDegree() {
        long degree = modul.degree(0);
        AlgebraicNumberRing<C> arr = this;
        RingFactory<C> cf = arr.ring.coFac;
        if ( cf instanceof AlgebraicNumberRing ) {
             arr = (AlgebraicNumberRing<C>) (Object) cf;
             if ( degree == 0L ) {
                 degree = arr.totalExtensionDegree();
             } else {
                 degree *= arr.totalExtensionDegree();
             }
        }
        return degree;
    }


}
