/*
 * Created on Sep 24, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;

/**
 * A class which creates and maintains indices for parallel edges.
 * Parallel edges are defined here to be the collection of edges 
 * that are returned by <code>v.findEdgeSet(w)</code> for some 
 * <code>v</code> and <code>w</code>.
 * 
 * <p>At this time, users are responsible for resetting the indices 
 * (by calling <code>reset()</code>) if changes to the
 * graph make it appropriate.</p>
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson
 *
 */
public class DefaultParallelEdgeIndexFunction<V,E> implements EdgeIndexFunction<V,E>
{
    protected Map<Context<Graph<V,E>,E>, Integer> edge_index = new HashMap<Context<Graph<V,E>,E>, Integer>();
    
    private DefaultParallelEdgeIndexFunction() {
    }
    
    /**
     * Returns an instance of this class.
     * @param <V> the vertex type
     * @param <E> the edge type
     */
    public static <V,E> DefaultParallelEdgeIndexFunction<V,E> getInstance() {
        return new DefaultParallelEdgeIndexFunction<V,E>();
    }
    
    /**
     * Returns the index for <code>e</code> in <code>graph</code>.
     * Calculates the indices for <code>e</code> and for all edges parallel
     * to <code>e</code>, if they are not already assigned.
     */
    public int getIndex(Graph<V,E> graph, E e)
    {
    	
        Integer index = edge_index.get(Context.<Graph<V,E>,E>getInstance(graph,e));
        	//edge_index.get(e);
        if(index == null) {
        	Pair<V> endpoints = graph.getEndpoints(e);
        	V u = endpoints.getFirst();
        	V v = endpoints.getSecond();
        	if(u.equals(v)) {
        		index = getIndex(graph, e, v);
        	} else {
        		index = getIndex(graph, e, u, v);
        	}
        }
        return index.intValue();
    }

    protected int getIndex(Graph<V,E> graph, E e, V v, V u) {
    	Collection<E> commonEdgeSet = new HashSet<E>(graph.getIncidentEdges(u));
    	commonEdgeSet.retainAll(graph.getIncidentEdges(v));
    	for(Iterator<E> iterator=commonEdgeSet.iterator(); iterator.hasNext(); ) {
    		E edge = iterator.next();
    		Pair<V> ep = graph.getEndpoints(edge);
    		V first = ep.getFirst();
    		V second = ep.getSecond();
    		// remove loops
    		if(first.equals(second) == true) {
    			iterator.remove();
    		}
    		// remove edges in opposite direction
    		if(first.equals(v) == false) {
    			iterator.remove();
    		}
    	}
    	int count=0;
    	for(E other : commonEdgeSet) {
    		if(e.equals(other) == false) {
    			edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,other), count);
    			count++;
    		}
    	}
    	edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,e), count);
    	return count;
     }
    
    protected int getIndex(Graph<V,E> graph, E e, V v) {
    	Collection<E> commonEdgeSet = new HashSet<E>();
    	for(E another : graph.getIncidentEdges(v)) {
    		V u = graph.getOpposite(v, another);
    		if(u.equals(v)) {
    			commonEdgeSet.add(another);
    		}
    	}
    	int count=0;
    	for(E other : commonEdgeSet) {
    		if(e.equals(other) == false) {
    			edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,other), count);
    			count++;
    		}
    	}
    	edge_index.put(Context.<Graph<V,E>,E>getInstance(graph,e), count);
    	return count;
    }

    
    /**
     * Resets the indices for this edge and its parallel edges.
     * Should be invoked when an edge parallel to <code>e</code>
     * has been added or removed.
     * @param e
     */
    public void reset(Graph<V,E> graph, E e) {
    	Pair<V> endpoints = graph.getEndpoints(e);
        getIndex(graph, e, endpoints.getFirst());
        getIndex(graph, e, endpoints.getFirst(), endpoints.getSecond());
    }
    
    /**
     * Clears all edge indices for all edges in all graphs.
     * Does not recalculate the indices.
     */
    public void reset()
    {
        edge_index.clear();
    }
}
