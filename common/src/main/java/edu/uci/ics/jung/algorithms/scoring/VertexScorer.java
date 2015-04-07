/*
 * Created on Jul 6, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring;


/**
 * An interface for algorithms that assign scores to vertices.
 *
 * @param <V> the vertex type
 * @param <S> the score type
 */
public interface VertexScorer<V, S>
{
    /**
     * Returns the algorithm's score for this vertex.
     * @return the algorithm's score for this vertex
     */
    public S getVertexScore(V v);
}
