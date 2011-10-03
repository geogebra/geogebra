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

import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns a score to each vertex equal to its degree.
 *
 * @param <V> the vertex type
 */
public class DegreeScorer<V> implements VertexScorer<V,Integer>
{
	/**
	 * The graph for which scores are to be generated.
	 */
    protected Hypergraph<V,?> graph;
    
    /**
     * Creates an instance for the specified graph.
     * @param graph the input graph
     */
    public DegreeScorer(Hypergraph<V,?> graph)
    {
        this.graph = graph;
    }
    
    /**
     * Returns the degree of the vertex.
     * @return the degree of the vertex
     */
    public Integer getVertexScore(V v) 
    {
        return graph.degree(v); 
    }
}
