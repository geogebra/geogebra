/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package edu.uci.ics.jung.graph.event;

import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * 
 * @author tom nelson
 *
 * @param <V>
 *            the vertex type
 * @param <E>
 *            the edge type
 */
public abstract class GraphEvent<V, E> {

	protected Graph<V, E> source;
	protected Type type;

	/**
	 * Creates an instance with the specified {@code source} graph and
	 * {@code Type} (vertex/edge addition/removal).
	 */
	public GraphEvent(Graph<V, E> source, Type type) {
		this.source = source;
		this.type = type;
	}

	/**
	 * Types of graph events.
	 */
	public static enum Type {
		VERTEX_ADDED, VERTEX_REMOVED, EDGE_ADDED, EDGE_REMOVED
	}

	/**
	 * An event type pertaining to graph vertices.
	 */
	public static class Vertex<V, E> extends GraphEvent<V, E> {
		protected V vertex;

		/**
		 * Creates a graph event for the specified graph, vertex, and type.
		 */
		public Vertex(Graph<V, E> source, Type type, V vertex) {
			super(source, type);
			this.vertex = vertex;
		}

		/**
		 * Retrieves the vertex associated with this event.
		 */
		public V getVertex() {
			return vertex;
		}

		@Override
		public String toString() {
			return "GraphEvent type:" + type + " for " + vertex;
		}

	}

	/**
	 * An event type pertaining to graph edges.
	 */
	public static class Edge<V, E> extends GraphEvent<V, E> {
		protected E edge;

		/**
		 * Creates a graph event for the specified graph, edge, and type.
		 */
		public Edge(Graph<V, E> source, Type type, E edge) {
			super(source, type);
			this.edge = edge;
		}

		/**
		 * Retrieves the edge associated with this event.
		 */
		public E getEdge() {
			return edge;
		}

		@Override
		public String toString() {
			return "GraphEvent type:" + type + " for " + edge;
		}

	}

	/**
	 * @return the source
	 */
	public Graph<V, E> getSource() {
		return source;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
}
