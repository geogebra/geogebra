/*
 * Created on Mar 26, 2007
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
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An implementation of <code>DirectedGraph</code> suitable for sparse graphs.
 */
@SuppressWarnings("serial")
public class DirectedSparseGraph<V, E> extends AbstractTypedGraph<V, E>
		implements DirectedGraph<V, E> {
	/**
	 * Returns a {@code Factory} that creates an instance of this graph type.
	 * 
	 * @param <V>
	 *            the vertex type for the graph factory
	 * @param <E>
	 *            the edge type for the graph factory
	 */
	public static final <V, E> Supplier<DirectedGraph<V, E>> getFactory() {
		return new Supplier<DirectedGraph<V, E>>() {
			@Override
			public DirectedGraph<V, E> get() {
				return new DirectedSparseGraph<V, E>();
			}
		};
	}

	protected Map<V, Pair<Map<V, E>>> vertices; // Map of vertices to Pair of
												// adjacency maps {incoming,
												// outgoing}
												// of neighboring vertices to
												// incident edges
	protected Map<E, Pair<V>> edges; // Map of edges to incident vertex pairs

	/**
	 * Creates an instance.
	 */
	public DirectedSparseGraph() {
		super(EdgeType.DIRECTED);
		vertices = new HashMap<V, Pair<Map<V, E>>>();
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

		V source = new_endpoints.getFirst();
		V dest = new_endpoints.getSecond();

		if (findEdge(source, dest) != null) {
			return false;
		}

		edges.put(edge, new_endpoints);

		if (!vertices.containsKey(source)) {
			this.addVertex(source);
		}

		if (!vertices.containsKey(dest)) {
			this.addVertex(dest);
		}

		// map source of this edge to <dest, edge> and vice versa
		vertices.get(source).getSecond().put(dest, edge);
		vertices.get(dest).getFirst().put(source, edge);

		return true;
	}

	@Override
	public E findEdge(V v1, V v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) {
			return null;
		}
		return vertices.get(v1).getSecond().get(v2);
	}

	@Override
	public Collection<E> findEdgeSet(V v1, V v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) {
			return null;
		}
		ArrayList<E> edge_collection = new ArrayList<E>(1);
		E e = findEdge(v1, v2);
		if (e == null) {
			return edge_collection;
		}
		edge_collection.add(e);
		return edge_collection;
	}

	protected Collection<E> getIncoming_internal(V vertex) {
		return vertices.get(vertex).getFirst().values();
	}

	protected Collection<E> getOutgoing_internal(V vertex) {
		return vertices.get(vertex).getSecond().values();
	}

	protected Collection<V> getPreds_internal(V vertex) {
		return vertices.get(vertex).getFirst().keySet();
	}

	protected Collection<V> getSuccs_internal(V vertex) {
		return vertices.get(vertex).getSecond().keySet();
	}

	@Override
	public Collection<E> getInEdges(V vertex) {
		if (!containsVertex(vertex)) {
			return null;
		}
		return Collections.unmodifiableCollection(getIncoming_internal(vertex));
	}

	@Override
	public Collection<E> getOutEdges(V vertex) {
		if (!containsVertex(vertex)) {
			return null;
		}
		return Collections.unmodifiableCollection(getOutgoing_internal(vertex));
	}

	@Override
	public Collection<V> getPredecessors(V vertex) {
		if (!containsVertex(vertex)) {
			return null;
		}
		return Collections.unmodifiableCollection(getPreds_internal(vertex));
	}

	@Override
	public Collection<V> getSuccessors(V vertex) {
		if (!containsVertex(vertex)) {
			return null;
		}
		return Collections.unmodifiableCollection(getSuccs_internal(vertex));
	}

	@Override
	public Pair<V> getEndpoints(E edge) {
		if (!containsEdge(edge)) {
			return null;
		}
		return edges.get(edge);
	}

	@Override
	public V getSource(E directed_edge) {
		if (!containsEdge(directed_edge)) {
			return null;
		}
		return edges.get(directed_edge).getFirst();
	}

	@Override
	public V getDest(E directed_edge) {
		if (!containsEdge(directed_edge)) {
			return null;
		}
		return edges.get(directed_edge).getSecond();
	}

	@Override
	public boolean isSource(V vertex, E edge) {
		if (!containsEdge(edge) || !containsVertex(vertex)) {
			return false;
		}
		return vertex.equals(this.getEndpoints(edge).getFirst());
	}

	@Override
	public boolean isDest(V vertex, E edge) {
		if (!containsEdge(edge) || !containsVertex(vertex)) {
			return false;
		}
		return vertex.equals(this.getEndpoints(edge).getSecond());
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

		Collection<V> neighbors = new HashSet<V>();
		neighbors.addAll(getPreds_internal(vertex));
		neighbors.addAll(getSuccs_internal(vertex));
		return Collections.unmodifiableCollection(neighbors);
	}

	@Override
	public Collection<E> getIncidentEdges(V vertex) {
		if (!containsVertex(vertex)) {
			return null;
		}

		Collection<E> incident_edges = new HashSet<E>();
		incident_edges.addAll(getIncoming_internal(vertex));
		incident_edges.addAll(getOutgoing_internal(vertex));
		return Collections.unmodifiableCollection(incident_edges);
	}

	@Override
	public boolean addVertex(V vertex) {
		if (vertex == null) {
			throw new IllegalArgumentException("vertex may not be null");
		}
		if (!containsVertex(vertex)) {
			vertices.put(vertex, new Pair<Map<V, E>>(new HashMap<V, E>(),
					new HashMap<V, E>()));
			return true;
		}
		return false;
	}

	@Override
	public boolean removeVertex(V vertex) {
		if (!containsVertex(vertex)) {
			return false;
		}

		// copy to avoid concurrent modification in removeEdge
		ArrayList<E> incident = new ArrayList<E>(getIncoming_internal(vertex));
		incident.addAll(getOutgoing_internal(vertex));

		for (E edge : incident) {
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

		Pair<V> endpoints = this.getEndpoints(edge);
		V source = endpoints.getFirst();
		V dest = endpoints.getSecond();

		// remove vertices from each others' adjacency maps
		vertices.get(source).getSecond().remove(dest);
		vertices.get(dest).getFirst().remove(source);

		edges.remove(edge);
		return true;
	}

	@Override
	public DirectedSparseGraph<V, E> newInstance() {
		return new DirectedSparseGraph<V, E>();
	}
}
