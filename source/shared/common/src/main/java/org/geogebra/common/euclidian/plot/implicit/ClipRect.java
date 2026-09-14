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

package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

/**
 * Axis-aligned rectangle used for clipping contours to the current viewport.
 * <p>
 * Expands the view bounds by a pixel margin converted to world units so
 * intersections near the screen edge are handled robustly.
 * </p>
 */
public final class ClipRect {
	private final double xmin;
	private final double xmax;
	private final double ymin;
	private final double ymax;

	/**
	 * Creates a clip rectangle expanded by {@code clipMargin} screen pixels.
	 *
	 * @param bounds      view bounds supplying world coordinates and scales
	 * @param clipMargin  extra margin in screen pixels to add on each side
	 */
	public ClipRect(EuclidianViewBounds bounds, int clipMargin) {
		this(bounds.getXmin() - bounds.getInvXscale() * clipMargin,
				bounds.getXmax() + bounds.getInvXscale() * clipMargin,
				bounds.getYmin() - bounds.getInvYscale() * clipMargin,
				bounds.getYmax() + bounds.getInvYscale() * clipMargin);
	}

	/**
	 * Creates a clip rectangle from explicit world-coordinate bounds.
	 *
	 * @param xmin minimum x
	 * @param xmax maximum x
	 * @param ymin minimum y
	 * @param ymax maximum y
	 */
	public ClipRect(double xmin, double xmax, double ymin, double ymax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}

	/**
	 * Left x of the expanded rectangle.
	 *
	 * @return xmin in world units
	 */
	public double getXmin() {
		return xmin;
	}

	/**
	 * Right x of the expanded rectangle.
	 *
	 * @return xmax in world units
	 */
	public double getXmax() {
		return xmax;
	}

	/**
	 * Bottom y of the expanded rectangle.
	 *
	 * @return ymin in world units
	 */
	public double getYmin() {
		return ymin;
	}

	/**
	 * Top y of the expanded rectangle.
	 *
	 * @return ymax in world units
	 */
	public double getYmax() {
		return ymax;
	}

}
