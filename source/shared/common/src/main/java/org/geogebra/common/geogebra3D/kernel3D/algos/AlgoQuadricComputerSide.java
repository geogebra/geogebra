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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * for open cylinders
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerSide extends AlgoQuadricComputer {

	@Override
	public GeoQuadric3D newQuadric(Construction c) {
		return new GeoQuadric3DPart(c);
	}

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, Coords eigen, double r, double r2) {

		quadric.set(origin, direction, eigen, r, r2);

	}

	@Override
	public double getNumber(double v) {
		return 0;
	}

}
