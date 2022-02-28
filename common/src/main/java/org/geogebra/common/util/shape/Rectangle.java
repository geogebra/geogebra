package org.geogebra.common.util.shape;

/**
 * Rectangle object.
 */
public class Rectangle {

	private final Point origin;
	private final Size size;

	/**
	 * Creates an empty rectangle object.
	 */
	public Rectangle() {
		this(0, 0, 0, 0);
	}

	/**
	 * Creates a rectangle object with the corners specified
	 * @param minX min x
	 * @param maxX max x
	 * @param minY min y
	 * @param maxY max y
	 */
	public Rectangle(double minX, double maxX, double minY, double maxY) {
		this(new Point(minX, minY), new Size(maxX - minX, maxY - minY));
	}

	/**
	 * Creates a rectangle objects with origin and size speicifed.
	 * @param origin origin
	 * @param size size
	 */
	public Rectangle(Point origin, Size size) {
		this.origin = origin;
		this.size = size;
	}

	/**
	 * Get width
	 * @return width
	 */
	public double getWidth() {
		return size.getWidth();
	}

	/**
	 * Get height
	 * @return height
	 */
	public double getHeight() {
		return size.getHeight();
	}

	/**
	 * Get min x
	 * @return min x
	 */
	public double getMinX() {
		return origin.getX();
	}

	/**
	 * Get max x
	 * @return max x
	 */
	public double getMaxX() {
		return origin.getX() + size.getWidth();
	}

	/**
	 * Get min y
	 * @return min y
	 */
	public double getMinY() {
		return origin.getY();
	}

	/**
	 * Get max y
	 * @return max y
	 */
	public double getMaxY() {
		return origin.getY() + size.getHeight();
	}
}
