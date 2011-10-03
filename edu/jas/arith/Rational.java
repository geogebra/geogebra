/*
 * $Id: Rational.java 2939 2009-12-30 08:56:43Z kredel $
 */

package edu.jas.arith;


/**
 * Interface with method to get a BigRational (approximation).
 * @author Heinz Kredel
 */

public interface Rational {


    /**
     * Return a BigRational approximation of this Element.
     * @return a BigRational approximation of this.
     */
    public BigRational getRational();

}
