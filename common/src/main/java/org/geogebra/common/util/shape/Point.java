package org.geogebra.common.util.shape;

/**
 * Point object.
 */
public class Point {

	private double x;
	private double y;

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
}
