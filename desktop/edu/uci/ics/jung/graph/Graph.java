/*
 * Created on Oct 17, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import java.util.Collection;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * A graph consisting of a set of vertices of type <code>V</code>
 * set and a set of edges of type <code>E</code>.  Edges of this
 * graph type have exactly two endpoints; whether these endpoints 
 * must be distinct depends on the implementation.
 * <P>
 * This interface permits, but does not enforce, any of the following 
 * common variations of graphs:
 * <ul>
 * <li> directed and undirected edges
 * <li> vertices and edges with attributes (for example, weighted edges)
 * <li> vertices and edges of different types (for example, bipartite 
 *      or multimodal graphs)
 * <li> parallel edges (multiple edges which connect a single set of vertices)
 * <li> representations as matrices or as adjacency lists or adjacency maps
 * </ul> 
 * Extensions or implementations of this interface 
 * may enforce or disallow any or all of these variations.
 * 
 * <p>Definitions (with respect to a given vertex <code>v</code>):
 * <ul>
 * <li/><b>incoming edge</b> of <code>v</code>: an edge that can be traversed 
 * from a neighbor of <code>v</code> to reach <code>v</code>
 * <li/><b>outgoing edge</b> of <code>v</code>: an edge that can be traversed
 * from <code>v</code> to reach some neighbor of <code>v</code> 
 * <li/><b>predecessor</b> of <code>v</code>: a vertex at the other end of an
 * incoming edge of <code>v</code>
 * <li/><b>successor</b> of <code>v</code>: a vertex at the other end of an 
 * outgoing edge of <code>v</code>
 * <li/>
 * </ul> 
 * 
 * @author Joshua O'Madadhain
 */
public interface Graph<V,E> extends Hypergraph<V,E>
{
    /**
     * Returns a <code>Collection</code> view of the incoming edges incident to <code>vertex</code>
     * in this graph.
     * @param vertex    the vertex whose incoming edges are to be returned
     * @return  a <code>Collection</code> view of the incoming edges incident 
     * to <code>vertex</code> in this graph
     */
    Collection<E> getInEdges(V vertex);
    
    /**
     * Returns a <code>Collection</code> view of the outgoing edges incident to <code>vertex</code>
     * in this graph.
     * @param vertex    the vertex whose outgoing edges are to be returned
     * @return  a <code>Collection</code> view of the outgoing edges incident 
     * to <code>vertex</code> in this graph
     */
    Collection<E> getOutEdges(V vertex);

    /**
     * Returns a <code>Collection</code> view of the predecessors of <code>vertex</code> 
     * in this graph.  A predecessor of <code>vertex</code> is defined as a vertex <code>v</code> 
     * which is connected to 
     * <code>vertex</code> by an edge <code>e</code>, where <code>e</code> is an outgoing edge of 
     * <code>v</code> and an incoming edge of <code>vertex</code>.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a <code>Collection</code> view of the predecessors of 
     * <code>vertex</code> in this graph
     */
    Collection<V> getPredecessors(V vertex);
    
    /**
     * Returns a <code>Collection</code> view of the successors of <code>vertex</code> 
     * in this graph.  A successor of <code>vertex</code> is defined as a vertex <code>v</code> 
     * which is connected to 
     * <code>vertex</code> by an edge <code>e</code>, where <code>e</code> is an incoming edge of 
     * <code>v</code> and an outgoing edge of <code>vertex</code>.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a <code>Collection</code> view of the successors of 
     * <code>vertex</code> in this graph
     */
    Collection<V> getSuccessors(V vertex);
    
    /**
     * Returns the number of incoming edges incident to <code>vertex</code>.
     * Equivalent to <code>getInEdges(vertex).size()</code>.
     * @param vertex    the vertex whose indegree is to be calculated
     * @return  the number of incoming edges incident to <code>vertex</code>
     */
    int inDegree(V vertex);
    
    /**
     * Returns the number of outgoing edges incident to <code>vertex</code>.
     * Equivalent to <code>getOutEdges(vertex).size()</code>.
     * @param vertex    the vertex whose outdegree is to be calculated
     * @return  the number of outgoing edges incident to <code>vertex</code>
     */
    int outDegree(V vertex);
    
    /**
     * Returns <code>true</code> if <code>v1</code> is a predecessor of <code>v2</code> in this graph.
     * Equivalent to <code>v1.getPredecessors().contains(v2)</code>.
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return <code>true</code> if <code>v1</code> is a predecessor of <code>v2</code>, and false otherwise.
     */
    boolean isPredecessor(V v1, V v2);
    
    /**
     * Returns <code>true</code> if <code>v1</code> is a successor of <code>v2</code> in this graph.
     * Equivalent to <code>v1.getSuccessors().contains(v2)</code>.
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return <code>true</code> if <code>v1</code> is a successor of <code>v2</code>, and false otherwise.
     */
    boolean isSuccessor(V v1, V v2);

