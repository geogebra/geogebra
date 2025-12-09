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
 * algorithm for Weibull[0,1,x]
 * 
 * @author Michael
 */
public class AlgoWeibullDF extends AlgoElement implements AlgoDistributionDF {

	private GeoNumberValue k; // input
	private GeoNumberValue l; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param k
	 *            mean
	 * @param l
	 *            standard deviation
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoWeibullDF(Construction cons, GeoNumberValue k, GeoNumberValue l,
			BooleanValue cumulative) {
		super(cons);
		this.k = k;
		this.l = l;
		this.cumulative = cumulative;

		ret = DistributionFunctionFactory.zeroWhenNegative(cons);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Weibull;
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
		input[0] = k.toGeoElement();
		input[1] = l.toGeoElement();
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
	@Override
	public GeoFunction getResult() {
		return ret;
	}

	@Override
	public void compute() {
		FunctionVariable fv = ret.getFunctionVariables()[0];
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);
		ExpressionNode kEn = new ExpressionNode(kernel, k);
		ExpressionNode en = fvEn.divide(l).power(k).reverseSign().exp();

		if (cumulative != null && cumulative.getBoolean()) {

			en = en.subtractR(1);

			// old hack:
			// command = "If[x<0,0,1-exp(-(x/("+l+"))^("+k+"))]";

		} else {

			ExpressionNode prod1 = kEn.divide(l);

			ExpressionNode prod2 = fvEn.divide(l).power(kEn.subtract(1));

			en = en.multiply(prod2).multiply(prod1);

			// old hack:
			// command =
			// "If[x<0,0,("+k+")/("+l+")(x/("+l+"))^("+k+"-1)exp(-(x/("+l+"))^("+k+"))]";
		}

		ret.getFunctionExpression().setRight(en);
	}

}
