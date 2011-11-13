/*
 * $Id: HenselApprox.java 2974 2010-01-04 22:19:52Z kredel $
 */

package edu.jas.ufd;

import java.io.Serializable;

import edu.jas.arith.BigInteger;
import edu.jas.arith.Modular;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.ModularRingFactory;


/**
 * Container for the approximation result from a Hensel algorithm.
 * @author Heinz Kredel
 * @param <MOD> coefficient type
 */

public class HenselApprox<MOD extends GcdRingElem<MOD> & Modular> implements Serializable {


    /**
     * Approximated polynomial with integer coefficients.
     */
    public final GenPolynomial<BigInteger> A;


    /**
     * Approximated polynomial with integer coefficients.
     */
    public final GenPolynomial<BigInteger> B;


    /**
     * Modular approximated polynomial with modular coefficients.
     */
    public final GenPolynomial<MOD> Am;


    /**
     * Modular approximated polynomial with modular coefficients.
     */
    public final GenPolynomial<MOD> Bm;


    /**
     * Constructor.
     * @param A approximated polynomial.
     * @param B approximated polynomial.
     * @param Am approximated modular polynomial.
     * @param Bm approximated modular polynomial.
     */
    public HenselApprox(GenPolynomial<BigInteger> A, GenPolynomial<BigInteger> B, GenPolynomial<MOD> Am , GenPolynomial<MOD> Bm) {
        this.A = A;
        this.B = B;
        this.Am = Am;
        this.Bm = Bm;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(A.toString());
        sb.append(",");
        sb.append(B.toString());
        sb.append(",");
        sb.append(Am.toString());
        sb.append(",");
        sb.append(Bm.toString());
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
        sb.append(A.toScript());
        sb.append(",");
        sb.append(B.toScript());
        sb.append(",");
        sb.append(Am.toScript());
        sb.append(",");
        sb.append(Bm.toScript());
        return sb.toString();
    }


    /**
     * Hash code for this Factors.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = A.hashCode();
        h = 37 * h + B.hashCode();
        h = 37 * h + Am.hashCode();
        h = 37 * h + Bm.hashCode();
        return h;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (!(B instanceof HenselApprox)) {
            return false;
        }
        HenselApprox<MOD> a = null;
        try {
            a = (HenselApprox<MOD>) B;
        } catch (ClassCastException ignored) {
        }
        if (a == null) {
            return false;
        }
        return A.equals(a.A) && B.equals(a.B) && Am.equals(a.Am) && Bm.equals(a.Bm);
    }


    /**
     * Get modul of modular polynomial.
     * @return coefficient modul of polynomial mpol.
     */
    public BigInteger approximationSize() {
        ModularRingFactory fac = (ModularRingFactory) Am.ring.coFac;
        return fac.getIntegerModul();
    }

}
