/*
 * Created on Oct 17, 2005
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.Pair;


/**
 * An implementation of <code>DirectedGraph</code>, suitable for sparse graphs, 
 * that orders its vertex and edge collections
 * according to insertion time.
 */
@SuppressWarnings("serial")
public class DirectedOrderedSparseMultigraph<V,E> 
    extends DirectedSparseMultigraph<V,E>
    implements DirectedGraph<V,E>, MultiGraph<V,E> 
{
    /**
     * Returns a {@code Factory} that creates an instance of this graph type.
     * @param <V> the vertex type for the graph factory
     * @param <E> the edge type for the graph factory
     */
	public static <V,E> Factory<DirectedGraph<V,E>> getFactory() {
		return new Factory<DirectedGraph<V,E>> () {
			public DirectedGraph<V,E> create() {
				return new DirectedOrderedSparseMultigraph<V,E>();
			}
		};
	}

    /**
     * Creates a new instance.
     */
    public DirectedOrderedSparseMultigraph() {
        vertices = new LinkedHashMap<V, Pair<Set<E>>>();
        edges = new LinkedHashMap<E, Pair<V>>();
    }
    
    @Override
    public boolean addVertex(V vertex) {
    	if(vertex == null) {
    		throw new IllegalArgumentException("vertex may not be null");
    	}
        if (!containsVertex(vertex)) {
            vertices.put(vertex, new Pair<Set<E>>(new LinkedHashSet<E>(), new LinkedHashSet<E>()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Collection<V> getPredecessors(V vertex) {
        if (!containsVertex(vertex)) 
            return null;
        Set<V> preds = new LinkedHashSet<V>();
        for (E edge : getIncoming_internal(vertex))
            preds.add(this.getSource(edge));
        
        return Collections.unmodifiableCollection(preds);
    }

    @Override
    public Collection<V> getSuccessors(V vertex) {
        if (!containsVertex(vertex)) 
            return null;
        Set<V> succs = new LinkedHashSet<V>();
        for (E edge : getOutgoing_internal(vertex))
            succs.add(this.getDest(edge));
        
        return Collections.unmodifiableCollection(succs);
    }

    @Override
    public Collection<V> getNeighbors(V vertex) {
        if (!containsVertex(vertex)) 
            return null;
        Collection<V> neighbors = new LinkedHashSet<V>();
        for (E edge : getIncoming_internal(vertex))
            neighbors.add(this.getSource(edge));
        for (E edge : getOutgoing_internal(vertex))
            neighbors.add(this.getDest(edge));
        return Collections.unmodifiableCollection(neighbors);
    }

    @Override
    public Collection<E> getIncidentEdges(V vertex) {
        if (!containsVertex(vertex)) 
            return null;
        Collection<E> incident = new LinkedHashSet<E>();
        incident.addAll(getIncoming_internal(vertex));
        incident.addAll(getOutgoing_internal(vertex));
        return incident;
    }

}
