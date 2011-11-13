/*
 * $Id: MultiplicativeSetCoPrime.java 2828 2009-09-27 12:30:52Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.PolyUtil;

import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GCDFactory;


/**
 * Multiplicative set of co-prime polynomials.
 * a, b in M implies a*b in M, 1 in M.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class MultiplicativeSetCoPrime<C extends GcdRingElem<C>> extends MultiplicativeSet<C> {


    private static final Logger logger = Logger.getLogger(MultiplicativeSetCoPrime.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /**
     * Gcd computation engine.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * MultiplicativeSet constructor. Constructs an empty multiplicative set.
     * @param ring polynomial ring factory for coefficients.
     */
    public MultiplicativeSetCoPrime(GenPolynomialRing<C> ring) {
        super(ring);
        engine = GCDFactory.getProxy(ring.coFac);
    }


    /**
     * MultiplicativeSet constructor. 
     * @param ring polynomial ring factory for coefficients.
     * @param ms a list of non-zero polynomials.
     * @param eng gcd computation engine.
     */
    protected MultiplicativeSetCoPrime(GenPolynomialRing<C> ring, List<GenPolynomial<C>> ms, GreatestCommonDivisorAbstract<C> eng) {
        super(ring,ms);
        engine = eng;
    }


    /**
     * toString.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MultiplicativeSetCoPrime" + mset;
    }


    /**
     * Add polynomial to mset. 
     * @param cc polynomial to be added to mset.
     * @return new multiplicative set.
     */
    public MultiplicativeSetCoPrime<C> add(GenPolynomial<C> cc) {
        if (cc == null || cc.isZERO() || cc.isConstant()) { 
            return this;
        }
        if ( ring.coFac.isField() ) {
            cc = cc.monic();
        }
        List<GenPolynomial<C>> list;
        if (mset.size() == 0) { 
            list = engine.coPrime(cc,mset);
            if ( ring.coFac.isField() ) {
                list = PolyUtil.<C> monic(list);
            }
            return new MultiplicativeSetCoPrime<C>(ring,list,engine);
        }
        GenPolynomial<C> c = removeFactors(cc);
        if ( c.isConstant() ) { 
            logger.info("skipped unit or constant = " + c);
            return this;
        }
        logger.info("added to co-prime mset = " + c);
        list = engine.coPrime(c,mset);
        if ( ring.coFac.isField() ) {
            list = PolyUtil.<C> monic(list);
        }
        return new MultiplicativeSetCoPrime<C>(ring,list,engine);
    }


    /**
     * Replace polynomial list of mset. 
     * @param L polynomial list to replace mset.
     * @return new multiplicative set.
     */
    public MultiplicativeSetCoPrime<C> replace(List<GenPolynomial<C>> L) {
        MultiplicativeSetCoPrime<C> ms = new MultiplicativeSetCoPrime<C>(ring);
        if (L == null || L.size() == 0) { 
            return ms;
        }
        for ( GenPolynomial<C> p : L ) {
            ms = ms.add(p);
        }
        return ms;
    }


}
