/*
 * Copyright (c) 2009, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */

package edu.uci.ics.jung.algorithms.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Simple generator of an m x n lattice where each vertex
 * is incident with each of its neighbors (to the left, right, up, and down).
 * May be toroidal, in which case the vertices on the edges are connected to
 * their counterparts on the opposite edges as well.
 * 
 * <p>If the graph factory supplied has a default edge type of {@code EdgeType.DIRECTED},
 * then edges will be created in both directions between adjacent vertices.
 * 
 * @author Joshua O'Madadhain
 */
public class Lattice2DGenerator<V,E> implements GraphGenerator<V,E>
{
    protected int row_count;
    protected int col_count;
    protected boolean is_toroidal;
    protected boolean is_directed;
    protected Factory<? extends Graph<V, E>> graph_factory;
    protected Factory<V> vertex_factory;
    protected Factory<E> edge_factory;
    private List<V> v_array;

    /**
     * Constructs a generator of square lattices of size {@code latticeSize} 
     * with the specified parameters.
     * 
     * @param graph_factory used to create the {@code Graph} for the lattice
     * @param vertex_factory used to create the lattice vertices
     * @param edge_factory used to create the lattice edges
     * @param latticeSize the number of rows and columns of the lattice
     * @param isToroidal if true, the created lattice wraps from top to bottom and left to right
     */
    public Lattice2DGenerator(Factory<? extends Graph<V,E>> graph_factory, Factory<V> vertex_factory, 
            Factory<E> edge_factory, int latticeSize, boolean isToroidal)
    {
        this(graph_factory, vertex_factory, edge_factory, latticeSize, latticeSize, isToroidal);
    }

    /**
     * Creates a generator of {@code row_count} x {@code col_count} lattices 
     * with the specified parameters.
     * 
     * @param graph_factory used to create the {@code Graph} for the lattice
     * @param vertex_factory used to create the lattice vertices
     * @param edge_factory used to create the lattice edges
     * @param row_count the number of rows in the lattice
     * @param col_count the number of columns in the lattice
     * @param isToroidal if true, the created lattice wraps from top to bottom and left to right
     */
    public Lattice2DGenerator(Factory<? extends Graph<V,E>> graph_factory, Factory<V> vertex_factory, 
            Factory<E> edge_factory, int row_count, int col_count, boolean isToroidal)
    {
        if (row_count < 2 || col_count < 2)
        {
            throw new IllegalArgumentException("Row and column counts must each be at least 2.");
        }

        this.row_count = row_count;
        this.col_count = col_count;
        this.is_toroidal = isToroidal;
        this.graph_factory = graph_factory;
        this.vertex_factory = vertex_factory;
        this.edge_factory = edge_factory;
        this.is_directed = (graph_factory.create().getDefaultEdgeType() == EdgeType.DIRECTED);
    }
    
    /**
     * @see edu.uci.ics.jung.algorithms.generators.GraphGenerator#create()
     */
    @SuppressWarnings("unchecked")
    public Graph<V,E> create()
    {
        int vertex_count = row_count * col_count;
        Graph<V,E> graph = graph_factory.create();
        v_array = new ArrayList<V>(vertex_count);
        for (int i = 0; i < vertex_count; i++)
        {
            V v = vertex_factory.create();
            graph.addVertex(v);
            v_array.add(i, v);
        }

        int start = is_toroidal ? 0 : 1;
        int end_row = is_toroidal ? row_count : row_count - 1;
        int end_col = is_toroidal ? col_count : col_count - 1;
        
        // fill in edges
        // down
        for (int i = 0; i < end_row; i++)
            for (int j = 0; j < col_count; j++)
                graph.addEdge(edge_factory.create(), getVertex(i,j), getVertex(i+1, j));
        // right
        for (int i = 0; i < row_count; i++)
            for (int j = 0; j < end_col; j++)
                graph.addEdge(edge_factory.create(), getVertex(i,j), getVertex(i, j+1));

        // if the graph is directed, fill in the edges going the other direction...
        if (graph.getDefaultEdgeType() == EdgeType.DIRECTED)
        {
            // up
            for (int i = start; i < row_count; i++)
                for (int j = 0; j < col_count; j++)
                    graph.addEdge(edge_factory.create(), getVertex(i,j), getVertex(i-1, j));
            // left
            for (int i = 0; i < row_count; i++)
                for (int j = start; j < col_count; j++)
                    graph.addEdge(edge_factory.create(), getVertex(i,j), getVertex(i, j-1));
        }
        
        return graph;
    }

    /**
     * Returns the number of edges found in a lattice of this generator's specifications.
     * (This is useful for subclasses that may modify the generated graphs to add more edges.)
     */
    public int getGridEdgeCount()
    {
        int boundary_adjustment = (is_toroidal ? 0 : 1);
        int vertical_edge_count = col_count * (row_count - boundary_adjustment);
        int horizontal_edge_count = row_count * (col_count - boundary_adjustment);
        
        return (vertical_edge_count + horizontal_edge_count) * (is_directed ? 2 : 1);
    }
    
    protected int getIndex(int i, int j)
    {
        return ((mod(i, row_count)) * col_count) + (mod(j, col_count));
    }

    protected int mod(int i, int modulus) 
    {
        int i_mod = i % modulus;
        return i_mod >= 0 ? i_mod : i_mod + modulus;
    }
    
    /**
     * Returns the vertex at position ({@code i mod row_count, j mod col_count}).
     */
    protected V getVertex(int i, int j)
    {
        return v_array.get(getIndex(i, j));
    }
    
    /**
     * Returns the {@code i}th vertex (counting row-wise).
     */
    protected V getVertex(int i)
    {
        return v_array.get(i);
    }
    
    /**
     * Returns the row in which vertex {@code i} is found.
     */
    protected int getRow(int i)
    {
        return i / row_count;
    }
    
    /**
     * Returns the column in which vertex {@code i} is found.
     */
    protected int getCol(int i)
    {
        return i % col_count;
    }
}
