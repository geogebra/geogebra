/*
 * $Id: GCDFactory.java 3159 2010-05-29 18:42:56Z kredel $
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.arith.Modular;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModularRingFactory;
import edu.jas.kern.ComputerThreads;


/**
 * Greatest common divisor algorithms factory. Select appropriate GCD engine
 * based on the coefficient types.
 * @todo Base decision also an degree vectors and number of variables of
 *       polynomials. Incorporate also number of CPUs / threads available (done
 *       with GCDProxy).
 * @author Heinz Kredel
 * @usage To create objects that implement the
 *        <code>GreatestCommonDivisor</code> interface use the
 *        <code>GCDFactory</code>. It will select an appropriate
 *        implementation based on the types of polynomial coefficients C. There
 *        are two methods to obtain an implementation: <code>getProxy()</code>
 *        and <code>getImplementation()</code>.
 *        <code>getImplementation()</code> returns an object of a class which
 *        implements the <code>GreatestCommonDivisor</code> interface.
 *        <code>getProxy()</code> returns a proxy object of a class which
 *        implements the <code>GreatestCommonDivisor</code>r interface. The
 *        proxy will run two implementations in parallel, return the first
 *        computed result and cancel the second running task. On systems with
 *        one CPU the computing time will be two times the time of the fastest
 *        algorithm implmentation. On systems with more than two CPUs the
 *        computing time will be the time of the fastest algorithm
 *        implmentation.
 * 
 * <pre>
 * GreatestCommonDivisor&lt;CT&gt; engine;
 * engine = GCDFactory.&lt;CT&gt; getImplementation(cofac);
 * or engine = GCDFactory.&lt;CT&gt; getProxy(cofac);
 * c = engine.gcd(a, b);
 * </pre>
 * 
 * For example, if the coefficient type is BigInteger, the usage looks
 *        like
 * 
 * <pre>
 * BigInteger cofac = new BigInteger();
 * GreatestCommonDivisor&lt;BigInteger&gt; engine;
 * engine = GCDFactory.getImplementation(cofac);
 * or engine = GCDFactory.getProxy(cofac);
 * c = engine.gcd(a, b);
 * </pre>
 * 
 * @see edu.jas.ufd.GreatestCommonDivisor#gcd( edu.jas.poly.GenPolynomial P,
 *      edu.jas.poly.GenPolynomial S)
 */

public class GCDFactory {


    private static final Logger logger = Logger.getLogger(GCDFactory.class);


    /**
     * Protected factory constructor.
     */
    protected GCDFactory() {
    }


    /**
     * Determine suitable implementation of gcd algorithms, case ModLong.
     * @param fac ModLongRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModLong> getImplementation(ModLongRing fac) {
        GreatestCommonDivisorAbstract<ModLong> ufd;
        if (fac.isField()) {
            ufd = new GreatestCommonDivisorModEval<ModLong>();
            return ufd;
        }
        ufd = new GreatestCommonDivisorSubres<ModLong>();
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case ModLong.
     * @param fac ModLongRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModLong> getProxy(ModLongRing fac) {
        GreatestCommonDivisorAbstract<ModLong> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorSubres<ModLong>();
        if (fac.isField()) {
            ufd2 = new GreatestCommonDivisorModEval<ModLong>();
        } else {
            ufd2 = new GreatestCommonDivisorSimple<ModLong>();
        }
        return new GCDProxy<ModLong>(ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case ModInteger.
     * @param fac ModIntegerRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModInteger> getImplementation(ModIntegerRing fac) {
        GreatestCommonDivisorAbstract<ModInteger> ufd;
        if (fac.isField()) {
            ufd = new GreatestCommonDivisorModEval<ModInteger>();
            return ufd;
        }
        ufd = new GreatestCommonDivisorSubres<ModInteger>();
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case ModInteger.
     * @param fac ModIntegerRing.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<ModInteger> getProxy(ModIntegerRing fac) {
        GreatestCommonDivisorAbstract<ModInteger> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorSubres<ModInteger>();
        if (fac.isField()) {
            ufd2 = new GreatestCommonDivisorModEval<ModInteger>();
        } else {
            ufd2 = new GreatestCommonDivisorSimple<ModInteger>();
        }
        return new GCDProxy<ModInteger>(ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case BigInteger.
     * @param fac BigInteger.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<BigInteger> getImplementation(BigInteger fac) {
        GreatestCommonDivisorAbstract<BigInteger> ufd;
        if (true) {
            ufd = new GreatestCommonDivisorModular<ModLong>(); // dummy type
            return ufd;
        }
        ufd = new GreatestCommonDivisorSubres<BigInteger>();
        return ufd;
    }


    /**
     * Determine suitable procy for gcd algorithms, case BigInteger.
     * @param fac BigInteger.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<BigInteger> getProxy(BigInteger fac) {
        GreatestCommonDivisorAbstract<BigInteger> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorSubres<BigInteger>();
        ufd2 = new GreatestCommonDivisorModular<ModLong>(); // dummy type
        return new GCDProxy<BigInteger>(ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, case BigRational.
     * @param fac BigRational.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<BigRational> getImplementation(BigRational fac) {
        GreatestCommonDivisorAbstract<BigRational> ufd;
        ufd = new GreatestCommonDivisorPrimitive<BigRational>();
        return ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, case BigRational.
     * @param fac BigRational.
     * @return gcd algorithm implementation.
     */
    public static GreatestCommonDivisorAbstract<BigRational> getProxy(BigRational fac) {
        GreatestCommonDivisorAbstract<BigRational> ufd1, ufd2;
        ufd1 = new GreatestCommonDivisorSubres<BigRational>();
        ufd2 = new GreatestCommonDivisorSimple<BigRational>();
        return new GCDProxy<BigRational>(ufd1, ufd2);
    }


