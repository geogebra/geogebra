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

package org.geogebra.common.kernel.implicit;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.util.DoubleUtil;

/**
 * Container for a polyline-like contour consisting of a {@code start} point,
 * zero or more intermediate points, and an {@code end} point.
 * <p>
 * Supports reversing, concatenating with another chain, and emitting the
 * points into a {@link GeneralPathClippedForCurvePlotter} for rendering.
 * Used by marching/contouring to assemble open or closed paths.
 * </p>
 *
 * @apiNote Closedness is decided by comparing {@code start} and {@code end}
 *          with a fixed tolerance (see {@link #equal(MyPoint, MyPoint)}).
 */
public class PointList {
	MyPoint start;
	MyPoint end;
	LinkedList<MyPoint> pts = new LinkedList<>();

	/**
	 * Creates a new chain with the given endpoints and initializes segment types
	 * (start as a move, end as a line).
	 *
	 * @param start first point of the chain (emitted as a move)
	 * @param end   last point of the chain (emitted as a line)
	 */
	PointList(MyPoint start, MyPoint end) {
		this.start = start;
		this.end = end;
		setSegmentTypes();
	}

	private void setSegmentTypes() {
		this.start.setLineTo(false);
		this.end.setLineTo(true);
	}

	/**
	 * Reverses this chain in place: swaps {@code start}/{@code end}, updates
	 * their segment types, and reverses the order of intermediate points.
	 */
	void reverse() {
		MyPoint tmp = start;
		start = end;
		end = tmp;
		setSegmentTypes();
		Collections.reverse(pts);
	}

	/**
	 * Concatenates another chain to the end of this chain, mutating this instance.
	 * <p>
	 * If {@code pl == this}, the chain is closed by inserting a copy of the start
	 * point. Otherwise, the method appends {@code pl}'s points and reuses the
	 * longer internal list to minimize copying.
	 * </p>
	 *
	 * @param pl the chain to append (may be {@code this})
	 */
	void mergeTo(PointList pl) {
		this.pts.addLast(this.end);
		if (pl == this) {
			MyPoint startCopy = new MyPoint(this.start.x, this.start.y,
					SegmentType.LINE_TO);
			this.pts.addLast(startCopy);
			return;
		}
		pl.start.setLineTo(true);
		this.pts.addLast(pl.start);
		this.end = pl.end;
		int s1 = this.pts.size(), s2 = pl.pts.size();

		if (s2 == 0) {
			return;
		}

		if (s1 < s2) {
			ListIterator<MyPoint> itr = this.pts.listIterator(s1 - 1);
			while (itr.hasPrevious()) {
				pl.pts.addFirst(itr.previous());
			}
			this.pts = pl.pts;
		} else {
			ListIterator<MyPoint> itr = pl.pts.listIterator();
			while (itr.hasNext()) {
				this.pts.addLast(itr.next());
			}
		}
	}

	/**
	 * Prepends a point before the current {@code start}, moving the previous
	 * {@code start} into the intermediate list.
	 *
	 * @param p new first point
	 */
	void extendBack(MyPoint p) {
		p.setLineTo(false);
		this.start.setLineTo(true);
		this.pts.addFirst(start);
		this.start = p;
	}

	/**
	 * Appends a point after the current {@code end}, moving the previous
	 * {@code end} into the intermediate list.
	 *
	 * @param p new last point
	 */
	void extendFront(MyPoint p) {
		p.setLineTo(true);
		this.pts.addLast(this.end);
		this.end = p;
	}

	/**
	 * Returns whether this chain is closed within a small tolerance.
	 *
	 * @return {@code true} if {@code start} and {@code end} are considered equal
	 */
	public boolean isClosed() {
		return equal(start, end);
	}

	/**
	 * Tests point equality under a fixed tolerance.
	 *
	 * @param q1 first point
	 * @param q2 second point
	 * @return {@code true} if both coordinates match within {@code 1e-10}
	 * @apiNote Uses {@link DoubleUtil#isEqual(double, double, double)} for x and y.
	 */
	static boolean equal(MyPoint q1, MyPoint q2) {
		return DoubleUtil.isEqual(q1.x, q2.x, 1e-10)
				&& DoubleUtil.isEqual(q1.y, q2.y, 1e-10);
	}

	/**
	 * Returns a concise, human-readable representation of this chain in
	 * {@code {start, p1, ..., end}} form.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(start);
		sb.append(", ");
		if (pts.size() != 0) {
			for (MyPoint p : pts) {
				sb.append(p);
				sb.append(", ");
			}
		}
		sb.append(end);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Adds the points of this list to a path gp within a given CoordSys.
	 *
	 * @param gp {@link GeneralPathClippedForCurvePlotter}
	 * @param coordSys {@link CoordSys}
	 */
	public void addToPath(GeneralPathClippedForCurvePlotter gp, CoordSys coordSys) {
		double[] coords = gp.newDoubleArray();
		gp.copyCoords(start, coords, coordSys);
		gp.drawTo(coords, start.getSegmentType());
		for (MyPoint p : pts) {
			gp.copyCoords(p, coords, coordSys);
			gp.lineTo(coords);
		}
		gp.copyCoords(end, coords, coordSys);
		gp.lineTo(coords);

		if (isClosed()) {
			gp.closePath();
		}
	}

	/**
	 * Total number of points in this chain, including {@code start} and {@code end}.
	 *
	 * @return number of points (at least {@code 2})
	 */
	public int size() {
		return pts.size() + 2;
	}

	/**
	 * Returns the point at index {@code i} in the chain.
	 * <p>
	 * Index {@code 0} is {@code start}; indices {@code 1..size()-2} are the
	 * intermediate points; index {@code size()-1} is {@code end}.
	 * Returns {@code null} when {@code i} is out of range.
	 * </p>
	 *
	 * @param i zero-based index into the chain
	 * @return the point at {@code i}, or {@code null} if {@code i} is invalid
	 */
	public MyPoint get(int i) {
		if (i == 0) {
			return start;
		}
		if (i < pts.size()) {
			return pts.get(i - 1);
		}
		if (i == pts.size()) {
			return end;
		}

		return null;
	}

	/**
	 * Returns all points of this chain in traversal order.
	 *
	 * @return ordered list containing start, intermediate points and end
	 */
	public List<MyPoint> asPoints() {
		List<MyPoint> points = new LinkedList<>();
		points.add(start);
		points.addAll(pts);
		points.add(end);
		return points;
	}
}
