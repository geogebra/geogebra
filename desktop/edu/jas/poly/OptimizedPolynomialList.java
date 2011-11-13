/*
 * $Id: OptimizedPolynomialList.java 2996 2010-02-07 13:32:42Z kredel $
 */

package edu.jas.poly;


import java.util.List;

import edu.jas.structure.RingElem;


/**
 * Container for optimization results.
 * @author Heinz Kredel
 */

public class OptimizedPolynomialList<C extends RingElem<C>> extends PolynomialList<C> {


    /**
     * Permutation vector used to optimize term order.
     */
    public final List<Integer> perm;


    /**
     * Constructor.
     */
    public OptimizedPolynomialList(List<Integer> P, GenPolynomialRing<C> R, List<GenPolynomial<C>> L) {
        super(R, L);
        perm = P;
    }


    /**
     * String representation.
     */
    @Override
    public String toString() {
        return "permutation = " + perm + "\n" + super.toString();
    }

}
