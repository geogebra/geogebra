/**
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Jun 7, 2008
 * 
 */
package edu.uci.ics.jung.algorithms.filters;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Utility methods relating to filtering.
 */
public class FilterUtils 
{
	/**
	 * Creates the induced subgraph from <code>graph</code> whose vertex set
	 * is equal to <code>vertices</code>.  The graph returned has 
	 * <code>vertices</code> as its vertex set, and includes all edges from
	 * <code>graph</code> which are incident only to elements of 
	 * <code>vertices</code>.
	 * 
	 * @param <V> the vertex type
	 * @param <E> the edge type
	 * @param vertices the subset of <code>graph</code>'s vertices around 
	 * which the subgraph is to be constructed
	 * @param graph the graph whose subgraph is to be constructed
	 * @return the subgraph induced by <code>vertices</code>
	 * @throws IllegalArgumentException if any vertex in 
	 * <code>vertices</code> is not in <code>graph</code>
	 */
	@SuppressWarnings("unchecked")
	public static <V,E,G extends Hypergraph<V,E>> G createInducedSubgraph(Collection<V> 
		vertices, G graph)
	{
		G subgraph = null;
		try 
		{
			subgraph = (G)graph.getClass().newInstance();
			
			for (V v : vertices)
			{
				if (!graph.containsVertex(v))
					throw new IllegalArgumentException("Vertex " + v + 
						" is not an element of " + graph);
				subgraph.addVertex(v);
			}

			for (E e : graph.getEdges())
			{
				Collection<V> incident = graph.getIncidentVertices(e);
				if (vertices.containsAll(incident))
					subgraph.addEdge(e, incident, graph.getEdgeType(e));
			}
		} 
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to create copy of existing graph: ", e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to create copy of existing graph: ", e);
        }
		return subgraph;
	}
	
	/**
	 * Creates the induced subgraphs of <code>graph</code> associated with each 
	 * element of <code>vertex_collections</code>.
	 * Note that these vertex collections need not be disjoint.
	 * @param <V> the vertex type
	 * @param <E> the edge type
	 * @param vertex_collections the collections of vertex collections to be
	 * used to induce the subgraphs
	 * @param graph the graph whose subgraphs are to be created
	 * @return the induced subgraphs of <code>graph</code> associated with each 
	 * element of <code>vertex_collections</code>
	 */
	public static <V,E,G extends Hypergraph<V,E>> Collection<G> 
		createAllInducedSubgraphs(Collection<? extends Collection<V>> 
			vertex_collections, G graph)
	{
		Collection<G> subgraphs = new ArrayList<G>();
		
		for (Collection<V> vertex_set : vertex_collections)
			subgraphs.add(createInducedSubgraph(vertex_set, graph));
		
		return subgraphs;
	}
}
