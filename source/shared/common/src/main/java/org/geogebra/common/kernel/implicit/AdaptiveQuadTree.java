package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Adaptive QuadTree algorithm that refines plot and segment checking depth
 * (or even gives up) if the work load is high.
 * @author GSoCImplicitCurve-2015
 */
class AdaptiveQuadTree extends QuadTree {
	private static final int RES_COARSE = 8;
	private static final int MAX_SPLIT = 40;
	private final GeoImplicitCurve curve;
	private int plotDepth;
	private int segmentCheckDepth;
	private int sw;
	private int sh;
	private Rect[][] grid;
	private final Timer timer = Timer.newTimer();
	private static int fastDrawThreshold = 10;

	public static void setFastDrawThreshold(int threshold) {
		fastDrawThreshold = threshold;
	}

	private static class Timer {
		public long now;
		public long elapse;

		public static Timer newTimer() {
			return new Timer();
		}

		public void reset() {
			this.now = System.currentTimeMillis();
		}

		public void record() {
			this.elapse = System.currentTimeMillis() - now;
		}
	}

	public AdaptiveQuadTree(GeoImplicitCurve curve) {
		super();
		this.curve = curve;
	}

	@Override
	public void updatePath() {
		for (int factor = 0; factor < curve.factorLength(); ++factor) {
			try {
				curve.evaluateImplicitCurve(0, 0, factor);
			} catch (Throwable e) {
				continue;
			}
			this.sw = Math.min(MAX_SPLIT, (int) (w * scaleX / RES_COARSE));
			this.sh = Math.min(MAX_SPLIT, (int) (h * scaleY / RES_COARSE));
			if (sw == 0 || sh == 0) {
				return;
			}

			this.grid = new Rect[sh][sw];

			double frx = w / sw;
			double fry = h / sh;

			double[] vertices = new double[sw + 1];
			double[] xcoords = new double[sw + 1];
			double[] ycoords = new double[sh + 1];
			double cur, prev;

			for (int i = 0; i <= sw; i++) {
				xcoords[i] = x + i * frx;
			}

			for (int i = 0; i <= sh; i++) {
				ycoords[i] = y + i * fry;
			}

			for (int i = 0; i <= sw; i++) {
				vertices[i] = curve.evaluateImplicitCurve(xcoords[i], ycoords[0],
						factor);
			}

			// initialize grid configuration at the search depth
			int i, j;
			double dx, dy, fx, fy;
			// debug = true;
			timer.reset();
			for (i = 1; i <= sh; i++) {
				prev = curve.evaluateImplicitCurve(xcoords[0], ycoords[i],
						factor);
				fy = ycoords[i] - 0.5 * fry;
				for (j = 1; j <= sw; j++) {
					cur = curve.evaluateImplicitCurve(xcoords[j], ycoords[i],
							factor);
					Rect rect = new Rect(j - 1, i - 1, frx, fry, false);
					rect.coords.val[0] = xcoords[j - 1];
					rect.coords.val[1] = ycoords[i - 1];
					rect.evals[0] = vertices[j - 1];
					rect.evals[1] = vertices[j];
					rect.evals[2] = cur;
					rect.evals[3] = prev;
					rect.status = edgeConfig(rect);
					rect.shares = 0xff;
					fx = xcoords[j] - 0.5 * frx;
					dx = curve.derivativeX(fx, fy);
					dy = curve.derivativeY(fx, fy);
					dx = Math.abs(dx) + Math.abs(dy);
					if (DoubleUtil.isZero(dx, 0.001)) {
						rect.singular = true;
					}
					this.grid[i - 1][j - 1] = rect;
					vertices[j - 1] = prev;
					prev = cur;
				}
				vertices[sw] = prev;
			}

			refineOnHighWorkload();
			plotGid(factor);
			if (giveUpOnExtremeWorkload()) {
				return;
			}
		}
	}

	private void plotGid(int factor) {
		for (int i = 0; i < sh; i++) {
			for (int j = 0; j < sw; j++) {
				if (grid[i][j].status != QuadTreeEdgeConfig.EMPTY.flag()) {
					plot(grid[i][j], 0, factor);
				}
			}
		}
	}

	private void refineOnHighWorkload() {
		timer.record();

		if (timer.elapse <= fastDrawThreshold) {
			// Fast device optimize for UX
			plotDepth = 3;
			segmentCheckDepth = 2;
			setListThreshold(48);
		} else {
			// Slow device detected reduce parameters
			plotDepth = 2;
			segmentCheckDepth = 1;
			setListThreshold(24);
		}
	}

