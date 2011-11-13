/*
 * $Id: Modular.java 2934 2009-12-29 14:34:30Z kredel $
 */

package edu.jas.arith;


/**
 * Interface with getInteger and getSymmetricInteger methods.
 * @author Heinz Kredel
 */

public interface Modular {


    /**
     * Return a BigInteger from this Element.
     * @return a BigInteger of this.
     */
    public BigInteger getInteger();


    /**
     * Return a symmetric BigInteger from this Element.
     * @return a symmetric BigInteger of this.
     */
    public BigInteger getSymmetricInteger();

}
