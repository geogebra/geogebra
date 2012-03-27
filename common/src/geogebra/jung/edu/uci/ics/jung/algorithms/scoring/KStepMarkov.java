/**
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Aug 22, 2008
 * 
 */
package edu.uci.ics.jung.algorithms.scoring;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.util.ScoringUtils;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * A special case of {@code PageRankWithPriors} in which the final scores
 * represent a probability distribution over position assuming a random (Markovian)
 * walk of exactly k steps, based on the initial distribution specified by the priors.
 * 
 * <p><b>NOTE</b>: The version of {@code KStepMarkov} in {@code algorithms.importance}
 * (and in JUNG 1.x) is believed to be incorrect: rather than returning 
 * a score which represents a probability distribution over position assuming
 * a k-step random walk, it returns a score which represents the sum over all steps
 * of the probability for each step.  If you want that behavior, set the 
 * 'cumulative' flag as follows <i>before calling {@code evaluate()}</i>:
 * <pre>
 *     KStepMarkov ksm = new KStepMarkov(...);
 *     ksm.setCumulative(true);
 *     ksm.evaluate();
 * </pre>
 * 
 * By default, the 'cumulative' flag is set to false.
 * 
 * NOTE: THIS CLASS IS NOT YET COMPLETE.  USE AT YOUR OWN RISK.  (The original behavior
 * is captured by the version still available in {@code algorithms.importance}.)
 * 
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 * @see PageRank
 * @see PageRankWithPriors
 */
public class KStepMarkov<V,E> extends PageRankWithPriors<V,E> 
{
	private boolean cumulative;
	
	/**
	 * Creates an instance based on the specified graph, edge weights, vertex
	 * priors (initial scores), and number of steps to take.
	 * @param graph the input graph
	 * @param edge_weights the edge weights (transition probabilities)
	 * @param vertex_priors the initial probability distribution (score assignment)
	 * @param steps the number of times that {@code step()} will be called by {@code evaluate}
	 */
	public KStepMarkov(Hypergraph<V,E> graph, Transformer<E, ? extends Number> edge_weights, 
					   Transformer<V, Double> vertex_priors, int steps)
	{
		super(graph, edge_weights, vertex_priors, 0);
		initialize(steps);
	}
	
	/**
	 * Creates an instance based on the specified graph, vertex
	 * priors (initial scores), and number of steps to take.  The edge
	 * weights (transition probabilities) are set to default values (a uniform
	 * distribution over all outgoing edges).
	 * @param graph the input graph
	 * @param vertex_priors the initial probability distribution (score assignment)
	 * @param steps the number of times that {@code step()} will be called by {@code evaluate}
	 */
	public KStepMarkov(Hypergraph<V,E> graph, Transformer<V, Double> vertex_priors, int steps)
	{
		super(graph, vertex_priors, 0);
		initialize(steps);
	}
	
	/**
	 * Creates an instance based on the specified graph and number of steps to 
	 * take.  The edge weights (transition probabilities) and vertex initial scores
	 * (prior probabilities) are set to default values (a uniform
	 * distribution over all outgoing edges, and a uniform distribution over
	 * all vertices, respectively).
	 * @param graph the input graph
	 * @param steps the number of times that {@code step()} will be called by {@code evaluate}
	 */
	public KStepMarkov(Hypergraph<V,E> graph, int steps)
	{
		super(graph, ScoringUtils.getUniformRootPrior(graph.getVertices()), 0);
		initialize(steps);
	}
	
	private void initialize(int steps)
	{
		this.acceptDisconnectedGraph(false);
		
		if (steps <= 0)
			throw new IllegalArgumentException("Number of steps must be > 0");
		
		this.max_iterations = steps;
		this.tolerance = -1.0;
		
		this.cumulative = false;
	}

	/**
	 * Specifies whether this instance should assign a score to each vertex
	 * based on the 
	 * @param cumulative
	 */
	public void setCumulative(boolean cumulative)
	{
		this.cumulative = cumulative;
	}
	
    /**
     * Updates the value for this vertex.  Called by <code>step()</code>.
     */
    @Override
    public double update(V v)
    {
    	if (!cumulative)
    		return super.update(v);
    	
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
        setOutputValue(v, new_value + getCurrentValue(v));

        // FIXME: DO WE NEED TO CHANGE HOW DISAPPEARING IS COUNTED?  NORMALIZE?
        
        return Math.abs(getCurrentValue(v) - new_value);
    }

}
