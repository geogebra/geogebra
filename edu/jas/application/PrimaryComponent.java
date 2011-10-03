/*
 * $Id: PrimaryComponent.java 3155 2010-05-25 20:10:18Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;

import edu.jas.structure.GcdRingElem;


/**
 * Container for primary components of ideals.
 * @author Heinz Kredel
 */
public class PrimaryComponent<C extends GcdRingElem<C>> implements Serializable {


    /**
     * The primary ideal.
     */
    public final Ideal<C> primary;


    /**
     * The associated prime ideal.
     */
    public final IdealWithUniv<C> prime;


    /**
     * The exponent of prime for primary.
     */
    protected int exponent;


    /**
     * Constructor not for use.
     */
    protected PrimaryComponent() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param q the primary ideal
     * @param p the prime ideal.
     */
    protected PrimaryComponent(Ideal<C> q, IdealWithUniv<C> p) {
        this(q, p, -1);
    }


    /**
     * Constructor.
     * @param q the primary ideal
     * @param p the prime ideal.
     * @param e the exponent of p for q.
     */
    protected PrimaryComponent(Ideal<C> q, IdealWithUniv<C> p, int e) {
        primary = q;
        prime = p;
        exponent = e;
    }


    /**
     * Get exponent.
     * @return exponent.
     */
    public int getExponent() {
        return exponent;
    }


    /**
     * Set exponent.
     * @param e the exponent.
     */
    public void setExponent(int e) {
        exponent = e;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = "\nprimary:\n" + primary.toString() + "\nprime:\n" + prime.toString();
        if (exponent < 0) {
            return s;
        } else {
            return s + "\nexponent:\n" + exponent;
        }
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        String s = primary.toScript() + ",  " + prime.toString();
        if (exponent < 0) {
            return s;
        } else {
            return s + ", " + exponent;
        }
    }

}
