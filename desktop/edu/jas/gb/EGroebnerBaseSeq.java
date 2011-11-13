/*
 * $Id: EGroebnerBaseSeq.java 2416 2009-02-07 13:24:32Z kredel $
 */

package edu.jas.gb;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;


/**
 * E-Groebner Base sequential algorithm.
 * Nearly empty class, only the e-reduction 
 * is used instead of d-reduction.
 * <b>Note:</b> Minimal reduced GBs are again unique.
 * see BWK, section 10.1.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class EGroebnerBaseSeq<C extends RingElem<C>> 
       extends DGroebnerBaseSeq<C> 
       /*implements GroebnerBase<C>*/ {


    private static final Logger logger = Logger.getLogger(EGroebnerBaseSeq.class);
    private final boolean debug = true; //logger.isDebugEnabled();



    /**
     * Reduction engine.
     */
    protected EReduction<C> red;  // shadow super.red


    /**
     * Constructor.
     */
    public EGroebnerBaseSeq() {
        this( new EReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red E-Reduction engine
     */
    public EGroebnerBaseSeq(EReductionSeq<C> red) {
        super(red);
        this.red = red;
    }


}
