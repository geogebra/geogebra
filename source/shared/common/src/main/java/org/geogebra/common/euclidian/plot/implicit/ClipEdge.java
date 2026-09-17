/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.util.MyMath.clamp;

import java.util.function.ToDoubleFunction;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;

/**
 * Enumerates the four axis-aligned edges of a rectangular viewport and provides
 * utilities to parameterize, traverse, and intersect them when clipping
 * parametric/implicit contours.
 *
 * <p><strong>Perimeter parameterization.</strong> The rectangle boundary is
 * parameterized clockwise starting at the top-left corner with {@code s in [0,4]}:
 * {@code TOP:[0,1]}, {@code RIGHT:[1,2]}, {@code BOTTOM:[2,3]}, {@code LEFT:[3,4]}.</p>
 * @apiNote Treat {@code s = 4.0} as equivalent to {@code s = 0.0} for cyclic ordering.
 */
public enum ClipEdge {
	TOP(0, -1, 0.0, r -> Double.NaN, ClipRect::getYmax, ClipRect::getXmax, ClipRect::getYmax) {
		@Override
		double edgeParam(MyPoint p, ClipRect r) {
			return clamp01((p.x - r.getXmin()) / (r.getXmax() - r.getXmin()));
		}
	},

	RIGHT(-1, 0, 1.0, ClipRect::getXmax, r -> Double.NaN, ClipRect::getXmax, ClipRect::getYmin) {
		@Override
		double edgeParam(MyPoint p, ClipRect r) {
			return clamp01((r.getYmax() - p.y) / (r.getYmax() - r.getYmin()));
		}
	},

	BOTTOM(0, 1, 2.0, r -> Double.NaN, ClipRect::getYmin, ClipRect::getXmin, ClipRect::getYmin) {
		@Override
		double edgeParam(MyPoint p, ClipRect r) {
			return clamp01((p.x - r.getXmin()) / (r.getXmax() - r.getXmin()));
		}
	},

	LEFT(1, 0, 3.0, ClipRect::getXmin, r -> Double.NaN, ClipRect::getXmin, ClipRect::getYmax) {
		@Override
		double edgeParam(MyPoint p, ClipRect r) {
			return clamp01((r.getYmax() - p.y) / (r.getYmax() - r.getYmin()));
		}
	};
	private final int nx, ny;
	private final double sBase;
	private final ToDoubleFunction<ClipRect> xConstFn;
	private final ToDoubleFunction<ClipRect> yConstFn;
	private final ToDoubleFunction<ClipRect> clockwiseCornerXFn;
	private final ToDoubleFunction<ClipRect> clockwiseCornerYFn;

	/**
	 * Creates an edge with its inward normal, perimeter base, and accessors for
	 * constant coordinates and its clockwise corner.
	 * @param nx x-component of the inward unit normal ({@code -1,0,+1})
	 * @param ny y-component of the inward unit normal ({@code -1,0,+1})
	 * @param sBase base offset of this edge on the clockwise perimeter ({@code TOP=0, RIGHT=1, BOTTOM=2, LEFT=3})
	 * @param xConstFn supplier for the edge's constant x (use {@code NaN} for horizontal edges)
	 * @param yConstFn supplier for the edge's constant y (use {@code NaN} for vertical edges)
	 * @param clockwiseCornerXFn supplier for the clockwise corner's x
	 * @param clockwiseCornerYFn supplier for the clockwise corner's y
	 * @implNote The perimeter coordinate increases clockwise; edges whose local
	 * direction opposes the CW walk are accounted for by flipping their
	 * local parameter internally.
	 */
	ClipEdge(
			int nx,
			int ny,
			double sBase,
			ToDoubleFunction<ClipRect> xConstFn,
			ToDoubleFunction<ClipRect> yConstFn,
			ToDoubleFunction<ClipRect> clockwiseCornerXFn,
			ToDoubleFunction<ClipRect> clockwiseCornerYFn) {
		this.nx = nx;
		this.ny = ny;
		this.sBase = sBase;
		this.xConstFn = xConstFn;
		this.yConstFn = yConstFn;
		this.clockwiseCornerXFn = clockwiseCornerXFn;
		this.clockwiseCornerYFn = clockwiseCornerYFn;
	}

