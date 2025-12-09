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
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.Operation;

/**
 * Fits an a*b^x to a list of points. Needed for pupils who don't know about e,
 * but in their curriculum are doing mathematical models with growth
 * (exponential) functions.
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-2010
 */

public class AlgoFitGrowth extends AlgoElement implements FitAlgo {

	private GeoList geolist; // input
	private GeoFunction geofunction; // output
	private RegressionMath regMath;

	/**
	 * @param cons
	 *            construction
	 * @param geolist
	 *            list of points
	 */
	public AlgoFitGrowth(Construction cons, GeoList geolist) {
		super(cons);

		regMath = new RegressionMath();

		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FitGrowth;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;

		setOnlyOutput(geofunction);
		setDependencies();
	}

	/**
	 * @return best fit growth function
	 */
	public GeoFunction getFitGrowth() {
		return geofunction;
	}

	@Override
	public final void compute() {
		int size = geolist.size();
		double a, b;
		if (!geolist.isDefined() || (size < 2)) {
			geofunction.setUndefined();
			return;
		}

		boolean regok = regMath.doExp(geolist);
		if (regok) {
			a = regMath.getP1();
			b = regMath.getP2();
			b = Math.exp(b);
			MyDouble A = new MyDouble(kernel, a);
			MyDouble B = new MyDouble(kernel, b);
			FunctionVariable X = new FunctionVariable(kernel);
			ExpressionValue expr = new ExpressionNode(kernel, B,
					Operation.POWER, X);
			ExpressionNode node = new ExpressionNode(kernel, A,
					Operation.MULTIPLY, expr);
			Function f = new Function(node, X);
			geofunction.setFunction(f);
			geofunction.setDefined(true);
		} else {
			geofunction.setUndefined();
		}
	}

	@Override
	public double[] getCoeffs() {
		double[] ret = { regMath.getP1(), Math.exp(regMath.getP2()) };
		return ret;
	}

}