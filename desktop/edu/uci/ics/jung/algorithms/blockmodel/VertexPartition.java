/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Feb 3, 2004
 */
package edu.uci.ics.jung.algorithms.blockmodel;

import java.util.*;

import edu.uci.ics.jung.graph.Graph;


/**
 * Maintains information about a vertex partition of a graph.
 * This can be built from a map from vertices to vertex sets 
 * or from a collection of (disjoint) vertex sets,
 * such as those created by various clustering methods.
 */
public class VertexPartition<V,E> 
{
	private Map<V,Set<V>> vertex_partition_map;
	private Collection<Set<V>> vertex_sets;
	private Graph<V,E> graph;
	
	/**
	 * Creates an instance based on the specified graph and mapping from vertices
	 * to vertex sets, and generates a set of partitions based on this mapping.
	 * @param g the graph over which the vertex partition is defined
	 * @param partition_map the mapping from vertices to vertex sets (partitions)
	 */
	public VertexPartition(Graph<V,E> g, Map<V, Set<V>> partition_map) 
	{
		this.vertex_partition_map = Collections.unmodifiableMap(partition_map);
		this.graph = g;
	}

	/**
     * Creates an instance based on the specified graph, vertex-set mapping, 
     * and set of disjoint vertex sets.  The vertex-set mapping and vertex 
     * partitions must be consistent; that is, the mapping must reflect the 
     * division of vertices into partitions, and each vertex must appear in 
     * exactly one partition.
     * @param g the graph over which the vertex partition is defined
     * @param partition_map the mapping from vertices to vertex sets (partitions)
	 * @param vertex_sets the set of disjoint vertex sets 
	 */
    public VertexPartition(Graph<V,E> g, Map<V, Set<V>> partition_map, 
    		Collection<Set<V>> vertex_sets) 
    {
        this.vertex_partition_map = Collections.unmodifiableMap(partition_map);
        this.vertex_sets = vertex_sets;
        this.graph = g;
    }

    /**
     * Creates an instance based on the specified graph and set of disjoint vertex sets, 
     * and generates a vertex-to-partition map based on these sets.
     * @param g the graph over which the vertex partition is defined
     * @param vertex_sets the set of disjoint vertex sets
     */
    public VertexPartition(Graph<V,E> g, Collection<Set<V>> vertex_sets)
    {
        this.vertex_sets = vertex_sets;
        this.graph = g;
    }
	
    /**
     * Returns the graph on which the partition is defined.
     * @return the graph on which the partition is defined
     */
	public Graph<V,E> getGraph() 
	{
		return graph;
	}

	/**
	 * Returns a map from each vertex in the input graph to its partition.
	 * This map is generated if it does not already exist.
	 * @return a map from each vertex in the input graph to a vertex set
	 */
	public Map<V,Set<V>> getVertexToPartitionMap() 
	{
		if (vertex_partition_map == null)
		{
	        this.vertex_partition_map = new HashMap<V, Set<V>>();
	        for (Set<V> set : this.vertex_sets)
	            for (V v : set)
	                this.vertex_partition_map.put(v, set);
		}
		return vertex_partition_map;
	}
	
	/**
	 * Returns a collection of vertex sets, where each vertex in the 
	 * input graph is in exactly one set.
	 * This collection is generated based on the vertex-to-partition map 
	 * if it does not already exist.
	 * @return a collection of vertex sets such that each vertex in the 
	 * instance's graph is in exactly one set
	 */
	public Collection<Set<V>> getVertexPartitions() 
	{
		if (vertex_sets == null)
		{
			this.vertex_sets = new HashSet<Set<V>>();
			this.vertex_sets.addAll(vertex_partition_map.values());
		}
	    return vertex_sets;
	}

	/**
	 * Returns the number of partitions.
	 */
	public int numPartitions() 
	{
		return vertex_sets.size();
	}
	
	@Override
  	public String toString() 
	{
		return "Partitions: " + vertex_partition_map;
	}
}
