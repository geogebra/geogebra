/*
 * $Id: CriticalPairList.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.RingElem;

/**
 * Critical pair list management.
 * Makes some effort to produce the same sequence of critical pairs 
 * as in the sequential case, when used in parallel.
 * However already reduced pairs are not rereduced if new
 * polynomials appear.
 * Implemented using GenPolynomial, TreeSet and BitSet.
 * @author Heinz Kredel
 */

public class CriticalPairList<C extends RingElem<C> > {

    private final GenPolynomialRing<C> ring;

    private final ArrayList<GenPolynomial<C>> P;

    private final SortedSet< CriticalPair<C> > pairlist;
    private final ArrayList<BitSet> red;

    private final Reduction<C> reduction;

    private boolean oneInGB = false;
    private boolean useCriterion4 = true;
    private int recordCount;
    private int putCount;
    private int remCount;
    private final int moduleVars;

    private static final Logger logger = Logger.getLogger(CriticalPairList.class);


    /**
     * Constructor for CriticalPairList.
     * @param r polynomial factory.
     */
    public CriticalPairList(GenPolynomialRing<C> r) {
        this(0,r);
    }


    /**
     * Constructor for CriticalPairList.
     * @param m number of module variables.
     * @param r polynomial factory.
     */
    public CriticalPairList(int m, GenPolynomialRing<C> r) {
        ring = r;
        if ( m < 0 || ring.nvar < m ) {
           throw new RuntimeException("moduleVars > nvars");
        }
        moduleVars = m;
        P = new ArrayList<GenPolynomial<C>>();
        Comparator< CriticalPair<C> > cpc; 
        cpc = new CriticalPairComparator<C>( ring.tord ); 
        pairlist = new TreeSet< CriticalPair<C> >( cpc );
        red = new ArrayList<BitSet>();
        recordCount = 0;
        putCount = 0;
        remCount = 0;
        if ( ring instanceof GenSolvablePolynomialRing ) {
           useCriterion4 = false;
        }
        reduction = new ReductionSeq<C>();
    }


    /**
     * Put a polynomial to the pairlist and reduction matrix.
     * @param p polynomial.
     * @return the index of the added polynomial.
     */
    public synchronized int put(GenPolynomial<C> p) { 
        putCount++;
        if ( oneInGB ) { 
           return P.size()-1;
        }
        CriticalPair<C> pair;
        ExpVector e = p.leadingExpVector(); 
        ExpVector f; 
        ExpVector g; 
        GenPolynomial<C> pj; 
        BitSet redi;
        int len = P.size();
        for ( int j = 0; j < len; j++ ) {
            pj = P.get(j);
            f = pj.leadingExpVector(); 
            if ( moduleVars > 0 ) { // test moduleCriterion
            if ( !reduction.moduleCriterion( moduleVars, e, f) ) {
                  continue; // skip pair
               }
            }
            g =  e.lcm( f );
            pair = new CriticalPair<C>( g, pj, p, j, len );
            //System.out.println("put pair = " + pair );
            pairlist.add( pair );
        }
        P.add( p );
        redi = new BitSet();
        redi.set( 0, len ); // >= jdk 1.4
        red.add( redi );
        if ( recordCount < len ) {
            recordCount = len;
        }
        return len;
    }


    /**
     * Put the ONE-Polynomial to the pairlist.
     * @return the index of the last polynomial.
     */
    public synchronized int putOne() { 
        putCount++;
        oneInGB = true;
        pairlist.clear();
        P.clear();
        P.add( ring.getONE() );
        red.clear();
        recordCount = 0;
        return 0;
    }


    /**
     * Get the next required pair from the pairlist.
     * Appy the criterions 3 and 4 to see if the S-polynomial is required.
     * The pair is not removed from the pair list.
     * @return the next pair if one exists, otherwise null.
     */
    public synchronized CriticalPair<C> getNext() { 
        if ( oneInGB ) {
           return null;
        }
        CriticalPair<C> pair = null;
        Iterator< CriticalPair<C> > ip = pairlist.iterator();
        boolean c = false;
        while ( !c & ip.hasNext() )  {
           pair = ip.next();
           if ( pair.getInReduction() ) {
               continue;
           }
           if ( pair.getReductum() != null ) {
               continue;
           }
           if ( logger.isInfoEnabled() ) {
              logger.info("" + pair);
           }
           if ( useCriterion4 ) {
              c = reduction.criterion4( pair.pi, pair.pj, pair.e ); 
              // System.out.println("c4  = " + c); 
           } else {
              c = true;
           }
           if ( c ) {
              c = criterion3( pair.i, pair.j, pair.e );
              // System.out.println("c3  = " + c); 
           }
           red.get( pair.j ).clear( pair.i ); // set(i,false) jdk1.4
           if ( ! c ) { // set done
               pair.setReductum( ring.getZERO() );
           }
        }
        if ( ! c ) {
           pair = null;
        } else {
           remCount++; // count only real pairs
           pair.setInReduction(); // set to work
        }
        return pair; 
    }


