/*
 * $Id: GBProxy.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.gb;


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
 * Groebner bases parallel proxy.
 * @author Heinz Kredel
 */

public class GBProxy<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GBProxy.class);


    private final boolean debug = logger.isDebugEnabled(); //logger.isInfoEnabled();


    /**
     * GB engines.
     */
    public final GroebnerBaseAbstract<C> e1;


    public final GroebnerBaseAbstract<C> e2;


    /**
     * Thread pool.
     */
    protected ExecutorService pool;


    /**
     * Proxy constructor.
     */
    public GBProxy(GroebnerBaseAbstract<C> e1, GroebnerBaseAbstract<C> e2) {
        this.e1 = e1;
        this.e2 = e2;
        if (pool == null) {
            pool = ComputerThreads.getPool();
            //System.out.println("pool 2 = "+pool);
        }
    }


    /**
     * Get the String representation with GB engines.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GBProxy[ " + e1.getClass().getName() + ", " + e2.getClass().getName() + " ]";
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    public void terminate() {
        e1.terminate();
        e2.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    public int cancel() {
        int s = e1.cancel();
        s += e2.cancel();
        return s;
    }


    /**
     * Groebner base.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    //JAVA6only: @Override
    public List<GenPolynomial<C>> GB( final int modv, final List<GenPolynomial<C>> F ) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        // parallel case
        List<GenPolynomial<C>> G = null;
        List<Callable<List<GenPolynomial<C>>>> cs = new ArrayList<Callable<List<GenPolynomial<C>>>>(2);
        cs.add(new Callable<List<GenPolynomial<C>>>() {
            public List<GenPolynomial<C>> call() {
                try {
                    //System.out.println("starting e1 " + e1.getClass().getName());
                    List<GenPolynomial<C>> G = e1.GB(modv,F);
                    if (debug) {
                        logger.info("GBProxy done e1 " + e1.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GBProxy e1 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GBProxy e1 " + e);
                    logger.info("Exception GBProxy F = " + F);
                    throw new RuntimeException("GBProxy e1 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        cs.add(new Callable<List<GenPolynomial<C>>>() {
            public List<GenPolynomial<C>> call() {
                try {
                    //System.out.println("starting e2 " + e2.getClass().getName());
                    List<GenPolynomial<C>> G = e2.GB(modv,F);
                    if (debug) {
                        logger.info("GBProxy done e2 " + e2.getClass().getName());
                    }
                    return G;
                } catch (PreemptingException e) {
                    throw new RuntimeException("GBProxy e2 preempted " + e);
                    //return P.ring.getONE();
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.info("GBProxy e2 " + e);
                    logger.info("Exception GBProxy F = " + F);
                    throw new RuntimeException("GBProxy e2 " + e);
                    //return P.ring.getONE();
                }
            }
        });
        try {
            G = pool.invokeAny(cs);
        } catch (InterruptedException ignored) {
            logger.info("InterruptedException " + ignored);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.info("ExecutionException " + e);
            Thread.currentThread().interrupt();
        }
        return G;
    }

}
