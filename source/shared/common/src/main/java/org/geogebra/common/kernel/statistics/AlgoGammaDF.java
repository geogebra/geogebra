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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * algorithm for Normal[0,1,x]
 * 
 * @author Michael
 */
public class AlgoGammaDF extends AlgoElement implements AlgoDistributionDF {

	private GeoNumberValue mean; // input
	private GeoNumberValue sd; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            mean
	 * @param b
	 *            standard deviation
	 * @param cumulative
	 *            cumulative
	 */
	public AlgoGammaDF(Construction cons, GeoNumberValue a, GeoNumberValue b,
			BooleanValue cumulative) {
		super(cons);
		this.mean = a;
		this.sd = b;
		this.cumulative = cumulative;

		ret = DistributionFunctionFactory.zeroWhenNegative(cons);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Gamma;
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
		NumberValue k = mean;
		NumberValue t = sd;

		FunctionVariable fv = ret.getFunctionVariables()[0];
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);
		ExpressionNode kEn = new ExpressionNode(kernel, k);
		ExpressionNode en = fvEn.divide(t);
		ExpressionNode div = kEn.gamma();

		if (cumulative != null && cumulative.getBoolean()) {

			en = en.gammaIncompleteReverseArgs(k).divide(div);

			// old hack:
			// command = "If[x<0,0,gamma("+k+",x/("+t+"))/gamma("+k+")]";

		} else {

			ExpressionNode tEn = new ExpressionNode(kernel, t);

			div = div.multiply(tEn.power(k));

			en = en.reverseSign().exp().multiply(fvEn.power(kEn.subtract(1)))
					.divide(div);

			// old hack:
			// command =
			// "If[x<0,0,x^("+k+"-1)
			// exp(-x/("+t+"))/(gamma("+k+")("+t+")^("+k+"))]";
		}

		ret.getFunctionExpression().setRight(en);

	}

}
