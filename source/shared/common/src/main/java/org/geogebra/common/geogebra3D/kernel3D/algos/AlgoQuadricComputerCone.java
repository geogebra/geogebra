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
 * for cones
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCone extends AlgoQuadricComputer {

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, Coords eigen, double r, double r2) {
		quadric.setCone(origin, direction.normalize(), eigen, r, r2);

	}

	@Override
	public double getNumber(double v) {
		double c = Math.cos(v);
		double s = Math.sin(v);

		if (c < 0 || s < 0) {
			return Double.NaN;
		} else if (DoubleUtil.isZero(c)) { // TODO if c=0 then draws a plane
			return Double.NaN;
		} else if (DoubleUtil.isZero(s)) { // TODO if s=0 then draws a line
			return Double.NaN;
		}

		return s / c;
	}
}
