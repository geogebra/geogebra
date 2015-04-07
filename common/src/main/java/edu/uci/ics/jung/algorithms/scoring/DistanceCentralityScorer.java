/*
 * Created on Jul 10, 2007
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns scores to vertices based on their distances to each other vertex 
 * in the graph.
 * 
 * This class optionally normalizes its results based on the value of its
 * 'averaging' constructor parameter.  If it is <code>true</code>, 
 * then the value returned for vertex v is 1 / (_average_ distance from v to all other vertices); 
 * this is sometimes called <i>closeness centrality</i>.
 * If it is <code>false</code>, then the value returned is 1 / (_total_ distance from
 * v to all other vertices); this is sometimes referred to as <i>barycenter centrality</i>.
 * (If the average/total distance is 0, the value returned is {@code Double.POSITIVE_INFINITY}.)
 * 
 * @see BarycenterScorer
 * @see ClosenessCentrality
 */
public class DistanceCentralityScorer<V,E> implements VertexScorer<V, Double>
{
    /**
     * The graph on which the vertex scores are to be calculated.
     */
    protected Hypergraph<V, E> graph;
    
    /**
     * The metric to use for specifying the distance between pairs of vertices.
     */
    protected Distance<V> distance;
    
    /**
     * The cache for the output results.  Null encodes "not yet calculated",
     * < 0 encodes "no such distance exists".
     */
    protected Map<V, Double> output;
    
    /**
     * Specifies whether the values returned are the sum of the v-distances
     * or the mean v-distance.
     */
    protected boolean averaging;
    
    /**
     * Specifies whether, for a vertex <code>v</code> with missing (null) distances, 
     * <code>v</code>'s score should ignore the missing values or be set to 'null'.
     * Defaults to 'true'.
     */
    protected boolean ignore_missing;

    /**
     * Specifies whether the values returned should ignore self-distances 
     * (distances from <code>v</code> to itself).
     * Defaults to 'true'.
     */
    protected boolean ignore_self_distances;
    
    /**
     * Creates an instance with the specified graph, distance metric, and 
     * averaging behavior.
     * 
     * @param graph     The graph on which the vertex scores are to be calculated.
     * @param distance  The metric to use for specifying the distance between 
     * pairs of vertices.
     * @param averaging Specifies whether the values returned is the sum of all 
     * v-distances or the mean v-distance.
     * @param ignore_missing	Specifies whether scores for missing distances 
     * are to ignore missing distances or be set to null.
     * @param ignore_self_distances	Specifies whether distances from a vertex
     * to itself should be included in its score.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, Distance<V> distance, 
    		boolean averaging, boolean ignore_missing, 
    		boolean ignore_self_distances)
    {
        this.graph = graph;
        this.distance = distance;
        this.averaging = averaging;
        this.ignore_missing = ignore_missing;
        this.ignore_self_distances = ignore_self_distances;
        this.output = new HashMap<V, Double>();
    }

    /**
     * Equivalent to <code>this(graph, distance, averaging, true, true)</code>.
     * 
     * @param graph     The graph on which the vertex scores are to be calculated.
     * @param distance  The metric to use for specifying the distance between 
     * pairs of vertices.
     * @param averaging Specifies whether the values returned is the sum of all 
     * v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, Distance<V> distance, 
    		boolean averaging)
    {
    	this(graph, distance, averaging, true, true);
    }
    
    /**
     * Creates an instance with the specified graph and averaging behavior
     * whose vertex distances are calculated based on the specified edge
     * weights.  
     * 
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param edge_weights  The edge weights to use for specifying the distance 
     * between pairs of vertices.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     * @param ignore_missing	Specifies whether scores for missing distances 
     * are to ignore missing distances or be set to null.
     * @param ignore_self_distances	Specifies whether distances from a vertex
     * to itself should be included in its score.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, 
            Transformer<E, ? extends Number> edge_weights, boolean averaging,
            boolean ignore_missing, boolean ignore_self_distances)
    {
        this(graph, new DijkstraDistance<V,E>(graph, edge_weights), averaging,
        	ignore_missing, ignore_self_distances);
    }
    
    /**
     * Equivalent to <code>this(graph, edge_weights, averaging, true, true)</code>.
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param edge_weights  The edge weights to use for specifying the distance 
     * between pairs of vertices.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, 
            Transformer<E, ? extends Number> edge_weights, boolean averaging)
    {
        this(graph, new DijkstraDistance<V,E>(graph, edge_weights), averaging,
        	true, true);
    }
    
    /**
     * Creates an instance with the specified graph and averaging behavior
     * whose vertex distances are calculated on the unweighted graph.  
     * 
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     * @param ignore_missing	Specifies whether scores for missing distances 
     * are to ignore missing distances or be set to null.
     * @param ignore_self_distances	Specifies whether distances from a vertex
     * to itself should be included in its score.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, boolean averaging,
            boolean ignore_missing, boolean ignore_self_distances)
    {
        this(graph, new UnweightedShortestPath<V,E>(graph), averaging, 
        	ignore_missing, ignore_self_distances);
    }

    /**
     * Equivalent to <code>this(graph, averaging, true, true)</code>.
     * @param graph         The graph on which the vertex scores are to be 
     * calculated.
     * @param averaging     Specifies whether the values returned is the sum of 
     * all v-distances or the mean v-distance.
     */
    public DistanceCentralityScorer(Hypergraph<V,E> graph, boolean averaging)
    {
        this(graph, new UnweightedShortestPath<V,E>(graph), averaging, true, true);
    }

	/**
	 * Calculates the score for the specified vertex.  Returns {@code null} if 
	 * there are missing distances and such are not ignored by this instance.
	 */
	public Double getVertexScore(V v) 
	{
	    Double value = output.get(v);
	    if (value != null)
	    {
	        if (value < 0)
	            return null;
	        return value;
	    }
	    
	    Map<V, Number> v_distances = new HashMap<V, Number>(distance.getDistanceMap(v));
	    if (ignore_self_distances)
	        v_distances.remove(v);
	    
		// if we don't ignore missing distances and there aren't enough
		// distances, output null (shortcut)
		if (!ignore_missing)
		{
			int num_dests = graph.getVertexCount() - 
			    (ignore_self_distances ? 1 : 0);
			if (v_distances.size() != num_dests) 
			{
				output.put(v, -1.0);
				return null;
			}
		}		
		
		Double sum = 0.0;
		for (V w : graph.getVertices())
		{
			if (w.equals(v) && ignore_self_distances)
				continue;
			Number w_distance = v_distances.get(w);
			if (w_distance == null)
				if (ignore_missing)
					continue;
				else
				{
					output.put(v, -1.0);
					return null;
				}
			else
				sum += w_distance.doubleValue();
		}
		value = sum;
		if (averaging)
		    value /= v_distances.size();
		
		double score = value == 0 ? 
			Double.POSITIVE_INFINITY : 
			1.0 / value;
	    output.put(v, score);
		   
		return score;
	}
}
