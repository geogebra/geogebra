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

/**
 * A hypergraph, consisting of a set of vertices of type <code>V</code>
 * and a set of hyperedges of type <code>E</code> which connect the vertices.  
 * This is the base interface for all JUNG graph types.
 * <P>
 * This interface permits, but does not enforce, any of the following 
 * common variations of graphs:
 * <ul>
 * <li/>hyperedges (edges which connect a set of vertices of any size)
 * <li/>edges (these have have exactly two endpoints, which may or may not be distinct)
 * <li/>self-loops (edges which connect exactly one vertex)
 * <li> directed and undirected edges
 * <li> vertices and edges with attributes (for example, weighted edges)
 * <li> vertices and edges with different constraints or properties (for example, bipartite 
 *      or multimodal graphs)
 * <li> parallel edges (multiple edges which connect a single set of vertices)
 * <li> internal representations as matrices or as adjacency lists or adjacency maps
 * </ul> 
 * Extensions or implementations of this interface 
 * may enforce or disallow any or all of these variations.
 * <p><b>Notes</b>:
 * <ul>
 * <li/> The collections returned by <code>Hypergraph</code> instances
 * should be treated in general as if read-only.  While they are not contractually 
 * guaranteed (or required) to be immutable,
 * this interface does not define the outcome if they are mutated.
 * Mutations should be done via <code>{add,remove}{Edge,Vertex}</code>, or
 * in the constructor.
 * <li/> 
 * </ul>
 * 
 * @author Joshua O'Madadhain
 */
public interface Hypergraph<V, E>
{
    /**
     * Returns a view of all edges in this graph. In general, this
     * obeys the <code>Collection</code> contract, and therefore makes no guarantees 
     * about the ordering of the vertices within the set.
     * @return a <code>Collection</code> view of all edges in this graph
     */
    Collection<E> getEdges();
    
    /**
     * Returns a view of all vertices in this graph. In general, this
     * obeys the <code>Collection</code> contract, and therefore makes no guarantees 
     * about the ordering of the vertices within the set.
     * @return a <code>Collection</code> view of all vertices in this graph
     */
    Collection<V> getVertices();
    
    /**
     * Returns true if this graph's vertex collection contains <code>vertex</code>.
     * Equivalent to <code>getVertices().contains(vertex)</code>.
     * @param vertex the vertex whose presence is being queried
     * @return true iff this graph contains a vertex <code>vertex</code>
     */
    boolean containsVertex(V vertex);
    
    /**
     * Returns true if this graph's edge collection contains <code>edge</code>.
     * Equivalent to <code>getEdges().contains(edge)</code>.
     * @param edge the edge whose presence is being queried
     * @return true iff this graph contains an edge <code>edge</code>
     */
    boolean containsEdge(E edge);
    
    /**
     * Returns the number of edges in this graph.
     * @return the number of edges in this graph
     */
    int getEdgeCount();
    
    /**
     * Returns the number of vertices in this graph.
     * @return the number of vertices in this graph
     */
    int getVertexCount();

    /**
     * Returns the collection of vertices which are connected to <code>vertex</code>
     * via any edges in this graph.
     * If <code>vertex</code> is connected to itself with a self-loop, then 
     * it will be included in the collection returned.
     * 
     * @param vertex the vertex whose neighbors are to be returned
     * @return  the collection of vertices which are connected to <code>vertex</code>, 
     * or <code>null</code> if <code>vertex</code> is not present
     */
    Collection<V> getNeighbors(V vertex);
    
    /**
     * Returns the collection of edges in this graph which are connected to <code>vertex</code>.
     * 
     * @param vertex the vertex whose incident edges are to be returned
     * @return  the collection of edges which are connected to <code>vertex</code>, 
     * or <code>null</code> if <code>vertex</code> is not present
     */
    Collection<E> getIncidentEdges(V vertex);
    
    /**
     * Returns the collection of vertices in this graph which are connected to <code>edge</code>.
     * Note that for some graph types there are guarantees about the size of this collection
     * (i.e., some graphs contain edges that have exactly two endpoints, which may or may 
     * not be distinct).  Implementations for those graph types may provide alternate methods 
     * that provide more convenient access to the vertices.
     * 
     * @param edge the edge whose incident vertices are to be returned
     * @return  the collection of vertices which are connected to <code>edge</code>, 
     * or <code>null</code> if <code>edge</code> is not present
     */
    Collection<V> getIncidentVertices(E edge);
    
