/*
 * $Id: ElemFactory.java 3193 2010-06-26 20:10:37Z kredel $
 */

package edu.jas.structure;

import java.math.BigInteger;
import java.io.Reader;
import java.io.Serializable;
import java.util.Random;
import java.util.List;

/**
 * Element factory interface.
 * Defines embedding of integers, parsing and random element construction.
 * @author Heinz Kredel
 */

public interface ElemFactory<C extends Element<C>> extends Serializable {


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     */
    public List<C> generators();


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     */
    public boolean isFinite();


    /**
     * Get the Element for a.
     * @param a long
     * @return element corresponding to a.
     */
    public C fromInteger(long a);


    /**
     * Get the Element for a.
     * @param a java.math.BigInteger.
     * @return element corresponding to a.
     */
    public C fromInteger(BigInteger a);


    /**
     * Generate a random Element with size less equal to n.
     * @param n
     * @return a random element.
     */
    public C random(int n);


    /**
     * Generate a random Element with size less equal to n.
     * @param n
     * @param random is a source for random bits.
     * @return a random element.
     */
    public C random(int n, Random random);


    /**
     * Create a copy of Element c.
     * @param c
     * @return a copy of c.
     */
    public C copy(C c);


    /**
     * Parse from String.
     * @param s String.
     * @return a Element corresponding to s.
     */
    public C parse(String s);


    /**
     * Parse from Reader.
     * @param r Reader.
     * @return the next Element found on r.
     */
    public C parse(Reader r);


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     */
    public String toScript();

}
