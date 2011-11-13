/*
 * $Id: Selector.java 2208 2008-11-15 18:47:54Z kredel $
 */

package edu.jas.structure;


/**
 * Selector interface.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface Selector<C extends RingElem<C>> {


    /**
     * Select.
     * @return true, if the element is selected, otherwise false.
     */
    public boolean select(C c);

}
