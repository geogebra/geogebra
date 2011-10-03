/*
 * $Id: PseudoReduction.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial pseudo reduction interface.
 * Defines additionaly normalformFactor.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */

public interface PseudoReduction<C extends RingElem<C>> 
                 extends Reduction<C> {


    /**
     * Normalform with multiplication factor.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and 
               mf as multiplication factor for Ap.
     */
    public PseudoReductionEntry<C> 
           normalformFactor( List<GenPolynomial<C>> Pp, 
                             GenPolynomial<C> Ap );

}
