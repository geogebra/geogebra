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