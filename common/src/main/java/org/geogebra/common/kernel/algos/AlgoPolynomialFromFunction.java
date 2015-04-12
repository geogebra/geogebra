/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Try to expand the given function to a polynomial.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolynomialFromFunction extends AlgoElement {

	private GeoFunction f; // input
	private GeoFunction g; // output

	public AlgoPolynomialFromFunction(Construction cons, String label,
			GeoFunction f) {
		super(cons);
		this.f = f;

		g = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Polynomial;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		super.setOutputLength(1);
		super.setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	public GeoFunction getPolynomial() {
		return g;
	}

	// ON CHANGE: similar code is in AlgoTaylorSeries
	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}

		Function inFun = f.getFunction();

		// check if it's a polynomial & get coefficients
		PolyFunction poly = inFun.expandToPolyFunction(inFun.getExpression(),
				false, false);

		if (poly == null) {
			g.setDefined(false);
			return;
		}

		double[] coeffs = poly.getCoeffs();

		Function polyFun = AlgoPolynomialFromCoordinates
				.buildPolyFunctionExpression(kernel, coeffs);

		if (polyFun == null) {
			g.setUndefined();
			return;
		}

		g.setFunction(polyFun);
		g.setDefined(true);
	}


	// TODO Consider locusequability

}
