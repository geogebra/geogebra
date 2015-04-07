/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */

package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * a pure decorator for the Layout interface. Intended to be overridden
 * to provide specific behavior decoration
 * 
 * @author Tom Nelson 
 *
 */
public abstract class LayoutDecorator<V, E> implements Layout<V, E>, IterativeContext {
    
    protected Layout<V, E> delegate;
    
    /**
     * Creates an instance backed by the specified delegate layout.
     */
    public LayoutDecorator(Layout<V, E> delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns the backing (delegate) layout.
     */
    public Layout<V,E> getDelegate() {
        return delegate;
    }

    /**
     * Sets the backing (delegate) layout.
     */
    public void setDelegate(Layout<V,E> delegate) {
        this.delegate = delegate;
    }

    /**
     * @see edu.uci.ics.jung.algorithms.util.IterativeContext#done()
     */
    public void step() {
    	if(delegate instanceof IterativeContext) {
    		((IterativeContext)delegate).step();
    	}
    }

    /**
	 * 
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#initialize()
	 */
	public void initialize() {
		delegate.initialize();
	}

	/**
	 * @param initializer
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#setInitializer(org.apache.commons.collections15.Transformer)
	 */
	public void setInitializer(Transformer<V, Point2D> initializer) {
		delegate.setInitializer(initializer);
	}

	/**
	 * @param v
	 * @param location
	 * @see edu.uci.ics.jung.algorithms.layout.Layout#setLocation(java.lang.Object, java.awt.geom.Point2D)
	 */
	public void setLocation(V v, Point2D location) {
		delegate.setLocation(v, location);
	}

    /**
     * @see edu.uci.ics.jung.algorithms.layout.Layout#getSize()
     */
    public Dimension getSize() {
        return delegate.getSize();
    }

    /**
     * @see edu.uci.ics.jung.algorithms.layout.Layout#getGraph()
     */
    public Graph<V, E> getGraph() {
        return delegate.getGraph();
    }

    /**
     * @see edu.uci.ics.jung.algorithms.layout.Layout#transform(Object)
     */
    public Point2D transform(V v) {
        return delegate.transform(v);
    }

    /**
     * @see edu.uci.ics.jung.algorithms.util.IterativeContext#done()
     */
    public boolean done() {
    	if(delegate instanceof IterativeContext) {
    		return ((IterativeContext)delegate).done();
    	}
    	return true;
    }

    /**
     * @see edu.uci.ics.jung.algorithms.layout.Layout#lock(Object, boolean)
     */
    public void lock(V v, boolean state) {
        delegate.lock(v, state);
    }

    /**
     * @see edu.uci.ics.jung.algorithms.layout.Layout#isLocked(Object)
     */
    public boolean isLocked(V v) {
        return delegate.isLocked(v);
    }
    
    /**
     * @see edu.uci.ics.jung.algorithms.layout.Layout#setSize(Dimension)
     */
    public void setSize(Dimension d) {
        delegate.setSize(d);
    }

    /**
     * @see edu.uci.ics.jung.algorithms.layout.Layout#reset()
     */
    public void reset() {
    	delegate.reset();
    }
    
    public void setGraph(Graph<V, E> graph) {
        delegate.setGraph(graph);
    }
}
