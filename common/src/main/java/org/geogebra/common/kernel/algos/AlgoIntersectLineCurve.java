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

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for intersection of a line with a curve adapted from
 * AlgoIntersectLinePolyLine
 * 
 * @author Michael
 */
public class AlgoIntersectLineCurve extends AlgoIntersectCoordSysCurve {

	private final GeoLine line; // input
	private final AlgoDispatcher algoDispatcher;

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
		Coords coeffs = line.getCoords();
		ExpressionNode fun1 = curve.getFun(0).getExpression();
		ExpressionNode fun2 = curve.getFun(1).getExpression();
		FunctionVariable functionVariable = curve.getFun(0).getFunctionVariable();

		if (curve.isSpline()) {
			Spline spline = new Spline(fun1, fun2, functionVariable);
			IntersectPolyCurvesAndLine polyCurvesAndLine =
					new IntersectPolyCurvesAndLine(spline, coeffs, kernel.getEquationSolver());
			List<Double> roots = polyCurvesAndLine.compute();
			if (roots.isEmpty()) {
				outputPoints.adjustOutputSize(0);
			} else {
				updatePoints(roots);
			}

		} else {
			PolyCurveParams params = new PolyCurveParams(curve, coeffs);
			params.multiplyWithLine();
			findIntersections(params.getEnX(), params.functionVariable);
		}
	}

	private void updatePoints(List<Double> roots) {
		outputPoints.adjustOutputSize(roots.size());
		FunctionVariable fv = curve.getFun(0).getFunctionVariable();
		for (int i = 0; i < roots.size(); i++) {
			getCoordsBySubstitution(fv, roots.get(i),
					outputPoints.getElement(i), curve);
		}
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
