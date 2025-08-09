/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Common class for 3D intersect algos.
 */
public abstract class AlgoIntersect3D extends AlgoIntersectND {

	/**
	 * @param c
	 *            construction
	 */
	public AlgoIntersect3D(Construction c) {
		super(c);
	}

	/**
	 * Avoids two intersection points at same position. This is only done as
	 * long as the second intersection point doesn't have a label yet.
	 */
	@Override
	protected void avoidDoubleTangentPoint() {
		GeoPoint3D[] points = getIntersectionPoints();
		if (!points[1].isLabelSet() && points[0].isEqual(points[1])) {
			points[1].setUndefined();
		}
	}

	/**
	 * Returns the index in output[] of the intersection point that is closest
	 * to the coordinates (xRW, yRW) TODO: move to an interface
	 */
	int getClosestPointIndex(double xRW, double yRW, CoordMatrix mat) {
		GeoPoint3D[] P = getIntersectionPoints();
		double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			Coords toScreenCoords;
			if (mat == null) {
				toScreenCoords = P[i].getInhomCoords();
			} else {
				toScreenCoords = mat.mul(P[i].getCoords().getCoordsLast1())
						.getInhomCoords();
			}
			x = toScreenCoords.getX() - xRW;
			y = toScreenCoords.getY() - yRW;
			// comment: the z dimension is the "height", which will not be used
			// here.
			lengthSqr = x * x + y * y;
			if (lengthSqr < mindist) {
				mindist = lengthSqr;
				minIndex = i;
			}
		}

		return minIndex;
	}

	/**
	 * 
	 * @param origin
	 *            line origin
	 * @param direction
	 *            line direction
	 * @return index of the intersection point which is the closest to the line
	 */
	int getClosestPointIndex(Coords origin, Coords direction) {
		GeoPoint3D[] P = getIntersectionPoints();
		double dist, mindist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			dist = P[i].getInhomCoords().distLine(origin, direction);
			if (dist < mindist) {
				mindist = dist;
				minIndex = i;
			}
		}
		return minIndex;
	}

	@Override
	public abstract GeoPoint3D[] getIntersectionPoints();

	@Override
	protected abstract GeoPoint3D[] getLastDefinedIntersectionPoints();

	int getClosestPointIndex(GeoPointND refPoint) {
		Coords refInhom = refPoint.getInhomCoordsInD3();
		GeoPoint3D[] P = getIntersectionPoints();
		double x, y, z, lengthSqr, mindist = Double.POSITIVE_INFINITY;
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			Coords PInhom = P[i].getInhomCoordsInD3();

			x = PInhom.getX() - refInhom.getX();
			y = PInhom.getY() - refInhom.getY();
			z = PInhom.getZ() - refInhom.getZ();
			lengthSqr = x * x + y * y + z * z;
			// if two distances are equal, smaller index gets priority
			if (DoubleUtil.isGreater(mindist, lengthSqr)) {
				mindist = lengthSqr;
				minIndex = i;
			}
		}

		return minIndex;
	}

	// TODO: organize better according to types: GeoVec, GeoVec4D, GeoVec3D,
	// Coords
	@Override
	protected void setCoords(GeoPointND destination, GeoPointND source) {
		((GeoPoint3D) destination).setCoords(((GeoPoint3D) source).getCoords());
	}

	@Override
	public abstract void compute();

	@Override
	public abstract void initForNearToRelationship();
}
