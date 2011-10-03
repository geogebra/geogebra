/*
 * $Id: Polynomial.java 1801 2008-05-19 20:50:39Z kredel $
 */

package edu.jas.poly;


import java.util.Map;
import java.util.Iterator;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Polynomial interface.
 * Adds methods specific to polynomials.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface Polynomial<C extends RingElem<C>> 
                 extends RingElem< Polynomial<C> > {

    /**
     * Leading monomial.
     * @return first map entry.
     */
    public Map.Entry<ExpVector,C> leadingMonomial();


    /**
     * Leading exponent vector.
     * @return first exponent.
     */
    public ExpVector leadingExpVector();


    /**
     * Leading base coefficient.
     * @return first coefficient.
     */
    public C leadingBaseCoefficient();


    /**
     * Trailing base coefficient.
     * @return coefficient of constant term.
     */
    public C trailingBaseCoefficient();


    /**
     * Reductum.
     * @return this - leading monomial.
     */
    public Polynomial<C> reductum();


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements and multiply by x_j^k.
     * @param pfac extended polynomial ring factory (by i variables).
     * @param j index of variable to be used for multiplication.
     * @param k exponent for x_j.
     * @return extended polynomial.
     */
    public Polynomial<C> extend(PolynomialRing<C> pfac, int j, long k);


    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i elements of each ExpVector.
     * @param pfac contracted polynomial ring factory (by i variables).
     * @return Map of exponents and contracted polynomials.
     * <b>Note:</b> could return SortedMap
     */
    public Map<ExpVector,Polynomial<C>> contract(PolynomialRing<C> pfac);


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial with reversed variables.
     */
    public Polynomial<C> reverse(PolynomialRing<C> oring);


    /**
     * Iterator over coefficients. 
     * @return val.values().iterator().
     */
    public Iterator<C> coefficientIterator();


    /**
     * Iterator over exponents. 
     * @return val.keySet().iterator().
     */
    public Iterator<ExpVector> exponentIterator();


    /**
     * Iterator over monomials. 
     * @return a PolyIterator.
     */
    public Iterator<Monomial<C>> monomialIterator();


}
