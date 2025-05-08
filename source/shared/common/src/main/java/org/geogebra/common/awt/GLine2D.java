package org.geogebra.common.awt;

import org.geogebra.common.annotation.MissingDoc;

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

	@MissingDoc
	GPoint2D getP1();

	@MissingDoc
	GPoint2D getP2();

	@MissingDoc
	double getX1();

	@MissingDoc
	double getY1();

	@MissingDoc
	double getX2();

	@MissingDoc
	double getY2();
}
