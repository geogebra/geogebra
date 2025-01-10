package org.geogebra.common.kernel.cas;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * T = sqrt( 1 + f'(x)^2)
 */
class LengthFunction implements UnivariateFunction {
	private final GeoFunction f1;

	/**
	 * @param f1
	 *            derivative of measured function
	 */
	public LengthFunction(GeoFunction f1) {
		this.f1 = f1;
	}

	@Override
	public double value(double t) {
		double p = f1.value(t);
		return Math.sqrt(1 + p * p);
	}
}