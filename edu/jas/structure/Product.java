/*
 * $Id: Product.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;

import java.util.Map;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.NotInvertibleException;


/**
 * Direct product element based on RingElem.
 * Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 */
public class Product<C extends RingElem<C> > 
             implements RegularRingElem< Product<C> > {

    private static final Logger logger = Logger.getLogger(Product.class);
    private boolean debug = logger.isDebugEnabled();


    /** Product class factory data structure. 
     */
    public final ProductRing<C> ring;


    /** Value part of the element data structure. 
     */
    public final SortedMap<Integer,C> val;


    /** Flag to remember if this product element is a unit in each cmponent.
     * -1 is unknown, 1 is unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /** The constructor creates a Product object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Product(ProductRing<C> r) {
        this( r, new TreeMap<Integer,C>(), 0 );
    }


    /** The constructor creates a Product object 
     * from a ring factory and a ring element. 
     * @param r ring factory.
     * @param a ring element.
     */
    public Product(ProductRing<C> r, SortedMap<Integer,C> a) {
        this( r, a, -1 );
    }


    /** The constructor creates a Product object 
     * from a ring factory, a ring element and an indicator if a is a unit. 
     * @param r ring factory.
     * @param a ring element.
     * @param u isunit indicator, -1, 0, 1.
     */
    public Product(ProductRing<C> r, SortedMap<Integer,C> a, int u) {
        ring = r;
        val = a;
        isunit = u;
    }


    /** Get component.
     * @param i index of component.
     * @return val(i).
     */
    public C get(int i) {
        return val.get(i); // auto-boxing
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ProductRing<C> factory() {
        return ring;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Product<C> clone() {
        return new Product<C>( ring, val, isunit );
    }
   

    /** Is Product zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.size() == 0;
    }


    /** Is Product one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        if ( val.size() != ring.length() ) {
           return false;
        }
        for ( C e : val.values() ) {
            if ( ! e.isONE() ) {
               return false;
            }
        } 
        return true;
    }


    /** Is Product full. 
     * @return If every component is non-zero, 
     *         then true is returned, else false.
     */
    public boolean isFull() {
        if ( val.size() != ring.length() ) {
           return false;
        }
        return true;
    }


    /** Is Product unit. 
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
        if ( isZERO() ) {
           isunit = 0;
           return false;
        }
        for ( C e : val.values() ) {
            if ( ! e.isUnit() ) {
               isunit = 0;
               return false;
            }
        }
        isunit = 1;
        return true;
    }


    /** Is Product idempotent. 
     * @return If this is a idempotent element then true is returned, else false.
     */
    public boolean isIdempotent() {
        for ( C e : val.values() ) {
            if ( ! e.isONE() ) {
               return false;
            }
        }
        return true;
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return val.toString(); 
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("( ");
        boolean first = true;
        for ( Integer i : val.keySet() ) {
            C v = val.get(i);
            if ( first ) {
                first = false;
            } else {
                if ( v.signum() < 0 ) {
                    s.append(" - ");
                    v = v.negate();
                } else {
                    s.append(" + ");
                }
            }
            if ( !v.isONE() ) {
                s.append( v.toScript() + "*" );
            } 
            s.append( "pg"+i );
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


    /** Product comparison.  
     * @param b Product.
     * @return sign(this-b).
     */
    //JAVA6only: @Override
    public int compareTo(Product<C> b) {
        if ( ! ring.equals( b.ring ) ) {
           throw new RuntimeException("rings not comparable " + this);
        }
        SortedMap<Integer,C> v = b.val;
        Iterator<Map.Entry<Integer,C>> ti = val.entrySet().iterator();
        Iterator<Map.Entry<Integer,C>> bi = v.entrySet().iterator();
        int s;
        while ( ti.hasNext() && bi.hasNext() ) {
            Map.Entry<Integer,C> te = ti.next();
            Map.Entry<Integer,C> be = bi.next();
            s = te.getKey().compareTo( be.getKey() );
            if ( s != 0 ) {
               return s;
            }
            s = te.getValue().compareTo( be.getValue() );
            if ( s != 0 ) {
               return s;
            }
        }
        if ( ti.hasNext() ) {
           return -1;
        }
        if ( bi.hasNext() ) {
           return 1;
        }
        return 0; 
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked") 
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof Product ) ) {
           return false;
        }
        Product<C> a = null;
        try {
            a = (Product<C>) b;
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
       int h = ring.hashCode();
       h = 37 * h + val.hashCode();
       return h;
    }


    /** Product extend.
     * Add new component j with value of component i.
     * @param i from index.
     * @param j to index.
     * @return the extended value of this.
     */
    public Product<C> extend( int i, int j ) {
        RingFactory<C> rf = ring.getFactory( j );
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>( val );
        C v = val.get( i );
        C w = rf.copy( v ); // valueOf
        if ( ! w.isZERO() ) {
           elem.put( j , w );
        }
        return new Product<C>( ring, elem, isunit );
    }


    /** Product absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Product<C> abs() {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            C v = val.get(i).abs();
            elem.put( i, v );
        }
        return new Product<C>( ring, elem, isunit );
    }


    /** Product summation.
     * @param S Product.
     * @return this+S.
     */
    public Product<C> sum(Product<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        if ( this.isZERO() ) {
           return S;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>( val ); // clone
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : sel.keySet() ) {
            C x = elem.get( i );
            C y = sel.get( i ); // assert y != null
            if ( x != null ) {
                x = x.sum(y);
                if ( ! x.isZERO() ) {
                    elem.put( i, x );
                } else {
                    elem.remove( i );
                }
            } else {
                elem.put( i, y );
            }
        }
        return new Product<C>( ring, elem );
    }


    /** Product negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Product<C> negate() {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            C v = val.get(i).negate();
            elem.put( i, v );
        }
        return new Product<C>( ring, elem, isunit );
    }


    /** Product signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum of first non-zero component.
     */
    public int signum() {
        if ( val.size() == 0 ) {
           return 0;
        }
        C v = val.get( val.firstKey() );
        return v.signum();
    }


    /** Product subtraction.
     * @param S Product.
     * @return this-S.
     */
    public Product<C> subtract(Product<C> S) {
        return sum( S.negate() );
    }


    /** Product quasi-inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined. 
     */
    public Product<C> inverse() {
        if ( this.isZERO() ) {
           return this;
        }
        int isu = 0;
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            C x = val.get( i ); // is non zero
            try {
                x = x.inverse();
            } catch(NotInvertibleException e) {
                // could happen for e.g. ModInteger or AlgebraicNumber
                x = null; //ring.getFactory(i).getZERO();
            }
            if ( x != null && ! x.isZERO() ) { // can happen
               elem.put( i, x );
               isu = 1;
            }
        }
        return new Product<C>( ring, elem, isu );
    }


    /** Product idempotent.  
     * @return smallest S with this*S = this.
     */
    public Product<C> idempotent() {
        if ( this.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            RingFactory<C> f = ring.getFactory( i );
            C x = f.getONE();
            elem.put( i, x );
        }
        return new Product<C>( ring, elem, 1 );
    }


    /** Product idempotent complement.  
     * @return 1-this.idempotent().
     */
    public Product<C> idemComplement() {
        if ( this.isZERO() ) {
            return ring.getONE();
        }
        int isu = 0;
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( int i = 0; i < ring.length(); i++ ) {
            C v = val.get( i );
            if ( v == null ) {
               RingFactory<C> f = ring.getFactory( i );
               C x = f.getONE();
               elem.put( i, x );
               isu = 1;
            }
        }
        return new Product<C>( ring, elem, isu );
    }


    /** Product idempotent and.  
     * @param S Product.
     * @return this.idempotent() and S.idempotent().
     */
    public Product<C> idempotentAnd(Product<C> S) {
        if ( this.isZERO() && S.isZERO() ) {
           return this;
        }
        int isu = 0;
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( int i = 0; i < ring.length(); i++ ) {
            C v = val.get( i );
            C w = S.val.get( i );
            if ( v != null && w != null ) {
               RingFactory<C> f = ring.getFactory( i );
               C x = f.getONE();
               elem.put( i, x );
               isu = 1;
            }
        }
        return new Product<C>( ring, elem, isu );
    }


    /** Product idempotent or.  
     * @param S Product.
     * @return this.idempotent() or S.idempotent().
     */
    public Product<C> idempotentOr(Product<C> S) {
        if ( this.isZERO() && S.isZERO() ) {
           return this;
        }
        int isu = 0;
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( int i = 0; i < ring.length(); i++ ) {
            C v = val.get( i );
            C w = S.val.get( i );
            if ( v != null || w != null ) {
               RingFactory<C> f = ring.getFactory( i );
               C x = f.getONE();
               elem.put( i, x );
               isu = 1;
            }
        }
        return new Product<C>( ring, elem, isu );
    }


    /** Product fill with idempotent.  
     * @param S Product.
     * @return fill this with S.idempotent().
     */
    public Product<C> fillIdempotent(Product<C> S) {
        if ( S.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>( val );
        for ( int i = 0; i < ring.length(); i++ ) {
            C v = elem.get( i );
            if ( v != null ) {
               continue;
            }
            C w = S.val.get( i );
            if ( w != null ) {
               RingFactory<C> f = ring.getFactory( i );
               C x = f.getONE();
               elem.put( i, x );
            }
        }
        return new Product<C>( ring, elem, isunit );
    }


    /** Product fill with one.  
     * @return fill this with one.
     */
    public Product<C> fillOne() {
        if ( this.isFull() ) {
           return this;
        }
        if ( this.isZERO() ) {
           return ring.getONE();
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>( val );
        for ( int i = 0; i < ring.length(); i++ ) {
            C v = elem.get( i );
            if ( v != null ) {
               continue;
            }
            RingFactory<C> f = ring.getFactory( i );
            C x = f.getONE();
            elem.put( i, x );
        }
        return new Product<C>( ring, elem, isunit );
    }


    /** Product quasi-division.
     * @param S Product.
     * @return this/S.
     */
    public Product<C> divide(Product<C> S) {
        if ( S == null ) {
           return ring.getZERO();
        }
        if ( S.isZERO() ) {
           return S;
        }
        if ( this.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : val.keySet() ) {
            C y = sel.get( i ); 
            if ( y != null ) {
               C x = val.get( i );
               try {
                   x = x.divide(y);
               } catch(NotInvertibleException e) { 
                   // should not happen any more
                   System.out.println("product divide error: x = " + x + ", y = " + y);
                   // could happen for e.g. ModInteger or AlgebraicNumber
                   x = null; //ring.getFactory(i).getZERO();
               }
               if ( x != null && ! x.isZERO() ) { // can happen
                  elem.put( i, x );
               }
            }
        }
        return new Product<C>( ring, elem );
    }


    /** Product quasi-remainder.
     * @param S Product.
     * @return this - (this/S)*S.
     */
    public Product<C> remainder(Product<C> S) {
        if ( S == null ) {
            return this; //ring.getZERO();
        }
        if ( S.isZERO() ) {
           return this;
        }
        if ( this.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : val.keySet() ) {
            C y = sel.get( i ); 
            if ( y != null ) {
               C x = val.get( i );
               x = x.remainder(y);
               if ( x != null && ! x.isZERO() ) { // can happen
                  elem.put( i, x );
               }
            }
        }
        return new Product<C>( ring, elem );
    }


    /** Product multiplication.
     * @param S Product.
     * @return this*S.
     */
    public Product<C> multiply(Product<C> S) {
        if ( S == null ) {
           return ring.getZERO();
        }
        if ( S.isZERO() ) {
           return S;
        }
        if ( this.isZERO() ) {
           return this;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : val.keySet() ) {
            C y = sel.get( i ); 
            if ( y != null ) {
               C x = val.get( i );
               x = x.multiply(y);
               if ( x != null && ! x.isZERO() ) {
                  elem.put( i, x );
               }
            }
        }
        return new Product<C>( ring, elem );
    }


    /** Product multiply by coefficient.
     * @param c coefficient.
     * @return this*c.
     */
    public Product<C> multiply(C c) {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( Integer i : val.keySet() ) {
            C v = val.get(i).multiply(c);
            if ( v != null && ! v.isZERO() ) {
               elem.put( i, v );
            }
        }
        return new Product<C>( ring, elem );
    }


    /**
     * Greatest common divisor.
     * @param S other element.
     * @return gcd(this,S).
     */
    public Product<C> gcd(Product<C> S) {
        if ( S == null || S.isZERO() ) {
           return this;
        }
        if ( this.isZERO() ) {
           return S;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>( val ); // clone
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : sel.keySet() ) {
            C x = elem.get( i );
            C y = sel.get( i ); // assert y != null
            if ( x != null ) {
                x = x.gcd(y);
                if ( x != null && ! x.isZERO() ) {
                    elem.put( i, x );
                } else {
                    elem.remove( i );
                }
            } else {
                elem.put( i, y );
            }
        }
        return new Product<C>( ring, elem );
    }


    /**
     * Extended greatest common divisor.
     * @param S other element.
     * @return [ gcd(this,S), c1, c2 ] with c1*this + c2*b = gcd(this,S).
     */
    @SuppressWarnings("unchecked") 
    public Product<C>[] egcd(Product<C> S) {
        Product<C>[] ret = (Product<C>[]) new Product[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if ( S == null || S.isZERO() ) {
           ret[0] = this;
           return ret;
        }
        if ( this.isZERO() ) {
           ret[0] = S;
           return ret;
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>( val ); // clone
        SortedMap<Integer,C> elem1 = this.idempotent().val; // init with 1
        SortedMap<Integer,C> elem2 = new TreeMap<Integer,C>(); // zero
        SortedMap<Integer,C> sel = S.val;
        for ( Integer i : sel.keySet() ) {
            C x = elem.get( i );
            C y = sel.get( i ); // assert y != null
            if ( x != null ) {
                C[] g = x.egcd(y);
                if ( ! g[0].isZERO() ) {
                    elem.put( i, g[0] );
                    elem1.put( i, g[1] );
                    elem2.put( i, g[2] );
                } else {
                    elem.remove( i );
                }
            } else {
                elem.put( i, y );
                elem2.put( i, ring.getFactory(i).getONE() );
            }
        }
        ret[0] = new Product<C>( ring, elem );
        ret[1] = new Product<C>( ring, elem1 );
        ret[2] = new Product<C>( ring, elem2 );
        return ret;
    }
 
}
