package org.geogebra.common.kernel.geos;

/**
 * Point with homogeneous 2D cartesian coordinates.
 */
public interface HasCoordinates {

	/**
	 * Make all coordinates undefined.
	 */
	void setUndefined();

	/**
	 * Set homogeneous coordinates.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param z z-coordinate (scale)
	 */
	void setCoords(double x, double y, double z);

	/**
	 * @return x-coordinate
	 */
	double getX();

	/**
	 * @return y-coordinate
	 */
	double getY();

	/**
	 * @return z-coordinate (scale)
	 */
	double getZ();
}
