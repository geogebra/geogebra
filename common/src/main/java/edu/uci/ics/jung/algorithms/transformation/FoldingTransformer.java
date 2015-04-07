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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Predicate;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.KPartiteGraph;

/**
 * Methods for creating a "folded" graph based on a k-partite graph or a
 * hypergraph.  
 * 
 * <p>A "folded" graph is derived from a k-partite graph by identifying
 * a partition of vertices which will become the vertices of the new graph, copying
 * these vertices into the new graph, and then connecting those vertices whose
 * original analogues were connected indirectly through elements
 * of other partitions.</p>
 * 
 * <p>A "folded" graph is derived from a hypergraph by creating vertices based on
 * either the vertices or the hyperedges of the original graph, and connecting 
 * vertices in the new graph if their corresponding vertices/hyperedges share a 
 * connection with a common hyperedge/vertex.</p>   
 * 
 * @author Danyel Fisher
 * @author Joshua O'Madadhain
 */
public class FoldingTransformer<V,E>
{
    
    /**
     * Converts <code>g</code> into a unipartite graph whose vertex set is the
     * vertices of <code>g</code>'s partition <code>p</code>.  For vertices
     * <code>a</code> and <code>b</code> in this partition, the resultant
     * graph will include the edge <code>(a,b)</code> if the original graph
     * contains edges <code>(a,c)</code> and <code>(c,b)</code> for at least
     * one vertex <code>c</code>.
     * 
     * <p>The vertices of the new graph are the same as the vertices of the
     * appropriate partition in the old graph; the edges in the new graph are
     * created by the input edge <code>Factory</code>.</p>
     * 
     * <p>If there is more than 1 such vertex <code>c</code> for a given pair
     * <code>(a,b)</code>, the type of the output graph will determine whether
     * it will contain parallel edges or not.</p>
     * 
     * <p>This function will not create self-loops.</p>
     * 
     * @param <V> vertex type
     * @param <E> input edge type
     * @param g input k-partite graph
     * @param p predicate specifying vertex partition
     * @param graph_factory factory used to create the output graph 
     * @param edge_factory factory used to create the edges in the new graph
     * @return a copy of the input graph folded with respect to the input partition
     */
    public static <V,E> Graph<V,E> foldKPartiteGraph(KPartiteGraph<V,E> g, Predicate<V> p, 
            Factory<Graph<V,E>> graph_factory, Factory<E> edge_factory)
    {
        Graph<V,E> newGraph = graph_factory.create();

        // get vertices for the specified partition
        Collection<V> vertices = g.getVertices(p);
        for (V v : vertices)
        {
            newGraph.addVertex(v);
            for (V s : g.getSuccessors(v))
            {
                for (V t : g.getSuccessors(s))
                {
                    if (!vertices.contains(t) || t.equals(v)) 
                        continue;
                    newGraph.addVertex(t);
                    newGraph.addEdge(edge_factory.create(), v, t);
                }
            }
        }
        return newGraph;
    }

