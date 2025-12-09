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
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * algorithm for Cauchy[0,1,x]
 * 
 * @author Michael
 */
public class AlgoCauchyDF extends AlgoElement implements AlgoDistributionDF {

	private GeoNumberValue a; // input
	private GeoNumberValue b; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param a
	 *            distribution parameter
	 * @param b
	 *            distribution parameter
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoCauchyDF(Construction cons, GeoNumberValue a, GeoNumberValue b,
			BooleanValue cumulative) {
		super(cons);
		this.a = a;
		this.b = b;
		this.cumulative = cumulative;
		ret = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Cauchy;
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
	@Override
	public GeoFunction getResult() {
		return ret;
	}

	@Override
	public void compute() {
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode x0 = new ExpressionNode(kernel, a);
		ExpressionNode g = new ExpressionNode(kernel, b);
		ExpressionNode en = new ExpressionNode(kernel, fv);

		if (cumulative != null && cumulative.getBoolean()) {

			en = en.subtract(x0).divide(g.abs()).atan().divide(Math.PI)
					.plus(0.5);

			// old hack:
			// command = "1/pi atan((x-("+x0+"))/abs("+g+"))+0.5";

		} else {

			en = g.abs().divide(g.square().plus(en.subtract(x0).square())
					.multiply(Math.PI));

			// old hack:
			// command = "1/pi abs("+g+")/(("+g+")^2+(x-("+x0+"))^2)";
		}

		Function tempFun = new Function(en, fv);
		tempFun.initFunction();

		ret.setFunction(tempFun);

	}

}
