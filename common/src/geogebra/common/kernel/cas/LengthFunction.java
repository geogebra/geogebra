package geogebra.common.kernel.cas;

import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.roots.RealRootFunction;

/**
 * T = sqrt( 1 + f'(x)^2)
 */
class LengthFunction implements RealRootFunction {
	private final GeoFunction f1;

	/**
	 * @param f1 derivative of measured function
	 */
	public LengthFunction(GeoFunction f1) {
		this.f1 = f1;
	}

	public double evaluate(double t) {
		double p = f1.evaluate(t);
		return Math.sqrt(1 + p * p);
	}
}