	private boolean giveUpOnExtremeWorkload() {
		timer.record();
		if (timer.elapse >= 500) {
			// I can't do anything more. I've been working for 500 ms
			// Therefore I am tired
			return true;
		} else if (timer.elapse >= 300) {
			// I am exhausted, reducing load!
			plotDepth -= 1;
			segmentCheckDepth -= 1;
		}
		return false;
	}

	public void createTree(Rect r, int depth, int factor) {
		Rect[] n = r.split(curve, factor);
		plot(n[0], depth, factor);
		plot(n[1], depth, factor);
		plot(n[2], depth, factor);
		plot(n[3], depth, factor);
	}

	public void plot(Rect r, int depth, int factor) {
		if (depth < segmentCheckDepth) {
			createTree(r, depth + 1, factor);
			return;
		}

		MarchingConfigProvider configProvider = new QuadTreeRectConfigProvider(curve, factor);
		int e = edgeConfig(r);
		if (grid[r.y][r.x].singular || e != QuadTreeEdgeConfig.EMPTY.flag()) {
			if (depth >= plotDepth) {
				if (segments().add(r, configProvider) == QuadTreeEdgeConfig.T0101.flag()) {
					createTree(r, depth + 1, factor);
					return;
				}
				if (r.x != 0 && (e & r.shares & 0x1) != 0) {
					nonempty(r.y, r.x - 1);

				}
				if (r.x + 1 != sw && (e & r.shares & 0x4) != 0) {
					nonempty(r.y, r.x + 1);
				}
				if (r.y != 0 && (e & r.shares & 0x8) != 0) {
					nonempty(r.y - 1, r.x);
				}
				if (r.y + 1 != sh && (e & r.shares & 0x2) != 0) {
					nonempty(r.y + 1, r.x);
				}
			} else {
				createTree(r, depth + 1, factor);
			}
		}
	}

	private void nonempty(int ry, int rx) {
		if (grid[ry][rx].status == QuadTreeEdgeConfig.EMPTY.flag()) {
			grid[ry][rx].status = 1;
		}
	}

	@Override
	public void polishPointOnPath(GeoPointND pt) {
		pt.updateCoords();
		double x1 = onScreen(pt.getInhomX(), this.x, this.x + this.w);
		double y1 = onScreen(pt.getInhomY(), this.y, this.y + this.h);
		double d1 = curve.evaluateImplicitCurve(x1, y1);
		if (DoubleUtil.isZero(d1)) {
			pt.setCoords(new Coords(x1, y1, 1.0), false);
			return;
		}

		// determine the direction of the gradient vector
		double derivativeX = curve.getDerivativeX().evaluate(x1, y1);
		double derivativeY = curve.getDerivativeY().evaluate(x1, y1);
		double derivativeLength = Math.hypot(derivativeX, derivativeY);

		// take one big step in the direction of the gradient vector
		double mv = Math.max(w, h) / MAX_SPLIT;
		double x2 = x1 - mv * (derivativeX / derivativeLength) * Math.signum(d1);
		double y2 = y1 - mv * (derivativeY / derivativeLength) * Math.signum(d1);
		double d2 = curve.evaluateImplicitCurve(x2, y2);

		// if we stepped over the curve...
		if (d2 * d1 <= 0.0) {
			double mx = x1;
			double my = y1;

			// binary search the closest point to the curve with a maximum depth of 64
			for (int count = 0; count < 64 && !DoubleUtil.isZero(d2); count++) {
				mx = 0.5 * (x1 + x2);
				my = 0.5 * (y1 + y2);
				double md = curve.evaluateImplicitCurve(mx, my);
				if (DoubleUtil.isZero(md)) {
					pt.setCoords(new Coords(mx, my, 1.0), false);
					return;
				}
				if (d1 * md <= 0.0) {
					d2 = md;
					x2 = mx;
					y2 = my;
				} else {
					d1 = md;
					x1 = mx;
					y1 = my;
				}
			}
			// we didn't hit exact 0, let's use the closest we have
			pt.setCoords(new Coords(mx, my, 1.0), false);
		}
	}

	private double onScreen(double v, double mn, double mx) {
		if (Double.isNaN(v) || Double.isInfinite(v) || v < mn || v > mx) {
			return (mn + mx) * 0.5;
		}
		return v;
	}
}
