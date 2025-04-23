package org.geogebra.common.awt;

/**
 * Rectangle.
 */
public interface GRectangle2D extends GRectangularShape {

	double getY();

	double getX();

	double getWidth();

	double getHeight();

	/**
	 * @param x left
	 * @param y top
	 * @param width width
	 * @param height height
	 */
	void setRect(double x, double y, double width, double height);

	/**
	 * @param x left
	 * @param y top
	 * @param width width
	 * @param height height
	 */
	void setFrame(double x, double y, double width, double height);

	@Override
	boolean intersects(double minX, double minY, double lengthX,
			double lengthY);

	@Override
	boolean intersects(GRectangle2D viewRect);

	GRectangle2D createIntersection(GRectangle2D r);

	double getMinX();

	double getMaxX();

	double getMinY();

	double getMaxY();

	/**
	 * @param xc start point's x-coordinate
	 * @param yc start point-s y-coordinate
	 * @param xe end point's x-coordinate
	 * @param ye end point's y-coordinate
	 * @return whether this intersects a segment between two points
	 */
	boolean intersectsLine(double xc, double yc, double xe, double ye);

	/**
	 * Extends this rectangle to include point (x,y).
	 * @param x point's c-coordinate
	 * @param y point's y-coordinate
	 */
	void add(double x, double y);

}
