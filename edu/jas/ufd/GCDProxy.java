/*
 * $Id: GCDProxy.java 3179 2010-06-09 22:55:48Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PreemptingException;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Greatest common divisor parallel proxy.
 * @author Heinz Kredel
 */

public class GCDProxy<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    //       implements GreatestCommonDivisor<C> {

    private static final Logger logger = Logger.getLogger(GCDProxy.class);


    private final boolean debug = logger.isDebugEnabled(); //logger.isInfoEnabled();


    /**
     * GCD engines.
     */
    public final GreatestCommonDivisorAbstract<C> e1;


    public final GreatestCommonDivisorAbstract<C> e2;


    /**
     * Thread pool.
     */
    protected ExecutorService pool;


    /**
     * Proxy constructor.
     */
    public GCDProxy(GreatestCommonDivisorAbstract<C> e1, GreatestCommonDivisorAbstract<C> e2) {
        this.e1 = e1;
        this.e2 = e2;
        if (pool == null) {
            pool = ComputerThreads.getPool();
            //System.out.println("pool 2 = "+pool);
        }
    }


    /**
     * Get the String representation with gcd engines.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GCDProxy[ " + e1.getClass().getName() + ", " + e2.getClass().getName() + " ]";
    }


    /**
     * Univariate GenPolynomial greatest comon divisor. Uses pseudoRemainder for
     * remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<C> baseGcd(final GenPolynomial<C> P, final GenPolynomial<C> S) {
        //throw new RuntimeException("baseGcd not implemented");
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenPolynomial<C> g = null;
        //Callable<GenPolynomial<C>> c0;
        //Callable<GenPolynomial<C>> c1;
        List<Callable<GenPolynomial<C>>> cs = new ArrayList<Callable<GenPolynomial<C>>>(2);
        cs.add(new Callable<GenPolynomial<C>>() {


            public GenPolynomial<C> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    GenPolynomial<C> g = e1.baseGcd(P, S);
                    if (debug) {
                        logger.info("GCDProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GCDProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GCDProxy e1 " + e);
                    logger.info("GCDProxy P = " + P);
                    logger.info("GCDProxy S = " + S);
                    throw new RuntimeException("GCDProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenPolynomial<C>>() {


            public GenPolynomial<C> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    GenPolynomial<C> g = e2.baseGcd(P, S);
                    if (debug) {
                        logger.info("GCDProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GCDProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GCDProxy e2 " + e);
                    logger.info("GCDProxy P = " + P);
                    logger.info("GCDProxy S = " + S);
                    throw new RuntimeException("GCDProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            g = pool.invokeAny(cs);
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        }
        return g;
    }


    /**
     * Univariate GenPolynomial recursive greatest comon divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<C>> recursiveUnivariateGcd(final GenPolynomial<GenPolynomial<C>> P,
            final GenPolynomial<GenPolynomial<C>> S) {
        // throw new RuntimeException("recursiveGcd not implemented");
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenPolynomial<GenPolynomial<C>> g = null;
        //Callable<GenPolynomial<GenPolynomial<C>>> c0;
        //Callable<GenPolynomial<GenPolynomial<C>>> c1;
        List<Callable<GenPolynomial<GenPolynomial<C>>>> cs = new ArrayList<Callable<GenPolynomial<GenPolynomial<C>>>>(
                2);
        cs.add(new Callable<GenPolynomial<GenPolynomial<C>>>() {


            public GenPolynomial<GenPolynomial<C>> call() {
                try {
                    GenPolynomial<GenPolynomial<C>> g = e1.recursiveUnivariateGcd(P, S);
                    if (debug) {
                        logger.info("GCDProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GCDProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GCDProxy e1 " + e);
                    logger.info("GCDProxy P = " + P);
                    logger.info("GCDProxy S = " + S);
                    throw new RuntimeException("GCDProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenPolynomial<GenPolynomial<C>>>() {


            public GenPolynomial<GenPolynomial<C>> call() {
                try {
                    GenPolynomial<GenPolynomial<C>> g = e2.recursiveUnivariateGcd(P, S);
                    if (debug) {
                        logger.info("GCDProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GCDProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GCDProxy e2 " + e);
                    logger.info("GCDProxy P = " + P);
                    logger.info("GCDProxy S = " + S);
                    throw new RuntimeException("GCDProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            g = pool.invokeAny(cs);
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        }
        return g;
    }


    /**
     * GenPolynomial greatest comon divisor. Main entry driver method.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<C> gcd(final GenPolynomial<C> P, final GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        // parallel case
        GenPolynomial<C> g = null;
        //Callable<GenPolynomial<C>> c0;
        //Callable<GenPolynomial<C>> c1;
        List<Callable<GenPolynomial<C>>> cs = new ArrayList<Callable<GenPolynomial<C>>>(2);
        cs.add(new Callable<GenPolynomial<C>>() {


            public GenPolynomial<C> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    GenPolynomial<C> g = e1.gcd(P, S);
                    if (debug) {
                        logger.info("GCDProxy done e1 " + e1.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GCDProxy e1 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GCDProxy e1 " + e);
                    logger.info("GCDProxy P = " + P);
                    logger.info("GCDProxy S = " + S);
                    throw new RuntimeException("GCDProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<GenPolynomial<C>>() {


            public GenPolynomial<C> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    GenPolynomial<C> g = e2.gcd(P, S);
                    if (debug) {
                        logger.info("GCDProxy done e2 " + e2.getClass().getName());
                    }
                    return g;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GCDProxy e2 pre " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GCDProxy e2 " + e);
                    logger.info("GCDProxy P = " + P);
                    logger.info("GCDProxy S = " + S);
                    throw new RuntimeException("GCDProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            g = pool.invokeAny(cs);
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        }
        return g;
    }

}
