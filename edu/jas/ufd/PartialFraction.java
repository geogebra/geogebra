/*
 * $Id: PartialFraction.java 3030 2010-03-08 22:43:41Z kredel $
 */

package edu.jas.ufd;


import java.io.Serializable;
import java.util.List;
//import java.util.Arrays;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the partial fraction decomposition of a squarefree denominator.
 * num/den = sum( a_i ( der(d_i) / d_i ) )
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class PartialFraction<C extends GcdRingElem<C>> implements Serializable {


    /**
     * Original numerator polynomial coefficients from C and deg(num) &lt; deg(den).
     */
    public final GenPolynomial<C> num;


    /**
     * Original (irreducible) denominator polynomial coefficients from C.
     */
    public final GenPolynomial<C> den;


    /**
     * List of numbers from C. 
     */
    public final List<C> cfactors;


    /**
     * List of linear factors of the denominator with coefficients from C. 
     */
    public final List<GenPolynomial<C>> cdenom;


    /**
     * List of algebraic numbers of an algebraic field extension over C. 
     */
    public final List<AlgebraicNumber<C>> afactors;


    /**
     * List of factors of the denominator with coefficients from an AlgebraicNumberRing&lt;C&gt;. 
     */
    public final List<GenPolynomial<AlgebraicNumber<C>>> adenom;


    /**
     * Constructor.
     * @param n numerator GenPolynomial over C.
     * @param d irreducible denominator GenPolynomial over C.
     * @param cf list of elements a_i.
     * @param cd list of linear factors d_i of d.
     * @param af list of algebraic elements a_i.
     * @param ad list of irreducible factors d_i of d with algebraic coefficients.
     * n/d = sum( a_i ( der(d_i) / d_i ) )
     */
    public PartialFraction(GenPolynomial<C> n, GenPolynomial<C> d, 
            List<C> cf,
            List<GenPolynomial<C>> cd,
            List<AlgebraicNumber<C>> af,
            List<GenPolynomial<AlgebraicNumber<C>>> ad) {
        num = n;
        den = d;
        cfactors = cf;
        cdenom = cd;
        afactors = af;
        adenom = ad;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(" + num.toString() + ")");
        sb.append(" / ");
        sb.append("(" + den.toString() + ")");
        sb.append(" =\n");
        boolean first = true;
        for (int i = 0; i < cfactors.size(); i++ ) {
            C cp = cfactors.get(i);
            if (first) {
                first = false;
            } else {
                sb.append(" + ");
            }
            sb.append("("+cp.toString()+")");
            GenPolynomial<C> p = cdenom.get(i);
            sb.append(" log( "+p.toString()+")");
        }
        if ( !first && afactors.size() > 0 ) {
            sb.append(" + ");
        }
        first = true;
        for (int i = 0; i < afactors.size(); i++ ) {
            if (first) {
                first = false;
            } else {
                sb.append(" + ");
            }
            AlgebraicNumber<C> ap =  afactors.get(i);
            AlgebraicNumberRing<C> ar = ap.factory();
            //sb.append(" ## over " + ap.factory() + "\n");
            GenPolynomial<AlgebraicNumber<C>> p = adenom.get(i);
            if ( p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2 ) {
                sb.append("sum_("+ar.getGenerator()+ " in ");
                sb.append("rootOf(" + ar.modul  +") ) ");
            } else {
                //sb.append("sum_("+ar+") ");
            }
            sb.append("(" + ap.toString() + ")");
            sb.append(" log( " + p.toString() + ")");
        }
        return sb.toString();
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    //@Override
    public String toStringX() {
        StringBuffer sb = new StringBuffer();
        sb.append("(" + num.toString() + ")");
        sb.append(" / ");
        sb.append("(" + den.toString() + ")");
        sb.append(" =\n");
        boolean first = true;
        for (C cp : cfactors) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(cp.toString());
        }
        if ( !first) {
            sb.append(" linear denominators: ");
        }
        first = true;
        for (GenPolynomial<C> cp : cdenom) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(cp.toString());
        }
        if ( !first) {
            sb.append("; ");
        }
        first = true;
        for (AlgebraicNumber<C> ap : afactors) {
            if (first) {
                first = false;
            } else {
                //sb.append(", ");
            }
            sb.append(ap.toString());
            sb.append(" ## over " + ap.factory() + "\n");
        }
        if ( !first) {
            sb.append(" denominators: ");
        }
        first = true;
        for (GenPolynomial<AlgebraicNumber<C>> ap : adenom) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(ap.toString());
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

        sb.append(num.toScript());
        sb.append(" / ");
        sb.append(den.toScript());
        sb.append(" = ");
        boolean first = true;
        for (C cp : cfactors) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(cp.toScript());
        }
        if ( !first) {
            sb.append(" linear denominators: ");
        }
        first = true;
        for (GenPolynomial<C> cp : cdenom) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(cp.toScript());
        }
        if ( !first) {
            sb.append(", ");
        }
        first = true;
        for (AlgebraicNumber<C> ap : afactors) {
            if (first) {
                first = false;
            } else {
                //sb.append(", ");
            }
            sb.append(ap.toScript());
            sb.append(" ## over " + ap.toScriptFactory() + "\n");
        }
        sb.append(" denominators: ");
        first = true;
        for (GenPolynomial<AlgebraicNumber<C>> ap : adenom) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(ap.toScript());
        }
        return sb.toString();
    }


    /**
     * Hash code for this Factors.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = num.hashCode();
        h = h * 37 + den.hashCode();
        h = h * 37 + cfactors.hashCode();
        h = h * 37 + cdenom.hashCode();
        h = h * 37 + afactors.hashCode();
        h = h * 37 + adenom.hashCode();
        return h;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (!(B instanceof PartialFraction)) {
            return false;
        }
        PartialFraction<C> a = null;
        try {
            a = (PartialFraction<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (a == null) {
            return false;
        }
        boolean t = num.equals(a.num) && den.equals(a.den);
        if ( ! t ) {
            return t;
        }
        t = cfactors.equals(a.cfactors);
        if ( ! t ) {
            return t;
        }
        t = cdenom.equals(a.cdenom);
        if ( ! t ) {
            return t;
        }
        t = afactors.equals(a.afactors);
        if ( ! t ) {
            return t;
        }
        t = adenom.equals(a.adenom);
        return t;
    }

}
