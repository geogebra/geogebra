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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.plugin.Operation;

/**
 *
 * @author mathieu
 */
public class AlgoDependentPlane3D extends AlgoElement3D {

	private Equation equation;
	private ExpressionValue[] ev = new ExpressionValue[4]; // input
	private GeoPlane3D p; // output
	private ExpressionNode lhs0z = null;

	/**
	 * Creates new AlgoDependentPlane
	 * 
	 * @param cons
	 *            construction
	 * @param equ
	 *            equation
	 */
	public AlgoDependentPlane3D(Construction cons, Equation equ) {
		super(cons, false); // don't add to construction list yet
		equation = equ;
		Polynomial lhs = equ.getNormalForm();

		ev[0] = lhs.getCoefficient("x");
		ev[1] = lhs.getCoefficient("y");
		ev[2] = lhs.getCoefficient("z");
		ev[3] = lhs.getConstantCoefficient();

		// check coefficients
		for (int i = 0; i < 4; i++) {
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

		p = new GeoPlane3D(cons);
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

		setOnlyOutput(p);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return the plane
	 */
	public GeoPlane3D getPlane() {
		return p;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {

		try {
			ExpressionNode exp = p.getDefinition();
			p.setEquation(ev[0].evaluateDouble(), ev[1].evaluateDouble(),
					ev[2].evaluateDouble(), ev[3].evaluateDouble());
			p.setDefinition(exp);
		} catch (Throwable e) {
			p.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (p.getDefinition() != null) {
			return p.getDefinition().toString(tpl);
		}
		if (p.isLabelSet() || equation.containsZ()) {
			return equation.toString(tpl);
		}

		// add 0*z to expression
		if (lhs0z == null) {
			ExpressionNode lhs = equation.getLHS();
			FunctionVariable z = new FunctionVariable(kernel, "z");
			lhs0z = new ExpressionNode(kernel, lhs, Operation.PLUS,
					new ExpressionNode(kernel, new ExpressionNode(kernel, 0),
							Operation.MULTIPLY, z));
		}
		return equation.toString(tpl, lhs0z);

	}
}
