/*
 * $Id: SolvableGroebnerBaseSeqPairParallel.java 3060 2010-04-02 21:39:51Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;

import edu.jas.util.Terminator;
import edu.jas.util.ThreadPool;


/**
 * Solvable Groebner Base parallel algorithm.
 * Makes some effort to produce the same sequence of critical pairs 
 * as in the sequential version.
 * However already reduced pairs are not rereduced if new
 * polynomials appear.
 * Implements a shared memory parallel version of Groebner bases.
 * Threads maintain pairlist.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableGroebnerBaseSeqPairParallel<C extends RingElem<C>>
             extends SolvableGroebnerBaseAbstract<C>  {

    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseSeqPairParallel.class);
    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Number of threads to use.
     */
    protected final int threads;


    /**
     * Pool of threads to use.
     */
    protected final ThreadPool pool;


    /**
     * Constructor.
     */
    public SolvableGroebnerBaseSeqPairParallel() {
        this(2);
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads) {
        this(threads, new ThreadPool(threads) );
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads, ThreadPool pool) {
        this(threads, pool, new SolvableReductionPar<C>() );
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param red parallelism aware reduction engine
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads, SolvableReduction<C> red) {
        this(threads, new ThreadPool(threads), red );
    }


    /**
     * Constructor.
     * @param threads number of threads to use.
     * @param pool ThreadPool to use.
     * @param sred parallelism aware reduction engine
     */
    public SolvableGroebnerBaseSeqPairParallel(int threads, ThreadPool pool, 
                                        SolvableReduction<C> sred) {
        super( new ReductionSeq<C>(), sred);
        if ( ! (sred instanceof SolvableReductionPar) ) {
           logger.warn("parallel GB should use parallel aware reduction");
        }
        if ( threads < 1 ) {
           threads = 1;
        }
        this.threads = threads;
        this.pool = pool;
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    public void terminate() {
        if ( pool == null ) {
           return;
        }
        pool.terminate();
    }


    /**
     * Parallel Groebner base using sequential pair order class.
     * Threads maintain pairlist.
     * @param modv number of module variables.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
             leftGB( int modv,
                     List<GenSolvablePolynomial<C>> F ) {  
        GenSolvablePolynomial<C> p;
        List<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>();
        CriticalPairList<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenSolvablePolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = (GenSolvablePolynomial<C>)p.monic();
               if ( p.isONE() ) {
                  G.clear(); G.add( p );
                  return G; // since no threads activated jet
               }
               G.add( p );
               if ( pairlist == null ) {
                  pairlist = new CriticalPairList<C>( modv, p.ring );
               }
               // putOne not required
               pairlist.put( p );
            } else {
               l--;
            }
        }
        if ( l <= 1 ) {
           return G; // since no threads activated jet
        }

        Terminator fin = new Terminator(threads);
        LeftSolvableReducerSeqPair<C> R;
        for ( int i = 0; i < threads; i++ ) {
            R = new LeftSolvableReducerSeqPair<C>( fin, G, pairlist );
            pool.addJob( R );
        }
        fin.waitDone();
        logger.debug("#parallel list = "+G.size());
        G = leftMinimalGB(G);
        // not in this context // pool.terminate();
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    //+ " #total = " + pairlist.pairCount()
                   );
        return G;
    }


    /**
     * Minimal ordered groebner basis, parallel.
     * @param Fp a Groebner base.
     * @return minimalGB(F) a minimal Groebner base of Fp.
     */
    @Override
     public List<GenSolvablePolynomial<C>> 
           leftMinimalGB(List<GenSolvablePolynomial<C>> Fp) {  
        GenSolvablePolynomial<C> a;
        ArrayList<GenSolvablePolynomial<C>> G;
        G = new ArrayList<GenSolvablePolynomial<C>>( Fp.size() );
        ListIterator<GenSolvablePolynomial<C>> it = Fp.listIterator();
        while ( it.hasNext() ) { 
            a = it.next();
            if ( a.length() != 0 ) { // always true
               // already monic  a = a.monic();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           return G;
        }

        ExpVector e;        
        ExpVector f;        
        GenSolvablePolynomial<C> p;
        ArrayList<GenSolvablePolynomial<C>> F;
        F = new ArrayList<GenSolvablePolynomial<C>>( G.size() );
        boolean mt;
        while ( G.size() > 0 ) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt =  e.multipleOf( f );
            }
            it = F.listIterator();
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt =  e.multipleOf( f );
            }
            if ( ! mt ) {
                F.add( a ); // no thread at this point
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }

        SolvableMiReducerSeqPair<C>[] mirs = (SolvableMiReducerSeqPair<C>[]) new SolvableMiReducerSeqPair[ G.size() ];
        int i = 0;
        F = new ArrayList<GenSolvablePolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            mirs[i] = new SolvableMiReducerSeqPair<C>( 
                                        (List<GenSolvablePolynomial<C>>)G.clone(), 
                                        (List<GenSolvablePolynomial<C>>)F.clone(), 
                                        a );
            pool.addJob( mirs[i] );
            i++;
            F.add( a );
        }
        G = F;
        F = new ArrayList<GenSolvablePolynomial<C>>( G.size() );
        for ( i = 0; i < mirs.length; i++ ) {
            a = mirs[i].getNF();
            F.add( a );
        }
        return F;
    }


    /**
     * Solvable Extended Groebner base using critical pair class.
     * @param modv module variable number.
     * @param F solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public SolvableExtendedGB<C> 
           extLeftGB( int modv, 
                      List<GenSolvablePolynomial<C>> F ) {
        throw new RuntimeException("parallel extLeftGB not implemented");
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, 
                      List<GenSolvablePolynomial<C>> Fp) {
        if ( Fp == null || Fp.size() == 0 ) { // 0 not 1
            return new ArrayList<GenSolvablePolynomial<C>>( );
        }
        GenSolvablePolynomialRing<C> fac = Fp.get(0).ring; // assert != null
        //List<GenSolvablePolynomial<C>> X = generateUnivar( modv, Fp );
        List<GenSolvablePolynomial<C>> X = fac.univariateList( modv );
        //System.out.println("X univ = " + X);
        List<GenSolvablePolynomial<C>> F 
            = new ArrayList<GenSolvablePolynomial<C>>( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        GenSolvablePolynomial<C> p, x, q;
        for ( int i = 0; i < Fp.size(); i++ ) {
            p = Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = X.get(j);
                q = p.multiply( x );
                q = sred.leftNormalform( F, q );
                if ( !q.isZERO() ) {
                   F.add( q );
                }
            }
        }
        //System.out.println("F generated = " + F);
        List<GenSolvablePolynomial<C>> G 
            = new ArrayList<GenSolvablePolynomial<C>>();
        CriticalPairList<C> pairlist = null; 
        int l = F.size();
        ListIterator<GenSolvablePolynomial<C>> it = F.listIterator();
        while ( it.hasNext() ) { 
            p = it.next();
            if ( p.length() > 0 ) {
               p = (GenSolvablePolynomial<C>)p.monic();
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
               l--;
            }
        }
        //System.out.println("G to check = " + G);
        if ( l <= 1 ) { // 1 ok
           return G; // since no threads are activated
        }
        Terminator fin = new Terminator(threads);
        TwosidedSolvableReducerSeqPair<C> R;
        for ( int i = 0; i < threads; i++ ) {
            R = new TwosidedSolvableReducerSeqPair<C>( fin, X, G, pairlist );
            pool.addJob( R );
        }
        fin.waitDone();
        logger.debug("#parallel list = "+G.size());
        G = leftMinimalGB(G);
        // not in this context // pool.terminate();
        logger.info("pairlist #put = " + pairlist.putCount() 
                  + " #rem = " + pairlist.remCount()
                    //+ " #total = " + pairlist.pairCount()
                   );
        return G;
    }

}


/**
 * Reducing left worker threads.
 * @param <C> coefficient type
 */
