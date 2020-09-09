package org.geogebra.common.euclidian.plot;

public class CurvePlotterStackItem {
	int dyadic;
	int depth;
	double[] pos;
	boolean onScreen;

	public void set(int dyadic, int depth, boolean onScreen, double[] pos) {
		this.dyadic = dyadic;
		this.depth = depth;
		this.onScreen = onScreen;
		this.pos = pos;
	}
}
