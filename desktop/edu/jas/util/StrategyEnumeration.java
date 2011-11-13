/*
 * $Id: StrategyEnumeration.java 2771 2009-08-05 20:10:49Z kredel $
 */

package edu.jas.util;


/**
 * StrategyEnumeration. This class names possible / implemented strategies for
 * thread pools.
 * @author Heinz Kredel.
 */

public final class StrategyEnumeration {


    public static final StrategyEnumeration FIFO = new StrategyEnumeration();


    public static final StrategyEnumeration LIFO = new StrategyEnumeration();


    private StrategyEnumeration() {
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        if (this == FIFO) {
            return "FIFO strategy";
        } else {
            return "LIFO strategy";
        }
    }

}
