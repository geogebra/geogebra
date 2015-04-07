/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.roots.RealRootFunction;

/**
 * Distance function of a curve that implements RealRootFunction.
 * 
 * @author Markus Hohenwarter
 */
public class ParametricCurveDistanceFunction implements RealRootFunction,
		DistanceFunction {

	// private GeoPoint P;
	private double px, py;
	private RealRootFunction funX, funY;

	// private double mint;
	// private double maxt;

	/**
	 * Creates a function for evaluating squared distance of (px,py) from curve
	 * (px and py must be entered using a setter)
	 * 
	 * @param curve
	 *            curve
	 */
	public ParametricCurveDistanceFunction(ParametricCurve curve) {
		funX = curve.getRealRootFunctionX();
		funY = curve.getRealRootFunctionY();
		// this.mint = curve.getMinParameter();
		// this.maxt = curve.getMaxParameter();
	}

	/**
	 * Returns the square of the distance between the currently set distance
	 * point and this curve at parameter position t, i.e. (funX(t) - Px)^2 +
	 * (funY(t) - Py)^2.
	 */
	public double evaluate(double t) {

		double dx = funX.evaluate(t) - px;
		double dy = funY.evaluate(t) - py;
		return dx * dx + dy * dy;
	}

	/**
	 * Sets the point to be used in the distance function (funX(t) - Px)^2 +
	 * (funY(t) - Py)^2.
	 * 
	 * @param px
	 *            distant point x-coord
	 * @param py
	 *            distant point y-coord
	 */
	@Override
	public void setDistantPoint(GeoPointND P) {
		Coords coords = P.getCoordsInD2();
		px = coords.getX() / coords.getZ();
		py = coords.getY() / coords.getZ();

	}

}
