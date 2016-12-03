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
 * algorithm for LogNormal[0,1,x]
 * 
 * @author Michael
 */
public class AlgoLogNormalDF extends AlgoElement implements AlgoDistributionDF {

	private GeoNumberValue mean, sd; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	@SuppressWarnings("javadoc")
	public AlgoLogNormalDF(Construction cons, String label, GeoNumberValue mean,
			GeoNumberValue sd, BooleanValue cumulative) {
		this(cons, mean, sd, cumulative);
		ret.setLabel(label);
	}

	@SuppressWarnings("javadoc")
	public AlgoLogNormalDF(Construction cons, GeoNumberValue mean,
			GeoNumberValue sd,
			BooleanValue cumulative) {
		super(cons);
		this.mean = mean;
		this.sd = sd;
		this.cumulative = cumulative;
		ret = DistributionFunctionFactory.zeroWhenNegative(cons);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.LogNormal;
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
		input[0] = mean.toGeoElement();
		input[1] = sd.toGeoElement();
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
		// ExpressionNode meanEn = new ExpressionNode(kernel, mean);
		ExpressionNode sdEn = new ExpressionNode(kernel, sd);
		ExpressionNode en = fvEn.ln().subtract(mean);

		if (cumulative != null && cumulative.getBoolean()) {

			ExpressionNode sqrt2 = (new ExpressionNode(kernel, 2.0)).sqrt();
			ExpressionNode div = sdEn.abs().multiply(sqrt2);

			en = en.divide(div).erf().multiply(0.5).plus(0.5);

			// old hack:
			// processAlgebraCommand(
			// "If[x<0,0,1/2 erf((ln(x)-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1/2]",
			// true );

		} else {

			ExpressionNode sqrt2pi = (new ExpressionNode(kernel, 2.0)
					.multiply(Math.PI)).sqrt();

			ExpressionNode prod = fvEn.multiply(sqrt2pi).multiply(sdEn.abs());

			en = en.square().divide(sdEn.square().multiply(2)).reverseSign()
					.exp().divide(prod);

			// old hack:
			// processAlgebraCommand(
			// "If[x<0,0,1/(x sqrt(2 * pi) * abs("+sd+"))*exp(-((ln(x)-("+mean+"))^2/(2*("+sd+")^2)))]",
			// true );
		}

		ret.getFunctionExpression().setRight(en);

	}

	
}
