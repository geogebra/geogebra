/*
 * $Id: GBFactory.java 3189 2010-06-26 20:10:32Z kredel $
 */

package edu.jas.gb;


import org.apache.log4j.Logger;

import edu.jas.arith.Modular;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModularRingFactory;
import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;
//import edu.jas.structure.RegularRingElem;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Groebner bases algorithms factory. Select appropriate Groebner bases engine
 * based on the coefficient types.
 * @author Heinz Kredel
 * @usage To create objects that implement the
 *        <code>GroebnerBase</code> interface use the
 *        <code>GBFactory</code>. It will select an appropriate
 *        implementation based on the types of polynomial coefficients C. The
 *        method to obtain an implementation is <code>getImplementation()</code>.
 *        <code>getImplementation()</code> returns an object of a class which
 *        implements the <code>GroebnerBase</code> interface, more precisely 
 *        an object of abstract class <code>GroebnerBaseAbstract</code>.
 * <pre>
 * GroebnerBase&lt;CT&gt; engine;
 * engine = GBFactory.&lt;CT&gt; getImplementation(cofac);
 * c = engine.GB(A);
 * </pre>
 * 
 * For example, if the coefficient type is BigInteger, the usage looks
 *        like
 * 
 * <pre>
 * BigInteger cofac = new BigInteger();
 * GroebnerBase&lt;BigInteger&gt; engine;
 * engine = GBFactory.getImplementation(cofac);
 * c = engine.GB(A);
 * </pre>
 * 
 * @see edu.jas.gb.GroebnerBase#GB(java.util.List P)
 */

public class GBFactory {


    private static final Logger logger = Logger.getLogger(GBFactory.class);


    public enum Algo { igb, egb, dgb }; 


    /**
     * Protected factory constructor.
     */
    protected GBFactory() {
    }


