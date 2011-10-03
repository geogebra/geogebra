/*
 * $Id: IdealWithComplexRoots.java 3111 2010-05-05 21:05:56Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.Complex;
import edu.jas.structure.GcdRingElem;


/**
 * Container for Ideals together with univariate polynomials and complex roots.
 * @author Heinz Kredel
 */
class IdealWithComplexRoots<C extends GcdRingElem<C>> extends IdealWithUniv<C> implements Serializable {


    /**
     * The list of complex roots.
     */
    public final List<List<Complex<BigDecimal>>> croots;


    /**
     * Constructor not for use.
     */
    protected IdealWithComplexRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univaraite polynomials
     * @param cr the list of complex roots
     */
    public IdealWithComplexRoots(Ideal<C> id, List<GenPolynomial<C>> up, List<List<Complex<BigDecimal>>> cr) {
        super(id, up);
        croots = cr;
    }


    /**
     * Constructor.
     * @param iu the ideal with univariate polynomials
     * @param cr the list of complex roots
     */
    public IdealWithComplexRoots(IdealWithUniv<C> iu, List<List<Complex<BigDecimal>>> cr) {
        super(iu.ideal, iu.upolys);
        croots = cr;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "\ncomplex roots: " + croots.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return super.toScript() + ",  " + croots.toString();
    }

}
