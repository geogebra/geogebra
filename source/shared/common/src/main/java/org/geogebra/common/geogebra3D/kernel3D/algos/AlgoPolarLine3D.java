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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPolarLineND;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author mathieu
 */
public class AlgoPolarLine3D extends AlgoPolarLineND {

	private GeoLine polar2D;

	private Coords coords2D;

	private double[] polarCoords;

	private Coords polarOrigin;
	private Coords polarDirection;

	/**
	 * Creates new AlgoPolarLine
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param c
	 *            conic
	 * @param P
	 *            polar point
	 */
	public AlgoPolarLine3D(Construction cons, String label, GeoConicND c,
			GeoPointND P) {
		super(cons, label, c, P);
	}

	@Override
	protected GeoLineND newGeoLine(Construction cons1) {

		// we also need this
		polar2D = new GeoLine(cons1);
		polarCoords = new double[3];

		// create 3D line
		return new GeoLine3D(cons1);
	}

	// calc polar line of P relative to c
	@Override
	public final void compute() {

		// check if point lies on conic coord sys
		coords2D = c.getCoordSys()
				.getNormalProjection(P.getInhomCoordsInD3())[1];
		if (!DoubleUtil.isZero(coords2D.getZ())) {
			polar.setUndefined();
			return;
		}

		// now it's a 2D point in coord sys
		coords2D.setZ(1);

		// update polar line in conic coord sys
		c.polarLine(coords2D, polar2D);

		// update 3D polar
		polar2D.getCoords(polarCoords);
		polarDirection = c.getCoordSys().getVector(-polarCoords[1],
				polarCoords[0]);
		if (DoubleUtil.isZero(polarCoords[0])) {
			polarOrigin = c.getCoordSys().getPoint(0,
					-polarCoords[2] / polarCoords[1]);
		} else {
			polarOrigin = c.getCoordSys()
					.getPoint(-polarCoords[2] / polarCoords[0], 0);
		}

		((GeoLine3D) polar).setCoord(polarOrigin, polarDirection);

	}

}
