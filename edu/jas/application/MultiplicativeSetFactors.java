/*
 * $Id: MultiplicativeSetFactors.java 2828 2009-09-27 12:30:52Z kredel $
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

import edu.jas.ufd.Factorization;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;


/**
 * Multiplicative set of irreducible polynomials.
 * a, b in M implies a*b in M, 1 in M.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class MultiplicativeSetFactors<C extends GcdRingElem<C>> extends MultiplicativeSet<C> {


    private static final Logger logger = Logger.getLogger(MultiplicativeSetFactors.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /**
     * Factors decomposition engine.
     */
    protected final FactorAbstract<C> engine;


    /**
     * MultiplicativeSet constructor. Constructs an empty multiplicative set.
     * @param ring polynomial ring factory for coefficients.
     */
    public MultiplicativeSetFactors(GenPolynomialRing<C> ring) {
        super(ring);
        engine = FactorFactory.getImplementation(ring.coFac);
    }


    /**
     * MultiplicativeSet constructor. 
     * @param ring polynomial ring factory for coefficients.
     * @param ms a list of non-zero polynomials.
     * @param eng factorization engine.
     */
    protected MultiplicativeSetFactors(GenPolynomialRing<C> ring, List<GenPolynomial<C>> ms, FactorAbstract<C> eng) {
        super(ring,ms);
        engine = eng;
    }


    /**
     * toString.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MultiplicativeSetFactors" + mset;
    }


    /**
     * Add polynomial to mset. 
     * @param cc polynomial to be added to mset.
     * @return new multiplicative set.
     */
    public MultiplicativeSetFactors<C> add(GenPolynomial<C> cc) {
        if (cc == null || cc.isZERO() || cc.isConstant()) { 
            return this;
        }
        if ( ring.coFac.isField() ) {
            cc = cc.monic();
        }
        GenPolynomial<C> c = removeFactors(cc);
        if ( c.isConstant() ) { 
            logger.info("skipped unit or constant = " + c);
            return this;
        }
        List<GenPolynomial<C>> list = engine.factorsRadical(c);
        logger.info("factorsRadical = " + list);
        if ( ring.coFac.isField() ) {
            list = PolyUtil.<C> monic(list);
        }
        List<GenPolynomial<C>> ms = new ArrayList<GenPolynomial<C>>(mset);
        for ( GenPolynomial<C> p : list ) {
            if ( !p.isConstant() && !p.isZERO() ) {
                if ( !mset.contains(p) ) {
                    logger.info("added to irreducible mset = " + p);
                    ms.add(p);
                }
            }
        }
        return new MultiplicativeSetFactors<C>(ring,ms,engine);
    }


    /**
     * Replace polynomial list of mset. 
     * @param L polynomial list to replace mset.
     * @return new multiplicative set.
     */
    public MultiplicativeSetFactors<C> replace(List<GenPolynomial<C>> L) {
        MultiplicativeSetFactors<C> ms = new MultiplicativeSetFactors<C>(ring);
        if (L == null || L.size() == 0) { 
            return ms;
        }
        for ( GenPolynomial<C> p : L ) {
            ms = ms.add(p);
        }
        return ms;
    }

}
