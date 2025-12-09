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
import org.geogebra.common.kernel.advanced.AlgoAxis;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 * Major axis
 */
public class AlgoAxis3D extends AlgoAxis {
	private GeoLine3D axis; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 * @param axisId
	 *            0 for major, 1 for minor
	 */
	public AlgoAxis3D(Construction cons, String label, GeoConicND c,
			int axisId) {
		super(cons, c, axisId);
		axis = new GeoLine3D(cons);
		finishSetup(label);
	}

	@Override
	public final void compute() {
		// axes are lines with directions of eigenvectors
		// through midpoint b
		axis.setCoord(getConic().getMidpoint3D(),
				getConic().getEigenvec3D(axisId));
		P.setCoords(getConic().getMidpoint3D(), false);
	}

	@Override
	public GeoLineND getAxis() {
		return axis;
	}

}
