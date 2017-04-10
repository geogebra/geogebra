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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;

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
			GeoFunction f,
			GeoLine g) {
		super(cons, labels, !cons.isSuppressLabelsActive(), f, null, g);
	}

	@Override
	public void compute() {

		super.compute();

		if (line != null && Kernel.isZero(line.y)) {
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
		return line == null ? Commands.Root : Commands.Intersect;
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
		ExpressionNode polyExpression = (ExpressionNode) f
				.getFunctionExpression().getRight();
		ExpressionNode condExpression = (ExpressionNode) f
				.getFunctionExpression().getLeft();
		if (intervalFun == null
				|| intervalFun.getFunctionExpression() != polyExpression
				|| interval.getExpression() != condExpression) {
			FunctionVariable fVar = f.getFunction().getFunctionVariable();
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
	public final String toString(StringTemplate tpl) {
		if (line != null) {
			return getLoc().getPlain("IntersectionPointOfAB",
					input[0].getLabel(tpl), input[1].getLabel(tpl));
		}
		// Root of ...
		return super.toString(tpl);
	}

}
