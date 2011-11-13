/*
 * $Id: BinaryFunctor.java 2955 2010-01-01 12:50:44Z kredel $
 */

package edu.jas.structure;


/**
 * Binary functor interface.
 * @param <C1> element type
 * @param <C2> element type
 * @param <D> element type
 * @author Heinz Kredel
 */

public interface BinaryFunctor<C1 extends Element<C1>,
                               C2 extends Element<C2>,
                               D extends Element<D> > {


    /**
     * Evaluate.
     * @return evaluated element.
     */
    public D eval(C1 c1, C2 c2);

}
