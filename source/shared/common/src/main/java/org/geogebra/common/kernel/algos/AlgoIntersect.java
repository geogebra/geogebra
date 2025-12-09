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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;

public abstract class AlgoIntersect extends AlgoIntersectND {

	public AlgoIntersect(Construction c) {
		super(c);
	}

	public AlgoIntersect(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}

	/**
	 * Avoids two intersection points at same position. This is only done as
	 * long as the second intersection point doesn't have a label yet.
	 */
	@Override
	protected void avoidDoubleTangentPoint() {
		GeoPoint[] points = getIntersectionPoints();
		if (!points[1].isLabelSet() && points[0].isEqual(points[1])) {
			points[1].setUndefined();
		}
	}

	/**
	 * Returns the index in output[] of the intersection point that is closest
	 * to the coordinates (xRW, yRW)
	 * 
	 * @param xRW
	 *            initial x-coordinate
	 * @param yRW
	 *            initial y-coordinate
	 * @return closest intersection index
	 */
	public int getClosestPointIndex(double xRW, double yRW) {
		GeoPoint[] P = getIntersectionPoints();
		double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			x = P[i].inhomX - xRW;
			y = P[i].inhomY - yRW;
			lengthSqr = x * x + y * y;
			if (lengthSqr < mindist) {
				mindist = lengthSqr;
				minIndex = i;
			}
		}
		return minIndex;
	}

	/**
	 * Returns the index in output[] of the intersection point that is closest
	 * to the GeoPoint refPoint
	 * 
	 * @param refPoint
	 *            initial point
	 * @return closest intersection point
	 */
	int getClosestPointIndex(GeoPoint refPoint) {
		GeoPoint[] P = getIntersectionPoints();
		double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			x = P[i].inhomX - refPoint.getInhomX();
			y = P[i].inhomY - refPoint.getInhomY();
			lengthSqr = x * x + y * y;
			// if two distances are equal, smaller index gets priority
			if (DoubleUtil.isGreater(mindist, lengthSqr)) {
				mindist = lengthSqr;
				minIndex = i;
			}
		}

		return minIndex;
	}

	@Override
	public abstract GeoPoint[] getIntersectionPoints();

	/**
	 * Implemented for LocusEqu.
	 * 
	 * @return a new array with the exact same members as getIntersectionPoints.
	 */
	public GeoPoint[] getCopyOfIntersectionPoints() {
		GeoPoint[] orig = this.getIntersectionPoints();
		GeoPoint[] copy = new GeoPoint[orig.length];
		System.arraycopy(orig, 0, copy, 0, copy.length);
		return copy;
	}

	@Override
	protected abstract GeoPoint[] getLastDefinedIntersectionPoints();

	@Override
	protected void setCoords(GeoPointND destination, GeoPointND source) {
		((GeoPoint) destination).setCoords((GeoPoint) source);
	}
}
