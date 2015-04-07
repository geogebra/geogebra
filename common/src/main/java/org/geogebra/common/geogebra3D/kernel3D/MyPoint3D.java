/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
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
	 * @param lineTo
	 *            lineto flag
	 */
	public MyPoint3D(double x, double y, double z, boolean lineTo) {
		super(x, y, lineTo);
		this.z = z;
	}

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
				&& Kernel.isEqual(z, pz, Kernel.MIN_PRECISION);
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

	/**
	 * Converts this into GeoPoint3D
	 * 
	 * @param cons
	 *            construction for the new point
	 * @return GeoPoint3D equivalent
	 */
	public GeoPoint3D getGeoPoint3D(Construction cons) {
		return new GeoPoint3D(cons, null, x, y, z, 1.0);
	}

	@Override
	public double getZ() {
		return z;
	}

	public double distance(double x1, double y1, double z1) {
		return distSqr(x1, y1, z1);
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public double distance(org.geogebra.common.awt.GPoint2D q) {
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
		return super.isFinite() && isFinite(z);
	}

	@Override
	public MyPoint3D barycenter(double t, MyPoint point2) {
		return new MyPoint3D((1 - t) * x + t * point2.x, (1 - t) * y + t
				* point2.y, (1 - t) * z + t * point2.getZ(), false);
	}
}
