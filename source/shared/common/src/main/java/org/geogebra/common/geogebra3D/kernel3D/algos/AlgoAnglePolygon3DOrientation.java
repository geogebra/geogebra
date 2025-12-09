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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAnglePointsND;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePolygon3DOrientation extends AlgoAnglePolygon3D {

	private GeoDirectionND orientation;

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @param orientation
	 *            orientation to determine clockwise
	 */
	public AlgoAnglePolygon3DOrientation(Construction cons, String[] labels,
			GeoPolygon poly, GeoDirectionND orientation) {
		super(cons, labels, poly, orientation, false);
	}

	@Override
	protected AlgoAnglePointsND newAlgoAnglePoints(Construction cons1) {
		return new AlgoAnglePoints3DOrientation(cons1, orientation, false);
	}

	@Override
	protected void setPolyAndOrientation(GeoPolygon p,
			GeoDirectionND orientation) {
		super.setPolyAndOrientation(p, orientation);
		this.orientation = orientation;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = poly;
		input[1] = (GeoElement) orientation;

		setDependencies();
	}
}
