/*
 * $Id: GenVector.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.vector;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.ModulFactory;
import edu.jas.structure.ModulElem;

import edu.jas.kern.PrettyPrint;


/**
 * GenVector implements generic vectors with RingElem entries.
 * Vectors of n columns over C.
 * @author Heinz Kredel
 */

public class GenVector<C extends RingElem<C> > 
    implements ModulElem<GenVector<C>,C> {

    private static final Logger logger = Logger.getLogger(GenVector.class);

    public final GenVectorModul< C > modul;

    public final List<C> val;


    /**
     * Constructor for GenVector.
     */
    public GenVector(GenVectorModul< C > m) {
        this( m, m.getZERO().val );
    }


    /**
     * Constructor for GenVector.
     */
    public GenVector(GenVectorModul< C > m, List<C> v) {
        if ( m == null || v == null ) {
            throw new RuntimeException("Empty m or v not allowed, m = " + m + ", v = " +v);
        }
        modul = m;
        val = v;
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("[ ");
        boolean first = true;
        for ( C c : val ) {
            if ( first ) {
                first = false;
            } else {
                s.append(", ");
            }
            s.append( c.toString() );
        }
        s.append(" ]");
        if ( !PrettyPrint.isTrue() ) {
           s.append(" :: " + modul.toString());
           s.append("\n");
        }
        return s.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer();
        s.append("( ");
        boolean first = true;
        for ( C c : val ) {
            if ( first ) {
                first = false;
            } else {
                s.append(", ");
            }
            s.append( c.toScript() );
        }
        s.append(" )");
        return s.toString();
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


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public GenVectorModul<C> factory() {
        return modul;
    }


    /**
     * clone method.
     * @see java.lang.Object#clone()
     */
    @Override
    @SuppressWarnings("unchecked")
    public GenVector<C> clone() {
        //return modul.copy(this);
        ArrayList<C> av = (ArrayList<C>) val;
        return new GenVector<C>( modul, (List<C>)av.clone() );
    }


    /**
     * test if this is equal to a zero vector.
     */
    public boolean isZERO() {
        return ( 0 == this.compareTo( modul.getZERO() ) );
    }


    /**
     * equals method.
     */
    @Override
    public boolean equals( Object other ) { 
        if ( ! (other instanceof GenVector) ) {
            return false;
        }
        GenVector ovec = (GenVector)other;
        if ( ! modul.equals(ovec.modul) ) {
            return false;
        }
        if ( ! val.equals(ovec.val) ) {
            return false;
        }
        return true;
    }


    /** Hash code for this GenVector.
     * @see java.lang.Object#hashCode()
     */
    @Override
     public int hashCode() {
        return 37 * val.hashCode() + modul.hashCode();
    }


    /**
     * compareTo, lexicogaphical comparison.
     * @param b other
     * @return 1 if (this &lt; b), 0 if (this == b) or -1 if (this &gt; b).
     */
    //JAVA6only: @Override
    public int compareTo(GenVector<C> b) {
        if ( ! modul.equals(b.modul) ) {
            return -1;
        }
        List<C> oval = b.val;
        int i = 0;
        for ( C c : val ) {
            int s = c.compareTo( oval.get( i++ ) );
            if ( s != 0 ) {
                return s;
            }
        }
        return 0;
    }


    /**
     * sign of vector.
     * @return 1 if (this &lt; 0), 0 if (this == 0) or -1 if (this &gt; 0).
     */
    public int signum() {
        return compareTo( modul.getZERO() );
    }


    /**
     * Sum of vectors.
     * @return this+b
     */
    public GenVector<C> sum(GenVector<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        int i = 0;
        for ( C c : val ) {
            C e = c.sum( oval.get( i++ ) );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * Difference of vectors.
     * @return this-b
     */
    public GenVector<C> subtract(GenVector<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        int i = 0;
        for ( C c : val ) {
            C e = c.subtract( oval.get( i++ ) );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * Negative of this vector.
     * @return -this
     */
    public GenVector<C> negate() {
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        for ( C c : val ) {
            C e = c.negate();
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * Absolute value of this vector.
     * @return abs(this)
     */
    public GenVector<C> abs() {
        if ( signum() < 0 ) { 
           return negate();
        } else {
           return this;
        }
    }


    /**
     * Product of this vector with scalar.
     * @return this*s
     */
    public GenVector<C> scalarMultiply(C s) {
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        for ( C c : val ) {
            C e = c.multiply( s );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * Left product of this vector with scalar.
     * @return s*this
     */
    public GenVector<C> leftScalarMultiply(C s) {
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        for ( C c : val ) {
            C e = s.multiply( c );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * Linear compination of this vector with 
     * scalar multiple of other vector.
     * @return this*s+b*t
     */
    public GenVector<C> linearCombination(C s, GenVector<C> b, C t) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        int i = 0;
        for ( C c : val ) {
            C c1 = c.multiply(s);
            C c2 = oval.get( i++ ).multiply( t );
            C e = c1.sum( c2 );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * Linear compination of this vector with 
     * scalar multiple of other vector.
     * @return this+b*t
     */
    public GenVector<C> linearCombination(GenVector<C> b, C t) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        int i = 0;
        for ( C c : val ) {
            C c2 = oval.get( i++ ).multiply( t );
            C e = c.sum( c2 );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * Left linear compination of this vector with 
     * scalar multiple of other vector.
     * @return this+t*b
     */
    public GenVector<C> linearCombination(C t, GenVector<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        int i = 0;
        for ( C c : val ) {
            C c2 = t.multiply( oval.get( i++ ) );
            C e = c.sum( c2 );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }


    /**
     * left linear compination of this vector with 
     * scalar multiple of other vector.
     * @return s*this+t*b
     */
    public GenVector<C> leftLinearCombination(C s, C t, 
                                              GenVector<C> b) {
        List<C> oval = b.val;
        ArrayList<C> a = new ArrayList<C>( modul.cols );
        int i = 0;
        for ( C c : val ) {
            C c1 = s.multiply(c);
            C c2 = t.multiply( oval.get( i++ ) );
            C e = c1.sum( c2 );
            a.add( e );
        }
        return new GenVector<C>(modul,a);
    }



    /**
     * scalar / dot product of this vector with other vector.
     * @return this . b
     */
    public C scalarProduct(GenVector<C> b) {
        C a = modul.coFac.getZERO();
        List<C> oval = b.val;
        int i = 0;
        for ( C c : val ) {
            C c2 = c.multiply( oval.get( i++ ) );
            a = a.sum( c2 );
        }
        return a;
    }


    /**
     * scalar / dot product of this vector with list of other vectors.
     * @return this * b
     */
    public GenVector<C> scalarProduct(List<GenVector<C>> B) {
        GenVector<C> A = modul.getZERO();
        int i = 0;
        for ( C c : val ) {
            GenVector<C> b = B.get( i++ );
            GenVector<C> a = b.leftScalarMultiply( c );
            A = A.sum( a );
        }
        return A;
    }


    /**
     * right scalar / dot product of this vector with list 
     * of other vectors.
     * @return b * this
     */
    public GenVector<C> rightScalarProduct(List<GenVector<C>> B) {
        GenVector<C> A = modul.getZERO();
        int i = 0;
        for ( C c : val ) {
            GenVector<C> b = B.get( i++ );
            GenVector<C> a = b.scalarMultiply( c );
            A = A.sum( a );
        }
        return A;
    }

}
