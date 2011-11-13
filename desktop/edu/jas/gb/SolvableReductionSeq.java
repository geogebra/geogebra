/*
 * $Id: SolvableReductionSeq.java 2922 2009-12-25 17:26:22Z kredel $
 */

package edu.jas.gb;

import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;


/**
 * Solvable polynomial Reduction algorithm.
 * Implements left, right normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableReductionSeq<C extends RingElem<C>>
             extends SolvableReductionAbstract<C> {

    //private static final Logger logger = Logger.getLogger(SolvableReductionSeq.class);


    /**
     * Constructor.
     */
    public SolvableReductionSeq() {
    }


    /**
     * Left Normalform.
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return left-nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
           return Ap;
        }
        int l;
        Map.Entry<ExpVector,C> m;
        GenSolvablePolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
            //P = Pp.toArray();
            for ( int j = 0; j < Pp.size(); j++ ) {
                P[j] = Pp.get(j);
            }
        }
        int i;
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenSolvablePolynomial<C>[] p = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[ l ];
        int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if ( m != null ) { 
               p[j] = p[i];
               htl[j] = m.getKey();
               lbc[j] = m.getValue();
               j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO();

        //GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
              m = S.leadingMonomial();
              e = m.getKey();
              //logger.info("red = " + e);
              a = m.getValue();
              for ( i = 0; i < l; i++ ) {
                  mt =  e.multipleOf( htl[i] );
                  if ( mt ) break; 
              }
              if ( ! mt ) { 
                 //logger.debug("irred");
                 //T = new OrderedMapPolynomial( a, e );
                 R = (GenSolvablePolynomial<C>)R.sum( a, e );
                 S = (GenSolvablePolynomial<C>)S.subtract( a, e ); 
                 // System.out.println(" S = " + S);
              } else { 
                 //logger.debug("red");
                 e =  e.subtract( htl[i] );
                 //a = a.divide( (C)lbc[i] );
                 Q = p[i].multiplyLeft( e );
                 a = a.divide( Q.leadingBaseCoefficient() );
                 Q = Q.multiplyLeft( a );
                 S = (GenSolvablePolynomial<C>)S.subtract( Q );
              }
        }
        return R;
    }


    /**
     * LeftNormalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the left normal form of Ap wrt. Pp.
     */
    public GenSolvablePolynomial<C> 
        leftNormalform(List<GenSolvablePolynomial<C>> row,
                       List<GenSolvablePolynomial<C>> Pp, 
                       GenSolvablePolynomial<C> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
            return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
            return Ap;
        }
        int l = Pp.size();
        GenSolvablePolynomial<C>[] P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[ l ];
        synchronized (Pp) {
            //P = Pp.toArray();
            for ( int i = 0; i < Pp.size(); i++ ) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenSolvablePolynomial<C>[] p = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[ l ];
        Map.Entry<ExpVector,C> m;
        int j = 0;
        int i;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if ( m != null ) { 
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> zero = Ap.ring.getZERO();
        GenSolvablePolynomial<C> R = Ap.ring.getZERO();

        GenSolvablePolynomial<C> fac = null;
        // GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for ( i = 0; i < l; i++ ) {
                mt =  e.multipleOf( htl[i] );
                if ( mt ) break; 
            }
            if ( ! mt ) { 
                //logger.debug("irred");
                R = (GenSolvablePolynomial<C>)R.sum( a, e );
                S = (GenSolvablePolynomial<C>)S.subtract( a, e ); 
                // System.out.println(" S = " + S);
                // throw new RuntimeException("Syzygy no leftGB");
            } else { 
                e =  e.subtract( htl[i] );
                //logger.info("red div = " + e);
                //a = a.divide( (C)lbc[i] );
                //Q = p[i].multiplyLeft( a, e );
                Q = p[i].multiplyLeft( e );
                a = a.divide( Q.leadingBaseCoefficient() );
                Q = Q.multiply( a );
                S = (GenSolvablePolynomial<C>)S.subtract( Q );
                fac = row.get(i);
                if ( fac == null ) {
                    fac = (GenSolvablePolynomial<C>)zero.sum( a, e );
                } else {
                    fac = (GenSolvablePolynomial<C>)fac.sum( a, e );
                }
                row.set(i,fac);
            }
        }
        return R;
    }


    /**
     * Right Normalform.
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return right-nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<C> 
        rightNormalform(List<GenSolvablePolynomial<C>> Pp, 
                        GenSolvablePolynomial<C> Ap) {
        if ( Pp == null || Pp.isEmpty() ) {
            return Ap;
        }
        if ( Ap == null || Ap.isZERO() ) {
            return Ap;
        }
        int l;
        Map.Entry<ExpVector,C> m;
        GenSolvablePolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[l];
            //P = Pp.toArray();
            for ( int j = 0; j < Pp.size(); j++ ) {
                P[j] = Pp.get(j);
            }
        }
        int i;
        ExpVector[] htl = new ExpVector[ l ];
        Object[] lbc = new Object[ l ]; // want <C>
        GenSolvablePolynomial<C>[] p = (GenSolvablePolynomial<C>[]) new GenSolvablePolynomial[ l ];
        int j = 0;
        for ( i = 0; i < l; i++ ) { 
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if ( m != null ) { 
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenSolvablePolynomial<C> R = Ap.ring.getZERO();

        //GenSolvablePolynomial<C> T = null;
        GenSolvablePolynomial<C> Q = null;
        GenSolvablePolynomial<C> S = Ap;
        while ( S.length() > 0 ) { 
            m = S.leadingMonomial();
            e = m.getKey();
            //logger.info("red = " + e);
            a = m.getValue();
            for ( i = 0; i < l; i++ ) {
                mt =  e.multipleOf( htl[i] );
                if ( mt ) break; 
            }
            if ( ! mt ) { 
                //logger.debug("irred");
                //T = new OrderedMapPolynomial( a, e );
                R = (GenSolvablePolynomial<C>)R.sum( a, e );
                S = (GenSolvablePolynomial<C>)S.subtract( a, e ); 
                // System.out.println(" S = " + S);
            } else { 
                //logger.debug("red");
                e =  e.subtract( htl[i] );
                //a = a.divide( (C)lbc[i] );
                Q = p[i].multiply( e );
                a = a.divide( Q.leadingBaseCoefficient() );
                Q = Q.multiply( a );
                S = (GenSolvablePolynomial<C>)S.subtract( Q );
            }
        }
        return R;
    }

}
