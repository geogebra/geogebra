package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Convention used in defining rectangle array properties:
 * 
 * 0----------0------------1 | | | | 3 1 | | | | 3----------2------------2
 * 
 * @author GSoCImplicitCurve2015
 */
class Rect {
	private static final int[] MASK = { 0x9, 0x3, 0x6, 0xC };
	/**
	 * {top, right, bottom, left}
	 */
	final double[] evals = new double[4];
	/**
	 * Number of vertices where function evaluate to zero
	 */
	int zero;
	/**
	 * Number of vertices where function value is positive
	 */
	int pos;
	/**
	 * Number of vertices where function value is negative
	 */
	int neg;
	/**
	 * x-index
	 */
	int x;
	/**
	 * y-index
	 */
	int y;
	/**
	 * edges shared by the parent
	 */
	int shares;
	/**
	 * 
	 */
	int status;
	/**
	 * width rectangle
	 */
	double fx;
	/**
	 * height of rectangle
	 */
	double fy;
	/**
	 * top left coordinates of rectangle
	 */
	Coords coords = new Coords(3);
	/**
	 * four children of rectangle
	 */
	Rect[] children;

	/**
	 * 
	 */
	public Rect() {
	}

	/**
	 * 
	 * @param x
	 *            x-index
	 * @param y
	 *            y-index
	 * @param fx
	 *            width of rectangle
	 * @param fy
	 *            height of rectangle
	 * @param status
	 *            status (bit packed array for the rectangle)
	 */
	public void set(int x, int y, double fx, double fy, int status) {
		this.x = x;
		this.y = y;
		this.fx = fx;
		this.fy = fy;
		this.status = (status & Consts.SINGULAR);
		this.shares = 0;
	}

	/**
	 * set this rectangle coordinate and values from other rectangle
	 * 
	 * @param r
	 *            other rectangle
	 */
	public void set(Rect r) {
		this.set(r.x, r.y, r.fx, r.fy, r.status);
		this.shares = r.shares;
		this.coords.set(r.coords);
		for (int i = 0; i < 4; i++) {
			this.evals[i] = r.evals[i];
		}
	}

	/**
	 * 
	 * @param geoImplicitCurve
	 *            function
	 * @param factor
	 *            factor
	 * @return four rectangles
	 */
	public Rect[] split(GeoImplicitCurve geoImplicitCurve, int factor) {
		if (this.children == null) {
			this.children = new Rect[4];
			for (int i = 0; i < 4; i++) {
				this.children[i] = new Rect();
			}
		}
		Rect[] rect = this.children;
		double fx2 = fx * 0.5;
		double fy2 = fy * 0.5;
		double x1 = this.coords.val[0];
		double y1 = this.coords.val[1];
		for (int i = 0; i < 4; i++) {
			rect[i].set(x, y, fx2, fy2, 0);
			rect[i].coords.set(x1, y1, 0.0);
			rect[i].evals[i] = this.evals[i];
			rect[i].shares = this.shares & MASK[i];
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

	/**
	 * Populate status from vertex information
	 */
	public void buildStatus() {
		this.pos = 0;
		this.neg = 0;
		this.zero = 0;
		this.status |= Consts.VALID;
		for (int i = 0; i < 4; i++) {
			if (Double.isNaN(this.evals[i])
					|| Double.isInfinite(this.evals[i])) {
				this.status ^= Consts.VALID;
				this.status |= Consts.EMPTY;
				return;
			}
			if (this.evals[i] < 0) {
				this.neg++;
			} else if (this.evals[i] > 0) {
				this.pos++;
			} else {
				this.zero++;
			}
		}
		if (((zero + 1) | pos | neg) >= 4) {
			this.status |= Consts.EMPTY;
		} else {
			this.status &= ~Consts.EMPTY;
		}
	}

	/**
	 * Test if the particular flag bit is set
	 * 
	 * @param bit
	 *            flag bit
	 * @return true if particular flag bit it set
	 */
	public boolean is(int bit) {
		return (this.status & bit) == bit;
	}

	/**
	 * @return top left x coordinate
	 */
	public double x1() {
		return this.coords.val[0];
	}

	/**
	 * @return bottom right x coordinate
	 */
	public double x2() {
		return this.coords.val[0] + fx;
	}

	/**
	 * @return top left y coordinate
	 */
	public double y1() {
		return this.coords.val[1];
	}

	/**
	 * @return bottom right y coordinate
	 */
	public double y2() {
		return this.coords.val[1] + fy;
	}
}