package org.geogebra.common.awt;

/**
 * 2D line.
 */
public interface GLine2D extends GShape {

	/**
	 * @param x1 start point's x-coordinate
	 * @param y1 start point's y-coordinate
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's y-coordinate
	 */
	void setLine(double x1, double y1, double x2, double y2);

	/**
	 * @return the start point
	 */
	GPoint2D getP1();

	/**
	 * @return the end point
	 */
	GPoint2D getP2();

	/**
	 * @return x-coordinate of the start point
	 */
	double getX1();

	/**
	 * @return y-coordinate of the start point
	 */
	double getY1();

	/**
	 * @return x-coordinate of the end point
	 */
	double getX2();

	/**
	 * @return y-coordinate of the end point
	 */
	double getY2();
}
