/*
 * Created on May 19, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.filters;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.jung.graph.Graph;

/**
 * Transforms the input graph into one which contains only those edges 
 * that pass the specified <code>Predicate</code>.  The filtered graph
 * is a copy of the original graph (same type, uses the same vertex and
 * edge objects).  All vertices from the original graph
 * are copied into the new graph (even if they are not incident to any
 * edges in the new graph).
 * 
 * @author Joshua O'Madadhain
 */
public class EdgePredicateFilter<V, E> implements Filter<V, E>
{
    protected Predicate<E> edge_pred;

    /**
     * Creates an instance based on the specified edge <code>Predicate</code>.
     * @param edge_pred   the predicate that specifies which edges to add to the filtered graph
     */
    public EdgePredicateFilter(Predicate<E> edge_pred)
    {
        this.edge_pred = edge_pred;
    }
    
    @SuppressWarnings("unchecked")
    public Graph<V,E> transform(Graph<V,E> g)
    {
        Graph<V, E> filtered;
        try
        {
            filtered = g.getClass().newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Unable to create copy of existing graph: ", e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Unable to create copy of existing graph: ", e);
        }

        for (V v : g.getVertices())
            filtered.addVertex(v);
        
        for (E e : g.getEdges())
        {
            if (edge_pred.evaluate(e))
                filtered.addEdge(e, g.getIncidentVertices(e));
        }
        
        return filtered;
    }

}
