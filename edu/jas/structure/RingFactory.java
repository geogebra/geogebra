/*
 * $Id: RingFactory.java 1259 2007-07-29 10:18:54Z kredel $
 */

package edu.jas.structure;


/**
 * Ring factory interface.
 * Defines test for field and access of characteristic.
 * @author Heinz Kredel
 */

public interface RingFactory<C extends RingElem<C>> 
    extends AbelianGroupFactory<C>, MonoidFactory<C> {


    /**
     * Query if this ring is a field.
     * May return false if it is to hard to determine if this ring is a field.
     * @return true if it is known that this ring is a field, else false.
     */
    public boolean isField();


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic();

}
