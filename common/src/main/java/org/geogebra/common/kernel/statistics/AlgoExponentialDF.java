/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Area of polygon P[0], ..., P[n]
 *
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
 * algorithm for Exponential[a,x, boolean] (PDF / CDF)
 * 
 * @author Michael
 */
public class AlgoExponentialDF extends AlgoElement
		implements AlgoDistributionDF {

	private GeoNumberValue lambda; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * @param cons
	 *            construction
	 * @param lambda
	 *            exponential function base
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoExponentialDF(Construction cons, GeoNumberValue lambda,
			BooleanValue cumulative) {
		super(cons);
		this.lambda = lambda;
		this.cumulative = cumulative;

		ret = DistributionFunctionFactory.zeroWhenNegative(cons);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Exponential;
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
		input[0] = lambda.toGeoElement();
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
		ExpressionNode en = new ExpressionNode(kernel, fv);

		if (cumulative != null && cumulative.getBoolean()) {

			en = en.multiply(lambda).reverseSign().exp().subtractR(1);

			// old hack:
			// command="If[x<0,0,1-exp(-("+l+")x)]";

		} else {

			en = en.multiply(lambda).reverseSign().exp().multiply(lambda);

			// old hack:
			// command="If[x<0,0,("+l+")exp(-("+l+")x)]";
		}

		ret.getFunctionExpression().setRight(en);

	}

}
