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

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.util.UniformDegreeWeight;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * A generalization of PageRank that permits non-uniformly-distributed random jumps.
 * The 'vertex_priors' (that is, prior probabilities for each vertex) may be
 * thought of as the fraction of the total 'potential' that is assigned to that 
 * vertex at each step out of the portion that is assigned according
 * to random jumps (this portion is specified by 'alpha').
 * 
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 * @see PageRank
 */
public class PageRankWithPriors<V, E> 
	extends AbstractIterativeScorerWithPriors<V,E,Double>
{
    /**
     * Maintains the amount of potential associated with vertices with no out-edges.
     */
    protected double disappearing_potential = 0.0;
    
    /**
     * Creates an instance with the specified graph, edge weights, vertex priors, and 
     * 'random jump' probability (alpha).
     * @param graph the input graph
     * @param edge_weights the edge weights, denoting transition probabilities from source to destination
     * @param vertex_priors the prior probabilities for each vertex
     * @param alpha the probability of executing a 'random jump' at each step
     */
    public PageRankWithPriors(Hypergraph<V,E> graph, 
    		Transformer<E, ? extends Number> edge_weights, 
            Transformer<V, Double> vertex_priors, double alpha)
    {
        super(graph, edge_weights, vertex_priors, alpha);
    }
    
    /**
     * Creates an instance with the specified graph, vertex priors, and 
     * 'random jump' probability (alpha).  The outgoing edge weights for each
     * vertex will be equal and sum to 1.
     * @param graph the input graph
     * @param vertex_priors the prior probabilities for each vertex
     * @param alpha the probability of executing a 'random jump' at each step
     */
    public PageRankWithPriors(Hypergraph<V,E> graph, 
    		Transformer<V, Double> vertex_priors, double alpha)
    {
        super(graph, vertex_priors, alpha);
        this.edge_weights = new UniformDegreeWeight<V,E>(graph);
    }
    
    /**
     * Updates the value for this vertex.  Called by <code>step()</code>.
     */
    @Override
    public double update(V v)
    {
        collectDisappearingPotential(v);
        
        double v_input = 0;
        for (E e : graph.getInEdges(v))
        {
        	// For graphs, the code below is equivalent to 
//          V w = graph.getOpposite(v, e);
//          total_input += (getCurrentValue(w) * getEdgeWeight(w,e).doubleValue());
        	// For hypergraphs, this divides the potential coming from w 
        	// by the number of vertices in the connecting edge e.
        	int incident_count = getAdjustedIncidentCount(e);
        	for (V w : graph.getIncidentVertices(e)) 
        	{
        		if (!w.equals(v) || hyperedges_are_self_loops) 
        			v_input += (getCurrentValue(w) * 
        					getEdgeWeight(w,e).doubleValue() / incident_count);
        	}
        }
        
        // modify total_input according to alpha
        double new_value = alpha > 0 ? 
        		v_input * (1 - alpha) + getVertexPrior(v) * alpha :
        		v_input;
        setOutputValue(v, new_value);
        
        return Math.abs(getCurrentValue(v) - new_value);
    }

    /**
     * Cleans up after each step.  In this case that involves allocating the disappearing
     * potential (thus maintaining normalization of the scores) according to the vertex 
     * probability priors, and then calling 
     * <code>super.afterStep</code>.
     */
    @Override
    protected void afterStep()
    {
        // distribute disappearing potential according to priors
        if (disappearing_potential > 0)
        {
            for (V v : graph.getVertices())
            {
                setOutputValue(v, getOutputValue(v) + 
                        (1 - alpha) * (disappearing_potential * getVertexPrior(v)));
            }
            disappearing_potential = 0;
        }
        
        super.afterStep();
    }
    
    /**
     * Collects the "disappearing potential" associated with vertices that have 
     * no outgoing edges.  Vertices that have no outgoing edges do not directly 
     * contribute to the scores of other vertices.  These values are collected 
     * at each step and then distributed across all vertices
     * as a part of the normalization process.
    */
    @Override
    protected void collectDisappearingPotential(V v)
    {
        if (graph.outDegree(v) == 0)
        {
            if (isDisconnectedGraphOK())
                disappearing_potential += getCurrentValue(v);
            else
                throw new IllegalArgumentException("Outdegree of " + v + " must be > 0");
        }
    }
}
