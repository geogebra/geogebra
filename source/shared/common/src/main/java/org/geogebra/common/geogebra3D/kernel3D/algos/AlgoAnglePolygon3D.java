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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAnglePointsND;
import org.geogebra.common.kernel.algos.AlgoAnglePolygonND;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;

/**
 *
 * @author mathieu
 */
public class AlgoAnglePolygon3D extends AlgoAnglePolygonND {

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 */
	public AlgoAnglePolygon3D(Construction cons, String[] labels,
			GeoPolygon poly) {
		this(cons, labels, poly, false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @param internalAngle
	 *            whether to return internal angles
	 */
	public AlgoAnglePolygon3D(Construction cons, String[] labels, GeoPolygon poly,
			boolean internalAngle) {
		this(cons, labels, poly, null, internalAngle);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param poly
	 *            polygon
	 * @param orientation
	 *            orientation
	 * @param internalAngle
	 *            if angles are internal
	 */
	public AlgoAnglePolygon3D(Construction cons, String[] labels,
			GeoPolygon poly, GeoDirectionND orientation, boolean internalAngle) {
		super(cons, labels, poly, orientation, internalAngle);
	}

	@Override
	protected AlgoAnglePointsND newAlgoAnglePoints(Construction cons1) {
		return new AlgoAnglePoints3DOrientation(cons1, getPolygon(),
				getPolygon().hasReverseNormal());
	}

	@Override
	final protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}

}
