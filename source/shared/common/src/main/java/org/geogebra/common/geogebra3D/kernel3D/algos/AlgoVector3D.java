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
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Vector between two points P and Q. Extends AlgoVector
 * 
 * @author ggb3D
 */

public class AlgoVector3D extends AlgoVector {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 */
	public AlgoVector3D(Construction cons, GeoPointND P,
			GeoPointND Q) {
		super(cons, P, Q);
	}

	@Override
	protected GeoVectorND createNewVector() {

		return new GeoVector3D(cons);

	}

	@Override
	protected GeoPointND newStartPoint() {

		return new GeoPoint3D(getP());

	}

	@Override
	protected void setCoords() {
		getVector().setCoords(getQ().getInhomCoordsInD3()
				.sub(getP().getInhomCoordsInD3()).get());
	}

}
