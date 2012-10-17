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

import java.util.HashSet;
import java.util.Set;

/**
 * A simple class to represent a graph, has useful methods to add an edge and
 * vertex.
 * 
 * @author Devender Gollapally
 * 
 */
public final class Graph {
	private Vertex[][] adjacencyList;
	private int[][] adjacencyMatrix;
	private final Type type;

	/**
	 * Creates a new graph which can be either directed or un-directed
	 * 
	 * @param type
	 */
	public Graph(Type type) {
		adjacencyList = new Vertex[0][];
		this.type = type;
	}

	/**
	 * Clones this Graph and gives a new Graph
	 * 
	 * @return Graph
	 */
	public Graph cloneGraph() {
		Graph graph = new Graph(this.type);
		graph.adjacencyList = new Vertex[this.adjacencyList.length][];
		for (int i = 0; i < this.adjacencyList.length; i++) {
			graph.adjacencyList[i] = cloneVertexList(this.adjacencyList[i]);
		}
		graph.buildAdjacencyMatrix();
		return graph;

	}

	private Vertex[] cloneVertexList(Vertex[] vertices) {
		Vertex[] newVertices = new Vertex[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			newVertices[i] = vertices[i];
		}
		return newVertices;
	}

	/**
	 * Adds a new vertex to the graph
	 * 
	 * @param vertex
	 */
	public Graph addVertex(Vertex vertex) {
		// check for duplicates
		if (containsVertex(vertex)) {
			return this;
		}

		int preLength = adjacencyList.length;
		increaseVertices();
		adjacencyList[preLength] = new Vertex[1];
		adjacencyList[preLength][0] = vertex;
		return this;
	}

