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
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Try to expand the given function to a polynomial.
 *
 * @author Markus Hohenwarter
 */
public class AlgoPolynomialFromFunctionNVar extends AlgoElement {

	private GeoFunctionNVar f; // input
	private GeoFunctionNVar g; // output
	private ExpressionNode poly;
	private FunctionVariable xVar;
	private FunctionVariable yVar;

	/**
	 * @param cons construction
	 * @param label output label
	 * @param f function, possibly polynomial
	 */
	public AlgoPolynomialFromFunctionNVar(Construction cons, String label,
			GeoFunctionNVar f) {
		super(cons);
		this.f = f;

		g = new GeoFunctionNVar(cons);
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

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return polynomial function
	 */
	public GeoFunctionNVar getPolynomial() {
		return g;
	}

	// ON CHANGE: similar code is in AlgoTaylorSeries
	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}
		FunctionNVar inFun = f.getFunction();
		xVar = new FunctionVariable(kernel, "x");
		yVar = new FunctionVariable(kernel, "y");
		ExpressionValue[][] coeff = inFun.getCoeff();
		ExpressionNode expressionNode = buildFromCoeff(coeff);
		if (expressionNode != null) {
			FunctionNVar functionNVar = new FunctionNVar(poly, new FunctionVariable[]{xVar, yVar});
			g.setFunction(functionNVar);

		}
	}

	private ExpressionNode buildFromCoeff(ExpressionValue[][] coeff) {
		poly = null; // expression for the expanded polynomial
		MyDouble coeffMyDouble = null;
		for (int i = 0; i < coeff.length; i++) {
			for (int j = 0; j < coeff[i].length; j++) {
				ExpressionValue coeffNode = coeff[i][j];
				if (coeffNode == null) {
					continue;
				}
				ExpressionNode c = new ExpressionNode(coeffNode.wrap());
				double cf = c.evaluateDouble();
				if (Double.isNaN(cf) || Double.isInfinite(cf)) {
					return poly;
				} else if (cf == 0) {
					continue; // this part vanished
				}
				boolean negativeCoeff = cf < 0;
				ExpressionValue xPower = makePowerExp(xVar, i);
				ExpressionValue yPower = makePowerExp(yVar, j);
				ExpressionValue xPart = makePartExp(xPower, cf);
				ExpressionValue yPart = makePartExp(yPower, 1);
				ExpressionValue mul = combineParts(xPart, yPart);
				if (poly == null) {
					if (mul != null) {
						poly = mul.wrap();
					}
				} else {
					if (negativeCoeff) {
						if (coeffMyDouble != null) {
							coeffMyDouble.set(-cf); // change sign
						}
						poly = new ExpressionNode(kernel, poly, Operation.MINUS,
								mul);
					} else {
						poly = new ExpressionNode(kernel, poly, Operation.PLUS,
								mul);
					}
				}
			}
		}
		return poly;
	}

	private ExpressionNode combineParts(ExpressionValue xPart, ExpressionValue yPart) {
		if (xPart != null && (yPart == null || yPart.evaluateDouble() == 1)) {
			return xPart.wrap();
		}
		if (xPart == null && (yPart != null || xPart.evaluateDouble() == 1)) {
			return yPart.wrap();
		}if (xPart == null && yPart == null) {
			return null;
		}
		return new ExpressionNode(kernel, xPart, Operation.MULTIPLY, yPart);
	}

	private ExpressionValue makePowerExp(FunctionVariable fVar, int power) {
		ExpressionValue powerExp;
		switch (power) {
		case 0:
			return null;
		case 1:
			return fVar;

		default:
			return new ExpressionNode(kernel, fVar, Operation.POWER,
					new MyDouble(kernel, power));
		}

	}

	private ExpressionValue makePartExp(ExpressionValue powerExp, double coeff) {
		ExpressionValue partExp;
		MyDouble coeffMyDouble = null;
		// check for poly != null rather than k != n-1 in case the leading
		// coefficient was 0, eg FitPoly[{(1,-1),(0,0),(-1,-1),(2,-4)},3]
		if (DoubleUtil.isEqual(coeff, 1.0)
				|| (poly != null && DoubleUtil.isEqual(coeff, -1.0))) {
			if (powerExp == null) {
				partExp = new MyDouble(kernel, 1.0);
			} else {
				partExp = powerExp;
			}
		} else {
			coeffMyDouble = coeff == -1 ? new MinusOne(kernel) : new MyDouble(kernel, coeff);
			if (powerExp == null) {
				partExp = coeffMyDouble;
			} else {
				partExp = new ExpressionNode(kernel, coeffMyDouble,
						Operation.MULTIPLY, powerExp);
			}
		}
		return partExp;
	}
}