package org.geogebra.common.awt;

/** Class for integer tuples **/
public class GPoint {
	/** y-coordinate **/
	public int y;
	/** x-coordinate **/
	public int x;

	/**
	 * Point (0, 0)
	 */
	public GPoint() {
		x = 0;
		y = 0;
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public GPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set x and y at the same time
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Take both coords from a point
	 * 
	 * @param p
	 *            point
	 */
	public void setLocation(GPoint p) {
		this.x = p.x;
		this.y = p.y;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return 0;
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof GPoint)) {
			return false;
		}
		return ((GPoint) o).x == x && ((GPoint) o).y == y
				&& ((GPoint) o).getZ() == getZ();
	}

	@Override
	public int hashCode() {
		return (x << 16) ^ y;
	}

	public double distance(GPoint d) {
		return Math.sqrt((x - d.x) * (x - d.x) + (y - d.y) * (y - d.y));
	}

	/**
	 * Distance from point (dx, dy)
	 * @param dx other point's x-coordinate
	 * @param dy other point's y-coordinate
	 * @return Euclidean distance
	 */
	public double distance(double dx, double dy) {
		return (int) Math.sqrt((x - dx) * (x - dx) + (y - dy) * (y - dy));
	}

	@Override
	public String toString() {
		return x + " : " + y;
	}

}
