/*
 * $Id: ReductionPar.java 2921 2009-12-25 17:06:56Z kredel $
 */

package edu.jas.gb;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;
import edu.jas.util.DistHashTable;


/**
 * Polynomial Reduction parallel usable algorithm.
 * Implements normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ReductionPar<C extends RingElem<C>>
             extends ReductionAbstract<C> {

    //private static final Logger logger = Logger.getLogger(ReductionPar.class);


    /**
     * Constructor.
     */
    public ReductionPar() {
    }


    /**
     * Normalform. Allows concurrent modification of the list.
     * @param Ap polynomial.
     * @param Pp polynomial list, concurrent modification allowed.
     * @return nf(Ap) with respect to Pp.
     */
    public GenPolynomial<C> 
           normalform(List<GenPolynomial<C>> Pp, 
                      GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) { // required, ok in dist
           l = Pp.size();
           P = (GenPolynomial<C>[]) new GenPolynomial[l];
           //P = Pp.values().toArray();
           for ( int i = 0; i < Pp.size(); i++ ) {
               P[i] = Pp.get(i);
           }
        }

        Map.Entry<ExpVector,C> m;
        Map.Entry<ExpVector,C> m1;
        ExpVector e;
        ExpVector f = null;
        C a;
        boolean mt = false;
        GenPolynomial<C> Rz = Ap.ring.getZERO();
        GenPolynomial<C> R = Rz;
        GenPolynomial<C> p = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
              if ( Pp.size() != l ) { 
                 //long t = System.currentTimeMillis();
                 synchronized (Pp) { // required, bad in parallel
                    l = Pp.size();
                    P = (GenPolynomial<C>[]) new GenPolynomial[ l ];
                    //P = Pp.toArray();
                    for ( int i = 0; i < Pp.size(); i++ ) {
                        P[i] = Pp.get(i);
                    }
                 }
                 //t = System.currentTimeMillis()-t;
                 //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                 S = Ap; // S.add(R)? // restart reduction ?
                 R = Rz; 
              }
              m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              for ( int i = 0; i < P.length ; i++ ) {
                  p = P[i];
                  f = p.leadingExpVector();
                  if ( f != null ) {
                     mt =  e.multipleOf( f );
                     if ( mt ) break; 
                  }
              }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = R.sum( a, e );
                 S = S.subtract( a, e ); 
                 // System.out.println(" S = " + S);
              } else { 
                 //logger.debug("red");
                 m1 = p.leadingMonomial();
                 e =  e.subtract( f );
                 a = a.divide( m1.getValue() );
                 Q = p.multiply( a, e );
                 S = S.subtract( Q );
              }
        }
        return R;
    }


    /**
     * Normalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    public GenPolynomial<C> 
           normalform(List<GenPolynomial<C>> row,
                      List<GenPolynomial<C>> Pp, 
                      GenPolynomial<C> Ap) {  
        throw new RuntimeException("normalform with recording not implemented");
    }


    /**
     * Normalform. Allows concurrent modification of the DHT.
     * @param Ap polynomial.
     * @param Pp distributed hash table, concurrent modification allowed.
     * @return nf(Ap) with respect to Pp.
     */
    public GenPolynomial<C> 
           normalform(DistHashTable Pp, 
                      GenPolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized ( Pp.getList() ) { // required, ok in dist
           l = Pp.size();
           P = (GenPolynomial<C>[]) new GenPolynomial[l];
           //P = Pp.values().toArray();
           Collection<GenPolynomial<C>> Pv 
               = (Collection<GenPolynomial<C>>)Pp.values();
           int i = 0;
           for ( GenPolynomial<C> x : Pv ) {
               P[i++] = x;
           }
        }

        Map.Entry<ExpVector,C> m;
        Map.Entry<ExpVector,C> m1;
        ExpVector e;
        ExpVector f = null;
        C a;
        boolean mt = false;
        GenPolynomial<C> Rz = Ap.ring.getZERO();
        GenPolynomial<C> R = Rz;
        GenPolynomial<C> p = null;
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
              if ( Pp.size() != l ) { 
                 //long t = System.currentTimeMillis();
                 synchronized ( Pp.getList() ) { // required, ok in distributed
                    l = Pp.size();
                    P = (GenPolynomial<C>[]) new GenPolynomial[ l ];
                    //P = Pp.values().toArray();
                    Collection<GenPolynomial<C>> Pv 
                        = (Collection<GenPolynomial<C>>)Pp.values();
                    int i = 0;
                    for ( GenPolynomial<C> x : Pv ) {
                        P[i++] = x;
                    }
                 }
                 //t = System.currentTimeMillis()-t;
                 //logger.info("Pp.toArray() = " + t + " ms, size() = " + l);
                 //logger.info("Pp.toArray() size() = " + l);
                 S = Ap; // S.add(R)? // restart reduction ?
                 R = Rz; 
              }

              m = S.leadingMonomial();
              e = m.getKey();
              a = m.getValue();
              for ( int i = 0; i < P.length ; i++ ) {
                  p = P[i];
                  f = p.leadingExpVector();
                  if ( f != null ) {
                     mt =  e.multipleOf( f );
                     if ( mt ) break; 
                  }
              }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = R.sum( a, e );
                 S = S.subtract( a, e ); 
                 // System.out.println(" S = " + S);
              } else { 
                 //logger.debug("red");
                 m1 = p.leadingMonomial();
                 e =  e.subtract( f );
                 a = a.divide( m1.getValue() );
                 Q = p.multiply( a, e );
                 S = S.subtract( Q );
              }
        }
        return R;
    }

}
