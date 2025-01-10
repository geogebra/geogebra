/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentLine.java
 *
 * Created on 29. October 2001
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 *
 * @author Markus
 */
public class AlgoDependentLine extends AlgoElement
		implements EvaluateAtPoint, DependentAlgo {

	private Equation equation;
	private ExpressionValue[] ev = new ExpressionValue[3]; // input
	private GeoLine g; // output

	/**
	 * Creates new AlgoDependentLine
	 * 
	 * @param cons
	 *            construction
	 * @param equ
	 *            equation
	 */
	public AlgoDependentLine(Construction cons, Equation equ) {
		super(cons, false); // don't add to construction list yet
		equation = equ;
		equation.initEquation();
		Polynomial lhs = equ.getNormalForm();

		ev[0] = lhs.getCoefficient("x");
		ev[1] = lhs.getCoefficient("y");
		ev[2] = lhs.getConstantCoefficient();

		// check coefficients
		for (int i = 0; i < 3; i++) {
			if (!ev[i].inspect(Inspecting.dynamicGeosFinder)) {
				ev[i] = ev[i].evaluate(StringTemplate.defaultTemplate);
			}

			// check that coefficient is a number: this may throw an exception
			ExpressionValue eval = ev[i]
					.evaluate(StringTemplate.defaultTemplate);
			((NumberValue) eval).getDouble();
		}

		// if we get here, all is ok: let's add this algorithm to the
		// construction list
		cons.addToConstructionList(this, false);

		g = new GeoLine(cons);
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = equation.getGeoElementVariables(
				SymbolicMode.NONE);

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting line
	 */
	public GeoLine getLine() {
		return g;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {

		try {
			g.x = ev[0].evaluateDouble();
			g.y = ev[1].evaluateDouble();
			g.z = ev[2].evaluateDouble();

			// other algos might use the startPoint so we have to update it
			if (g.getStartPoint() != null) {
				g.setStandardStartPoint();
			}
		} catch (Throwable e) {
			g.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (g.getDefinition() != null) {
			return g.getDefinition().toString(tpl);
		}
		return equation.toString(tpl);
	}

	@Override
	final public double evaluate(GeoPoint P) {
		double mat0 = ev[0].evaluateDouble(); // x\u00b2
		double mat1 = ev[1].evaluateDouble(); // y\u00b2
		double mat2 = ev[2].evaluateDouble(); // constant
		return P.x * mat0 + P.y * mat1 + P.z * mat2;
	}

	@Override
	public ExpressionNode getExpression() {
		return null;
	}

}
