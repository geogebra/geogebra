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
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Algo for intersection of a curve with a curve
 * 
 * either uses CAS Solve[] to get all points, or NSolve for one
 * 
 * adapted from AlgoIntersectLineCurve
 * 
 * @author Michael
 */
public class AlgoIntersectCurveCurve extends AlgoIntersectCoordSysCurve
		implements UsesCAS {

	private GeoCurveCartesianND curve2;
	private GeoNumberValue t1;
	private GeoNumberValue t2;

	// numeric = false is too slow on MPReduce (ggb42). OK to set false for Giac
	private boolean numeric = false;

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
			GeoCurveCartesianND c1, GeoCurveCartesianND c2) {

		super(c);

		this.curve = c1;
		this.curve2 = c2;
		outputPoints = createOutputPoints(is3d());

		compute();

		setInputOutput(); // for AlgoElement

		outputPoints.setLabelsMulti(labels);

		update();
	}

	private boolean is3d() {
		return curve.isGeoElement3D() || curve2.isGeoElement3D();
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
			GeoCurveCartesianND c1, GeoCurveCartesianND c2, GeoNumberValue t1,
			GeoNumberValue t2) {

		super(c);

		this.curve = c1;
		this.curve2 = c2;
		outputPoints = createOutputPoints(is3d());

		this.t1 = t1;
		this.t2 = t2;
		numeric = true;

		compute();

		setInputOutput(); // for AlgoElement

		outputPoints.setLabelsMulti(labels);

		update();
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

		if (numeric) {

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
			GeoPointND point = outputPoints.getElement(index);

			index++;

			checkPointInRange(x1, y1, point);
		} else {

			// use CAS Solver
			// works well for polynomials to get all roots

			String fv1 = curve.getFun(0).getFunctionVariable()
					.toString(StringTemplate.defaultTemplate);
			String fv2 = curve2.getFun(1).getFunctionVariable()
					.toString(StringTemplate.defaultTemplate);

			// toString() returns with variables in
			// for most cases, toValueString() with values substituted is better
			// because:
			// * can get two values returned, eg Solve[{3 + t - d=t2^2 - 1,7 / 2
			// - t=t2^2 - 2t2},{t,t2}] when d=8
			// * can get equations that we can't solve exactly

			// use StringTemplate that gives 3 not 3.00000000000000
			int dim = is3d() ? 3 : 2;
			String[] c1 = new String[dim];
			String[] c2 = new String[dim];
			for (int i = 0; i < dim; i++) {
				c1[i] = curve.getFun(i).toValueString(
						StringTemplate.fullFigures(StringType.GEOGEBRA_XML));
				c2[i] = curve2.getFun(i).toValueString(
						StringTemplate.fullFigures(StringType.GEOGEBRA_XML));
			}

			// it's likely that both curves have parameter 't'
			if (fv1.equals(fv2)) {
				fv2 = fv2 + "2"; // eg t -> t2
				for (int i = 0; i < dim; i++) {
					c2[i] = c2[i].replaceAll(fv1, fv2);
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Solve[{");
			for (int i = 0; i < dim; i++) {
				if (i != 0) {
					sb.append(',');
				}
				sb.append(c1[i]);
				sb.append('=');
				sb.append(c2[i]);
			}
			sb.append("},{");
			sb.append(fv1);
			sb.append(',');
			sb.append(fv2);
			sb.append("}]");

			// Log.debug(sb.toString());

			String result = "";
			try {
				result = kernel.evaluateGeoGebraCAS(sb.toString(), null);
			} catch (Throwable e) {
				// other points are undefined
				for (int i = 0; i < outputPoints.size(); i++) {
					outputPoints.getElement(i).setUndefined();
				}

				Log.debug(e);
				return;
			}

			// eg {{ t = 3 / 2, t2 = 1 / 2}}

			// result can have eg 1/2 or sqrt(5) in so needs parsing
			AlgebraProcessor ap = kernel.getAlgebraProcessor();

			int firstBrace = result.indexOf("{");
			int currentBrace = result.indexOf("{", firstBrace + 1);

			while (currentBrace > -1) {
				int nextComma = result.indexOf(",", currentBrace + 1);
				int nextCloseBrace = result.indexOf("}", currentBrace + 1);

				if (nextComma > -1 && nextCloseBrace > -1) {

					outputPoints.adjustOutputSize(index + 1);
					GeoPointND point = outputPoints.getElement(index);
					index++;

					// eg t=3/2
					String s1 = result.substring(currentBrace + 1, nextComma)
							.replaceAll(" ", "");
					// eg t2=1/2
					String s2 = result.substring(nextComma + 1, nextCloseBrace)
							.replaceAll(" ", "");

					if (s1.startsWith(fv1 + "=") && s2.startsWith(fv2 + "=")) {
						double p1 = ap.evaluateToDouble(
								s1.substring(fv1.length() + 1), true, null);
						double p2 = ap.evaluateToDouble(
								s2.substring(fv2.length() + 1), true, null);

						checkPointInRange(p1, p2, point);

					} else if (s1.startsWith(fv2 + "=")
							&& s2.startsWith(fv1 + "=")) {
						double p2 = ap.evaluateToDouble(
								s1.substring(fv2.length() + 1), true, null);
						double p1 = ap.evaluateToDouble(
								s2.substring(fv1.length() + 1), true, null);

						checkPointInRange(p1, p2, point);

					} else {
						Log.debug("problem: " + s1 + " " + s2);
						point.setUndefined();
					}

					// if this is -1, we're done so finish while() loop
					currentBrace = result.indexOf("{", currentBrace + 1);
				} else {
					// something's gone wrong
					Log.debug("problem with result");
					currentBrace = -1;
				}
			}
		}

		// Log.debug(index+" "+outputPoints.size());

		// other points are undefined
		for (; index < outputPoints.size(); index++) {
			// Log.debug("setting undefined "+index);
			outputPoints.getElement(index).setUndefined();
		}
	}

	private void checkPointInRange(double p1, double p2, GeoPointND point) {
		// check parameters in range
		if (DoubleUtil.isGreaterEqual(p1, curve.getMinParameter())
				&& DoubleUtil.isGreaterEqual(curve.getMaxParameter(), p1)
				&& DoubleUtil.isGreaterEqual(p2, curve2.getMinParameter())
				&& DoubleUtil.isGreaterEqual(curve2.getMaxParameter(), p2)) {
			FunctionVariable fv = curve.getFun(0).getFunctionVariable();

			ExpressionNode xFun = curve.getFun(0).getExpression();
			ExpressionNode yFun = curve.getFun(1).getExpression();
			double z = 0;
			fv.set(p1);
			if (curve.getDimension() > 2) {
				z = curve.getFun(2).getExpression().evaluateDouble();
			}
			point.setCoords(xFun.evaluateDouble(), yFun.evaluateDouble(), z, 1.0);
			// for symbolic all 3 coords are checked by the CAS
			if (numeric && !onCurve2(point, p2)) {
				point.setUndefined();
			}
		} else {
			point.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("IntersectionOfAandB",
				"Intersection of %0 and %1",
				curve.getLabel(tpl),
				curve2.getLabel(tpl));
	}

	protected boolean onCurve2(GeoPointND point, double param) {
		return DoubleUtil.isEqual(curve2.getFun(2).value(param), point.getInhomZ());
	}

}
