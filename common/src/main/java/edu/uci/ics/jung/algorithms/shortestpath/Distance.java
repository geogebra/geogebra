/*
 * Created on Apr 2, 2004
 *
 * Copyright (c) 2004, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.shortestpath;

import java.util.Map;


/**
 * An interface for classes which calculate the distance between
 * one vertex and another.
 * 
 * @author Joshua O'Madadhain
 */
public interface Distance<V>
{
    /**
     * Returns the distance from the <code>source</code> vertex 
     * to the <code>target</code> vertex.  If <code>target</code> 
     * is not reachable from <code>source</code>, returns null.
     */ 
     Number getDistance(V source, V target);

    /**
     * <p>Returns a <code>Map</code> which maps each vertex 
     * in the graph (including the <code>source</code> vertex) 
     * to its distance (represented as a Number) 
     * from <code>source</code>.  If any vertex 
     * is not reachable from <code>source</code>, no 
     * distance is stored for that vertex.
     */
     Map<V,Number> getDistanceMap(V source);
}
