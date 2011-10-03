/*
 * $Id: Power.java 2712 2009-07-05 13:05:37Z kredel $
 */

package edu.jas.structure;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.MonoidElem;
import edu.jas.structure.MonoidFactory;


/**
 * Power class to compute powers of RingElem. 
 * @author Heinz Kredel
 */
public class Power<C extends RingElem<C> > {

    private static final Logger logger = Logger.getLogger(Power.class);
    private static boolean debug = logger.isDebugEnabled();


    private final RingFactory<C> fac;


    /** The constructor creates a Power object.
     */
    public Power() {
        this(null);
    }


    /** The constructor creates a Power object.
     * @param fac ring factory 
     */
    public Power(RingFactory<C> fac) {
        this.fac = fac;
    }


    /** power of a to the n-th, n positive. 
     * @param a element. 
     * @param n integer exponent > 0.
     * @return a^n.
     */
    public static <C extends RingElem<C>> C positivePower(C a, long n) {
        if ( n <= 0 ) {
            throw new IllegalArgumentException("only positive n allowed");
        }
        if ( a.isZERO() || a.isONE() ) {
           return a;
        }
        C b = a;
        long i = n-1;
        C p = b;
        do {
           if ( i % 2 == 1 ) {
              p = p.multiply( b );
           }
           i = i / 2;
           if ( i > 0 ) {
              b = b.multiply( b );
           }
        } while ( i > 0 );
        return p;
    }


    /** power of a to the n-th, n positive. 
     * @param a element. 
     * @param n java.math.BigInteger exponent > 0.
     * @return a^n.
     */
    public static <C extends RingElem<C>> C positivePower(C a, java.math.BigInteger n) {
        if ( n.signum() <= 0 ) {
            throw new IllegalArgumentException("only positive n allowed");
        }
        if ( a.isZERO() || a.isONE() ) {
           return a;
        }
        C b = a;
        if ( n.compareTo(java.math.BigInteger.ONE) == 0 ) {
           return b;
        }
        C p = a;
        java.math.BigInteger i = n.subtract(java.math.BigInteger.ONE);
        do {
            if ( i.testBit(0) ) {
               p = p.multiply( b );
            }
            i = i.shiftRight(1);
            if ( i.signum() > 0 ) {
               b = b.multiply( b );
            }
        } while ( i.signum() > 0 );
        return p;
    }


    /** power of a to the n-th, n positive, modulo m. 
     * @param a element. 
     * @param n integer exponent > 0.
     * @param m modulus. 
     * @return a^n mod m.
     */
    public static <C extends RingElem<C>> C modPositivePower(C a, long n, C m) {
        if ( n <= 0 ) {
            throw new IllegalArgumentException("only positive n allowed");
        }
        if ( a.isZERO() || a.isONE() ) {
           return a;
        }

        C b = a.remainder(m);
        long i = n-1;
        C p = b;
        do {
           if ( i % 2 == 1 ) {
              p = p.multiply( b ).remainder(m);
           }
           i = i / 2;
           if ( i > 0 ) {
              b = b.multiply( b ).remainder(m);
           }
        } while ( i > 0 );
        return p;
    }


    /**
     * power of a to the n-th.
     * @param a element.
     * @param n integer exponent.
     * @return a^n, with 0^0 = 0 and a^{-n} = {1/a}^n.
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> C power( RingFactory<C> fac, C a, long n ) {
        if ( a == null || a.isZERO() ) {
           return a;
        }
        //return a;
        return (C) Power.<MonoidElem>power( (MonoidFactory)fac, a, n);
    }


    /**
     * power of a to the n-th.
     * @param a element.
     * @param n integer exponent.
     * @return a^n, with a^{-n} = {1/a}^n.
     */
    public static <C extends MonoidElem<C>> C power( MonoidFactory<C> fac, C a, long n ) {
        if ( n == 0 ) {
           if ( fac == null ) {
              throw new IllegalArgumentException("fac may not be null for a^0");
           }
           return fac.getONE();
        }
        if ( a.isONE() ) {
           return a;
        }
        C b = a;
        if ( n < 0 ) {
           b = a.inverse();
           n = -n;
        }
        if ( n == 1 ) {
           return b;
        }
        C p = fac.getONE();
        long i = n;
        do {
           if ( i % 2 == 1 ) {
              p = p.multiply( b );
           }
           i = i / 2;
           if ( i > 0 ) {
              b = b.multiply( b );
           }
        } while ( i > 0 );
        if ( n > 11 && debug ) {
            logger.info("n  = " + n + ", p  = " + p);
        }
        return p;
    }


