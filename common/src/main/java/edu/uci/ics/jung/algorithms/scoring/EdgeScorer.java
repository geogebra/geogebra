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
 * An interface for algorithms that assign scores to edges.
 *
 * @param <E> the edge type
 * @param <S> the score type
 */
public interface EdgeScorer<E, S>
{
    /**
     * Returns the algorithm's score for this edge.
     * @return the algorithm's score for this edge
     */
    public S getEdgeScore(E e);
}
