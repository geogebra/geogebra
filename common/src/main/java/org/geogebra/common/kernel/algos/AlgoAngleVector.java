/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for Angle(vector)
 */
public class AlgoAngleVector extends AlgoAngleVectorND {

	private double[] coords;

	/**
	 * @param cons
	 *            construction
	 * @param vec
	 *            vector
	 */
	public AlgoAngleVector(Construction cons, GeoVec3D vec) {
		super(cons, vec);
	}

	/**
	 * @return vector
	 */
	public GeoVec3D getVec3D() {
		return (GeoVec3D) vec;
	}

	@Override
	public final void compute() {
		if (coords == null) {
			coords = new double[2];
		}
		if (vec instanceof GeoPoint && ((GeoPoint) vec).getZ() == 0) {
			coords[0] = ((GeoPoint) vec).getX();
			coords[1] = ((GeoPoint) vec).getY();
		} else {
			((GeoVec3D) vec).getInhomCoords(coords);
		}
		angle.setValue(Math.atan2(coords[1], coords[0]));
	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {
		if (vec.isGeoVector()) {
			GeoPointND vertex = getStartPoint((GeoVector) vec);
			if (vertex != null) {
				vertex.getInhomCoords(m);
			}
			return vertex != null && vertex.isDefined() && !vertex.isInfinite();
		}
		m[0] = 0;
		m[1] = 0;
		return vec.isDefined();
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {

		if (vec.isGeoVector()) {
			GeoPointND vertex = getStartPoint((GeoVectorND) vec);
			if (centerIsNotDrawable(vertex)) {
				return false;
			}
			drawCoords[0] = vertex.getInhomCoordsInD3();
			drawCoords[2] = ((GeoVector) vec).getCoordsInD3();
		} else {
			drawCoords[0] = Coords.O;
			drawCoords[2] = ((GeoPoint) vec).getCoordsInD3();
			drawCoords[2].setW(0);
		}

		drawCoords[1] = Coords.VX;

		return true;
	}

}
