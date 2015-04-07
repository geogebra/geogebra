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

import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns scores to each vertex according to the PageRank algorithm.  
 * 
 * <p>PageRank is an eigenvector-based algorithm.  The score for a given vertex may be thought of
 * as the fraction of time spent 'visiting' that vertex (measured over all time) 
 * in a random walk over the vertices (following outgoing edges from each vertex).  
 * PageRank modifies this random walk by adding to the model a probability (specified as 'alpha' 
 * in the constructor) of jumping to any vertex.  If alpha is 0, this is equivalent to the
 * eigenvector centrality algorithm; if alpha is 1, all vertices will receive the same score
 * (1/|V|).  Thus, alpha acts as a sort of score smoothing parameter.
 * 
 * <p>The original algorithm assumed that, for a given vertex, the probability of following any 
 * outgoing edge was the same; this is the default if edge weights are not specified.  
 * This implementation generalizes the original by permitting 
 * the user to specify edge weights; in order to maintain the original semantics, however,
 * the weights on the outgoing edges for a given vertex must represent transition probabilities; 
 * that is, they must sum to 1.
 * 
 * <p>If a vertex has no outgoing edges, then the probability of taking a random jump from that
 * vertex is (by default) effectively 1.  If the user wishes to instead throw an exception when this happens,
 * call <code>acceptDisconnectedGraph(false)</code> on this instance.
 * 
 * <p>Typical values for alpha (according to the original paper) are in the range [0.1, 0.2]
 * but may be any value between 0 and 1 inclusive.
 * 
 * @see "The Anatomy of a Large-Scale Hypertextual Web Search Engine by L. Page and S. Brin, 1999"
 */
public class PageRank<V,E> extends PageRankWithPriors<V,E>
{

    /**
     * Creates an instance for the specified graph, edge weights, and random jump probability.
     * @param graph the input graph
     * @param edge_weight the edge weights (transition probabilities)
     * @param alpha the probability of taking a random jump to an arbitrary vertex
     */
    public PageRank(Hypergraph<V,E> graph, Transformer<E, ? extends Number> edge_weight, double alpha)
    {
        super(graph, edge_weight, ScoringUtils.getUniformRootPrior(graph.getVertices()), alpha);
    }

    /**
     * Creates an instance for the specified graph and random jump probability; the probability
     * of following any outgoing edge from a given vertex is the same.
     * @param graph the input graph
     * @param alpha the probability of taking a random jump to an arbitrary vertex
     */
    public PageRank(Hypergraph<V,E> graph, double alpha)
    {
        super(graph, ScoringUtils.getUniformRootPrior(graph.getVertices()), alpha);
    }
}