    /**
     * Determine suitable implementation of GB algorithms, no factory case.
     * @return GB algorithm implementation for field coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GroebnerBaseAbstract<C> getImplementation() {
        logger.warn("no coefficent factory given, assuming field coeffcients");
        GroebnerBaseAbstract<C> bba = new GroebnerBaseSeq<C>();
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case ModLong.
     * @param fac ModLongRing.
     * @return GB algorithm implementation.
     */
    public static GroebnerBaseAbstract<ModLong> getImplementation(ModLongRing fac) {
        GroebnerBaseAbstract<ModLong> bba;
        if (fac.isField()) {
            bba = new GroebnerBaseSeq<ModLong>();
        } else {
            bba = new GroebnerBasePseudoSeq<ModLong>(fac);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case ModInteger.
     * @param fac ModIntegerRing.
     * @return GB algorithm implementation.
     */
    public static GroebnerBaseAbstract<ModInteger> getImplementation(ModIntegerRing fac) {
        GroebnerBaseAbstract<ModInteger> bba;
        if (fac.isField()) {
            bba = new GroebnerBaseSeq<ModInteger>();
        } else {
            bba = new GroebnerBasePseudoSeq<ModInteger>(fac);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigInteger.
     * @param fac BigInteger.
     * @return GB algorithm implementation.
     */
    public static GroebnerBaseAbstract<BigInteger> getImplementation(BigInteger fac) {
        return getImplementation(fac,Algo.igb);
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigInteger.
     * @param fac BigInteger.
     * @param a algorithm.
     * @return GB algorithm implementation.
     */
    public static GroebnerBaseAbstract<BigInteger> 
      getImplementation(BigInteger fac, Algo a) {
        GroebnerBaseAbstract<BigInteger> bba;
        switch (a) {
        case igb: bba = new GroebnerBasePseudoSeq<BigInteger>(fac); 
            break;
        case egb: bba = new EGroebnerBaseSeq<BigInteger>();
            break;
        case dgb: bba = new DGroebnerBaseSeq<BigInteger>();
            break;
        default:
            throw new RuntimeException("algorithm not available " + a);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case BigRational.
     * @param fac BigRational.
     * @return GB algorithm implementation.
     */
    public static GroebnerBaseAbstract<BigRational> getImplementation(BigRational fac) {
        GroebnerBaseAbstract<BigRational> bba;
        bba = new GroebnerBaseSeq<BigRational>();
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case (recursive) polynomial.
     * @param fac GenPolynomialRing&lt;C&gt;.
     * @return GB algorithm implementation.
     */
    public static <C extends GcdRingElem<C>> 
      GroebnerBaseAbstract<GenPolynomial<C>> getImplementation(GenPolynomialRing<C> fac) {
        GroebnerBaseAbstract<GenPolynomial<C>> bba;
        bba = new GroebnerBasePseudoRecSeq<C>(fac);
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, case regular rings.
     * @param fac RegularRing.
     * @return GB algorithm implementation.
     */
    public static <C extends RingElem<C>>  
      GroebnerBaseAbstract<Product<C>> getImplementation(ProductRing<C> fac) {
        GroebnerBaseAbstract<Product<C>> bba;
        if (fac.onlyFields()) {
            bba = new RGroebnerBaseSeq<Product<C>>();
        } else {
            bba = new RGroebnerBasePseudoSeq<Product<C>>(fac);
        }
        return bba;
    }


    /**
     * Determine suitable implementation of GB algorithms, other cases.
     * @param fac RingFactory&lt;C&gt;.
     * @return GB algorithm implementation.
     */
    //@SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>>  // interface RingElem not sufficient 
      GroebnerBaseAbstract<C> getImplementation(RingFactory<C> fac) {
        logger.debug("fac = " + fac.getClass().getName());
        if (fac.isField()) {
            return new GroebnerBaseSeq<C>();
        }
        GroebnerBaseAbstract bba = null;
        Object ofac = fac;
        if ( ofac instanceof GenPolynomialRing ) {
            GroebnerBaseAbstract<GenPolynomial<C>> bbr 
                = new GroebnerBasePseudoRecSeq<C>( (GenPolynomialRing<C>) ofac);
            bba = (GroebnerBaseAbstract) bbr;
        } else if ( ofac instanceof ProductRing ) {
            ProductRing pfac = (ProductRing) ofac;
            if (pfac.onlyFields()) {
               bba = new RGroebnerBaseSeq<Product<C>>();
            } else {
               bba = new RGroebnerBasePseudoSeq<Product<C>>(pfac);
            }
        } else {
            bba = new GroebnerBasePseudoSeq<C>(fac);
        }
        logger.debug("bba = " + bba.getClass().getName());
        return (GroebnerBaseAbstract<C>)bba;
    }


    /**
     * Determine suitable concurrent implementation of GB algorithms if possible.
     * @param fac RingFactory&lt;C&gt;.
     * @return GB proxy algorithm implementation.
     */
    //@SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>>  // interface RingElem not sufficient 
      GroebnerBaseAbstract<C> getProxy(RingFactory<C> fac) {
        logger.debug("fac = " + fac.getClass().getName());
        if (fac.isField()) {
            if ( ComputerThreads.NO_THREADS ) {
                return new GroebnerBaseSeq<C>();
            }
            GroebnerBaseAbstract<C> e1 = new GroebnerBaseSeq<C>();
            GroebnerBaseAbstract<C> e2 = new GroebnerBaseParallel<C>(ComputerThreads.N_CPUS);
            return new GBProxy<C>(e1,e2);
        }
        GroebnerBaseAbstract bba = null;
        Object ofac = fac;
        if ( ofac instanceof GenPolynomialRing ) {
            GroebnerBaseAbstract<GenPolynomial<C>> bbr 
                = new GroebnerBasePseudoRecSeq<C>( (GenPolynomialRing<C>) ofac);
            bba = (GroebnerBaseAbstract) bbr;
        } else if ( ofac instanceof ProductRing ) {
            ProductRing pfac = (ProductRing) ofac;
            if (pfac.onlyFields()) {
               bba = new RGroebnerBaseSeq<Product<C>>();
            } else {
               bba = new RGroebnerBasePseudoSeq<Product<C>>(pfac);
            }
        } else {
            bba = new GroebnerBasePseudoSeq<C>(fac);
        }
        logger.debug("bba = " + bba.getClass().getName());
        return (GroebnerBaseAbstract<C>)bba;
    }

}

