/*
 * $Id: RealRoots.java 2939 2009-12-30 08:56:43Z kredel $
 */

package edu.jas.root;


import java.util.List;

import edu.jas.arith.Rational;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Real roots interface.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public interface RealRoots<C extends RingElem<C> & Rational> {


    /**
     * Real root bound. With f(M) * f(-M) != 0.
     * @param f univariate polynomial.
     * @return M such that -M &lt; root(f) &gt; M.
     */
    public C realRootBound(GenPolynomial<C> f);


    /**
     * Isolating intervals for the real roots.
     * @param f univariate polynomial.
     * @return a list of isolating intervalls for the real roots of f.
     */
    public List<Interval<C>> realRoots(GenPolynomial<C> f);


    /**
     * Isolating intervals for the real roots.
     * @param f univariate polynomial.
     * @param eps requested intervals length.
     * @return a list of isolating intervals v such that |v| &lt; eps.
     */
    public List<Interval<C>> realRoots(GenPolynomial<C> f, C eps);


    /**
     * Sign changes on interval bounds.
     * @param iv root isolating interval with f(left) * f(right) != 0.
     * @param f univariate polynomial.
     * @return true if f(left) * f(right) &lt; 0, else false
     */
    public boolean signChange(Interval<C> iv, GenPolynomial<C> f);


    /**
     * Number of real roots in interval.
     * @param iv interval with f(left) * f(right) != 0.
     * @param f univariate polynomial.
     * @return number of real roots of f in I.
     */
    public long realRootCount(Interval<C> iv, GenPolynomial<C> f);


    /**
     * Refine interval.
     * @param iv root isolating interval with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return a new interval v such that |v| &lt; eps.
     */
    public Interval<C> refineInterval(Interval<C> iv, GenPolynomial<C> f, C eps);


    /**
     * Refine intervals.
     * @param V list of isolating intervals with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param eps requested intervals length.
     * @return a list of new intervals v such that |v| &lt; eps.
     */
    public List<Interval<C>> refineIntervals(List<Interval<C>> V, GenPolynomial<C> f, C eps);


    /**
     * Real algebraic number sign.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return sign(g(v)), with v a new interval contained in iv such that g(v) !=
     *         0.
     */
    public int realSign(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g);


    /**
     * Real algebraic number magnitude.
     * @param iv root isolating interval for f, with f(left) * f(right) &lt; 0.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for interval length.
     * @return g(iv).
     */
    public C realMagnitude(Interval<C> iv, GenPolynomial<C> f, GenPolynomial<C> g, C eps);

}
