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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.AlgoIntersectND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public abstract class AlgoIntersect3D extends AlgoIntersectND {

	// gives the number of intersection algorithms
	// this algorithm is used by: see AlgoIntersectSingle
	private int numberOfUsers = 0;

	// used in setIntersectionPoint to remember all indices that have been set
	private boolean[] didSetIntersectionPoint;

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
		// Application.debug("\nxRW="+xRW+"\nyRW="+yRW+"\nmatrix=\n"+mat);
		int minIndex = 0;
		for (int i = 0; i < P.length; i++) {
			Coords toScreenCoords;
			if (mat == null)
				toScreenCoords = P[i].getInhomCoords();
			else
				toScreenCoords = mat.mul(P[i].getCoords().getCoordsLast1())
						.getInhomCoords();
			// Application.debug("\nScreen coords of point "+i+" is:\n"+toScreenCoords);
			x = (toScreenCoords.getX() - xRW);
			y = (toScreenCoords.getY() - yRW);
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

			x = (PInhom.getX() - refInhom.getX());
			y = (PInhom.getY() - refInhom.getY());
			z = (PInhom.getZ() - refInhom.getZ());
			lengthSqr = x * x + y * y + z * z;
			// if two distances are equal, smaller index gets priority
			if (Kernel.isGreater(mindist, lengthSqr)) {
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
