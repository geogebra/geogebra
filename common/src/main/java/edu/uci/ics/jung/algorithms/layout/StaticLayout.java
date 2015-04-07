/*
 * Created on Jul 21, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

/**
 * StaticLayout places the vertices in the locations specified by its Transformer<V,Point2D>
 * initializer. Vertex locations can be placed in a Map<V,Point2D> and then supplied to
 * this layout as follows:
 * <code>
            Transformer<V,Point2D> vertexLocations =
        	TransformerUtils.mapTransformer(map);
 * </code>
 * @author Tom Nelson - tomnelson@dev.java.net
 *
 * @param <V>
 * @param <E>
 */
public class StaticLayout<V, E> extends AbstractLayout<V,E> {
	
    /**
     * Creates an instance for the specified graph, locations, and size.
     */
    public StaticLayout(Graph<V,E> graph, Transformer<V,Point2D> initializer, Dimension size) {
        super(graph, initializer, size);
    }
    
    /**
     * Creates an instance for the specified graph and locations, with default size.
     */
    public StaticLayout(Graph<V,E> graph, Transformer<V,Point2D> initializer) {
        super(graph, initializer);
    }
    
    /**
     * Creates an instance for the specified graph and default size; vertex locations
     * are randomly assigned.
     */
    public StaticLayout(Graph<V,E> graph) {
    	super(graph);
    }
    
    /**
     * Creates an instance for the specified graph and size.
     */
    public StaticLayout(Graph<V,E> graph, Dimension size) {
    	super(graph, size);
    }
    
    public void initialize() {}

	public void reset() {}

}
