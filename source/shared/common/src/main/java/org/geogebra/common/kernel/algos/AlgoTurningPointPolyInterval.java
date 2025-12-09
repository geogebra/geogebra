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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoFunctionable;

/**
 * Finds all points of inflection of a polynomial wrapped in If[] eg If[0 &lt; x
 * &lt; 10,3x^3 - 48x^2 + 162x + 300]
 * 
 * @author Michael
 */
public class AlgoTurningPointPolyInterval
		extends AlgoTurningPointPolynomial {

	private Function interval;

	/**
	 * @param cons
	 *            cons
	 * @param labels
	 *            labels
	 * @param f
	 *            function
	 */
	public AlgoTurningPointPolyInterval(Construction cons,
			String[] labels, GeoFunctionable f) {
		super(cons, labels, f);
	}

	@Override
	public final void compute() {
		if (f.isDefined()) {
			Function geoFunction = f.getFunction();
			ExpressionNode polyExpression = (ExpressionNode) geoFunction
					.getFunctionExpression().getRight();
			ExpressionNode condExpression = (ExpressionNode) geoFunction
					.getFunctionExpression().getLeft();
			if (yValFunction == null
					|| yValFunction.getExpression() != polyExpression
					|| interval.getFunctionExpression() != condExpression) {
				FunctionVariable fVar = geoFunction.getFunctionVariable();

				// extract poly from If[0<x<10, poly]
				yValFunction = new Function(polyExpression, fVar);

				// extract interval
				interval = new Function(condExpression, fVar);

			}

			// roots of second derivative
			// (roots without change of sign are removed)
			calcRoots(yValFunction, 2);
		} else {
			solution.resetRoots();
		}

		setRootPoints(solution.curRoots, solution.curRealRoots);

		// remove points that aren't in the interval
		for (int i = 0; i < rootPoints.length; i++) {
			double xCoord = rootPoints[i].getInhomX();
			if (!interval.evaluateBoolean(xCoord)) {
				rootPoints[i].setUndefined();
			}
		}

	}

}
