/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Finds all points of inflection of a polynomial wrapped in If[] eg If[0 < x <
 * 10,3x³ - 48x² + 162x + 300]
 * 
 * @author Michael
 */
public class AlgoTurningPointPolynomialInterval extends
		AlgoTurningPointPolynomial {

	private Function interval;

	/**
	 * @param cons
	 *            cons
	 * @param labels
	 *            labels
	 * @param f
	 *            function
	 */
	public AlgoTurningPointPolynomialInterval(Construction cons, String[] labels,
			GeoFunction f) {
		super(cons, labels, f);
	}

	@Override
	public final void compute() {
		if (f.isDefined()) {

			if (yValFunction == null) {
				FunctionVariable fVar = f.getFunction().getFunctionVariable();

				// extract poly from If[0<x<10, poly]
				yValFunction = new Function((ExpressionNode) f
						.getFunctionExpression().getRight(), fVar);

				// extract interval
				interval = new Function((ExpressionNode) f
						.getFunctionExpression().getLeft(), fVar);

			}

			// roots of second derivative
			// (roots without change of sign are removed)
			calcRoots(yValFunction, 2);
		} else {
			curRealRoots = 0;
		}

		setRootPoints(curRoots, curRealRoots);

		// remove points that aren't in the interval
		for (int i = 0; i < rootPoints.length; i++) {
			double xCoord = rootPoints[i].getInhomX();
			if (!interval.evaluateBoolean(xCoord)) {
				rootPoints[i].setUndefined();
			}
		}

	}

}
