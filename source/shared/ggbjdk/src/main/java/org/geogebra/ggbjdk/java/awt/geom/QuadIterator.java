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

package org.geogebra.ggbjdk.java.awt.geom;

import java.util.NoSuchElementException;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;

/**
 * A utility class to iterate over the path segments of a quadratic curve
 * segment through the PathIterator interface.
 *
 * @author      Jim Graham
 */
class QuadIterator implements GPathIterator {
	QuadCurve2D quad;
	GAffineTransform affine;
	int index;

	QuadIterator(QuadCurve2D q, GAffineTransform at) {
		this.quad = q;
		this.affine = at;
	}

	/**
	 * Return the winding rule for determining the insideness of the
	 * path.
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	@Override
	public int getWindingRule() {
		return WIND_NON_ZERO;
	}

	/**
	 * Tests if there are more points to read.
	 * @return true if there are more points to read
	 */
	@Override
	public boolean isDone() {
		return (index > 1);
	}

	/**
	 * Moves the iterator to the next segment of the path forwards
	 * along the primary direction of traversal as long as there are
	 * more points in that direction.
	 */
	@Override
	public void next() {
		index++;
	}

	/**
	 * Returns the coordinates and type of the current path segment in
	 * the iteration.
	 * The return value is the path segment type:
	 * SEG_MOVETO, SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE.
	 * A double array of length 6 must be passed in and may be used to
	 * store the coordinates of the point(s).
	 * Each point is stored as a pair of double x,y coordinates.
	 * SEG_MOVETO and SEG_LINETO types will return one point,
	 * SEG_QUADTO will return two points,
	 * SEG_CUBICTO will return 3 points
	 * and SEG_CLOSE will not return any points.
	 * @see #SEG_MOVETO
	 * @see #SEG_LINETO
	 * @see #SEG_QUADTO
	 * @see #SEG_CUBICTO
	 * @see #SEG_CLOSE
	 */
	@Override
	public int currentSegment(double[] coords) {
		if (isDone()) {
			throw new NoSuchElementException("quad iterator iterator out of bounds");
		}
		int type;
		if (index == 0) {
			coords[0] = quad.getX1();
			coords[1] = quad.getY1();
			type = SEG_MOVETO;
		} else {
			coords[0] = quad.getCtrlX();
			coords[1] = quad.getCtrlY();
			coords[2] = quad.getX2();
			coords[3] = quad.getY2();
			type = SEG_QUADTO;
		}
		if (affine != null) {
			affine.transform(coords, 0, coords, 0, index == 0 ? 1 : 2);
		}
		return type;
	}
}
