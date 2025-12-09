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
 * Fits an a*e^(b*x) to a list of pints. Adapted from AlgoFitLine and
 * AlgoPolynomialFromCoordinates (Borcherds)
 * 
 * @author Hans-Petter Ulven
 * @version 24.04.08
 */
public class AlgoFitExp extends AlgoElement implements FitAlgo {

	private GeoList geolist; // input
	private GeoFunction geofunction; // output
	private RegressionMath regMath;

	/**
	 * @param cons
	 *            construction
	 * @param geolist
	 *            input points
	 */
	public AlgoFitExp(Construction cons, GeoList geolist) {
		super(cons);

		regMath = new RegressionMath();

		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FitExp;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;

		setOnlyOutput(geofunction);
		setDependencies();
	}

	public GeoFunction getFitExp() {
		return geofunction;
	}

	@Override
	public final void compute() {
		int size = geolist.size();
		double a, b;
		if (!geolist.isDefined() || (size < 2)) { // 24.04.08:2
			geofunction.setUndefined();
			return;
		}

		boolean regok = regMath.doExp(geolist);
		if (regok) {
			a = regMath.getP1();
			b = regMath.getP2();
			MyDouble A = new MyDouble(kernel, a);
			MyDouble B = new MyDouble(kernel, b);
			// 24.04.08: not: MyDouble E=new MyDouble(kernel,Math.E);
			FunctionVariable X = new FunctionVariable(kernel);
			ExpressionValue expr = new ExpressionNode(kernel, B,
					Operation.MULTIPLY, X);
			expr = new ExpressionNode(kernel, expr, Operation.EXP, null);
			// 24.04.08: changed 2.71..to "e" with the null trick!

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
		double[] ret = { regMath.getP1(), regMath.getP2() };
		return ret;
	}

}