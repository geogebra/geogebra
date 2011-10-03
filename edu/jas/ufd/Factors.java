/*
 * $Id: Factors.java 3031 2010-03-08 23:18:01Z kredel $
 */

package edu.jas.ufd;


import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;


/**
 * Container for the factors of absolute factorization.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class Factors<C extends GcdRingElem<C>> implements Comparable<Factors<C>>, Serializable {


    /**
     * Original (irreducible) polynomial to be factored with coefficients from C.
     */
    public final GenPolynomial<C> poly;


    /**
     * Algebraic field extension over C. Should be null, if p is absolutely
     * irreducible.
     */
    public final AlgebraicNumberRing<C> afac;


    /**
     * Original polynomial to be factored with coefficients from
     * AlgebraicNumberRing&lt;C&gt;. Should be null, if p is absolutely irreducible.
     */
    public final GenPolynomial<AlgebraicNumber<C>> apoly;


    /**
     * List of factors with coefficients from AlgebraicNumberRing&lt;C&gt;. Should be
     * null, if p is absolutely irreducible.
     */
    public final List<GenPolynomial<AlgebraicNumber<C>>> afactors;


    /**
     * List of factors with coefficients from AlgebraicNumberRing&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * Should be null, if p is absolutely irreducible.
     */
    public final List<Factors<AlgebraicNumber<C>>> arfactors;


    /**
     * Constructor.
     * @param p absolute irreducible GenPolynomial.
     */
    public Factors(GenPolynomial<C> p) {
        this(p, null, null, null, null);
    }


    /**
     * Constructor.
     * @param p irreducible GenPolynomial over C.
     * @param af algebraic extension field of C where p has factors from afact.
     * @param ap GenPolynomial p represented with coefficients from af.
     * @param afact absolute irreducible factors of p with coefficients from af.
     */
    public Factors(GenPolynomial<C> p, AlgebraicNumberRing<C> af, GenPolynomial<AlgebraicNumber<C>> ap,
            List<GenPolynomial<AlgebraicNumber<C>>> afact) {
        this(p, af, ap, afact, null);
    }


    /**
     * Constructor.
     * @param p irreducible GenPolynomial over C.
     * @param af algebraic extension field of C where p has factors from afact.
     * @param ap GenPolynomial p represented with coefficients from af.
     * @param afact absolute irreducible factors of p with coefficients from af.
     * @param arfact further absolute irreducible factors of p with coefficients from extensions of af.
     */
    public Factors(GenPolynomial<C> p, AlgebraicNumberRing<C> af, GenPolynomial<AlgebraicNumber<C>> ap,
            List<GenPolynomial<AlgebraicNumber<C>>> afact, List<Factors<AlgebraicNumber<C>>> arfact) {
        poly = p;
        afac = af;
        apoly = ap;
        afactors = afact;
        arfactors = arfact;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(poly.toString());
        if (afac == null) {
            return sb.toString();
        }
        sb.append(" = ");
        boolean first = true;
        for (GenPolynomial<AlgebraicNumber<C>> ap : afactors) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(ap.toString());
        }
        sb.append("\n  ## over " + afac.toString() + "\n");
        if (arfactors == null) {
            return sb.toString();
        }
        first = true;
        for (Factors<AlgebraicNumber<C>> arp : arfactors) {
            if (first) {
                first = false;
            } else {
                sb.append(",\n");
            }
            sb.append(arp.toString());
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
        //sb.append(poly.toScript());
        if (afac == null) {
            return sb.toString();
        }
        //sb.append(" =\n");
        boolean first = true;
        for (GenPolynomial<AlgebraicNumber<C>> ap : afactors) {
            if (first) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            //sb.append("( " + ap.toScript() + " )");
            sb.append(ap.toScript());
        }
        sb.append("   ## over " + afac.toScript() + "\n");
        if (arfactors == null) {
            return sb.toString();
        }
        first = true;
        for (Factors<AlgebraicNumber<C>> arp : arfactors) {
            if (first) {
                first = false;
            } else {
                sb.append("\n * ");
            }
            sb.append(arp.toScript());
        }
        return sb.toString();
    }


    /**
     * Hash code for this Factors.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = poly.hashCode();
        if (afac == null) {
            return h;
        }
        h = (h << 27);
        h += afac.hashCode();
        if ( afactors != null ) {
            h = (h << 27);
            h += afactors.hashCode();
        }
        if ( arfactors != null ) {
            h = (h << 27);
            h += arfactors.hashCode();
        }
        return h;
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (!(B instanceof Factors)) {
            return false;
        }
        Factors<C> a = null;
        try {
            a = (Factors<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (a == null) {
            return false;
        }
        return this.compareTo(a) == 0;
    }


    /**
     * Comparison.
     * @param facs factors container.
     * @return sign(this.poly-facs.poly) lexicographic &gt;
     *         sign(afac.modul-facs.afac.modul) 
     *         lexicographic &gt; afactors.compareTo(facs.afactors)
     *         lexicographic &gt; arfactors[i].compareTo(facs.arfactors[i])
     */
    public int compareTo(Factors<C> facs) {
        int s = poly.compareTo(facs.poly);
        //System.out.println("s1 = " + s); 
        if (s != 0) {
            return s;
        }
        if (afac == null) {
            return -1;
        }
        if (facs.afac == null) {
            return +1;
        }
        s = afac.modul.compareTo(facs.afac.modul);
        //System.out.println("s2 = " + s); 
        if ( s != 0 ) {
            return s;
        }
        GenPolynomialRing<AlgebraicNumber<C>> ar = afactors.get(0).ring;
        GenPolynomialRing<AlgebraicNumber<C>> br = facs.afactors.get(0).ring;
        PolynomialList<AlgebraicNumber<C>> ap = new PolynomialList<AlgebraicNumber<C>>(ar,afactors);
        PolynomialList<AlgebraicNumber<C>> bp = new PolynomialList<AlgebraicNumber<C>>(br,facs.afactors);
        s = ap.compareTo(bp);
        //System.out.println("s3 = " + s); 
        if ( s != 0 ) {
            return s;
        }
        if (arfactors == null && arfactors == null) {
            return 0;
        }
        if (arfactors == null) {
            return -1;
        }
        if (facs.arfactors == null) {
            return +1;
        }
        // lexicographic (?)
        int i = 0;
        for (Factors<AlgebraicNumber<C>> arp : arfactors) {
            if ( i >= facs.arfactors.size() ) {
                return +1;
            }
            Factors<AlgebraicNumber<C>> brp = facs.arfactors.get(i);
            //System.out.println("arp = " + arp); 
            //System.out.println("brp = " + brp); 
            s = arp.compareTo(brp);
            //System.out.println("s4 = " + s); 
            if ( s != 0 ) {
                return s;
            }
            i++;
        }
        if ( i < facs.arfactors.size() ) {
            return -1;
        }
        return 0;
    }


    /**
     * Find largest extension field.
     * @return largest extension field or null if no extension field
     */
    @SuppressWarnings("unchecked")
    public AlgebraicNumberRing<C> findExtensionField() {
        if (afac == null) {
            return null;
        }
        if (arfactors == null) {
            return afac;
        }
        AlgebraicNumberRing<C> arr = afac;
        int depth = 1;
        for (Factors<AlgebraicNumber<C>> af : arfactors) {
            AlgebraicNumberRing<AlgebraicNumber<C>> aring = af.findExtensionField();
            if (aring == null) {
                continue;
            }
            int d = aring.depth();
            if (d > depth) {
                depth = d;
                arr = (AlgebraicNumberRing<C>) (Object) aring;
            }
        }
        return arr;
    }


    /**
     * Get the list of factors at one level.
     * @return list of algebraic factors 
     */
    public List<GenPolynomial<AlgebraicNumber<C>>> getFactors() {
        List<GenPolynomial<AlgebraicNumber<C>>> af = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if ( afac == null ) {
            return af;
        }
        af.addAll(afactors);
        if ( arfactors == null ) {
            return af;
        }
        for (Factors<AlgebraicNumber<C>> arp : arfactors) {
            af.add( arp.poly );
        }
        return af;
    }


    /**
     * Get the factor for polynomial.
     * @return algebraic factor 
     */
    public Factors<AlgebraicNumber<C>> getFactor(GenPolynomial<AlgebraicNumber<C>> p) {
        if ( afac == null ) {
            return null;
        }
        for (Factors<AlgebraicNumber<C>> arp : arfactors) {
            if ( p.equals( arp.poly ) ) {
                return arp;
            }
        }
        return null;
    }

}
