/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentConic.java
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
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 *
 * @author Markus
 */
public class AlgoDependentConic extends AlgoElement
		implements EvaluateAtPoint, DependentAlgo {

	private Equation equation;
	private ExpressionValue[] ev = new ExpressionValue[6]; // input
	private GeoConic conic; // output

	/**
	 * Creates new AlgoDependentConic
	 * 
	 * @param cons
	 *            construction
	 * @param equ
	 *            conic equation
	 */
	public AlgoDependentConic(Construction cons, Equation equ) {
		super(cons, false); // don't add to construction list yet
		equation = equ;
		Polynomial lhs = equ.getNormalForm();

		ev[0] = lhs.getCoefficient("xx");
		ev[1] = lhs.getCoefficient("xy");
		ev[2] = lhs.getCoefficient("yy");
		ev[3] = lhs.getCoefficient("x");
		ev[4] = lhs.getCoefficient("y");
		ev[5] = lhs.getConstantCoefficient();

		// check coefficients
		for (int i = 0; i < 6; i++) {
			// find constant parts of input and evaluate them right now
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

		conic = new GeoConic(cons);
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

		setOnlyOutput(conic);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return conic
	 */
	public GeoConic getConic() {
		return conic;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {
		try {
			ExpressionNode def = conic.getDefinition();
			conic.setCoeffs(ev[0].evaluateDouble(), ev[1].evaluateDouble(),
					ev[2].evaluateDouble(), ev[3].evaluateDouble(),
					ev[4].evaluateDouble(), ev[5].evaluateDouble());
			conic.setDefinition(def);
		} catch (Throwable e) {
			conic.setUndefined();
		}
	}

	@Override
	final public double evaluate(GeoPoint P) {
		double mat0 = ev[0].evaluateDouble(); // x\u00b2
		double mat1 = ev[2].evaluateDouble(); // y\u00b2
		double mat2 = ev[5].evaluateDouble(); // constant
		double mat3 = ev[1].evaluateDouble() / 2.0; // xy
		double mat4 = ev[3].evaluateDouble() / 2.0; // x
		double mat5 = ev[4].evaluateDouble() / 2.0;
		return P.x * (mat0 * P.x + mat3 * P.y + mat4 * P.z)
				+ P.y * (mat3 * P.x + mat1 * P.y + mat5 * P.z)
				+ P.z * (mat4 * P.x + mat5 * P.y + mat2 * P.z);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		if (conic.getDefinition() != null) {
			return conic.getDefinition().toString(tpl);
		}
		return equation.toString(tpl);
	}

	@Override
	public ExpressionNode getExpression() {
		return equation.wrap();
	}
}
