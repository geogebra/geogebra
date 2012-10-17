/*
 * Copyright 2008 the original author or authors.
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package geogebra.common.kernel.discrete.alds.ds.graphs;

import geogebra.common.kernel.discrete.alds.ds.graphs.Graph.Type;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a graph where the edges have weights associated with them. This
 * class delegates all work to the {@link Graph} class except for maintaining
 * the weights of edges.
 * 
 * @author Devender Gollapally
 * 
 */
public class WeightedGraph {
	private Graph graph;
	private Map<Vertex, Map<Vertex, Integer>> edgeWeights = null;
	private static Comparator<Vertex> vertexComparator = new Comparator<Vertex>() {
		public int compare(Vertex o1, Vertex o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	/**
	 * Creates a new Graph of the given type
	 * 
	 * @see Graph
	 * @param type
	 */
	public WeightedGraph(Type type) {
		graph = new Graph(type);
		edgeWeights = new TreeMap<Vertex, Map<Vertex, Integer>>(
				vertexComparator);
	}

	/**
	 * Adds a new vertex to the graph
	 * 
	 * @param vertex
	 */
	public WeightedGraph addVertex(Vertex vertex) {
		graph.addVertex(vertex);
		return this;
	}

	/**
	 * Adds a new Edge to the graph, in addition if the vertex is not already
	 * present adds it to the graph.
	 * 
	 * @param a
	 * @param b
	 * @param weight
	 */
	public WeightedGraph addEdge(Vertex a, Vertex b, int weight) {
		graph.addEdge(a, b);
		addEdgeWeight(a, b, weight);
		if (graph.getType().equals(Type.UNDIRECTED)) {
			addEdgeWeight(b, a, weight);
		}
		return this;
	}

	/**
	 * Returns the weight of the edge if present else returns Integer.Max
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public int getEdgeWeight(Vertex a, Vertex b) {
		if (edgeWeights.containsKey(a)) {
			Map<Vertex, Integer> map = edgeWeights.get(a);
			if (map.containsKey(b)) {
				return map.get(b);
			} else {
				return Integer.MAX_VALUE;
			}
		} else {
			return Integer.MAX_VALUE;
		}
	}

	private void addEdgeWeight(Vertex a, Vertex b, int weight) {
		Map<Vertex, Integer> map = null;
		if (edgeWeights.containsKey(a)) {
			map = edgeWeights.get(a);
		} else {
			map = new TreeMap<Vertex, Integer>(vertexComparator);
		}
		map.put(b, weight);
		edgeWeights.put(a, map);
	}

	/**
	 * Deletes a given edge
	 * 
	 * @param a
	 * @param b
	 */
	public void deleteEdge(Vertex a, Vertex b) {
		graph.deleteEdge(a, b);
	}

	/**
	 * Checks if given edge is present
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public boolean containsEdge(Vertex a, Vertex b) {
		return graph.containsEdge(a, b);
	}

	/**
	 * Number of Vertices
	 * 
	 * @return int
	 */
	public int numberOfVertices() {
		return graph.numberOfVertices();
	}

	/**
	 * Number of Edges
	 * 
	 * @return int
	 */
	public int numberOfEdges() {
		return graph.numberOfEdges();
	}

	/**
	 * 
	 * @return
	 */
	public Vertex[][] getAdjacencyList() {
		return graph.getAdjacencyList();
	}

	/**
	 * 
	 * @return
	 */
	public int[][] getAdjacencyMatrix() {
		return graph.getAdjacencyMatrix();
	}

	/**
	 * 
	 * @param vertex
	 * @return
	 */
	public Vertex[] getAllAdjacentVertices(Vertex vertex) {
		return graph.getAllAdjacentVertices(vertex);
	}

	/**
	 * 
	 * @param vertex
	 * @return
	 */
	public int getOutDegree(Vertex vertex) {
		return graph.getOutDegree(vertex);
	}

	/**
	 * 
	 * @param vertex
	 * @return
	 */
	public int getInDegree(Vertex vertex) {
		return graph.getInDegree(vertex);
	}

	/**
	 * 
	 */
	public String toString() {
		return graph.toString();
	}

	/**
	 * 
	 * @return
	 */
	public Set<Vertex> getVertices() {
		return graph.getVertices();
	}

	/**
	 * 
	 * @return
	 */
	public Type getType() {
		return graph.getType();
	}
}