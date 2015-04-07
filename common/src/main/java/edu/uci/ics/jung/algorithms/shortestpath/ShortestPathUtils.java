/*
 * Created on Jul 10, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Utilities relating to the shortest paths in a graph.
 */
public class ShortestPathUtils
{
    /**
     * Returns a <code>List</code> of the edges on the shortest path from 
     * <code>source</code> to <code>target</code>, in order of their
     * occurrence on this path.  
     */
    public static <V, E> List<E> getPath(Graph<V,E> graph, ShortestPath<V,E> sp, V source, V target)
    {
        LinkedList<E> path = new LinkedList<E>();
        
        Map<V,E> incomingEdges = sp.getIncomingEdgeMap(source);
        
        if (incomingEdges.isEmpty() || incomingEdges.get(target) == null)
            return path;
        V current = target;
        while (!current.equals(source))
        {
            E incoming = incomingEdges.get(current);
            path.addFirst(incoming);
            Pair<V> endpoints = graph.getEndpoints(incoming);
            if(endpoints.getFirst().equals(current)) {	
            	current = endpoints.getSecond();
            } else {
            	current = endpoints.getFirst();
            }
            		//incoming.getOpposite(current);
        }
        return path;
    }
}
