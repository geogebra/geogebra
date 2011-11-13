/*
 * $Id: Integral.java 2876 2009-11-15 17:15:57Z kredel $
 */

package edu.jas.integrate;


import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.PartialFraction;


/**
 * Container for a rational function integral, polynomial version.
 * integral(num/den) = pol + sum_rat( rat_i/rat_{i+1} ) + sum_log( a_i log ( d_i ) )
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class Integral<C extends GcdRingElem<C>> implements Serializable {


    /**
     * Original numerator polynomial with coefficients from C.
     */
    public final GenPolynomial<C> num;


    /**
     * Original denominator polynomial with coefficients from C.
     */
    public final GenPolynomial<C> den;


    /**
     * Integral of the polynomial part. 
     */
    public final GenPolynomial<C> pol;


    /**
     * Integral of the rational part.
     */
    public final List<GenPolynomial<C>> rational;


    /**
     * Integral of the logarithmic part.
     */
    public final List<LogIntegral<C>> logarithm;


    /**
     * Constructor.
     * @param n numerator GenPolynomial over C.
     * @param d denominator GenPolynomial over C.
     * @param p integral of polynomial part.
     * n/d = 
     */
    public Integral(GenPolynomial<C> n, GenPolynomial<C> d,
            GenPolynomial<C> p) {
        this(n,d,p, new ArrayList<GenPolynomial<C>>() );
    }


    /**
     * Constructor.
     * @param n numerator GenPolynomial over C.
     * @param d denominator GenPolynomial over C.
     * @param p integral of polynomial part.
     * @param rat list of rational integrals.
     * n/d = 
     */
    public Integral(GenPolynomial<C> n, GenPolynomial<C> d,
            GenPolynomial<C> p, 
            List<GenPolynomial<C>> rat) {
        this(n,d,p,rat, new ArrayList<LogIntegral<C>>() );
    }


    /**
     * Constructor.
     * @param n numerator GenPolynomial over C.
     * @param d denominator GenPolynomial over C.
     * @param p integral of polynomial part.
     * @param rat list of rational integrals.
     * @param log list of logarithmic part.
     * n/d = 
     */
    public Integral(GenPolynomial<C> n, GenPolynomial<C> d,
            GenPolynomial<C> p, 
            List<GenPolynomial<C>> rat,
            List<LogIntegral<C>> log) {
        num = n;
        den = d;
        pol = p;
        rational = rat;
        logarithm = log;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("integral( (" + num.toString() );
        sb.append(") / (");
        sb.append(den.toString() + ") )");
        sb.append(" =\n");
        if ( ! pol.isZERO() ) {
            sb.append(pol.toString());
        }
        boolean first = true;
        if ( rational.size() != 0 ) {
            if ( ! pol.isZERO() ) {
               sb.append(" + ");
            }
            for ( int i = 0; i < rational.size(); i++ ) {
               if ( first ) {
                   first = false;
               } else {
                   sb.append(" + ");
               }
               sb.append("("+ rational.get(i++)+")/(");
               sb.append(rational.get(i)+")");
            }
        }
        if ( logarithm.size() != 0 ) {
            if ( !pol.isZERO() || rational.size() != 0 ) {
              sb.append(" + ");
           }
           first = true;
           for ( LogIntegral<C> pf : logarithm ) {
               if ( first ) {
                   first = false;
               } else {
                   sb.append(" + ");
               }
               sb.append(pf);
           }
           sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * Hash code for Integral.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = num.hashCode();
        h = h * 37 + den.hashCode();
        h = h * 37 + pol.hashCode();
        h = h * 37 + rational.hashCode();
        h = h * 37 + logarithm.hashCode();
        return h;
    }

}
