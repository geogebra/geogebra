/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Jul 2, 2003
 *  
 */
package edu.uci.ics.jung.graph.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

/**
 * Provides generators for several different test graphs.
 */
public class TestGraphs {

	/**
	 * A series of pairs that may be useful for generating graphs. The
	 * miniature graph consists of 8 edges, 10 nodes, and is formed of two
	 * connected components, one of 8 nodes, the other of 2.
	 *  
	 */
	public static String[][] pairs = { { "a", "b", "3" }, {
			"a", "c", "4" }, {
			"a", "d", "5" }, {
			"d", "c", "6" }, {
			"d", "e", "7" }, {
			"e", "f", "8" }, {
			"f", "g", "9" }, {
			"h", "i", "1" }
	};

	/**
	 * Creates a small sample graph that can be used for testing purposes. The
	 * graph is as described in the section on {@link #pairs pairs}. If <code>isDirected</code>,
	 * the graph is a {@link DirectedSparseMultigraph DirectedSparseMultigraph},
	 * otherwise, it is an {@link UndirectedSparseMultigraph UndirectedSparseMultigraph}.
	 * 
	 * @return a graph consisting of eight edges and ten nodes.
	 */
	public static Graph<String, Number> createTestGraph(boolean directed) {
		Graph<String, Number> graph = null;
		if(directed) {
			graph = new DirectedSparseMultigraph<String,Number>();
		} else {
			graph = new UndirectedSparseMultigraph<String,Number>();
		}

		for (int i = 0; i < pairs.length; i++) {
			String[] pair = pairs[i];
			graph.addEdge(Integer.parseInt(pair[2]), pair[0], pair[1]);
		}
		return graph;
	}

    /**
     * Returns a graph consisting of a chain of <code>vertex_count - 1</code> vertices
     * plus one isolated vertex.
     */
    public static Graph<String,Number> createChainPlusIsolates(int chain_length, int isolate_count)
    {
    	Graph<String,Number> g = new UndirectedSparseMultigraph<String,Number>();
        if (chain_length > 0)
        {
            String[] v = new String[chain_length];
            v[0] = "v"+0;
            g.addVertex(v[0]);
            for (int i = 1; i < chain_length; i++)
            {
                v[i] = "v"+i;
                g.addVertex(v[i]);
                g.addEdge(new Double(Math.random()), v[i], v[i-1]);
            }
        }
        for (int i = 0; i < isolate_count; i++) {
            String v = "v"+(chain_length+i);
            g.addVertex(v);
        }
        return g;
    }
    
	/**
	 * Creates a sample directed acyclic graph by generating several "layers",
	 * and connecting nodes (randomly) to nodes in earlier (but never later)
	 * layers. Each layer has some random number of nodes in it 1 less than n
	 * less than maxNodesPerLayer.
	 * 
	 * @return the created graph
	 */
	public static Graph<String,Number> createDirectedAcyclicGraph(
		int layers,
		int maxNodesPerLayer,
		double linkprob) {

		DirectedGraph<String,Number> dag = new DirectedSparseMultigraph<String,Number>();
		Set<String> previousLayers = new HashSet<String>();
		Set<String> inThisLayer = new HashSet<String>();
		for (int i = 0; i < layers; i++) {

			int nodesThisLayer = (int) (Math.random() * maxNodesPerLayer) + 1;
			for (int j = 0; j < nodesThisLayer; j++) {
                String v = i+":"+j;
				dag.addVertex(v);
				inThisLayer.add(v);
				// for each previous node...
                for(String v2 : previousLayers) {
					if (Math.random() < linkprob) {
                        Double de = new Double(Math.random());
						dag.addEdge(de, v, v2);
					}
				}
			}

			previousLayers.addAll(inThisLayer);
			inThisLayer.clear();
		}
		return dag;
	}
	
	private static void createEdge(
			Graph<String, Number> g,
			String v1Label,
			String v2Label,
			int weight) {
			g.addEdge(new Double(Math.random()), v1Label, v2Label);
	}
	
	/**
	 * Returns a bigger, undirected test graph with a just one component. This
	 * graph consists of a clique of ten edges, a partial clique (randomly
	 * generated, with edges of 0.6 probability), and one series of edges
	 * running from the first node to the last.
	 * 
	 * @return the testgraph
	 */
	public static Graph<String,Number> getOneComponentGraph() {

		UndirectedGraph<String,Number> g = new UndirectedSparseMultigraph<String,Number>();
		// let's throw in a clique, too
		for (int i = 1; i <= 10; i++) {
			for (int j = i + 1; j <= 10; j++) {
				String i1 = "" + i;
				String i2 = "" + j;
				g.addEdge(Math.pow(i+2,j), i1, i2);
			}
		}

		// and, last, a partial clique
		for (int i = 11; i <= 20; i++) {
			for (int j = i + 1; j <= 20; j++) {
				if (Math.random() > 0.6)
					continue;
				String i1 = "" + i;
				String i2 = "" + j;
				g.addEdge(Math.pow(i+2,j), i1, i2);
			}
		}

		List<String> index = new ArrayList<String>();
		index.addAll(g.getVertices());
		// and one edge to connect them all
		for (int i = 0; i < index.size() - 1; i++) 
		    g.addEdge(new Integer(i), index.get(i), index.get(i+1));

		return g;
	}

	/**
	 * Returns a bigger test graph with a clique, several components, and other
	 * parts.
	 * 
	 * @return a demonstration graph of type <tt>UndirectedSparseMultigraph</tt>
	 *         with 28 vertices.
	 */
	public static Graph<String, Number> getDemoGraph() {
		UndirectedGraph<String, Number> g = 
            new UndirectedSparseMultigraph<String, Number>();

		for (int i = 0; i < pairs.length; i++) {
			String[] pair = pairs[i];
			createEdge(g, pair[0], pair[1], Integer.parseInt(pair[2]));
		}

		// let's throw in a clique, too
		for (int i = 1; i <= 10; i++) {
			for (int j = i + 1; j <= 10; j++) {
				String i1 = "c" + i;
				String i2 = "c" + j;
                g.addEdge(Math.pow(i+2,j), i1, i2);
			}
		}

		// and, last, a partial clique
		for (int i = 11; i <= 20; i++) {
			for (int j = i + 1; j <= 20; j++) {
				if (Math.random() > 0.6)
					continue;
				String i1 = "p" + i;
				String i2 = "p" + j;
                g.addEdge(Math.pow(i+2,j), i1, i2);
			}
		}
		return g;
	}

    /**
     * Returns a small graph with directed and undirected edges, and parallel edges.
     */
    public static Graph<String, Number> getSmallGraph() {
        Graph<String, Number> graph = 
            new SparseMultigraph<String, Number>();
        String[] v = new String[3];
        for (int i = 0; i < 3; i++) {
            v[i] = String.valueOf(i);
            graph.addVertex(v[i]);
        }
        graph.addEdge(new Double(0), v[0], v[1], EdgeType.DIRECTED);
        graph.addEdge(new Double(.1), v[0], v[1], EdgeType.DIRECTED);
        graph.addEdge(new Double(.2), v[0], v[1], EdgeType.DIRECTED);
        graph.addEdge(new Double(.3), v[1], v[0], EdgeType.DIRECTED);
        graph.addEdge(new Double(.4), v[1], v[0], EdgeType.DIRECTED);
        graph.addEdge(new Double(.5), v[1], v[2]);
        graph.addEdge(new Double(.6), v[1], v[2]);

        return graph;
    }
}
