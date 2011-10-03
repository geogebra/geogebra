/*
 * $Id: AbelianGroupFactory.java 1259 2007-07-29 10:18:54Z kredel $
 */

package edu.jas.structure;


/**
 * Abelian group factory interface.
 * Defines get zero.
 * @author Heinz Kredel
 */

public interface AbelianGroupFactory<C extends AbelianGroupElem<C>> 
                 extends ElemFactory<C> {


    /**
     * Get the constant zero for the AbelianGroupElem.
     * @return 0.
     */
    public C getZERO();


}
