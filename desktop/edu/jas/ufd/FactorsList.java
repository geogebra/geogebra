/*
 * $Id: FactorsList.java 2754 2009-07-16 19:40:51Z kredel $
 */

package edu.jas.ufd;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the factors of a squarefree factorization.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class FactorsList<C extends GcdRingElem<C>> implements Serializable {


    /**
     * Original polynomial to be factored with coefficients from C.
     */
    public final GenPolynomial<C> poly;


    /**
     * List of factors with coefficients from C.
     */
    public final List<GenPolynomial<C>> factors;


    /**
     * List of factors with coefficients from AlgebraicNumberRings.
     */
    public final List<Factors<C>> afactors;


    /**
     * Constructor.
     * @param p given GenPolynomial over C.
     * @param list irreducible factors of p with coefficients from C.
     */
    public FactorsList(GenPolynomial<C> p, List<GenPolynomial<C>> list) {
        this(p, list, null);
    }


    /**
     * Constructor.
     * @param p given GenPolynomial over C.
     * @param list irreducible factors of p with coefficients from C.
     * @param alist irreducible factors of p with coefficients from an algebraic
     *            number field.
     */
    public FactorsList(GenPolynomial<C> p, List<GenPolynomial<C>> list, List<Factors<C>> alist) {
        poly = p;
        factors = list;
        afactors = alist;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        //sb.append(poly.toString());
        //sb.append(" =\n");
        boolean first = true;
        for (GenPolynomial<C> p : factors) {
            if (first) {
                first = false;
            } else {
                sb.append(",\n ");
            }
            sb.append(p.toString());
        }
        if (afactors == null) {
            return sb.toString();
        }
        for (Factors<C> f : afactors) {
            if (first) {
                first = false;
            } else {
                sb.append(",\n ");
            }
            sb.append(f.toString());
        }
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this container.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer();
        sb.append(poly.toScript());
        sb.append(" =\n");
        boolean first = true;
        for (GenPolynomial<C> p : factors) {
            if (first) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            sb.append(p.toScript());
        }
        if (afactors == null) {
            return sb.toString();
        }
        for (Factors<C> f : afactors) {
            if (first) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            sb.append(f.toScript());
        }
        return sb.toString();
    }


    /**
     * Find largest extension field.
     * @return largest extension field or null if no extension field
     */
    public AlgebraicNumberRing<C> findExtensionField() {
        if (afactors == null) {
            return null;
        }
        AlgebraicNumberRing<C> ar = null;
        int depth = 0;
        for (Factors<C> f : afactors) {
            AlgebraicNumberRing<C> aring = f.findExtensionField();
            if (aring == null) {
                continue;
            }
            int d = aring.depth();
            if (d > depth) {
                depth = d;
                ar = aring;
            }
        }
        return ar;
    }

}