    /**
     * Record reduced polynomial.
     * @param pair the corresponding critical pair.
     * @param p polynomial.
     * @return index of recorded polynomial, or -1 if not added.
     */
    public int record(CriticalPair<C> pair, GenPolynomial<C> p) { 
        if ( p == null ) {
            p = ring.getZERO();
        }
        pair.setReductum(p);
        // trigger thread
        if ( ! p.isZERO() && ! p.isONE() ) {
           recordCount++;
           return recordCount;
        }
        return -1;
    }


    /**
     * Record reduced polynomial and update critical pair list. 
     * Note: it is better to use record and uptate separately.
     * @param pair the corresponding critical pair.
     * @param p polynomial.
     * @return index of recorded polynomial
     */
    public int update(CriticalPair<C> pair, GenPolynomial<C> p) { 
        if ( p == null ) {
            p = ring.getZERO();
        }
        pair.setReductum(p);
        if ( ! p.isZERO() && ! p.isONE() ) {
           recordCount++;
        }
        int c = update();
        if ( ! p.isZERO() && ! p.isONE() ) {
           return recordCount;
        } 
        return -1;
    }


    /**
     * Update pairlist.
     * Preserve the sequential pair sequence.
     * Remove pairs with completed reductions.
     * @return the number of added polynomials.
     */
    public synchronized int update() { 
        int num = 0;
        if ( oneInGB ) {
           return num;
        }
        while ( pairlist.size() > 0 ) {
            CriticalPair<C> pair = pairlist.first();
            GenPolynomial<C> p = pair.getReductum();
            if ( p != null ) {
               pairlist.remove( pair );
               num++;
               if ( ! p.isZERO() ) {
                  if ( p.isONE() ) {
                     putOne(); // sets size = 1
                  } else {
                     put( p ); // changes pair list
                  }
               }
            } else {
               break;
            }
        }
        return num;
    }


    /**
     * In work pairs. List pairs which are currently reduced.
     * @return list of critical pairs which are in reduction.
     */
    public synchronized List<CriticalPair<C>> inWork() { 
        List<CriticalPair<C>> iw;
        iw = new ArrayList<CriticalPair<C>>();
        if ( oneInGB ) {
            return iw;
        }
        for ( CriticalPair<C> pair : pairlist ) {
            if ( pair.getInReduction() ) {
               iw.add( pair );
            }
        }
        return iw;
    }


    /**
     * Update pairlist, several pairs at once. 
     * This version does not preserve the sequential pair sequence.
     * Remove pairs with completed reductions.
     * @return the number of added polynomials.
     */
    public synchronized int updateMany() { 
        int num = 0;
        if ( oneInGB ) {
           return num;
        }
        List<CriticalPair<C>> rem = new ArrayList<CriticalPair<C>>();
        for ( CriticalPair<C> pair : pairlist ) {
            if ( pair.getReductum() != null ) {
               rem.add( pair );
               num++;
            } else {
               break;
            }
        }
        // must work on a copy to avoid concurrent modification
        for ( CriticalPair<C> pair : rem ) {
            // System.out.println("update = " + pair); 
            pairlist.remove( pair );
            GenPolynomial<C> p = pair.getReductum();
            if ( ! p.isZERO() ) {
               if ( p.isONE() ) {
                  putOne();
               } else {
                  put( p );
               }
            }
        }
        return num;
    }


    /**
     * Test if there is possibly a pair in the list.
     * @return true if a next pair could exist, otherwise false.
     */
    public boolean hasNext() { 
          return pairlist.size() > 0;
    }


    /**
     * Get the list of polynomials.
     * @return the polynomial list.
     */
    public ArrayList<GenPolynomial<C>> getList() { 
          return P;
    }


    /**
     * Get the number of polynomials put to the pairlist.
     * @return the number of calls to put.
     */
    public int putCount() { 
          return putCount;
    }


    /**
     * Get the number of required pairs removed from the pairlist.
     * @return the number of non null pairs delivered.
     */
    public int remCount() { 
          return remCount;
    }


    /**
     * GB criterium 3.
     * @return true if the S-polynomial(i,j) is required.
     */
    public boolean criterion3(int i, int j, ExpVector eij) {  
        assert i < j;
        boolean s;
        s = red.get( j ).get( i ); 
        if ( ! s ) { 
           logger.warn("c3.s false for " + j + " " + i); 
           return s;
        }
        // now s == true;
        for ( int k = 0; k < P.size(); k++ ) {
            // System.out.println("i , k , j "+i+" "+k+" "+j); 
            if ( i != k && j != k ) {
               GenPolynomial<C> A = P.get( k );
               ExpVector ek = A.leadingExpVector();
               boolean m = eij.multipleOf(ek);
               if ( m ) {
                  if ( k < i ) {
                     s =    red.get( i ).get(k) 
                         || red.get( j ).get(k); 
                  } else if ( i < k && k < j ) {
                     s =    red.get( k ).get(i) 
                         || red.get( j ).get(k); 
                  } else if ( j < k ) {
                     s =    red.get( k ).get(i) 
                         || red.get( k ).get(j); 
                  }
                  //System.out.println("s."+k+" = " + s); 
                  if ( ! s ) return s;
               }
            }
        }
        return true;
    }
}

