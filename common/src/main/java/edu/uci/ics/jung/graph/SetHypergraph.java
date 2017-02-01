/*
 * Created on Feb 4, 2007
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * An implementation of <code>Hypergraph</code> that is suitable for sparse
 * graphs and permits parallel edges.
 */
@SuppressWarnings("serial")
public class SetHypergraph<V, H>
		implements Hypergraph<V, H>, MultiGraph<V, H>, Serializable {
	protected Map<V, Set<H>> vertices; // Map of vertices to incident hyperedge
										// sets
	protected Map<H, Set<V>> edges; // Map of hyperedges to incident vertex sets

	/**
	 * Returns a <code>Factory</code> which creates instances of this class.
	 * 
	 * @param <V>
	 *            vertex type of the hypergraph to be created
	 * @param <H>
	 *            edge type of the hypergraph to be created
	 * @return a <code>Factory</code> which creates instances of this class
	 */
	public static <V, H> Factory<Hypergraph<V, H>> getFactory() {
		return new Factory<Hypergraph<V, H>>() {
			@Override
			public Hypergraph<V, H> create() {
				return new SetHypergraph<V, H>();
			}
		};
	}

	/**
	 * Creates a <code>SetHypergraph</code> and initializes the internal data
	 * structures.
	 */
	public SetHypergraph() {
		vertices = new HashMap<V, Set<H>>();
		edges = new HashMap<H, Set<V>>();
	}

	/**
	 * Adds <code>hyperedge</code> to this graph and connects them to the vertex
	 * collection <code>to_attach</code>. Any vertices in <code>to_attach</code>
	 * that appear more than once will only appear once in the incident vertex
	 * collection for <code>hyperedge</code>, that is, duplicates will be
	 * ignored.
	 * 
	 * @see Hypergraph#addEdge(Object, Collection)
	 */
	@Override
	public boolean addEdge(H hyperedge, Collection<? extends V> to_attach) {
		if (hyperedge == null) {
			throw new IllegalArgumentException(
					"input hyperedge may not be null");
		}

		if (to_attach == null) {
			throw new IllegalArgumentException("endpoints may not be null");
		}

		if (to_attach.contains(null)) {
			throw new IllegalArgumentException(
					"cannot add an edge with a null endpoint");
		}

		Set<V> new_endpoints = new HashSet<V>(to_attach);
		if (edges.containsKey(hyperedge)) {
			Collection<V> attached = edges.get(hyperedge);
			if (!attached.equals(new_endpoints)) {
				throw new IllegalArgumentException("Edge " + hyperedge
						+ " exists in this graph with endpoints " + attached);
			}
			return false;
		}
		edges.put(hyperedge, new_endpoints);
		for (V v : to_attach) {
			// add v if it's not already in the graph
			addVertex(v);

			// associate v with hyperedge
			vertices.get(v).add(hyperedge);
		}
		return true;
	}

	/**
	 * @see Hypergraph#addEdge(Object, Collection, EdgeType)
	 */
	@Override
	public boolean addEdge(H hyperedge, Collection<? extends V> to_attach,
			EdgeType edge_type) {
		if (edge_type != EdgeType.UNDIRECTED) {
			throw new IllegalArgumentException("Edge type for this "
					+ "implementation must be EdgeType.HYPER, not "
					+ edge_type);
		}
		return addEdge(hyperedge, to_attach);
	}

	/**
	 * @see Hypergraph#getEdgeType(Object)
	 */
	@Override
	public EdgeType getEdgeType(H edge) {
		if (containsEdge(edge)) {
			return EdgeType.UNDIRECTED;
		}
		return null;
	}

	@Override
	public boolean containsVertex(V vertex) {
		return vertices.keySet().contains(vertex);
	}

	@Override
	public boolean containsEdge(H edge) {
		return edges.keySet().contains(edge);
	}

	@Override
	public Collection<H> getEdges() {
		return edges.keySet();
	}

	@Override
	public Collection<V> getVertices() {
		return vertices.keySet();
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

		Set<V> neighbors = new HashSet<V>();
		for (H hyperedge : vertices.get(vertex)) {
			neighbors.addAll(edges.get(hyperedge));
		}
		return neighbors;
	}

	@Override
	public Collection<H> getIncidentEdges(V vertex) {
		return vertices.get(vertex);
	}

	@Override
	public Collection<V> getIncidentVertices(H edge) {
		return edges.get(edge);
	}

	@Override
	public H findEdge(V v1, V v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) {
			return null;
		}

		for (H h : getIncidentEdges(v1)) {
			if (isIncident(v2, h)) {
				return h;
			}
		}
		return null;
	}

	@Override
	public Collection<H> findEdgeSet(V v1, V v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) {
			return null;
		}

		Collection<H> edges1 = new ArrayList<H>();
		for (H h : getIncidentEdges(v1)) {
			if (isIncident(v2, h)) {
				edges1.add(h);
			}
		}
		return Collections.unmodifiableCollection(edges1);
	}

	@Override
	public boolean addVertex(V vertex) {
		if (vertex == null) {
			throw new IllegalArgumentException("cannot add a null vertex");
		}
		if (containsVertex(vertex)) {
			return false;
		}
		vertices.put(vertex, new HashSet<H>());
		return true;
	}

	@Override
	public boolean removeVertex(V vertex) {
		if (!containsVertex(vertex)) {
			return false;
		}
		for (H hyperedge : vertices.get(vertex)) {
			edges.get(hyperedge).remove(vertex);
		}
		vertices.remove(vertex);
		return true;
	}

	@Override
	public boolean removeEdge(H hyperedge) {
		if (!containsEdge(hyperedge)) {
			return false;
		}
		for (V vertex : edges.get(hyperedge)) {
			vertices.get(vertex).remove(hyperedge);
		}
		edges.remove(hyperedge);
		return true;
	}

	@Override
	public boolean isNeighbor(V v1, V v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) {
			return false;
		}

		if (vertices.get(v2).isEmpty()) {
			return false;
		}
		for (H hyperedge : vertices.get(v1)) {
			if (edges.get(hyperedge).contains(v2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isIncident(V vertex, H edge) {
		if (!containsVertex(vertex) || !containsEdge(edge)) {
			return false;
		}

		return vertices.get(vertex).contains(edge);
	}

	@Override
	public int degree(V vertex) {
		if (!containsVertex(vertex)) {
			return 0;
		}

		return vertices.get(vertex).size();
	}

	@Override
	public int getNeighborCount(V vertex) {
		if (!containsVertex(vertex)) {
			return 0;
		}

		return getNeighbors(vertex).size();
	}

	@Override
	public int getIncidentCount(H edge) {
		if (!containsEdge(edge)) {
			return 0;
		}

		return edges.get(edge).size();
	}

	@Override
	public int getEdgeCount(EdgeType edge_type) {
		if (edge_type == EdgeType.UNDIRECTED) {
			return edges.size();
		}
		return 0;
	}

	@Override
	public Collection<H> getEdges(EdgeType edge_type) {
		if (edge_type == EdgeType.UNDIRECTED) {
			return edges.keySet();
		}
		return null;
	}

	@Override
	public EdgeType getDefaultEdgeType() {
		return EdgeType.UNDIRECTED;
	}

	@Override
	public Collection<H> getInEdges(V vertex) {
		return getIncidentEdges(vertex);
	}

	@Override
	public Collection<H> getOutEdges(V vertex) {
		return getIncidentEdges(vertex);
	}

	@Override
	public int inDegree(V vertex) {
		return degree(vertex);
	}

	@Override
	public int outDegree(V vertex) {
		return degree(vertex);
	}

	@Override
	public V getDest(H directed_edge) {
		return null;
	}

	@Override
	public V getSource(H directed_edge) {
		return null;
	}

	@Override
	public Collection<V> getPredecessors(V vertex) {
		return getNeighbors(vertex);
	}

	@Override
	public Collection<V> getSuccessors(V vertex) {
		return getNeighbors(vertex);
	}

	@Override
	public SetHypergraph<V, H> newInstance() {
		return new SetHypergraph<V, H>();
	}
}
