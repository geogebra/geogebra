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

import static org.geogebra.common.util.DoubleUtil.isEqual;

/**
 * Rectangle cell used by marching/contouring algorithms.
 * <p>
 * Exposes the cell bounds in world coordinates and the scalar samples at
 * its four corners. Implementations provide the values consumed by the
 * marching configuration logic.
 * </p>
 */
public interface MarchingRect {

	/**
	 * Minimum x-coordinate (left edge) of this cell.
	 *
	 * @return left x value
	 */
	double x1();

	/**
	 * Minimum y-coordinate (bottom edge) of this cell.
	 *
	 * @return bottom y value
	 */
	double y1();

	/**
	 * Maximum x-coordinate (right edge) of this cell.
	 *
	 * @return right x value
	 */
	double x2();

	/**
	 * Maximum y-coordinate (top edge) of this cell.
	 *
	 * @return top y value
	 */
	double y2();

	/**
	 * Scalar sample at the top-left corner, i.e., at {@code (x1(), y2())}.
	 *
	 * @return field value at top-left
	 */
	double topLeft();

	/**
	 * Scalar sample at the top-right corner, i.e., at {@code (x2(), y2())}.
	 *
	 * @return field value at top-right
	 */
	double topRight();

	/**
	 * Scalar sample at the bottom-left corner, i.e., at {@code (x1(), y1())}.
	 *
	 * @return field value at bottom-left
	 */
	double bottomLeft();

	/**
	 * Scalar sample at the bottom-right corner, i.e., at {@code (x2(), y1())}.
	 *
	 * @return field value at bottom-right
	 */
	double bottomRight();

	/**
	 * Scalar sample at a corner by index.
	 * <p>
	 * Corner order follows the usual marching convention:
	 * {@code 0=top-left, 1=top-right, 2=bottom-right, 3=bottom-left}.
	 * </p>
	 *
	 * @param i corner index in {@code [0,3]}
	 * @return field value at the indexed corner
	 */
	double cornerAt(int i);

	/**
	 * Returns whether the given bounds match this cell's bounds within
	 * the tolerance of {@code DoubleUtil.isEqual}.
	 *
	 * @param x1 expected left x
	 * @param y1 expected bottom y
	 * @param x2 expected right x
	 * @param y2 expected top y
	 * @return {@code true} if all coordinates are equal within tolerance
	 */
	default boolean same(double x1, double y1, double x2, double y2) {
		return isEqual(x1, x1()) && isEqual(x2, x2())
				&& isEqual(y1, y1()) && isEqual(y2, y2());
	}
}
