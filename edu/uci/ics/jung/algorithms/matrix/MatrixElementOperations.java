/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.matrix;

import java.util.Map;


/**
 * An interface for specifying the behavior of graph/matrix operations
 * for a particular element type.
 * <P>
 * Graph/matrix multiplication requires the definition of two operations: 
 * <p>
 * <ol>
 * <li>
 * Calculating an aggregate property of paths of length 2 between two
 * vertices v1 and v2 (analogous to element multiplication in matrix
 * arithmetic); this is handled by computePathData().
 * </li>
 * <li>
 * Aggregating the properties of all such paths, and assigning the result to
 * a new edge in the output graph (analogous to element addition in matrix
 * arithmetic); this is handled by mergePaths().
 * </li>
 * </ol>
 * <p>
 * Together, computePathData() and mergePaths() specify how the equivalent of
 * the vector inner (dot) product is to function.
 * <p>
 * For instance, to implement the equivalent of standard matrix multiplication
 * on two graphs, computePathData() should return the products of the 
 * weights of a two-edge path, and mergePaths() should add
 * the output of computePathData() to an existing edge (or possibly create such
 * an edge if none exists).
 * 
 * @author Joshua O'Madadhain
 */
public interface MatrixElementOperations<E>
{
    /**
     * If either e or pathData is null, the effect of mergePaths() is
     * implementation-dependent.
     * 
     * @param e		(possibly) existing edge in the output graph which
     * 					represents a path in the input graph(s)
     * 
     * @param pathData	data (which represents another path with the same source
     * and destination as e in the input graphs) which is to be merged into e
     */
    public void mergePaths(E e, Object pathData); 
    
    /**
     * If either e1 or e2 is null, the Object reference returned should be null.
     * 
     * @param e1 first edge from 2-edge path in input graph(s)
     * @param e2 second edge from 2-edge path in input graph(s)
     * @return aggregation of data from the edges of the 2-edge path
     * (from source of e1 to destination of e2) comprised of (e1, e2)
     */
    public Number computePathData(E e1, E e2);
    
    /**
     * Returns a map from edges to values.
     */
    public Map<E,Number> getEdgeData();
}