	/**
	 * Returns the signed alignment of a direction with this edge's inward normal.
	 * @param dx x-component of the direction
	 * @param dy y-component of the direction
	 * @return positive if the direction points into the viewport across this edge,
	 * negative if it points outwards, near zero for tangency
	 */
	public final double dotInward(double dx, double dy) {
		return dx * nx + dy * ny;
	}

	/**
	 * Returns the edge-local parameter of a point lying on this edge.
	 *
	 * <p>For horizontal edges, {@code t} increases left to right.
	 * For vertical edges, {@code t} increases bottom to top.</p>
	 * @param p world-space point on the edge (after snapping)
	 * @param r clip rectangle
	 * @return normalized parameter {@code t in [0,1]}
	 */
	double edgeParam(MyPoint p, ClipRect r) {
		if (isHorizontal()) {
			return clamp01((p.x - r.getXmin()) / (r.getXmax() - r.getXmin()));
		}
		return clamp01((p.y - r.getYmin()) / (r.getYmax() - r.getYmin()));
	}

	private boolean isHorizontal() {
		return ny != 0;
	}

	/**
	 * Maps an edge-local parameter to the global clockwise perimeter coordinate.
	 * @param t edge-local parameter {@code [0,1]}
	 * @return perimeter position {@code s in [0,4]}
	 * @apiNote Use for ordering hits around the viewport boundary. Treat {@code s=4}
	 * as {@code s=0} for cyclic comparisons.
	 */
	private double perimeterParamCW(double t) {
		return sBase + (isForward() ? t : 1.0 - t);
	}

	private boolean isForward() {
		return nx + ny < 0;
	}

	/**
	 * Returns the rectangle corner reached when continuing clockwise from this edge.
	 * @param r clip rectangle
	 * @return the next clockwise corner on the rectangle boundary
	 */
	MyPoint cwCorner(ClipRect r) {
		return new MyPoint(clockwiseCornerXFn.applyAsDouble(r), clockwiseCornerYFn.applyAsDouble(r));
	}

	/**
	 * Returns the next edge in clockwise order.
	 * @return the clockwise successor of this edge
	 */
	final ClipEdge nextCW() {
		return values()[(ordinal() + 1) & 3];
	}

	/**
	 * Intersects this edge with a contour segment {@code A -> B}.
	 * @param r clip rectangle
	 * @param A segment start (world)
	 * @param B segment end (world)
	 * @param segIndex index of {@code A -> B} within the owning contour
	 * @param eps numerical tolerances
	 * @return an {@link EdgeHit} describing the intersection (including the snapped
	 * point, edge-local parameter, perimeter coordinate, and segment parameter),
	 * or {@code null} if no intersection occurs
	 * @apiNote The returned point is snapped to the rectangle and may coincide with a corner.
	 */
	final EdgeHit intersectSegment(ClipRect r, MyPoint A, MyPoint B, int segIndex, ClipEpsilon eps) {
		if (isHorizontal()) {
			return intersectHorizontal(yConstFn.applyAsDouble(r), this, r, A, B, segIndex, eps);
		}
		return intersectVertical(xConstFn.applyAsDouble(r), this, r, A, B, segIndex, eps);
	}

	private static EdgeHit intersectHorizontal(
			double y0,
			ClipEdge edge,
			ClipRect r,
			MyPoint start,
			MyPoint end,
			int segIndex,
			ClipEpsilon eps) {

		double tSeg = getTSegment(y0, start.y, end.y, eps);
		if (Double.isNaN(tSeg)) {
			return null;
		}

		double x = lerp(start.x, end.x, tSeg);
		if (x < r.getXmin() - eps.world() || x > r.getXmax() + eps.world()) {
			return null;
		}
		x = clamp(x, r.getXmin(), r.getXmax());

		MyPoint P = new MyPoint(x, y0, SegmentType.LINE_TO);
		return newEdgeHit(edge, P, r, segIndex, tSeg, eps);
	}

