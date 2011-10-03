/*
 * $Id: GroebnerBaseSeqPairSeq.java 3189 2010-06-26 20:10:32Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Groebner Base sequential algorithm.
 * Implements Groebner bases and GB test.
 * Uses sequential pair list class.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GroebnerBaseSeqPairSeq<C extends RingElem<C>> 
       extends GroebnerBaseAbstract<C>  {

    private static final Logger logger = Logger.getLogger(GroebnerBaseSeqPairSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public GroebnerBaseSeqPairSeq() {
        super();
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBaseSeqPairSeq(Reduction<C> red) {
        super(red);
    }


    /**
     * Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        CriticalPairList<C> pairlist = null; 
        int len = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = p.monic();
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p );
               if ( pairlist == null ) {
                  pairlist = new CriticalPairList<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               len--;
            }
        }
        if ( len <= 1 ) {
           return G; // since no threads are activated
        }

        CriticalPair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> H;
        while ( pairlist.hasNext() ) {
              pair = pairlist.getNext();
              if ( pair == null ) { 
                 pairlist.update(); // ?
                 continue; 
              }
              pi = pair.pi; 
              pj = pair.pj; 
              if ( debug ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              S = red.SPolynomial( pi, pj );
              if ( S.isZERO() ) {
                 pairlist.update( pair, S );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              H = red.normalform( G, S );
              if ( H.isZERO() ) {
                 pairlist.update( pair, H );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }

              H = H.monic();
              if ( H.isONE() ) {
                  // pairlist.record( pair, H );
                 G.clear(); G.add( H );
                 return G; // since no threads are activated
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              G.add( H );
              pairlist.update( pair, H );
              //pairlist.update();
        }
        logger.debug("#sequential list = "+G.size());
        G = minimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }


    /**
     * Extended Groebner base using critical pair class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return a container for an extended Groebner base of F.
     */
    @Override
     public ExtendedGB<C> 
             extGB( int modv, 
                    List<GenPolynomial<C>> F ) {  
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        List<List<GenPolynomial<C>>> F2G = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> G2F = new ArrayList<List<GenPolynomial<C>>>();
        CriticalPairList<C> pairlist = null; 
        boolean oneInGB = false;
        int len = F.size();

        List<GenPolynomial<C>> row = null;
        List<GenPolynomial<C>> rows = null;
        List<GenPolynomial<C>> rowh = null;
        GenPolynomialRing<C> ring = null;
        GenPolynomial<C> H;
        GenPolynomial<C> p;

        int nzlen = 0;
        for ( GenPolynomial<C> f : F ) { 
            if ( f.length() > 0 ) {
                nzlen++;
            }
            if ( ring == null ) {
               ring = f.ring;
            }
        }
        GenPolynomial<C> mone = ring.getONE(); //.negate();
        int k = 0;
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               row = new ArrayList<GenPolynomial<C>>( nzlen );
               for ( int j = 0; j < nzlen; j++ ) {
                   row.add(null);
               }
               //C c = p.leadingBaseCoefficient();
               //c = c.inverse();
               //p = p.multiply( c );
               row.set( k, mone ); //.multiply(c) );
               k++;
               if ( p.isUnit() ) {
                  G.clear(); G.add( p );
                  G2F.clear(); G2F.add( row );
                  oneInGB = true;
                  break;
               }
               G.add( p );
               G2F.add( row );
               if ( pairlist == null ) {
                  pairlist = new CriticalPairList<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               len--;
            }
        }
        ExtendedGB<C> exgb;
        if ( len <= 1 || oneInGB ) {
           // adjust F2G
           for ( GenPolynomial<C> f : F ) {
               row = new ArrayList<GenPolynomial<C>>( G.size() );
               for ( int j = 0; j < G.size(); j++ ) {
                   row.add(null);
               }
               H = red.normalform( row, G, f );
               if ( ! H.isZERO() ) {
                  logger.error("nonzero H = " + H );
               }
               F2G.add( row );
           }
           exgb = new ExtendedGB<C>(F,G,F2G,G2F);
           //System.out.println("exgb 1 = " + exgb);
           return exgb;
        }

        CriticalPair<C> pair;
        int i, j;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> x;
        GenPolynomial<C> y;
        //GenPolynomial<C> z;
        while ( pairlist.hasNext() && ! oneInGB ) {
              pair = pairlist.getNext();
              if ( pair == null ) { 
                 pairlist.update(); // ?
                 continue; 
              }
              i = pair.i; 
              j = pair.j; 
              pi = pair.pi; 
              pj = pair.pj; 
              if ( debug ) {
                 logger.info("i, pi    = " + i + ", " + pi );
                 logger.info("j, pj    = " + j + ", " + pj );
              }

              rows = new ArrayList<GenPolynomial<C>>( G.size() );
              for ( int m = 0; m < G.size(); m++ ) {
                  rows.add(null);
              }
              S = red.SPolynomial( rows, i, pi, j, pj );
              if ( debug ) {
                 logger.debug("is reduction S = " 
                             + red.isReductionNF( rows, G, ring.getZERO(), S ) );
              }
              if ( S.isZERO() ) {
                 pairlist.update( pair, S );
                 // do not add to G2F
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }

              rowh = new ArrayList<GenPolynomial<C>>( G.size() );
              for ( int m = 0; m < G.size(); m++ ) {
                  rowh.add(null);
              }
              H = red.normalform( rowh, G, S );
              if ( debug ) {
                 logger.debug("is reduction H = " 
                              + red.isReductionNF( rowh, G, S, H ) );
              }
              if ( H.isZERO() ) {
                 pairlist.update( pair, H );
                 // do not add to G2F
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }

              row = new ArrayList<GenPolynomial<C>>( G.size()+1 );
              for ( int m = 0; m < G.size(); m++ ) {
                  x = rows.get(m);
                  if ( x != null ) {
                     //System.out.println("ms = " + m + " " + x);
                     x = x.negate();
                  }
                  y = rowh.get(m);
                  if ( y != null ) {
                     y = y.negate();
                     //System.out.println("mh = " + m + " " + y);
                  }
                  if ( x == null ) {
                     x = y;
                  } else {
                     x = x.sum( y );
                  }
                  //System.out.println("mx = " + m + " " + x);
                  row.add( x );
              }
              if ( debug ) {
                 logger.debug("is reduction 0+sum(row,G) == H : " 
                             + red.isReductionNF( row, G, H, ring.getZERO() ) );
              }
              row.add( null );


              //  H = H.monic();
              C c = H.leadingBaseCoefficient();
              c = c.inverse();
              H = H.multiply( c );
              row = blas.scalarProduct( mone.multiply(c), row );
              row.set( G.size(), mone );
              if ( H.isONE() ) {
                 // pairlist.record( pair, H );
                 // G.clear(); 
                 G.add( H );
                 G2F.add( row );
                 oneInGB = true;
                 break; 
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              G.add( H );
              pairlist.update( pair, H );
              G2F.add( row );
        }
        if ( debug ) {
           exgb = new ExtendedGB<C>(F,G,F2G,G2F);
           logger.info("exgb unnorm = " + exgb);
        }
        G2F = normalizeMatrix( F.size(), G2F );
        if ( debug ) {
           exgb = new ExtendedGB<C>(F,G,F2G,G2F);
           logger.info("exgb nonmin = " + exgb);
           boolean t2 = isReductionMatrix( exgb );
           logger.info("exgb t2 = " + t2);
        }
        exgb = minimalExtendedGB(F.size(),G,G2F);
        G = exgb.G;
        G2F = exgb.G2F;
        logger.debug("#sequential list = " + G.size());
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        // setup matrices F and F2G
        for ( GenPolynomial<C> f : F ) {
            row = new ArrayList<GenPolynomial<C>>( G.size() );
            for ( int m = 0; m < G.size(); m++ ) {
                row.add(null);
            }
            H = red.normalform( row, G, f );
            if ( ! H.isZERO() ) {
               logger.error("nonzero H = " + H );
            }
            F2G.add( row );
        }
        exgb = new ExtendedGB<C>(F,G,F2G,G2F);
        if ( debug ) {
           logger.info("exgb nonmin = " + exgb);
           boolean t2 = isReductionMatrix( exgb );
           logger.info("exgb t2 = " + t2);
        }
        return exgb;
    }

}






