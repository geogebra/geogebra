/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Taylor series of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoTaylorSeries extends AlgoElement {

	// works OK for simple functions eg sin(x)
	// sin(cos(x)) very slow for n=10 and above
	private static final int MAX_ORDER = 80;

	private GeoFunctionable f; // input
	private GeoNumberValue a; // series for f about the point x = a
	private GeoNumberValue n; // order of series
	private GeoFunction g; // output g = f'

	/**
	 * Creates new Taylor series for function f about the point x=a of order n.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param a
	 *            start value
	 * @param n
	 *            degree
	 */
	public AlgoTaylorSeries(Construction cons, String label, GeoFunctionable f,
			GeoNumberValue a, GeoNumberValue n) {
		super(cons);
		this.f = f;
		this.a = a;
		this.n = n;

		g = new GeoFunction(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.TaylorSeries;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f.toGeoElement();
		input[1] = a.toGeoElement();
		input[2] = n.toGeoElement();

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return result
	 */
	public GeoFunction getPolynomial() {
		return g;
	}

	// ON CHANGE: similar code is in AlgoPolynomialForFunction
	@Override
	public final void compute() {
		if (!f.isDefined() || !a.isDefined() || !n.isDefined()) {
			g.setUndefined();
			return;
		}

		if (f.getFunction().getExpression().isSecret()) {
			g.setUndefined();
			return;
		}

		// check order
		double nd = n.getDouble();
		if (nd < 0) {
			g.setUndefined();
			return;
		} else if (nd > MAX_ORDER) {
			nd = MAX_ORDER;
		}
		int order = (int) Math.round(nd);

		// build expression of polynomial
		double ad = a.getDouble();

		// first part f(a)
		double coeff = f.value(ad);
		if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
			g.setUndefined();
			return;
		}

		ExpressionNode series = null; // expression for the Taylor series
		if (!DoubleUtil.isZero(coeff)) {
			series = new ExpressionNode(kernel, new MyDouble(kernel, coeff));
		}

		FunctionVariable fVar = new FunctionVariable(kernel);

		// other parts (k-thDerivative of f at a) / k! * (x - a)^k
		if (order > 0) {
			ExpressionValue diffExp;

			// build the expression (x - a)
			if (DoubleUtil.isZero(ad)) { // only x
				diffExp = fVar;
			} else if (ad > 0) { // (x - a)
				diffExp = new ExpressionNode(kernel, fVar, Operation.MINUS,
						new MyDouble(kernel, ad));
			} else { // (x + a)
				diffExp = new ExpressionNode(kernel, fVar, Operation.PLUS,
						new MyDouble(kernel, -ad));
			}

			Function deriv = f.getFunction();

			for (int k = 1; k <= order; k++) {

				// calculate next derivative from the last one
				deriv = deriv.getDerivative(1, true);

				if (deriv == null) {
					g.setUndefined();
					return;
				}
				coeff = deriv.value(ad);

				// Log.debug("deriv = "
				// + deriv.toValueString(StringTemplate.giacTemplate));

				// Log.debug("coeff(" + k + ") = " + coeff);

				if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
					g.setUndefined();
					return;
				} else if (DoubleUtil.isZero(coeff)) {
					continue; // this part vanished
				}

				boolean negativeCoeff = coeff < 0;

				// build the expression (x - a) ^ k
				ExpressionValue powerExp;
				switch (k) {
				case 1:
					powerExp = diffExp;
					break;
				default:
					powerExp = new ExpressionNode(kernel,
							new ExpressionNode(kernel, diffExp, Operation.POWER,
									new MyDouble(kernel, k)),
							Operation.DIVIDE,
							new ExpressionNode(kernel, new MyDouble(kernel, k),
									Operation.FACTORIAL, null));
				}

				// build the expression
				// (k-thDerivative of f at a) * (x - a)^k / k!
				ExpressionValue partExp;
				MyDouble coeffMyDouble = null;
				if (DoubleUtil.isEqual(coeff, 1.0)) {
					partExp = powerExp;
				} else {
					coeffMyDouble = new MyDouble(kernel, coeff);
					partExp = new ExpressionNode(kernel, coeffMyDouble,
							Operation.MULTIPLY, powerExp);
				}

				// add part to series
				if (series == null) {
					series = new ExpressionNode(kernel, partExp);
				} else {
					if (negativeCoeff) {
						if (coeffMyDouble != null) {
							coeffMyDouble.set(-coeff); // change sign
						}
						series = new ExpressionNode(kernel, series,
								Operation.MINUS, partExp);
					} else {
						series = new ExpressionNode(kernel, series,
								Operation.PLUS, partExp);
					}
				}
			}
		}

		if (series == null) { // this means f(x) = 0
			series = new ExpressionNode(kernel, new MyDouble(kernel, 0));
		}

		// Function series
		Function seriesFun = new Function(series, fVar);
		g.setFunction(seriesFun);
		g.setDefined(true);
	}

}
