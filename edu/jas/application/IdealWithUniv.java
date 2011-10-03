/*
 * $Id: IdealWithUniv.java 3167 2010-06-04 21:43:23Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for Ideals together with univariate polynomials.
 * @author Heinz Kredel
 */
public class IdealWithUniv<C extends GcdRingElem<C>> implements Serializable {


    /**
     * The ideal.
     */
    public final Ideal<C> ideal;


    /**
     * The list of univariate polynomials.
     * Contains polynomials from serveral rings, depending on the stage of the decomposition. 
     * 1) polynomials in a ring of one variable,
     * 2) polynomials depending on only one variable but in a ring with multiple variables,
     * 3) after contraction to a non-zero dimensional ring multivariate polynomials depending on 
     * one significant variable and multiple variables from the quotient coefficients.
     */
    public final List<GenPolynomial<C>> upolys;


    /**
     * A list of other useful polynomials.
     * 1) field extension polynomials,
     * 2) generators for infinite quotients.
     */
    public final List<GenPolynomial<C>> others;


    /**
     * Constructor not for use.
     */
    protected IdealWithUniv() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univariate polynomials
     */
    protected IdealWithUniv(Ideal<C> id, List<GenPolynomial<C>> up) {
        this(id, up, null);
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univariate polynomials
     * @param og the list of other polynomials
     */
    protected IdealWithUniv(Ideal<C> id, List<GenPolynomial<C>> up, List<GenPolynomial<C>> og) {
        ideal = id;
        upolys = up;
        others = og;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = ideal.toString();
	if ( upolys != null ) 
             s += "\nunivariate polynomials:\n" + upolys.toString();
        if (others == null) {
            return s;
        } else {
            return s + "\nother polynomials:\n" + others.toString();
        }
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        String s = ideal.toScript() + ",  " + upolys.toString();
        if (others == null) {
            return s;
        } else {
            return s + ", " + others.toString();
        }
    }

}
