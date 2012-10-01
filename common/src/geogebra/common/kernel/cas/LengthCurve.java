package geogebra.common.kernel.cas;

import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.roots.RealRootFunction;

/**
 * T = sqrt(a'(t)^2+b'(t)^2)
 */
public class LengthCurve implements RealRootFunction {
	private	GeoCurveCartesian c1;
	/**
	 * Curve length function for numeric estimation of curve length
	 * @param c1 derivative of measured curve
	 */
	public LengthCurve(GeoCurveCartesian c1) {
		this.c1 = c1;
	}

	public double evaluate(double t) {
		double f1eval[] = new double[2];
		c1.evaluateCurve(t, f1eval);
		return (Math.sqrt(f1eval[0] * f1eval[0] + f1eval[1] * f1eval[1]));
	}
}