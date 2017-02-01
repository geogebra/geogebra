/*
 * Created on Apr 1, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
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
import java.util.Map;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An implementation of <code>UndirectedGraph</code> that is suitable for sparse
 * graphs.
 */
@SuppressWarnings("serial")
public class UndirectedSparseGraph<V, E> extends AbstractTypedGraph<V, E>
		implements UndirectedGraph<V, E> {

	/**
	 * Returns a {@code Factory} that creates an instance of this graph type.
	 * 
	 * @param <V>
	 *            the vertex type for the graph factory
	 * @param <E>
	 *            the edge type for the graph factory
	 */
	public static <V, E> Factory<UndirectedGraph<V, E>> getFactory() {
		return new Factory<UndirectedGraph<V, E>>() {

			@Override
			public UndirectedGraph<V, E> create() {
				return new UndirectedSparseGraph<V, E>();
			}
		};
	}

	protected Map<V, Map<V, E>> vertices; // Map of vertices to adjacency maps
											// of vertices to incident edges
	protected Map<E, Pair<V>> edges; // Map of edges to incident vertex sets

	/**
	 * Creates an instance.
	 */
	public UndirectedSparseGraph() {
		super(EdgeType.UNDIRECTED);
		vertices = new HashMap<V, Map<V, E>>();
		edges = new HashMap<E, Pair<V>>();
	}

	@Override
	public boolean addEdge(E edge, Pair<? extends V> endpoints,
			EdgeType edgeType) {
		this.validateEdgeType(edgeType);
		Pair<V> new_endpoints = getValidatedEndpoints(edge, endpoints);
		if (new_endpoints == null) {
			return false;
		}

		V v1 = new_endpoints.getFirst();
		V v2 = new_endpoints.getSecond();

		if (findEdge(v1, v2) != null) {
			return false;
		}

		edges.put(edge, new_endpoints);

		if (!vertices.containsKey(v1)) {
			this.addVertex(v1);
		}

		if (!vertices.containsKey(v2)) {
			this.addVertex(v2);
		}

		// map v1 to <v2, edge> and vice versa
		vertices.get(v1).put(v2, edge);
		vertices.get(v2).put(v1, edge);

		return true;
	}

	@Override
	public Collection<E> getInEdges(V vertex) {
		return this.getIncidentEdges(vertex);
	}

	@Override
	public Collection<E> getOutEdges(V vertex) {
		return this.getIncidentEdges(vertex);
	}

	@Override
	public Collection<V> getPredecessors(V vertex) {
		return this.getNeighbors(vertex);
	}

	@Override
	public Collection<V> getSuccessors(V vertex) {
		return this.getNeighbors(vertex);
	}

	@Override
	public E findEdge(V v1, V v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) {
			return null;
		}
		return vertices.get(v1).get(v2);
	}

	@Override
	public Collection<E> findEdgeSet(V v1, V v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) {
			return null;
		}
		ArrayList<E> edge_collection = new ArrayList<E>(1);
		// if (!containsVertex(v1) || !containsVertex(v2))
		// return edge_collection;
		E e = findEdge(v1, v2);
		if (e == null) {
			return edge_collection;
		}
		edge_collection.add(e);
		return edge_collection;
	}

	@Override
	public Pair<V> getEndpoints(E edge) {
		return edges.get(edge);
	}

	@Override
	public V getSource(E directed_edge) {
		return null;
	}

	@Override
	public V getDest(E directed_edge) {
		return null;
	}

	@Override
	public boolean isSource(V vertex, E edge) {
		return false;
	}

	@Override
	public boolean isDest(V vertex, E edge) {
		return false;
	}

	@Override
	public Collection<E> getEdges() {
		return Collections.unmodifiableCollection(edges.keySet());
	}

	@Override
	public Collection<V> getVertices() {
		return Collections.unmodifiableCollection(vertices.keySet());
	}

	@Override
	public boolean containsVertex(V vertex) {
		return vertices.containsKey(vertex);
	}

	@Override
	public boolean containsEdge(E edge) {
		return edges.containsKey(edge);
	}

	@Override
	public int getEdgeCount() {
		return edges.size();
	}

	@Override
	public int getVertexCount() {
		return vertices.size();
	}

	@Override
	public Collection<V> getNeighbors(V vertex) {
		if (!containsVertex(vertex)) {
			return null;
		}
		return Collections
				.unmodifiableCollection(vertices.get(vertex).keySet());
	}

	@Override
	public Collection<E> getIncidentEdges(V vertex) {
		if (!containsVertex(vertex)) {
			return null;
		}
		return Collections
				.unmodifiableCollection(vertices.get(vertex).values());
	}

	@Override
	public boolean addVertex(V vertex) {
		if (vertex == null) {
			throw new IllegalArgumentException("vertex may not be null");
		}
		if (!containsVertex(vertex)) {
			vertices.put(vertex, new HashMap<V, E>());
			return true;
		}
		return false;
	}

	@Override
	public boolean removeVertex(V vertex) {
		if (!containsVertex(vertex)) {
			return false;
		}

		// iterate over copy of incident edge collection
		for (E edge : new ArrayList<E>(vertices.get(vertex).values())) {
			removeEdge(edge);
		}

		vertices.remove(vertex);
		return true;
	}

	@Override
	public boolean removeEdge(E edge) {
		if (!containsEdge(edge)) {
			return false;
		}

		Pair<V> endpoints = getEndpoints(edge);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		// remove incident vertices from each others' adjacency maps
		vertices.get(v1).remove(v2);
		vertices.get(v2).remove(v1);

		edges.remove(edge);
		return true;
	}

	@Override
	public UndirectedSparseGraph<V, E> newInstance() {
		return new UndirectedSparseGraph<V, E>();
	}

}
