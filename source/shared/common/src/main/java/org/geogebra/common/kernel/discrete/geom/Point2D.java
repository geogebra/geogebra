package org.geogebra.common.kernel.discrete.geom;

import java.util.Objects;

public class Point2D implements Comparable<Point2D> {

	public double x;
	public double y;

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public int compareTo(Point2D other) {
		if (x != other.x) {
			return Double.compare(x, other.x);
		}
		return Double.compare(y, other.y);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point2D)) {
			return false;
		}
		Point2D other = (Point2D) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

}
