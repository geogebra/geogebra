/*
 * $Id: RingElem.java 1737 2008-03-10 21:42:36Z kredel $
 */

package edu.jas.structure;


/**
 * Ring element interface.
 * Combines additive and multiplicative methods.
 * Adds also gcd because of polynomials.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface RingElem<C extends RingElem<C>> 
                 extends AbelianGroupElem<C>, MonoidElem<C> {

    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public C gcd(C b);


    /**
     * Extended greatest common divisor.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public C[] egcd(C b);

}
