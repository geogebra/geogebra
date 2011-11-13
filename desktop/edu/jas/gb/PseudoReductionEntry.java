/*
 * $Id: PseudoReductionEntry.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial reduction container.
 * Used as container for the return value of normalformFactor.
 * @author Heinz Kredel
 */

public class PseudoReductionEntry<C extends RingElem<C>> {

    public final GenPolynomial<C> pol;
    public final C multiplicator;

    public PseudoReductionEntry(GenPolynomial<C> pol,
                                C multiplicator) {
        this.pol = pol;
        this.multiplicator = multiplicator;
    }

}
