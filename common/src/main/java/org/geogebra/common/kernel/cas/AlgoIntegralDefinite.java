/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.LegendreGaussIntegrator;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.Bounds;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
@SuppressWarnings("deprecation")
public class AlgoIntegralDefinite extends AlgoUsingTempCASalgo
		implements DrawInformationAlgo, AlgoIntegralDefiniteInterface {

	private GeoFunction f; // input
	private NumberValue a; // input
	private NumberValue b; // input
	private GeoBoolean evaluate; // input
	private GeoElement ageo;
	private GeoElement bgeo;
	private GeoNumeric n; // output g = integral(f(x), x, a, b)
	private boolean numeric;
	// for symbolic integration
	private GeoFunction symbIntegral;
	private boolean evaluateNumerically;
	private boolean validButUndefined = false;

	// for numerical adaptive GaussQuad integration
	private static final int FIRST_ORDER = 3;
	private static final int SECOND_ORDER = 5;
	private static final int MIN_ITER = 1;
	private static final int MAX_ITER = 5;
	private static LegendreGaussIntegrator firstGauss;
	private static LegendreGaussIntegrator secondGauss;
	private static int adaptiveGaussQuadCounter = 0;
	private static final int MAX_GAUSS_QUAD_CALLS = 500;
	private static final int STANDARD_MULTIPLIER = 1;
	// freehand functions tend to be less smooth
	private static final int FREEHAND_MULTIPLIER = 10;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param a
	 *            from number
	 * @param b
	 *            to number
	 * @param numeric
	 *            true to use numeric method
	 */
	public AlgoIntegralDefinite(Construction cons, String label, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b, boolean numeric) {
		this(cons, f, a, b, null, numeric);
		this.numeric = numeric;
		n.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param a
	 *            from number
	 * @param b
	 *            to number
	 * @param evaluate
	 *            true to evaluate, false to shade only
	 */
	public AlgoIntegralDefinite(Construction cons, String label, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b, GeoBoolean evaluate) {
		this(cons, f, a, b, evaluate);
		n.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param a
	 *            from number
	 * @param b
	 *            to number
	 * @param evaluate
	 *            true to evaluate, false to shade only
	 */
	public AlgoIntegralDefinite(Construction cons, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b, GeoBoolean evaluate) {
		this(cons, f, a, b, evaluate, false);

	}

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param a
	 *            from number
	 * @param b
	 *            to number
	 * @param num
	 *            numeric true to use numeric method
	 * @param evaluate
	 *            true to evaluate, false to shade only
	 */
	public AlgoIntegralDefinite(Construction cons, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b, GeoBoolean evaluate,
			boolean num) {
		super(cons);
		evaluateNumerically = num;
		this.f = f;
		n = new GeoNumeric(cons); // output
		this.a = a;
		this.b = b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		this.evaluate = evaluate;

		// always use numerical algorithm in web (CAS much too slow)
		if (!kernel.getApplication().nativeCAS()
				&& !Double.isInfinite(a.evaluateDouble())
				&& !Double.isInfinite(b.evaluateDouble())) {
			evaluateNumerically = true;
		}

		// create helper algorithm for symbolic integral
		// don't use symbolic integral for conditional functions
		// or if it should not be evaluated (i.e. a shade-only integral)
		if ((evaluate == null || evaluate.getBoolean())
				&& !f.isGeoFunctionConditional() && !f.isFreehandFunction()
				&& !f.includesDivisionByVar() && !evaluateNumerically) {
			refreshCASResults();
		}

		setInputOutput(); // for AlgoElement
		compute();
		n.setDrawable(true);
	}

	/**
	 * @param f
	 *            function
	 * @param a
	 *            from number
	 * @param b
	 *            to number
	 * @param evaluate
	 *            true to evaluate, false to shade only
	 */
	public AlgoIntegralDefinite(GeoFunction f, NumberValue a, NumberValue b,
			GeoBoolean evaluate) {
		super(f.getConstruction(), false);
		this.f = f;
		this.a = a;
		this.b = b;
		this.evaluate = evaluate;
	}

	@Override
	public GetCommand getClassName() {
		return numeric ? Commands.NIntegral : Commands.Integral;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (evaluate == null) {
			input = new GeoElement[3];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
		} else {
			input = new GeoElement[4];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = evaluate;
		}

		setOutputLength(1);
		setOutput(0, n);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting integral
	 */
	public GeoNumeric getIntegral() {
		return n;
	}

	/**
	 * @return value of integral
	 */
	double getIntegralValue() {
		return n.getValue();
	}

	/**
	 * @return input function
	 */
	public GeoFunction getFunction() {
		return f;
	}

	/**
	 * @return left border
	 */
	public NumberValue getA() {
		return a;
	}

	/**
	 * @return right border
	 */
	public NumberValue getB() {
		return b;
	}

	@Override
	public final void compute() {
		if (!f.isDefined() || !ageo.isDefined() || !bgeo.isDefined()) {
			n.setUndefined();
			return;
		}
		this.validButUndefined = false;
		// check for equal bounds
		final double lowerLimit = a.getDouble();
		final double upperLimit = b.getDouble();
		if (DoubleUtil.isEqual(lowerLimit, upperLimit)) {
			n.setValue(0);
			return;
		}
		// check if f(a) and f(b) are defined
		double fa = f.value(lowerLimit);
		double fb = f.value(upperLimit);
		if (Double.isInfinite(lowerLimit) || Double.isInfinite(upperLimit) || Double.isNaN(fa)
				|| Double.isInfinite(fa) || Double.isNaN(fb) || Double.isInfinite(fb)) {
			if (!this.evaluateNumerically && !evaluateOnly()
					&& !f.includesFreehandOrData()) {
				computeSpecial();
			} else {
				n.setUndefined();
			}
			return;
		}

		// return if it should not be evaluated (i.e. is shade-only)
		if (evaluateOnly()) {
			n.setValue(Double.NaN);
			return;
		}

		/*
		 * Try to use symbolic integral
		 * 
		 * We only do this for functions that do NOT include divisions by their
		 * variable. Otherwise there might be problems like: Integral[ 1/x, -2,
		 * -1 ] would be undefined (log(-1) - log(-2)) Integral[ 1/x^2, -1, 1 ]
		 * would be defined (-2)
		 */
		if (!f.includesFreehandOrData()) {
			if (algoCAS instanceof AlgoIntegral
					&& !((AlgoIntegral) algoCAS).isComputedSymbolically()) {
				algoCAS.compute();
			}
			if (symbIntegral != null && symbIntegral.isDefined()
					&& !f.includesDivisionByVar()
					&& !f.includesNonContinuousIntegral()
					// eg Integral( x sqrt( 1 + cos(2x) ) , 0 , pi / 2 )
					&& !symbIntegral.includesNonContinuousIntegral()) {
				double val = symbIntegral.value(upperLimit)
						- symbIntegral.value(lowerLimit);
				n.setValue(val);
				if (n.isDefined()) {
					return;
				}
			} else if (symbIntegral != null && symbIntegral.isDefined()
					&& !this.evaluateNumerically) {
				computeSpecial();
				if (!n.isDefined()) { // giac failed
					standardIntegral(lowerLimit, upperLimit);
				}
				return;
			}
		}

		// numerical integration
		// max_error = ACCURACY; // current maximum error
		// maxstep = 0;

		if (f.isFreehandFunction()) {
			n.setValue(freehandIntegration(f, lowerLimit, upperLimit));
		} else if (f.isDataFunction()) {
			n.setValue(dataIntegration(f, lowerLimit, upperLimit));
		} else {
			// more accurate numeric-integration for polynomials
			Function inFun = f.getFunction();

			// check if it's a polynomial
			PolyFunction polyIntegral = inFun.getNumericPolynomialIntegral();

			// it it is...
			if (polyIntegral != null) {
				// ... we can calculate the integral more accurately
				n.setValue(polyIntegral.value(upperLimit)
						- polyIntegral.value(lowerLimit));

			} else {
				AlgoElement algo = f.getParentAlgorithm();
				if (algo instanceof AlgoDependentFunction
						|| f.getFunctionExpression().isConditional()) {

					ExpressionNode exp = algo instanceof AlgoDependentFunction
							? ((AlgoDependentFunction) algo).getExpression()
							: f.getFunctionExpression();

					if (exp.isConditional()) {
						computeConditional(exp, lowerLimit, upperLimit);
						return;
					}
				}

				standardIntegral(lowerLimit, upperLimit);
			}
		}
	}

	private void computeConditional(ExpressionNode exp, double lowerLimit, double upperLimit) {
		double upperLimit0 = upperLimit;
		double lowerLimit0 = lowerLimit;
		double sign = +1;

		if (upperLimit < lowerLimit) {
			upperLimit0 = lowerLimit;
			lowerLimit0 = upperLimit;
			sign = -1;

		}
		ArrayList<ExpressionNode> nodesAl = new ArrayList<>();
		ArrayList<Bounds> boundsAl = new ArrayList<>();

		boolean complete = Bounds.collectCases(exp,
				nodesAl, boundsAl, new Bounds(kernel,
						f.getFunctionVariables()[0]),
				true);

		int size = complete ? (nodesAl.size() - 1)
				: nodesAl.size();

		double sum = 0;

		Function fun = null;

		double coveredMin = Double.NaN;
		double coveredMax = Double.NaN;
		ExpressionNode node;

		for (int i = 0; i < size; i++) {
			node = nodesAl.get(i);

			Bounds bound = boundsAl.get(i);
			if (bound != null) {
				double lower = bound.getLower();
				double upper = bound.getUpper();
				if (!bound.isInterval()) {
					// eg f(x) = If(-2 <= x < 2, x, 2 <= x <= 3,
					// 4 - x, 3 < x <= 6, 1)
					Log.debug(
							"non-linear condition "
									+ bound);
					standardIntegral(lowerLimit, upperLimit);
					return;
				}
				if (i == 0) {
					coveredMin = lower;
					coveredMax = upper;
				} else if (lower == coveredMax) {
					coveredMax = upper;
				} else if (upper == coveredMin) {
					coveredMin = lower;
				} else {
					// regions are defined in a strange order
					Log.debug(
							"regions not sorted, can't use fast method for "
									+ bound);
					standardIntegral(lowerLimit, upperLimit);
					return;
				}

				if (fun == null) {
					fun = new Function(node,
							f.getFunctionVariables()[0]);
				} else {
					fun.setExpression(node);
				}

				if (Double.isInfinite(lower)) {

					if (upper > lowerLimit0) {
						sum += sign * numericIntegration(fun,
								lowerLimit0,
								Math.min(upper, upperLimit0),
								STANDARD_MULTIPLIER);
					}

				} else if (Double.isInfinite(upper)) {

					if (lower < upperLimit0) {
						sum += sign * numericIntegration(fun,
								Math.max(lower, lowerLimit0),
								upperLimit0,
								STANDARD_MULTIPLIER);
					}

				} else if (upper <= lowerLimit0
						|| lower >= upperLimit0) {

					// nothing to do
				} else if (lower >= lowerLimit0
						&& upper <= upperLimit0) {

					// include all
					sum += sign * numericIntegration(fun, lower,
							upper,
							STANDARD_MULTIPLIER);
				} else if ((Double.isNaN(lower)
						|| lower <= lowerLimit0)
						&& upper <= upperLimit0) {

					sum += sign * numericIntegration(fun,
							lowerLimit0,
							upper, STANDARD_MULTIPLIER);
				} else if ((Double.isNaN(upper)
						|| upper >= upperLimit0)
						&& lower >= lowerLimit0) {

					sum += sign * numericIntegration(fun, lower,
							upperLimit0, STANDARD_MULTIPLIER);

				} else if (lower <= lowerLimit0
						&& upper >= upperLimit0) {

					sum += sign * numericIntegration(fun,
							lowerLimit0, upperLimit0,
							STANDARD_MULTIPLIER);

				} else {
					Log.error("lower = " + lower);
					Log.error("lowerLimit0 = " + lowerLimit0);
					Log.error("upper = " + upper);
					Log.error("upperLimit0 = " + upperLimit0);
				}

			}

		}

		if (complete) {
			// Log.error("TODO " + coveredMin + " " +
			// coveredMax);

			node = nodesAl.get(size);

			if (fun == null) {
				fun = new Function(node,
						f.getFunctionVariables()[0]);
			} else {
				fun.setExpression(node);
			}

			if (upperLimit0 <= coveredMin
					|| lowerLimit0 >= coveredMax) {
				// all outside what's been covered already
				sum += sign * numericIntegration(fun,
						lowerLimit0, upperLimit0,
						STANDARD_MULTIPLIER);
			} else if (lowerLimit0 >= coveredMin
					&& upperLimit0 <= coveredMax) {
				// nothing to do
			} else if (lowerLimit0 <= coveredMin
					&& upperLimit0 <= coveredMax) {
				sum += sign
						* numericIntegration(fun, lowerLimit0,
						coveredMin, STANDARD_MULTIPLIER);
			} else if (lowerLimit0 >= coveredMin
					&& upperLimit0 >= coveredMax) {
				sum += sign * numericIntegration(fun,
						coveredMax, upperLimit0,
						STANDARD_MULTIPLIER);
			} else {
				Log.error("GGB-2318 problem computing integral");
			}
		}

		n.setValue(sum);
	}

	private void standardIntegral(double lowerLimit, double upperLimit) {
		// freehand functions aren't generally nice and smooth, so more
		// iterations may be needed
		// https://help.geogebra.org/topic/problem-mit-integral-unter-freihandskizze
		n.setValue(numericIntegration(f, lowerLimit, upperLimit,
				f.includesFreehandOrData() ? FREEHAND_MULTIPLIER
						: STANDARD_MULTIPLIER));
	}

	// private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	private void computeSpecial() {

		StringBuilder sb = new StringBuilder(30);

		// #4687
		// as we want a numerical answer not exact, more robust to pass
		// 6.28318530717959
		// rather than
		// 628318530717959/100000000000000
		// so call evaluateRaw()
		sb.append("evalf(integrate(");
		sb.append(f.toValueString(StringTemplate.giacTemplate));
		sb.append(",");
		sb.append(f.getVarString(StringTemplate.defaultTemplate));
		sb.append(",");
		// #5130
		sb.append(a.toValueString(StringTemplate.giacNumeric13));
		sb.append(",");
		// #5130
		sb.append(b.toValueString(StringTemplate.giacNumeric13));
		sb.append("))");

		String result;
		try {
			result = kernel.evaluateRawGeoGebraCAS(sb.toString());
			// Log.debug("result from AlgoIntegralDefinite = " + result);

			// Giac can return 2 answers if it's not sure
			// test-case
			// result = "{3.12,4.0}";

			if (result.startsWith("{")) {
				result = result.split(",")[0];
				result = result.substring(1);
			}
			if (CASgiac.isUndefined(result)) {
				this.validButUndefined = true;
				n.setUndefined();
				return;
			}
			kernel.getAlgebraProcessor().evaluateToDouble(result, true, n);

		} catch (Throwable e) {
			e.printStackTrace();
			n.setUndefined();
		}

		/*
		 * sb.append("Numeric[Integral[");
		 * sb.append(f.toValueString(StringTemplate.maxPrecision));
		 * sb.append(",");
		 * sb.append(f.getVarString(StringTemplate.defaultTemplate));
		 * sb.append(",");
		 * sb.append(a.toValueString(StringTemplate.maxPrecision));
		 * sb.append(",");
		 * sb.append(b.toValueString(StringTemplate.maxPrecision));
		 * sb.append("]]"); try{ String functionOut = kernel
		 * .evaluateCachedGeoGebraCAS(sb.toString(),arbconst); if (functionOut
		 * == null || functionOut.length() == 0) { n.setUndefined(); } else { //
		 * read result back into function, do NOT show errors if eg complex
		 * number occurs
		 * n.setValue(kernel.getAlgebraProcessor().evaluateToDouble(functionOut,
		 * true)); } }catch(Throwable e){ n.setUndefined(); }
		 */

	}

	private double freehandIntegration(GeoFunction f2, double lowerLimitUser,
			double upperLimitUser) {

		int multiplier = 1;
		double lowerLimit = lowerLimitUser;
		double upperLimit = upperLimitUser;
		if (lowerLimit > upperLimit) {
			// swap a and b
			double temp = lowerLimit;
			lowerLimit = upperLimit;
			upperLimit = temp;
			multiplier = -1;
		}

		// AbstractApplication.debug("1");

		AlgoFunctionFreehand algo = (AlgoFunctionFreehand) f2
				.getParentAlgorithm();

		GeoList list = algo.getList();

		double a1 = list.get(0).evaluateDouble();
		double b1 = list.get(1).evaluateDouble();

		if (lowerLimit < a1 || upperLimit > b1) {
			return Double.NaN;
		}

		double nn = list.size() - 2;

		double step = (b1 - a1) / (nn - 1);

		int startGap = (int) Math.ceil((lowerLimit - a1) / step);
		int endGap = (int) Math.ceil((b1 - upperLimit) / step);

		double startx = a1 + step * startGap;
		double endx = b1 - step * endGap;

		// int noOfSteps = (int) ((b - step * end - (a + step * start) )/step);
		// int noOfSteps = (int) ((b - step * end - a - step * start) )/step)
		// should be an integer, add Math.round in case of rounding error
		int noOfSteps = (int) Math.round((b1 - a1) / step - endGap - startGap)
				+ 1;

		double area = 0;
		double sum = 0;
		// AbstractApplication.debug("noOfSteps = "+noOfSteps);
		// AbstractApplication.debug("step = "+step);
		// AbstractApplication.debug("startx = "+startx);
		// AbstractApplication.debug("endx = "+endx);
		// AbstractApplication.debug("start = "+startGap);
		// AbstractApplication.debug("end = "+endGap);
		// trapezoidal rule
		if (noOfSteps > 0) {

			for (int i = 0; i < noOfSteps; i++) {
				// y-coordinate
				double y = list.get(2 + i + startGap).evaluateDouble();
				if (i == 0 || (i == noOfSteps - 1)) {
					sum += y;
				} else {
					sum += 2 * y;
				}
			}
			// now add the extra bits at the start and end

			area = sum * step / 2.0;

			if (!DoubleUtil.isZero(startx - lowerLimit)) {
				// h (a+b) /2
				area += (startx - lowerLimit)
						* (f.value(startx) + f.value(lowerLimit)) / 2.0;
			}

			if (!DoubleUtil.isZero(endx - upperLimit)) {
				// h (a+b) /2
				area += (upperLimit - endx)
						* (f.value(endx) + f.value(upperLimit)) / 2.0;
			}
		} else {
			// just a trapezium from lowerLimit to upperLimit

			area = (upperLimit - lowerLimit)
					* (f.value(lowerLimit) + f.value(upperLimit)) / 2.0;
		}

		return DoubleUtil.checkDecimalFraction(area) * multiplier;

	}

	/**
	 * split the DataFunction into trapeziums to calculate the area
	 * 
	 * @param f2
	 *            function
	 * @param lowerLimitUser
	 *            lower limit from user input
	 * @param upperLimitUser
	 *            upper limit from user input
	 * @return integral
	 */
	private static double dataIntegration(GeoFunction f2, double lowerLimitUser,
			double upperLimitUser) {

		int multiplier = 1;
		double lowerLimit = lowerLimitUser;
		double upperLimit = upperLimitUser;
		if (lowerLimit > upperLimit) {
			// swap a and b
			double temp = lowerLimit;
			lowerLimit = upperLimit;
			upperLimit = temp;
			multiplier = -1;
		}

		ExpressionNode exp = f2.getFunction().getExpression();
		ExpressionValue rt = exp.getRight();

		ListValue keyList = (ListValue) ((MyNumberPair) rt).getX();
		ListValue valueList = (ListValue) ((MyNumberPair) rt).getY();

		int n = keyList.size();

		if (n < 1) {
			return Double.NaN;
		}
		double max = keyList.getListElement(keyList.size() - 1)
				.evaluateDouble();
		double min = keyList.getListElement(0).evaluateDouble();
		if (max < upperLimit || min > lowerLimit) {
			return Double.NaN;
		}

		int start = 1;
		int end = n;

		// find (approx) start and end
		// binary search would be more efficient
		for (int i = 0; i < n; i++) {
			double x = keyList.getListElement(i).evaluateDouble();

			if (x < lowerLimit) {
				start = i + 1;
			}

			if (x > upperLimit) {
				end = i;
				break;
			}
		}

		double area = 0, x1, y1, x2, y2;

		// special case: both endpoints are inside the same interval
		if (start == end) {
			x1 = lowerLimit;
			x2 = upperLimit;
			y1 = f2.value(x1);
			y2 = f2.value(x2);

			// area of trapezium
			return multiplier * trapeziumArea(x1, x2, y1, y2);

		}

		for (int i = start; i < end - 1; i++) {
			x1 = keyList.getListElement(i).evaluateDouble();
			x2 = keyList.getListElement(i + 1).evaluateDouble();
			y1 = valueList.getListElement(i).evaluateDouble();
			y2 = valueList.getListElement(i + 1).evaluateDouble();
			// App.error("i = " + i + "x1 = " + x1 + " x2 = " + x2 + " y1 = " +
			// y1
			// + " y2 = " + y2 + " area = " + trapeziumArea(x1, x2, y1, y2));
			// area of trapezium
			area += trapeziumArea(x1, x2, y1, y2);

		}

		x1 = keyList.getListElement(start - 1).evaluateDouble();
		x2 = keyList.getListElement(start).evaluateDouble();
		y1 = valueList.getListElement(start - 1).evaluateDouble();
		y2 = valueList.getListElement(start).evaluateDouble();

		// if (lowerLimit < x1 || lowerLimit > x2) {
		// App.error(
		// "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		// }
		//
		// App.error("start = " + start + " lowerLimit = " + lowerLimit + "x1 =
		// "
		// + x1 + " x2 = " + x2 + " y1 = " + y1 + " y2 = " + y2);

		// interpolate
		y1 = ((lowerLimit - x1) * y2 + y1 * (x2 - lowerLimit)) / (x2 - x1);
		x1 = lowerLimit;
		// area of trapezium
		area += trapeziumArea(x1, x2, y1, y2);

		// App.error("x1 = " + x1 + " x2 = " + x2 + " y1 = " + y1 + " y2 = " +
		// y2
		// + " area = " + trapeziumArea(x1, x2, y1, y2));

		x1 = keyList.getListElement(end - 1).evaluateDouble();
		y1 = valueList.getListElement(end - 1).evaluateDouble();

		// if (upperLimit < x1 || upperLimit > x2) {
		// App.error(
		// "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		// }
		// App.error("end = " + end + " upperLimit = " + upperLimit + "x1 = " +
		// x1
		// + " x2 = " + x2 + " y1 = " + y1 + " y2 = " + y2 + " area = "
		// + trapeziumArea(x1, x2, y1, y2));

		x2 = upperLimit;
		y2 = f2.value(x2);
		// area of trapezium
		area += trapeziumArea(x1, x2, y1, y2);

		// App.error("x1 = " + x1 + " x2 = " + x2 + " y1 = " + y1 + " y2 = " +
		// y2);

		return area * multiplier;

	}

	private static double trapeziumArea(double x1, double x2, double y1,
			double y2) {
		// not needed, gives the same answer!
		// Substitute[(1 / 2 (y1 (p - x1) + y2 (-p + x2))),{p = (-x1 y2 + x2 y1)
		// / (y1 - y2)}]-((x2 - x1) * (y1 + y2) / 2)
		// if (Math.signum(y1) != Math.signum(y2)) {
		// // interpolate
		// // Solve[(x1-p)/y1=(x2-p)/y2,p]
		// double p = (((-x1) * y2) + (x2 * y1)) / (y1 - y2);
		// // 2 triangles
		// App.error("old = " + (x2 - x1) * (y1 + y2) / 2);
		// App.error("new = " + ((p - x1) * y1 + (x2 - p) * y2) / 2);
		// return ((p - x1) * y1 + (x2 - p) * y2) / 2;
		// }
		return (x2 - x1) * (y1 + y2) / 2;
	}

	/**
	 * Computes integral of function fun in interval a, b using an adaptive
	 * Gauss quadrature approach.
	 * 
	 * @param fun
	 *            function
	 * @param a
	 *            lower bound
	 * @param b
	 *            upper bound
	 * @return integral value
	 */
	public static double numericIntegration(UnivariateFunction fun, double a,
			double b) {

		return numericIntegration(fun, a, b, STANDARD_MULTIPLIER);

	}

	/**
	 * Computes integral of function fun in interval a, b using an adaptive
	 * Gauss quadrature approach.
	 * 
	 * @param ad
	 *            function
	 * @param a
	 *            lower bound
	 * @param b
	 *            upper bound
	 * @param maxMultiplier
	 *            multiplier (to allow more iterations for freehand functions)
	 * @return integral value
	 */
	public static double numericIntegration(UnivariateFunction ad, double a,
			double b, int maxMultiplier) {
		
		// GGB-2318
		// f(x) = If(x < 0, 0, x <= 2, x)
		if (a == b) {
			return 0;
		}

		adaptiveGaussQuadCounter = 0;
		if (a > b) {
			return -doAdaptiveGaussQuad(ad, b, a, maxMultiplier);
		}
		return doAdaptiveGaussQuad(ad, a, b, maxMultiplier);

		// System.out.println("calls: " + adaptiveGaussQuadCounter);

	}

	private static double doAdaptiveGaussQuad(UnivariateFunction fun, double a,
			double b, int maxMultiplier) {
		if (++adaptiveGaussQuadCounter > MAX_GAUSS_QUAD_CALLS * maxMultiplier) {
			return Double.NaN;
		}

		// init GaussQuad classes for numerical integration
		if (firstGauss == null) {
			firstGauss = new LegendreGaussIntegrator(FIRST_ORDER, MIN_ITER,
					MAX_ITER);
			secondGauss = new LegendreGaussIntegrator(SECOND_ORDER, MIN_ITER,
					MAX_ITER);
		}

		double firstSum = 0;
		double secondSum = 0;

		boolean error = false;

		// integrate using gauss quadrature
		try {
			firstSum = firstGauss.integrate(MAX_GAUSS_QUAD_CALLS, fun, a, b);
			if (Double.isNaN(firstSum)) {
				return Double.NaN;
			}
			secondSum = secondGauss.integrate(MAX_GAUSS_QUAD_CALLS, fun, a, b);
			if (Double.isNaN(secondSum)) {
				return Double.NaN;
			}
		} catch (IllegalArgumentException e) {
			return Double.NaN;
		} catch (RuntimeException e) {
			// catches IllegalStateException and ArithmeticException
			// eg
			// org.apache.commons.math3.exception.TooManyEvaluationsException:
			// illegal state: maximal count ({0}) exceeded500: evaluations
			error = true;
		}

		// if (!error) Application.debug(a+" "+b+" "+(firstSum - secondSum),
		// Kernel.isEqual(firstSum, secondSum, Kernel.STANDARD_PRECISION) ? 1 :
		// 0);
		// else Application.debug(a+" "+b+" error",1);

		// check if both results are equal
		boolean equal = !error && DoubleUtil.isEqual(firstSum, secondSum,
				Kernel.STANDARD_PRECISION);

		if (equal) {
			// success
			return secondSum;
		}
		double mid = (a + b) / 2;
		double left = doAdaptiveGaussQuad(fun, a, mid, maxMultiplier);
		if (Double.isNaN(left)) {
			return Double.NaN;
		}
		return left + doAdaptiveGaussQuad(fun, mid, b, maxMultiplier);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("IntegralOfAfromBtoC",
				"Integral of %0 from %1 to %2", f.getLabel(tpl),
				ageo.getLabel(tpl), bgeo.getLabel(tpl));
	}

	@Override
	public DrawInformationAlgo copy() {
		if (evaluate != null) {
			return new AlgoIntegralDefinite(f.copy(),
					(NumberValue) a.deepCopy(kernel),
					(NumberValue) b.deepCopy(kernel), evaluate.copy());
		}
		return new AlgoIntegralDefinite(f.copy(),
				(NumberValue) a.deepCopy(kernel),
				(NumberValue) b.deepCopy(kernel), null);
	}

	/*
	 * make sure shaded-only integrals are drawn
	 */
	@Override
	public boolean evaluateOnly() {
		return evaluateOnlySet() || validButUndefined;
	}

	private boolean evaluateOnlySet() {
		return evaluate != null && !evaluate.getBoolean();
	}

	@Override
	public void refreshCASResults() {
		if (!evaluateNumerically) {
			AlgoIntegral algoInt = new AlgoIntegral(cons, f, null, false,
					new EvalInfo(false), false);
			symbIntegral = (GeoFunction) algoInt.getResult();
			cons.removeFromConstructionList(algoInt);
			// make sure algo is removed properly
			algoCAS = algoInt;
		}
	}

	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		f.replaceChildrenByValues(geo);

	}

}
