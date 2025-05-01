package org.geogebra.common.awt;

/**
 * Rectangle.
 */
public interface GRectangle extends GRectangle2D {

	@Override
	double getY();

	@Override
	double getX();

	@Override
	double getWidth();

	@Override
	double getHeight();

	/**
	 * Sets the bounding <code>Rectangle</code> of this
	 * <code>Rectangle</code> to the specified
	 * <code>x</code>, <code>y</code>, <code>width</code>,
	 * and <code>height</code>.
	 * <p>
	 * This method is included for completeness, to parallel the
	 * <code>setBounds</code> method of <code>Component</code>.
	 * @param x the new X coordinate for the upper-left
	 *                    corner of this <code>Rectangle</code>
	 * @param y the new Y coordinate for the upper-left
	 *                    corner of this <code>Rectangle</code>
	 * @param width the new width for this <code>Rectangle</code>
	 * @param height the new height for this <code>Rectangle</code>
	 */
	void setBounds(int x, int y, int width, int height);

	/**
	 * Set location.
	 * @param x left
	 * @param y top
	 */
	void setLocation(int x, int y);

	/**
	 * Set bounds (i.e. copy size and position).
	 * @param rectangle bounding rectangle
	 */
	void setBounds(GRectangle rectangle);

	void add(GRectangle bb);

	@Override
	void add(double x, double y);

	// boolean contains(PathPoint prevP);
	boolean contains(GPoint2D p1);

	GRectangle union(GRectangle bounds);

	/**
	 * Set size.
	 * @param width width
	 * @param height height
	 */
	void setSize(int width, int height);

}
