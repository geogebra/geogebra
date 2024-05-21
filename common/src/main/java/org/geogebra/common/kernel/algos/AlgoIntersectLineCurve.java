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
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Algo for intersection of a line with a curve adapted from
 * AlgoIntersectLinePolyLine
 * 
 * @author Michael
 */
public class AlgoIntersectLineCurve extends AlgoIntersectCoordSysCurve {

	private final GeoLine line; // input

	/**
	 * common constructor
	 * @param c Construction
	 * @param labels labels
	 * @param l line
	 * @param p curve
	 */
	public AlgoIntersectLineCurve(Construction c, String[] labels, GeoLine l,
			GeoCurveCartesianND p) {

		super(c);

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
		ExpressionNode xFun = curve.getFun(0).getExpression();
		ExpressionNode yFun = curve.getFun(1).getExpression();
		FunctionVariable functionVariable = curve.getFun(0).getFunctionVariable();

		if (curve.isSpline()) {
			Spline spline = new Spline(xFun, yFun, functionVariable);
			findIntersectionWithSpline(spline, functionVariable, coeffs);
		} else {
			findIntersections(getMultiplyExpression(xFun, yFun, coeffs),
					functionVariable);
		}
	}

	static ExpressionNode getMultiplyExpression(ExpressionNode xFun,
			ExpressionNode yFun, Coords coeffs) {
		ExpressionNode enx, eny;
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
		return enx;
	}

	private void findIntersectionWithSpline(Spline spline,
			FunctionVariable functionVariable, Coords coeffs) {
		IntersectLineSpline lineSpline =
				new IntersectLineSpline(spline, coeffs, kernel.getEquationSolver());

		List<Double> roots = lineSpline.compute();

		outputPoints.adjustOutputSize(roots.size());
		if (!roots.isEmpty()) {
			for (int i = 0; i < roots.size(); i++) {
				getCoordsBySubstitution(functionVariable, roots.get(i),
						outputPoints.getElement(i), curve);
			}
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
