package org.geogebra.common.euclidian.plot;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.euclidian.EuclidianView;

/**
 * Class to calculate the parameters of the segment
 */
public class SegmentParams {
	public static final int MAX_DEFINED_BISECTIONS = 16;

	// maximum number of times to loop when xDiff, yDiff are both zero
	// eg Curve[0sin(t), 0t, t, 0, 6]
	private static final int MAX_ZERO_COUNT = 1000;
	int dyad;
	int depth;
	double[] diff;
	double[] prevDiff;
	int countDiffZeros;
	double t;
	double left;
	private final double tMin;
	double[] divisors;
	private final EuclidianView view;

	/**
	 *
	 * @param tMin minimum of the segment parameter.
	 * @param divisors splitters of the segment.
	 * @param view {@link EuclidianView}
	 */
	public SegmentParams(double tMin, double[] divisors, EuclidianView view) {
		this.tMin = tMin;
		this.divisors = divisors;
		this.view = view;
		dyad = 1;
		depth = 0;
		this.t = tMin;
		this.left = tMin;
		countDiffZeros = 0;
	}

	/**
	 *
	 * @return true if max depth of the bisections is not reached yet.
	 */
	public boolean hasMaxDepthNotReached() {
		return depth < MAX_DEFINED_BISECTIONS;
	}

	/**
	 *
	 * @return true if max number of zero differences is not reached yet.
	 */
	public boolean isDiffZerosLimitNotReached() {
		return countDiffZeros < MAX_ZERO_COUNT;
	}

	/**
	 *
	 * @param maxParamStep max step
	 * @return true if current divisor is too big yet.
	 */
	public boolean isStepTooBig(double maxParamStep) {
		return currentDivisor() > maxParamStep;
	}

	/**
	 *
	 * @return current divisor
	 */
	protected double currentDivisor() {
		return divisors[depth];
	}

	/**
	 * Move parameters forward.
	 */
	public void progress() {
		dyad = 2 * dyad - 1;
		depth++;
		updateT();
	}

	/**
	 * updates the main segment parameter t.
	 */
	public void updateT() {
		t = tMin + dyad * currentDivisor();
	}

	/**
	 * Updates the difference between two algo steps.
	 * @param evalLeft left value.
	 * @param evalRight right value.
	 */
	public void updateDiff(double[] evalLeft, double[] evalRight) {
		diff = view.getOnScreenDiff(evalLeft, evalRight);
	}

	/**
	 * Stores current difference as previous.
	 */
	public void updatePreviousDiff() {
		prevDiff = Cloner.clone(diff);
	}

	/**
	 * Restores previously stored parameters from a stack item.
	 *
	 * @param item to restore from.
	 */
	public void restoreFromStack(CurvePlotterStackItem item) {
		depth = item.depth + 1; // pop stack and go to right
		dyad = item.dyadic * 2;
		updatePreviousDiff();
		updateT();
	}
}
