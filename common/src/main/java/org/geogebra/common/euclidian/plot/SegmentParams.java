package org.geogebra.common.euclidian.plot;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.euclidian.EuclidianView;

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


	public SegmentParams(double tMin, double[] divisors, EuclidianView view) {
		this.tMin = tMin;
		this.divisors = divisors;
		this.view = view;
		dyad = 1;
		depth = 0;
		this.t = tMin;
		this.left = tMin;
//		this.diff = view.getOnScreenDiff(evalLeft, evalRight);
//		this.prevDiff = view.getOnScreenDiff(evalLeft, eval);
		countDiffZeros = 0;
	}


	public boolean hasMaxDepthNotReached() {
		return depth < MAX_DEFINED_BISECTIONS;
	}

	public boolean isDiffZerosLimitNotReached() {
		return countDiffZeros < MAX_ZERO_COUNT;
	}

	public boolean isStepTooBig(double maxParamStep) {
		return currentDivisor() > maxParamStep;
	}

	protected double currentDivisor() {
		return divisors[depth];
	}

	public void progress() {
		dyad = 2 * dyad - 1;
		depth++;
		updateT();
	}

	public void updateT() {
		// t=tMin+(tMax-tMin)*(dyad/2^depth)
		t = tMin + dyad * currentDivisor();
	}

	public void updateDiff(double[] evalLeft, double[] evalRight) {
		diff = view.getOnScreenDiff(evalLeft, evalRight);
	}

	public void updatePreviousDiff() {
		prevDiff = Cloner.clone(diff);
	}

	public void updateFromStack(CurvePlotterStackItem item) {
		depth = item.depth + 1; // pop stack and go to right
		dyad = item.dyadic * 2;
		updatePreviousDiff();
		updateT();
	}
}
