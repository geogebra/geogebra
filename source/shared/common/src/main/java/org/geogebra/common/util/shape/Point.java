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
