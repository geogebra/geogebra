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
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

/**
 * Algo for intersection of a curve with a curve
 * 
 * uses bivariate Newton iteration
 * 
 * @author Michael
 */
public class AlgoIntersectCurveCurve extends AlgoIntersectLineCurve
		implements UsesCAS {

	private GeoCurveCartesianND curve2;
	private GeoNumberValue t1, t2;

	/**
	 * common constructor
	 * 
	 * @param c
	 *            Construction
	 * @param labels
	 *            labels
	 * @param c1
	 *            curve 1
	 * @param c2
	 *            curve 2
	 */
	public AlgoIntersectCurveCurve(Construction c, String[] labels,
			GeoCurveCartesian c1, GeoCurveCartesian c2) {

		super(c);
		cons.addCASAlgo(this);

		outputPoints = createOutputPoints();

		this.curve = c1;
		this.curve2 = c2;

		compute();

		setInputOutput(); // for AlgoElement

		outputPoints.setLabelsMulti(labels);

		update();
	}

	/**
	 * @param c
	 *            Construction
	 * @param labels
	 *            labels
	 * @param c1
	 *            curve 1
	 * @param c2
	 *            curve 2
	 * @param t1
	 *            path parameter to start iteration from
	 * @param t2
	 *            path parameter to start iteration from
	 */
	public AlgoIntersectCurveCurve(Construction c, String[] labels,
			GeoCurveCartesian c1, GeoCurveCartesian c2, GeoNumberValue t1,
			GeoNumberValue t2) {

		super(c);
		cons.addCASAlgo(this);
		outputPoints = createOutputPoints();

		this.curve = c1;
		this.curve2 = c2;

		this.t1 = t1;
		this.t2 = t2;

		compute();

		setInputOutput(); // for AlgoElement

		outputPoints.setLabelsMulti(labels);

		update();
	}

	/**
	 * 
	 * @return handler for output points
	 */
	@Override
	protected OutputHandler<GeoElement> createOutputPoints() {
		return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			@Override
			public GeoPoint newElement() {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectCurveCurve.this);
				return p;
			}
		});
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		if (t1 != null) {
			input = new GeoElement[4];
			input[2] = t1.toGeoElement();
			input[3] = t2.toGeoElement();
		} else {
			input = new GeoElement[2];
		}

		input[0] = curve;
		input[1] = curve2;

		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {

		int index = 0;



		// use bivariate Newton iteration
		// want to solve system of equations funx1=funx2, funy1=funy2
		// to find where curves cross
		// http://introcs.cs.princeton.edu/java/96optimization/

		Function funx1 = curve.getFun(0);
		Function funx2 = curve2.getFun(0);
		Function funy1 = curve.getFun(1);
		Function funy2 = curve2.getFun(1);

		ExpressionNode enx1 = funx1.getExpression();
		ExpressionNode eny1 = funy1.getExpression();
		ExpressionNode enx2 = funx2.getExpression();
		ExpressionNode eny2 = funy2.getExpression();

		FunctionVariable fVarx1 = funx1.getFunctionVariable();
		FunctionVariable fVarx2 = funx2.getFunctionVariable();
		FunctionVariable fVary1 = funy1.getFunctionVariable();
		FunctionVariable fVary2 = funy2.getFunctionVariable();

		// Jacobian matrix
		// partial derivative of eg enx2 wrt
		// curve.getFunX().getFunctionVariable() is zero, so ignore
		ExpressionNode j00 = enx1.derivative(fVarx1, kernel);
		ExpressionNode minusj10 = enx2.derivative(fVarx2, kernel);
		ExpressionNode j01 = eny1.derivative(fVary1, kernel);
		ExpressionNode minusj11 = eny2.derivative(fVary2, kernel);

		// Log.debug(j00.toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML)));
		// Log.debug(minusj10.toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML)));
		// Log.debug(j01.toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML)));
		// Log.debug(minusj11.toValueString(StringTemplate.fullFigures(StringType.GEOGEBRA_XML)));

		// starting point for iteration
		double x1 = t1.getDouble();
		double y1 = t2.getDouble();

		// just need to be different to x0, y0
		double x0 = x1 + 1;
		double y0 = y1 + 1;

		int count = 0;
		int maxCount = 100;

		double EPS = 1e-15;

		while (count < maxCount
				&& (Math.abs(x0 - x1) > EPS || Math.abs(y0 - y1) > EPS)) {

			count++;
			x0 = x1;
			y0 = y1;

			fVarx1.set(x0);
			fVarx2.set(y0);
			fVary1.set(x0);
			fVary2.set(y0);

			double j00Eval = j00.evaluateDouble();
			double j01Eval = j01.evaluateDouble();
			double j10Eval = -minusj10.evaluateDouble();
			double j11Eval = -minusj11.evaluateDouble();

			// Log.debug(j00Eval+" "+j10Eval+" "+j01Eval+" "+j11Eval);

			double f1Eval = enx1.evaluateDouble() - enx2.evaluateDouble();
			double f2Eval = eny1.evaluateDouble() - eny2.evaluateDouble();

			// Log.debug(f1Eval + " " + f2Eval);

			double determinant = j00Eval * j11Eval - j01Eval * j10Eval;

			// x_(k+1) = x_k - J(x_k)^(-1) * f(x_k)
			x1 = x0 - (j11Eval * f1Eval - j10Eval * f2Eval) / determinant;
			y1 = y0 - (j00Eval * f2Eval - j01Eval * f1Eval) / determinant;

			// Log.debug(count+" "+x1+" "+y1);
		}

		if (count >= maxCount || Double.isNaN(x1) || Double.isNaN(y1)) {
			// iteration failed to converge
			x1 = Double.NaN;
			y1 = Double.NaN;
		}

		outputPoints.adjustOutputSize(index + 1);
		GeoPoint point = (GeoPoint) outputPoints.getElement(index);

		index++;

		checkPointInRange(x1, y1, point);

		// if (point.isDefined()) {
		// Log.debug("("+point.inhomX+","+point.inhomY+")");
		// } else {
		// Log.debug("out of range");
		// }



		// Log.debug(index+" "+outputPoints.size());

		// other points are undefined
		for (; index < outputPoints.size(); index++) {
			// Log.debug("setting undefined "+index);
			outputPoints.getElement(index).setUndefined();
		}
	}

	private void checkPointInRange(double p1, double p2, GeoPoint point) {
		// check parameters in range
		if (Kernel.isGreaterEqual(p1, curve.getMinParameter())
				&& Kernel.isGreaterEqual(curve.getMaxParameter(), p1)
				&& Kernel.isGreaterEqual(p2, curve2.getMinParameter())
				&& Kernel.isGreaterEqual(curve2.getMaxParameter(), p2)) {

			double x = curve.getFun(0).value(p1);
			double y = curve.getFun(1).value(p1);
			// Log.debug("in range: ("+x+", "+y+")");

			point.setCoords(x, y, 1.0);

		} else {
			point.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("IntersectionPointOfAB",
				((GeoElement) curve).getLabel(tpl),
				((GeoElement) curve2).getLabel(tpl));
	}

}
