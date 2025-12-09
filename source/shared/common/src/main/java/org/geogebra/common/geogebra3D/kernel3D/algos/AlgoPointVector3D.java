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
import org.geogebra.common.kernel.algos.AlgoPointVectorND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

public class AlgoPointVector3D extends AlgoPointVectorND {
	private Coords tmpCoords;

	public AlgoPointVector3D(Construction cons, GeoPointND P, GeoVectorND v) {
		super(cons, P, v);
	}

	@Override
	public final void compute() {
		tmpCoords.setAdd3(P.getInhomCoordsInD3(), v.getCoordsInD3());
		Q.setCoords(tmpCoords, false);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		// create tmpCoords here to avoid null at first compute
		tmpCoords = Coords.createInhomCoorsInD3();
		return new GeoPoint3D(cons1);
	}

}
