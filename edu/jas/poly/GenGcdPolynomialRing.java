/*
 * $Id: GenGcdPolynomialRing.java 2371 2009-01-25 16:21:56Z kredel $
 */

package edu.jas.poly;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorSubres;


/**
 * GenGcdPolynomialRing generic polynomial factory implementing RingFactory;
 * Factory for n-variate ordered polynomials over C. Almost immutable object,
 * except variable names.
 * @author Heinz Kredel
 */

public class GenGcdPolynomialRing<C extends GcdRingElem<C>> extends GenPolynomialRing<C> {


    /**
     * Log4j logger object.
     */
    private static final Logger logger = Logger.getLogger(GenGcdPolynomialRing.class);


    /**
     * GCD engine of the factory.
     */
    public final GreatestCommonDivisor<C> engine;


    /**
     * The constructor creates a polynomial factory object with the default term
     * order.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     */
    public GenGcdPolynomialRing(RingFactory<C> cf, int n) {
        this(cf, n, new TermOrder(), null);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     */
    public GenGcdPolynomialRing(RingFactory<C> cf, int n, TermOrder t) {
        this(cf, n, t, null);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param v names for the variables.
     */
    public GenGcdPolynomialRing(RingFactory<C> cf, int n, String[] v) {
        this(cf, n, new TermOrder(), v);
    }


    /**
     * The constructor creates a polynomial factory object.
     * @param cf factory for coefficients of type C.
     * @param n number of variables.
     * @param t a term order.
     * @param v names for the variables.
     */
    public GenGcdPolynomialRing(RingFactory<C> cf, int n, TermOrder t, String[] v) {
        super(cf, n, t, v);
        //engine = GCDFactory.<C>getImplementation(coFac);
        engine = new GreatestCommonDivisorSubres<C>();
    }


    /**
     * The constructor creates a polynomial factory object with the the same
     * term order, number of variables and variable names as the given
     * polynomial factory, only the coefficient factories differ.
     * @param cf factory for coefficients of type C.
     * @param o other polynomial ring.
     */
    public GenGcdPolynomialRing(RingFactory<C> cf, GenPolynomialRing o) {
        this(cf, o.nvar, o.tord, o.getVars());
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend number of
     * variables by i.
     * @param i number of variables to extend.
     * @return extended polynomial ring factory.
     */
    @Override
    public GenGcdPolynomialRing<C> extend(int i) {
        // add module variable names
        String[] v = null;
        if (vars != null) {
            v = new String[vars.length + i];
            for (int k = 0; k < vars.length; k++) {
                v[k] = vars[k];
            }
            for (int k = 0; k < i; k++) {
                v[vars.length + k] = "e" + (k + 1);
            }
        }
        TermOrder to = tord.extend(nvar, i);
        GenGcdPolynomialRing<C> pfac = new GenGcdPolynomialRing<C>(coFac, nvar + i, to, v);
        return pfac;
    }


    /**
     * Contract variables. Used e.g. in module embedding. Contract number of
     * variables by i.
     * @param i number of variables to remove.
     * @return contracted polynomial ring factory.
     */
    @Override
    public GenGcdPolynomialRing<C> contract(int i) {
        String[] v = null;
        if (vars != null) {
            v = new String[vars.length - i];
            for (int j = 0; j < vars.length - i; j++) {
                v[j] = vars[j];
            }
        }
        TermOrder to = tord.contract(i, nvar - i);
        GenGcdPolynomialRing<C> pfac = new GenGcdPolynomialRing<C>(coFac, nvar - i, to, v);
        return pfac;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial ring factory with reversed variables.
     */
    @Override
    public GenGcdPolynomialRing<C> reverse() {
        return reverse(false);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @param partial true for partialy reversed term orders.
     * @return polynomial ring factory with reversed variables.
     */
    @Override
    public GenGcdPolynomialRing<C> reverse(boolean partial) {
        String[] v = null;
        if (vars != null) { // vars are not inversed
            v = new String[vars.length];
            int k = tord.getSplit();
            if (partial && k < vars.length) {
                for (int j = 0; j < k; j++) {
                    v[vars.length - k + j] = vars[vars.length - 1 - j];
                }
                for (int j = 0; j < vars.length - k; j++) {
                    v[j] = vars[j];
                }
            } else {
                for (int j = 0; j < vars.length; j++) {
                    v[j] = vars[vars.length - 1 - j];
                }
            }
        }
        TermOrder to = tord.reverse(partial);
        GenGcdPolynomialRing<C> pfac = new GenGcdPolynomialRing<C>(coFac, nvar, to, v);
        pfac.partial = partial;
        return pfac;
    }

}
