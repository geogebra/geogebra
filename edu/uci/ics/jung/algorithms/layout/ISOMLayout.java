/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package edu.uci.ics.jung.algorithms.layout;

import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a self-organizing map layout algorithm, based on Meyer's
 * self-organizing graph methods.
 *
 * @author Yan Biao Boey
 */
public class ISOMLayout<V, E> extends AbstractLayout<V,E> implements IterativeContext {

	Map<V, ISOMVertexData> isomVertexData =
		LazyMap.decorate(new HashMap<V, ISOMVertexData>(),
				new Factory<ISOMVertexData>() {
					public ISOMVertexData create() {
						return new ISOMVertexData();
					}});

	private int maxEpoch;
	private int epoch;

	private int radiusConstantTime;
	private int radius;
	private int minRadius;

	private double adaption;
	private double initialAdaption;
	private double minAdaption;

    protected GraphElementAccessor<V,E> elementAccessor =
    	new RadiusGraphElementAccessor<V,E>();

	private double coolingFactor;

	private List<V> queue = new ArrayList<V>();
	private String status = null;

	/**
	 * Returns the current number of epochs and execution status, as a string.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Creates an <code>ISOMLayout</code> instance for the specified graph <code>g</code>.
	 * @param g
	 */
	public ISOMLayout(Graph<V,E> g) {
		super(g);
	}

	public void initialize() {

		setInitializer(new RandomLocationTransformer<V>(getSize()));
		maxEpoch = 2000;
		epoch = 1;

		radiusConstantTime = 100;
		radius = 5;
		minRadius = 1;

		initialAdaption = 90.0D / 100.0D;
		adaption = initialAdaption;
		minAdaption = 0;

		//factor = 0; //Will be set later on
		coolingFactor = 2;

		//temperature = 0.03;
		//initialJumpRadius = 100;
		//jumpRadius = initialJumpRadius;

		//delay = 100;
	}


	/**
	* Advances the current positions of the graph elements.
	*/
	public void step() {
		status = "epoch: " + epoch + "; ";
		if (epoch < maxEpoch) {
			adjust();
			updateParameters();
			status += " status: running";

		} else {
			status += "adaption: " + adaption + "; ";
			status += "status: done";
//			done = true;
		}
	}

	private synchronized void adjust() {
		//Generate random position in graph space
		Point2D tempXYD = new Point2D.Double();

		// creates a new XY data location
        tempXYD.setLocation(10 + Math.random() * getSize().getWidth(),
                10 + Math.random() * getSize().getHeight());

		//Get closest vertex to random position
		V winner = elementAccessor.getVertex(this, tempXYD.getX(), tempXYD.getY());

		while(true) {
		    try {
		    	for(V v : getGraph().getVertices()) {
		            ISOMVertexData ivd = getISOMVertexData(v);
		            ivd.distance = 0;
		            ivd.visited = false;
		        }
		        break;
		    } catch(ConcurrentModificationException cme) {}
        }
		adjustVertex(winner, tempXYD);
	}

	private synchronized void updateParameters() {
		epoch++;
		double factor = Math.exp(-1 * coolingFactor * (1.0 * epoch / maxEpoch));
		adaption = Math.max(minAdaption, factor * initialAdaption);
		//jumpRadius = (int) factor * jumpRadius;
		//temperature = factor * temperature;
		if ((radius > minRadius) && (epoch % radiusConstantTime == 0)) {
			radius--;
		}
	}

	private synchronized void adjustVertex(V v, Point2D tempXYD) {
		queue.clear();
		ISOMVertexData ivd = getISOMVertexData(v);
		ivd.distance = 0;
		ivd.visited = true;
		queue.add(v);
		V current;

		while (!queue.isEmpty()) {
			current = queue.remove(0);
			ISOMVertexData currData = getISOMVertexData(current);
			Point2D currXYData = transform(current);

			double dx = tempXYD.getX() - currXYData.getX();
			double dy = tempXYD.getY() - currXYData.getY();
			double factor = adaption / Math.pow(2, currData.distance);

			currXYData.setLocation(currXYData.getX()+(factor*dx), currXYData.getY()+(factor*dy));

			if (currData.distance < radius) {
			    Collection<V> s = getGraph().getNeighbors(current);
			    while(true) {
			        try {
			        	for(V child : s) {
			                ISOMVertexData childData = getISOMVertexData(child);
			                if (childData != null && !childData.visited) {
			                    childData.visited = true;
			                    childData.distance = currData.distance + 1;
			                    queue.add(child);
			                }
			            }
			            break;
			        } catch(ConcurrentModificationException cme) {}
			    }
			}
		}
	}

	protected ISOMVertexData getISOMVertexData(V v) {
		return isomVertexData.get(v);
	}

	/**
	 * This one is an incremental visualization.
	 * @return <code>true</code> is the layout algorithm is incremental, <code>false</code> otherwise
	 */
	public boolean isIncremental() {
		return true;
	}

	/**
	 * Returns <code>true</code> if the vertex positions are no longer being
	 * updated.  Currently <code>ISOMLayout</code> stops updating vertex
	 * positions after a certain number of iterations have taken place.
	 * @return <code>true</code> if the vertex position updates have stopped,
	 * <code>false</code> otherwise
	 */
	public boolean done() {
		return epoch >= maxEpoch;
	}

	protected static class ISOMVertexData {
		int distance;
		boolean visited;

		protected ISOMVertexData() {
		    distance = 0;
		    visited = false;
		}
	}

	/**
	 * Resets the layout iteration count to 0, which allows the layout algorithm to
	 * continue updating vertex positions.
	 */
	public void reset() {
		epoch = 0;
	}
}