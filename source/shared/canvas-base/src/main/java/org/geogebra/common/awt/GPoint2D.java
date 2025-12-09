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

package org.geogebra.common.awt;

public class GPoint2D {

	public double x;
	public double y;

	public GPoint2D(double x, double y) {
		setLocation(x, y);
	}

	public GPoint2D() {
		setLocation(0, 0);
	}

	/**
	 * Set x and y coordinates of the point
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/**
	 * @return 0; for 3D compatibility
	 */
	public double getZ() {
		return 0;
	}

	/**
	 * Gets Euclidean distance to a point.
	 * @param q other point
	 * @return euclidean distance
	 */
	public double distance(GPoint2D q) {
		return Math.hypot(getX() - q.getX(), getY() - q.getY());
	}

	/**
	 * Gets Euclidean distance to a point.
	 * @param x1 x-coord of the other point
	 * @param y1 y-coord of the other point
	 * @return euclidean distance
	 */
	public double distance(double x1, double y1) {
		return Math.hypot(getX() - x1, getY() - y1);
	}

	/**
	 * Gts squared distance to a point
	 * @param to other point
	 * @return squared distance
	 */
	public double distanceSq(GPoint2D to) {
		return distanceSq(x, y, to.getX(), to.getY());
	}

	/**
	 * @param x1
	 *            x-coord of first point
	 * @param y1
	 *            y-coord of first point
	 * @param x2
	 *            x-coord of second point
	 * @param y2
	 *            y-coord of second point
	 * @return squared distance
	 */
	public static double distanceSq(double x1, double y1, double x2,
			double y2) {
		double d_x = x2 - x1;
		double d_y = y2 - y1;
		return d_x * d_x + d_y * d_y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
