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
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.DoubleUtil;

/**
 * Point representing part of a segment
 */
public class MyPoint extends GPoint2D {

	private SegmentType segmentType = SegmentType.LINE_TO;

	/**
	 * Creates new empty MyPoint for cache
	 */
	public MyPoint() {
		super();
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
		super(x, y);
	}

	/**
	 * Creates new MyPoint
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param segmentType
	 *            segment type
	 */
	public MyPoint(double x, double y, SegmentType segmentType) {
		super(x, y);
		this.segmentType = segmentType;
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
	 *            whether this should be lineto point
	 */
	public void setLineTo(boolean lineTo) {
		this.segmentType = lineTo ? SegmentType.LINE_TO : SegmentType.MOVE_TO;
	}

	/**
	 * Reuses the segmentType field (active iff LINE_TO)
	 * @return whether it is an active point (for TSP solver)
	 */
	public boolean isActive() {
		return segmentType == SegmentType.LINE_TO;
	}

	/**
	 * Reuses the segmentType field (for TSP solver)
	 * @param active if true, set segmentType to LINE_TO, otherwise to MOVE_TO
	 */
	public void setActive(boolean active) {
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
	 * @param returnType
	 *            new type
	 * @return copy with given type
	 */
	public MyPoint withType(SegmentType returnType) {
		return new MyPoint(x, y, returnType);
	}
}
