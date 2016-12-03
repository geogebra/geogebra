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
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * algorithm for Normal[0,1,x]
 * 
 * @author Michael
 */
public class AlgoNormalDF extends AlgoElement implements AlgoDistributionDF {

	private GeoNumberValue mean, sd; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	@SuppressWarnings("javadoc")
	public AlgoNormalDF(Construction cons, String label, GeoNumberValue mean,
			GeoNumberValue sd, BooleanValue cumulative) {
		this(cons, mean, sd, cumulative);
		ret.setLabel(label);
	}

	@SuppressWarnings("javadoc")
	public AlgoNormalDF(Construction cons, GeoNumberValue a, GeoNumberValue b,
			BooleanValue cumulative) {
		super(cons);
		this.mean = a;
		this.sd = b;
		this.cumulative = cumulative;
		ret = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Normal;
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
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode en = new ExpressionNode(kernel, fv);
		ExpressionNode div = new ExpressionNode(kernel, sd);

		if (cumulative != null && cumulative.getBoolean()) {

			ExpressionNode sqrt2 = (new ExpressionNode(kernel, 2)).sqrt();
			div = sqrt2.multiply(div.abs());

			en = en.subtract(mean).divide(div).erf().plus(1).divide(2);

			// old hack from CmdNormal:
			// return kernelA.getAlgebraProcessor().processAlgebraCommand(
			// "(erf((x-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1)/2", true );

		} else {

			ExpressionNode div2 = new ExpressionNode(kernel, sd);
			div2 = div2.square().multiply(2);

			ExpressionNode sqrt2pi = (new ExpressionNode(kernel, Math.PI))
					.multiply(2).sqrt();

			div = sqrt2pi.multiply(div.abs());

			en = en.subtract(mean).square().reverseSign().divide(div2).exp()
					.divide(div);

			// old hack:
			// return kernelA.getAlgebraProcessor().processAlgebraCommand(
			// "exp(-((x-("+mean+"))/("+sd+"))^2/2)/(sqrt(2*pi)*abs("+sd+"))",
			// true );
		}

		Function tempFun = new Function(en, fv);
		tempFun.initFunction();

		ret.setFunction(tempFun);

	}

	

}
