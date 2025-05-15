package org.geogebra.common.awt;

/**
 * Rectangle.
 */
public interface GRectangle2D extends GRectangularShape {

	/**
	 * Returns the Y coordinate of the upper-left corner of
	 * the rectangle in <code>double</code> precision.
	 * @return the Y coordinate of the upper-left corner of
	 * the rectangle.
	 */
	double getY();

	/**
	 * Returns the X coordinate of the upper-left corner of
	 * the rectangle in <code>double</code> precision.
	 * @return the X coordinate of the upper-left corner of
	 * the rectangle.
	 */
	double getX();

	/**
	 * Returns the width of the rectangle in
	 * <code>double</code> precision.
	 * @return the width of the rectangle.
	 */
	double getWidth();

	/**
	 * Returns the height of the rectangle in
	 * <code>double</code> precision.
	 * @return the height of the rectangle.
	 */
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

	/**
	 * Returns a new <code>Rectangle2D</code> object representing the
	 * intersection of this <code>Rectangle2D</code> with the specified
	 * <code>Rectangle2D</code>.
	 * @param r the <code>Rectangle2D</code> to be intersected with
	 * this <code>Rectangle2D</code>
	 * @return the largest <code>Rectangle2D</code> contained in both
	 *          the specified <code>Rectangle2D</code> and in this
	 *          <code>Rectangle2D</code>.
	 */
	GRectangle2D createIntersection(GRectangle2D r);

	/**
	 * Returns the smallest Y coordinate of the framing
	 * rectangle of the <code>Shape</code> in <code>double</code>
	 * precision.
	 * @return the smallest Y coordinate of the framing
	 *          rectangle of the <code>Shape</code>.
	 */
	double getMinX();

	/**
	 * Returns the largest X coordinate of the
	 * rectangle of the <code>Shape</code> in <code>double</code>
	 * precision.
	 * @return the largest X coordinate of the
	 *          rectangle of the <code>Shape</code>.
	 */
	double getMaxX();

	/**
	 * Returns the smallest Y coordinate of the
	 * rectangle of the <code>Shape</code> in <code>double</code>
	 * precision.
	 * @return the smallest Y coordinate of the
	 *          rectangle of the <code>Shape</code>.
	 */
	double getMinY();

	/**
	 * Returns the largest Y coordinate of the
	 * rectangle of the <code>Shape</code> in <code>double</code>
	 * precision.
	 * @return the largest Y coordinate of the
	 *          rectangle of the <code>Shape</code>.
	 */
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
