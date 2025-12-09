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

package org.geogebra.common.kernel.geos;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

/**
 * T = sqrt(a'(t)^2+b'(t)^2)
 */
public class LengthCurve implements UnivariateFunction {
	private GeoCurveCartesianND c1;
	private double[] f1eval = new double[] { 0, 0, 0 };

	/**
	 * Curve length function for numeric estimation of curve length
	 * 
	 * @param c1
	 *            derivative of measured curve
	 */
	public LengthCurve(GeoCurveCartesianND c1) {
		this.c1 = c1;
	}

	@Override
	public double value(double t) {
		c1.evaluateCurve(t, f1eval);
		return Math.sqrt(f1eval[0] * f1eval[0] + f1eval[1] * f1eval[1]
				+ f1eval[2] * f1eval[2]);
	}
}