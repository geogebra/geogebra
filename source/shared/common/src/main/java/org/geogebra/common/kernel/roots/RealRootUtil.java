/**
 *   Class RealRootUtil
 *   Contains util methods for finding a real root
 *  
 *   @author Markus Hohenwarter
 *   @date   25 Sept 2006
 *   
 *   GeoGebra - Dynamic Mathematics for Everyone
 *   http://www.geogebra.org
 *   This file is part of GeoGebra.
 *   
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   
 */

package org.geogebra.common.kernel.roots;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.arithmetic.MyDouble;

/**
 * Utility class for root finding
 *
 */
public class RealRootUtil {

	private static final int ITER_MAX = 100; // maximum number of iterations allowed

	/**
	 * Returns an interval within [a, b] where f(x) is defined.
	 * 
	 * @param f
	 *            function
	 * @param a
	 *            interval min
	 * @param b
	 *            interval max
	 * @return [defined interval min, defined interval max]
	 * 
	 * @see #getDefinitionBorder(UnivariateFunction, double, double)
	 */
	public static double[] getDefinedInterval(UnivariateFunction f, double a,
			double b) {
		double[] bounds = new double[2];

		// calculate the function value at the estimate of the higher bound to x
		double fa = f.value(a);
		double fb = f.value(b);
		boolean faNaN = Double.isNaN(fa) || Double.isInfinite(fa);
		boolean fbNaN = Double.isNaN(fb) || Double.isInfinite(fb);

		if (faNaN || fbNaN) {
			// handle undefined borders
			if (faNaN && fbNaN) {
				// desperate mode: try if midpoint is defined
				double m = (a + b) * 0.5;
				double fm = f.value(m);
				if (Double.isNaN(fm)) {
					// bad luck: could not find an interval
					bounds[0] = Double.NaN;
					bounds[1] = Double.NaN;
				} else {
					// lucky you! the midpoint is defined so let's try to
					// find an interval around it
					bounds[0] = getDefinitionBorder(f, a, m);
					bounds[1] = getDefinitionBorder(f, m, b);
				}
			} else if (faNaN) {
				bounds[0] = getDefinitionBorder(f, a, b);
				bounds[1] = b;
			} else {
				bounds[0] = a;
				bounds[1] = getDefinitionBorder(f, a, b);
			}
		} else {
			// both borders are defined
			bounds[0] = a;
			bounds[1] = b;
		}

		return bounds;
	}

	/**
	 * Returns x0 where f(x) changes from defined to undefined in [a, b]. If
	 * f(a) is defined and f(b) is undefined, f(x) is defined on [a, x0] and
	 * undefined on (x0, b]. If f(a) is undefined and f(b) is defined, f(x) is
	 * (likely to be) undefined on [a, x0) and defined on [x0, b]. If both f(a)
	 * and f(b) are defined resp. undefined Double.NaN is returned.
	 * 
	 * @param f
	 *            function
	 * @param a
	 *            interval min
	 * @param b
	 *            interval max
	 * @return border between defined an undefined
	 */
	static double getDefinitionBorder(UnivariateFunction f, double a,
			double b) {
		double left = a, right = b;
		boolean leftDef = false, rightDef;

		int iter = 0;
		while (iter < ITER_MAX && !MyDouble.exactEqual(left, right)) {
			double fleft = f.value(left);
			double fright = f.value(right);
			leftDef = !(Double.isNaN(fleft) || Double.isInfinite(fleft));
			rightDef = !(Double.isNaN(fright) || Double.isInfinite(fright));

			// both borders are defined or undefined => failed
			if (leftDef == rightDef) {
				return Double.NaN;
			}
			// make next step using midpoint of interval
			iter++;
			double m = (left + right) * 0.5;
			double fm = f.value(m);
			boolean mDef = !(Double.isNaN(fm) || Double.isInfinite(fm));
			// set next interval by preserving the definition change
			if (mDef == leftDef) {
				left = m;
			} else { // mDef == rightDef
				right = m;
			}
		}

		// return last defined border
		if (leftDef) {
			return left;
		}
		return right;
	}

	/**
	 * updates the interval within [a, b] where f(x) is defined.
	 * 
	 * @param f
	 *            function
	 * @param a
	 *            min
	 * @param b
	 *            max
	 * @param interval
	 *            old interval
	 * 
	 * 
	 */
	public static void updateDefinedIntervalIntersecting(UnivariateFunction f,
			double a, double b, double[] interval) {

		double[] interval2 = getDefinedInterval(f, a, b);

		if (interval[0] < interval2[0]) {
			interval[0] = interval2[0];
		}

		if (interval[1] > interval2[1]) {
			interval[1] = interval2[1];
		}

	}

}
