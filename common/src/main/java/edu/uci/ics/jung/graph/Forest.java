package edu.uci.ics.jung.graph;

import java.util.Collection;

/**
 * An interface for a graph which consists of a collection of rooted 
 * directed acyclic graphs.
 * 
 * @author Joshua O'Madadhain
 */
public interface Forest<V,E> extends DirectedGraph<V,E> {
	
    /**
     * Returns a view of this graph as a collection of <code>Tree</code> instances.
     * @return a view of this graph as a collection of <code>Tree</code>s
     */
	Collection<Tree<V,E>> getTrees();

    /**
     * Returns the parent of <code>vertex</code> in this tree.
     * (If <code>vertex</code> is the root, returns <code>null</code>.)
     * The parent of a vertex is defined as being its predecessor in the 
     * (unique) shortest path from the root to this vertex.
     * This is a convenience method which is equivalent to 
     * <code>Graph.getPredecessors(vertex).iterator().next()</code>.
     * @return the parent of <code>vertex</code> in this tree
     * @see Graph#getPredecessors(Object)
     * @see #getParentEdge(Object)
     */
    public V getParent(V vertex);
    
    /**
     * Returns the edge connecting <code>vertex</code> to its parent in
     * this tree.
     * (If <code>vertex</code> is the root, returns <code>null</code>.)
     * The parent of a vertex is defined as being its predecessor in the 
     * (unique) shortest path from the root to this vertex.
     * This is a convenience method which is equivalent to 
     * <code>Graph.getInEdges(vertex).iterator().next()</code>,
     * and also to <code>Graph.findEdge(vertex, getParent(vertex))</code>.
     * @return the edge connecting <code>vertex</code> to its parent, or 
     * <code>null</code> if <code>vertex</code> is the root
     * @see Graph#getInEdges(Object)
     * @see #getParent(Object)
     */
    public E getParentEdge(V vertex);
    
    /**
     * Returns the children of <code>vertex</code> in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getSuccessors(vertex)</code>. 
     * @param vertex the vertex whose children are to be returned
     * @return the <code>Collection</code> of children of <code>vertex</code> 
     * in this tree
     * @see Graph#getSuccessors(Object)
     * @see #getChildEdges(Object)
     */
    public Collection<V> getChildren(V vertex);
    
    /**
     * Returns the edges connecting <code>vertex</code> to its children 
     * in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getOutEdges(vertex)</code>. 
     * @param vertex the vertex whose child edges are to be returned
     * @return the <code>Collection</code> of edges connecting 
     * <code>vertex</code> to its children in this tree
     * @see Graph#getOutEdges(Object)
     * @see #getChildren(Object)
     */
    public Collection<E> getChildEdges(V vertex);
    
    /**
     * Returns the number of children that <code>vertex</code> has in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getSuccessorCount(vertex)</code>. 
     * @param vertex the vertex whose child edges are to be returned
     * @return the <code>Collection</code> of edges connecting 
     * <code>vertex</code> to its children in this tree
     * @see #getChildEdges(Object)
     * @see #getChildren(Object)
     * @see Graph#getSuccessorCount(Object)
     */
    public int getChildCount(V vertex);
}