    /**
     * Returns the number of predecessors that <code>vertex</code> has in this graph.
     * Equivalent to <code>vertex.getPredecessors().size()</code>.
     * @param vertex the vertex whose predecessor count is to be returned
     * @return  the number of predecessors that <code>vertex</code> has in this graph
     */
    int getPredecessorCount(V vertex);
    
    /**
     * Returns the number of successors that <code>vertex</code> has in this graph.
     * Equivalent to <code>vertex.getSuccessors().size()</code>.
     * @param vertex the vertex whose successor count is to be returned
     * @return  the number of successors that <code>vertex</code> has in this graph
     */
    int getSuccessorCount(V vertex);
    
    /**
     * If <code>directed_edge</code> is a directed edge in this graph, returns the source; 
     * otherwise returns <code>null</code>. 
     * The source of a directed edge <code>d</code> is defined to be the vertex for which  
     * <code>d</code> is an outgoing edge.
     * <code>directed_edge</code> is guaranteed to be a directed edge if 
     * its <code>EdgeType</code> is <code>DIRECTED</code>. 
     * @param directed_edge
     * @return  the source of <code>directed_edge</code> if it is a directed edge in this graph, or <code>null</code> otherwise
     */
    V getSource(E directed_edge);

    /**
     * If <code>directed_edge</code> is a directed edge in this graph, returns the destination; 
     * otherwise returns <code>null</code>. 
     * The destination of a directed edge <code>d</code> is defined to be the vertex 
     * incident to <code>d</code> for which  
     * <code>d</code> is an incoming edge.
     * <code>directed_edge</code> is guaranteed to be a directed edge if 
     * its <code>EdgeType</code> is <code>DIRECTED</code>. 
     * @param directed_edge
     * @return  the destination of <code>directed_edge</code> if it is a directed edge in this graph, or <code>null</code> otherwise
     */
    V getDest(E directed_edge);
    
    /**
     * Returns <code>true</code> if <code>vertex</code> is the source of <code>edge</code>.
     * Equivalent to <code>getSource(edge).equals(vertex)</code>.
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return <code>true</code> iff <code>vertex</code> is the source of <code>edge</code>
     */
    boolean isSource(V vertex, E edge);
    
    /**
     * Returns <code>true</code> if <code>vertex</code> is the destination of <code>edge</code>.
     * Equivalent to <code>getDest(edge).equals(vertex)</code>.
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return <code>true</code> iff <code>vertex</code> is the destination of <code>edge</code>
     */
    boolean isDest(V vertex, E edge);

    /**
     * Adds edge <code>e</code> to this graph such that it connects 
     * vertex <code>v1</code> to <code>v2</code>.
     * Equivalent to <code>addEdge(e, new Pair<V>(v1, v2))</code>.
     * If this graph does not contain <code>v1</code>, <code>v2</code>, 
     * or both, implementations may choose to either silently add 
     * the vertices to the graph or throw an <code>IllegalArgumentException</code>.
     * If this graph assigns edge types to its edges, the edge type of
     * <code>e</code> will be the default for this graph.
     * See <code>Hypergraph.addEdge()</code> for a listing of possible reasons
     * for failure.
     * @param e the edge to be added
     * @param v1 the first vertex to be connected
     * @param v2 the second vertex to be connected
     * @return <code>true</code> if the add is successful, <code>false</code> otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object, EdgeType)
     */
    boolean addEdge(E e, V v1, V v2);
    
    /**
     * Adds edge <code>e</code> to this graph such that it connects 
     * vertex <code>v1</code> to <code>v2</code>.
     * Equivalent to <code>addEdge(e, new Pair<V>(v1, v2))</code>.
     * If this graph does not contain <code>v1</code>, <code>v2</code>, 
     * or both, implementations may choose to either silently add 
     * the vertices to the graph or throw an <code>IllegalArgumentException</code>.
     * If <code>edgeType</code> is not legal for this graph, this method will
     * throw <code>IllegalArgumentException</code>.
     * See <code>Hypergraph.addEdge()</code> for a listing of possible reasons
     * for failure.
     * @param e the edge to be added
     * @param v1 the first vertex to be connected
     * @param v2 the second vertex to be connected
     * @param edgeType the type to be assigned to the edge
     * @return <code>true</code> if the add is successful, <code>false</code> otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object)
     */
    boolean addEdge(E e, V v1, V v2, EdgeType edgeType);

    /**
     * Returns the endpoints of <code>edge</code> as a <code>Pair<V></code>.
     * @param edge the edge whose endpoints are to be returned
     * @return the endpoints (incident vertices) of <code>edge</code>
     */
    Pair<V> getEndpoints(E edge);
    
    /**
     * Returns the vertex at the other end of <code>edge</code> from <code>vertex</code>.
     * (That is, returns the vertex incident to <code>edge</code> which is not <code>vertex</code>.)
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return the vertex at the other end of <code>edge</code> from <code>vertex</code>
     */
    V getOpposite(V vertex, E edge); 
}
