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


import java.util.HashMap;
import java.util.Map;

/**
 * Implements the basic matrix operations on double-precision values.  Assumes
 * that the edges have a MutableDouble value.
 * 
 * @author Joshua O'Madadhain
 */
public class RealMatrixElementOperations<E> implements MatrixElementOperations<E>
{
    private Map<E,Number> edgeData = new HashMap<E,Number>();

    /**
     * Creates an instance using the specified edge values.
     */
    public RealMatrixElementOperations(Map<E,Number> edgeData)
    {
        this.edgeData = edgeData;
    }

	/**
	 * @see MatrixElementOperations#mergePaths(Object, Object)
	 */
	public void mergePaths(E e, Object pathData) 
    {

        Number pd = (Number)pathData;
        Number ed = edgeData.get(e);
        if (ed == null) {
        	edgeData.put(e, pd);

        } else {
        	edgeData.put(e, ed.doubleValue()+pd.doubleValue());

        }

	}

	/**
	 * @see MatrixElementOperations#computePathData(Object, Object)
	 */
	public Number computePathData(E e1, E e2) 
    {
        double d1 = edgeData.get(e1).doubleValue();
        double d2 = edgeData.get(e2).doubleValue();
        return d1*d2;
	}

	/**
	 * @return the edgeData
	 */
	public Map<E, Number> getEdgeData() {
		return edgeData;
	}
}
