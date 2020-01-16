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

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double distance(GPoint2D q) {
		return Math.sqrt(distanceSq(getX(), getY(), q.getX(), q.getY()));
	}

	public double distance(double x1, double y1) {
		return Math.sqrt(distanceSq(getX(), getY(), x1, y1));
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
}
