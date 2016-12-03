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
 * algorithm for FDistribution[0,1,x]
 * 
 * @author Michael
 */
public class AlgoFDistributionDF extends AlgoElement implements
		AlgoDistributionDF {

	private GeoNumberValue d1, d2; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	@SuppressWarnings("javadoc")
	public AlgoFDistributionDF(Construction cons, String label,
			GeoNumberValue mean, GeoNumberValue sd, BooleanValue cumulative) {
		this(cons, mean, sd, cumulative);
		ret.setLabel(label);
	}

	@SuppressWarnings("javadoc")
	public AlgoFDistributionDF(Construction cons, GeoNumberValue a,
			GeoNumberValue b,
			BooleanValue cumulative) {
		super(cons);
		this.d1 = a;
		this.d2 = b;
		this.cumulative = cumulative;

		ret = DistributionFunctionFactory.zeroWhenNegative(cons);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FDistribution;
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
		input[0] = d1.toGeoElement();
		input[1] = d2.toGeoElement();
		input[2] = dummyFun;
		if (cumulative != null) {
			input[3] = (GeoElement) cumulative;
		}

		super.setOutputLength(1);
		super.setOutput(0, ret);
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
		FunctionVariable fv = ret.getFunctionVariables()[0];
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);
		ExpressionNode d1En = new ExpressionNode(kernel, d1);
		ExpressionNode d2En = new ExpressionNode(kernel, d2);

		ExpressionNode halfd1 = d1En.divide(2);
		ExpressionNode halfd2 = d2En.divide(2);

		ExpressionNode en;

		if (cumulative != null && cumulative.getBoolean()) {

			en = d1En.divide(2).betaRegularized(halfd2,
					fvEn.multiply(d1).divide(fvEn.multiply(d1).plus(d2)));

			// old hack:
			// command =
			// "If[x<0,0,betaRegularized(("+d1+")/2,("+d2+")/2,("+d1+")*x/(("+d1+")*x+"+d2+"))]";

		} else {

			ExpressionNode beta = halfd1.beta(halfd2);

			ExpressionNode mult = d2En.power(halfd2);

			mult = fvEn.multiply(d1).power(halfd1).multiply(mult);

			ExpressionNode div = fvEn.multiply(d1).plus(d2)
					.power(halfd1.plus(halfd2)).multiply(fv).multiply(beta);

			en = mult.divide(div);

			// old hack:
			// command =
			// "If[x<0,0,((("+d1+")*x)^(("+d1+")/2)*("+d2+")^(("+d2+")/2))/(x*(("+d1+")*x+"+d2+")^(("+d1+"+"+d2+")/2)*beta(("+d1+")/2,("+d2+")/2))]";
		}

		ret.getFunctionExpression().setRight(en);

	}

	

}
