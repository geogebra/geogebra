/*
 * Created on Feb 3, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph;


/**
 * A subtype of <code>Graph</code> which is a (directed, rooted) tree.
 * What we refer to as a "tree" here is actually (in the terminology of graph theory) a
 * rooted tree.  (That is, there is a designated single vertex--the <i>root</i>--from which we measure
 * the shortest path to each vertex, which we call its <i>depth</i>; the maximum over all such 
 * depths is the tree's <i>height</i>.  Note that for a tree, there is exactly
 * one unique path from the root to any vertex.)
 * 
 * @author Joshua O'Madadhain
 */
public interface Tree<V,E> extends Forest<V,E>
{
    /**
     * Returns the (unweighted) distance of <code>vertex</code> 
     * from the root of this tree.
     * @param vertex    the vertex whose depth is to be returned.
     * @return the length of the shortest unweighted path 
     * from <code>vertex</code> to the root of this tree
     * @see #getHeight()
     */
    public int getDepth(V vertex);
    
    /**
     * Returns the maximum depth in this tree.
     * @return the maximum depth in this tree
     * @see #getDepth(Object)
     */
    public int getHeight();
    
    /**
     * Returns the root of this tree.
     * The root is defined to be the vertex (designated either at the tree's
     * creation time, or as the first vertex to be added) with respect to which 
     * vertex depth is measured.
     * @return the root of this tree
     */
    public V getRoot();
}
