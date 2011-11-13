/*
 * Created on Mar 6, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Oct 18, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An implementation of <code>UndirectedGraph</code> that is suitable for 
 * sparse graphs and permits parallel edges.
 */
@SuppressWarnings("serial")
public class UndirectedSparseMultigraph<V,E> 
    extends AbstractTypedGraph<V,E>
    implements UndirectedGraph<V,E>, MultiGraph<V,E>
{
    /**
     * Returns a {@code Factory} that creates an instance of this graph type.
     * @param <V> the vertex type for the graph factory
     * @param <E> the edge type for the graph factory
     */
    public static <V,E> Factory<UndirectedGraph<V,E>> getFactory() {
        return new Factory<UndirectedGraph<V,E>> () {

            public UndirectedGraph<V,E> create() {
                return new UndirectedSparseMultigraph<V,E>();
            }
        };
    }

    protected Map<V, Set<E>> vertices; // Map of vertices to adjacency sets
    protected Map<E, Pair<V>> edges;    // Map of edges to incident vertex sets

    /**
     * Creates a new instance.
     */
    public UndirectedSparseMultigraph() {
    	super(EdgeType.UNDIRECTED);
        vertices = new HashMap<V, Set<E>>();
        edges = new HashMap<E, Pair<V>>();
    }

    public Collection<E> getEdges() {
        return Collections.unmodifiableCollection(edges.keySet());
    }

    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(vertices.keySet());
    }

    public boolean containsVertex(V vertex) {
    	return vertices.keySet().contains(vertex);
    }
    
    public boolean containsEdge(E edge) {
    	return edges.keySet().contains(edge);
    }

    protected Collection<E> getIncident_internal(V vertex)
    {
        return vertices.get(vertex);
    }
    
    public boolean addVertex(V vertex) {
        if(vertex == null) {
            throw new IllegalArgumentException("vertex may not be null");
        }
        if (!containsVertex(vertex))
        {
            vertices.put(vertex, new HashSet<E>());
            return true;
        } else {
            return false;
        }
    }

    public boolean removeVertex(V vertex) {
        if (!containsVertex(vertex))
            return false;
        
        for (E edge : new ArrayList<E>(getIncident_internal(vertex)))
            removeEdge(edge);
        
        vertices.remove(vertex);
        return true;
    }
    
    @Override
    public boolean addEdge(E edge, V v1, V v2, EdgeType edgeType) {
        return addEdge(edge, new Pair<V>(v1, v2), edgeType);
    }
    
    @Override
    public boolean addEdge(E edge, Pair<? extends V> endpoints, EdgeType edge_type) 
    {
    	validateEdgeType(edge_type);
    	
        Pair<V> new_endpoints = getValidatedEndpoints(edge, endpoints);
        if (new_endpoints == null)
            return false;
        
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();

        edges.put(edge, new_endpoints);
        
        if (!containsVertex(v1))
            this.addVertex(v1);
        
        if (!containsVertex(v2))
            this.addVertex(v2);

        vertices.get(v1).add(edge);
        vertices.get(v2).add(edge);        
        
        return true;
    }

    public boolean removeEdge(E edge) {
        if (!containsEdge(edge))
            return false;
        
        Pair<V> endpoints = getEndpoints(edge);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        
        // remove edge from incident vertices' adjacency sets
        vertices.get(v1).remove(edge);
        vertices.get(v2).remove(edge);

        edges.remove(edge);
        return true;
    }
    
    public Collection<E> getInEdges(V vertex) {
        return this.getIncidentEdges(vertex);
    }

    public Collection<E> getOutEdges(V vertex) {
        return this.getIncidentEdges(vertex);
    }

    public Collection<V> getPredecessors(V vertex) {
        return this.getNeighbors(vertex);
    }

    public Collection<V> getSuccessors(V vertex) {
        return this.getNeighbors(vertex);
    }

    public Collection<V> getNeighbors(V vertex) {
        if (!containsVertex(vertex))
            return null;
        
        Set<V> neighbors = new HashSet<V>();
        for (E edge : getIncident_internal(vertex))
        {
            Pair<V> endpoints = this.getEndpoints(edge);
            V e_a = endpoints.getFirst();
            V e_b = endpoints.getSecond();
            if (vertex.equals(e_a))
                neighbors.add(e_b);
            else
                neighbors.add(e_a);
        }
        
        return Collections.unmodifiableCollection(neighbors);
    }

    public Collection<E> getIncidentEdges(V vertex) {
        if (!containsVertex(vertex))
            return null;
        
        return Collections.unmodifiableCollection(getIncident_internal(vertex));
    }

    @Override
    public E findEdge(V v1, V v2) {
        if (!containsVertex(v1) || !containsVertex(v2))
            return null;
        for (E edge : getIncident_internal(v1)) {
            Pair<V> endpoints = this.getEndpoints(edge);
            V e_a = endpoints.getFirst();
            V e_b = endpoints.getSecond();
            if ((v1.equals(e_a) && v2.equals(e_b)) || (v1.equals(e_b) && v2.equals(e_a)))
                return edge;
        }
        return null;
    }

    public Pair<V> getEndpoints(E edge) {
        return edges.get(edge);
    }

    public V getDest(E directed_edge) {
        return null;
    }

    public V getSource(E directed_edge) {
        return null;
    }

    public boolean isDest(V vertex, E edge) {
        return false;
    }

    public boolean isSource(V vertex, E edge) {
        return false;
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public int getVertexCount() {
        return vertices.size();
    }

}