    /**
     * Converts <code>g</code> into a unipartite graph whose vertices are the
     * vertices of <code>g</code>'s partition <code>p</code>, and whose edges
     * consist of collections of the intermediate vertices from other partitions.  
     * For vertices
     * <code>a</code> and <code>b</code> in this partition, the resultant
     * graph will include the edge <code>(a,b)</code> if the original graph
     * contains edges <code>(a,c)</code> and <code>(c,b)</code> for at least
     * one vertex <code>c</code>.
     * 
     * <p>The vertices of the new graph are the same as the vertices of the
     * appropriate partition in the old graph; the edges in the new graph are
     * collections of the intermediate vertices <code>c</code>.</p>
     * 
     * <p>This function will not create self-loops.</p>
     * 
     * @param <V> vertex type
     * @param <E> input edge type
     * @param g input k-partite graph
     * @param p predicate specifying vertex partition
     * @param graph_factory factory used to create the output graph 
     * @return the result of folding g into unipartite graph whose vertices
     * are those of the <code>p</code> partition of g
     */
    public static <V,E> Graph<V, Collection<V>> foldKPartiteGraph(KPartiteGraph<V,E> g, Predicate<V> p, 
            Factory<Graph<V, Collection<V>>> graph_factory)
    {
        Graph<V, Collection<V>> newGraph = graph_factory.create();

        // get vertices for the specified partition, copy into new graph
        Collection<V> vertices = g.getVertices(p);

        for (V v : vertices)
        {
            newGraph.addVertex(v);
            for (V s : g.getSuccessors(v))
            {
                for (V t : g.getSuccessors(s))
                {
                    if (!vertices.contains(t) || t.equals(v)) 
                        continue;
                    newGraph.addVertex(t);
                    Collection<V> v_coll = newGraph.findEdge(v, t);
                    if (v_coll == null)
                    {
                        v_coll = new ArrayList<V>();
                        newGraph.addEdge(v_coll, v, t);
                    }
                    v_coll.add(s);
                }
            }
        }
        return newGraph;
    }
    
    /**
     * Creates a <code>Graph</code> which is an edge-folded version of <code>h</code>, where
     * hyperedges are replaced by k-cliques in the output graph.
     * 
     * <p>The vertices of the new graph are the same objects as the vertices of 
     * <code>h</code>, and <code>a</code> 
     * is connected to <code>b</code> in the new graph if the corresponding vertices
     * in <code>h</code> are connected by a hyperedge.  Thus, each hyperedge with 
     * <i>k</i> vertices in <code>h</code> induces a <i>k</i>-clique in the new graph.</p>
     * 
     * <p>The edges of the new graph consist of collections of each hyperedge that connected
     * the corresponding vertex pair in the original graph.</p>
     * 
     * @param <V> vertex type
     * @param <E> input edge type
     * @param h hypergraph to be folded
     * @param graph_factory factory used to generate the output graph
     * @return a copy of the input graph where hyperedges are replaced by cliques
     */
    public static <V,E> Graph<V, Collection<E>> foldHypergraphEdges(Hypergraph<V,E> h, 
            Factory<Graph<V, Collection<E>>> graph_factory)
    {
        Graph<V, Collection<E>> target = graph_factory.create();

        for (V v : h.getVertices())
            target.addVertex(v);
        
        for (E e : h.getEdges())
        {
            ArrayList<V> incident = new ArrayList<V>(h.getIncidentVertices(e));
            populateTarget(target, e, incident);
        }
        return target;
    }


    /**
     * Creates a <code>Graph</code> which is an edge-folded version of <code>h</code>, where
     * hyperedges are replaced by k-cliques in the output graph.
     * 
     * <p>The vertices of the new graph are the same objects as the vertices of 
     * <code>h</code>, and <code>a</code> 
     * is connected to <code>b</code> in the new graph if the corresponding vertices
     * in <code>h</code> are connected by a hyperedge.  Thus, each hyperedge with 
     * <i>k</i> vertices in <code>h</code> induces a <i>k</i>-clique in the new graph.</p>
     * 
     * <p>The edges of the new graph are generated by the specified edge factory.</p>
     * 
     * @param <V> vertex type
     * @param <E> input edge type
     * @param h hypergraph to be folded
     * @param graph_factory factory used to generate the output graph
     * @param edge_factory factory used to create the new edges 
     * @return a copy of the input graph where hyperedges are replaced by cliques
     */
    public static <V,E> Graph<V,E> foldHypergraphEdges(Hypergraph<V,E> h, 
            Factory<Graph<V,E>> graph_factory, Factory<E> edge_factory)
    {
        Graph<V,E> target = graph_factory.create();

        for (V v : h.getVertices())
            target.addVertex(v);
        
        for (E e : h.getEdges())
        {
            ArrayList<V> incident = new ArrayList<V>(h.getIncidentVertices(e));
            for (int i = 0; i < incident.size(); i++)
                for (int j = i+1; j < incident.size(); j++)
                    target.addEdge(edge_factory.create(), incident.get(i), incident.get(j));
        }
        return target;
    }

