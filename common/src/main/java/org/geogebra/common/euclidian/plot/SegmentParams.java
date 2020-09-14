package org.geogebra.common.euclidian.plot;

public class SegmentParams {
	int dyad;
	int depth = 0;
	double[] diff;
	double[] prevDiff;
	int countDiffZeros = 0;
	double t;
	double left;

	public SegmentParams(double t, double left, double[] diff, double[] prevDiff) {
		dyad = 1;
		depth = 0;
		this.t = t;
		this.left = left;
		this.diff = diff;
		this.prevDiff = prevDiff;
	}
}
