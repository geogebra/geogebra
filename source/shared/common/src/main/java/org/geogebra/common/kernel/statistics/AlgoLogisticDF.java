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
 * algorithm for Logistic[0,1,x]
 * 
 * @author Michael
 */
public class AlgoLogisticDF extends AlgoElement implements AlgoDistributionDF {

	private GeoNumberValue mean; // input
	private GeoNumberValue scale; // input
	private BooleanValue cumulative; // optional input
	private GeoFunction ret; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param mean
	 *            mean
	 * @param scale
	 *            scale
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoLogisticDF(Construction cons, GeoNumberValue mean,
			GeoNumberValue scale, BooleanValue cumulative) {
		super(cons);
		this.mean = mean;
		this.scale = scale;
		this.cumulative = cumulative;
		ret = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Logistic;
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
		input[1] = scale.toGeoElement();
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
		ExpressionNode en = new ExpressionNode(kernel, fv);
		ExpressionNode absS = new ExpressionNode(kernel, scale).abs();

		en = en.subtract(mean).divide(absS).reverseSign().exp();

		if (cumulative != null && cumulative.getBoolean()) {

			en = en.plus(1).reciprocate();

			// old hack
			// processAlgebraCommand( "1/(1+exp(-(x-("+m+"))/abs("+s+")))", true
			// );

		} else {

			en = en.divide(absS.multiply(en.plus(1).square()));

			// old hack:
			// processAlgebraCommand(
			// "exp(-(x-("+m+"))/abs("+s+"))/(abs("+s+")*(1+exp(-(x-("+m+"))/abs("+s+")))^2)",
			// true );
		}

		Function tempFun = new Function(en, fv);
		tempFun.initFunction();

		ret.setFunction(tempFun);

	}

}
