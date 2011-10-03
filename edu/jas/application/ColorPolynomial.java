/*
 * $Id: ColorPolynomial.java 2043 2008-08-10 16:22:19Z kredel $
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Colored Polynomials with green, red and white coefficients. Not implementing
 * RingElem. <b>Note:</b> not general purpose, use only in comprehensive GB.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ColorPolynomial<C extends RingElem<C>>
    /* implements RingElem< ColorPolynomial<C> > */ {


    /**
     * The part with green (= zero) terms and coefficients.
     */
    public final GenPolynomial<GenPolynomial<C>> green;


    /**
     * The part with red (= non zero) terms and coefficients.
     */
    public final GenPolynomial<GenPolynomial<C>> red;


    /**
     * The part with white (= unknown color) terms and coefficients.
     */
    public final GenPolynomial<GenPolynomial<C>> white;


    /**
     * The constructor creates a colored polynomial from the colored parts.
     * @param g green colored terms and coefficients.
     * @param r red colored terms and coefficients.
     * @param w white colored terms and coefficients.
     */
    public ColorPolynomial(GenPolynomial<GenPolynomial<C>> g,
            GenPolynomial<GenPolynomial<C>> r, GenPolynomial<GenPolynomial<C>> w) {
        if (g == null || r == null || w == null) {
            throw new IllegalArgumentException("g,r,w may not be null");
        }
        green = g;
        red = r;
        white = w;
    }


    /**
     * String representation of GenPolynomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(":green: ");
        s.append(green.toString());
        s.append(" :red: ");
        s.append(red.toString());
        s.append(" :white: ");
        s.append(white.toString());
        return s.toString();
    }


    /**
     * Is this polynomial ZERO.
     * @return true, if there are only green terms, else false.
     */
    public boolean isZERO() {
        return (red.isZERO() && white.isZERO());
    }


    /**
     * Is this polynomial ONE.
     * @return true, if the only non green term is 1, else false.
     */
    public boolean isONE() {
        return ((red.isZERO() && white.isONE()) || (red.isONE() && white.isZERO()));
    }


    /**
     * Is this polynomial equal to other.
     * @param p other polynomial.
     * @return true, if this is equal to other, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object p) {
        ColorPolynomial<C> cp = null;
        try {
            cp = (ColorPolynomial<C>) p;
        } catch (ClassCastException e) {
            return false;
        }
        if (cp == null) {
            return false;
        }
        return (green.equals(cp.green) && red.equals(cp.red) && white.equals(cp.white));
    }


    /**
     * Hash code for this colored polynomial.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = green.hashCode();
        h = h << 11;
        h += red.hashCode();
        h = h << 11;
        h += white.hashCode();
        return h;
    }


    /**
     * Is this polynomial determined.
     * @return true, if there are nonzero red terms or if this == 0, else false.
     */
    public boolean isDetermined() {
        return (!red.isZERO() || white.isZERO());
    }


    /**
     * Check ordering invariants. TT(green) > LT(red) and TT(red) > LT(white).
     * @return true, if all ordering invariants are met, else false.
     */
    public boolean checkInvariant() {
        boolean t = true;
        ExpVector ttg, ltr, ttr, ltw;
        Comparator<ExpVector> cmp;
        if (green.isZERO() && red.isZERO() && white.isZERO()) {
            return true;
        }
        if (green.isZERO() && red.isZERO()) {
            return true;
        }
        if (red.isZERO() && white.isZERO()) {
            return true;
        }

        if (!green.isZERO() && !red.isZERO()) {
            ttg = green.trailingExpVector();
            ltr = red.leadingExpVector();
            cmp = green.ring.tord.getDescendComparator();
            t = t && (cmp.compare(ttg, ltr) < 0);
        }
        if (!red.isZERO() && !white.isZERO()) {
            ttr = red.trailingExpVector();
            ltw = white.leadingExpVector();
            cmp = white.ring.tord.getDescendComparator();
            t = t && (cmp.compare(ttr, ltw) < 0);
        }
        if (red.isZERO() && !green.isZERO() && !white.isZERO()) {
            ttg = green.trailingExpVector();
            ltw = white.leadingExpVector();
            cmp = white.ring.tord.getDescendComparator();
            t = t && (cmp.compare(ttg, ltw) < 0);
        }
        if (!t) {
            System.out.println("not invariant " + this);
            // throw new RuntimeException("test");
        }
        return t;
    }


    /**
     * Get zero condition on coefficients.
     * @return green coefficients.
     */
    public List<GenPolynomial<C>> getGreenCoefficients() {
        Collection<GenPolynomial<C>> c = green.getMap().values();
        return new ArrayList<GenPolynomial<C>>(c);
    }


    /**
     * Get non zero condition on coefficients.
     * @return red coefficients.
     */
    public List<GenPolynomial<C>> getRedCoefficients() {
        Collection<GenPolynomial<C>> c = red.getMap().values();
        return new ArrayList<GenPolynomial<C>>(c);
    }


    /**
     * Get full polynomial.
     * @return sum of all parts.
     */
    public GenPolynomial<GenPolynomial<C>> getPolynomial() {
        GenPolynomial<GenPolynomial<C>> f = green.sum(red).sum(white);
        int s = green.length() + red.length() + white.length();
        int t = f.length();
        if (t != s) {
            throw new RuntimeException("illegal coloring state " + s + " != " + t);
        }
        return f;
    }


    /**
     * Get essential polynomial.
     * @return sum of red and white parts.
     */
    public GenPolynomial<GenPolynomial<C>> getEssentialPolynomial() {
        GenPolynomial<GenPolynomial<C>> f = red.sum(white);
        int s = red.length() + white.length();
        int t = f.length();
        if (t != s) {
            throw new RuntimeException("illegal coloring state " + s + " != " + t);
        }
        return f;
    }


    /**
     * Length of red and white parts.
     * @return length of essential parts.
     */
    public int length() {
        int s = red.length() + white.length();
        return s;
    }


    /**
     * Get leading exponent vector.
     * @return LT of red or white parts.
     */
    public ExpVector leadingExpVector() {
        if (!red.isZERO()) {
            return red.leadingExpVector();
        }
        return white.leadingExpVector();
    }


    /**
     * Get leading monomial.
     * @return LM of red or white parts.
     */
    public Map.Entry<ExpVector, GenPolynomial<C>> leadingMonomial() {
        if (!red.isZERO()) {
            return red.leadingMonomial();
        }
        return white.leadingMonomial();
    }


    /**
     * ColorPolynomial absolute value.
     * @return abs(this).
     */
    public ColorPolynomial<C> abs() {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        int s = green.signum();
        if (s > 0) {
            return this;
        }
        if (s < 0) {
            g = green.negate();
            r = red.negate();
            w = white.negate();
            return new ColorPolynomial<C>(g, r, w);
        }
        // green == 0
        g = green;
        s = red.signum();
        if (s > 0) {
            return this;
        }
        if (s < 0) {
            r = red.negate();
            w = white.negate();
            return new ColorPolynomial<C>(g, r, w);
        }
        // red == 0
        r = red;
        s = white.signum();
        if (s > 0) {
            return this;
        }
        if (s < 0) {
            w = white.negate();
            return new ColorPolynomial<C>(g, r, w);
        }
        // white == 0
        w = white;
        return new ColorPolynomial<C>(g, r, w);
    }


    /**
     * ColorPolynomial summation. <b>Note:</b> green coefficients stay green,
     * all others become white.
     * @param S ColorPolynomial.
     * @return this+S.
     */
    public ColorPolynomial<C> sum(ColorPolynomial<C> S) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green.sum(S.green);
        r = red.ring.getZERO();
        w = getEssentialPolynomial().sum(S.getEssentialPolynomial());
        return new ColorPolynomial<C>(g, r, w);
    }


    /**
     * ColorPolynomial summation.
     * @param s GenPolynomial.
     * @param e exponent vector.
     * @return this+(c e).
     */
    public ColorPolynomial<C> sum(GenPolynomial<C> s, ExpVector e) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green;
        r = red;
        w = white;
        if (green.getMap().keySet().contains(e)) {
            g = green.sum(s, e);
        } else if (red.getMap().keySet().contains(e)) {
            r = red.sum(s, e);
        } else {
            w = white.sum(s, e);
        }
        return new ColorPolynomial<C>(g, r, w);
    }


    /**
     * ColorPolynomial subtraction. <b>Note:</b> green coefficients stay green,
     * all others become white.
     * @param S ColorPolynomial.
     * @return this-S.
     */
    public ColorPolynomial<C> subtract(ColorPolynomial<C> S) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green.subtract(S.green);
        r = red.ring.getZERO();
        w = getEssentialPolynomial().subtract(S.getEssentialPolynomial());
        return new ColorPolynomial<C>(g, r, w);
    }


    /**
     * ColorPolynomial subtract.
     * @param s GenPolynomial.
     * @param e exponent vector.
     * @return this-(c e).
     */
    public ColorPolynomial<C> subtract(GenPolynomial<C> s, ExpVector e) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green;
        r = red;
        w = white;
        if (green.getMap().keySet().contains(e)) {
            g = green.subtract(s, e);
        } else if (red.getMap().keySet().contains(e)) {
            r = red.subtract(s, e);
        } else {
            w = white.subtract(s, e);
        }
        return new ColorPolynomial<C>(g, r, w);
    }


    /**
     * ColorPolynomial multiplication by monomial.
     * @param s Coefficient.
     * @param e Expvector.
     * @return this * (c t).
     */
    public ColorPolynomial<C> multiply(GenPolynomial<C> s, ExpVector e) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green.multiply(s, e);
        r = red.multiply(s, e);
        w = white.multiply(s, e);
        return new ColorPolynomial<C>(g, r, w);
    }


    /**
     * ColorPolynomial multiplication by coefficient.
     * @param s Coefficient.
     * @return this * (s).
     */
    public ColorPolynomial<C> multiply(GenPolynomial<C> s) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green.multiply(s);
        r = red.multiply(s);
        w = white.multiply(s);
        return new ColorPolynomial<C>(g, r, w);
    }


    /**
     * ColorPolynomial division by coefficient.
     * @param s Coefficient.
     * @return this / (s).
     */
    public ColorPolynomial<C> divide(GenPolynomial<C> s) {
        GenPolynomial<GenPolynomial<C>> g, r, w;
        g = green.divide(s);
        r = red.divide(s);
        w = white.divide(s);
        return new ColorPolynomial<C>(g, r, w);
    }


}
