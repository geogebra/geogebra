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

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

/**
 * Immutable, axis-aligned rectangle described by its minimum/maximum X and Y
 * coordinates in world space.
 * <p>
 * This is a lightweight bounds holder used in plotting/clipping code where only
 * the four extrema are needed. The class does not enforce any invariants at
 * runtime; callers are expected to pass consistent values (i.e.,
 * {@code xmin <= xmax} and {@code ymin <= ymax}).
 * </p>
 *
 * <h2>Coordinate system</h2>
 * The rectangle is expressed in world coordinates; its interpretation is
 * independent of screen coordinate conventions (Y-up vs. Y-down).
 *
 */
public class BoundsRectangle {

	private final double xmin;
	private final double xmax;
	private final double ymin;
	private final double ymax;

	/**
	 * Creates a bounds rectangle from explicit extrema.
	 *
	 * @param xmin minimum X (world units)
	 * @param xmax maximum X (world units)
	 * @param ymin minimum Y (world units)
	 * @param ymax maximum Y (world units)
	 *
	 * @implNote No validation is performed. For correct behavior downstream,
	 *           the caller should ensure {@code xmin <= xmax} and
	 *           {@code ymin <= ymax}.
	 */
	public BoundsRectangle(double xmin, double xmax, double ymin, double ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	/**
	 * Creates a bounds rectangle by expanding an {@link EuclidianViewBounds}
	 * by the given margins on each side.
	 * <p>
	 * The resulting rectangle is:
	 * </p>
	 * <pre>
	 *   [xmin, xmax] = [bounds.xmin - mx, bounds.xmax + mx]
	 *   [ymin, ymax] = [bounds.ymin - my, bounds.ymax + my]
	 * </pre>
	 *
	 * @param bounds source view bounds in world units
	 * @param mx     horizontal margin to add on both left and right (world units)
	 * @param my     vertical margin to add on both bottom and top (world units)
	 *
	 * @implNote Margins may be negative to shrink the rectangle.
	 * @see EuclidianViewBounds
	 */
	public BoundsRectangle(EuclidianViewBounds bounds, double mx, double my) {
		this(
				bounds.getXmin() - mx, bounds.getXmax() + mx, bounds.getYmin() - my, bounds.getYmax() + my);
	}

	/**
	 * @return minimum X in world coordinates
	 */
	public double getXmin() {
		return xmin;
	}

	/**
	 * @return maximum X in world coordinates
	 */
	public double getXmax() {
		return xmax;
	}

	/**
	 * @return minimum Y in world coordinates
	 */
	public double getYmin() {
		return ymin;
	}

	/**
	 * @return maximum Y in world coordinates
	 */
	public double getYmax() {
		return ymax;
	}

	/**
	 * Returns a concise string representation for debugging, including all four
	 * extrema.
	 *
	 * @return string form {@code BoundsRectangle{xmin=..., xmax=..., ymin=..., ymax=...}}
	 */
	@Override
	public String toString() {
		return "BoundsRectangle{"
				+ "xmin=" + xmin
				+ ", xmax=" + xmax
				+ ", ymin=" + ymin
				+ ", ymax=" + ymax
				+ '}';
	}

	/**
	 * Normalizes a world-space x-coordinate into the rectangle's unit interval.
	 *
	 * @param x world-space x-coordinate
	 * @return normalized x-coordinate
	 */
	public double normalizeX(double x) {
		return (x - xmin) / (xmax - xmin);
	}

	/**
	 * Normalizes a world-space y-coordinate into the rectangle's unit interval.
	 *
	 * @param y world-space y-coordinate
	 * @return normalized y-coordinate
	 */
	public double normalizeY(double y) {
		return (y - ymin) / (xmax - xmin);
	}
}
