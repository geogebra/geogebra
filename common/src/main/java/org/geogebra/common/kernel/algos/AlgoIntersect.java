/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public abstract class AlgoIntersect extends AlgoIntersectND {

	public AlgoIntersect(Construction c) {
		super(c);
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
	 */
	public int getClosestPointIndex(double xRW, double yRW) {
		// AbstractApplication.debug("\nxRW="+xRW+"\nyRW="+yRW);
		GeoPoint[] P = getIntersectionPoints();
		double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			// AbstractApplication.debug("\npoint "+i+":\nx="+P[i].inhomX+"\ny="+P[i].inhomY);
			x = (P[i].inhomX - xRW);
			y = (P[i].inhomY - yRW);
			lengthSqr = x * x + y * y;
			if (lengthSqr < mindist) {
				mindist = lengthSqr;
				minIndex = i;
			}
		}
		// AbstractApplication.debug("\nminIndex="+minIndex);
		return minIndex;
	}

	/**
	 * Returns the index in output[] of the intersection point that is closest
	 * to the GeoPoint refPoint
	 */
	int getClosestPointIndex(GeoPoint refPoint) {
		GeoPoint[] P = getIntersectionPoints();
		double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			x = (P[i].inhomX - refPoint.getInhomX());
			y = (P[i].inhomY - refPoint.getInhomY());
			lengthSqr = x * x + y * y;
			// if two distances are equal, smaller index gets priority
			if (Kernel.isGreater(mindist, lengthSqr)) {
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
