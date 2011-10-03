/*
 * Created on Jul 14, 2007
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
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.graph.Hypergraph;

/**
 * A generalization of HITS that permits non-uniformly-distributed random jumps.
 * The 'vertex_priors' (that is, prior probabilities for each vertex) may be
 * thought of as the fraction of the total 'potential' (hub or authority score)
 * that is assigned to that vertex out of the portion that is assigned according
 * to random jumps.
 * 
 * @see "Algorithms for Estimating Relative Importance in Graphs by Scott White and Padhraic Smyth, 2003"
 */
public class HITSWithPriors<V, E> 
	extends AbstractIterativeScorerWithPriors<V,E,HITS.Scores>
{
    /**
     * The sum of the potential, at each step, associated with vertices with no outedges (authority)
     * or no inedges (hub).
     */
    protected HITS.Scores disappearing_potential;

    /**
     * Creates an instance for the specified graph, edge weights, vertex prior probabilities,
     * and random jump probability (alpha).
     * @param g the input graph
     * @param edge_weights the edge weights 
     * @param vertex_priors the prior probability for each vertex
     * @param alpha the probability of a random jump at each step
     */
    public HITSWithPriors(Hypergraph<V,E> g,
            Transformer<E, ? extends Number> edge_weights,
            Transformer<V, HITS.Scores> vertex_priors, double alpha)
    {
        super(g, edge_weights, vertex_priors, alpha);
        disappearing_potential = new HITS.Scores(0,0);
    }

    /**
     * Creates an instance for the specified graph, vertex priors, and random
     * jump probability (alpha).  The edge weights default to 1.0.
     * @param g the input graph
     * @param vertex_priors the prior probability for each vertex
     * @param alpha the probability of a random jump at each step
     */
    @SuppressWarnings("unchecked")
    public HITSWithPriors(Hypergraph<V,E> g, 
          Transformer<V, HITS.Scores> vertex_priors, double alpha)
    {
    	super(g, new ConstantTransformer(1.0), vertex_priors, alpha);
        disappearing_potential = new HITS.Scores(0,0);
    }

    /**
     * Updates the value for this vertex.
     */
    @Override
    protected double update(V v)
    {
        collectDisappearingPotential(v);
        
        double v_auth = 0;
        for (E e : graph.getInEdges(v))
        {
        	int incident_count = getAdjustedIncidentCount(e);
        	for (V w : graph.getIncidentVertices(e)) 
        	{
        		if (!w.equals(v) || hyperedges_are_self_loops) 
        			v_auth += (getCurrentValue(w).hub * 
        					getEdgeWeight(w,e).doubleValue() / incident_count);
        	}
//            V w = graph.getOpposite(v, e);
//            auth += (getCurrentValue(w).hub * getEdgeWeight(w, e).doubleValue());
        }
        
        double v_hub = 0;
        for (E e : graph.getOutEdges(v))
        {
        	int incident_count = getAdjustedIncidentCount(e);
        	for (V w : graph.getIncidentVertices(e)) 
        	{
        		if (!w.equals(v) || hyperedges_are_self_loops) 
        			v_hub += (getCurrentValue(w).authority * 
        					getEdgeWeight(w,e).doubleValue() / incident_count);
        	}
//            V x = graph.getOpposite(v,e);
//            hub += (getCurrentValue(x).authority * getEdgeWeight(x, e).doubleValue()); 
        }
        
        // modify total_input according to alpha
        if (alpha > 0) 
        {
	        v_auth = v_auth * (1 - alpha) + getVertexPrior(v).authority * alpha;
	        v_hub = v_hub * (1 - alpha) + getVertexPrior(v).hub * alpha;
        }
        setOutputValue(v, new HITS.Scores(v_hub, v_auth));

        return Math.max(Math.abs(getCurrentValue(v).hub - v_hub), 
                        Math.abs(getCurrentValue(v).authority - v_auth));
    }

    /**
     * Code which is executed after each step.  In this case, deals with the
     * 'disappearing potential', normalizes the scores, and then calls
     * <code>super.afterStep()</code>.
     * @see #collectDisappearingPotential(Object)
     */
    @Override
    protected void afterStep()
    {
        if (disappearing_potential.hub > 0 || disappearing_potential.authority > 0)
        {
            for (V v : graph.getVertices())
            {
                double new_hub = getOutputValue(v).hub + 
                    (1 - alpha) * (disappearing_potential.hub * getVertexPrior(v).hub);
                double new_auth = getOutputValue(v).authority + 
                    (1 - alpha) * (disappearing_potential.authority * getVertexPrior(v).authority);
                setOutputValue(v, new HITS.Scores(new_hub, new_auth));
            }
            disappearing_potential.hub = 0;
            disappearing_potential.authority = 0;
        }
        
    	normalizeScores();
    	
        super.afterStep();
    }

	/**
	 * Normalizes scores so that sum of their squares = 1.
	 * This method may be overridden so as to yield different 
	 * normalizations.
	 */
	protected void normalizeScores() {
    	double hub_ssum = 0;
    	double auth_ssum = 0;
    	for (V v : graph.getVertices())
    	{
    		double hub_val = getOutputValue(v).hub;
    		double auth_val = getOutputValue(v).authority;
    		hub_ssum += (hub_val * hub_val);
    		auth_ssum += (auth_val * auth_val);
    	}

    	hub_ssum = Math.sqrt(hub_ssum);
    	auth_ssum = Math.sqrt(auth_ssum);
    	
    	for (V v : graph.getVertices())
    	{
    		HITS.Scores values = getOutputValue(v);
    		setOutputValue(v, new HITS.Scores(
    				values.hub / hub_ssum,
    				values.authority / auth_ssum));
    	}
	}
    
	/**
	 * Collects the "disappearing potential" associated with vertices that have either 
	 * no incoming edges, no outgoing edges, or both.  Vertices that have no incoming edges
	 * do not directly contribute to the hub scores of other vertices; similarly, vertices
	 * that have no outgoing edges do not directly contribute to the authority scores of
	 * other vertices.  These values are collected at each step and then distributed across all vertices
	 * as a part of the normalization process.  (This process is not required for, and does
	 * not affect, the 'sum-of-squares'-style normalization.) 
	 */
    @Override
    protected void collectDisappearingPotential(V v)
    {
        if (graph.outDegree(v) == 0)
        {
            if (isDisconnectedGraphOK())
                disappearing_potential.hub += getCurrentValue(v).authority;
            else
                throw new IllegalArgumentException("Outdegree of " + v + " must be > 0");
        }
        if (graph.inDegree(v) == 0)
        {
            if (isDisconnectedGraphOK())
                disappearing_potential.authority += getCurrentValue(v).hub;
            else
                throw new IllegalArgumentException("Indegree of " + v + " must be > 0");
        }
    }

}
