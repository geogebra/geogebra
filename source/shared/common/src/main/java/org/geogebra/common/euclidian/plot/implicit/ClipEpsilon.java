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
 * Numeric tolerances used while clipping contours to the viewport.
 * <p>
 * {@code world} is a tolerance in world units (e.g., half a pixel). {@code param}
 * is a slack for segment/edge parameters in {@code [0,1]}.
 * </p>
 */
public final class ClipEpsilon {

	private final double world;
	private final double param;

	/**
	 * Creates a tolerance bundle.
	 *
	 * @param world tolerance in world units (e.g., half a pixel)
	 * @param param tolerance in parameter space for segment/edge math
	 */
	public ClipEpsilon(double world, double param) {
		this.world = world;
		this.param = param;
	}

	/**
	 * Derives reasonable tolerances from the current view bounds.
	 *
	 * @param bounds view bounds that provide pixels-to-world scaling
	 * @return a new {@code ClipEpsilon} with world and param tolerances
	 */
	public static ClipEpsilon fromBounds(EuclidianViewBounds bounds) {
		double pxX = bounds.getInvXscale();      // world units per screen px
		double pxY = bounds.getInvYscale();
		double epsWorld = 0.5 * Math.min(pxX, pxY); // ~ half a pixel
		double epsParam = 1e-9;             // stable param-space slack
		return new ClipEpsilon(epsWorld, epsParam);
	}

	/**
	 * Returns the world-space tolerance.
	 *
	 * @return tolerance in world units
	 */
	public double world() {
		return world;
	}

	/**
	 * Returns the parameter-space tolerance.
	 *
	 * @return tolerance for segment/edge parameters
	 */
	public double param() {
		return param;
	}

	/**
	 * Tolerance for snapping to rectangle corners.
	 *
	 * @return max of {@code world} and {@code 1e-9}
	 */
	public double corner() {
		return Math.max(world, 1e-9);
	}

	/**
	 * Tolerance for treating denominators as zero in intersection math.
	 *
	 * @return a very small epsilon, {@code 1e-15}
	 */
	public double denom() {
		return 1e-15;
	}

	/**
	 * Returns a concise debug string.
	 */
	@Override
	public String toString() {
		return "ClipEpsilon{" + "world=" + world + ", param=" + param + "}";
	}
}
