/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Algo for intersection of a line with a curve adapted from
 * AlgoIntersectLinePolyLine
 * 
 * @author Michael
 */
public class AlgoIntersectLineCurve extends AlgoIntersectCoordSysCurve {

	private final GeoLine line; // input
	private final AlgoDispatcher algoDispatcher;

	private class Params {
		private final Coords coeffs;
		private ExpressionNode enx;
		private ExpressionNode eny;
		ExpressionNode xFun;
		ExpressionNode yFun;
		FunctionVariable functionVariable;

		public Params(ExpressionNode xFun, ExpressionNode yFun, FunctionVariable functionVariable) {
			this.xFun = xFun;
			this.yFun = yFun;
			this.functionVariable = functionVariable;
			coeffs = line.getCoords();

		}

		public Params(ParametricCurve curve) {
			this(curve.getFun(0).getExpression(),
					curve.getFun(1).getExpression(),
					curve.getFun(0).getFunctionVariable());
		}

		private void multiplyWithLine() {
			if (DoubleUtil.isZero(coeffs.getZ())) {
				enx = xFun.multiply(coeffs.getX());
				eny = yFun.multiply(coeffs.getY());
				enx = enx.plus(eny);
			} else {
				// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
				enx = xFun.multiply(coeffs.getX() / coeffs.getZ());
				eny = yFun.multiply(coeffs.getY() / coeffs.getZ());
				enx = enx.plus(eny).plus(1);
			}


		}
	}

	private ExpressionValue findExpressionValue;
	/**
	 * common constructor
	 * @param c Construction
	 * @param labels labels
	 * @param l line
	 * @param p curve
	 * @param algoDispatcher
	 */
	public AlgoIntersectLineCurve(Construction c, String[] labels, GeoLine l,
			GeoCurveCartesianND p, AlgoDispatcher algoDispatcher) {

		super(c);
		this.algoDispatcher = algoDispatcher;

		outputPoints = createOutputPoints(false);

		this.line = l;
		this.curve = p;

		compute();

		setInputOutput(); // for AlgoElement

		outputPoints.setLabelsMulti(labels);

		update();
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = line;
		input[1] = curve;

		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {
		Params params = new Params(curve);

		params.multiplyWithLine();

		if (curve.isSpline()) {
			findSplineIntersection();
		} else {
			findIntersections(params.enx, params.functionVariable);
		}
	}

	private void findSplineIntersection() {
		findConditional(curve.getFun(0).getExpression());
		if (findExpressionValue == null) {
			return;
		}

		FunctionVariable fv = curve.getFun(0).getFunctionVariable();
		ExpressionNode node = findExpressionValue.wrap();

		MyList conditions = (MyList) node.getLeft();
		MyList polyCurves = (MyList) node.getRight();
		List<Double> result = new ArrayList<>();

		for (int i = 0; i < polyCurves.size(); i++) {
			ExpressionValue curve1 = polyCurves.getItem(i);
			Function function = new Function(kernel, curve1.wrap());
			Params params = new Params(function.getExpression(),
					function.getExpression(), fv);
			params.multiplyWithLine();
			GeoFunction function1 = params.enx.buildFunction(fv);
			Solution soln = new Solution();

			AlgoRootsPolynomial.calcRootsMultiple(function1.getFunction(),
					0, soln, kernel.getEquationSolver());
			soln.sortAndMakeUnique();
			double[] roots = soln.curRoots;
			if (roots != null) {
				for (int j = 0; j < soln.curRealRoots; j++) {
					result.add(roots[i]);
				}
			}

		}
		Log.debug("result: " + result);
	}


	private void findConditional(ExpressionNode enx) {
		findExpressionValue = null;
		enx.inspect(v -> {
			if (v.isOperation(Operation.IF_LIST)) {
				findExpressionValue = v;
				return true;
			}
			return false;
		});
	}

	@Override
	protected boolean inCoordSys(GeoPointND point) {
		return line.isIntersectionPointIncident((GeoPoint) point,
				Kernel.MIN_PRECISION);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("IntersectionOfAandB",
				"Intersection of %0 and %1",
				line.getLabel(tpl),
				curve.getLabel(tpl));
	}

}
