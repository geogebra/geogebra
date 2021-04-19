/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algorithm for coefficients of a plane
 */
public class AlgoPlaneCoefficients extends AlgoEquationCoefficients {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param plane
	 *            plane
	 */
	public AlgoPlaneCoefficients(Construction cons, String label,
			GeoPlaneND plane) {
		super(cons, label, plane, 4);
	}

	@Override
	public final void extractCoefficients() {
		Coords matrix = ((GeoPlaneND) eqn).getCoordSys().getEquationVector();
		for (int i = 0; i < 4; i++) {
			setCoeff(i, matrix.get(i + 1));
		}
	}

}
