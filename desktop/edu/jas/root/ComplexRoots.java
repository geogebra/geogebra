/*
 * $Id: ComplexRoots.java 2975 2010-01-05 10:21:17Z kredel $
 */

package edu.jas.root;


import java.util.List;

import edu.jas.arith.Rational;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.Complex;
import edu.jas.structure.RingElem;


/**
 * Complex roots interface.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public interface ComplexRoots<C extends RingElem<C> & Rational> {


    /**
     * Root bound. With f(-M + i M) * f(-M - i M) * f(M - i M) * f(M + i M) != 0.
     * @param f univariate polynomial.
     * @return M such that root(f) is contained in the rectangle spanned by M.
     */
    public Complex<C> rootBound(GenPolynomial<Complex<C>> f);


    /**
     * Complex root count of complex polynomial on rectangle.
     * @param rect rectangle.
     * @param a univariate complex polynomial.
     * @return root count of a in rectangle.
     */
    public long complexRootCount(Rectangle<C> rect, GenPolynomial<Complex<C>> a)
            throws InvalidBoundaryException;


    /**
     * List of complex roots of complex polynomial a on rectangle.
     * @param rect rectangle.
     * @param a univariate squarefree complex polynomial.
     * @return list of complex roots.
     */
    public List<Rectangle<C>> complexRoots(Rectangle<C> rect, GenPolynomial<Complex<C>> a)
            throws InvalidBoundaryException;


    /**
     * List of complex roots of complex polynomial.
     * @param a univariate complex polynomial.
     * @return list of complex roots.
     */
    public List<Rectangle<C>> complexRoots(GenPolynomial<Complex<C>> a);


    /**
     * Complex root refinement of complex polynomial a on rectangle.
     * @param rect rectangle containing exactly one complex root.
     * @param a univariate squarefree complex polynomial.
     * @param len rational length for refinement.
     * @return refined complex root.
     */
    public Rectangle<C> complexRootRefinement(Rectangle<C> rect, GenPolynomial<Complex<C>> a, BigRational len)
            throws InvalidBoundaryException;

}
