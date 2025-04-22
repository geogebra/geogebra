/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.plugin.Operation;

/**
 * Try to expand the given function to a polynomial.
 *
 * @author Markus Hohenwarter
 */
public class AlgoPolynomialFromFunctionNVar extends AlgoElement {

	private GeoFunctionNVar f; // input
	private GeoFunctionNVar g; // output
	private ExpressionNode poly;
	private FunctionVariable var1;
	private FunctionVariable var2;

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
		FunctionNVar functionNVar1 = f.getFunction();
		if (!f.isDefined() || functionNVar1 == null) {
			g.setUndefined();
			return;
		}
		FunctionVariable[] functionVariables = f.getFunctionVariables();

		if (functionVariables.length > 2) {
			g.setFunction(f.getFunction());
			g.setUndefined();
			return;
		}

		String varName1 = functionVariables[0].getSetVarString();
		String varName2 = functionVariables[1].getSetVarString();

		ExpressionValue[][] coeff = functionNVar1.getCoeff();
		if (coeff == null) {
			g.setUndefined();
			return;
		}
		poly = null;
		var1 = new FunctionVariable(kernel, varName1);
		var2 = new FunctionVariable(kernel, varName2);
		List<CoeffPowerProduct> products = buildFromCoeff(coeff);
		products.sort(CoeffPowerProduct.getComparator());
		createPolyFrom(products);
		if (poly == null) {
			poly = new ExpressionNode(kernel, 0);
		}
		FunctionNVar functionNVar = new FunctionNVar(poly, new FunctionVariable[]{var1, var2});
		g.setDefined(true);
		g.setFunction(functionNVar);
	}

	private List<CoeffPowerProduct> buildFromCoeff(ExpressionValue[][] coeff) {
		List<CoeffPowerProduct> products = new ArrayList<>();
		for (int i = coeff.length - 1; i >= 0; i--) {
			for (int j = coeff[i].length - 1; j >= 0; j--) {
				ExpressionValue coeffNode = coeff[i][j];
				if (coeffNode == null) {
					continue;
				}
				double coeffValue = coeffNode.evaluateDouble();
				if (Double.isNaN(coeffValue) || Double.isInfinite(coeffValue)) {
					return products;
				} else if (coeffValue == 0) {
					continue; // this part vanished
				}

				CoeffPowerProduct product =
						new CoeffPowerProduct(
								makeProduct(makePowerExp(var1, i), makePowerExp(var2, j)),
								coeffValue, i + j, i);
				products.add(product);
			}
		}
		return products;
	}

	private ExpressionNode createPolyFrom(List<CoeffPowerProduct> products) {
		for (CoeffPowerProduct p: products) {
			poly = AlgoPolynomialFromCoordinates.addToPoly(poly, p.getExpression(),
					p.getCoeffValue(), kernel);
		}
		return poly;
	}

	private ExpressionValue makeProduct(ExpressionValue part1, ExpressionValue part2) {
		if (part1 == null) {
			return part2;
		}
		if (part2 == null) {
			return part1;
		}
		return new ExpressionNode(kernel, part1, Operation.MULTIPLY, part2);
	}

	private ExpressionValue makePowerExp(FunctionVariable fVar, int power) {
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
}