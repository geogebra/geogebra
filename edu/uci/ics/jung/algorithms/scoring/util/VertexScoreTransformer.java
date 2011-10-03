/*
 * Created on Jul 18, 2008
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

import edu.uci.ics.jung.algorithms.scoring.VertexScorer;

/**
 * A Transformer convenience wrapper around VertexScorer.
 */
public class VertexScoreTransformer<V, S> implements Transformer<V, S>
{
    /**
     * The VertexScorer instance that provides the values returned by <code>transform</code>.
     */
    protected VertexScorer<V,S> vs;

    /**
     * Creates an instance based on the specified VertexScorer.
     */
    public VertexScoreTransformer(VertexScorer<V,S> vs)
    {
        this.vs = vs;
    }

    /**
     * Returns the score for this vertex.
     */
    public S transform(V v)
    {
        return vs.getVertexScore(v);
    }

}
