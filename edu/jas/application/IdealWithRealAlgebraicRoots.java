/*
 * $Id: IdealWithRealAlgebraicRoots.java 3111 2010-05-05 21:05:56Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.Rational;
import edu.jas.poly.GenPolynomial;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;


/**
 * Container for Ideals together with univariate polynomials and real algebraic
 * roots.
 * @author Heinz Kredel
 */
public class IdealWithRealAlgebraicRoots<C extends RingElem<C> & Rational, D extends GcdRingElem<D> & Rational>
        extends IdealWithUniv<D> implements Serializable {


    /**
     * The list of real algebraic roots.
     */
    public final List<List<RealAlgebraicNumber<D>>> ran;


    /**
     * The list of decimal approximations of the real algebraic roots.
     */
    protected List<List<BigDecimal>> droots = null;


    /**
     * Constructor not for use.
     */
    protected IdealWithRealAlgebraicRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univaraite polynomials
     * @param rr the list of real algebraic roots
     */
    public IdealWithRealAlgebraicRoots(Ideal<D> id, List<GenPolynomial<D>> up,
            List<List<RealAlgebraicNumber<D>>> rr) {
        super(id, up);
        ran = rr;
    }


    /**
     * Constructor.
     * @param iu the ideal with univariate polynomials
     * @param rr the list of real algebraic roots
     */
    public IdealWithRealAlgebraicRoots(IdealWithUniv<D> iu, List<List<RealAlgebraicNumber<D>>> rr) {
        super(iu.ideal, iu.upolys);
        ran = rr;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString() + "\nreal roots:\n");
        sb.append("[");
        boolean f1 = true;
        for (List<RealAlgebraicNumber<D>> lr : ran) {
            if (!f1) {
                sb.append(", ");
            } else {
                f1 = false;
            }
            sb.append("[");
            boolean f2 = true;
            for (RealAlgebraicNumber<D> rr : lr) {
                if (!f2) {
                    sb.append(", ");
                } else {
                    f2 = false;
                }
                sb.append(rr.ring.toScript());
            }
            sb.append("]");
        }
        sb.append("]");
        if (droots != null) {
            sb.append("\ndecimal real root approximation:\n");
            for (List<BigDecimal> d : droots) {
                sb.append(d.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return super.toScript() + ",  " + ran.toString();
    }


    /**
     * Get decimal approximation of the real root tuples.
     */
    public synchronized List<List<BigDecimal>> decimalApproximation() {
        if (this.droots != null) {
            return droots;
        }
        List<List<BigDecimal>> rroots = new ArrayList<List<BigDecimal>>();
        for (List<RealAlgebraicNumber<D>> rri : this.ran) {
            List<BigDecimal> r = new ArrayList<BigDecimal>();
            for (RealAlgebraicNumber<D> rr : rri) {
                BigDecimal d = new BigDecimal(rr.magnitude());
                r.add(d);
            }
            rroots.add(r);
        }
        droots = rroots;
        return rroots;
    }


    /**
     * compute decimal approximation of the real root tuples.
     */
    public void doDecimalApproximation() {
        List<List<BigDecimal>> unused = decimalApproximation();
        return;
    }

}
