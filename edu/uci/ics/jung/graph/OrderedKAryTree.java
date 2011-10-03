/*
 * Created on May 8, 2008
 *
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An implementation of <code>Tree</code> in which each vertex has
 * <= k children.  The value of 'k' is specified by the constructor
 * parameter.  A specific child (edge) can be retrieved directly by specifying the
 * index at which the child is located.  By default, new (child) vertices
 * are added at the lowest index available, if no index is specified.
 * 
 */
@SuppressWarnings("serial")
public class OrderedKAryTree<V, E> extends AbstractTypedGraph<V, E> implements Tree<V, E> 
{
    protected Map<E, Pair<V>> edge_vpairs;
    protected Map<V, VertexData> vertex_data;
    protected int height;
    protected V root;
    protected int order;
    
    /**
     * Returns a {@code Factory} that creates an instance of this graph type.
     * @param <V> the vertex type for the graph factory
     * @param <E> the edge type for the graph factory
     */
    public static <V,E> Factory<DirectedGraph<V,E>> getFactory(final int order) {
        return new Factory<DirectedGraph<V,E>> () {
            public DirectedGraph<V,E> create() {
                return new OrderedKAryTree<V,E>(order);
            }
        };
    }
    
    /**
     * Creates a new instance with the specified order (maximum number of children).
     */
    public OrderedKAryTree(int order)
    {
    	super(EdgeType.DIRECTED);
    	this.order = order;
    	this.height = -1;
    	this.edge_vpairs = new HashMap<E, Pair<V>>();
    	this.vertex_data = new HashMap<V, VertexData>();
    }
  
    /**
     * Returns the number of children that {@code vertex} has.  
     * @see edu.uci.ics.jung.graph.Tree#getChildCount(java.lang.Object)
     */
    public int getChildCount(V vertex) {
        if (!containsVertex(vertex)) 
            return 0;
        List<E> edges = vertex_data.get(vertex).child_edges;
        if (edges == null)
        	return 0;
        int count = 0;
        for (E edge : edges)
            count += edge == null ? 0 : 1;
    
        return count;
    }
  
    /**
     * Returns the child edge of the vertex at index <code>index</code>.
     * @param vertex
     * @param index
     * @return the child edge of the vertex at index <code>index</code>
     */
    public E getChildEdge(V vertex, int index) 
    {
        if (!containsVertex(vertex)) 
        	return null;
        List<E> edges = vertex_data.get(vertex).child_edges;
        if (edges == null)
        	return null;
        return edges.get(index);
    }

    /**
     * @see edu.uci.ics.jung.graph.Tree#getChildEdges(java.lang.Object)
     */
    public Collection<E> getChildEdges(V vertex) 
    {
        if (!containsVertex(vertex)) 
        	return null;
        List<E> edges = vertex_data.get(vertex).child_edges;
        return edges == null ? Collections.<E>emptySet() : 
            CollectionUtils.unmodifiableCollection(edges);
    }
  
