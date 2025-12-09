/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;

/**
 * Extracts left / right side from equation.
 */
public class AlgoLeftRightSide extends AlgoElement {

	private GeoElement equation;
	private GeoFunctionNVar side;
	private boolean left;
	private FunctionVariable[] fv;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param equation
	 *            equation
	 * @param left
	 *            whether to get left side (or right)
	 */
	public AlgoLeftRightSide(Construction cons, String label,
			GeoElement equation, boolean left) {
		super(cons);
		this.equation = equation;
		this.left = left;
		if (equation.isGeoElement3D()) {
			fv = new FunctionVariable[] { new FunctionVariable(kernel, "x"),
					new FunctionVariable(kernel, "y"),
					new FunctionVariable(kernel, "z") };
		} else {
			fv = new FunctionVariable[] { new FunctionVariable(kernel, "x"),
					new FunctionVariable(kernel, "y") };
		}
		FunctionNVar f = new FunctionNVar(new ExpressionNode(kernel, fv[0]),
				fv);
		side = new GeoFunctionNVar(cons, f);

		setInputOutput();
		compute();
		side.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(side);
		input = new GeoElement[] { equation };
		setDependencies();
	}

	@Override
	public void compute() {
		if (!equation.isDefined()) {
			side.setUndefined();
			return;
		}
		String str = equation.toValueString(StringTemplate.maxPrecision);
		String[] sides = str.split("=");
		String sideStr = left ? sides[0] : sides[1];
		GeoFunctionNVar processed = kernel.getAlgebraProcessor()
				.evaluateToFunctionNVar(sideStr, true, false);
		side.set(processed);

	}

	@Override
	public Commands getClassName() {
		return left ? Commands.LeftSide : Commands.RightSide;
	}

	/**
	 * @return one side of the equation
	 */
	public GeoFunctionNVar getResult() {
		return side;
	}

}