class LeftSolvableReducerSeqPair<C extends RingElem<C>> implements Runnable {
        private List<GenSolvablePolynomial<C>> G;
        private CriticalPairList<C> pairlist;
        private Terminator pool;
        private SolvableReductionPar<C> sred;
        private static final Logger logger = Logger.getLogger(LeftSolvableReducerSeqPair.class);
        private static final boolean debug = logger.isDebugEnabled();

        LeftSolvableReducerSeqPair(Terminator fin, 
                                   List<GenSolvablePolynomial<C>> G, 
                                   CriticalPairList<C> L) {
            pool = fin;
            this.G = G;
            pairlist = L;
            sred = new SolvableReductionPar<C>();
        } 


        public void run() {
           CriticalPair<C> pair;
           GenSolvablePolynomial<C> S;
           GenSolvablePolynomial<C> H;
           boolean set = false;
           int reduction = 0;
           int sleeps = 0;
           while ( pairlist.hasNext() || pool.hasJobs() ) {
              while ( ! pairlist.hasNext() ) {
                  pairlist.update();
                  // wait
                  pool.beIdle(); set = true;
                  try {
                      sleeps++;
                      if ( sleeps % 10 == 0 ) {
                         logger.info(" reducer is sleeping");
                      } else {
                         logger.debug("r");
                      }
                      Thread.sleep(100);
                  } catch (InterruptedException e) {
                     break;
                  }
                  if ( ! pool.hasJobs() ) {
                     break;
                  }
              }
              if ( ! pairlist.hasNext() && ! pool.hasJobs() ) {
                 break;
              }
              if ( set ) {
                 pool.notIdle();
              }
              pair = pairlist.getNext();
              if ( pair == null ) {
                 pairlist.update();
                 continue; 
              }
              if ( debug ) {
                 logger.debug("pi = " + pair.pi );
                 logger.debug("pj = " + pair.pj );
              }
              S = sred.leftSPolynomial( (GenSolvablePolynomial<C>)pair.pi, 
                                        (GenSolvablePolynomial<C>)pair.pj );
              if ( S.isZERO() ) {
                 pairlist.record( pair, S );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }
              H = sred.leftNormalform( G, S ); //mod
              reduction++;
              if ( H.isZERO() ) {
                 pairlist.record( pair, H );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }
              H = (GenSolvablePolynomial<C>)H.monic();
              // System.out.println("H   = " + H);
              if ( H.isONE() ) { 
                 // pairlist.update( pair, H );
                 pairlist.putOne(); // not really required
                 synchronized (G) {
                     G.clear(); G.add( H );
                 }
                 pool.allIdle();
                 return;
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              synchronized (G) {
                     G.add( H );
              }
              pairlist.update( pair, H );
              //pairlist.record( pair, H );
              //pairlist.update();
           }
           logger.info( "terminated, done " + reduction + " reductions");
        }
}


/**
 * Reducing twosided worker threads.
 * @param <C> coefficient type
 */
class TwosidedSolvableReducerSeqPair<C extends RingElem<C>> implements Runnable {
        private List<GenSolvablePolynomial<C>> X;
        private List<GenSolvablePolynomial<C>> G;
        private CriticalPairList<C> pairlist;
        private Terminator pool;
        private SolvableReductionPar<C> sred;
        private static final Logger logger = Logger.getLogger(TwosidedSolvableReducerSeqPair.class);
        private static final boolean debug = logger.isDebugEnabled();

