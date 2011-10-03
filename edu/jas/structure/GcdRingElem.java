/*
 * $Id: GcdRingElem.java 1708 2008-02-24 17:28:36Z kredel $
 */

package edu.jas.structure;


/**
 * Gcd ring element interface.
 * Adds greatest common divisor and extended greatest common divisor.
 * Empty interface since gcd and egcd is now in RingElem.
 * @param <C> gcd element type
 * @author Heinz Kredel
 */

public interface GcdRingElem<C extends GcdRingElem<C>> 
                 extends RingElem<C> {

    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
    public C gcd(C b);
     */


    /**
     * Extended greatest common divisor.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
    public C[] egcd(C b);
     */

}
