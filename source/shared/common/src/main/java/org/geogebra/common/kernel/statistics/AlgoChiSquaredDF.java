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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * algorithm for ChiSquared[a,x]
 * 
 * @author Michael
 */
public class AlgoChiSquaredDF extends AlgoElement
		implements AlgoDistributionDF {

	private GeoNumberValue k; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            distribution parameter
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoChiSquaredDF(Construction cons, GeoNumberValue a,
			BooleanValue cumulative) {
		super(cons);
		this.k = a;
		this.cumulative = cumulative;

		ret = DistributionFunctionFactory.zeroWhenNegative(cons);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ChiSquared;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		// dummy function for the "x" argument, eg
		// Normal[0,1,x]
		// Normal[0,1,x,true]
		FunctionVariable fv = new FunctionVariable(kernel);
		GeoFunction dummyFun = fv.wrap().buildFunction(fv);

		input = new GeoElement[cumulative == null ? 2 : 3];
		input[0] = k.toGeoElement();
		input[1] = dummyFun;
		if (cumulative != null) {
			input[2] = (GeoElement) cumulative;
		}

		setOnlyOutput(ret);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return Normal PDF or CDF function
	 */
	@Override
	public GeoFunction getResult() {
		return ret;
	}

	@Override
	public void compute() {

		FunctionVariable fv = ret.getFunctionVariables()[0];
		ExpressionNode kEn = new ExpressionNode(kernel, k);
		ExpressionNode halfk = kEn.divide(2);
		ExpressionNode en;
		ExpressionNode div = halfk.gamma();

		if (cumulative != null && cumulative.getBoolean()) {

			en = new ExpressionNode(kernel, fv);

			en = en.divide(2).gammaIncompleteReverseArgs(halfk).divide(div);

			// old hack:
			// command = "If[x<0,0,gamma(("+k+")/2,x/2)/gamma(("+k+")/2)]";

		} else {

			en = new ExpressionNode(kernel, fv);

			en = en.power(halfk.subtract(1))
					.multiply(en.reverseSign().divide(2).exp());

			div = div.multiply(halfk.powerR(2));

			en = en.divide(div);

			// old hack:
			// command =
			// "If[x<0,0,(x^(("+k+")/2-1)exp(-x/2))/(2^(("+k+")/2)gamma(("+k+")/2))]";
		}

		ret.getFunctionExpression().setRight(en);

	}

}
