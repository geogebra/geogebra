/*
 * Created on Jul 11, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring.util;

import org.apache.commons.collections15.Transformer;

/**
 * A <code>Transformer<VEPair,Number></code> that delegates its operation to a
 * <code>Transformer<E,Number></code>.  Mainly useful for technical reasons inside 
 * AbstractIterativeScorer; in essence it allows the edge weight instance 
 * variable to be of type <code>VEPair,W</code> even if the edge weight 
 * <code>Transformer</code> only operates on edges.
 */
public class DelegateToEdgeTransformer<V,E> implements
        Transformer<VEPair<V,E>,Number>
{
	/**
	 * The transformer to which this instance delegates its function.
	 */
    protected Transformer<E,? extends Number> delegate;
    
    /**
     * Creates an instance with the specified delegate transformer.
     * @param delegate the Transformer to which this instance will delegate
     */
    public DelegateToEdgeTransformer(Transformer<E,? extends Number> delegate)
    {
        this.delegate = delegate;
    }
    
    /**
     * @see Transformer#transform(Object)
     */
    public Number transform(VEPair<V,E> arg0)
    {
        return delegate.transform(arg0.getE());
    }

}
