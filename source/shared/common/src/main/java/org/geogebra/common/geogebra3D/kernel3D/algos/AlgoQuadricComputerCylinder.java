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
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * for cylinders
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCylinder extends AlgoQuadricComputer {

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, Coords eigen, double r, double r2) {
		quadric.setCylinder(origin, direction.normalize(), eigen, r, r2);

	}

	@Override
	public double getNumber(double v) {

		if (DoubleUtil.isZero(v)) {
			return 0;
		} else if (v < 0) {
			return Double.NaN;
		}

		return v;
	}

}