    /**
     * Returns an edge that connects this vertex to <code>v</code>.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting 
     * <code>v1</code> to <code>v2</code>), any of these edges 
     * may be returned.  <code>findEdgeSet(v1, v2)</code> may be 
     * used to return all such edges.
     * Returns null if either of the following is true:
     * <ul>
     * <li/><code>v2</code> is not connected to <code>v1</code>
     * <li/>either <code>v1</code> or <code>v2</code> are not present in this graph
     * </ul> 
     * <p><b>Note</b>: for purposes of this method, <code>v1</code> is only considered to be connected to
     * <code>v2</code> via a given <i>directed</i> edge <code>e</code> if
     * <code>v1 == e.getSource() && v2 == e.getDest()</code> evaluates to <code>true</code>.
     * (<code>v1</code> and <code>v2</code> are connected by an undirected edge <code>u</code> if 
     * <code>u</code> is incident to both <code>v1</code> and <code>v2</code>.)
     * 
     * @return  an edge that connects <code>v1</code> to <code>v2</code>, 
     * or <code>null</code> if no such edge exists (or either vertex is not present)
     * @see Hypergraph#findEdgeSet(Object, Object) 
     */
    E findEdge(V v1, V v2);
    
    /**
     * Returns all edges that connects this vertex to <code>v</code>.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting 
     * <code>v1</code> to <code>v2</code>), any of these edges 
     * may be returned.  <code>findEdgeSet(v1, v2)</code> may be 
     * used to return all such edges.
     * Returns null if <code>v2</code> is not connected to <code>v1</code>.
     * <br/>Returns an empty collection if either <code>v1</code> or <code>v2</code> are not present in this graph.
     *  
     * <p><b>Note</b>: for purposes of this method, <code>v1</code> is only considered to be connected to
     * <code>v2</code> via a given <i>directed</i> edge <code>d</code> if
     * <code>v1 == d.getSource() && v2 == d.getDest()</code> evaluates to <code>true</code>.
     * (<code>v1</code> and <code>v2</code> are connected by an undirected edge <code>u</code> if 
     * <code>u</code> is incident to both <code>v1</code> and <code>v2</code>.)
     * 
     * @return  a collection containing all edges that connect <code>v1</code> to <code>v2</code>, 
     * or <code>null</code> if either vertex is not present
     * @see Hypergraph#findEdge(Object, Object) 
     */
    Collection<E> findEdgeSet(V v1, V v2);
    
    /**
     * Adds <code>vertex</code> to this graph.
     * Fails if <code>vertex</code> is null or already in the graph.
     * 
     * @param vertex    the vertex to add
     * @return <code>true</code> if the add is successful, and <code>false</code> otherwise
     * @throws IllegalArgumentException if <code>vertex</code> is <code>null</code>
     */
    boolean addVertex(V vertex);
    
    /**
     * Adds <code>edge</code> to this graph.
     * Fails under the following circumstances:
     * <ul>
     * <li/><code>edge</code> is already an element of the graph 
     * <li/>either <code>edge</code> or <code>vertices</code> is <code>null</code>
     * <li/><code>vertices</code> has the wrong number of vertices for the graph type
     * <li/><code>vertices</code> are already connected by another edge in this graph,
     * and this graph does not accept parallel edges
     * </ul>
     * 
     * @param edge
     * @param vertices
     * @return <code>true</code> if the add is successful, and <code>false</code> otherwise
     * @throws IllegalArgumentException if <code>edge</code> or <code>vertices</code> is null, 
     * or if a different vertex set in this graph is already connected by <code>edge</code>, 
     * or if <code>vertices</code> are not a legal vertex set for <code>edge</code> 
     */
    boolean addEdge(E edge, Collection<? extends V> vertices);

    /**
     * Adds <code>edge</code> to this graph with type <code>edge_type</code>.
     * Fails under the following circumstances:
     * <ul>
     * <li/><code>edge</code> is already an element of the graph 
     * <li/>either <code>edge</code> or <code>vertices</code> is <code>null</code>
     * <li/><code>vertices</code> has the wrong number of vertices for the graph type
     * <li/><code>vertices</code> are already connected by another edge in this graph,
     * and this graph does not accept parallel edges
     * <li/><code>edge_type</code> is not legal for this graph
     * </ul>
     * 
     * @param edge
     * @param vertices
     * @return <code>true</code> if the add is successful, and <code>false</code> otherwise
     * @throws IllegalArgumentException if <code>edge</code> or <code>vertices</code> is null, 
     * or if a different vertex set in this graph is already connected by <code>edge</code>, 
     * or if <code>vertices</code> are not a legal vertex set for <code>edge</code> 
     */
    boolean addEdge(E edge, Collection<? extends V> vertices, EdgeType 
    		edge_type);
    
