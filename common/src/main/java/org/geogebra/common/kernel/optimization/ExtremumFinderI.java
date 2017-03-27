package org.geogebra.common.kernel.optimization;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface ExtremumFinderI {

	double findMinimum(double left, double right, UnivariateFunction distFun,
			double minPrecision);

	double findMaximum(double intervalMin, double intervalMax,
			UnivariateFunction distFun, double minPrecision);

}
