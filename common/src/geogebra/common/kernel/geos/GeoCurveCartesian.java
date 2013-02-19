/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MatrixTransformable;
import geogebra.common.kernel.ParametricCurveDistanceFunction;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.PathMoverGeneric;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoMacroInterface;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.kernel.roots.RealRootFunction;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.MyMath;

import java.util.ArrayList;

/**
 * Cartesian parametric curve, e.g. (cos(t), sin(t)) for t from 0 to 2pi.
 * 
 * @author Markus Hohenwarter
 */
public class GeoCurveCartesian extends GeoCurveCartesianND implements
		Transformable, VarString, Translateable, PointRotateable, Mirrorable,
		Dilateable, MatrixTransformable, CasEvaluableFunction, ParametricCurve,
		LineProperties, ConicMirrorable {

	// samples to find interval with closest parameter position to given point
	private static final int CLOSEST_PARAMETER_SAMPLES = 100;

	private Function funX, funY;
	private boolean isClosedPath;
	private boolean trace = false;// , spreadsheetTrace = false; -- not used,
									// commented out (Zbynek Konecny,
									// 2010-06-16)
	// Victor Franco Espino 25-04-2007
	/**
	 * Parameter in dialog box for adjust color of curvature
	 */
	double CURVATURE_COLOR = 15;// optimal value
	// Victor Franco Espino 25-04-2007

	private ParametricCurveDistanceFunction distFun;

	private boolean hideRangeInFormula;

	/**
	 * @return whether range is hidden in algebra
	 */
	public boolean isHiddenRange(){
		return hideRangeInFormula;
	}
	/**
	 * Creates new curve
	 * 
	 * @param c
	 *            construction
	 * 
	 */
	public GeoCurveCartesian(Construction c) {
		super(c);
	}

	/**
	 * Creates new curve
	 * 
	 * @param c
	 *            construction
	 * @param fx
	 *            x-coord function
	 * @param fy
	 *            y-coord function
	 */
	public GeoCurveCartesian(Construction c, Function fx, Function fy) {
		super(c);
		setFunctionX(fx);
		setFunctionY(fy);
	}

	@Override
	public String translatedTypeString() {
		return app.getPlain("Curve");
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CURVE_CARTESIAN;
	}

	/**
	 * copy constructor
	 * 
	 * @param f
	 *            Curve to copy
	 */
	public GeoCurveCartesian(GeoCurveCartesian f) {
		super(f.cons);
		set(f);
	}

	@Override
	public GeoElement copy() {
		return new GeoCurveCartesian(this);
	}

	/**
	 * Sets the function of the x coordinate of this curve.
	 * 
	 * @param funX
	 *            new x-coord function
	 */
	final public void setFunctionX(Function funX) {
		this.funX = funX;
	}

	/**
	 * Sets the function of the y coordinate of this curve.
	 * 
	 * @param funY
	 *            new y-coord function
	 */
	final public void setFunctionY(Function funY) {
		this.funY = funY;
	}

	/**
	 * Replaces geo and all its dependent geos in this function's expression by
	 * copies of their values.
	 * 
	 * @param geo
	 *            Element to be replaced
	 */
	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		if (funX != null) {
			funX.replaceChildrenByValues(geo);
		}
		if (funY != null) {
			funY.replaceChildrenByValues(geo);
		}
	}

	/**
	 * Sets the start and end parameter value of this curve. Note:
	 * setFunctionX() and setFunctionY() has to be called before this method.
	 */
	@Override
	public void setInterval(double startParam, double endParam) {

		/*
		 * this.startParam = startParam; this.endParam = endParam;
		 * 
		 * isDefined = startParam <= endParam;
		 */

		super.setInterval(startParam, endParam);

		// update isClosedPath, i.e. startPoint == endPoint
		isClosedPath = Kernel.isEqual(funX.evaluate(startParam),
				funX.evaluate(endParam), Kernel.MIN_PRECISION)
				&& Kernel.isEqual(funY.evaluate(startParam),
						funY.evaluate(endParam), Kernel.MIN_PRECISION);
	}

	@Override
	public void set(GeoElement geo) {
		GeoCurveCartesian geoCurve = (GeoCurveCartesian) geo;

		funX = new Function(geoCurve.funX, kernel);
		funY = new Function(geoCurve.funY, kernel);
		startParam = geoCurve.startParam;
		endParam = geoCurve.endParam;
		isDefined = geoCurve.isDefined;

		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput()) {
			if (!geo.isIndependent()) {
				// System.out.println("set " + this.label);
				// System.out.println("   funX before: " +
				// funX.toLaTeXString(true));

				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's
				// expression
				AlgoMacroInterface algoMacro = (AlgoMacroInterface) getParentAlgorithm();
				algoMacro.initFunction(funX);
				algoMacro.initFunction(funY);
				// System.out.println("   funX after: " +
				// funX.toLaTeXString(true));
			}
		}
		distFun = new ParametricCurveDistanceFunction(this);
	}

	/**
	 * Set this curve by applying CAS command to f.
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic,MyArbitraryConstant arbconst) {
		GeoCurveCartesian c = (GeoCurveCartesian) f;

		if (c.isDefined()) {
			//register the variable name to make sure parsing of CAS output runs OK, see #3006
			GeoNumeric geo = new GeoNumeric(cons);
			cons.addLocalVariable(funX.getVarString(StringTemplate.defaultTemplate), geo);
			funX = (Function) c.funX.evalCasCommand(ggbCasCmd, symbolic,arbconst);
			funY = (Function) c.funY.evalCasCommand(ggbCasCmd, symbolic,arbconst);
			cons.removeLocalVariable(funX.getVarString(StringTemplate.defaultTemplate));
			isDefined = !(funX == null || funY == null);
			if (isDefined)
				setInterval(c.startParam, c.endParam);
		} else {
			isDefined = false;
		}
		distFun = new ParametricCurveDistanceFunction(this);
	}
	/**
	 * @param order order of derivative
	 * @return derivative as curve
	 */
	public GeoCurveCartesian getGeoDerivative(int order) {
		if (derivGeoFun == null) {
			derivGeoFun = new GeoCurveCartesian(cons);
		}

		derivGeoFun.setDerivative(this, order);
		return derivGeoFun;
	}

	private GeoCurveCartesian derivGeoFun;

	/**
	 * Set this curve to the n-th derivative of c
	 * @param curve curve whose derivative we want
	 * 
	 * @param n
	 *            order of derivative
	 */
	public void setDerivative(GeoCurveCartesian curve, int n) {
		if (curve.isDefined()) {
			funX = curve.funX.getDerivative(n);
			funY = curve.funY.getDerivative(n);
			isDefined = !(funX == null || funY == null);
			if (isDefined)
				setInterval(curve.startParam, curve.endParam);
		} else {
			isDefined = false;
		}
		distFun = new ParametricCurveDistanceFunction(this);
	}

	/**
	 * Sets this curve to the parametric derivative of the given curve c. The
	 * parametric derivative of a curve c(t) = (x(t), y(t)) is defined as (x(t),
	 * y'(t)/x'(t)).
	 * @param curve curve whose derivative we want
	 */
	public void setParametricDerivative(GeoCurveCartesian curve) {
		if (curve.isDefined()) {
			funX = curve.funX;
			funY = Function.getDerivativeQuotient(curve.funX, curve.funY);
			isDefined = !(funX == null || funY == null);
			if (isDefined)
				setInterval(curve.startParam, curve.endParam);
		} else {
			isDefined = false;
		}
		distFun = new ParametricCurveDistanceFunction(this);
	}

	// added by Loic Le Coq 2009/08/12
	/**
	 * @param tpl string template
	 * @return value string x-coord function
	 */
	final public String getFunX(StringTemplate tpl) {
		return funX.toValueString(tpl);
	}

	/**
	 * @param tpl string template
	 * @return value string y-coord function
	 */
	final public String getFunY(StringTemplate tpl) {
		return funY.toValueString(tpl);
	}

	// end Loic Le Coq

	final public RealRootFunction getRealRootFunctionX() {
		return funX;
	}

	final public RealRootFunction getRealRootFunctionY() {
		return funY;
	}

	/**
	 * translate function by vector v
	 */
	final public void translate(Coords v) {
		funX.translateY(v.getX());
		funY.translateY(v.getY());
	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	@Override
	final public boolean isMatrixTransformable() {
		return true;
	}

	/**
	 * Translates the curve by vector given by coordinates
	 * 
	 * @param vx
	 *            x-coord of the translation vector
	 * @param vy
	 *            y-coord of the translation vector
	 */
	final public void translate(double vx, double vy) {
		funX.translateY(vx);
		funY.translateY(vy);
	}

	final public void rotate(NumberValue phi, GeoPoint P) {
		translate(-P.getX(), -P.getY());
		rotate(phi);
		translate(P.getX(), P.getY());
	}

	final public void mirror(GeoPoint P) {
		dilate(new MyDouble(kernel, -1.0), P);
	}

	final public void mirror(GeoLine g) {
		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirrorTransform(phi)
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.getX()) > Math.abs(g.getY())) {
			qx = g.getZ() / g.getX();
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = g.getZ() / g.getY();
		}

		// translate -Q
		translate(qx, qy);

		// S(phi)
		mirror(new MyDouble(kernel, 2.0 * Math.atan2(-g.getX(), g.getY())));

		// translate back +Q
		translate(-qx, -qy);

		// update inhom coords
	}

	final public void rotate(NumberValue phi) {
		double cosPhi = Math.cos(phi.getDouble());
		double sinPhi = Math.sin(phi.getDouble());
		matrixTransform(cosPhi, -sinPhi, sinPhi, cosPhi);
	}

	public void dilate(NumberValue ratio, GeoPoint P) {
		translate(-P.getX(), -P.getY());
		ExpressionNode exprX = ((Function) funX.deepCopy(kernel))
				.getExpression();
		ExpressionNode exprY = ((Function) funY.deepCopy(kernel))
				.getExpression();
		funX.setExpression(new ExpressionNode(kernel, ratio,
				Operation.MULTIPLY, exprX));
		funY.setExpression(new ExpressionNode(kernel, ratio,
				Operation.MULTIPLY, exprY));
		translate(P.getX(), P.getY());
	}

	/**
	 * mirror transform with angle phi [ cos(phi) sin(phi) ] [ sin(phi)
	 * -cos(phi) ]
	 */
	private void mirror(NumberValue phi) {
		double cosPhi = Math.cos(phi.getDouble());
		double sinPhi = Math.sin(phi.getDouble());
		matrixTransform(cosPhi, sinPhi, sinPhi, -cosPhi);
	}

	/**
	 * return n different points on curve, needs for inversion
	 * 
	 * @param n
	 *            number of requested points
	 * @param startInterval least value of param
	 * @param endInterval highest value of param
	 * @return array list of points
	 */
	public ArrayList<GeoPoint> getPointsOnCurve(int n, double startInterval,
			double endInterval) {
		ArrayList<GeoPoint> pointList = new ArrayList<GeoPoint>();

		double step = (endInterval - startInterval) / (n + 1);

		for (double i = 0, v = startInterval; i < n; i++, v += step) {
			double[] point = new double[2];
			point[0] = funX.evaluate(v);
			point[1] = funY.evaluate(v);
			pointList.add(new GeoPoint(cons, point[0], point[1], 1));
		}

		return pointList;
	}

	/**
	 * Transforms curve using matrix [a b] [c d]
	 * 
	 * @param a
	 *            top left matrix element
	 * @param b
	 *            top right matrix element
	 * @param c
	 *            bottom left matrix element
	 * @param d
	 *            bottom right matrix element
	 */
	public void matrixTransform(double a, double b, double c, double d) {
		MyDouble ma = new MyDouble(kernel, a);
		MyDouble mb = new MyDouble(kernel, b);
		MyDouble mc = new MyDouble(kernel, c);
		MyDouble md = new MyDouble(kernel, d);
		ExpressionNode exprX = ((Function) funX.deepCopy(kernel))
				.getExpression();
		ExpressionNode exprY = ((Function) funY.deepCopy(kernel))
				.getExpression();
		ExpressionNode transX = exprX.multiply(ma).plus(exprY.multiply(mb));
		ExpressionNode transY = exprX.multiply(mc).plus(exprY.multiply(md));
		funX.setExpression(transX);
		funY.setExpression(transY);
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	// TODO remove and use super method (funX and funY should be removed in
	// fun[])
	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder(80);
		
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			// sbToString.append('(');
			// sbToString.append(funX.getVarString());
			// sbToString.append(") = ");
			// changed to ':' to make LaTeX output better
			sbToString.append(':');
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	// private StringBuilder sbToString;
	// private StringBuilder sbTemp;

	// TODO remove and use super method (funX and funY should be removed in
	// fun[])
	@Override
	public String toValueString(StringTemplate tpl) {

		if (isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);
			sbTemp.setLength(0);
			sbTemp.append('(');
			sbTemp.append(funX.toValueString(tpl));
			sbTemp.append(", ");
			sbTemp.append(funY.toValueString(tpl));
			sbTemp.append(')');
			return sbTemp.toString();
		}
		return app.getPlain("Undefined");
	}

	// TODO remove and use super method (funX and funY should be removed in
	// fun[])

	@Override
	public String toSymbolicString(StringTemplate tpl) {
		if (isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);
			sbTemp.setLength(0);
			sbTemp.append('(');
			sbTemp.append(funX.toString(tpl));
			sbTemp.append(", ");
			sbTemp.append(funY.toString(tpl));
			sbTemp.append(')');
			return sbTemp.toString();
		}
		return app.getPlain("Undefined");
	}

	// TODO remove and use super method (funX and funY should be removed in
	// fun[])
	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (isDefined) {
			StringBuilder sbTemp =
				 new StringBuilder(80);
			
			String param = getVarString(tpl);
			
			if (app.isHTML5Applet()) {
				// mathquill friendly
				sbTemp.append("\\binom{x}{y} = \\binom{");
				sbTemp.append(funX.toLaTeXString(symbolic, tpl));
				sbTemp.append("}{");
				sbTemp.append(funY.toLaTeXString(symbolic, tpl));
				sbTemp.append("} ");
				if (!hideRangeInFormula) {
					sbTemp.append(",");
					sbTemp.append(kernel.format(startParam, tpl));
					sbTemp.append(" \\le ");
					sbTemp.append(param);
					sbTemp.append(" \\le ");
					sbTemp.append(kernel.format(endParam, tpl));
				}
			} else {

				if (!hideRangeInFormula) {
					sbTemp.append("\\left.");
				}
				sbTemp.append("\\begin{array}{ll} x = ");
				sbTemp.append(funX.toLaTeXString(symbolic, tpl));
				sbTemp.append("\\\\ y = ");
				sbTemp.append(funY.toLaTeXString(symbolic, tpl));
				sbTemp.append(" \\end{array}");
				if (!hideRangeInFormula) {
					sbTemp.append("\\right} \\; ");
					sbTemp.append(kernel.format(startParam, tpl));
					sbTemp.append(" \\le ");
					sbTemp.append(param);
					sbTemp.append(" \\le ");
					sbTemp.append(kernel.format(endParam, tpl));
				}
			}
			return sbTemp.toString();
		}
		return app.getPlain("Undefined");
	}

	/*
	 * Path interface
	 */
	public void pointChanged(GeoPointND PI) {

		GeoPoint P = (GeoPoint) PI;

		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		pp.t = t;
		pathChanged(P,false);
	}

	public boolean isOnPath(GeoPointND PI, double eps) {

		GeoPoint P = (GeoPoint) PI;

		if (P.getPath() == this)
			return true;

		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		boolean onPath = Math.abs(funX.evaluate(t) - P.getInhomX()) <= eps
				&& Math.abs(funY.evaluate(t) - P.getInhomY()) <= eps;
		return onPath;
	}

	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		pathChanged(PI,!getKernel().usePathAndRegionParameters(this));

	}
	
	private void pathChanged(GeoPointND PI,boolean changePoint) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (changePoint) {
			pointChanged(PI);
			return;
		}

		GeoPoint P = (GeoPoint) PI;

		PathParameter pp = P.getPathParameter();
		if (pp.t < startParam)
			pp.t = startParam;
		else if (pp.t > endParam)
			pp.t = endParam;

		// calc point for given parameter
		P.setX(funX.evaluate(pp.t));
		P.setY(funY.evaluate(pp.t));
		P.setZ(1.0);
	}

	/**
	 * Returns the parameter value t where this curve has minimal distance to
	 * point P.
	 * 
	 * @param startValue
	 *            an interval around startValue is specially investigated
	 * @param P
	 *            point to which the distance is minimized
	 * @return optimal parameter value t
	 */
	public double getClosestParameter(GeoPoint P, double startValue) {
		double startVal = startValue;
		if (distFun == null)
			distFun = new ParametricCurveDistanceFunction(this);
		distFun.setDistantPoint(P.getX() / P.getZ(), P.getY() / P.getZ());

		// check if P is on this curve and has the right path parameter already
		if (P.getPath() == this || true) {
			// point A is on curve c, take its parameter
			PathParameter pp = P.getPathParameter();
			double pathParam = pp.t;
			if (distFun.evaluate(pathParam) < Kernel.MIN_PRECISION
					* Kernel.MIN_PRECISION)
				return pathParam;

			// if we don't have a startValue yet, let's take the path parameter
			// as a guess
			if (Double.isNaN(startVal))
				startVal = pathParam;
		}

		// first sample distFun to find a start intervall for ExtremumFinder
		double step = (endParam - startParam) / CLOSEST_PARAMETER_SAMPLES;
		double minVal = distFun.evaluate(startParam);
		double minParam = startParam;
		double t = startParam;
		for (int i = 0; i < CLOSEST_PARAMETER_SAMPLES; i++) {
			t = t + step;
			double ft = distFun.evaluate(t);
			if (ft < minVal) {
				// found new minimum
				minVal = ft;
				minParam = t;
			}
		}

		// use interval around our minParam found by sampling
		// to find minimum
		// Math.max/min removed and ParametricCurveDistanceFunction modified instead 
		double left = minParam - step; 
		double right = minParam + step;

		ExtremumFinder extFinder = kernel.getExtremumFinder();
		double sampleResult = extFinder.findMinimum(left, right, distFun,
				Kernel.MIN_PRECISION);
		
		sampleResult = adjustRange(sampleResult);

		// if we have a valid startParam we try the interval around it too
		// however, we don't check the same interval again
		if (!Double.isNaN(startVal)
				&& (startVal < left || right < startVal)) {
			
			// Math.max/min removed and ParametricCurveDistanceFunction modified instead 
			left = startVal - step; 
			right = startVal + step;

			double startValResult = extFinder.findMinimum(left, right, distFun,
					Kernel.MIN_PRECISION);
			
			startValResult = adjustRange(startValResult); 
			
			if (distFun.evaluate(startValResult) < distFun
					.evaluate(sampleResult) + Kernel.MIN_PRECISION/2) {
				return startValResult;
			}
		}

		return sampleResult;
	}

	/** 
	 * allow a curve like Curve[sin(t), cos(t), t, 0, 12*2pi] 
	 * to "join up" properly at 0 and 12*2pi 
	 *  
	 * @param startValResult 
	 * @return startValResult adjusted to be in range [startParam, endParam] if it's just outside 
	 */ 
	private double adjustRange(double startValResult) { 
		if (startValResult < startParam) { 
			return startValResult + (endParam - startParam); 
		} 

		if (startValResult > endParam) { 
			return startValResult - (endParam - startParam); 
		} 

		return startValResult; 
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public boolean isClosedPath() {
		return isClosedPath;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean isVectorValue() {
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
	}

	@Override
	public boolean isTextValue() {
		return false;
	}

	@Override
	final public boolean isTraceable() {
		return true;
	}

	final public boolean getTrace() {
		return trace;
	}

	// G.Sturr 2010-5-18 get/set spreadsheet trace not needed here
	/*
	 * public void setSpreadsheetTrace(boolean spreadsheetTrace) {
	 * this.spreadsheetTrace = spreadsheetTrace; }
	 * 
	 * public boolean getSpreadsheetTrace() { return spreadsheetTrace; }
	 */

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	/**
	 * Calculates the Cartesian coordinates of this curve for the given
	 * parameter paramVal. The result is written to out.
	 */
	public void evaluateCurve(double paramVal, double[] out) {
		out[0] = funX.evaluate(paramVal);
		out[1] = funY.evaluate(paramVal);
	}

	public GeoVec2D evaluateCurve(double t) {
		return new GeoVec2D(kernel, funX.evaluate(t), funY.evaluate(t));
	}

	/**
	 * Calculates curvature for curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T =
	 * sqrt(a'(t)^2+b'(t)^2)
	 * 
	 * @author Victor Franco, Markus Hohenwarter
	 */
	public double evaluateCurvature(double t) {
		Function f1X, f1Y, f2X, f2Y;
		f1X = funX.getDerivative(1);
		f1Y = funY.getDerivative(1);
		f2X = funX.getDerivative(2);
		f2Y = funY.getDerivative(2);

		if (f1X == null || f1Y == null || f2X == null || f2Y == null)
			return Double.NaN;

		double f1eval[] = new double[2];
		double f2eval[] = new double[2];
		f1eval[0] = f1X.evaluate(t);
		f1eval[1] = f1Y.evaluate(t);
		f2eval[0] = f2X.evaluate(t);
		f2eval[1] = f2Y.evaluate(t);
		double t1 = Math.sqrt(f1eval[0] * f1eval[0] + f1eval[1] * f1eval[1]);
		double t3 = t1 * t1 * t1;
		return (f1eval[0] * f2eval[1] - f2eval[0] * f1eval[1]) / t3;
	}

	@Override
	public boolean isCasEvaluableObject() {
		return true;
	}

	public String getVarString(StringTemplate tpl) {
		return funX.getVarString(tpl);
	}

	final public boolean isFunctionInX() {
		return false;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElement geo) {
		// TODO check for equality?
		return false;
		// if (geo.isGeoCurveCartesian()) return xxx; else return false;
	}

	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public boolean isInverseFillable() {
		return isFillable();
	}

	@Override
	public boolean isVector3DValue() {
		return false;
	}

	final public void mirror(GeoConic c) {
		if (c.getType() == GeoConicNDConstants.CONIC_CIRCLE) {

			// Mirror point in circle
			double r = c.getHalfAxes()[0];
			GeoVec2D midpoint = c.getTranslationVector();
			double a = midpoint.getX();
			double b = midpoint.getY();
			this.translate(-a, -b);
			ExpressionNode exprX = ((Function) funX.deepCopy(kernel))
					.getExpression();
			ExpressionNode exprY = ((Function) funY.deepCopy(kernel))
					.getExpression();
			MyDouble d2 = new MyDouble(kernel, 2);
			ExpressionNode sf = new ExpressionNode(kernel, new MyDouble(kernel,
					r * r), Operation.DIVIDE, exprX.power(d2).plus(
					exprY.power(d2)));
			ExpressionNode transX = exprX.multiply(sf);
			ExpressionNode transY = exprY.multiply(sf);
			funX.setExpression(transX);
			funY.setExpression(transY);
			this.translate(a, b);

		} else {
			setUndefined();
		}
	}

	/*
	 * gets shortest distance to point p overridden in eg GeoPoint, GeoLine for
	 * compound paths
	 */
	@Override
	public double distance(GeoPoint p) {
		double t = getClosestParameter(p, 0);
		return MyMath.length(funX.evaluate(t) - p.getX(),
				funY.evaluate(t) - p.getY());
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		MyDouble ma00 = new MyDouble(kernel, a00);
		MyDouble ma01 = new MyDouble(kernel, a01);
		MyDouble ma02 = new MyDouble(kernel, a02);
		MyDouble ma10 = new MyDouble(kernel, a10);
		MyDouble ma11 = new MyDouble(kernel, a11);
		MyDouble ma12 = new MyDouble(kernel, a12);
		MyDouble ma20 = new MyDouble(kernel, a20);
		MyDouble ma21 = new MyDouble(kernel, a21);
		MyDouble ma22 = new MyDouble(kernel, a22);

		ExpressionNode exprX = ((Function) funX.deepCopy(kernel))
				.getExpression();
		ExpressionNode exprY = ((Function) funY.deepCopy(kernel))
				.getExpression();
		ExpressionNode transX = exprX.multiply(ma00).plus(exprY.multiply(ma01))
				.plus(ma02);
		ExpressionNode transY = exprX.multiply(ma10).plus(exprY.multiply(ma11))
				.plus(ma12);
		ExpressionNode transZ = exprX.multiply(ma20).plus(exprY.multiply(ma21))
				.plus(ma22);
		funX.setExpression(new ExpressionNode(kernel, transX, Operation.DIVIDE,
				transZ));
		funY.setExpression(new ExpressionNode(kernel, transY, Operation.DIVIDE,
				transZ));

	}
	/**
	 * 
	 * @param points list of vertices
	 * @param repeatLast true if we should add last-first edge
	 */
	public void setFromPolyLine(GeoPointND[] points, boolean repeatLast) {
		double coef = 0, coefY = 0;
		double cumulative = 0, cumulativeY = 0;
		ExpressionNode enx = new ExpressionNode(kernel, new MyDouble(kernel,
				points[0].getInhomCoordsInD(2).getX()));
		ExpressionNode eny = new ExpressionNode(kernel, new MyDouble(kernel,
				points[0].getInhomCoordsInD(2).getY()));
		FunctionVariable fv = new FunctionVariable(kernel, "t");
		double sum = 0;
		double sumY = 0;
		int limit = repeatLast ? points.length + 1 : points.length;
		int nonzeroSegments = 0;
		for (int i = 1; i < limit; i++) {
			int pointIndex = i >= points.length ? 0 : i;
			ExpressionNode greater = new ExpressionNode(kernel,
					new ExpressionNode(kernel, fv, Operation.MINUS,
							new MyDouble(kernel, nonzeroSegments)), Operation.ABS, null);
			Coords c1 = points[pointIndex].getInhomCoordsInD(2);
			Coords c2 = points[i-1].getInhomCoordsInD(2);
			if(c1.isEqual(c2))
				continue;
			coef = 0.5 * c1.getX() - 0.5
					* c2.getX() - cumulative;
			coefY = 0.5 * c1.getY() - 0.5
					* c2.getY() - cumulativeY;
			sum += coef * nonzeroSegments;
			sumY += coefY * nonzeroSegments;
			nonzeroSegments++;
			cumulative += coef;
			cumulativeY += coefY;
			enx = enx.plus(greater.multiply(new MyDouble(kernel, coef)));
			eny = eny.plus(greater.multiply(new MyDouble(kernel, coefY)));
		}
		enx = enx.plus(new ExpressionNode(kernel, fv, Operation.MULTIPLY,
				new MyDouble(kernel, cumulative)));
		eny = eny.plus(new ExpressionNode(kernel, fv, Operation.MULTIPLY,
				new MyDouble(kernel, cumulativeY)));
		enx = enx.plus(new MyDouble(kernel, -sum));
		eny = eny.plus(new MyDouble(kernel, -sumY));
		Function xFun = new Function(enx, fv);
		Function yFun = new Function(eny, fv);
		this.setFunctionY(yFun);

		this.setFunctionX(xFun);
		this.setInterval(0, nonzeroSegments);
	}
	/**
	 * Hide range in formula -- needed when the curve is infinite and 
	 * range is used for drawing only (e.g. rotated functions)
	 * @param b true to hide
	 */
	public void setHideRangeInFormula(boolean b) {
		hideRangeInFormula = b;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	public FunctionVariable[] getFunctionVariables() {
		return funX.getFunctionVariables();
	}

	/**
	 * @return x-function
	 */
	public Function getFunX() {
		return funX;
	}

	/**
	 * @return y-function
	 */
	public Function getFunY() {
		return funY;
	}

	public void clearCasEvalMap(String key) {
		funX.clearCasEvalMap(key);
		funY.clearCasEvalMap(key);		
	}

}
