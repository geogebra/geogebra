/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.util.MyMath;
/**
 * Lightweight point with lineTo flag that can be easily transformed into GeoPoint 
 */
public class MyPoint {
	/** x-coord */
	public double x;
	/** y-coord */
	public double y;
	/** lineto flag */
	public boolean lineTo;

	/**
	 * Creates new MyPoint
	 * @param x x-coord
	 * @param y y-coord
	 * @param lineTo lineto flag
	 */
	public MyPoint(double x, double y, boolean lineTo) {
		this.x = x;
		this.y = y;
		this.lineTo = lineTo;
	}

	/**
	 * @param px x-coordinate
	 * @param py y-coordinate
	 * @return euclidian distance to otherpoint squared
	 */
	public double distSqr(double px, double py) {
		double vx = px - x;
		double vy = py - y;
		return vx * vx + vy * vy;
	}

	/**
	 * @param px x-coord
	 * @param py y-coord
	 * @return true if points are equal (Kernel.MIN_PRECISION)
	 */
	public boolean isEqual(double px, double py) {
		return Kernel.isEqual(x, px, Kernel.MIN_PRECISION)
				&& Kernel.isEqual(y, py, Kernel.MIN_PRECISION);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * @param p other point
	 * @return euclidian distance from p
	 */
	public double distance(MyPoint p) {
		return MyMath.length(p.x - x, p.y - y);
	}

	/**
	 * Converts this into GeoPoint
	 * @param cons construction for the new point 
	 * @return GeoPoint equivalent
	 */
	public GeoPoint2 getGeoPoint(Construction cons) {
		return new GeoPoint2(cons, null, x, y, 1.0);
	}
}
