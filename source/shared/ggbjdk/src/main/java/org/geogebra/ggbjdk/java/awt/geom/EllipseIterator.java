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
 * A utility class to iterate over the path segments of an ellipse
 * through the PathIterator interface.
 *
 * @author      Jim Graham
 */
class EllipseIterator implements GPathIterator {
	double x, y, w, h;
	GAffineTransform affine;
	int index;

	EllipseIterator(Ellipse2D e, GAffineTransform at) {
		this.x = e.getX();
		this.y = e.getY();
		this.w = e.getWidth();
		this.h = e.getHeight();
		this.affine = at;
		if (w < 0 || h < 0) {
			index = 6;
		}
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
		return index > 5;
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

	// ArcIterator.btan(Math.PI/2)
	public static final double CtrlVal = 0.5522847498307933;

	/*
	 * ctrlpts contains the control points for a set of 4 cubic
	 * bezier curves that approximate a circle of radius 0.5
	 * centered at 0.5, 0.5
	 */
	private static final double pcv = 0.5 + CtrlVal * 0.5;
	private static final double ncv = 0.5 - CtrlVal * 0.5;
	private static double ctrlpts[][] = {
		{1.0, pcv, pcv, 1.0, 0.5, 1.0},
		{ncv, 1.0, 0.0, pcv, 0.0, 0.5},
		{0.0, ncv, ncv, 0.0, 0.5, 0.0},
		{pcv, 0.0, 1.0, ncv, 1.0, 0.5}
	};

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
			throw new NoSuchElementException("ellipse iterator out of bounds");
		}
		if (index == 5) {
			return SEG_CLOSE;
		}
		if (index == 0) {
			double ctrls[] = ctrlpts[3];
			coords[0] = x + ctrls[4] * w;
			coords[1] = y + ctrls[5] * h;
			if (affine != null) {
				affine.transform(coords, 0, coords, 0, 1);
			}
			return SEG_MOVETO;
		}
		double ctrls[] = ctrlpts[index - 1];
		coords[0] = x + ctrls[0] * w;
		coords[1] = y + ctrls[1] * h;
		coords[2] = x + ctrls[2] * w;
		coords[3] = y + ctrls[3] * h;
		coords[4] = x + ctrls[4] * w;
		coords[5] = y + ctrls[5] * h;
		if (affine != null) {
			affine.transform(coords, 0, coords, 0, 3);
		}
		return SEG_CUBICTO;
	}
}
