package org.geogebra.common.kernel.optimization;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Numeric optimizer for single variable function
 *
 */
public interface ExtremumFinderI {

	/**
	 * @param intervalMin
	 *            interval min
	 * @param intervalMax
	 *            interval max
	 * @param distFun
	 *            function we want to minimize
	 * @param minPrecision
	 *            precision
	 * @return x
	 */
	double findMinimum(double intervalMin, double intervalMax,
			UnivariateFunction distFun,
			double minPrecision);

	/**
	 * @param intervalMin
	 *            interval min
	 * @param intervalMax
	 *            interval max
	 * @param distFun
	 *            function we want to maximize
	 * @param minPrecision
	 *            precision
	 * @return x
	 */
	double findMaximum(double intervalMin, double intervalMax,
			UnivariateFunction distFun, double minPrecision);

}