    /**
     * Returns an ordered list of {@code vertex}'s child vertices.
     * If there is no child in position i, then the list will contain
     * {@code null} in position i.  If {@code vertex} has no children
     * then the empty set will be returned.
     * @see edu.uci.ics.jung.graph.Tree#getChildren(java.lang.Object)
     */
    public Collection<V> getChildren(V vertex) 
    {
        if (!containsVertex(vertex)) 
            return null;
        List<E> edges = vertex_data.get(vertex).child_edges;
        if (edges == null)
        	return Collections.emptySet();
        Collection<V> children = new ArrayList<V>(order);
        for (E edge : edges)
            children.add(this.getOpposite(vertex, edge));
        return CollectionUtils.unmodifiableCollection(children);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getDepth(java.lang.Object)
     * @return the depth of the vertex in this tree, or -1 if the vertex is
     * not present in this tree
     */
    public int getDepth(V vertex) 
    {
        if (!containsVertex(vertex))
            return -1;
        return vertex_data.get(vertex).depth;
    }
  
    /**
     * Returns the height of the tree, or -1 if the tree is empty.
     * @see edu.uci.ics.jung.graph.Tree#getHeight()
     */
    public int getHeight() 
    {
        return height;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getParent(java.lang.Object)
     */
    public V getParent(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        else if (vertex.equals(root))
            return null;
        return edge_vpairs.get(vertex_data.get(vertex).parent_edge).getFirst();
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getParentEdge(java.lang.Object)
     */
    public E getParentEdge(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        return vertex_data.get(vertex).parent_edge;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Tree#getRoot()
     */
    public V getRoot() 
    {
        return root;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Forest#getTrees()
     */
    public Collection<Tree<V, E>> getTrees() 
    {
        Collection<Tree<V, E>> forest = new ArrayList<Tree<V, E>>(1);
        forest.add(this);
        return forest;
    }
  
    /**
     * Adds the specified {@code child} vertex and edge {@code e} to the graph
     * with the specified parent vertex {@code parent}.  If {@code index} is 
     * greater than or equal to 0, then the child is placed at position
     * {@code index}; if it is less than 0, the child is placed at the lowest
     * available position; if it is greater than or equal to the order of this
     * tree, an exception is thrown.
     * 
     * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
     */
	public boolean addEdge(E e, V parent, V child, int index) 
    {
        if (e == null || child == null || parent == null)
            throw new IllegalArgumentException("Inputs may not be null");
    	if (!containsVertex(parent))
    		throw new IllegalArgumentException("Tree must already " +
    				"include parent: " + parent);
    	if (containsVertex(child))
    		throw new IllegalArgumentException("Tree must not already " +
    				"include child: " + child);
		if (parent.equals(child))
			throw new IllegalArgumentException("Input vertices must be distinct");
		if (index < 0 || index >= order)
		    throw new IllegalArgumentException("'index' must be in [0, [order-1]]");
    	
    	Pair<V> endpoints = new Pair<V>(parent, child);
    	if (containsEdge(e))
    		if (!endpoints.equals(edge_vpairs.get(e)))
    			throw new IllegalArgumentException("Tree already includes edge" + 
    					e + " with different endpoints " + edge_vpairs.get(e));
    		else
    			return false;

    	VertexData parent_data = vertex_data.get(parent);
    	List<E> outedges = parent_data.child_edges;
    	
    	if (outedges == null)
    	    outedges = new ArrayList<E>(this.order);

    	boolean edge_placed = false;
    	if (index >= 0)
    		if (outedges.get(index) != null)
        		throw new IllegalArgumentException("Parent " + parent + 
        				" already has a child at index " + index + " in this tree");
    		else
    			outedges.set(index, e);
    	for (int i = 0; i < order; i++)
    	{
    		if (outedges.get(i) == null)
    		{
    			outedges.set(i, e);
    			edge_placed = true;
    			break;
    		}
    	}
    	if (!edge_placed)
    		throw new IllegalArgumentException("Parent " + parent + " already" +
    				" has " + order + " children in this tree");
    	
    	// initialize VertexData for child; leave child's child_edges null for now
    	VertexData child_data = new VertexData(e, parent_data.depth + 1);
    	vertex_data.put(child, child_data);
    	
    	height = child_data.depth > height ? child_data.depth : height;
    	edge_vpairs.put(e, endpoints);
    	
    	return true;
    }

    /**
     * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
     */
	@Override
    public boolean addEdge(E e, V parent, V child)
	{
		return addEdge(e, parent, child, -1);
	}

    
    /**
     * @see edu.uci.ics.jung.graph.Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object, edu.uci.ics.jung.graph.util.EdgeType)
     */
    @Override
    public boolean addEdge(E e, V v1, V v2, EdgeType edge_type) 
    {
    	this.validateEdgeType(edge_type);
    	return addEdge(e, v1, v2);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getDest(java.lang.Object)
     */
    public V getDest(E directed_edge) 
    {
        if (!containsEdge(directed_edge))
            return null;
        return edge_vpairs.get(directed_edge).getSecond();
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getEndpoints(java.lang.Object)
     */
    public Pair<V> getEndpoints(E edge) 
    {
        if (!containsEdge(edge))
            return null;
        return edge_vpairs.get(edge);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getInEdges(java.lang.Object)
     */
    public Collection<E> getInEdges(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        else if (vertex.equals(root))
            return Collections.emptySet();
        else
            return Collections.singleton(getParentEdge(vertex));
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getOpposite(java.lang.Object, java.lang.Object)
     */
    @Override
    public V getOpposite(V vertex, E edge) 
    {
        if (!containsVertex(vertex) || !containsEdge(edge))
            return null;
        Pair<V> endpoints = edge_vpairs.get(edge);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        return v1.equals(vertex) ? v2 : v1;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getOutEdges(java.lang.Object)
     */
    public Collection<E> getOutEdges(V vertex) 
    {
        return getChildEdges(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getPredecessorCount(java.lang.Object)
     * @return 0 if <code>vertex</code> is the root, -1 if the vertex is 
     * not an element of this tree, and 1 otherwise
     */
    @Override
    public int getPredecessorCount(V vertex) 
    {
        if (!containsVertex(vertex))
            return -1;
        return vertex.equals(root) ? 0 : 1;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getPredecessors(java.lang.Object)
     */
    public Collection<V> getPredecessors(V vertex) 
    {
        if (!containsVertex(vertex))
            return null;
        if (vertex.equals(root))
            return Collections.emptySet();
        return Collections.singleton(getParent(vertex));
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getSource(java.lang.Object)
     */
    public V getSource(E directed_edge) 
    {
        if (!containsEdge(directed_edge))
            return null;
        return edge_vpairs.get(directed_edge).getSecond();
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getSuccessorCount(java.lang.Object)
     */
    @Override
    public int getSuccessorCount(V vertex) 
    {
        return getChildCount(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#getSuccessors(java.lang.Object)
     */
    public Collection<V> getSuccessors(V vertex) 
    {
        return getChildren(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#inDegree(java.lang.Object)
     */
    @Override
    public int inDegree(V vertex) 
    {
        if (!containsVertex(vertex))
            return 0;
        if (vertex.equals(root))
            return 0;
        return 1;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object, java.lang.Object)
     */
    public boolean isDest(V vertex, E edge) 
    {
        if (!containsEdge(edge) || !containsVertex(vertex))
            return false;
        return edge_vpairs.get(edge).getSecond().equals(vertex);
    }
  
    /**
     * Returns <code>true</code> if <code>vertex</code> is a leaf of this tree,
     * i.e., if it has no children.
     * @param vertex the vertex to be queried
     * @return <code>true</code> if <code>outDegree(vertex)==0</code>
     */
    public boolean isLeaf(V vertex)
    {
        if (!containsVertex(vertex))
            return false;
        return outDegree(vertex) == 0;
    }
    
    /**
     * Returns true iff <code>v1</code> is the parent of <code>v2</code>.
     * Note that if <code>v2</code> is the root and <code>v1</code> is <code>null</code>,
     * this method returns <code>true</code>.
     * 
     * @see edu.uci.ics.jung.graph.Graph#isPredecessor(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isPredecessor(V v1, V v2) 
    {
        if (!containsVertex(v2))
            return false;
        return getParent(v2).equals(v1);
    }
  
    /**
     * Returns <code>true</code> if <code>vertex</code> is a leaf of this tree,
     * i.e., if it has no children.
     * @param vertex the vertex to be queried
     * @return <code>true</code> if <code>outDegree(vertex)==0</code>
     */
    public boolean isRoot(V vertex)
    {
        if (root == null)
            return false;
        return root.equals(vertex);
    }
    
    /**
     * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object, java.lang.Object)
     */
    public boolean isSource(V vertex, E edge) 
    {
        if (!containsEdge(edge) || !containsVertex(vertex))
            return false;
        return edge_vpairs.get(edge).getFirst().equals(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#isSuccessor(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isSuccessor(V v1, V v2) 
    {
        if (!containsVertex(v2))
            return false;
        if (containsVertex(v1))
            return getParent(v1).equals(v2);
        return isLeaf(v2) && v1 == null;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Graph#outDegree(java.lang.Object)
     */
    @Override
    public int outDegree(V vertex) 
    {
        if (!containsVertex(vertex))
            return 0;
        List<E> out_edges = vertex_data.get(vertex).child_edges;
        if (out_edges == null)
        	return 0;
        int degree = 0;
        for (E e : out_edges)
        	degree += (e == null) ? 0 : 1;
        return degree;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#addEdge(java.lang.Object, java.util.Collection)
     */
    @Override
    @SuppressWarnings("unchecked")
	public boolean addEdge(E edge, Collection<? extends V> vertices, EdgeType edge_type) 
    {
        if (edge == null || vertices == null)
            throw new IllegalArgumentException("inputs may not be null");
        if (vertices.size() != 2)
            throw new IllegalArgumentException("'vertices' must contain " +
            		"exactly 2 distinct vertices");
    	this.validateEdgeType(edge_type);
		Pair<V> endpoints;
		if (vertices instanceof Pair)
			endpoints = (Pair<V>)vertices;
		else
			endpoints = new Pair<V>(vertices);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();
		if (v1.equals(v2))
			throw new IllegalArgumentException("Input vertices must be distinct");
		return addEdge(edge, v1, v2);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
     */
    public boolean addVertex(V vertex) 
    {
		if(root == null) 
		{
			this.root = vertex;
			vertex_data.put(vertex, new VertexData(null, 0));
			this.height = 0;
			return true;
		} 
		else 
		{
			throw new UnsupportedOperationException("Unless you are setting " +
					"the root, use addEdge() or addChild()");
		}
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#isIncident(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isIncident(V vertex, E edge) 
    {
        if (!containsVertex(vertex) || !containsEdge(edge))
            return false;
        return edge_vpairs.get(edge).contains(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#isNeighbor(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isNeighbor(V v1, V v2) 
    {
    	if (!containsVertex(v1) || !containsVertex(v2))
    		return false;
    	return getNeighbors(v1).contains(v2);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#containsEdge(java.lang.Object)
     */
    public boolean containsEdge(E edge) 
    {
    	return edge_vpairs.containsKey(edge);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#containsVertex(java.lang.Object)
     */
    public boolean containsVertex(V vertex) 
    {
    	return vertex_data.containsKey(vertex);
    }
  
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#findEdge(java.lang.Object, java.lang.Object)
     */
    @Override
    public E findEdge(V v1, V v2) 
    {
        if (!containsVertex(v1) || !containsVertex(v2))
            return null;
    	VertexData v1_data = vertex_data.get(v1);
    	if (edge_vpairs.get(v1_data.parent_edge).getFirst().equals(v2))
    		return v1_data.parent_edge;
    	List<E> edges = v1_data.child_edges;
    	if (edges == null)
    		return null;
    	for (E edge : edges)
    		if (edge != null && edge_vpairs.get(edge).getSecond().equals(v2))
    			return edge;
    	return null;
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#findEdgeSet(java.lang.Object, java.lang.Object)
     */
    @Override
    public Collection<E> findEdgeSet(V v1, V v2) 
    {
    	E edge = findEdge(v1, v2);
    	if (edge == null)
    		return Collections.emptySet();
    	else
    		return Collections.singleton(edge);
    }
  
    /**
     * Returns the child of <code>vertex</code> at position <code>index</code> 
     * in this tree, or <code>null</code> if it has no child at that position.
     * @param vertex the vertex to query
     * @return the child of <code>vertex</code> at position <code>index</code> 
     * in this tree, or <code>null</code> if it has no child at that position
     * @throws ArrayIndexOutOfBoundsException if <code>index</code> is not in 
     * the range {@code [0, order-1]}
     */
    public V getChild(V vertex, int index)
    {
        if (index < 0 || index >= order)
            throw new ArrayIndexOutOfBoundsException(index + " is not in [0, order-1]");
        if (!containsVertex(vertex))
            return null;
        List<E> edges = vertex_data.get(vertex).child_edges;
        if (edges == null)
        	return null;
        E edge = edges.get(index);
        return edge == null ? null : edge_vpairs.get(edge).getSecond();
    }
    
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount()
     */
    public int getEdgeCount() 
    {
    	return edge_vpairs.size();
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getEdges()
     */
    public Collection<E> getEdges() 
    {
    	return CollectionUtils.unmodifiableCollection(edge_vpairs.keySet());
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentCount(java.lang.Object)
     */
    @Override
    public int getIncidentCount(E edge) 
    {
    	return 2;  // all tree edges have 2 incident vertices
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentEdges(java.lang.Object)
     */
    public Collection<E> getIncidentEdges(V vertex) 
    {
    	if (!containsVertex(vertex))
    		return null;
    	ArrayList<E> edges = new ArrayList<E>(order+1);
    	VertexData v_data = vertex_data.get(vertex);
    	if (v_data.parent_edge != null)
    		edges.add(v_data.parent_edge);
    	if (v_data.child_edges != null)
    	{
    		for (E edge : v_data.child_edges)
    			if (edge != null)
    				edges.add(edge);
    	}
    	if (edges.isEmpty())
    		return Collections.emptySet();
    	return Collections.unmodifiableCollection(edges);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentVertices(java.lang.Object)
     */
    @Override
    public Collection<V> getIncidentVertices(E edge) 
    {
    	return edge_vpairs.get(edge);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getNeighborCount(java.lang.Object)
     */
    @Override
    public int getNeighborCount(V vertex) 
    {
        if (!containsVertex(vertex))
            return 0;
    	return (vertex.equals(root) ? 0 : 1) + this.getChildCount(vertex);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getNeighbors(java.lang.Object)
     */
    public Collection<V> getNeighbors(V vertex) 
    {
    	if (!containsVertex(vertex))
    		return null;
    	ArrayList<V> vertices = new ArrayList<V>(order+1);
    	VertexData v_data = vertex_data.get(vertex);
    	if (v_data.parent_edge != null)
    		vertices.add(edge_vpairs.get(v_data.parent_edge).getFirst());
    	if (v_data.child_edges != null)
    	{
    		for (E edge : v_data.child_edges)
    			if (edge != null)
    				vertices.add(edge_vpairs.get(edge).getSecond());
    	}
    	if (vertices.isEmpty())
    		return Collections.emptySet();
    	return Collections.unmodifiableCollection(vertices);
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getVertexCount()
     */
    public int getVertexCount() 
    {
    	return vertex_data.size();
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#getVertices()
     */
    public Collection<V> getVertices() 
    {
      return CollectionUtils.unmodifiableCollection(vertex_data.keySet());
    }
  
    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
     */
    public boolean removeEdge(E edge) 
    {
    	if (!containsEdge(edge))
    		return false;
    	
    	removeVertex(edge_vpairs.get(edge).getSecond());
    	edge_vpairs.remove(edge);
    	
    	return true;
    }

    /**
     * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
     */
    public boolean removeVertex(V vertex) 
    {
    	if (!containsVertex(vertex))
    		return false;
    	
    	// recursively remove all of vertex's children
		for(V v : getChildren(vertex))
			removeVertex(v);

		E parent_edge = getParentEdge(vertex);
		edge_vpairs.remove(parent_edge);
		List<E> edges = vertex_data.get(vertex).child_edges;
		if (edges != null)
			for (E edge : edges)
				edge_vpairs.remove(edge);
		vertex_data.remove(vertex);
		
		return true;
    }
	
	protected class VertexData
	{
	    List<E> child_edges;
		E parent_edge;
		int depth;
		
		protected VertexData(E parent_edge, int depth)
		{
			this.parent_edge = parent_edge;
			this.depth = depth;
		}
	}

	@Override
	public boolean addEdge(E edge, Pair<? extends V> endpoints, EdgeType edgeType) 
	{
	    if (edge == null || endpoints == null)
	        throw new IllegalArgumentException("inputs must not be null");
		return addEdge(edge, endpoints.getFirst(), endpoints.getSecond(), edgeType);
	}
}
