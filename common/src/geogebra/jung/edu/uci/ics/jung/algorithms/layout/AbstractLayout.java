/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 * Created on Jul 7, 2003
 * 
 */
package edu.uci.ics.jung.algorithms.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;
import org.apache.commons.collections15.functors.CloneTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Graph;

/**
 * Abstract class for implementations of {@code Layout}.  It handles some of the
 * basic functions: storing coordinates, maintaining the dimensions, initializing
 * the locations, maintaining locked vertices.
 * 
 * @author Danyel Fisher, Scott White
 * @author Tom Nelson - converted to jung2
 * @param <V> the vertex type
 * @param <E> the edge type
 */
abstract public class AbstractLayout<V, E> implements Layout<V,E> {

    /**
     * a set of vertices that should not move in relation to the
     * other vertices
     */
	private Set<V> dontmove = new HashSet<V>();

	protected Dimension size;
	protected Graph<V, E> graph;
	protected boolean initialized;
    
    protected Map<V, Point2D> locations = 
    	LazyMap.decorate(new HashMap<V, Point2D>(),
    			new Transformer<V,Point2D>() {
					public Point2D transform(V arg0) {
						return new Point2D.Double();
					}});


	/**
	 * Creates an instance which does not initialize the vertex locations.
	 * 
	 * @param graph the graph for which the layout algorithm is to be created.
	 */
	protected AbstractLayout(Graph<V, E> graph) {
	    if (graph == null) 
	    {
	        throw new IllegalArgumentException("Graph must be non-null");
	    }
		this.graph = graph;
	}
	
    @SuppressWarnings("unchecked")
    protected AbstractLayout(Graph<V,E> graph, Transformer<V,Point2D> initializer) {
		this.graph = graph;
		Transformer<V, ? extends Object> chain = 
			ChainedTransformer.getInstance(initializer, CloneTransformer.getInstance());
		this.locations = LazyMap.decorate(new HashMap<V,Point2D>(), (Transformer<V,Point2D>)chain);
		initialized = true;
	}
	
	protected AbstractLayout(Graph<V,E> graph, Dimension size) {
		this.graph = graph;
		this.size = size;
	}
	
	@SuppressWarnings("unchecked")
    protected AbstractLayout(Graph<V,E> graph, Transformer<V,Point2D> initializer, Dimension size) {
		this.graph = graph;
		Transformer<V, ? extends Object> chain = 
			ChainedTransformer.getInstance(initializer, CloneTransformer.getInstance());
		this.locations = LazyMap.decorate(new HashMap<V,Point2D>(), (Transformer<V,Point2D>)chain);
		this.size = size;
	}
    
    public void setGraph(Graph<V,E> graph) {
        this.graph = graph;
        if(size != null && graph != null) {
        	initialize();
        }
    }
    
	/**
	 * When a visualization is resized, it presumably wants to fix the
	 * locations of the vertices and possibly to reinitialize its data. The
	 * current method calls <tt>initializeLocations</tt> followed by <tt>initialize_local</tt>.
	 */
	public void setSize(Dimension size) {
		
		if(size != null && graph != null) {
			
			Dimension oldSize = this.size;
			this.size = size;
			initialize();
			
			if(oldSize != null) {
				adjustLocations(oldSize, size);
			}
		}
	}
	
	private void adjustLocations(Dimension oldSize, Dimension size) {

		int xOffset = (size.width - oldSize.width) / 2;
		int yOffset = (size.height - oldSize.height) / 2;

		// now, move each vertex to be at the new screen center
		while(true) {
		    try {
                for(V v : getGraph().getVertices()) {
		            offsetVertex(v, xOffset, yOffset);
		        }
		        break;
		    } catch(ConcurrentModificationException cme) {
		    }
		}
	}
    
    public boolean isLocked(V v) {
        return dontmove.contains(v);
    }
    
    @SuppressWarnings("unchecked")
    public void setInitializer(Transformer<V,Point2D> initializer) {
    	if(this.equals(initializer)) {
    		throw new IllegalArgumentException("Layout cannot be initialized with itself");
    	}
		Transformer<V, ? extends Object> chain = 
			ChainedTransformer.getInstance(initializer, CloneTransformer.getInstance());
    	this.locations = LazyMap.decorate(new HashMap<V,Point2D>(), (Transformer<V, Point2D>)chain);
    	initialized = true;
    }
    
	/**
	 * Returns the current size of the visualization space, accoring to the
	 * last call to resize().
	 * 
	 * @return the current size of the screen
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * Returns the Coordinates object that stores the vertex' x and y location.
	 * 
	 * @param v
	 *            A Vertex that is a part of the Graph being visualized.
	 * @return A Coordinates object with x and y locations.
	 */
	private Point2D getCoordinates(V v) {
        return locations.get(v);
	}
	
	public Point2D transform(V v) {
		return getCoordinates(v);
	}
	
	/**
	 * Returns the x coordinate of the vertex from the Coordinates object.
	 * in most cases you will be better off calling transform(v).
	 */
	public double getX(V v) {
        assert getCoordinates(v) != null : "Cannot getX for an unmapped vertex "+v;
        return getCoordinates(v).getX();
	}

	/**
	 * Returns the y coordinate of the vertex from the Coordinates object.
	 * In most cases you will be better off calling transform(v).
	 */
	public double getY(V v) {
        assert getCoordinates(v) != null : "Cannot getY for an unmapped vertex "+v;
        return getCoordinates(v).getY();
	}
	
	/**
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	protected void offsetVertex(V v, double xOffset, double yOffset) {
		Point2D c = getCoordinates(v);
        c.setLocation(c.getX()+xOffset, c.getY()+yOffset);
		setLocation(v, c);
	}

	/**
	 * Accessor for the graph that represets all vertices.
	 * 
	 * @return the graph that contains all vertices.
	 */
	public Graph<V, E> getGraph() {
	    return graph;
	}
	
	/**
	 * Forcibly moves a vertex to the (x,y) location by setting its x and y
	 * locations to the inputted location. Does not add the vertex to the
	 * "dontmove" list, and (in the default implementation) does not make any
	 * adjustments to the rest of the graph.
	 */
	public void setLocation(V picked, double x, double y) {
		Point2D coord = getCoordinates(picked);
		coord.setLocation(x, y);
	}

	public void setLocation(V picked, Point2D p) {
		Point2D coord = getCoordinates(picked);
		coord.setLocation(p);
	}

	/**
	 * Locks {@code v} in place if {@code state} is {@code true}, otherwise unlocks it.
	 */
	public void lock(V v, boolean state) {
		if(state == true) 
		    dontmove.add(v);
		else 
		    dontmove.remove(v);
	}
	
	/**
	 * Locks all vertices in place if {@code lock} is {@code true}, otherwise unlocks all vertices.
	 */
	public void lock(boolean lock) {
		for(V v : graph.getVertices()) {
			lock(v, lock);
		}
	}
}
