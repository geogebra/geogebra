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
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Feature;

/**
 * Polynomial remainder
 * 
 * @author Michael Borcherds
 */
public class AlgoPolynomialDivision extends AlgoElement {

	private GeoFunction f1, f2; // input
	private GeoFunction g, h; // output
	private GeoList result;
	private StringBuilder sb = new StringBuilder();

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

		setOutputLength(1);
		setOutput(0, result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return list {div,mod}
	 */
	public GeoList getResult() {
		return result;
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	@Override
	public final void compute() {
		if (!f1.isDefined() || !f2.isDefined()) {
			result.setUndefined();
			return;
		}

		if (kernel.getApplication().has(Feature.NON_CAS_POLYNOMIAL_DIVISION)) {

			nonCASDivision(kernel, f1, f2, g, h);

			return;

		}

		try {
			// get function and function variable string using temp variable
			// prefixes,
			// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2",
			// "ggbtmpvarx"}
			String[] funVarStr1 = f1.getTempVarCASString(false);
			String[] funVarStr2 = f2.getTempVarCASString(false);

			sb.setLength(0);
			sb.append("Div(");
			sb.append(funVarStr1[0]); // function f1 expression
			sb.append(",");
			sb.append(funVarStr2[0]); // function f2 expression
			sb.append(")");

			// cached evaluation of MPReduce as we are only using variable
			// values
			String functionOut = kernel.evaluateCachedGeoGebraCAS(sb.toString(),
					arbconst);
			if (functionOut == null || functionOut.length() == 0) {
				g.setUndefined();
			} else {
				// read result back into function
				g.set(kernel.getAlgebraProcessor()
						.evaluateToFunction(functionOut, false));
			}

			sb.setLength(0);
			sb.append("Mod(");
			sb.append(funVarStr1[0]); // function f1 expression
			sb.append(",");
			sb.append(funVarStr2[0]); // function f2 expression
			sb.append(")");

			// cached evaluation of MPReduce as we are only using variable
			// values
			functionOut = kernel.evaluateCachedGeoGebraCAS(sb.toString(),
					arbconst);
			if (functionOut == null || functionOut.length() == 0) {
				h.setUndefined();
			} else {
				// read result back into function
				h.set(kernel.getAlgebraProcessor()
						.evaluateToFunction(functionOut, false));
			}
		} catch (Throwable th) {
			g.setUndefined();
		}
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

		double[] dividend = poly1.getCoeffs();
		int m = dividend.length;
		double[] divisor = poly2.getCoeffs();
		int n = divisor.length;

		if (m < n) {

			if (g != null) {
				double[] z = { 0 };
				Function zero = AlgoPolynomialFromCoordinates
						.buildPolyFunctionExpression(kernel, z);

				g.setFunction(zero);
			}
			if (h != null) {
				h.set(f1);
			}
			return;
		}

		// for (int i = 0; i < dividend.length; i++) {
		// Log.debug(dividend[i]);
		// }
		//
		// for (int i = 0; i < divisor.length; i++) {
		// Log.debug(divisor[i]);
		// }

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
				if (g != null) {
					g.setUndefined();
				}
				if (h != null) {
					h.setUndefined();
				}
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

		// for (int i = 0; i < div.length; i++) {
		// Log.debug("div = " + div[i]);
		// }
		//
		// for (int i = 0; i < mod.length; i++) {
		// Log.debug("mod = " + mod[i]);
		// }

		Function divPolyFun = AlgoPolynomialFromCoordinates
				.buildPolyFunctionExpression(kernel, div);
		Function modPolyFun = AlgoPolynomialFromCoordinates
				.buildPolyFunctionExpression(kernel, mod);

		// Log.debug("divPolyFun = "
		// + divPolyFun.toString(StringTemplate.defaultTemplate));
		// Log.debug("modPolyFun = "
		// + modPolyFun.toString(StringTemplate.defaultTemplate));

		if (g != null) {
			g.setDefined(true);
			g.setFunction(divPolyFun);
		}

		if (h != null) {
			h.setDefined(true);
			h.setFunction(modPolyFun);
		}
	}

}