    /**
     * Creates a <code>Graph</code> which is a vertex-folded version of <code>h</code>, whose
     * vertices are the input's hyperedges and whose edges are induced by adjacent hyperedges
     * in the input.
     * 
     * <p>The vertices of the new graph are the same objects as the hyperedges of 
     * <code>h</code>, and <code>a</code> 
     * is connected to <code>b</code> in the new graph if the corresponding edges
     * in <code>h</code> have a vertex in common.  Thus, each vertex incident to  
     * <i>k</i> edges in <code>h</code> induces a <i>k</i>-clique in the new graph.</p>
     * 
     * <p>The edges of the new graph are created by the specified factory.</p>
     * 
     * @param <V> vertex type
     * @param <E> input edge type
     * @param <F> output edge type
     * @param h hypergraph to be folded
     * @param graph_factory factory used to generate the output graph
     * @param edge_factory factory used to generate the output edges
     * @return a transformation of the input graph whose vertices correspond to the input's hyperedges 
     * and edges are induced by hyperedges sharing vertices in the input
     */
    public static <V,E,F> Graph<E,F> foldHypergraphVertices(Hypergraph<V,E> h, 
            Factory<Graph<E,F>> graph_factory, Factory<F> edge_factory)
    {
        Graph<E,F> target = graph_factory.create();
        
        for (E e : h.getEdges())
            target.addVertex(e);
        
        for (V v : h.getVertices())
        {
            ArrayList<E> incident = new ArrayList<E>(h.getIncidentEdges(v));
            for (int i = 0; i < incident.size(); i++)
                for (int j = i+1; j < incident.size(); j++)
                    target.addEdge(edge_factory.create(), incident.get(i), incident.get(j));
        }
        
        return target;
    }

    /**
     * Creates a <code>Graph</code> which is a vertex-folded version of <code>h</code>, whose
     * vertices are the input's hyperedges and whose edges are induced by adjacent hyperedges
     * in the input.
     * 
     * <p>The vertices of the new graph are the same objects as the hyperedges of 
     * <code>h</code>, and <code>a</code> 
     * is connected to <code>b</code> in the new graph if the corresponding edges
     * in <code>h</code> have a vertex in common.  Thus, each vertex incident to  
     * <i>k</i> edges in <code>h</code> induces a <i>k</i>-clique in the new graph.</p>
     * 
     * <p>The edges of the new graph consist of collections of each vertex incident to 
     * the corresponding hyperedge pair in the original graph.</p>
     * 
     * @param h hypergraph to be folded
     * @param graph_factory factory used to generate the output graph
     * @return a transformation of the input graph whose vertices correspond to the input's hyperedges 
     * and edges are induced by hyperedges sharing vertices in the input
     */
    public Graph<E,Collection<V>> foldHypergraphVertices(Hypergraph<V,E> h, 
            Factory<Graph<E,Collection<V>>> graph_factory)
    {
        Graph<E,Collection<V>> target = graph_factory.create();

        for (E e : h.getEdges())
            target.addVertex(e);
        
        for (V v : h.getVertices())
        {
            ArrayList<E> incident = new ArrayList<E>(h.getIncidentEdges(v));
            populateTarget(target, v, incident);
        }
        return target;
    }
    
    /**
     * @param target
     * @param e
     * @param incident
     */
    private static <S,T> void populateTarget(Graph<S, Collection<T>> target, T e,
            ArrayList<S> incident)
    {
        for (int i = 0; i < incident.size(); i++)
        {
            S v1 = incident.get(i);
            for (int j = i+1; j < incident.size(); j++)
            {
                S v2 = incident.get(j);
                Collection<T> e_coll = target.findEdge(v1, v2);
                if (e_coll == null)
                {
                    e_coll = new ArrayList<T>();
                    target.addEdge(e_coll, v1, v2);
                }
                e_coll.add(e);
            }
        }
    }

}