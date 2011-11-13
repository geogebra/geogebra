
/*
 * $Id: Monomial.java 2209 2008-11-15 20:08:23Z kredel $
 */

package edu.jas.poly;

import java.util.Map;
import java.util.SortedMap;
import java.util.Iterator;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;


/**
 * Monomial class. 
 * Represents pairs of exponent vectors and coefficients.
 * Adaptor for Map.Entry.
 * @author Heinz Kredel
 */

public final class Monomial<C extends RingElem<C> > {

    /** 
     * Exponent of monomial.
     */
    public final ExpVector e;


    /** 
     * Coefficient of monomial.
     */
    public final C c;


    /** 
     * Constructor of monomial.
     * @param me a MapEntry.
     */
    public Monomial(Map.Entry<ExpVector,C> me){
        this( me.getKey(), me.getValue() );
    }


    /** 
     * Constructor of monomial.
     * @param e exponent.
     * @param c coefficient.
     */
    public Monomial(ExpVector e, C c) {
        this.e = e;
        this.c = c;
    }


    /** 
     * Getter for exponent.
     * @return exponent.
     */
    public ExpVector exponent() {
        return e;
    }


    /** 
     * Getter for coefficient.
     * @return coefficient.
     */
    public C coefficient() {
        return c;
    }

    /**
     * String representation of Monomial.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return c.toString() + " " + e.toString();
    }

}
