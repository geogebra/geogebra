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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

public class AlgoPointVector extends AlgoPointVectorND {

	public AlgoPointVector(Construction cons, GeoPointND P, GeoVectorND v) {
		super(cons, P, v);
	}

	@Override
	public final void compute() {
		Q.setCoords(((GeoPoint) P).inhomX + ((GeoVector) v).x,
				((GeoPoint) P).inhomY + ((GeoVector) v).y, 1.0);
	}

	@Override
	protected GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint(cons1);
	}

}
