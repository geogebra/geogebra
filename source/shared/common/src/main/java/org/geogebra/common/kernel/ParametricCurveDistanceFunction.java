/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Distance function of a curve that implements UnivariateFunction.
 * 
 * @author Markus Hohenwarter
 */
public class ParametricCurveDistanceFunction implements DistanceFunction {

	// private GeoPoint P;
	private double px;
	private double py;
	private UnivariateFunction funX;
	private UnivariateFunction funY;

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
		setFunctions(curve);
	}

	/**
	 * set functions from curve
	 * 
	 * @param curve
	 *            curve
	 */
	public void setFunctions(ParametricCurve curve) {
		funX = curve.getUnivariateFunctionX();
		funY = curve.getUnivariateFunctionY();
	}

	/**
	 * Returns the square of the distance between the currently set distance
	 * point and this curve at parameter position t, i.e. (funX(t) - Px)^2 +
	 * (funY(t) - Py)^2.
	 */
	@Override
	public double value(double t) {
		double dx = funX.value(t) - px;
		double dy = funY.value(t) - py;
		return dx * dx + dy * dy;
	}

	/**
	 * Sets the point to be used in the distance function (funX(t) - Px)^2 +
	 * (funY(t) - Py)^2.
	 * 
	 * @param P
	 *            distant point
	 */
	@Override
	public void setDistantPoint(GeoPointND P) {
		Coords coords = P.getCoordsInD2();
		px = coords.getX() / coords.getZ();
		py = coords.getY() / coords.getZ();

	}

}
