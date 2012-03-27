/*
 * Created on Apr 2, 2006
 *
 * Copyright (c) 2006, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Abstract implementation of the <code>Graph</code> interface.  
 * Designed to simplify implementation of new graph classes.
 * 
 * @author Joshua O'Madadhain
 */
@SuppressWarnings("serial")
public abstract class AbstractGraph<V, E> implements Graph<V,E>, Serializable 
{
	public boolean addEdge(E edge, Collection<? extends V> vertices) 
	{
		return addEdge(edge, vertices, this.getDefaultEdgeType());
	}

	@SuppressWarnings("unchecked")
	public boolean addEdge(E edge, Collection<? extends V> vertices, EdgeType edgeType) {
	    if (vertices == null)
	        throw new IllegalArgumentException("'vertices' parameter must not be null");
	    if (vertices.size() == 2)
	        return addEdge(edge, 
	        			   vertices instanceof Pair ? (Pair<V>)vertices : new Pair<V>(vertices), 
	        			   edgeType);
        else if (vertices.size() == 1)
        {
            V vertex = vertices.iterator().next();
            return addEdge(edge, new Pair<V>(vertex, vertex), edgeType);
        }
        else
            throw new IllegalArgumentException("Graph objects connect 1 or 2 vertices; vertices arg has " + vertices.size());
	}
	
	public boolean addEdge(E e, V v1, V v2)
	{
		return addEdge(e, v1, v2, this.getDefaultEdgeType());
	}

	public boolean addEdge(E e, V v1, V v2, EdgeType edge_type)
	{
		return addEdge(e, new Pair<V>(v1, v2), edge_type);
	}
	
	/**
	 * Adds {@code edge} to this graph with the specified {@code endpoints},
	 * with the default edge type.
	 * @return {@code} true iff the graph was modified as a result of this call
	 */
	public boolean addEdge(E edge, Pair<? extends V> endpoints)
	{
		return addEdge(edge, endpoints, this.getDefaultEdgeType());
	}
	
    /**
     * Adds {@code edge} to this graph with the specified {@code endpoints}
     * and {@code EdgeType}.
     * @return {@code} true iff the graph was modified as a result of this call
     */
	public abstract boolean addEdge(E edge, Pair<? extends V> endpoints, EdgeType edgeType);

    protected Pair<V> getValidatedEndpoints(E edge, Pair<? extends V> endpoints)
    {
        if (edge == null)
            throw new IllegalArgumentException("input edge may not be null");
        
        if (endpoints == null)
            throw new IllegalArgumentException("endpoints may not be null");
        
        Pair<V> new_endpoints = new Pair<V>(endpoints.getFirst(), endpoints.getSecond());
        if (containsEdge(edge))
        {
            Pair<V> existing_endpoints = getEndpoints(edge);
            if (!existing_endpoints.equals(new_endpoints)) {
                throw new IllegalArgumentException("edge " + edge + 
                        " already exists in this graph with endpoints " + existing_endpoints + 
                        " and cannot be added with endpoints " + endpoints);
            } else {
                return null;
            }
        }
        return new_endpoints;
    }
    
    public int inDegree(V vertex)
    {
        return this.getInEdges(vertex).size();
    }

    public int outDegree(V vertex)
    {
        return this.getOutEdges(vertex).size();
    }

    public boolean isPredecessor(V v1, V v2)
    {
        return this.getPredecessors(v1).contains(v2);
    }

    public boolean isSuccessor(V v1, V v2)
    {
        return this.getSuccessors(v1).contains(v2);
    }

    public int getPredecessorCount(V vertex)
    {
        return this.getPredecessors(vertex).size();
    }

    public int getSuccessorCount(V vertex)
    {
        return this.getSuccessors(vertex).size();
    }

    public boolean isNeighbor(V v1, V v2)
    {
        if (!containsVertex(v1) || !containsVertex(v2))
            throw new IllegalArgumentException("At least one of these not in this graph: " + v1 + ", " + v2);
        return this.getNeighbors(v1).contains(v2);
    }

    public boolean isIncident(V vertex, E edge)
    {
        if (!containsVertex(vertex) || !containsEdge(edge))
            throw new IllegalArgumentException("At least one of these not in this graph: " + vertex + ", " + edge);
        return this.getIncidentEdges(vertex).contains(edge);
    }

    public int getNeighborCount(V vertex)
    {
        if (!containsVertex(vertex))
            throw new IllegalArgumentException(vertex + " is not a vertex in this graph");
        return this.getNeighbors(vertex).size();
    }

    public int degree(V vertex)
    {
        if (!containsVertex(vertex))
            throw new IllegalArgumentException(vertex + " is not a vertex in this graph");
        return this.getIncidentEdges(vertex).size();
    }

    public int getIncidentCount(E edge)
    {
        Pair<V> incident = this.getEndpoints(edge);
        if (incident == null)
            return 0;
        if (incident.getFirst() == incident.getSecond())
            return 1;
        else
            return 2;
    }
    
    public V getOpposite(V vertex, E edge)
    {
        Pair<V> incident = this.getEndpoints(edge); 
        V first = incident.getFirst();
        V second = incident.getSecond();
        if (vertex.equals(first))
            return second;
        else if (vertex.equals(second))
            return first;
        else 
            throw new IllegalArgumentException(vertex + " is not incident to " + edge + " in this graph");
    }

    public E findEdge(V v1, V v2)
    {
        for (E e : getOutEdges(v1))
        {
            if (getOpposite(v1, e).equals(v2))
                return e;
        }
        return null;
    }
    
    public Collection<E> findEdgeSet(V v1, V v2)
    {
        if (!getVertices().contains(v1))
            throw new IllegalArgumentException(v1 + " is not an element of this graph");
        
        if (!getVertices().contains(v2))
            throw new IllegalArgumentException(v2 + " is not an element of this graph");
        
        Collection<E> edges = new ArrayList<E>();
        for (E e : getOutEdges(v1))
        {
            if (getOpposite(v1, e).equals(v2))
                edges.add(e);
        }
        return Collections.unmodifiableCollection(edges);
    }
    
    public Collection<V> getIncidentVertices(E edge)
    {
        Pair<V> endpoints = this.getEndpoints(edge);
        Collection<V> incident = new ArrayList<V>();
        incident.add(endpoints.getFirst());
        incident.add(endpoints.getSecond());
        
        return Collections.unmodifiableCollection(incident);
    }
    
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer("Vertices:");
    	for(V v : getVertices()) {
    		sb.append(v+",");
    	}
    	sb.setLength(sb.length()-1);
    	sb.append("\nEdges:");
    	for(E e : getEdges()) {
    		Pair<V> ep = getEndpoints(e);
    		sb.append(e+"["+ep.getFirst()+","+ep.getSecond()+"] ");
    	}
        return sb.toString();
    }

}
