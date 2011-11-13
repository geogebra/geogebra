/*
 * $Id: FieldElem.java 1708 2008-02-24 17:28:36Z kredel $
 */

package edu.jas.structure;


/**
 * Field element interface.
 * Empty interface since inverse is already in RingElem.
 * @param <C> field element type
 * @author Heinz Kredel
 */

public interface FieldElem<C extends FieldElem<C>> 
                 extends RingElem<C> {

}
