/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Polynomial remainder
 * 
 * @author Michael Borcherds
 */
public class AlgoPolynomialDivision extends AlgoElement {

	private GeoFunction f1; // input
	private GeoFunction f2; // input
	private GeoFunction g; // output
	private GeoFunction h; // output
	private GeoList result;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f1
	 *            divided function
	 * @param f2
	 *            divisor function
	 */
	public AlgoPolynomialDivision(Construction cons, String label,
			GeoFunction f1, GeoFunction f2) {
		super(cons);
		this.f1 = f1;
		this.f2 = f2;

		g = new GeoFunction(cons);
		h = new GeoFunction(cons);
		result = new GeoList(cons);
		result.add(g);
		result.add(h);
		setInputOutput(); // for AlgoElement
		compute();

		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Division;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = f1;
		input[1] = f2;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return list {div,mod}
	 */
	public GeoList getResult() {
		return result;
	}

	@Override
	public final void compute() {
		if (!f1.isDefined() || !f2.isDefined()) {
			result.setUndefined();
			return;
		}

		nonCASDivision(kernel, f1, f2, g, h);

	}

	/**
	 * divide f1 by f2 and return result in g = div, h = mod
	 * 
	 * @param kernel
	 *            kernel
	 * @param f1
	 *            firs polynomial
	 * @param f2
	 *            second polynomial
	 * @param g
	 *            div result (or null)
	 * @param h
	 *            mod result (or null)
	 */
	public static void nonCASDivision(Kernel kernel, GeoFunction f1,
			GeoFunction f2, GeoFunction g, GeoFunction h) {
		Function fun1 = f1.getFunction();
		Function fun2 = f2.getFunction();

		PolyFunction poly1 = fun1.expandToPolyFunction(
				fun1.getFunctionExpression(), false, true);
		PolyFunction poly2 = fun2.expandToPolyFunction(
				fun2.getFunctionExpression(), false, true);
		if (poly1 == null || poly2 == null) {
			setUndefined(g, h);
			return;
		}
		double[] dividend = poly1.getCoeffs();
		int m = dividend.length;
		double[] divisor = poly2.getCoeffs();
		int n = divisor.length;

		if (m < n) {
			setFunction(g, new double[] { 0 });

			if (h != null) {
				h.set(f1);
			}
			return;
		}

		double[] result = Cloner.clone(dividend);
		double lead = divisor[n - 1];

		// special case
		// leading coefficient is 0
		if (lead == 0) {
			int n2 = n - 1;
			while (divisor[n2] == 0 && n2 > 0) {
				n2--;
			}

			double[] divisor2 = new double[n2 + 1];

			// polynomial has all coeffs zero
			if (n2 == -1) {
				setUndefined(g, h);
				return;
			}

			for (int i = 0; i < divisor2.length; i++) {
				divisor2[i] = divisor[i];
			}

			divisor = divisor2;
			n = divisor.length;
			lead = divisor[n - 1];

		}

		double[] div = new double[m - n + 1];
		double[] mod = new double[n - 1];

		for (int i = 0; i < m - n + 1; i++) {
			result[m - 1 - i] /= lead;

			double coeff = result[m - 1 - i];
			if (coeff != 0) {
				for (int j = 1; j < n; j++) {
					result[m - 1 - (i + j)] += -divisor[n - 1 - j] * coeff;
				}
			}
		}

		int divLen = div.length;

		for (int i = 0; i < divLen; i++) {
			div[divLen - 1 - i] = result[m - 1 - i];
		}

		for (int i = divLen; i < m; i++) {
			mod[mod.length - 1 - i + divLen] = result[m - 1 - i];
		}

		setFunction(g, div);
		setFunction(h, mod);
	}

	private static void setUndefined(GeoFunction g2, GeoFunction h2) {
		if (g2 != null) {
			g2.setUndefined();
		}
		if (h2 != null) {
			h2.setUndefined();
		}

	}

	private static void setFunction(GeoFunction g2, double[] div) {
		if (g2 != null) {
			Function divPolyFun = AlgoPolynomialFromCoordinates
					.buildPolyFunctionExpression(g2.getKernel(), div);
			g2.setDefined(true);
			g2.setFunction(divPolyFun);
		}

	}

}
