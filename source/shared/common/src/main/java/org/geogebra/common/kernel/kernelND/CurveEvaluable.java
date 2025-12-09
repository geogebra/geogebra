/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for geos that can be evaluate as a curve. evaluateCurve() may
 * compute 2D or 3D points.
 * 
 * @author mathieu
 *
 */
public interface CurveEvaluable {

	/**
	 * Returns the smallest possible parameter value for this path (may be
	 * Double.NEGATIVE_INFINITY)
	 * 
	 * @return minimum parameter value for this path
	 */
	public double getMinParameter();

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY)
	 * 
	 * @return maximum parameter value for this path
	 */
	public double getMaxParameter();

	/**
	 * create new double[] to store point coords
	 * 
	 * @return new double[] with correct dimension
	 */
	public double[] newDoubleArray();

	/**
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return max abs distance between points coords
	 */
	public double distanceMax(double[] p1, double[] p2);

	/**
	 * Evaluates the curve for given parameter value
	 * 
	 * @param t
	 *            parameter value
	 * @param out
	 *            array to store the result
	 */
	void evaluateCurve(double t, double[] out);

	/**
	 * @param a
	 *            start parameter
	 * @param b
	 *            end parameter
	 * @return an interval within [a, b] where the curve is defined.
	 * 
	 */
	public double[] getDefinedInterval(double a, double b);

	/**
	 * @return whether this curve is tracing
	 */
	public boolean getTrace();

	/**
	 * @return whether this curve is closed
	 */
	public boolean isClosedPath();

	/**
	 * @return whether this is a function of x
	 */
	public boolean isFunctionInX();

	/**
	 * @return cast to geo OR cast parent to geo
	 */
	public GeoElement toGeoElement();

	default double getMinDistX() {
		return 0;
	}

	/**
	 * Update simplified expressions to speed up plotting
	 */
	default void updateExpandedFunctions() {
		// only for curves
	}
}
