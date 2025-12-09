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

package org.geogebra.common.kernel.optimization;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import org.geogebra.common.kernel.Kernel;

/**
 * wrapper class for BrentOptimizer
 *
 */
public class ExtreumumFinderBrent implements ExtremumFinderI {

	final private static MaxEval MAX_EVALUATIONS = new MaxEval(100);

	private BrentOptimizer brentOptimizer;

	/**
	 * Default constructor.
	 */
	public ExtreumumFinderBrent() {
		this.brentOptimizer = new BrentOptimizer(Kernel.STANDARD_PRECISION,
				Kernel.MAX_PRECISION);

	}

	private double findMinOrMax(double left, double right,
			UnivariateFunction distFun, double minPrecision,
			GoalType goalType) {
		
		brentOptimizer.setRelativePrecision(minPrecision);
		
		UnivariateObjectiveFunction fun = new UnivariateObjectiveFunction(
				distFun);

		UnivariatePointValuePair solution = brentOptimizer.optimize(fun,
				MAX_EVALUATIONS, goalType, new SearchInterval(left, right));

		// if (GoalType.MINIMIZE.equals(goalType)) {
		// Log.error("value from old optimizer = "
		// + oldOptimizer.findMinimum(left, right, distFun,
		// minPrecision));
		// } else {
		// Log.error("value from old optimizer = " + oldOptimizer
		// .findMaximum(left, right, distFun, minPrecision));
		//
		// }
		// Log.error("value from new optimizer = " + solution.getPoint() + " "
		// + solution.getValue());
		
		double ret = solution.getPoint();
		double retVal = solution.getValue();

		if (Double.isNaN(ret)) {
			return Double.NaN;
		}

		double leftVal = distFun.value(left);
		double rightVal = distFun.value(right);

		// check end points
		if (GoalType.MINIMIZE.equals(goalType)) {

			if (leftVal < retVal) {
				return leftVal;
			}

			if (rightVal < retVal) {
				return rightVal;
			}

		} else /* MAXIMIZE */ {

			if (leftVal > retVal) {
				return leftVal;
			}

			if (rightVal > retVal) {
				return rightVal;
			}

		}

		return ret;
	}

	@Override
	public double findMinimum(double left, double right,
			UnivariateFunction distFun, double minPrecision) {
		return findMinOrMax(left, right, distFun, minPrecision,
				GoalType.MINIMIZE);
	}

	@Override
	public double findMaximum(double left, double right,
			UnivariateFunction distFun, double minPrecision) {
		return findMinOrMax(left, right, distFun, minPrecision,
				GoalType.MAXIMIZE);
	}

}
