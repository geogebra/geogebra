/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	/**
	 * Adds a <code>Rectangle</code> to this <code>Rectangle</code>.
	 * The resulting <code>Rectangle</code> is the union of the two
	 * rectangles.
	 * <p>
	 * If either {@code Rectangle} has any dimension less than 0, the
	 * result will have the dimensions of the other {@code Rectangle}.
	 * If both {@code Rectangle}s have at least one dimension less
	 * than 0, the result will have at least one dimension less than 0.
	 * <p>
	 * If either {@code Rectangle} has one or both dimensions equal
	 * to 0, the result along those axes with 0 dimensions will be
	 * equivalent to the results obtained by adding the corresponding
	 * origin coordinate to the result rectangle along that axis,
	 * similar to the operation of the {@link #add(double, double)} method,
	 * but contribute no further dimension beyond that.
	 * <p>
	 * If the resulting {@code Rectangle} would have a dimension
	 * too large to be expressed as an {@code int}, the result
	 * will have a dimension of {@code Integer.MAX_VALUE} along
	 * that dimension.
	 * @param  r the specified <code>Rectangle</code>
	 */
	void add(GRectangle r);

	@Override
	void add(double x, double y);

	/**
	 * @param p1 point
	 * @return whether this contains the point
	 */
	boolean contains(GPoint2D p1);

	/**
	 * Computes the union of this <code>Rectangle</code> with the
	 * specified <code>Rectangle</code>. Returns a new
	 * <code>Rectangle</code> that
	 * represents the union of the two rectangles.
	 * <p>
	 * If either {@code Rectangle} has any dimension less than zero
	 * the rules for <a href=#NonExistent>non-existent</a> rectangles
	 * apply.
	 * If only one has a dimension less than zero, then the result
	 * will be a copy of the other {@code Rectangle}.
	 * If both have dimension less than zero, then the result will
	 * have at least one dimension less than zero.
	 * <p>
	 * If the resulting {@code Rectangle} would have a dimension
	 * too large to be expressed as an {@code int}, the result
	 * will have a dimension of {@code Integer.MAX_VALUE} along
	 * that dimension.
	 * @param r the specified <code>Rectangle</code>
	 * @return    the smallest <code>Rectangle</code> containing both
	 *            the specified <code>Rectangle</code> and this
	 *            <code>Rectangle</code>.
	 */
	GRectangle union(GRectangle r);

	/**
	 * Set size.
	 * @param width width
	 * @param height height
	 */
	void setSize(int width, int height);

}
