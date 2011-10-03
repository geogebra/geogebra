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

import java.util.Collection;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.jung.graph.Graph;

/**
 * Transforms the input graph into one which contains only those vertices 
 * that pass the specified <code>Predicate</code>.  The filtered graph
 * is a copy of the original graph (same type, uses the same vertex and
 * edge objects).  Only those edges whose entire incident vertex collection
 * passes the predicate are copied into the new graph.
 * 
 * @author Joshua O'Madadhain
 */
public class VertexPredicateFilter<V,E> implements Filter<V,E>
{
    protected Predicate<V> vertex_pred;

    /**
     * Creates an instance based on the specified vertex <code>Predicate</code>.
     * @param vertex_pred   the predicate that specifies which vertices to add to the filtered graph
     */
    public VertexPredicateFilter(Predicate<V> vertex_pred)
    {
        this.vertex_pred = vertex_pred;
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
            if (vertex_pred.evaluate(v))
                filtered.addVertex(v);
        
        Collection<V> filtered_vertices = filtered.getVertices();
        
        for (E e : g.getEdges())
        {
            Collection<V> incident = g.getIncidentVertices(e);
            if (filtered_vertices.containsAll(incident))
                filtered.addEdge(e, incident);
        }
        
        return filtered;
    }

}
