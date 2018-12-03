/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * Lightweight point with lineTo flag that can be easily transformed into
 * GeoPoint3D
 */
public class MyPoint3D extends MyPoint {

	/** z-coord */
	public double z;

	/**
	 * Creates new MyPoint3D
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param segmentType
	 *            segment type
	 */
	public MyPoint3D(double x, double y, double z, SegmentType segmentType) {
		super(x, y, segmentType);
		this.z = z;
	}

	/**
	 * Simple constructor
	 */
	public MyPoint3D() {
		super();
	}

	/**
	 * @param px
	 *            x-coordinate
	 * @param py
	 *            y-coordinate
	 * @param pz
	 *            z-coordinate
	 * @return euclidian distance to otherpoint squared
	 */
	public double distSqr(double px, double py, double pz) {
		double vx = px - x;
		double vy = py - y;
		double vz = pz - z;
		return vx * vx + vy * vy + vz * vz;
	}

	/**
	 * @param px
	 *            x-coord
	 * @param py
	 *            y-coord
	 * @param pz
	 *            z-coord
	 * @return true if points are equal (Kernel.MIN_PRECISION)
	 */
	public boolean isEqual(double px, double py, double pz) {
		return super.isEqual(px, py)
				&& DoubleUtil.isEqual(z, pz, Kernel.MIN_PRECISION);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	/**
	 * @param p
	 *            other point
	 * @return euclidian distance from p
	 */
	public double distance(MyPoint3D p) {
		return MyMath.length(p.x - x, p.y - y, p.z - z);
	}

	@Override
	public double getZ() {
		return z;
	}

	/**
	 * @param x1
	 *            x
	 * @param y1
	 *            y
	 * @param z1
	 *            z
	 * @return distance from (x1,y1,z1)
	 */
	public double distance(double x1, double y1, double z1) {
		return distSqr(x1, y1, z1);
	}

	/**
	 * @param z
	 *            z-coord
	 */
	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public double distance(GPoint2D q) {
		return distance(q.getX(), q.getY(), 0);
	}

	/**
	 * 
	 * @param point
	 *            point
	 * @return true if same (x,y)
	 */
	public boolean isEqual(MyPoint3D point) {
		return isEqual(point.x, point.y, point.z);
	}

	@Override
	public boolean isFinite() {
		return super.isFinite() && MyDouble.isFinite(z);
	}

	@Override
	public MyPoint3D barycenter(double t, MyPoint point2) {
		return new MyPoint3D((1 - t) * x + t * point2.x,
				(1 - t) * y + t * point2.y, (1 - t) * z + t * point2.getZ(),
				SegmentType.MOVE_TO);
	}

	@Override
	public MyPoint copy() {
		return new MyPoint3D(x, y, z, getSegmentType());
	}
}
