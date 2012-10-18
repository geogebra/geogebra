/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoPoint;

/**
 * @author Markus
 */
public interface PathMover {
	/** minimal number of steps*/
	public static final int MIN_STEPS = 128; // 128;
	/** ratio for slowing down*/
	public static final double STEP_DECREASE_FACTOR = 0.5;
	/** ratio for speeding up*/
	public static final double STEP_INCREASE_FACTOR = 2;
	/** minimal step width*/
	public static final double MIN_STEP_WIDTH = 1E-8;
	/** -1*(minimal step width)	 */
	public static final double NEG_MIN_STEP_WIDTH = -1E-8;
	/** normalized parameter is defined on open interval,
	 * to avoid the borders, we use this offset*/
	public static final double OPEN_BORDER_OFFSET = 1E-5;	
	/** maximal number of points*/
	public static final int MAX_POINTS = 10000;
	
	/**
	 * Inits the path mover using a point p on the path
	 * and sets the orientation to positive.
	 * Note: the path parameter of p may be changed here!
	 * @param p initial point
	 */
	public void init(GeoPoint p);
	
	/**
	 * Inits the path mover using a point p on the path
	 * and sets the orientation to positive.
	 * Note: the path parameter of p may be changed here!
	 * @param p initial point
	 * @param min_steps minimal number of steps for the particular instance
	 */
	public void init(GeoPoint p, int min_steps);
	
	/**
	 * Inits the path mover using a path parameter on the path
	 * and sets the orientation to positive.
	 */
	//public void init(double param);	
	
	/**
	 * Sets point p to the current position on the path
	 * @param p current position
	 */
	public void getCurrentPosition(GeoPoint p);
	
	/**
	 * Sets point p to the next position on the path
	 * @param p point to be set
	 * @return true: draw line to point p; false: move to point p
	 */
	public boolean getNext(GeoPoint p);
	
	/**
	 * @return false whenever the next call of getNext() 
     * would lead to passing the init path parameter
     * (note: there are two orientations) 
	 */
	public boolean hasNext();	
	
	/**
	 * Resets this path mover to the inital start parameter.	 	 
	 */
	public void resetStartParameter();
	
	/**
	 * @return current path parameter
	 */
	public double getCurrentParameter();
	
	/**
	 * Changes the orientation of moving along the
	 * path.
	 */
	public void changeOrientation();	
	
	/**
	 * Returns whether the orientation of moving along the
	 * path is positive.
	 * @return true for positive orientation
	 */
	public boolean hasPositiveOrientation();	
	
	/**
	 * Decreases the step width. Returns wheter this was possible. 
	 * @return true if it was possible
	 */
	public boolean smallerStep();
	
	/**
	 * Increases the step width. Returns whether this was possible.	 
	 * @return true if it was possible
	 */
	public boolean biggerStep();
	
	/**
	 * Sets step width. Returns whether this was possible.	 
	 * @param step step width
	 * @return true if this was possible
	 */
	public boolean setStep(double step);
	
	/**
	 * Get step width.	 	 
	 * @return step
	 */
	public double getStep();
	
	/**
	 * Goes back one step.
	 */
	public void stepBack();
}