	private boolean containsVertex(Vertex vertex) {
		if (getVertexIndex(vertex) == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Adds an edge from a into b, if it is an UN directed graph will also add
	 * the edge from b into a, if the vertex was not previously added using the
	 * addVertex method, this will add it
	 * 
	 * In an un directed graph self loops are forbidden, so will not add any
	 * edges to the graph<br>
	 * NOTE: At present does not check for duplicates.
	 * 
	 * @param a
	 * @param b
	 */
	public Graph addEdge(Vertex a, Vertex b) {
		// cycles are not allowed in undirected graphs
		if (a.equals(b) && Graph.Type.UNDIRECTED == this.type) {
			return this;
		}

		// (a,b) and (b,a) are considered as same in an undirected graph
		if ((containsEdge(a, b) || containsEdge(a, b))
				&& Graph.Type.UNDIRECTED == this.type) {
			return this;
		}

		addVertex(a);
		addVertex(b);

		for (int i = 0; i < adjacencyList.length; i++) {
			Vertex[] vertexs = adjacencyList[i];

			if (vertexs[0].equals(a)) {
				vertexs = addVertexToAdjacentVerticies(vertexs, b);
				adjacencyList[i] = vertexs;
			} else if (type.equals(Type.UNDIRECTED) && vertexs[0].equals(b)) {
				vertexs = addVertexToAdjacentVerticies(vertexs, a);
				adjacencyList[i] = vertexs;
			}
		}

		buildAdjacencyMatrix();
		return this;
	}

	public void deleteEdge(Vertex a, Vertex b) {
		if (containsEdge(a, b)) {
			for (int i = 0; i < adjacencyList.length; i++) {
				Vertex[] vertexs = adjacencyList[i];

				if (vertexs[0].equals(a)) {
					vertexs = deleteVertexFromAdjacentVerticies(vertexs, b);
					adjacencyList[i] = vertexs;
				} else if (type.equals(Type.UNDIRECTED) && vertexs[0].equals(b)) {
					vertexs = deleteVertexFromAdjacentVerticies(vertexs, a);
					adjacencyList[i] = vertexs;
				}
			}
		}
		buildAdjacencyMatrix();
	}

	/**
	 * Deletes a given vertex from an array of vertices and returns back a new
	 * array.
	 * 
	 * @param vertexs
	 * @param b
	 * @return
	 */
	private Vertex[] deleteVertexFromAdjacentVerticies(Vertex[] vertexs,
			Vertex b) {
		Vertex[] vertexs2 = new Vertex[vertexs.length - 1];

		int j = 0;
		for (int i = 0; i < vertexs.length; i++) {
			if (!vertexs[i].equals(b)) {
				vertexs2[j++] = vertexs[i];
			}
		}

		return vertexs2;
	}

	/**
	 * Checks if the given edge is in graph
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean containsEdge(Vertex a, Vertex b) {
		int indexA = getVertexIndex(a);
		int indexB = getVertexIndex(b);
		int[][] m = getAdjacencyMatrix();
		if (m != null && indexA >= 0 && indexB >= 0 && m[indexA][indexB] == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a Vertex to an array of Vertices
	 * 
	 * @param vertexs
	 * @param vertex
	 * @return
	 */
	private Vertex[] addVertexToAdjacentVerticies(Vertex[] vertexs,
			Vertex vertex) {
		int preLength = vertexs.length;
		vertexs = increaseSize(vertexs);
		vertexs[preLength] = vertex;
		return vertexs;
	}

	/**
	 * Increases size of the given Vertex[] array by 1
	 * 
	 * @param vertexs
	 * @return
	 */
	private Vertex[] increaseSize(Vertex[] vertexs) {
		Vertex[] newVertexs = new Vertex[vertexs.length + 1];
		for (int i = 0; i < vertexs.length; i++) {
			newVertexs[i] = vertexs[i];
		}
		return newVertexs;
	}

	/**
	 * Increases the total number of vertices in this graph by one
	 * 
	 * @return
	 */
	private Vertex[][] increaseVertices() {
		Vertex[][] newAdjacencyList = new Vertex[adjacencyList.length + 1][];

		for (int i = 0; i < adjacencyList.length; i++) {
			newAdjacencyList[i] = adjacencyList[i];
		}

		adjacencyList = newAdjacencyList;
		return adjacencyList;
	}

	/**
	 * Returns the total number of vertices in this graph
	 * 
	 * @return
	 */
	public int numberOfVertices() {
		return adjacencyList.length;
	}

	/**
	 * Returns the total number of edges in this graph
	 * 
	 * @return
	 */
	public int numberOfEdges() {
		int numberOfEdges = 0;
		for (int i = 0; i < numberOfVertices(); i++) {
			numberOfEdges = numberOfEdges + adjacencyList[i].length - 1;
		}
		return numberOfEdges;
	}

	/**
	 * Returns the internal graph representation in the form of an adjacency
	 * list
	 * 
	 * @return
	 */
	public Vertex[][] getAdjacencyList() {
		return adjacencyList;
	}

	/**
	 * Builds the Adjancency Matrix
	 */
	private void buildAdjacencyMatrix() {
		int[][] matrix = new int[numberOfVertices()][];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new int[numberOfVertices()];
		}

		for (int i = 0; i < numberOfVertices(); i++) {
			Vertex[] edges = adjacencyList[i];
			if (edges.length > 1) {
				int indexOfRow = getVertexIndex(edges[0]);
				for (int j = 1; j < edges.length; j++) {
					int indexOfColumn = getVertexIndex(edges[j]);
					matrix[indexOfRow][indexOfColumn] = 1;
				}
			}
		}
		adjacencyMatrix = matrix;
	}

	/**
	 * Returns the internal graph representation in the form of a adjacency
	 * matrix
	 */
	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	/**
	 * If the given vertex is in the graph will return all the adjacent vertices
	 * of the given vertex
	 * 
	 * @param vertex
	 * @return
	 */
	public Vertex[] getAllAdjacentVertices(Vertex vertex) {
		if (containsVertex(vertex)) {
			int index = getVertexIndex(vertex);
			Vertex[] vertexs = adjacencyList[index];

			if (vertexs.length > 1) {
				Vertex[] adjacent = new Vertex[vertexs.length - 1];
				for (int i = 1; i < vertexs.length; i++) {
					adjacent[i - 1] = vertexs[i];
				}
				return adjacent;
			}
		}

		return null;
	}

	/**
	 * Returns the index of the vertex in the adjacency list if the vertex is
	 * not present else returns -1
	 * 
	 * @param vertex
	 * @return
	 */
	private int getVertexIndex(Vertex vertex) {
		for (int i = 0; i < numberOfVertices(); i++) {
			if (adjacencyList[i][0].equals(vertex)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * If the given vertex is in the graph returns the number of edges
	 * entering/incident to the given vertex. For an undirected graph the in
	 * degree and out degree is the same.
	 * 
	 * @param vertex
	 * @return
	 */
	public int getOutDegree(Vertex vertex) {
		int index = getVertexIndex(vertex);
		if (index != -1) {
			return adjacencyList[index].length - 1;
		}
		return 0;
	}

	public int getInDegree(Vertex vertex) {
		// if undirected in==out
		if (Type.UNDIRECTED == this.type) {
			return getOutDegree(vertex);
		} else {
			// get adjacency matrix and sum the the col which represents the
			// vertex
			int indegree = 0;
			int index = getVertexIndex(vertex);
			if (index != -1) {
				for (int[] col : adjacencyMatrix) {
					indegree = indegree + col[index];
				}
			}
			return indegree;
		}
	}

	/**
	 * Prints the graph's adjacency list
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < numberOfVertices(); i++) {
			Vertex[] vertexs = adjacencyList[i];
			for (int j = 0; j < vertexs.length; j++) {
				builder.append(vertexs[j]);
				builder.append("->");
			}
			builder.append("/ \n");
		}

		return builder.toString();
	}

	/**
	 * Useful to print out the graph as an adjacency matrix
	 * 
	 * @return
	 */
	public String matrixToString() {
		StringBuilder builder = new StringBuilder();
		int[][] matrix = getAdjacencyMatrix();
		for (int i = 0; i < matrix.length; i++) {
			int edges[] = matrix[i];
			for (int j = 0; j < edges.length; j++) {
				builder.append(edges[j]);
				builder.append(" ");
			}
			builder.append("\n");
		}

		return builder.toString();
	}

	/**
	 * Returns a list of all vertices in this graph
	 * 
	 * @return
	 */
	public Set<Vertex> getVertices() {
		Set<Vertex> set = new HashSet<Vertex>(numberOfVertices());
		for (int i = 0; i < numberOfVertices(); i++) {
			set.add(adjacencyList[i][0]);
		}
		return set;
	}

	/**
	 * Returns the graph type of this graph
	 * 
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Represents the type of Graph either a directed or un-directed
	 * 
	 * @author Devender Gollapally
	 * 
	 */
	public static enum Type {
		DIRECTED, UNDIRECTED;
	}

}