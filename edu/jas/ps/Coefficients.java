/*
 * $Id: Coefficients.java 2208 2008-11-15 18:47:54Z kredel $
 */

package edu.jas.ps;


import java.util.HashMap;

import edu.jas.structure.RingElem;


/**
 * Abstract class for generating functions for coefficients of power series.
 * Was an interface, now this class handles the caching itself.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public abstract class Coefficients<C extends RingElem<C>> {


    /**
     * Cache for already computed coefficients.
     */
    public final HashMap<Integer,C> coeffCache;


    /**
     * Public no arguments constructor.
     */
    public Coefficients() {
        this( new HashMap<Integer,C>() );
    }


    /**
     * Public constructor with pre-filled cache.
     * @param cache pre-filled coefficient cache.
     */
    public Coefficients(HashMap<Integer,C> cache) {
        coeffCache = cache;
    }


    /**
     * Get cached coefficient or generate coefficient.
     * @param index of requested coefficient.
     * @return coefficient at index.
     */
    public C get(int index) {
        if ( coeffCache == null ) {
            return generate( index );
        }
        Integer i = index;
        C c = coeffCache.get( i );
        if ( c != null ) {
            return c;
        }
        c = generate( index );
        coeffCache.put( i, c );
        return c;
    }


    /**
     * Generate coefficient.
     * @param index of requested coefficient.
     * @return coefficient at index.
     */
    protected abstract C generate(int index);
 
}
