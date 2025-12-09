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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPolarPointND;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author mathieu (adapted from AlgoPolarLine3D)
 */
public class AlgoPolarPoint3D extends AlgoPolarPointND {

	private GeoPoint polar2D;

	private Coords equation2D;

	private double[] polarCoords;

	/**
	 * Creates new AlgoPolarLine
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param c
	 *            conic
	 * @param line
	 *            polar line
	 */
	public AlgoPolarPoint3D(Construction cons, String label, GeoConicND c,
			GeoLineND line) {
		super(cons, label, c, line);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {

		// we also need this
		polar2D = new GeoPoint(cons1);
		polarCoords = new double[3];

		// create 3D line
		return new GeoPoint3D(cons1);
	}

	// calc polar line of P relative to c
	@Override
	public final void compute() {

		// check if line lies on conic coord sys
		equation2D = line.getCartesianEquationVector(
				c.getCoordSys().getMatrixOrthonormal());
		if (equation2D == null) {
			polar.setUndefined();
			return;
		}

		// update polar point in conic coord sys
		c.polarPoint(equation2D, polar2D);

		polar2D.getCoords(polarCoords);

		// update 3D polar
		((GeoPoint3D) polar).setCoords(c.getCoordSys().getPoint(polarCoords[0],
				polarCoords[1], polarCoords[2]));

	}

}
