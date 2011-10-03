/*
 * $Id: StarRingElem.java 1708 2008-02-24 17:28:36Z kredel $
 */

package edu.jas.structure;


/**
 * Star ring element interface.
 * Defines norm and conjugation.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface StarRingElem<C extends StarRingElem<C>> 
                 extends RingElem<C> {

    /**
     * Conjugate of this.
     * @return conj(this).
     */
    public C conjugate();


    /**
     * Norm of this.
     * @return norm(this).
     */
    public C norm();

}
