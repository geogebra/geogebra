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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoTranslateVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Vector w = v starting at A
 * 
 * @author mathieu
 *
 */
public class AlgoTranslateVector3D extends AlgoTranslateVector {

	/**
	 * Constructor
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param v
	 *            input vector
	 * @param A
	 *            starting point
	 */
	public AlgoTranslateVector3D(Construction cons, String label, GeoVectorND v,
			GeoPointND A) {
		super(cons, label, v, A);
	}

	@Override
	protected GeoVectorND newGeoVector(Construction cons1) {
		return new GeoVector3D(cons1);
	}

	@Override
	public void compute() {
		((GeoVector3D) w).setCoords(v.getCoordsInD3());
	}

}
