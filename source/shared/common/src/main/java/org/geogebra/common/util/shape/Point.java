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

package org.geogebra.common.util.shape;

import java.util.Objects;

/**
 * Point object.
 */
public final class Point {

	public final double x;
	public final double y;

	/**
	 * Creates a point with coordinates specified
	 * @param x x
	 * @param y y
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get x coordinate
	 * @return x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Get y coordinate
	 * @return y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param x1 other point's x-coordinate
	 * @param y1 other point's y-coordinate
	 * @return Euclidean distance to another point
	 */
	public double distanceTo(double x1, double y1) {
		return Math.hypot(x - x1, y - y1);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Point)) {
			return false;
		}
		Point other = (Point) object;
		return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
