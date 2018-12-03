/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.discrete.tsp.impl.Point;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * Lightweight point with lineTo flag that can be easily transformed into
 * GeoPoint
 */
public class MyPoint extends GPoint2D implements Point {
	/** x-coord */
	public double x;
	/** y-coord */
	public double y;
	/** lineto flag */
	private SegmentType segmentType = SegmentType.LINE_TO;

	/**
	 * Creates new empty MyPoint for cache
	 */
	public MyPoint() {
		//
	}

	/**
	 * Creates new lineto MyPoint
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public MyPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates new MyPoint
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param segmentType
	 *            lineto flag
	 */
	public MyPoint(double x, double y, SegmentType segmentType) {
		this.x = x;
		this.y = y;
		this.segmentType = segmentType;
	}

	/**
	 * @param px
	 *            x-coordinate
	 * @param py
	 *            y-coordinate
	 * @return euclidian distance to otherpoint squared
	 */
	public double distSqr(double px, double py) {
		double vx = px - x;
		double vy = py - y;
		return vx * vx + vy * vy;
	}

	/**
	 * @param px
	 *            x-coord
	 * @param py
	 *            y-coord
	 * @return true if points are equal (Kernel.MIN_PRECISION)
	 */
	public boolean isEqual(double px, double py) {
		return DoubleUtil.isEqual(x, px, Kernel.MIN_PRECISION)
				&& DoubleUtil.isEqual(y, py, Kernel.MIN_PRECISION);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * @param p
	 *            other point
	 * @return euclidian distance from p
	 */
	@Override
	public double distance(Point p) {
		return MyMath.length(p.getX() - x, p.getY() - y);
	}

	/**
	 * Converts this into GeoPoint
	 * 
	 * @param cons
	 *            construction for the new point
	 * @return GeoPoint equivalent
	 */
	public GeoPoint getGeoPoint(Construction cons) {
		return new GeoPoint(cons, null, x, y, 1.0);
	}

	/**
	 * @return lineTo flag
	 */
	public boolean getLineTo() {
		return segmentType == SegmentType.LINE_TO;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	/**
	 * @return 0; for 3D compatibility
	 */
	public double getZ() {
		return 0;
	}

	@Override
	public double distance(double x1, double y1) {
		return GPoint2D.distanceSq(getX(), getY(), x1, y1);
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public double distance(GPoint2D q) {
		return distance(q.getX(), q.getY());
	}

	/**
	 * 
	 * @param point
	 *            point
	 * @return true if same (x,y)
	 */
	public boolean isEqual(MyPoint point) {
		return isEqual(point.x, point.y);
	}

	/**
	 * 
	 * @return true if coords are finite numbers
	 */
	public boolean isFinite() {
		return MyDouble.isFinite(x) && MyDouble.isFinite(y);
	}

	/**
	 * 
	 * @param t
	 *            parameter
	 * @param point2
	 *            second point
	 * @return (1-t) * this + t * point2
	 */
	public MyPoint barycenter(double t, MyPoint point2) {
		return new MyPoint((1 - t) * x + t * point2.x,
				(1 - t) * y + t * point2.y, SegmentType.MOVE_TO);
	}

	/**
	 * Change to lineto /moveto point
	 * 
	 * @param lineTo
	 *            whether this shoul be linto point
	 */
	public void setLineTo(boolean lineTo) {
		this.segmentType = lineTo ? SegmentType.LINE_TO : SegmentType.MOVE_TO;

	}

	@Override
	public double distanceSqr(Point to) {
		return distSqr(to.getX(), to.getY());
	}

	@Override
	public boolean isActive() {
		// reuse field "lineTo"
		return segmentType == SegmentType.LINE_TO;
	}

	@Override
	public void setActive(boolean active) {
		// re-use field "lineTo"
		this.segmentType = active ? SegmentType.LINE_TO : SegmentType.MOVE_TO;

	}

	/**
	 * @return segment type
	 */
	public SegmentType getSegmentType() {
		return segmentType;
	}

	/**
	 * @return copy of this point
	 */
	public MyPoint copy() {
		return new MyPoint(x, y, segmentType);
	}

	/**
	 * @return whether coordinates are not NaN (checks only x)
	 */
	public boolean isDefined() {
		return MyDouble.isFinite(x);
	}

	/**
	 * @param x1
	 *            x-coord
	 * @param y1
	 *            y-coord
	 */
	public void setCoords(double x1, double y1) {
		x = x1;
		y = y1;

	}

	/**
	 * Invalidate this point
	 */
	public void setUndefined() {
		x = java.lang.Double.NaN;

	}

	/**
	 * @param returnType
	 *            new type
	 * @return copy with given type
	 */
	public MyPoint withType(SegmentType returnType) {
		return new MyPoint(x, y, returnType);
	}
}
