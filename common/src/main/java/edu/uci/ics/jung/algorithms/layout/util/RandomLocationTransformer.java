/*
 * Created on Jul 19, 2005
 *
 * Copyright (c) 2005, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.layout.util;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Random;

import org.apache.commons.collections15.Transformer;

/**
 * Transforms the input type into a random location within
 * the bounds of the Dimension property.
 * This is used as the backing Transformer for the LazyMap
 * for many Layouts,
 * and provides a random location for unmapped vertices
 * the first time they are accessed.
 * 
 * @author Tom Nelson
 *
 * @param <V>
 */
public class RandomLocationTransformer<V> implements Transformer<V,Point2D> {

	Dimension d;
	Random random;
    
    /**
     * Creates an instance with the specified size which uses the current time 
     * as the random seed.
     */
    public RandomLocationTransformer(Dimension d) {
    	this(d, new Date().getTime());
    }
    
    /**
     * Creates an instance with the specified dimension and random seed.
     * @param d
     * @param seed
     */
    public RandomLocationTransformer(final Dimension d, long seed) {
    	this.d = d;
    	this.random = new Random(seed);
    }
    
    public Point2D transform(V v) {
        return new Point2D.Double(random.nextDouble() * d.width, random.nextDouble() * d.height);
    }
}
