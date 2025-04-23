package org.geogebra.common.awt;

/**
 * General path.
 */
public interface GGeneralPath extends GShape {

	void moveTo(double x, double y);

	void reset();

	/**
	 * Ad a line segment to this path.
	 * @param x endpoint's x-coordinate
	 * @param y endpoint's y-coordinate
	 */
	void lineTo(double x, double y);

	void append(GShape s, boolean connect);

	void closePath();

	GShape createTransformedShape(GAffineTransform affineTransform);

	GPoint2D getCurrentPoint();

	@Override
	boolean contains(GRectangle2D p);

	/**
	 * Whether this contains a  rectangle with given boundaries
	 * @param x rectangle's left
	 * @param y rectangle's top
	 * @param w width of the rectangle
	 * @param h height of the rectangle
	 * @return whether rectangle is inside this shape.
	 */
	boolean contains(double x, double y, double w, double h);

	@Override
	boolean intersects(GRectangle2D arg0);

	/**
	 * @param p point
	 * @return whether the point is inside this shape.
	 */
	boolean contains(GPoint2D p);

	/**
	 * Add a bezier curve segment to this path.
	 * @param x1 first control point's x-coordinate
	 * @param y1 first control point's y-coordinate
	 * @param x2 second control point's x-coordinate
	 * @param y2 second control point's y-coordinate
	 * @param x3 end point's x-coordinate
	 * @param y3 end point's y-coordinate
	 */
	void curveTo(double x1, double y1, double x2, double y2,
			double x3, double y3);

	/**
	 * Add a quadratic curve segment to this path.
	 * @param x1 control point's x-coordinate
	 * @param y1 control point's y-coordinate
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's y-coordinate
	 */
	void quadTo(double x1, double y1, double x2, double y2);

}