    /**
     * Determine suitable implementation of gcd algorithms, other cases.
     * @param fac RingFactory&lt;C&gt;.
     * @return gcd algorithm implementation.
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GreatestCommonDivisorAbstract<C> getImplementation(
            RingFactory<C> fac) {
        GreatestCommonDivisorAbstract/*raw type<C>*/ufd;
        logger.debug("fac = " + fac.getClass().getName());
        int t = 0;
        Object ofac = fac;
        if (ofac instanceof BigInteger) {
            t = 1;
        }
        if (ofac instanceof ModIntegerRing) {
            t = 2;
        }
        if (ofac instanceof BigRational) {
            t = 3;
        }
        if (ofac instanceof ModLongRing) {
            t = 4;
        }
        //System.out.println("gt = " + t);
        if (t == 1) {
            ufd = new GreatestCommonDivisorModular/*<BigInteger>*/();
            //ufd = new GreatestCommonDivisorSubres<BigInteger>();
            //ufd = new GreatestCommonDivisorModular/*<BigInteger>*/(true);
        } else if (t == 2) {
            ufd = new GreatestCommonDivisorModEval<ModInteger>();
        } else if (t == 3) {
            ufd = new GreatestCommonDivisorSubres<C>();
        } else if (t == 4) {
            ufd = new GreatestCommonDivisorModEval<ModLong>();
        } else {
            if (fac.isField()) {
                ufd = new GreatestCommonDivisorSimple<C>();
            } else {
                ufd = new GreatestCommonDivisorSubres<C>();
            }
        }
        logger.debug("ufd = " + ufd);
        return (GreatestCommonDivisorAbstract<C>) ufd;
    }


    /**
     * Determine suitable proxy for gcd algorithms, other cases.
     * @param fac RingFactory&lt;C&gt;.
     * @return gcd algorithm implementation.
     * <b>Note:</b> This method contains a hack for Google app engine to not use threads.
     * @see edu.jas.kern.ComputerThreads#NO_THREADS
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GreatestCommonDivisorAbstract<C> getProxy(RingFactory<C> fac) {
        if ( ComputerThreads.NO_THREADS ) { // hack for Google app engine
           return GCDFactory.<C> getImplementation(fac);
        }
        GreatestCommonDivisorAbstract/*raw type<C>*/ufd;
        logger.debug("fac = " + fac.getClass().getName());
        int t = 0;
        Object ofac = fac;
        if (ofac instanceof BigInteger) {
            t = 1;
        }
        if (ofac instanceof ModIntegerRing) {
            t = 2;
        }
        if (ofac instanceof BigRational) {
            t = 3;
        }
        if (ofac instanceof ModLongRing) {
            t = 4;
        }
        //System.out.println("t     = " + t);
        if (t == 1) {
            ufd = new GCDProxy<BigInteger>(new GreatestCommonDivisorSubres<BigInteger>(),
                                           new GreatestCommonDivisorModular/*<BigInteger>*/());
            //    new GreatestCommonDivisorModular/*<BigInteger>*/(true) );
        } else if (t == 2) {
            ufd = new GCDProxy<ModInteger>(new GreatestCommonDivisorSubres<ModInteger>(),
                                           new GreatestCommonDivisorModEval<ModInteger>());
        } else if (t == 3) {
            ufd = new GCDProxy<C>(new GreatestCommonDivisorSubres<C>(), new GreatestCommonDivisorSimple<C>());
        } else if (t == 4) {
            ufd = new GCDProxy<ModLong>(new GreatestCommonDivisorSubres<ModLong>(),
                                        new GreatestCommonDivisorModEval<ModLong>());
        } else {
            if (fac.isField()) {
                ufd = new GCDProxy<C>(new GreatestCommonDivisorSimple<C>(),
                                      new GreatestCommonDivisorSubres<C>());
            } else {
                ufd = new GCDProxy<C>(new GreatestCommonDivisorSubres<C>(),
                                      new GreatestCommonDivisorPrimitive<C>());
            }
        }
        logger.debug("ufd = " + ufd);
        return (GCDProxy<C>) ufd;
    }

}
