/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.generators.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.GraphGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * Generates a random graph using the Erdos-Renyi binomial model
 * (each pair of vertices is connected with probability p).
 * 
 *  @author William Giordano, Scott White, Joshua O'Madadhain
 */
public class ErdosRenyiGenerator<V,E> implements GraphGenerator<V,E> {
    private int mNumVertices;
    private double mEdgeConnectionProbability;
    private Random mRandom;
    Factory<UndirectedGraph<V,E>> graphFactory;
    Factory<V> vertexFactory;
    Factory<E> edgeFactory;

    /**
     *
     * @param numVertices number of vertices graph should have
     * @param p Connection's probability between 2 vertices
     */
	public ErdosRenyiGenerator(Factory<UndirectedGraph<V,E>> graphFactory,
			Factory<V> vertexFactory, Factory<E> edgeFactory,
			int numVertices,double p)
    {
        if (numVertices <= 0) {
            throw new IllegalArgumentException("A positive # of vertices must be specified.");
        }
        mNumVertices = numVertices;
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("p must be between 0 and 1.");
        }
        this.graphFactory = graphFactory;
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
        mEdgeConnectionProbability = p;
        mRandom = new Random();
	}

    /**
     * Returns a graph in which each pair of vertices is connected by 
     * an undirected edge with the probability specified by the constructor.
     */
	public Graph<V,E> create() {
        UndirectedGraph<V,E> g = graphFactory.create();
        for(int i=0; i<mNumVertices; i++) {
        	g.addVertex(vertexFactory.create());
        }
        List<V> list = new ArrayList<V>(g.getVertices());

		for (int i = 0; i < mNumVertices-1; i++) {
            V v_i = list.get(i);
			for (int j = i+1; j < mNumVertices; j++) {
                V v_j = list.get(j);
				if (mRandom.nextDouble() < mEdgeConnectionProbability) {
					g.addEdge(edgeFactory.create(), v_i, v_j);
				}
			}
		}
        return g;
    }

    /**
     * Sets the seed of the internal random number generator to {@code seed}.
     * Enables consistent behavior.
     */
    public void setSeed(long seed) {
        mRandom.setSeed(seed);
    }
}











