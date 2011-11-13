/*
 * $Id: ProductRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.structure;

import java.io.Reader;
import java.io.StringReader;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Random;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Direct product ring factory based on RingElem and RingFactory module.
 * Objects of this class are <b>mutable</b>.
 * @author Heinz Kredel
 */
public class ProductRing<C extends RingElem<C> > 
             implements RingFactory< Product<C> > {

    private static final Logger logger = Logger.getLogger(ProductRing.class);
    //private boolean debug = logger.isDebugEnabled();


    /** Ring factory is n copies. 
     */
    protected int nCopies;


    /** One Ring factory. 
     */
    protected final RingFactory<C> ring;


    /** Ring factory list. 
     */
    protected final List<RingFactory<C>> ringList;


    /**
     * A default random sequence generator.
     */
    protected final static Random random = new Random(); 


    /** The constructor creates a ProductRing object 
     * from an ring factory and a modul. 
     * @param r ring factory.
     * @param n number of copies.
     */
    public ProductRing(RingFactory<C> r, int n) {
        ring = r;
        nCopies = n;
        ringList = null;
    }


    /** The constructor creates a ProductRing object 
     * from an ring factory and a modul. 
     * @param l list of ring factories.
     */
    public ProductRing(List<RingFactory<C>> l) {
        ringList = l;
        ring = null;
        nCopies = 0;
    }


    /** Get ring factory at index i.
     * @param i index.
     * @return RingFactory_i.
     */
    public RingFactory<C> getFactory(int i) {
        if ( nCopies != 0 ) {
           if ( 0 <= i && i < nCopies ) {
              return ring;
           }
           throw new RuntimeException("index out of bound " 
                                     + this.getClass().getName());
        } else {
           return ringList.get(i);
        }
    }


    /** Add a ring factory.
     * @param rf new ring factory.
     */
    public synchronized void addFactory(RingFactory<C> rf) {
        if ( nCopies != 0 ) {
           if ( ring.equals(rf) ) {
              nCopies++;
           }
           throw new RuntimeException("wrong RingFactory: " + rf);
        } else {
           ringList.add(rf);
        }
    }


    /** Contains a ring factory.
     * @param rf ring factory.
     * @return true, if rf is contained in this, else false.
     */
    public boolean containsFactory(RingFactory<C> rf) {
        if ( nCopies != 0 ) {
           if ( ring.equals(rf) ) {
              return true;
           }
           return false; // misleading
        } else {
           return ringList.contains(rf);
        }
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        if ( nCopies != 0 ) {
           return ring.isFinite();
        } else {
           for ( RingFactory<C> f : ringList ) {
               boolean b = f.isFinite();
               if ( !b ) {
                   return false;
               }
           }
           return true;
        }
    }


    /** Copy Product element c.
     * @param c
     * @return a copy of c.
     */
    public Product<C> copy(Product<C> c) {
        return new Product<C>( c.ring, c.val, c.isunit );
    }


    /** Get the zero element.
     * @return 0 as Product.
     */
    public Product<C> getZERO() {
        return new Product<C>( this );
    }


    /** Get the one element.
     * @return 1 as Product.
     */
    public Product<C> getONE() {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        if ( nCopies != 0 ) {
           for ( int i = 0; i < nCopies; i++ ) {
               elem.put( i, ring.getONE() );
           }
        } else {
           int i = 0;
           for ( RingFactory<C> f : ringList ) {
               elem.put( i, f.getONE() );
               i++;
           }
        }
        return new Product<C>( this, elem, 1 );
    }


    /**  Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<Product<C>> generators() {
        List<Product<C>> gens = new ArrayList<Product<C>>(/*nCopies*ring.generators.size()*/);
        int n = nCopies;
        if ( n == 0 ) {
            n = ringList.size();
        }
        for ( int i = 0; i < n; i++ ) {
            //System.out.println("i = " + i + ", n = " + n);
            RingFactory<C> f = getFactory(i);
            List<? extends C> rgens = f.generators();
            for ( C c: rgens ) {
                SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
                elem.put( i, c );
                Product<C> g = new Product<C>( this, elem );
                //g = g.fillOne();
                gens.add( g );
            }
        } 
        return gens;
    }


    /** Get an atomic element.
     * @param i index.
     * @return e_i as Product.
     */
    public Product<C> getAtomic(int i) {
        if ( i < 0 || i >= length() ) {
           throw new RuntimeException("index out of bounds " + i);
        }
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        if ( nCopies != 0 ) {
           elem.put( i, ring.getONE() );
        } else {
           RingFactory<C> f = ringList.get(i);
           elem.put( i, f.getONE() );
        }
        return new Product<C>( this, elem, 1 );
    }


    /** Get the number of factors of this ring.
     * @return nCopies or ringList.size().
     */
    public int length() {
        if ( nCopies != 0 ) {
           return nCopies;
        } else {
           return ringList.size();
        }
    }

    
    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        if ( nCopies != 0 ) {
           return ring.isCommutative();
        } else {
           for ( RingFactory<C> f : ringList ) {
               if ( ! f.isCommutative() ) {
                  return false;
               }
           }
           return true;
        }
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        if ( nCopies != 0 ) {
           return ring.isAssociative();
        } else {
           for ( RingFactory<C> f : ringList ) {
               if ( ! f.isAssociative() ) {
                  return false;
               }
           }
           return true;
        }
    }


    /**
     * Query if this ring is a field.
     * @return true or false.
     */
    public boolean isField() {
        if ( nCopies != 0 ) {
           if ( nCopies == 1 ) {
              return ring.isField();
           }
        } else {
           if ( ringList.size() == 1 ) {
              return ringList.get(0).isField();
           }
        }
        return false;
    }


    /**
     * Query if this ring consists only of fields.
     * @return true or false.
     */
    public boolean onlyFields() {
        if ( nCopies != 0 ) {
            return ring.isField();
        } else {
           for ( RingFactory<C> f : ringList ) {
               if ( ! f.isField() ) {
                  return false;
               }
           }
        }
        return true;
    }


    /**
     * Characteristic of this ring.
     * @return minimal characteristic of ring component.
     */
    public java.math.BigInteger characteristic() {
        if ( nCopies != 0 ) {
           return ring.characteristic();
        } else {
           java.math.BigInteger c = null;
           java.math.BigInteger d;
           for ( RingFactory<C> f : ringList ) {
               if ( c == null ) {
                  c = f.characteristic();
               } else {
                  d = f.characteristic();
                  if ( c.compareTo(d) > 0 ) { // c > d
                     c = d;
                  }
               }
           }
           return c;
        }
    }


    /** Get a Product element from a BigInteger value.
     * @param a BigInteger.
     * @return a Product.
     */
    public Product<C> fromInteger(java.math.BigInteger a) {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        if ( nCopies != 0 ) {
           C c = ring.fromInteger(a);
           for ( int i = 0; i < nCopies; i++ ) {
               elem.put( i, c );
           }
        } else {
           int i = 0;
           for ( RingFactory<C> f : ringList ) {
               elem.put( i, f.fromInteger(a) );
               i++;
           }
        }
        return new Product<C>( this, elem );
    }


    /** Get a Product element from a long value.
     * @param a long.
     * @return a Product.
     */
    public Product<C> fromInteger(long a) {
        return fromInteger( new java.math.BigInteger(""+a) );
    }
    

    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if ( nCopies != 0 ) {
           String cf = ring.toString();
           if ( cf.matches("[0-9].*") ) {
               cf = ring.getClass().getSimpleName();
           } 
           return "ProductRing[ " 
                  + cf + "^" + nCopies + " ]";
        } else {
           StringBuffer sb = new StringBuffer("ProductRing[ ");
           int i = 0;
           for ( RingFactory<C> f : ringList ) {
               if ( i != 0 ) {
                  sb.append( ", " );
               }
               String cf = f.toString();
               if ( cf.matches("[0-9].*") ) {
                  cf = f.getClass().getSimpleName();
               } 
               sb.append( cf ); 
               i++;
           }
           sb.append(" ]");
           return sb.toString();
        }
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("RR( [ ");
        for ( int i = 0; i < length(); i++ ) {
            if ( i > 0 ) {
                s.append(", ");
            }
            RingFactory<C> v = getFactory(i);
            String f = null;
            try {
                f = ((RingElem<C>)v).toScriptFactory(); // sic
            } catch (Exception e) {
                f = v.toScript();
            }
            s.append( f );
        }
        s.append(" ] )");
        return s.toString();
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") 
    public boolean equals(Object b) {
        if ( ! ( b instanceof ProductRing ) ) {
           return false;
        }
        ProductRing<C> a = null;
        try {
            a = (ProductRing<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        if ( nCopies != 0 ) {
           if ( nCopies != a.nCopies || !ring.equals( a.ring ) ) {
              return false;
           }
        } else {
           if ( ringList.size() != a.ringList.size() ) {
              return false;
           }
           int i = 0;
           for ( RingFactory<C> f : ringList ) {
               if ( !f.equals( a.ringList.get(i) ) ) {
                  return false;
               }
               i++;
           }
        }
        return true;
    }


    /** Hash code for this product ring.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h = 0;
       if ( nCopies != 0 ) {
          h = ring.hashCode();
          h = 37 * h + nCopies;
       } else {
          for ( RingFactory<C> f : ringList ) {
              h = 37 * h + f.hashCode(); 
          }
       }
       return h;
    }


    /** Product random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random product element v.
     */
    public Product<C> random(int n) {
        return random( n, 0.5f );
    }


    /** Product random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param q density of nozero entries.
     * @return a random product element v.
     */
    public Product<C> random(int n, float q) {
        return random( n, q, random );
    }


    /** Product random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random product element v.
     */
    public Product<C> random(int n, Random rnd) {
        return random( n, 0.5f, random );
    }


    /** Product random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param q density of nozero entries.
     * @param rnd is a source for random bits.
     * @return a random product element v.
     */
    public Product<C> random(int n, float q, Random rnd) {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        float d;
        if ( nCopies != 0 ) {
           for ( int i = 0; i < nCopies; i++ ) {
               d = rnd.nextFloat();
               if ( d < q ) {
                  C r = ring.random( n, rnd );
                  if ( !r.isZERO() ) {
                     elem.put( i, r );
                  }
               }
           }
        } else {
           int i = 0;
           for ( RingFactory<C> f : ringList ) {
               d = rnd.nextFloat();
               if ( d < q ) {
                  C r = f.random( n, rnd );
                  if ( !r.isZERO() ) {
                     elem.put( i, r );
                  }
               }
               i++;
           }
        }
        return new Product<C>( this, elem );
    }


    /** Parse Product from String.
     * @param s String.
     * @return Product from s.
     */
    public Product<C> parse(String s) {
        StringReader sr = new StringReader(s);
        return parse( sr );
    }


    /** Parse Product from Reader.
     * Syntax: p1 ... pn (no commas)
     * @param r Reader.
     * @return next Product from r.
     */
    public Product<C> parse(Reader r) {
        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        if ( nCopies != 0 ) {
           for ( int i = 0; i < nCopies; i++ ) {
               elem.put( i, ring.parse( r ) );
           }
        } else {
           int i = 0;
           for ( RingFactory<C> f : ringList ) {
               elem.put( i, f.parse( r ) );
               i++;
           }
        }
        return new Product<C>( this, elem );
    }

}
