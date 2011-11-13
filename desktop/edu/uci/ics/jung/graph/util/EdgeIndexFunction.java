/*
 * Created on Sep 24, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph.util;

import edu.uci.ics.jung.graph.Graph;


/**
 * An interface for a service to access the index of a given edge (in a given graph)
 * into the set formed by the given edge and all the other edges it is parallel to.
 * 
 * <p>Note that in current use, this index is assumed to be an integer value in
 * the interval [0,n-1], where n-1 is the number of edges parallel to <code>e</code>.
 * 
 * @author Tom Nelson 
 *
 */
public interface EdgeIndexFunction<V,E> {
    
    /**
     * Returns <code>e</code>'s index in <code>graph</code>.
     * The index of <code>e</code> is defined as its position in some 
     * consistent ordering of <code>e</code> and all edges parallel to <code>e</code>.
     * @param graph the graph in which the edge is to be queried
     * @param e the edge whose index is to be queried
     * @return <code>e</code>'s index in <code>graph</code>
     */
    int getIndex(Graph<V,E> graph, E e);
    
    /**
     * Resets the indices for <code>edge</code> and its parallel edges in <code>graph</code>.
     * Should be invoked when an edge parallel to <code>edge</code>
     * has been added or removed.
     * 
     * @param g the graph in which <code>edge</code>'s index is to be reset
     * @param edge the edge whose index is to be reset
     */
    void reset(Graph<V,E> g, E edge);
    
    /**
     * Clears all edge indices for all edges in all graphs.
     * Does not recalculate the indices.
     */
    void reset();
}
