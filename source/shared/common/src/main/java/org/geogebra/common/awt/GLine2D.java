package org.geogebra.common.awt;

public interface GLine2D extends GShape {

	/**
	 * @param x1 start point's x-coordinate
	 * @param y1 start point's y-coordinate
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's y-coordinate
	 */
	void setLine(double x1, double y1, double x2, double y2);

	GPoint2D getP1();

	GPoint2D getP2();

	double getX1();

	double getY1();

	double getX2();

	double getY2();
}
