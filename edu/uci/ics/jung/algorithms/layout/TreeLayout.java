/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 9, 2005
 */

package edu.uci.ics.jung.algorithms.layout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.TreeUtils;

/**
 * @author Karlheinz Toni
 * @author Tom Nelson - converted to jung2
 *  
 */

public class TreeLayout<V,E> implements Layout<V,E> {

	protected Dimension size = new Dimension(600,600);
	protected Forest<V,E> graph;
	protected Map<V,Integer> basePositions = new HashMap<V,Integer>();

    protected Map<V, Point2D> locations = 
    	LazyMap.decorate(new HashMap<V, Point2D>(),
    			new Transformer<V,Point2D>() {
					public Point2D transform(V arg0) {
						return new Point2D.Double();
					}});
    
    protected transient Set<V> alreadyDone = new HashSet<V>();

    /**
     * The default horizontal vertex spacing.  Initialized to 50.
     */
    public static int DEFAULT_DISTX = 50;
    
    /**
     * The default vertical vertex spacing.  Initialized to 50.
     */
    public static int DEFAULT_DISTY = 50;
    
    /**
     * The horizontal vertex spacing.  Defaults to {@code DEFAULT_XDIST}.
     */
    protected int distX = 50;
    
    /**
     * The vertical vertex spacing.  Defaults to {@code DEFAULT_YDIST}.
     */
    protected int distY = 50;
    
    protected transient Point m_currentPoint = new Point();

    /**
     * Creates an instance for the specified graph with default X and Y distances.
     */
    public TreeLayout(Forest<V,E> g) {
    	this(g, DEFAULT_DISTX, DEFAULT_DISTY);
    }

    /**
     * Creates an instance for the specified graph and X distance with
     * default Y distance.
     */
    public TreeLayout(Forest<V,E> g, int distx) {
        this(g, distx, DEFAULT_DISTY);
    }

    /**
     * Creates an instance for the specified graph, X distance, and Y distance.
     */
    public TreeLayout(Forest<V,E> g, int distx, int disty) {
        if (g == null)
            throw new IllegalArgumentException("Graph must be non-null");
        if (distx < 1 || disty < 1)
            throw new IllegalArgumentException("X and Y distances must each be positive");
    	this.graph = g;
        this.distX = distx;
        this.distY = disty;
        buildTree();
    }
    
	protected void buildTree() {
        this.m_currentPoint = new Point(0, 20);
        Collection<V> roots = TreeUtils.getRoots(graph);
        if (roots.size() > 0 && graph != null) {
       		calculateDimensionX(roots);
       		for(V v : roots) {
        		calculateDimensionX(v);
        		m_currentPoint.x += this.basePositions.get(v)/2 + this.distX;
        		buildTree(v, this.m_currentPoint.x);
        	}
        }
        int width = 0;
        for(V v : roots) {
        	width += basePositions.get(v);
        }
    }

    protected void buildTree(V v, int x) {

        if (!alreadyDone.contains(v)) {
            alreadyDone.add(v);

            //go one level further down
            this.m_currentPoint.y += this.distY;
            this.m_currentPoint.x = x;

            this.setCurrentPositionFor(v);

            int sizeXofCurrent = basePositions.get(v);

            int lastX = x - sizeXofCurrent / 2;

            int sizeXofChild;
            int startXofChild;

            for (V element : graph.getSuccessors(v)) {
                sizeXofChild = this.basePositions.get(element);
                startXofChild = lastX + sizeXofChild / 2;
                buildTree(element, startXofChild);
                lastX = lastX + sizeXofChild + distX;
            }
            this.m_currentPoint.y -= this.distY;
        }
    }
    
    private int calculateDimensionX(V v) {

        int size = 0;
        int childrenNum = graph.getSuccessors(v).size();

        if (childrenNum != 0) {
            for (V element : graph.getSuccessors(v)) {
                size += calculateDimensionX(element) + distX;
            }
        }
        size = Math.max(0, size - distX);
        basePositions.put(v, size);

        return size;
    }

    private int calculateDimensionX(Collection<V> roots) {

    	int size = 0;
    	for(V v : roots) {
    		int childrenNum = graph.getSuccessors(v).size();

    		if (childrenNum != 0) {
    			for (V element : graph.getSuccessors(v)) {
    				size += calculateDimensionX(element) + distX;
    			}
    		}
    		size = Math.max(0, size - distX);
    		basePositions.put(v, size);
    	}

    	return size;
    }
    
    /**
     * This method is not supported by this class.  The size of the layout
     * is determined by the topology of the tree, and by the horizontal 
     * and vertical spacing (optionally set by the constructor).
     */
    public void setSize(Dimension size) {
        throw new UnsupportedOperationException("Size of TreeLayout is set" +
                " by vertex spacing in constructor");
    }

    protected void setCurrentPositionFor(V vertex) {
    	int x = m_currentPoint.x;
    	int y = m_currentPoint.y;
    	if(x < 0) size.width -= x;
    	
    	if(x > size.width-distX) 
    		size.width = x + distX;
    	
    	if(y < 0) size.height -= y;
    	if(y > size.height-distY) 
    		size.height = y + distY;
    	locations.get(vertex).setLocation(m_currentPoint);

    }

	public Graph<V,E> getGraph() {
		return graph;
	}

	public Dimension getSize() {
		return size;
	}

	public void initialize() {

	}

	public boolean isLocked(V v) {
		return false;
	}

	public void lock(V v, boolean state) {
	}

	public void reset() {
	}

	public void setGraph(Graph<V,E> graph) {
		if(graph instanceof Forest) {
			this.graph = (Forest<V,E>)graph;
			buildTree();
		} else {
			throw new IllegalArgumentException("graph must be a Forest");
		}
	}

	public void setInitializer(Transformer<V, Point2D> initializer) {
	}
	
    /**
     * Returns the center of this layout's area.
     */
	public Point2D getCenter() {
		return new Point2D.Double(size.getWidth()/2,size.getHeight()/2);
	}

	public void setLocation(V v, Point2D location) {
		locations.get(v).setLocation(location);
	}
	
	public Point2D transform(V v) {
		return locations.get(v);
	}
}
