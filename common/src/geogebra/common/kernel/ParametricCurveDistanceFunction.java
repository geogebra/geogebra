/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel;

import geogebra.common.kernel.geos.ParametricCurve;
import geogebra.common.kernel.roots.RealRootFunction;

/**
 * Distance function of a curve that implements RealRootFunction.
 * @author Markus Hohenwarter
 */
public class ParametricCurveDistanceFunction implements RealRootFunction {

	//private GeoPoint P;	
	private double px, py;
	private RealRootFunction funX, funY;
	private double mint;
	private double maxt;

	/**
	 * Creates a function for evaluating squared distance of (px,py)
	 * from curve (px and py must be entered using a setter)
	 * @param curve curve
	 */
	public ParametricCurveDistanceFunction(ParametricCurve curve) {		
		funX = curve.getRealRootFunctionX();
		funY = curve.getRealRootFunctionY();
		this.mint = curve.getMinParameter(); 
		this.maxt = curve.getMaxParameter();
	}

	/**
	 * Sets the point to be used in the distance function 
	 * (funX(t) - Px)^2 + (funY(t) - Py)^2.
	 * @param px distant point x-coord
	 * @param py distant point y-coord
	 */
	public void setDistantPoint(double px, double py) {
		this.px = px;
		this.py = py;
	}

	/**
	 * Returns the square of the distance between the currently set
	 * distance point and this curve at parameter position t, i.e. 
	 * (funX(t) - Px)^2 + (funY(t) - Py)^2.
	 */
	public double evaluate(double t) {

		// make sure it "wraps around" nicely 
		if (t < mint) { 
			t += (maxt - mint); 
		} else if (t > mint) { 
			t -= (maxt - mint); 
		} 

		double dx = funX.evaluate(t) - px;
		double dy = funY.evaluate(t) - py;
		return dx * dx + dy * dy;		
	}

}