        TwosidedSolvableReducerSeqPair(Terminator fin, 
                                       List<GenSolvablePolynomial<C>> X,
                                       List<GenSolvablePolynomial<C>> G, 
                                       CriticalPairList<C> L) {
            pool = fin;
            this.X = X;
            this.G = G;
            pairlist = L;
            sred = new SolvableReductionPar<C>();
        } 


        public void run() {
           GenSolvablePolynomial<C> p, x, q;
           CriticalPair<C> pair;
           GenSolvablePolynomial<C> S;
           GenSolvablePolynomial<C> H;
           boolean set = false;
           int reduction = 0;
           int sleeps = 0;
           while ( pairlist.hasNext() || pool.hasJobs() ) {
              while ( ! pairlist.hasNext() ) {
                  pairlist.update();
                  // wait
                  pool.beIdle(); set = true;
                  try {
                      sleeps++;
                      if ( sleeps % 10 == 0 ) {
                         logger.info(" reducer is sleeping");
                      } else {
                         logger.debug("r");
                      }
                      Thread.sleep(50);
                  } catch (InterruptedException e) {
                     break;
                  }
                  if ( ! pool.hasJobs() ) {
                     break;
                  }
              }
              if ( ! pairlist.hasNext() && ! pool.hasJobs() ) {
                 break;
              }
              if ( set ) {
                 pool.notIdle();
              }
              pair = pairlist.getNext();
              if ( pair == null ) {
                 pairlist.update();
                 continue; 
              }
              if ( debug ) {
                 logger.debug("pi = " + pair.pi );
                 logger.debug("pj = " + pair.pj );
              }
              S = sred.leftSPolynomial( (GenSolvablePolynomial<C>)pair.pi, 
                                        (GenSolvablePolynomial<C>)pair.pj );
              if ( S.isZERO() ) {
                 pairlist.record( pair, S );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(S) = " + S.leadingExpVector() );
              }
              H = sred.leftNormalform( G, S ); //mod
              reduction++;
              if ( H.isZERO() ) {
                 pairlist.record( pair, H );
                 continue;
              }
              if ( debug ) {
                 logger.debug("ht(H) = " + H.leadingExpVector() );
              }
              H = (GenSolvablePolynomial<C>)H.monic();
              // System.out.println("H   = " + H);
              if ( H.isONE() ) { 
                 // pairlist.update( pair, H );
                 pairlist.putOne(); // not really required
                 synchronized (G) {
                     G.clear(); G.add( H );
                 }
                 pool.allIdle();
                 return;
              }
              if ( debug ) {
                 logger.debug("H = " + H );
              }
              synchronized (G) {
                     G.add( H );
              }
              pairlist.update( pair, H );
              for ( int j = 0; j < X.size(); j++ ) {
                  x = X.get(j);
                  p = H.multiply( x );
                  p = sred.leftNormalform( G, p );
                  if ( !p.isZERO() ) {
                     p = (GenSolvablePolynomial<C>)p.monic();
                     if ( p.isONE() ) {
                        synchronized (G) {
                           G.clear(); G.add( p );
                        }
                        pool.allIdle();
                        return; 
                     }
                     synchronized (G) {
                        G.add( p );
                     }
                     pairlist.put( p );
                  }
              }
           }
           logger.info( "terminated, done " + reduction + " reductions");
        }
}


/**
 * Reducing worker threads for minimal GB.
 * @param <C> coefficient type
 */
class SolvableMiReducerSeqPair<C extends RingElem<C>> implements Runnable {
        private List<GenSolvablePolynomial<C>> G;
        private List<GenSolvablePolynomial<C>> F;
        private GenSolvablePolynomial<C> S;
        private GenSolvablePolynomial<C> H;
        private SolvableReductionPar<C> sred;
        private Semaphore done = new Semaphore(0);
        private static final Logger logger = Logger.getLogger(SolvableMiReducerSeqPair.class);
        private static final boolean debug = logger.isDebugEnabled();

        SolvableMiReducerSeqPair(List<GenSolvablePolynomial<C>> G, 
                                 List<GenSolvablePolynomial<C>> F, 
                                 GenSolvablePolynomial<C> p) {
            this.G = G;
            this.F = F;
            S = p;
            H = S;
            sred = new SolvableReductionPar<C>();
        } 


        /**
         * getNF. Blocks until the normal form is computed.
         * @return the computed normal form.
         */
        public GenSolvablePolynomial<C> getNF() {
            try { done.acquire(); //done.P();
            } catch (InterruptedException e) { 
            }
            return H;
        }

        public void run() {
            if ( debug ) {
               logger.debug("ht(S) = " + S.leadingExpVector() );
            }
            H = sred.leftNormalform( G, H ); //mod
            H = sred.leftNormalform( F, H ); //mod
            done.release(); //done.V();
            if ( debug ) {
               logger.debug("ht(H) = " + H.leadingExpVector() );
            }
            // H = H.monic();
        }

}

