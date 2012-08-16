/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.plugin.Operation;

/**
 * Taylor series of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoTaylorSeries extends AlgoElement {

	// works OK for simple functions eg sin(x)
	// sin(cos(x)) very slow for n=10 and above
	private static final int MAX_ORDER = 80;

	private GeoFunction f; // input
	private NumberValue a; // series for f about the point x = a
	private NumberValue n; // order of series
	private GeoFunction g; // output g = f'

	private GeoElement ageo, ngeo;

	/**
	 * Creates new Taylor series for function f about the point x=a of order n.
	 */
	public AlgoTaylorSeries(Construction cons, String label, GeoFunction f,
			NumberValue a, NumberValue n) {
		super(cons);
		this.f = f;
		this.a = a;
		this.n = n;

		ageo = a.toGeoElement();
		ngeo = n.toGeoElement();

		g = new GeoFunction(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoTaylorSeries;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f;
		input[1] = ageo;
		input[2] = ngeo;

		super.setOutputLength(1);
		super.setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	public GeoFunction getPolynomial() {
		return g;
	}

	// ON CHANGE: similiar code is in AlgoPolynomialForFunction
	@Override
	public final void compute() {
		if (!f.isDefined() || !ageo.isDefined() || !ngeo.isDefined()) {
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
		double coeff = f.evaluate(ad);
		if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
			g.setUndefined();
			return;
		}

		ExpressionNode series = null; // expression for the Taylor series
		if (!Kernel.isZero(coeff)) {
			series = new ExpressionNode(kernel, new MyDouble(kernel, coeff));
		}

		FunctionVariable fVar = new FunctionVariable(kernel);

		// other parts (k-thDerivative of f at a) / k! * (x - a)^k
		if (order > 0) {
			ExpressionValue diffExp;

			// build the expression (x - a)
			if (Kernel.isZero(ad)) { // only x
				diffExp = fVar;
			} else if (ad > 0) { // (x - a)
				diffExp = new ExpressionNode(kernel, fVar,
						Operation.MINUS, new MyDouble(kernel, ad));
			} else { // (x + a)
				diffExp = new ExpressionNode(kernel, fVar, Operation.PLUS,
						new MyDouble(kernel, -ad));
			}

			for (int k = 1; k <= order; k++) {
				Function deriv = f.getFunction().getDerivative(k);
				if (deriv == null) {
					g.setUndefined();
					return;
				}
				coeff = deriv.evaluate(ad);

				// Application.debug("coeff(" + k + ") = " + coeff);

				if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
					g.setUndefined();
					return;
				} else if (Kernel.isZero(coeff))
					continue; // this part vanished

				boolean negativeCoeff = coeff < 0;

				// build the expression (x - a) ^ k
				ExpressionValue powerExp;
				switch (k) {
				case 1:
					powerExp = diffExp;
					break;
				default:
					powerExp = new ExpressionNode(kernel, new ExpressionNode(
							kernel, diffExp, Operation.POWER,
							new MyDouble(kernel, k)), Operation.DIVIDE,
							new ExpressionNode(kernel, new MyDouble(kernel, k),
									Operation.FACTORIAL, null));
				}

				// build the expression
				// (k-thDerivative of f at a) * (x - a)^k / k!
				ExpressionValue partExp;
				MyDouble coeffMyDouble = null;
				if (Kernel.isEqual(Math.abs(coeff), 1.0)) {
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
						if (coeffMyDouble != null)
							coeffMyDouble.set(-coeff); // change sign
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

	@Override
	public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	// TODO Consider locusequability

}
