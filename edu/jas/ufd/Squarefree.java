/*
 * $Id: Squarefree.java 3068 2010-04-11 17:27:16Z kredel $
 */

package edu.jas.ufd;


import java.util.List;
import java.util.SortedMap;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Squarefree decomposition interface.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @usage To create objects that implement the <code>Squarefree</code>
 *        interface use the <code>SquarefreeFactory</code>. It will select an
 *        appropriate implementation based on the types of polynomial
 *        coefficients C. To obtain an implementation use
 *        <code>getImplementation()</code>, it returns an object of a class
 *        which extends the <code>SquarefreeAbstract</code> class which
 *        implements the <code>Squarefree</code> interface.
 * 
 * <pre>
 * Squarefree&lt;CT&gt; engine;
 * engine = SquarefreeFactory.&lt;CT&gt; getImplementation(cofac);
 * c = engine.squarefreeFactors(a);
 * </pre>
 * 
 * For example, if the coefficient type is BigInteger, the usage looks like
 * 
 * <pre>
 * BigInteger cofac = new BigInteger();
 * Squarefree&lt;BigInteger&gt; engine;
 * engine = SquarefreeFactory.getImplementation(cofac);
 * Sm = engine.sqaurefreeFactors(poly);
 * </pre>
 * 
 * @see edu.jas.ufd.SquarefreeFactory#getImplementation
 */

public interface Squarefree<C extends GcdRingElem<C>> {


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P);


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P);


    /**
     * GenPolynomial list test if squarefree.
     * @param L list of GenPolynomial.
     * @return true if each P in L is squarefree, else false.
     */
    public boolean isSquarefree(List<GenPolynomial<C>> L);


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P);


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, List<GenPolynomial<C>> F);


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, SortedMap<GenPolynomial<C>, Long> F);


    /**
     * GenPolynomial squarefree and co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     *         a in A there exists b in B with b|a and each b in B is
     *         squarefree. B does not contain zero or constant polynomials.
     */
    public List<GenPolynomial<C>> coPrimeSquarefree(List<GenPolynomial<C>> A);


    /**
     * GenPolynomial squarefree and co-prime list.
     * @param a polynomial.
     * @param P squarefree co-prime list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for non-constant a
     *         there exists b in P with b|a. B does not contain zero or constant
     *         polynomials.
     */
    public List<GenPolynomial<C>> coPrimeSquarefree(GenPolynomial<C> a, List<GenPolynomial<C>> P);


    /**
     * Test if list of GenPolynomials is squarefree and co-prime.
     * @param B list of GenPolynomials.
     * @return true, if for all b != c in B gcd(b,c) = 1 and 
     *          each b in B is squarefree, else false. 
     */
    public boolean isCoPrimeSquarefree(List<GenPolynomial<C>> B);

}
