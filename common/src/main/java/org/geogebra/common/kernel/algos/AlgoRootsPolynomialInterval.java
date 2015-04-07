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
 * 
 * 
 * @author Michael
 */
public class AlgoRootsPolynomialInterval extends AlgoRootsPolynomial {

	private Function intervalFun;
	private Function interval;

	/**
	 * @param cons
	 *            cons
	 * @param labels
	 *            labels
	 * @param f
	 *            function
	 */
	public AlgoRootsPolynomialInterval(Construction cons, String[] labels,
			GeoFunction f) {
		super(cons, labels, f);
	}

	@Override
	public void compute() {

		super.compute();

		// remove points that aren't in the interval
		for (int i = 0; i < rootPoints.length; i++) {
			double xCoord = rootPoints[i].getInhomX();
			if (!interval.evaluateBoolean(xCoord)) {
				rootPoints[i].setUndefined();
			}
		}
	}

	@Override
	protected void computeRoots() {
		if (f.isDefined()) {
			if (intervalFun == null) {
				FunctionVariable fVar = f.getFunction().getFunctionVariable();
				// extract poly from If[0<x<10, poly]
				intervalFun = new Function((ExpressionNode) f
						.getFunctionExpression().getRight(), fVar);

				// extract interval
				interval = new Function((ExpressionNode) f
						.getFunctionExpression().getLeft(), fVar);

			}
			// get polynomial factors and calc roots
			calcRoots(intervalFun, 0);
		} else {
			curRealRoots = 0;
		}
	}

}
