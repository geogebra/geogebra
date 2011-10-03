/*
 * $Id: ModulElem.java 1708 2008-02-24 17:28:36Z kredel $
 */

package edu.jas.structure;


import java.util.List;


/**
 * Module element interface.
 * Defines scalar operations.
 * @param <M> module type
 * @param <C> scalar type
 * @author Heinz Kredel
 */
public interface ModulElem<M extends ModulElem<M,C>,
                           C extends RingElem<C>> 
                 extends AbelianGroupElem< M > {

    /**
     * Scalar multiplication. Multiply this by a scalar.
     * @param s scalar
     * @return this * s.
     */
    public M scalarMultiply(C s);


    /**
     * Linear combination.
     * @param a scalar
     * @param b module element
     * @param s scalar
     * @return a * b + this * s.
     */
    public M linearCombination(C a, M b, C s);


    /**
     * Linear combination.
     * @param b module element
     * @param s scalar
     * @return b + this * s.
     */
    public M linearCombination(M b, C s);


    /**
     * Scalar product. Multiply two vectors to become a scalar.
     * @param b module element
     * @return this * b, a scalar.
     */
    public C scalarProduct(M b);


    /**
     * Scalar product. Multiply this vectors by list of vectors to become a vector.
     * @param b list of module elements
     * @return this * b, a list of scalars, a module element.
     */
    public M scalarProduct(List<M> b);

}
