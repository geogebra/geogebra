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
 * algorithm for Uniform[a, b, x, boolean]
 * 
 * @author Michael
 */
public class AlgoUniformDF extends AlgoElement {

	private GeoNumberValue a; // input
	private GeoNumberValue b; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            left interval bound
	 * @param b
	 *            right interval bound
	 * @param cumulative
	 *            cumulative
	 */
	public AlgoUniformDF(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b, BooleanValue cumulative) {
		this(cons, a, b, cumulative);
		ret.setLabel(label);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param a
	 *            left interval bound
	 * @param b
	 *            right interval bound
	 * @param cumulative
	 *            cumulative
	 */
	public AlgoUniformDF(Construction cons, GeoNumberValue a, GeoNumberValue b,
			BooleanValue cumulative) {
		super(cons);
		this.a = a;
		this.b = b;
		this.cumulative = cumulative;
		ret = DistributionFunctionFactory.zeroWhenLessThan(a, cons, true);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Uniform;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		// dummy function for the "x" argument, eg
		// Normal[0,1,x]
		// Normal[0,1,x,true]
		FunctionVariable fv = new FunctionVariable(kernel);
		GeoFunction dummyFun = fv.wrap().buildFunction(fv);

		input = new GeoElement[cumulative == null ? 3 : 4];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
		input[2] = dummyFun;
		if (cumulative != null) {
			input[3] = (GeoElement) cumulative;
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

		if (!a.isDefined() || !b.isDefined()) {
			ret.setUndefined();
			return;
		}

		if (a.getDouble() >= b.getDouble()) {
			ret.setUndefined();
			return;
		}

		FunctionVariable fv = ret.getFunctionVariables()[0];

		ExpressionNode lessThanB = fv.wrap().lessThan(b);

		ExpressionNode mainBranch;
		MyDouble rightBranch;

		if (cumulative != null && cumulative.getBoolean()) {
			mainBranch = fv.wrap().subtract(a).divide(b.wrap().subtract(a));
			rightBranch = new MyDouble(kernel, 1);
		} else {
			mainBranch = b.wrap().subtract(a).reciprocate();
			rightBranch = new MyDouble(kernel, 0);
		}

		ExpressionNode en = lessThanB.ifElse(mainBranch, rightBranch);
		ret.getFunctionExpression().setRight(en);

		ret.setDefined(true);
	}

}
