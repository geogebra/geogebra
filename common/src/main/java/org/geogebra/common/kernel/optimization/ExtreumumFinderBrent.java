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
