/**
 * Copyright (c) 2008, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Sep 1, 2008
 * 
 */
package edu.uci.ics.jung.graph;

import java.util.Collection;
import java.util.Collections;

import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * An abstract class for graphs whose edges all have the same {@code EdgeType}.
 * Intended to simplify the implementation of such graph classes.
 */
@SuppressWarnings("serial")
public abstract class AbstractTypedGraph<V,E> extends AbstractGraph<V, E> 
{
	/**
	 * The edge type for all edges in this graph.
	 */
	protected final EdgeType edge_type;
	
	/**
	 * Creates an instance with the specified edge type.
	 * @param edge_type the type of edges that this graph accepts
	 */
	public AbstractTypedGraph(EdgeType edge_type)
	{
		this.edge_type = edge_type;
	}
	
	/**
	 * Returns this graph's edge type.
	 */
	public EdgeType getDefaultEdgeType()
	{
		return this.edge_type;
	}
	
	/**
	 * Returns this graph's edge type, or {@code null} if {@code e} is not
	 * in this graph.
	 */
	public EdgeType getEdgeType(E e)
	{
		return hasEqualEdgeType(edge_type) ? this.edge_type : null;
	}
	
	/**
	 * Returns the edge set for this graph if {@code edgeType} matches the 
	 * edge type for this graph, and an empty set otherwise.
	 */
	public Collection<E> getEdges(EdgeType edge_type) 
	{
		return hasEqualEdgeType(edge_type) ? this.getEdges() : Collections.<E>emptySet();
	}

	/**
	 * Returns the edge count for this graph if {@code edge_type} matches
	 * the edge type for this graph, and 0 otherwise.
	 */
    public int getEdgeCount(EdgeType edge_type)
    {
    	return hasEqualEdgeType(edge_type) ? this.getEdgeCount() : 0;
    }
    
    /**
     * Returns {@code true} if {@code edge_type} matches the default edge type for 
     * this graph, and {@code false} otherwise.
     * @param edge_type the edge type to compare to this instance's default edge type
     */
    protected boolean hasEqualEdgeType(EdgeType edge_type)
    {
    	return this.edge_type.equals(edge_type);
    }

    /**
     * Throws an {@code IllegalArgumentException} if {@code edge_type} does not
     * match the default edge type for this graph.
     * @param edge_type the edge type to compare to this instance's default edge type
     */
    protected void validateEdgeType(EdgeType edge_type)
    {
    	if (!hasEqualEdgeType(edge_type))
    		throw new IllegalArgumentException("Edge type '" + edge_type + 
    				"' does not match the default edge type for this graph: '" + 
    				this.edge_type + "'");
    }
}
