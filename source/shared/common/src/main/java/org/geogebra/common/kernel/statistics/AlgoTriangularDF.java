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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * algorithm for Triangular[a, b, mode,x, boolean]
 * 
 * @author Michael
 */
public class AlgoTriangularDF extends AlgoElement {

	private GeoNumberValue a;
	private GeoNumberValue b;
	private GeoNumberValue mode; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            lower triangle bound
	 * @param b
	 *            upper triangle bound
	 * @param mode
	 *            mode
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoTriangularDF(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue mode, BooleanValue cumulative) {
		super(cons);
		this.a = a;
		this.b = b;
		this.mode = mode;
		this.cumulative = cumulative;
		ret = DistributionFunctionFactory.zeroWhenLessThan(a, cons, true);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Triangular;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		// dummy function for the "x" argument, eg
		// Normal[0,1,x]
		// Normal[0,1,x,true]
		FunctionVariable fv = new FunctionVariable(kernel);
		GeoFunction dummyFun = fv.wrap().buildFunction(fv);

		input = new GeoElement[cumulative == null ? 4 : 5];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
		input[2] = mode.toGeoElement();
		input[3] = dummyFun;
		if (cumulative != null) {
			input[4] = (GeoElement) cumulative;
		}

		setOnlyOutput(ret);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return Normal PDF or CDF function
	 */
	public GeoFunction getResult() {
		return ret;
	}

	@Override
	public void compute() {

		if (!a.isDefined() || !b.isDefined() || !mode.isDefined()) {
			ret.setUndefined();
			return;
		}

		if (a.getDouble() >= b.getDouble() || mode.getDouble() > b.getDouble()
				|| mode.getDouble() < a.getDouble()) {
			ret.setUndefined();
			return;
		}

		ExpressionNode bEn = new ExpressionNode(kernel, b);
		ExpressionNode modeEn = new ExpressionNode(kernel, mode);

		// make function x<a
		FunctionVariable fv = ret.getFunctionVariables()[0];

		// make function x<b
		ExpressionNode lessThanB = fv.wrap().lessThan(b);

		// make function x<mode
		ExpressionNode lessThanMode = fv.wrap().lessThan(mode);

		ExpressionNode branchAtoMode, branchModeToB;
		MyDouble rightBranch;

		if (cumulative != null && cumulative.getBoolean()) {

			branchAtoMode = fv.wrap().subtract(a).square()
					.divide(bEn.subtract(a).multiply(modeEn.subtract(a)));
			branchModeToB = fv.wrap().subtract(b).square()
					.divide(bEn.subtract(a).multiply(modeEn.subtract(b)))
					.plus(1);
			rightBranch = new MyDouble(kernel, 1);
		} else {

			branchAtoMode = fv.wrap().subtract(a).multiplyR(2)
					.divide(bEn.subtract(a).multiply(modeEn.subtract(a)));
			branchModeToB = fv.wrap().subtract(b).multiplyR(2)
					.divide(bEn.subtract(a).multiply(modeEn.subtract(b)));
			rightBranch = new MyDouble(kernel, 0);

			// old hack:
			// processAlgebraCommand(
			// "If[x < "+a+", 0, If[x < "+c+", 2(x - ("+a+")) / ("+b+" -
			// ("+a+")) / ("+c+" - ("+a+")), If[x < "+b+", 2(x - ("+b+")) /
			// ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 0]]]",
			// true );
		}
		ExpressionNode middleRight = lessThanMode.ifElse(branchAtoMode,
				lessThanB.ifElse(branchModeToB, rightBranch));

		ret.setDefined(true);
		ret.getFunctionExpression().setRight(middleRight);

	}

}
