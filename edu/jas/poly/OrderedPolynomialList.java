/*
 * $Id: OrderedPolynomialList.java 2921 2009-12-25 17:06:56Z kredel $
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.TreeMap;
import java.util.Comparator;

//import java.io.Serializable;

import edu.jas.structure.RingElem;
//import edu.jas.structure.RingFactory;

//import edu.jas.arith.BigRational;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomialRing;


/**
 * Ordered list of polynomials.
 * Mainly for storage and printing / toString and 
 * conversions to other representations.
 * Polynomials in this list are sorted according to their head terms.
 * @author Heinz Kredel
 */

public class OrderedPolynomialList<C extends RingElem<C> > 
             extends PolynomialList<C> {


    /**
     * Constructor.
     * @param r polynomial ring factory.
     * @param l list of polynomials.
     */
    public OrderedPolynomialList( GenPolynomialRing< C > r,
                                  List< GenPolynomial< C > > l ) {
        super(r, sort(r,l) );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object p) {
        if ( ! super.equals(p) ) {
            return false;
        }
        OrderedPolynomialList< C > pl = null;
        try {
            pl = (OrderedPolynomialList< C >)p;
        } catch (ClassCastException ignored) {
        }
        if ( pl == null ) {
           return false;
        }
        // compare sorted lists
        // done already in super.equals()
        return true;
    }


    /**
     * Sort a list of polynomials with respect to the ascending order 
     * of the leading Exponent vectors. 
     * The term order is taken from the ring.
     * @param r polynomial ring factory.
     * @param l polynomial list.
     * @return sorted polynomial list from l.
     */
    public static <C extends RingElem<C> >
    List<GenPolynomial<C>> sort( GenPolynomialRing< C > r,
                                 List<GenPolynomial<C>> l ) {
        if ( l == null ) {
            return l;
        }
        if ( l.size() <= 1 ) { // nothing to sort
            return l;
        }
        final Comparator<ExpVector> evc = r.tord.getAscendComparator();
        Comparator<GenPolynomial<C>> cmp = new Comparator<GenPolynomial<C>>() {
                public int compare(GenPolynomial<C> p1, 
                                   GenPolynomial<C> p2) {
                       ExpVector e1 = p1.leadingExpVector();
                       ExpVector e2 = p2.leadingExpVector();
                       if ( e1 == null ) {
                          return -1; // dont care
                       }
                       if ( e2 == null ) {
                          return 1; // dont care
                       }
                       if ( e1.length() != e2.length() ) {
                          if ( e1.length() > e2.length() ) {
                             return 1; // dont care
                          } else {
                             return -1; // dont care
                          }
                       }
                       return evc.compare(e1,e2);
                }
            };
        GenPolynomial<C>[] s = null;
        try {
            s = (GenPolynomial<C>[]) new GenPolynomial[ l.size() ]; 
            //System.out.println("s.length = " + s.length );
            //s = l.toArray(s); does not work
            //for ( int i = 0; i < l.size(); i++ ) {
            //    s[i] = l.get(i);
            //}
            int i = 0;
            for ( GenPolynomial<C> p : l ) {
                s[i++] = p;
            }
            Arrays.<GenPolynomial<C>>sort( s, cmp );
            return new ArrayList<GenPolynomial<C>>( 
                            Arrays.<GenPolynomial<C>>asList(s) );
        } catch(ClassCastException ok) {
            System.out.println("Warning: polynomials not sorted");
        }
        return l; // unsorted
    }

}
