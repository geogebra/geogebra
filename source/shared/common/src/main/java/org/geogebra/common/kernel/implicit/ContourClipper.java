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

package org.geogebra.common.kernel.implicit;

import java.util.List;

import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragmentsResult;
import org.geogebra.common.euclidian.plot.implicit.EdgeHit;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.jspecify.annotations.Nullable;

/**
 * Clips marching-produced contours against a viewport and exposes
 * auxiliary data produced during clipping.
 * <p>
 * Implementations apply a clipping strategy (e.g., perimeter-based or
 * polygonal) for the current {@link EuclidianViewBounds} and may collect
 * edge intersection points for stitching and debugging.
 * </p>
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Call {@link #setPolynomial(BernsteinPolynomial2D)} before
 *       {@link #clip(EuclidianViewBounds)} if the clipper requires inside/outside tests.</li>
 *   <li>{@link #clip(EuclidianViewBounds)} prepares internal state and updates edge points;
 *       {@link #getEdgePoints()} reflects the most recent run.</li>
 *   <li>{@link #clearEdgePoints()} resets the collected edge intersections without
 *       changing the source contours.</li>
 * </ul>
 */
public interface ContourClipper {
	/**
	 * Performs clipping for the given viewport bounds.
	 *
	 * @param bounds current view bounds in world coordinates
	 */
	void clip(EuclidianViewBounds bounds);

	/**
	 * Reports whether the most recent run produced any clipping effects
	 * (e.g., segments truncated or perimeter runs emitted).
	 *
	 * @return {@code true} if clipping modified the contours; {@code false} otherwise
	 */
	boolean isClipped();

	/**
	 * Clears the list of collected edge intersection points.
	 * <p>
	 * Useful when discarding diagnostic data between runs.
	 * </p>
	 */
	void clearEdgePoints();

	/**
	 * Returns edge-intersection points gathered during the last
	 * {@link #clip(EuclidianViewBounds)} call.
	 *
	 * @return a list of boundary points (ordering is implementation-dependent)
	 */
	List<MyPoint> getEdgePoints();

	/**
	 * Supplies the polynomial used for inside/outside tests during clipping.
	 * <p>
	 * Implementations may evaluate this polynomial to decide whether perimeter
	 * segments belong to the filled region.
	 * </p>
	 *
	 * @param polynomial Bernstein-form polynomial defined over the current bounds
	 */
	void setPolynomial(BernsteinPolynomial2D polynomial);

	/**
	 * Exposes the collected edge hits from the last clipping pass.
	 *
	 * @return the hit list (may be {@code null} before the first run)
	 */
	@Nullable List<EdgeHit> getHits();

	/**
	 * Exposes post-clip contour fragments from the last clipping pass.
	 *
	 * @return clipped fragments in source-contour order
	 */
	List<ClippedFragment> getFragments();

	/**
	 * Exposes the full post-clip fragment result from the last clipping pass.
	 *
	 * @return fragments plus collected hits
	 */
	ClippedFragmentsResult getClippedFragmentsResult();
}
