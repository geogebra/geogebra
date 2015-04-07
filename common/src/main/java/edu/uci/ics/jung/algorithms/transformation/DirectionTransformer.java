/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
/*
 * Created on Apr 21, 2004
 */
package edu.uci.ics.jung.algorithms.transformation;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * <p>Functions for transforming graphs into directed or undirected graphs.</p>
 * 
 * 
 * @author Danyel Fisher
 * @author Joshua O'Madadhain
 */
public class DirectionTransformer 
{

    /**
     * Transforms <code>graph</code> (which may be of any directionality)
     * into an undirected graph. (This may be useful for
     * visualization tasks).
     * Specifically:
     * <ul>
     * <li/>Vertices are copied from <code>graph</code>.
     * <li/>Directed edges are 'converted' into a single new undirected edge in the new graph.
     * <li/>Each undirected edge (if any) in <code>graph</code> is 'recreated' with a new undirected edge in the new
     * graph if <code>create_new</code> is true, or copied from <code>graph</code> otherwise.
     * </ul>
     * 
     * @param graph     the graph to be transformed
     * @param create_new specifies whether existing undirected edges are to be copied or recreated
     * @param graph_factory used to create the new graph object
     * @param edge_factory used to create new edges
     * @return          the transformed <code>Graph</code>
     */
    public static <V,E> UndirectedGraph<V,E> toUndirected(Graph<V,E> graph, 
    		Factory<UndirectedGraph<V,E>> graph_factory,
            Factory<E> edge_factory, boolean create_new)
    {
        UndirectedGraph<V,E> out = graph_factory.create();
        
        for (V v : graph.getVertices())
            out.addVertex(v);
        
        for (E e : graph.getEdges())
        {
            Pair<V> endpoints = graph.getEndpoints(e);
            V v1 = endpoints.getFirst();
            V v2 = endpoints.getSecond();
            E to_add;
            if (graph.getEdgeType(e) == EdgeType.DIRECTED || create_new)
                to_add = edge_factory.create();
            else
                to_add = e;
            out.addEdge(to_add, v1, v2, EdgeType.UNDIRECTED);
        }
        return out;
    }
    
    /**
     * Transforms <code>graph</code> (which may be of any directionality)
     * into a directed graph.  
     * Specifically:
     * <ul>
     * <li/>Vertices are copied from <code>graph</code>.
     * <li/>Undirected edges are 'converted' into two new antiparallel directed edges in the new graph.
     * <li/>Each directed edge (if any) in <code>graph</code> is 'recreated' with a new edge in the new
     * graph if <code>create_new</code> is true, or copied from <code>graph</code> otherwise.
     * </ul>
     * 
     * @param graph     the graph to be transformed
     * @param create_new specifies whether existing directed edges are to be copied or recreated
     * @param graph_factory used to create the new graph object
     * @param edge_factory used to create new edges
     * @return          the transformed <code>Graph</code>
     */
    public static <V,E> Graph<V,E> toDirected(Graph<V,E> graph, Factory<DirectedGraph<V,E>> graph_factory,
            Factory<E> edge_factory, boolean create_new)
    {
        DirectedGraph<V,E> out = graph_factory.create();
        
        for (V v : graph.getVertices())
            out.addVertex(v);
        
        for (E e : graph.getEdges())
        {
            Pair<V> endpoints = graph.getEndpoints(e);
            if (graph.getEdgeType(e) == EdgeType.UNDIRECTED)
            {
                V v1 = endpoints.getFirst();
                V v2 = endpoints.getSecond();
                out.addEdge(edge_factory.create(), v1, v2, EdgeType.DIRECTED);
                out.addEdge(edge_factory.create(), v2, v1, EdgeType.DIRECTED);
            }
            else // if the edge is directed, just add it 
            {
                V source = graph.getSource(e);
                V dest = graph.getDest(e);
                E to_add = create_new ? edge_factory.create() : e;
                out.addEdge(to_add, source, dest, EdgeType.DIRECTED);
            }
                
        }
        return out;
    }
}