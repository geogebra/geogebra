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
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.plugin.Operation;

public class AlgoBetaDF extends AlgoElement implements AlgoDistributionDF  {
	private GeoNumberValue alpha; // input
	private final GeoNumberValue beta; // input
	private final BooleanValue cumulative; // optional input
	private final GeoFunction ret; // output
	private final MyNumberPair conditionAndLeft;

	/**
	 * @param cons
	 *            construction
	 * @param alpha
	 *            alpha exponent
	 * @param beta
	 *            beta exponent
	 * @param cumulative
	 *            cumulative
	 */
	public AlgoBetaDF(Construction cons, GeoNumberValue alpha, GeoNumberValue beta,
			BooleanValue cumulative) {
		super(cons);
		this.alpha = alpha;
		this.beta = beta;
		this.cumulative = cumulative;
		FunctionVariable fv = new FunctionVariable(kernel);

		ExpressionNode en = new ExpressionNode(kernel, 0)
				.apply(Operation.LESS, fv)
				.apply(Operation.AND_INTERVAL, fv.wrap()
						.apply(Operation.LESS, new MyDouble(kernel, 1)));
		conditionAndLeft = new MyNumberPair(kernel, en, new ExpressionNode(kernel, 0));
		ret = new ExpressionNode(kernel, conditionAndLeft, Operation.IF_ELSE,
						getFallback(fv)).buildFunction(fv);

		setInputOutput(); // for AlgoElement

		// compute angle
		compute();
	}

	private ExpressionValue getFallback(FunctionVariable fv) {
		if (cumulative == null || !cumulative.getBoolean()) {
			return new ExpressionNode(kernel, 0);
		} else {
			return fv.wrap().apply(Operation.LESS_EQUAL, new MyDouble(kernel, 0))
					.ifElse(new MyDouble(kernel, 0), new MyDouble(kernel, 1));
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.BetaDist;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		FunctionVariable fv = new FunctionVariable(kernel);
		GeoFunction dummyFun = fv.wrap().buildFunction(fv);

		input = new GeoElement[cumulative == null ? 3 : 4];
		input[0] = alpha.toGeoElement();
		input[1] = beta.toGeoElement();
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
		ExpressionNode en;

		FunctionVariable fv = ret.getFunctionVariables()[0];
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);

		if (cumulative != null && cumulative.getBoolean()) {
			en = fvEn.applyReverse(Operation.BETA_INCOMPLETE_REGULARIZED,
					new MyNumberPair(kernel, alpha, beta));
		} else {
			en = fvEn.power(alpha.wrap().subtract(1))
					.multiplyR(fvEn.subtractR(1).power(beta.wrap().subtract(1)))
					.divide(new ExpressionNode(kernel, alpha, Operation.BETA, beta));
		}
		conditionAndLeft.setY(en);
		ret.getFunctionExpression().setRight(getFallback(fv));
	}

}
