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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * Algorithm for coefficients of a quadric
 * 
 * @author Michael Borcherds
 */
public class AlgoQuadricCoefficients extends AlgoConicCoefficients {

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
		super(cons);
		this.c = c;

		// matrix =
		// ( A[0] A[4] A[5] A[7])
		// ( A[4] A[1] A[6] A[8])
		// ( A[5] A[6] A[2] A[9])
		// ( A[7] A[8] A[9] A[3])

		g = new GeoList(cons);
		double[] matrix = c.getFlatMatrix();
		g.add(new GeoNumeric(cons, matrix[0]));
		g.add(new GeoNumeric(cons, matrix[1]));
		g.add(new GeoNumeric(cons, matrix[2]));
		g.add(new GeoNumeric(cons, matrix[3]));
		g.add(new GeoNumeric(cons, matrix[4] * 2));
		g.add(new GeoNumeric(cons, matrix[5] * 2));
		g.add(new GeoNumeric(cons, matrix[6] * 2));
		g.add(new GeoNumeric(cons, matrix[7] * 2));
		g.add(new GeoNumeric(cons, matrix[8] * 2));
		g.add(new GeoNumeric(cons, matrix[9] * 2));

		setInputOutput(); // for AlgoElement
		// compute();
		g.setLabel(label);
	}

	@Override
	public final void compute() {
		if (!c.isDefined()) {
			g.setUndefined();
			return;
		}

		double[] matrix = c.getFlatMatrix();
		g.setDefined(true);
		((GeoNumeric) g.get(0)).setValue(matrix[0]);
		((GeoNumeric) g.get(1)).setValue(matrix[1]);
		((GeoNumeric) g.get(2)).setValue(matrix[2]);
		((GeoNumeric) g.get(3)).setValue(matrix[3]);
		((GeoNumeric) g.get(4)).setValue(matrix[4] * 2);
		((GeoNumeric) g.get(5)).setValue(matrix[5] * 2);
		((GeoNumeric) g.get(6)).setValue(matrix[6] * 2);
		((GeoNumeric) g.get(7)).setValue(matrix[7] * 2);
		((GeoNumeric) g.get(8)).setValue(matrix[8] * 2);
		((GeoNumeric) g.get(9)).setValue(matrix[9] * 2);

	}

}
