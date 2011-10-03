/*
 * $Id: DGroebnerBaseSeq.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.gb.OrderedDPairlist;
import edu.jas.poly.GenPolynomial;


/**
 * D-Groebner Base sequential algorithm.
 * Implements D-Groebner bases and GB test.
 * <b>Note:</b> Minimal reduced GBs are not unique.
 * see BWK, section 10.1.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class DGroebnerBaseSeq<C extends RingElem<C>> 
       extends GroebnerBaseAbstract<C>  {


    private static final Logger logger = Logger.getLogger(DGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();



    /**
     * Reduction engine.
     */
    protected DReduction<C> red;  // shadow super.red ??


    /**
     * Constructor.
     */
    public DGroebnerBaseSeq() {
        this( new DReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red D-Reduction engine
     */
    public DGroebnerBaseSeq(DReduction<C> red) {
        super(red);
        this.red = red;
    }


    /**
     * D-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a D-Groebner base, else false.
     */
    @Override
     public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        GenPolynomial<C> pi, pj, s, d;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                d = red.GPolynomial( pi, pj );
                if ( ! d.isZERO() ) {
                   // better check for top reduction only
                   d = red.normalform( F, d );
                }
                if ( ! d.isZERO() ) {
                   System.out.println("d-pol("+i+","+j+") != 0: " + d);
                   return false;
                }
                // works ok
                if ( ! red.criterion4( pi, pj ) ) { 
                   continue;
                }
                s = red.SPolynomial( pi, pj );
                if ( ! s.isZERO() ) {
                   s = red.normalform( F, s );
                }
                if ( ! s.isZERO() ) {
                   System.out.println("s-pol("+i+","+j+") != 0: " + s);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * D-Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a D-Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( int modv, 
                 List<GenPolynomial<C>> F ) {  
        //throw new RuntimeException("not jet implemented");
        GenPolynomial<C> p;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedDPairlist<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenPolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( !p.isZERO() ) {
               p = p.abs(); // not monic
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads are activated
               }
               G.add( p ); //G.add( 0, p ); //reverse list
               if ( pairlist == null ) {
                  pairlist = new OrderedDPairlist<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else { 
               l--;
            }
        }
        if ( l <= 1 ) {
           return G; // since no threads are activated
        }

        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        GenPolynomial<C> D;
        GenPolynomial<C> H;
        //int len = G.size();
        //System.out.println("len = " + len);
        while ( pairlist.hasNext() ) {
              pair = pairlist.removeNext();
              //System.out.println("pair = " + pair);
              if ( pair == null ) continue; 

              pi = pair.pi; 
              pj = pair.pj; 
              if ( false && logger.isDebugEnabled() ) {
                 logger.debug("pi    = " + pi );
                 logger.debug("pj    = " + pj );
              }

              // D-polynomial case ----------------------
              D = red.GPolynomial( pi, pj );
              //System.out.println("D_d = " + D);
              if ( !D.isZERO() && !red.isTopReducible(G,D) ) {
                  H = red.normalform( G, D );
                  if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                  }
                  if ( !H.isZERO() ) {
                      logger.info("Dred = " + H);
                      l++;
                      G.add( H );
                      pairlist.put( H );
                  }
              }

              // S-polynomial case -----------------------
              if ( pair.getUseCriterion3() && pair.getUseCriterion4() ) {
                  S = red.SPolynomial( pi, pj );
                  //System.out.println("S_d = " + S);
                  if ( S.isZERO() ) {
                      pair.setZero();
                      continue;
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("ht(S) = " + S.leadingExpVector() );
                  }

                  H = red.normalform( G, S );
                  if ( H.isZERO() ) {
                      pair.setZero();
                      continue;
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("ht(H) = " + H.leadingExpVector() );
                  }

                  if ( H.isONE() ) {
                      G.clear(); G.add( H );
                      return G; // since no threads are activated
                  }
                  if ( logger.isDebugEnabled() ) {
                      logger.debug("H = " + H );
                  }
                  if ( !H.isZERO() ) {
                      logger.info("Sred = " + H);
                      //len = G.size();
                      l++;
                      G.add( H );
                      pairlist.put( H );
                  }
              }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    // + " #total = " + pairlist.pairCount()
                   );
        return G;
    }

}
