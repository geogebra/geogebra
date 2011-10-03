/*
 * $Id: OrderedModuleList.java 2920 2009-12-25 16:50:47Z kredel $
 */

package edu.jas.vector;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

//import edu.jas.structure.RingFactory;
import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;



/**
 * Ordered list of vectors of polynomials.
 * Mainly for storage and printing / toString and 
 * conversions to other representations.
 * Lists of polynomials in this list are sorted according to 
 * the head terms of the first column.
 * @author Heinz Kredel
 */

public class OrderedModuleList<C extends RingElem<C> > 
             extends ModuleList<C> {


    /**
     * Constructor.
     * @param r polynomial ring factory.
     * @param l list of list of polynomials.
     */
    public OrderedModuleList( GenPolynomialRing< C > r,
                              List<List<GenPolynomial<C>>> l ) {
        super( r, sort( r, ModuleList.padCols(r,l) ) );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked") // not jet working
    public boolean equals(Object m) {
        if ( ! super.equals(m) ) {
            return false;
        }
        OrderedModuleList<C> ml = null;
        try {
            ml = (OrderedModuleList<C>)m;
        } catch (ClassCastException ignored) {
        }
        if ( ml == null ) {
           return false;
        }
        // compare sorted lists
        // done already in super.equals()
        return true;
    }



    /**
     * Sort a list of vectors of polynomials with respect to the 
     * ascending order of the leading Exponent vectors of the 
     * first column. 
     * The term order is taken from the ring.
     * @param r polynomial ring factory.
     * @param l list of polynomial lists.
     * @return sorted list of polynomial lists from l.
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C> >
           List<List<GenPolynomial<C>>> 
           sort( GenPolynomialRing<C> r,
                 List<List<GenPolynomial<C>>> l) {
        if ( l == null ) {
            return l;
        }
        if ( l.size() <= 1 ) { // nothing to sort
            return l;
        }
        final Comparator<ExpVector> evc = r.tord.getAscendComparator();
        Comparator<List<GenPolynomial<C>>> cmp 
              = new Comparator<List<GenPolynomial<C>>>() {
                public int compare(List<GenPolynomial<C>> l1, 
                                   List<GenPolynomial<C>> l2) {
                       int c = 0;
                       for ( int i = 0; i < l1.size(); i++ ) {
                           GenPolynomial<C> p1 = l1.get(i);
                           GenPolynomial<C> p2 = l2.get(i);
                           ExpVector e1 = p1.leadingExpVector();
                           ExpVector e2 = p2.leadingExpVector();
                           if ( e1 == e2 ) {
                               continue; 
                           }
                           if ( e1 == null && e2 != null ) {
                               return -1; 
                           }
                           if ( e1 != null && e2 == null ) {
                               return 1; 
                           }
                           if ( e1.length() != e2.length() ) {
                              if ( e1.length() > e2.length() ) {
                                 return 1; 
                              } else {
                                 return -1;
                              }
                           }
                           c = evc.compare(e1,e2);
                           if ( c != 0 ) {
                               return c;
                           }
                       }
                       return c;
                }
            };

        List<GenPolynomial<C>>[] s = null;
        try {
            s = (List<GenPolynomial<C>>[]) new List[ l.size() ]; 
            //System.out.println("s.length = " + s.length );
            //s = l.toArray(s); does not work
            //for ( int i = 0; i < l.size(); i++ ) {
            //    s[i] = l.get(i);
            //}
            int i = 0;
            for ( List<GenPolynomial<C>> p : l ) {
                s[i++] = p;
            }
            Arrays.<List<GenPolynomial<C>>>sort( s, cmp );
            return new ArrayList<List<GenPolynomial<C>>>( 
                            Arrays.<List<GenPolynomial<C>>>asList(s) );
        } catch(ClassCastException ok) {
            System.out.println("Warning: polynomials not sorted");
        }
        return l; // unsorted
    }

}
