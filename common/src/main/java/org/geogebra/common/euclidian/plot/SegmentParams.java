package org.geogebra.common.euclidian.plot;

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


	public SegmentParams(double tMin, double tMax, double[] divisors, double[] diff,
			double[] prevDiff) {
		this.tMin = tMin;
		this.tMax = tMax;
		this.divisors = divisors;
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
		return divisors[depth] > maxParamStep;
	}
}