    /**
     * power of a to the n-th modulo m.
     * @param a element.
     * @param n integer exponent.
     * @param m modulus.
     * @return a^n mod m, with a^{-n} = {1/a}^n.
     */
    public static <C extends MonoidElem<C>> C modPower( MonoidFactory<C> fac, C a, long n, C m) {
        if ( n == 0 ) {
           if ( fac == null ) {
              throw new IllegalArgumentException("fac may not be null for a^0");
           }
           return fac.getONE();
        }
        if ( a.isONE() ) {
           return a;
        }
        C b = a.remainder(m);
        if ( n < 0 ) {
           b = a.inverse().remainder(m);
           n = -n;
        }
        if ( n == 1 ) {
           return b;
        }
        C p = fac.getONE();
        long i = n;
        do {
           if ( i % 2 == 1 ) {
              p = p.multiply( b ).remainder(m);
           }
           i = i / 2;
           if ( i > 0 ) {
              b = b.multiply( b ).remainder(m);
           }
        } while ( i > 0 );
        if ( n > 11 && debug ) {
            logger.info("n  = " + n + ", p  = " + p);
        }
        return p;
    }


    /**
     * power of a to the n-th modulo m.
     * @param a element.
     * @param n integer exponent.
     * @param m modulus.
     * @return a^n mod m, with a^{-n} = {1/a}^n.
     */
    public static <C extends MonoidElem<C>> C modPower( MonoidFactory<C> fac, C a, 
                                                        java.math.BigInteger n, C m) {
        if ( n.signum() == 0 ) {
           if ( fac == null ) {
              throw new IllegalArgumentException("fac may not be null for a^0");
           }
           return fac.getONE();
        }
        if ( a.isONE() ) {
           return a;
        }
        C b = a.remainder(m);
        if ( n.signum() < 0 ) {
           b = a.inverse().remainder(m);
           n = n.negate();
        }
        if ( n.compareTo(java.math.BigInteger.ONE) == 0 ) {
           return b;
        }
        C p = fac.getONE();
        java.math.BigInteger i = n;
        do {
            if ( i.testBit(0) ) {
               p = p.multiply( b ).remainder(m);
            }
            i = i.shiftRight(1);
            if ( i.signum() > 0 ) {
               b = b.multiply( b ).remainder(m);
            }
        } while ( i.signum() > 0 );
        if ( debug ) {
            logger.info("n  = " + n + ", p  = " + p);
        }
        return p;
    }


    /** power of a to the n-th. 
     * @param a element. 
     * @param n integer exponent.
     * @return a^n, with 0^0 = 0.
     */
    public C power(C a, long n) {
        return power( fac, a, n );
    }


    /** power of a to the n-th mod m. 
     * @param a element. 
     * @param n integer exponent.
     * @param m modulus. 
     * @return a^n mod m, with 0^0 = 0.
     */
    public C modPower(C a, long n, C m) {
        return modPower( fac, a, n, m );
    }


    /** power of a to the n-th mod m. 
     * @param a element. 
     * @param n integer exponent.
     * @param m modulus. 
     * @return a^n mod m, with 0^0 = 0.
     */
    public C modPower(C a, java.math.BigInteger n, C m) {
        return modPower( fac, a, n, m );
    }
 
}
