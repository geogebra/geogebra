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

import java.util.LinkedList;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.DoubleUtil;

/**
 * Links pairs of marching algorithm points into one or more continuous contours.
 *
 * <p>Subclasses implement {@link #link(MyPoint, MyPoint, boolean)} to define
 * their specific linking strategy.</p>
 */
public abstract class ContourLinker extends LinkedList<PointList> {
	private static final double COORDINATE_DELTA = 1e-10;

	/**
	 * Adds a pair of points into the contour according to the chosen strategy.
	 *
	 * @param p0 first {@link MyPoint} endpoint
	 * @param p1 second {@link MyPoint} endpoint
	 * @param canSwap whether endpoints can be swapped
	 */
	public abstract void link(MyPoint p0, MyPoint p1, boolean canSwap);

	/**
	 * Returns true if q1 and q2 are equal within a small epsilon.
	 *
	 * @param q1 the first {@link MyPoint} to compare
	 * @param q2 the second {@link MyPoint} to compare
	 * @return true if both x and y differ by at most {@value #COORDINATE_DELTA}
	 */
	protected boolean equal(MyPoint q1, MyPoint q2) {
		return DoubleUtil.isEqual(q1.x, q2.x, COORDINATE_DELTA)
				&& DoubleUtil.isEqual(q1.y, q2.y, COORDINATE_DELTA);
	}
}