    /**
     * Removes <code>vertex</code> from this graph.
     * As a side effect, removes any edges <code>e</code> incident to <code>vertex</code> if the 
     * removal of <code>vertex</code> would cause <code>e</code> to be incident to an illegal
     * number of vertices.  (Thus, for example, incident hyperedges are not removed, but 
     * incident edges--which must be connected to a vertex at both endpoints--are removed.) 
     * 
     * <p>Fails under the following circumstances:
     * <ul>
     * <li/><code>vertex</code> is not an element of this graph
     * <li/><code>vertex</code> is <code>null</code>
     * </ul>
     * 
     * @param vertex the vertex to remove
     * @return <code>true</code> if the removal is successful, <code>false</code> otherwise
     */
    boolean removeVertex(V vertex);

    /**
     * Removes <code>edge</code> from this graph.
     * Fails if <code>edge</code> is null, or is otherwise not an element of this graph.
     * 
     * @param edge the edge to remove
     * @return <code>true</code> if the removal is successful, <code>false</code> otherwise
     */
    boolean removeEdge(E edge);

    
    /**
     * Returns <code>true</code> if <code>v1</code> and <code>v2</code> share an incident edge.
     * Equivalent to <code>getNeighbors(v1).contains(v2)</code>.
     * 
     * @param v1 the first vertex to test
     * @param v2 the second vertex to test
     * @return <code>true</code> if <code>v1</code> and <code>v2</code> share an incident edge
     */
    boolean isNeighbor(V v1, V v2);

    /**
     * Returns <code>true</code> if <code>vertex</code> and <code>edge</code> 
     * are incident to each other.
     * Equivalent to <code>getIncidentEdges(vertex).contains(edge)</code> and to
     * <code>getIncidentVertices(edge).contains(vertex)</code>.
     * @param vertex
     * @param edge
     * @return <code>true</code> if <code>vertex</code> and <code>edge</code> 
     * are incident to each other
     */
    boolean isIncident(V vertex, E edge);
    
    /**
     * Returns the number of edges incident to <code>vertex</code>.  
     * Special cases of interest:
     * <ul>
     * <li/> Incident self-loops are counted once.
     * <li> If there is only one edge that connects this vertex to
     * each of its neighbors (and vice versa), then the value returned 
     * will also be equal to the number of neighbors that this vertex has
     * (that is, the output of <code>getNeighborCount</code>).
     * <li> If the graph is directed, then the value returned will be 
     * the sum of this vertex's indegree (the number of edges whose 
     * destination is this vertex) and its outdegree (the number
     * of edges whose source is this vertex), minus the number of
     * incident self-loops (to avoid double-counting).
     * </ul>
     * <p>Equivalent to <code>getIncidentEdges(vertex).size()</code>.
     * 
     * @param vertex the vertex whose degree is to be returned
     * @return the degree of this node
     * @see Hypergraph#getNeighborCount(Object)
     */
    int degree(V vertex);

    /**
     * Returns the number of vertices that are adjacent to <code>vertex</code>
     * (that is, the number of vertices that are incident to edges in <code>vertex</code>'s
     * incident edge set).
     * 
     * <p>Equivalent to <code>getNeighbors(vertex).size()</code>.
     * @param vertex the vertex whose neighbor count is to be returned
     * @return the number of neighboring vertices
     */
    int getNeighborCount(V vertex);
    
    /**
     * Returns the number of vertices that are incident to <code>edge</code>.
     * For hyperedges, this can be any nonnegative integer; for edges this
     * must be 2 (or 1 if self-loops are permitted). 
     * 
     * <p>Equivalent to <code>getIncidentVertices(edge).size()</code>.
     * @param edge the edge whose incident vertex count is to be returned
     * @return the number of vertices that are incident to <code>edge</code>.
     */
    int getIncidentCount(E edge);
    
    /**
     * Returns the edge type of <code>edge</code> in this graph.
     * @param edge
     * @return the <code>EdgeType</code> of <code>edge</code>, or <code>null</code> if <code>edge</code> has no defined type
     */
    EdgeType getEdgeType(E edge); 
    
    /**
     * Returns the default edge type for this graph.
     * 
     * @return the default edge type for this graph
     */
    EdgeType getDefaultEdgeType();
    
    /**
     * Returns the collection of edges in this graph which are of type <code>edge_type</code>.
     * @param edge_type the type of edges to be returned
     * @return the collection of edges which are of type <code>edge_type</code>, or
     * <code>null</code> if the graph does not accept edges of this type
     * @see EdgeType
     */
    Collection<E> getEdges(EdgeType edge_type);
    
    /**
     * Returns the number of edges of type <code>edge_type</code> in this graph.
     * @param edge_type the type of edge for which the count is to be returned
     * @return the number of edges of type <code>edge_type</code> in this graph
     */
    int getEdgeCount(EdgeType edge_type);
    
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
}
