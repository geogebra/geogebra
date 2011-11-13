
package edu.uci.ics.jung.algorithms.generators.random;

/*
* Copyright (c) 2009, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.Lattice2DGenerator;
import edu.uci.ics.jung.algorithms.util.WeightedChoice;
import edu.uci.ics.jung.graph.Graph;

/**
 * Graph generator that produces a random graph with small world properties. 
 * The underlying model is an mxn (optionally toroidal) lattice. Each node u 
 * has four local connections, one to each of its neighbors, and
 * in addition 1+ long range connections to some node v where v is chosen randomly according to
 * probability proportional to d^-alpha where d is the lattice distance between u and v and alpha
 * is the clustering exponent.
 * 
 * @see "Navigation in a small world J. Kleinberg, Nature 406(2000), 845."
 * @author Joshua O'Madadhain
 */
public class KleinbergSmallWorldGenerator<V, E> extends Lattice2DGenerator<V, E> {
    private double clustering_exponent;
    private Random random;
    private int num_connections = 1;
    
    /**
     * Creates 
     * @param graph_factory
     * @param vertex_factory
     * @param edge_factory
     * @param latticeSize
     * @param clusteringExponent
     */
    public KleinbergSmallWorldGenerator(Factory<? extends Graph<V,E>> graph_factory, Factory<V> vertex_factory, 
            Factory<E> edge_factory, int latticeSize, double clusteringExponent) 
    {
        this(graph_factory, vertex_factory, edge_factory, latticeSize, latticeSize, clusteringExponent);
    }

    /**
     * @param graph_factory
     * @param vertex_factory
     * @param edge_factory
     * @param row_count
     * @param col_count
     * @param clusteringExponent
     */
    public KleinbergSmallWorldGenerator(Factory<? extends Graph<V,E>> graph_factory, Factory<V> vertex_factory, 
            Factory<E> edge_factory, int row_count, int col_count, double clusteringExponent) 
    {
        super(graph_factory, vertex_factory, edge_factory, row_count, col_count, true);
        clustering_exponent = clusteringExponent;
        initialize();
    }

    /**
     * @param graph_factory
     * @param vertex_factory
     * @param edge_factory
     * @param row_count
     * @param col_count
     * @param clusteringExponent
     * @param isToroidal
     */
    public KleinbergSmallWorldGenerator(Factory<? extends Graph<V,E>> graph_factory, Factory<V> vertex_factory, 
            Factory<E> edge_factory, int row_count, int col_count, double clusteringExponent, 
            boolean isToroidal) 
    {
        super(graph_factory, vertex_factory, edge_factory, row_count, col_count, isToroidal);
        clustering_exponent = clusteringExponent;
        initialize();
    }

    private void initialize()
    {
        this.random = new Random();
    }
    
    /**
     * Sets the {@code Random} instance used by this instance.  Useful for 
     * unit testing.
     */
    public void setRandom(Random random)
    {
        this.random = random;
    }
    
    /**
     * Sets the seed of the internal random number generator.  May be used to provide repeatable
     * experiments.
     */
    public void setRandomSeed(long seed) 
    {
        random.setSeed(seed);
    }

    /**
     * Sets the number of new 'small-world' connections (outgoing edges) to be added to each vertex.
     */
    public void setConnectionCount(int num_connections)
    {
        if (num_connections <= 0)
        {
            throw new IllegalArgumentException("Number of new connections per vertex must be >= 1");
        }
        this.num_connections = num_connections;
    }

    /**
     * Returns the number of new 'small-world' connections to be made to each vertex.
     */
    public int getConnectionCount()
    {
        return this.num_connections;
    }
    
    /**
     * Generates a random small world network according to the parameters given
     * @return a random small world graph
     */
    @Override
    public Graph<V,E> create() 
    {
        Graph<V, E> graph = super.create();
        
        // TODO: For toroidal graphs, we can make this more clever by pre-creating the WeightedChoice object
        // and using the output as an offset to the current vertex location.
        WeightedChoice<V> weighted_choice;
        
        // Add long range connections
        for (int i = 0; i < graph.getVertexCount(); i++)
        {
            V source = getVertex(i);
            int row = getRow(i);
            int col = getCol(i);
            int row_offset = row < row_count/2 ? -row_count : row_count;
            int col_offset = col < col_count/2 ? -col_count : col_count;

            Map<V, Float> vertex_weights = new HashMap<V, Float>();
            for (int j = 0; j < row_count; j++)
            {
                for (int k = 0; k < col_count; k++)
                {
                    if (j == row && k == col)
                        continue;
                    int v_dist = Math.abs(j - row);
                    int h_dist = Math.abs(k - col);
                    if (is_toroidal)
                    {
                        v_dist = Math.min(v_dist, Math.abs(j - row+row_offset));
                        h_dist = Math.min(h_dist, Math.abs(k - col+col_offset));
                    }
                    int distance = v_dist + h_dist;
                    if (distance < 2)
                        continue;
                    else
                        vertex_weights.put(getVertex(j,k), (float)Math.pow(distance, -clustering_exponent));
                }
            }

            for (int j = 0; j < this.num_connections; j++) {
                weighted_choice = new WeightedChoice<V>(vertex_weights, random);
                V target = weighted_choice.nextItem();
                graph.addEdge(edge_factory.create(), source, target);
            }
        }

        return graph;
    }
}
