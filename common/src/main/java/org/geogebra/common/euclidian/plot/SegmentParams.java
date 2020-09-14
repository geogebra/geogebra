package org.geogebra.common.euclidian.plot;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.euclidian.EuclidianView;

public class SegmentParams {
	public static final int MAX_DEFINED_BISECTIONS = 16;

	// maximum number of times to loop when xDiff, yDiff are both zero
	// eg Curve[0sin(t), 0t, t, 0, 6]
	private static final int MAX_ZERO_COUNT = 1000;
	int dyad;
	int depth = 0;
	double[] diff;
	double[] prevDiff;
	int countDiffZeros = 0;
	double t;
	double left;
	private final double tMin;
	private double tMax;
	double[] divisors;
	private EuclidianView view;


	public SegmentParams(double tMin, double tMax, double[] divisors,
			EuclidianView view, double[] diff,
			double[] prevDiff) {
		this.tMin = tMin;
		this.tMax = tMax;
		this.divisors = divisors;
		this.view = view;
		dyad = 1;
		depth = 0;
		this.t = tMin;
		this.left = tMin;
		this.diff = diff;
		this.prevDiff = prevDiff;
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

	public void update() {
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
}
