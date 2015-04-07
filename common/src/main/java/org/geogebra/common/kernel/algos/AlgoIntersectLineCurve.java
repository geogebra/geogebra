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
import java.util.Iterator;
import java.util.LinkedList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.Operation;

/**
 * Algo for intersection of a line with a curve adapted from
 * AlgoIntersectLinePolyLine
 * 
 * @author Michael
 */
public class AlgoIntersectLineCurve extends AlgoElement {

	private GeoLine line; // input
	@SuppressWarnings("javadoc")
	protected GeoCurveCartesian curve;
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

		setLabels(labels);

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
	 * @param labels
	 *            labels
	 */
	protected void setLabels(String[] labels) {
		// if only one label (e.g. "A") for more than one output, new labels
		// will be A_1, A_2, ...
		if (labels != null && labels.length == 1 &&
		// outputPoints.size() > 1 &&
				labels[0] != null && !labels[0].equals("")) {
			outputPoints.setIndexLabels(labels[0]);
		} else {

			outputPoints.setLabels(labels);
			outputPoints.setIndexLabels(outputPoints.getElement(0).getLabel(
					StringTemplate.defaultTemplate));
		}
	}

	/**
	 * 
	 * @return handler for output points
	 */
	protected OutputHandler<GeoElement> createOutputPoints() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
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

		ExpressionNode xFun = curve.getFunX().getExpression();
		ExpressionNode yFun = curve.getFunY().getExpression();

		FunctionVariable fv = curve.getFunX().getFunctionVariable();

		// substitute x = x(t), y=y(t) into
		// ax + by + c
		ExpressionNode enx, eny;

		if (Kernel.isZero(coeffs.getZ())) {
			enx = new ExpressionNode(kernel,
					new MyDouble(kernel, coeffs.getX()), Operation.MULTIPLY,
					xFun);
			eny = new ExpressionNode(kernel,
					new MyDouble(kernel, coeffs.getY()), Operation.MULTIPLY,
					yFun);
			enx = enx.plus(eny);

		} else {
			// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
			enx = new ExpressionNode(kernel, new MyDouble(kernel, coeffs.getX()
					/ coeffs.getZ()), Operation.MULTIPLY, xFun);
			eny = new ExpressionNode(kernel, new MyDouble(kernel, coeffs.getY()
					/ coeffs.getZ()), Operation.MULTIPLY, yFun);
			enx = enx.plus(eny).plus(1);

		}

		// wrap in a function
		GeoFunction geoFun = enx.buildFunction(fv);

		double[] roots = null;
		int outputSize = -1;

		ArrayList<Double> polyRoots = new ArrayList<Double>();

		if (geoFun.isPolynomialFunction(true)) {
			// AbstractApplication.debug("trying polynomial");

			LinkedList<PolyFunction> factorList = geoFun.getFunction()
					.getPolynomialFactors(false);

			if (factorList != null) {
				// compute the roots of every single factor
				Iterator<PolyFunction> it = factorList.iterator();
				while (it.hasNext()) {
					PolyFunction polyFun = it.next();

					if (polyFun.updateCoeffValues()) {
						// now let's compute the roots of this factor
						// compute all roots of polynomial polyFun
						roots = polyFun.getCoeffsCopy();
						int n = cons.getKernel().getEquationSolver()
								.polynomialRoots(roots, true);

						for (int i = 0; i < n; i++) {
							polyRoots.add(roots[i]);
						}
					} else {
						outputSize = -1;
						break;
					}

				}
			}

		}

		if (polyRoots.size() > 0) {

			outputSize = polyRoots.size();

			roots = new double[outputSize];

			for (int i = 0; i < outputSize; i++) {
				roots[i] = polyRoots.get(i);
			}
		} else {
			// polynomial method hasn't worked
			// AbstractApplication.debug("trying non-polynomial");

			// solve a x(t) + b y(t) + c = 0 (for t)
			roots = AlgoRoots.findRoots(geoFun, curve.getMinParameter(),
					curve.getMaxParameter(), 100);

			outputSize = roots == null || roots.length == 0 ? 1 : roots.length;

		}

		// update and/or create points
		outputPoints.adjustOutputSize(outputSize);

		// affect new computed points
		int index = 0;
		if (roots != null) {
			for (index = 0; index < outputSize; index++) {
				double paramVal = roots[index];
				GeoPoint point = (GeoPoint) outputPoints.getElement(index);

				if (paramVal < curve.getMinParameter()
						|| paramVal > curve.getMaxParameter()) {
					// intersection is not on the curve
					point.setUndefined();
				} else {

					// substitute parameter back into curve to get cartesian
					// coords
					fv.set(paramVal);
					point.setCoords(xFun.evaluateDouble(),
							yFun.evaluateDouble(), 1.0);

					// test the intersection point
					// this is needed for the intersection of Segments, Rays
					if (!(line.isIntersectionPointIncident(point,
							Kernel.MIN_PRECISION))) {
						point.setUndefined();
					}
				}

				// AbstractApplication.debug(xFun.evaluateDouble()+","+
				// yFun.evaluateDouble());
			}
		}

		// other points are undefined
		for (; index < outputPoints.size(); index++) {
			// AbstractApplication.debug("setting undefined "+index);
			outputPoints.getElement(index).setUndefined();
		}
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("IntersectionPointOfAB",
				((GeoElement) line).getLabel(tpl),
				((GeoElement) curve).getLabel(tpl));
	}

	// TODO Consider locusequability

}
