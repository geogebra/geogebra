package org.geogebra.common.awt;

public interface GShape {

	/**
	 * Checks if this shape intersects given rectangle.
	 * @param x rectangle's left
	 * @param y rectangle's top
	 * @param w rectangle's width
	 * @param h rectangle's height
	 * @return whether this shape instersects the rectangle.
	 */
	boolean intersects(int x, int y, int w, int h);

	boolean contains(int x, int y);

	GRectangle getBounds();

	GRectangle2D getBounds2D();

	boolean contains(GRectangle2D rectangle);

	boolean contains(double x, double y);

	GPathIterator getPathIterator(GAffineTransform affineTransform);

	/**
	 * Checks if this shape intersects given rectangle.
	 * @param x rectangle's left
	 * @param y rectangle's top
	 * @param w rectangle's width
	 * @param h rectangle's height
	 * @return whether this shape instersects the rectangle.
	 */
	public boolean intersects(double x, double y, double w, double h);

	/**
	 * Checks if this shape intersects given rectangle.
	 * @param r the rectangle
	 * @return whether this shape instersects the rectangle.
	 */
	public boolean intersects(GRectangle2D r);

}
