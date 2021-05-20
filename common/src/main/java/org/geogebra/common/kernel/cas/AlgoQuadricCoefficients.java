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
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * Algorithm for coefficients of a quadric
 * 
 * @author Michael Borcherds
 */
public class AlgoQuadricCoefficients extends AlgoEquationCoefficients {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param c
	 *            quadric
	 */
	public AlgoQuadricCoefficients(Construction cons, String label,
			GeoQuadricND c) {
		super(cons, label, c, 10);
	}

	@Override
	public final void extractCoefficients() {
		double[] matrix = ((GeoQuadricND) eqn).getFlatMatrix();
		for (int i = 0; i < 10; i++) {
			setCoeff(i, i < 4 ? matrix[i] : matrix[i] * 2);
		}
	}

}
