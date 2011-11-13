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
package edu.uci.ics.jung.algorithms.generators.random;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * 
 * Generates a mixed-mode random graph based on the output of <code>BarabasiAlbertGenerator</code>.
 * Primarily intended for providing a heterogeneous sample graph for visualization testing, etc.
 *  
 */
public class MixedRandomGraphGenerator {

	/**
	 * Equivalent to <code>generateMixedRandomGraph(edge_weight, num_vertices, true)</code>.
	 */
	public static <V,E> Graph<V, E> generateMixedRandomGraph(
			Factory<Graph<V,E>> graphFactory,
			Factory<V> vertexFactory,
    		Factory<E> edgeFactory,
    		Map<E,Number> edge_weight, 
			int num_vertices, Set<V> seedVertices)
	{
		return generateMixedRandomGraph(graphFactory, vertexFactory, edgeFactory, 
				edge_weight, num_vertices, true, seedVertices);
	}

    /**
     * Returns a random mixed-mode graph.  Starts with a randomly generated 
     * Barabasi-Albert (preferential attachment) generator 
     * (4 initial vertices, 3 edges added at each step, and num_vertices - 4 evolution steps).
     * Then takes the resultant graph, replaces random undirected edges with directed
     * edges, and assigns random weights to each edge.
     */
    public static <V,E> Graph<V,E> generateMixedRandomGraph(
    		Factory<Graph<V,E>> graphFactory,
    		Factory<V> vertexFactory,
    		Factory<E> edgeFactory,
    		Map<E,Number> edge_weights, 
            int num_vertices, boolean parallel, Set<V> seedVertices)
    {
        int seed = (int)(Math.random() * 10000);
        BarabasiAlbertGenerator<V,E> bag = 
            new BarabasiAlbertGenerator<V,E>(graphFactory, vertexFactory, edgeFactory,
            		4, 3, //false, parallel, 
            		seed, seedVertices);
        bag.evolveGraph(num_vertices - 4);
        Graph<V, E> ug = bag.create();

        // create a SparseMultigraph version of g
        Graph<V, E> g = graphFactory.create();
        	//new SparseMultigraph<V, E>();
        for(V v : ug.getVertices()) {
        	g.addVertex(v);
        }
        
        // randomly replace some of the edges by directed edges to 
        // get a mixed-mode graph, add random weights
        
        for(E e : ug.getEdges()) {
            V v1 = ug.getEndpoints(e).getFirst();
            V v2 = ug.getEndpoints(e).getSecond();

            E me = edgeFactory.create();
            g.addEdge(me, v1, v2, Math.random() < .5 ? EdgeType.DIRECTED : EdgeType.UNDIRECTED);
            edge_weights.put(me, Math.random());
        }
        
        return g;
    }
    
}
