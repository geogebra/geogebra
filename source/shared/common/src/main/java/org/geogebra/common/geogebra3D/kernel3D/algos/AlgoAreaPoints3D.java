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
