/*
 * Created on Jul 12, 2007
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

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns scores to each vertex according to the sum of its distances to all other vertices.
 */
public class BarycenterScorer<V,E> extends DistanceCentralityScorer<V, E>
{
    /**
     * Creates an instance with the specified graph and distance metric.
     * @param graph the input graph
     * @param distance the distance metric to use
     */
    public BarycenterScorer(Hypergraph<V,E> graph, Distance<V> distance)
    {
        super(graph, distance, false);
    }
    
    /**
     * Creates an instance with the specified graph and edge weights.
     * Will generate a <code>Distance</code> metric internally based on the edge weights. 
     * @param graph the input graph
     * @param edge_weights the edge weights to use to calculate vertex/vertex distances
     */
    public BarycenterScorer(Hypergraph<V,E> graph, Transformer<E, ? extends Number> edge_weights)
    {
        super(graph, edge_weights, false);
    }

    /**
     * Creates an instance with the specified graph.
     * Will generate a <code>Distance</code> metric internally assuming that the
     * graph is unweighted. 
     * @param graph the input graph
     */
    public BarycenterScorer(Hypergraph<V,E> graph)
    {
        super(graph, false);
    }
}
