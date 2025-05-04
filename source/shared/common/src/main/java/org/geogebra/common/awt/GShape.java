package org.geogebra.common.awt;

/**
 * Geometric shape.
 */
public interface GShape {

	/**
	 * Checks if this shape intersects given rectangle.
	 * @param x rectangle's left
	 * @param y rectangle's top
	 * @param w rectangle's width
	 * @param h rectangle's height
	 * @return whether this shape intersects the rectangle.
	 */
	boolean intersects(int x, int y, int w, int h);

	/**
	 * Check if this contains a given point.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return whether this shape contains given point
	 */
	boolean contains(int x, int y);

	/**
	 * @return shape bounds
	 */
	GRectangle getBounds();

	/**
	 * @return shape bounds
	 */
	GRectangle2D getBounds2D();

	/**
	 * Check if this contains a given rectangle.
	 * @param rectangle rectangle
	 * @return whether this shape contains given rectangle
	 */
	boolean contains(GRectangle2D rectangle);

	/**
	 * Check if this contains a given point.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return whether this shape contains given point
	 */
	boolean contains(double x, double y);

	/**
	 * Path iterator for the outline of this shape.
	 * @param affineTransform transformation
	 * @return the iterator
	 */
	GPathIterator getPathIterator(GAffineTransform affineTransform);

	/**
	 * Checks if this shape intersects given rectangle.
	 * @param x rectangle's left
	 * @param y rectangle's top
	 * @param w rectangle's width
	 * @param h rectangle's height
	 * @return whether this shape intersects the rectangle.
	 */
	boolean intersects(double x, double y, double w, double h);

	/**
	 * Checks if this shape intersects given rectangle.
	 * @param r the rectangle
	 * @return whether this shape intersects the rectangle.
	 */
	boolean intersects(GRectangle2D r);

}
