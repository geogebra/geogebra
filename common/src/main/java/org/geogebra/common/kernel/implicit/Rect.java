package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.matrix.Coords;

class Rect {
	/**
	 * 
	 */
	// private final GeoImplicitCurve geoImplicitCurve;
	/**
	 * {top, right, bottom, left}
	 */
	final double[] evals = new double[4];
	int x;
	int y;
	int shares;
	int status;
	double fx;
	double fy;
	boolean singular;
	Coords coords = new Coords(3);

	public Rect(int x, int y, double fx, double fy, boolean singular) {
		this.x = x;
		this.y = y;
		this.fx = fx;
		this.fy = fy;
		this.singular = singular;
		this.shares = 0;
	}

	public Rect[] split(GeoImplicitCurve geoImplicitCurve, int factor) {
		double fx2 = fx * 0.5;
		double fy2 = fy * 0.5;
		double x1 = this.coords.val[0];
		double y1 = this.coords.val[1];

		Rect[] rect = new Rect[4];
		for (int i = 0; i < 4; i++) {
			rect[i] = new Rect(x, y, fx2, fy2, singular);
			rect[i].coords.set(x1, y1, 0.0);
			rect[i].evals[i] = this.evals[i];
			rect[i].shares = this.shares & GeoImplicitCurve.MASK[i];
		}

		rect[1].coords.val[0] += fx2;
		rect[2].coords.val[0] += fx2;
		rect[2].coords.val[1] += fy2;
		rect[3].coords.val[1] += fy2;
		rect[1].evals[0] = geoImplicitCurve.evaluate(rect[1].coords.val,
				factor);
		rect[2].evals[0] = geoImplicitCurve.evaluate(rect[2].coords.val,
				factor);
		rect[2].evals[1] = geoImplicitCurve.evaluate(x1 + fx, y1 + fy2, factor);
		rect[2].evals[3] = geoImplicitCurve.evaluate(x1 + fx2, y1 + fy, factor);
		rect[3].evals[0] = geoImplicitCurve.evaluate(rect[3].coords.val,
				factor);
		rect[3].evals[1] = rect[0].evals[2] = rect[1].evals[3] = rect[2].evals[0];
		rect[0].evals[1] = rect[1].evals[0];
		rect[0].evals[3] = rect[3].evals[0];
		rect[1].evals[2] = rect[2].evals[1];
		rect[3].evals[2] = rect[2].evals[3];
		return rect;
	}

	public double x1() {
		return this.coords.val[0];
	}

	public double x2() {
		return this.coords.val[0] + fx;
	}

	public double y1() {
		return this.coords.val[1];
	}

	public double y2() {
		return this.coords.val[1] + fy;
	}
}