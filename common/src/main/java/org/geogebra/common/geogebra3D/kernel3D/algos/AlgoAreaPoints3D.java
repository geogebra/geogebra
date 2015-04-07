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
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAreaPoints;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoAreaPoints3D extends AlgoAreaPoints {

	public AlgoAreaPoints3D(Construction cons, String label, GeoPointND[] P) {
		super(cons, label, P);
	}

	private CoordSys coordSys;
	private GeoPoint[] points2D;

	@Override
	protected void createOutput(Construction cons) {
		super.createOutput(cons);
		coordSys = new CoordSys(2);
		points2D = new GeoPoint[P.length];
		for (int i = 0; i < P.length; i++) {
			points2D[i] = new GeoPoint(cons, true);
		}
	}

	private Coords tmpCoords;

	@Override
	protected void initCoords() {
		tmpCoords = new Coords(4);
	}

	@Override
	public final void compute() {
		if (GeoPolygon3D.updateCoordSys(coordSys, P, points2D, tmpCoords)) {
			area.setValue(Math.abs(AlgoPolygon.calcAreaWithSign(points2D)));
		} else {
			area.setUndefined();
		}
	}

}
