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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author mathieu
 */
public class AlgoAngleConic3D extends AlgoAngleElement3D {

	private Coords o;

	/**
	 * @param cons
	 *            construction
	 * @param vec
	 *            vector
	 */
	public AlgoAngleConic3D(Construction cons, GeoConic3D vec) {
		super(cons, vec);
	}

	@Override
	protected final Coords getVectorCoords() {

		Coords v = ((GeoConic3D) vec).getEigenvec3D(0).copyVector();
		return v;
	}

	@Override
	protected final Coords getOrigin() {
		return o;
	}

	@Override
	protected final void setOrigin() {
		o = ((GeoConic3D) vec).getMidpoint3D();
	}

}
