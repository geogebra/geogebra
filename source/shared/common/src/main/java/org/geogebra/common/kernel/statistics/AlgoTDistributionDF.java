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
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * algorithm for PDF / CDF of T Distribution TDistribution[a,x]
 * 
 * @author Michael
 */
public class AlgoTDistributionDF extends AlgoElement
		implements AlgoDistributionDF {

	private GeoNumberValue v; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            variable value
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoTDistributionDF(Construction cons, GeoNumberValue a,
			BooleanValue cumulative) {
		super(cons);
		this.v = a;
		this.cumulative = cumulative;
		ret = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.TDistribution;
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
		input[0] = v.toGeoElement();
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
		ExpressionNode en;
		FunctionVariable x = new FunctionVariable(kernel);
		ExpressionNode vEn = new ExpressionNode(kernel, v);
		ExpressionNode xEn = new ExpressionNode(kernel, x);
		ExpressionNode div = vEn;
		ExpressionNode pi = new ExpressionNode(kernel, Math.PI);

		if (cumulative != null && cumulative.getBoolean()) {

			ExpressionValue half = new MyDouble(kernel, 0.5);
			ExpressionNode halfV = vEn.divide(2);

			ExpressionNode beta1 = halfV.betaRegularized(half,
					new MyDouble(kernel, 1));
			ExpressionNode beta2 = halfV.betaRegularized(half,
					vEn.divide(vEn.plus(xEn.square())));

			en = new ExpressionNode(kernel, half);
			en = en.plus(xEn.sgn().divide(2).multiply(beta1.subtract(beta2)));
		} else {

			en = new ExpressionNode(kernel, v);
			ExpressionNode mult = new ExpressionNode(kernel, x);

			mult = mult.square().divide(v).plus(1)
					.power(vEn.plus(1).divide(2).reverseSign());

			div = div.divide(2).gamma().multiply(pi.multiply(v).sqrt());

			en = en.plus(1).divide(2).gamma().multiply(mult).divide(div);
		}

		Function tempFun = new Function(en, x);
		tempFun.initFunction();

		ret.setFunction(tempFun);

	}

}
