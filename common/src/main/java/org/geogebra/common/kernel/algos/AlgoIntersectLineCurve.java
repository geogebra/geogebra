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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;

/**
 * Algo for intersection of a line with a curve adapted from
 * AlgoIntersectLinePolyLine
 * 
 * @author Michael
 */
public class AlgoIntersectLineCurve extends AlgoIntersectCoordSysCurve {

	private GeoLine line; // input

	@SuppressWarnings("javadoc")
	protected OutputHandler<GeoElement> outputPoints; // output

	/**
	 * common constructor
	 * 
	 * @param c
	 *            Construction
	 * @param labels
	 *            labels
	 * @param l
	 *            line
	 * @param p
	 *            curve
	 */
	public AlgoIntersectLineCurve(Construction c, String[] labels, GeoLine l,
			GeoCurveCartesian p) {

		super(c);

		outputPoints = createOutputPoints();

		this.line = l;
		this.curve = p;

		compute();

		setInputOutput(); // for AlgoElement

		outputPoints.setLabelsMulti(labels);

		update();
	}

	/**
	 * @param c
	 *            cons
	 */
	public AlgoIntersectLineCurve(Construction c) {
		super(c);
	}

	/**
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoElement> createOutputPoints() {
		return new OutputHandler<>(new ElementFactory<GeoElement>() {
			@Override
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLineCurve.this);
				return p;
			}
		});
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

		FunctionVariable fv = curve.getFun(0).getFunctionVariable();

		// substitute x = x(t), y=y(t) into
		// ax + by + c
		ExpressionNode enx, eny;

		if (DoubleUtil.isZero(coeffs.getZ())) {
			enx = new ExpressionNode(kernel,
					new MyDouble(kernel, coeffs.getX()), Operation.MULTIPLY,
					xFun);
			eny = new ExpressionNode(kernel,
					new MyDouble(kernel, coeffs.getY()), Operation.MULTIPLY,
					yFun);
			enx = enx.plus(eny);

		} else {
			// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
			enx = new ExpressionNode(kernel,
					new MyDouble(kernel, coeffs.getX() / coeffs.getZ()),
					Operation.MULTIPLY, xFun);
			eny = new ExpressionNode(kernel,
					new MyDouble(kernel, coeffs.getY() / coeffs.getZ()),
					Operation.MULTIPLY, yFun);
			enx = enx.plus(eny).plus(1);

		}

		findIntersections(enx, fv);
	}

	@Override
	protected void updatePoint(GeoPointND point, double paramVal,
			FunctionVariable fv) {
		ExpressionNode xFun = curve.getFun(0).getExpression();
		ExpressionNode yFun = curve.getFun(1).getExpression();
		fv.set(paramVal);
		point.setCoords(xFun.evaluateDouble(), yFun.evaluateDouble(), 1.0);

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
				((GeoElement) line).getLabel(tpl),
				((GeoElement) curve).getLabel(tpl));
	}

	@Override
	protected OutputHandler<GeoElement> getOutputPoints() {
		return outputPoints;
	}

}
