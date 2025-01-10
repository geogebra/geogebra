/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAreaPoints;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;

/**
 * 3D polygon area
 *
 */
public class AlgoAreaPoints3D extends AlgoAreaPoints {

	private CoordSys coordSys;
	private GeoPoint[] points2D;
	private double[] tmpCoords;

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            polygon vertices
	 */
	public AlgoAreaPoints3D(Construction cons, GeoPointND[] P) {
		super(cons, P);
	}

	@Override
	protected void createOutput(Construction cons1) {
		super.createOutput(cons1);
		coordSys = new CoordSys(2);
		points2D = new GeoPoint[P.length];
		for (int i = 0; i < P.length; i++) {
			points2D[i] = new GeoPoint(cons1, true);
		}
	}

	@Override
	protected void initCoords() {
		tmpCoords = new double[4];
	}

	@Override
	public final void compute() {
		if (GeoPolygon3D.updateCoordSys(coordSys, P, points2D, tmpCoords,
				kernel.getStandardPrecision())) {
			area.setValue(Math.abs(AlgoPolygon.calcAreaWithSign(points2D)));
		} else {
			area.setUndefined();
		}
	}

}
