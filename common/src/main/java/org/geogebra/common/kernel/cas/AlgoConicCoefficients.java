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
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Algorithm for coefficients of a conic
 * 
 * @author Michael Borcherds
 */
public class AlgoConicCoefficients extends AlgoElement {

	private GeoConicND c; // input
	private GeoList g; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param c
	 *            conic
	 */
	public AlgoConicCoefficients(Construction cons, String label, GeoConicND c) {
		super(cons);
		this.c = c;

		g = new GeoList(cons);
		double[] matrix = c.getFlatMatrix();
		g.add(new GeoNumeric(cons, matrix[0]));
		g.add(new GeoNumeric(cons, matrix[1]));
		g.add(new GeoNumeric(cons, matrix[2]));
		g.add(new GeoNumeric(cons, matrix[3] * 2));
		g.add(new GeoNumeric(cons, matrix[4] * 2));
		g.add(new GeoNumeric(cons, matrix[5] * 2));

		setInputOutput(); // for AlgoElement
		// compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Coefficients;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		super.setOutputLength(1);
		super.setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting list of coefficients
	 */
	public GeoList getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!c.isDefined()) {
			g.setUndefined();
			return;
		}

		double[] matrix = c.getFlatMatrix();
		((GeoNumeric) g.get(0)).setValue(matrix[0]);
		((GeoNumeric) g.get(1)).setValue(matrix[1]);
		((GeoNumeric) g.get(2)).setValue(matrix[2]);
		((GeoNumeric) g.get(3)).setValue(matrix[3] * 2);
		((GeoNumeric) g.get(4)).setValue(matrix[4] * 2);
		((GeoNumeric) g.get(5)).setValue(matrix[5] * 2);

	}

	// TODO Consider locusequability

}
