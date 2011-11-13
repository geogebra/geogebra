/*
 * $Id: PolynomialList.java 3047 2010-03-14 21:29:42Z kredel $
 */

package edu.jas.poly;


import java.lang.Comparable;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
//import edu.jas.structure.RingFactory;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;

import edu.jas.vector.ModuleList;


/**
 * List of polynomials.
 * Mainly for storage and printing / toString and 
 * conversions to other representations.
 * @author Heinz Kredel
 */

public class PolynomialList<C extends RingElem<C> > 
    implements Comparable<PolynomialList<C>>, Serializable, Cloneable {


    /** The factory for the solvable polynomial ring. 
     */
    public final GenPolynomialRing< C > ring;


    /** The data structure is a List of polynomials. 
     */
    public final List< GenPolynomial<C> > list;


    private static final Logger logger = Logger.getLogger(PolynomialList.class);


    /**
     * Constructor.
     * @param r polynomial ring factory.
     * @param l list of polynomials.
     */
    public PolynomialList( GenPolynomialRing< C > r,
                           List<GenPolynomial< C >> l) {
        ring = r;
        list = l; 
    }


    /**
     * Constructor.
     * @param r solvable polynomial ring factory.
     * @param l list of solvable polynomials.
     */
    public PolynomialList( GenSolvablePolynomialRing< C > r,
                           List<GenSolvablePolynomial< C >> l) {
        this( r, PolynomialList.<C>castToList(l) ); 
    }


    /**
     * Clone this.
     * @return a copy of this.
     */
    @Override
    public PolynomialList<C> clone() {
        return new PolynomialList<C>(ring,new ArrayList<GenPolynomial<C>>(list));
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override 
    @SuppressWarnings("unchecked")
    public boolean equals(Object p) {
        if ( ! (p instanceof PolynomialList) ) {
            System.out.println("no PolynomialList");
            return false;
        }
        PolynomialList< C > pl = null;
        try {
            pl = (PolynomialList< C >)p;
        } catch (ClassCastException ignored) {
        }
        if ( pl == null ) {
           return false;
        }
        if ( ! ring.equals( pl.ring ) ) {
            System.out.println("not same Ring " + ring.toScript() + ", " + pl.ring.toScript());
            return false;
        }
        return ( compareTo(pl) == 0 );
        // otherwise tables may be different
    }


    /** Polynomial list comparison.  
     * @param L other PolynomialList.
     * @return lexicographical comparison, sign of first different polynomials.
     */
    public int compareTo(PolynomialList<C> L) {
        int si = L.list.size();
        if ( list.size() < si ) { // minimum
            si = list.size();
        }
        int s = 0;
        List<GenPolynomial<C>> l1 = OrderedPolynomialList.<C>sort( ring, list );
        List<GenPolynomial<C>> l2 = OrderedPolynomialList.<C>sort( ring, L.list );
        for ( int i = 0; i < si; i++ ) {
            GenPolynomial<C> a = l1.get(i);
            GenPolynomial<C> b = l2.get(i);
            s = a.compareTo(b);
            if ( s != 0 ) {
               return s;
            }
        }
        if ( list.size() > si ) { 
            return 1;
        }
        if ( L.list.size() > si ) { 
            return -1;
        }
        return s;
    }


    /** Hash code for this polynomial list.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 37 * h + ( list == null ? 0 : list.hashCode() );
       return h;
    }


    /**
     * String representation of the polynomial list.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer erg = new StringBuffer();
        String[] vars = null;
        if ( ring != null ) {
           erg.append( ring.toString() );
           vars = ring.getVars();
        }
        boolean first = true;
        erg.append("\n(\n");
        String sa = null;
        for ( GenPolynomial<C> oa: list ) {
            if ( vars != null ) {
               sa = oa.toString(vars);
            } else {
               sa = oa.toString();
            }
            if ( first ) {
               first = false;
            } else {
               erg.append( ", " );
               if ( sa.length() > 10 ) {
                  erg.append("\n");
               }
            }
            erg.append( "( " + sa + " )" );
        }
        erg.append("\n)");
        return erg.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this polynomial list.
     */
    public String toScript() {
        // Python case
        StringBuffer erg = new StringBuffer();
        erg.append("Ideal(");
        if ( ring != null ) {
           erg.append( ring.toScript() );
        }
        if ( list == null ) {
            erg.append(")");
            return erg.toString();
        }
        erg.append(",list=[");
        boolean first = true;
        String sa = null;
        for ( GenPolynomial<C> oa: list ) {
            sa = oa.toScript();
            if ( first ) {
               first = false;
            } else {
               erg.append( ", " );
            }
            erg.append( "( " + sa + " )" );
        }
        erg.append("])");
        return erg.toString();
    }


    /**
     * Get ModuleList from PolynomialList.
     * Extract module from polynomial ring. 
     * @see edu.jas.vector.ModuleList
     * @param i number of variables to be contract form the polynomials.
     * @return module list corresponding to this.
     */
    @SuppressWarnings("unchecked")
    public ModuleList<C> getModuleList(int i) {
        GenPolynomialRing< C > pfac = ring.contract(i);
        logger.debug("contracted ring = " + pfac);
        //System.out.println("contracted ring = " + pfac);

        List<List<GenPolynomial<C>>> vecs = null;
        if ( list == null ) { 
           return new ModuleList<C>(pfac,vecs);
        }
        int rows = list.size();
        vecs = new ArrayList<List<GenPolynomial<C>>>( rows );
        if ( rows == 0 ) { // nothing to do
           return new ModuleList<C>(pfac,vecs);
        }

        ArrayList<GenPolynomial<C>> zr 
             = new ArrayList<GenPolynomial<C>>( i-1 );
        GenPolynomial<C> zero = pfac.getZERO();
        for ( int j = 0; j < i; j++ ) {
            zr.add(j,zero);
        }

        for ( GenPolynomial<C> p: list ) {
            if ( p != null ) {
                Map<ExpVector,GenPolynomial<C>> r = null;
                r = p.contract( pfac );
                //System.out.println("r = " + r ); 
                List<GenPolynomial<C>> row 
                    = (ArrayList<GenPolynomial<C>>)zr.clone();
                for ( ExpVector e: r.keySet() ) {
                    int[] dov = e.dependencyOnVariables();
                    int ix = 0;
                    if ( dov.length > 1 ) {
                       throw new RuntimeException("wrong dependencyOnVariables " + e);
                    } else if ( dov.length == 1 )  {
                       ix = dov[0];
                    }
                    //ix = i-1 - ix; // revert
                    //System.out.println("ix = " + ix ); 
                    GenPolynomial<C> vi = r.get( e );
                    row.set(ix,vi);
                }
                //System.out.println("row = " + row ); 
                vecs.add( row );
            }
        }
        return new ModuleList<C>(pfac,vecs);
    }


    /**
     * Get list as List of GenSolvablePolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List&lt;GenSolvablePolynomial&lt;C&gt;&gt;) list.
     * @return solvable polynomial list from this.
     */
    public List< GenSolvablePolynomial<C> > castToSolvableList() {
        List< GenSolvablePolynomial<C> > slist = null;
        if ( list == null ) {
            return slist;
        }
        slist = new ArrayList< GenSolvablePolynomial<C> >( list.size() ); 
        GenSolvablePolynomial<C> s;
        for ( GenPolynomial<C> p: list ) {
            if ( ! (p instanceof GenSolvablePolynomial) ) {
               throw new RuntimeException("no solvable polynomial "+p);
            }
            s = (GenSolvablePolynomial<C>) p;
            slist.add( s );
        }
        return slist;
    }


    /**
     * Get list of extensions of polynomials as List of GenPolynomials.
     * Required because no List casts allowed. Equivalent to 
     * cast (List&lt;GenPolynomial&lt;C&gt;&gt;) list.
     * Mainly used for lists of GenSolvablePolynomials.
     * @param slist list of extensions of polynomials.
     * @return polynomial list from slist.
     */
    public static <C extends RingElem<C> > 
           List< GenPolynomial<C> > 
           castToList( List<? extends GenPolynomial<C>> slist) {
        List< GenPolynomial<C> > list = null;
        if ( slist == null ) {
            return list;
        }
        list = new ArrayList< GenPolynomial<C> >( slist.size() ); 
        for ( GenPolynomial<C> p: slist ) {
            list.add( p );
        }
        return list;
    }


  /**
   * Test if list contains only ZEROs.
   * @return true, if this is the 0 list, else false
   */
  public boolean isZERO() {
      if ( list == null ) {
          return true;
      }
      for ( GenPolynomial<C> p : list ) {
          if ( p == null ) {
              continue;
          }
          if ( ! p.isZERO() ) {
             return false;
          }
      }
      return true;
  }


  /**
   * Test if list contains a ONE.
   * @return true, if this contains 1, else false
   */
  public boolean isONE() {
      if ( list == null ) {
          return false;
      }
      for ( GenPolynomial<C> p : list ) {
          if ( p == null ) {
              continue;
          }
          if ( p.isONE() ) {
             return true;
          }
      }
      return false;
  }


}
