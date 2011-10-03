/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.roots.RealRootFunction;

/**
 * Distance function of a curve that implements RealRootFunction.
 * @author Markus Hohenwarter
 */
public class ParametricCurveDistanceFunction implements RealRootFunction {
	
	//private GeoPoint P;	
	private double px, py;
	private RealRootFunction funX, funY;
	
	public ParametricCurveDistanceFunction(ParametricCurve curve) {		
		funX = curve.getRealRootFunctionX();
		funY = curve.getRealRootFunctionY();
	}
	
	/**
	 * Sets the point to be used in the distance function 
	 * (funX(t) - Px)^2 + (funY(t) - Py)^2.
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
		double dx = funX.evaluate(t) - px;
		double dy = funY.evaluate(t) - py;
		return dx * dx + dy * dy;		
	}
	
}
