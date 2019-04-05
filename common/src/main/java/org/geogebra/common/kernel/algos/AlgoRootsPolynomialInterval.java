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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.util.DoubleUtil;

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
			GeoFunctionable f) {
		super(cons, labels, f, true);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param f
	 *            function
	 * @param g
	 *            line
	 */
	public AlgoRootsPolynomialInterval(Construction cons, String[] labels,
			GeoFunctionable f, GeoLine g) {
		super(cons, labels, !cons.isSuppressLabelsActive(), f, null, g);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param conditional
	 *            conditional function
	 * @param polynomial
	 *            polynomial function
	 */
	public AlgoRootsPolynomialInterval(Construction cons, String[] labels,
			GeoFunctionable conditional, GeoFunctionable polynomial) {
		super(cons, labels, !cons.isSuppressLabelsActive(), conditional, polynomial, null);
	}

	@Override
	public void compute() {

		super.compute();

		if (line != null && DoubleUtil.isZero(line.y)) {
			// the vertical line solution is filtered in super.compute already
			return;
		}
		// remove points that aren't in the interval
		for (int i = 0; i < rootPoints.length; i++) {
			double xCoord = rootPoints[i].getInhomX();
			if (interval == null || !interval.evaluateBoolean(xCoord)) {
				rootPoints[i].setUndefined();
			}
		}
	}

	@Override
	public Commands getClassName() {
		return line == null && g == null ? Commands.Root : Commands.Intersect;
	}

	@Override
	protected void computeRoots() {
		if (f.isDefined()) {
			updateIntervalFun();
			// get polynomial factors and calc roots
			calcRoots(intervalFun, 0);
		} else {
			solution.resetRoots();
		}
	}

	private void updateIntervalFun() {
		Function function = f.getFunction();
		ExpressionNode polyExpression = function
				.getFunctionExpression().getRight()
				.wrap();
		ExpressionNode condExpression = function
				.getFunctionExpression().getLeft()
				.wrap();
		if (intervalFun == null
				|| intervalFun.getFunctionExpression() != polyExpression
				|| interval.getExpression() != condExpression) {
			FunctionVariable fVar = function
					.getFunctionVariable();
			// extract poly from If[0<x<10, poly]
			intervalFun = new Function(polyExpression, fVar);

			// extract interval
			interval = new Function(condExpression, fVar);
		}
	}

	@Override
	protected void updateDiffLine() {
		updateIntervalFun();
		Function.difference(intervalFun, line, diffFunction);
	}

	@Override
	protected void updateDiffFunctions() {
		updateIntervalFun();
		Function.difference(intervalFun, g.getFunction(), diffFunction);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		if (line != null || g != null) {
			return getLoc().getPlainDefault("IntersectionPointOfAB",
					"Intersection point of %0, %1", input[0].getLabel(tpl),
					input[1].getLabel(tpl));
		}
		// Root of ...
		return super.toString(tpl);
	}

}
