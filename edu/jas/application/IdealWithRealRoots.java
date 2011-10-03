/*
 * $Id: IdealWithRealRoots.java 3111 2010-05-05 21:05:56Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for Ideals together with univariate polynomials and real roots.
 * @author Heinz Kredel
 */
public class IdealWithRealRoots<C extends GcdRingElem<C>> extends IdealWithUniv<C> implements Serializable {


    /**
     * The list of real roots.
     */
    public final List<List<BigDecimal>> rroots;


    /**
     * Constructor not for use.
     */
    protected IdealWithRealRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univaraite polynomials
     * @param rr the list of real roots
     */
    public IdealWithRealRoots(Ideal<C> id, List<GenPolynomial<C>> up, List<List<BigDecimal>> rr) {
        super(id, up);
        rroots = rr;
    }


    /**
     * Constructor.
     * @param iu the ideal with univariate polynomials
     * @param rr the list of real roots
     */
    public IdealWithRealRoots(IdealWithUniv<C> iu, List<List<BigDecimal>> rr) {
        super(iu.ideal, iu.upolys);
        rroots = rr;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "\nreal roots: " + rroots.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return super.toScript() + ",  " + rroots.toString();
    }

}