	private static EdgeHit intersectVertical(
			double x0,
			ClipEdge edge,
			ClipRect r,
			MyPoint start,
			MyPoint end,
			int segIndex,
			ClipEpsilon eps) {

		double tSeg = getTSegment(x0, start.x, end.x, eps);
		if (Double.isNaN(tSeg)) {
			return null;
		}

		double y = lerp(start.y, end.y, tSeg);
		if (y < r.getYmin() - eps.world() || y > r.getYmax() + eps.world()) {
			return null;
		}
		y = clamp(y, r.getYmin(), r.getYmax());

		MyPoint P = new MyPoint(x0, y, SegmentType.LINE_TO);
		return newEdgeHit(edge, P, r, segIndex, tSeg, eps);
	}

	private static double getTSegment(double t0, double tA, double tB, ClipEpsilon eps) {
		double dA = tA - t0;
		double dB = tB - t0;

		if ((dA > eps.world() && dB > eps.world()) || (dA < -eps.world() && dB < -eps.world())) {
			return Double.NaN;
		}

		double denom = tB - tA;
		double tSeg;
		if (Math.abs(denom) <= eps.denom()) {
			if (Math.abs(dA) <= eps.world() && Math.abs(dB) <= eps.world()) {
				return Double.NaN;
			}

			if (Math.abs(dA) <= eps.world()) {
				tSeg = 0.0;
			} else if (Math.abs(dB) <= eps.world()) {
				tSeg = 1.0;
			} else {
				return Double.NaN;
			}
		} else {
			tSeg = (t0 - tA) / denom;
		}
		if (tSeg < -eps.param() || tSeg > 1.0 + eps.param()) {
			return Double.NaN;
		}
		return clamp01(tSeg);
	}

	private static EdgeHit newEdgeHit(
			ClipEdge edge, MyPoint point, ClipRect r, int segIndex, double tSeg, ClipEpsilon eps) {
		snapToCorners(point, r, eps.corner());
		double t = edge.edgeParam(point, r);
		double s = edge.perimeterParamCW(t);
		return new EdgeHit(edge, clamp01(t), segIndex, clamp01(tSeg), s, point);
	}

	private static void snapToCorners(MyPoint w, ClipRect r, double eps) {
		final double xmin = r.getXmin(), xmax = r.getXmax();
		final double ymin = r.getYmin(), ymax = r.getYmax();

		final double sx = near(w.x, xmin, eps) ? xmin : near(w.x, xmax, eps) ? xmax : w.x;

		final double sy = near(w.y, ymin, eps) ? ymin : near(w.y, ymax, eps) ? ymax : w.y;
		if (sx != w.x && sy != w.y) {
			w.x = sx;
			w.y = sy;
		}
	}

	private static boolean near(double a, double b, double eps) {
		return Math.abs(a - b) <= eps;
	}

	static double clamp01(double v) {
		return v < 0 ? 0 : (v > 1 ? 1 : v);
	}

	static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	/**
	 * Returns the midpoint of this edge on the given rectangle.
	 * @param clipRect clip rectangle
	 * @return the edge midpoint in world coordinates
	 */
	public MyPoint middlePoint(ClipRect clipRect) {
		return isHorizontal()
				? new MyPoint(
						(clipRect.getXmin() + clipRect.getXmax()) * 0.5,
						yConstFn.applyAsDouble(clipRect),
						SegmentType.LINE_TO)
				: new MyPoint(
						xConstFn.applyAsDouble(clipRect),
						(clipRect.getYmin() + clipRect.getYmax()) * 0.5,
						SegmentType.LINE_TO);
	}
}
