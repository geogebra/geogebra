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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An implementation of <code>Graph</code> that orders its vertex and edge collections
 * according to insertion time, is suitable for sparse graphs, and 
 * permits directed, undirected, and parallel edges.
 */
@SuppressWarnings("serial")
public class OrderedSparseMultigraph<V,E> 
    extends SparseMultigraph<V,E>
    implements MultiGraph<V,E> {
	
    /**
     * Returns a {@code Factory} that creates an instance of this graph type.
     * @param <V> the vertex type for the graph factory
     * @param <E> the edge type for the graph factory
     */
	public static <V,E> Factory<Graph<V,E>> getFactory() { 
		return new Factory<Graph<V,E>> () {
			public Graph<V,E> create() {
				return new OrderedSparseMultigraph<V,E>();
			}
		};
	}

    /**
     * Creates a new instance.
     */
    public OrderedSparseMultigraph()
    {
        vertices = new LinkedHashMap<V, Pair<Set<E>>>();
        edges = new LinkedHashMap<E, Pair<V>>();
        directedEdges = new LinkedHashSet<E>();
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
    public Collection<V> getPredecessors(V vertex)
    {
        if (!containsVertex(vertex))
            return null;
        
        Set<V> preds = new LinkedHashSet<V>();
        for (E edge : getIncoming_internal(vertex)) {
        	if(getEdgeType(edge) == EdgeType.DIRECTED) {
        		preds.add(this.getSource(edge));
        	} else {
        		preds.add(getOpposite(vertex, edge));
        	}
        }
        return Collections.unmodifiableCollection(preds);
    }

    @Override
    public Collection<V> getSuccessors(V vertex)
    {
        if (!containsVertex(vertex))
            return null;

        Set<V> succs = new LinkedHashSet<V>();
        for (E edge : getOutgoing_internal(vertex)) {
        	if(getEdgeType(edge) == EdgeType.DIRECTED) {
        		succs.add(this.getDest(edge));
        	} else {
        		succs.add(getOpposite(vertex, edge));
        	}
        }
        return Collections.unmodifiableCollection(succs);
    }

    @Override
    public Collection<V> getNeighbors(V vertex)
    {
        if (!containsVertex(vertex))
            return null;

        Collection<V> out = new LinkedHashSet<V>();
        out.addAll(this.getPredecessors(vertex));
        out.addAll(this.getSuccessors(vertex));
        return out;
    }

    @Override
    public Collection<E> getIncidentEdges(V vertex)
    {
        if (!containsVertex(vertex))
            return null;
        
        Collection<E> out = new LinkedHashSet<E>();
        out.addAll(this.getInEdges(vertex));
        out.addAll(this.getOutEdges(vertex));
        return out;
    }
}
