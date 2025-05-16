package org.geogebra.common.kernel.cas;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * T = sqrt( 1 + f'(x)^2)
 */
class LengthFunction implements UnivariateFunction {
	private final GeoFunction df;
	private final GeoFunction f;

	/**
	 * @param df
	 *            derivative of measured function
	 * @param f measured function
	 */
	public LengthFunction(GeoFunction df, GeoFunction f) {
		this.df = df;
		this.f = f;
	}

	@Override
	public double value(double t) {
		double p = df.value(t);
		return Math.hypot(1, p);
	}

	/**
	 * Compute definite integral from a to b.
	 * If this length function goes to infinity, but the underlying function is finite
	 * at the edges of the interval, replace parts of the function by segments.
	 * @param a interval min
	 * @param b interval max
	 * @return integral value
	 */
	public double integral(double a, double b) {
		double dist = b - a;
		double edges = 0;
		double dx = dist * Kernel.MAX_PRECISION;
		double min = a;
		double max = b;
		if (Double.isInfinite(value(min))) {
			edges += Math.hypot(dx, f.value(min + dx) - f.value(min));
			min += dx;
		}
		if (Double.isInfinite(value(max))) {
			edges += Math.hypot(dx, f.value(max - dx) - f.value(max));
			max -= dx;
		}
		return Math.abs(AlgoIntegralDefinite.numericIntegration(this, min, max)) + edges;
	}
}