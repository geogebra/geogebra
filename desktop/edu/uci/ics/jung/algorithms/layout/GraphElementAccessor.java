/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 *
 * Created on Apr 12, 2005
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Shape;
import java.util.Collection;

/**
 * Interface for coordinate-based selection of graph components.
 * @author Tom Nelson
 * @author Joshua O'Madadhain
 */
public interface GraphElementAccessor<V, E>
{
    /**
     * Returns a vertex which is associated with the 
     * location <code>(x,y)</code>.  This is typically determined
     * with respect to the vertex's location as specified
     * by a <code>Layout</code>.
     */
    V getVertex(Layout<V,E> layout, double x, double y);
    
    /**
     * Returns the vertices contained within {@code rectangle} relative
     * to {@code layout}.
     */
    Collection<V> getVertices(Layout<V,E> layout, Shape rectangle);

    /**
     * Returns an edge which is associated with the 
     * location <code>(x,y)</code>.  This is typically determined
     * with respect to the edge's location as specified
     * by a {@code Layout}.
     */
    E getEdge(Layout<V,E> layout, double x, double y);

}