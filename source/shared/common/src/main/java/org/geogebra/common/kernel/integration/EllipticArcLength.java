/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.integration;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Computes the arc length of an ellipse.
 */
public class EllipticArcLength {
	/** half axes of the ellipse */
	double[] halfAxes;
	private UnivariateFunction arcLengthFunction;

	/**
	 * Creates new elliptic arc length calculator
	 * 
	 * @param ellipse
	 *            ellipse
	 */
	public EllipticArcLength(GeoConicND ellipse) {
		halfAxes = ellipse.getHalfAxes();
		arcLengthFunction = new EllipticArcLengthFunction();
	}

	/**
	 * Computes the arc length of an ellipse where a is the start parameter and
	 * b is the end parameter of the arc in radians.
	 * 
	 * @param a
	 *            start param
	 * @param b
	 *            end param
	 * @return arc length
	 */
	public double compute(double a, double b) {
		if (a <= b) {
			return AlgoIntegralDefinite.numericIntegration(arcLengthFunction, a,
					b);
		}
		return AlgoIntegralDefinite.numericIntegration(arcLengthFunction, 0,
				Kernel.PI_2)
				- AlgoIntegralDefinite.numericIntegration(arcLengthFunction, b,
						a);

	}

	/**
	 * f(t) = sqrt((a sin(t))^2 + (b cos(t))^2)
	 */
	private class EllipticArcLengthFunction implements UnivariateFunction {

		protected EllipticArcLengthFunction() {
			// avoid synth access warning

		}

		@Override
		public double value(double t) {
			double p = halfAxes[0] * Math.sin(t);
			double q = halfAxes[1] * Math.cos(t);
			return Math.sqrt(p * p + q * q);
		}
	}

	/**
	 * @param semiMajor
	 *            major semiaxis
	 * @param semiMinor
	 *            minor semiaxis
	 * @return ellipse circumference using power series
	 */
	public static double getEllipseCircumference(double semiMajor,
			double semiMinor) {
		double k = semiMinor / semiMajor;
		// Gauss-KummerSeries doesn't converge fast so use Cayley in this case
		// http://help.geogebra.org/topic/inaccurate-circumference-of-ellipse
		// for eccentricity > 0.92, i.e. k < 0.392
		if (k < 0.392) {

			double q = Math.log(4 / k) - 0.5;
			k = k * k;
			double pVal = 0.5 * k;
			double sum = 1 + pVal * q;
			double n = 1;
			double r = 0.5;
			double s = 0.5;
			double oldSum = sum + 1;

			// stop when there's no change, or after 20
			while (!MyDouble.exactEqual(sum, oldSum) && n < 40) {
				n += 2;

				// eg (1^2*3^2*5)/(2^2*4^2*6)
				pVal = pVal * r;
				r = n / (n + 1);
				pVal = pVal * k * r;

				// eg ln(k/4) - (2)/(1*2) - (2)/(3*4) - (1)/(5*6)
				q = q - s;
				s = 1 / (n * n + n);
				q = q - s;

				oldSum = sum;
				sum = sum + pVal * q;
			}

			// Log.debug("Cayley = " + 4 * a * sum + " after " + i);
			return 4 * semiMajor * sum;
		}

		double h = (semiMajor - semiMinor) / (semiMajor + semiMinor);
		double h2 = h * h;
		double hn = h2;

		// http://mathworld.wolfram.com/Gauss-KummerSeries.html
		// http://oeis.org/A056981
		// http://oeis.org/A056982
		double[] a056981 = { 1, 1, 1, 1, 25, 49, 441, 1089, 184041, 511225,
				5909761, 17631601, 863948449, 2704312009L, 34493775625L,
				111759833025L, 93990019574025L, 312541206957225L,
				4201942893536025L, 14258670483605625L, 780804795682244025L };
		double[] a056982 = { 1, 4, 64, 256, 16384, 65536, 1048576, 4194304,
				1073741824, 4294967296L, 68719476736L, 274877906944L,
				17592186044416L, 70368744177664L, 1125899906842624L,
				4503599627370496L, 4611686018427387904L };

		double ret = 1;
		double lastAnswer;

		for (int i = 1; i < 17; i++) {
			lastAnswer = ret;
			ret += a056981[i] / a056982[i] * hn;

			hn = hn * h2;

			// stop early if answer has converged to 15 sig figs
			if (MyDouble.exactEqual(lastAnswer, ret)) {
				break;
			}

		}

		ret *= (semiMajor + semiMinor) * Math.PI;
		// Log.debug("GK = " + ret);
		return ret;
	}
}
