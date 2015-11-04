/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.ParametricCurveDistanceFunction;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoMacroInterface;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.optimization.ExtremumFinder;
import org.geogebra.common.kernel.roots.RealRootFunction;
import org.geogebra.common.kernel.roots.RealRootUtil;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;

/**
 * Cartesian parametric curve, e.g. (cos(t), sin(t)) for t from 0 to 2pi.
 * 
 * @author Markus Hohenwarter
 */
public class GeoCurveCartesian extends GeoCurveCartesianND implements
		Transformable, Translateable, PointRotateable, Mirrorable,
		Dilateable, MatrixTransformable, ParametricCurve,
		ConicMirrorable {


	//private Function funX, funY;
	private boolean isClosedPath;
	private boolean trace = false;

	/**
	 * Creates new curve
	 * 
	 * @param c
	 *            construction
	 * 
	 */
	public GeoCurveCartesian(Construction c) {
		super(c, 2, null);
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
	public GeoCurveCartesian(Construction c, Function fx, Function fy,
			ExpressionNode point) {
		super(c, 2, point);
		setFunctionX(fx);
		setFunctionY(fy);
	}

	@Override
	public String translatedTypeString() {
		return this.getLoc().getPlain("Curve");
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
		super(f.cons, 2, null);
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
		setFun(0, funX);
	}

	/**
	 * Sets the function of the y coordinate of this curve.
	 * 
	 * @param funY
	 *            new y-coord function
	 */
	final public void setFunctionY(Function funY) {
		setFun(1, funY);
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
		if (getFun(0) != null) {
			getFun(0).replaceChildrenByValues(geo);
		}
		if (getFun(1) != null) {
			getFun(1).replaceChildrenByValues(geo);
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
		this.isClosedPath = Kernel.isEqual(getFun(0).evaluate(startParam),
				getFun(0).evaluate(endParam), Kernel.MIN_PRECISION)
				&& Kernel.isEqual(getFun(1).evaluate(startParam),
						getFun(1).evaluate(endParam), Kernel.MIN_PRECISION);
	}

	@Override
	public void set(GeoElementND geo) {
		GeoCurveCartesian geoCurve = (GeoCurveCartesian) geo;

		setFun(0, new Function(geoCurve.fun[0], this.kernel));
		setFun(1, new Function(geoCurve.fun[1], this.kernel));
		this.startParam = geoCurve.startParam;
		this.endParam = geoCurve.endParam;
		this.isDefined = geoCurve.isDefined;

		// macro OUTPUT
		if (geo.getConstruction() != this.cons && isAlgoMacroOutput()) {
			if (!geo.isIndependent()) {
				// System.out.println("set " + this.label);
				// System.out.println("   funX before: " +
				// funX.toLaTeXString(true));

				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's
				// expression
				AlgoMacroInterface algoMacro = (AlgoMacroInterface) getParentAlgorithm();
				algoMacro.initFunction(getFun(0));
				algoMacro.initFunction(getFun(1));
				// System.out.println("   funX after: " +
				// funX.toLaTeXString(true));
			}
		}
		this.distFun = null;
	}


	

	
	/**
	 * Sets this curve to the parametric derivative of the given curve c. The
	 * parametric derivative of a curve c(t) = (x(t), y(t)) is defined as (x(t),
	 * y'(t)/x'(t)).
	 * @param curve curve whose derivative we want
	 */
	public void setParametricDerivative(GeoCurveCartesian curve) {
		if (curve.isDefined()) {
			setFun(0, curve.fun[0]);
			setFun(1, Function.getDerivativeQuotient(curve.fun[0], curve.fun[1]));
			this.isDefined = !(getFun(0) == null || getFun(1) == null);
			if (this.isDefined)
				setInterval(curve.startParam, curve.endParam);
		} else {
			this.isDefined = false;
		}
		this.distFun = null;
	}

	
	// added by Loic Le Coq 2009/08/12
	/**
	 * @param tpl string template
	 * @return value string x-coord function
	 */
	final public String getFunX(StringTemplate tpl) {
		return getFun(0).toValueString(tpl);
	}

	/**
	 * @param tpl string template
	 * @return value string y-coord function
	 */
	final public String getFunY(StringTemplate tpl) {
		return getFun(1).toValueString(tpl);
	}

	// end Loic Le Coq

	@Override
	final public RealRootFunction getRealRootFunctionX() {
		return getFun(0);
	}

	@Override
	final public RealRootFunction getRealRootFunctionY() {
		return getFun(1);
	}

	/**
	 * translate function by vector v
	 */
	@Override
	final public void translate(Coords v) {
		getFun(0).translateY(v.getX());
		getFun(1).translateY(v.getY());
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
		getFun(0).translateY(vx);
		getFun(1).translateY(vy);
	}

	@Override
	final public void rotate(NumberValue phi, GeoPointND point) {
		Coords P = point.getInhomCoords();
		translate(-P.getX(), -P.getY());
		rotate(phi);
		translate(P.getX(), P.getY());
	}

	@Override
	final public void mirror(Coords P) {
		dilate(new MyDouble(this.kernel, -1.0), P);
	}

	@Override
	final public void mirror(GeoLineND g1) {
		
		GeoLine g = (GeoLine) g1;

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
		mirror(new MyDouble(this.kernel, 2.0 * Math.atan2(-g.getX(), g.getY())));

		// translate back +Q
		translate(-qx, -qy);

		// update inhom coords
	}

	@Override
	final public void rotate(NumberValue phi) {
		double cosPhi = Math.cos(phi.getDouble());
		double sinPhi = Math.sin(phi.getDouble());
		matrixTransform(cosPhi, -sinPhi, sinPhi, cosPhi);
	}

	
	@Override
	public void dilate(NumberValue ratio, Coords P) {
		translate(-P.getX(), -P.getY());
		ExpressionNode exprX = getFun(0).deepCopy(this.kernel)
				.getExpression();
		ExpressionNode exprY = getFun(1).deepCopy(this.kernel)
				.getExpression();
		getFun(0).setExpression(new ExpressionNode(this.kernel, ratio,
				Operation.MULTIPLY, exprX));
		getFun(1).setExpression(new ExpressionNode(this.kernel, ratio,
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
			point[0] = getFun(0).evaluate(v);
			point[1] = getFun(1).evaluate(v);
			pointList.add(new GeoPoint(this.cons, point[0], point[1], 1));
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
	@Override
	public void matrixTransform(double a, double b, double c, double d) {
		MyDouble ma = new MyDouble(this.kernel, a);
		MyDouble mb = new MyDouble(this.kernel, b);
		MyDouble mc = new MyDouble(this.kernel, c);
		MyDouble md = new MyDouble(this.kernel, d);
		ExpressionNode exprX = getFun(0).deepCopy(this.kernel)
				.getExpression();
		ExpressionNode exprY = getFun(1).deepCopy(this.kernel)
				.getExpression();
		ExpressionNode transX = exprX.multiply(ma).plus(exprY.multiply(mb));
		ExpressionNode transY = exprX.multiply(mc).plus(exprY.multiply(md));
		getFun(0).setExpression(transX);
		getFun(1).setExpression(transY);
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
			sbToString.append(this.label);
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

		if (this.isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);

			if (tpl.getStringType().equals(StringType.GIAC)) {
				// eg plotparam([t,t^2],t,-10,10)
				// TODO: remove wrapping in equation when Giac supports intersecting equation, parametric
				sbTemp.append("equation(plotparam([");
				sbTemp.append(getFun(0).toValueString(tpl));
				sbTemp.append(',');
				sbTemp.append(getFun(1).toValueString(tpl));
				sbTemp.append("],");
				sbTemp.append(getFun(0).getFunctionVariable().toString(StringTemplate.giacTemplate));
				sbTemp.append(',');
				sbTemp.append(this.kernel.format(getMinParameter(), StringTemplate.giacTemplate));
				sbTemp.append(',');
				sbTemp.append(this.kernel.format(getMaxParameter(), StringTemplate.giacTemplate));
				sbTemp.append("))");
			} else {

				sbTemp.append('(');
				sbTemp.append(getFun(0).toValueString(tpl));
				sbTemp.append(", ");
				sbTemp.append(getFun(1).toValueString(tpl));
				sbTemp.append(')');
			}

			return sbTemp.toString();
		}
		return "?";
	}

	// TODO remove and use super method (funX and funY should be removed in
	// fun[])

	@Override
	public String toSymbolicString(StringTemplate tpl) {
		if (this.isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);
			sbTemp.setLength(0);
			sbTemp.append('(');
			sbTemp.append(getFun(0).toString(tpl));
			sbTemp.append(", ");
			sbTemp.append(getFun(1).toString(tpl));
			sbTemp.append(')');
			return sbTemp.toString();
		}
		return "?";
	}

	/*
	 * Path interface
	 */
	@Override
	public void pointChanged(GeoPointND P) {

		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		pp.t = t;
		pathChanged(P,false);
	}

	@Override
	public boolean isOnPath(GeoPointND PI, double eps) {

		GeoPoint P = (GeoPoint) PI;

		if (P.getPath() == this)
			return true;

		// get closest parameter position on curve
		PathParameter pp = P.getPathParameter();
		double t = getClosestParameter(P, pp.t);
		boolean onPath = Math.abs(getFun(0).evaluate(t) - P.getInhomX()) <= eps
				&& Math.abs(getFun(1).evaluate(t) - P.getInhomY()) <= eps;
		return onPath;
	}

	@Override
	public void pathChanged(GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		pathChanged(PI,!getKernel().usePathAndRegionParameters(PI));

	}
	
	private void pathChanged(GeoPointND P,boolean changePoint) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (changePoint) {
			pointChanged(P);
			return;
		}


		PathParameter pp = P.getPathParameter();
		if (pp.t < this.startParam)
			pp.t = this.startParam;
		else if (pp.t > this.endParam)
			pp.t = this.endParam;

		// calc point for given parameter
		P.setCoords2D(getFun(0).evaluate(pp.t), getFun(1).evaluate(pp.t), 1);
		P.updateCoordsFrom2D(false, null);
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
	@Override
	public double getClosestParameter(GeoPointND P, double startValue) {
		double startVal = startValue;
		if (this.distFun == null)
			this.distFun = new ParametricCurveDistanceFunction(this);
		
		this.distFun.setDistantPoint(P);

		// check if P is on this curve and has the right path parameter already
		if (P.getPath() == this) {
			// point A is on curve c, take its parameter
			PathParameter pp = P.getPathParameter();
			double pathParam = pp.t;
			if (this.distFun.evaluate(pathParam) < Kernel.MIN_PRECISION
					* Kernel.MIN_PRECISION)
				return pathParam;

			// if we don't have a startValue yet, let's take the path parameter
			// as a guess
			if (Double.isNaN(startVal))
				startVal = pathParam;
		}

		// first sample distFun to find a start intervall for ExtremumFinder
		double step = (this.endParam - this.startParam) / CLOSEST_PARAMETER_SAMPLES;
		double minVal = this.distFun.evaluate(this.startParam);
		double minParam = this.startParam;
		double t = this.startParam;
		for (int i = 0; i < CLOSEST_PARAMETER_SAMPLES; i++) {
			t = t + step;
			double ft = this.distFun.evaluate(t);
			if (ft < minVal) {
				// found new minimum
				minVal = ft;
				minParam = t;
			}
		}
		
		if (minParam - step < this.getMinParameter() || minParam + step > this.getMaxParameter()) {
			// at end, so can't sample either side!
			// #4567
			return minParam;
		}

		// use interval around our minParam found by sampling
		// to find minimum
		// Math.max/min removed and ParametricCurveDistanceFunction modified instead 
		double left = minParam - step; 
		double right = minParam + step;

		ExtremumFinder extFinder = this.kernel.getExtremumFinder();
		double sampleResult = extFinder.findMinimum(left, right, this.distFun,
				Kernel.MIN_PRECISION);
		
		sampleResult = adjustRange(sampleResult);

		// if we have a valid startParam we try the interval around it too
		// however, we don't check the same interval again
		if (!Double.isNaN(startVal)
				&& (startVal < left || right < startVal)) {
			
			// Math.max/min removed and ParametricCurveDistanceFunction modified instead 
			left = startVal - step; 
			right = startVal + step;

			double startValResult = extFinder.findMinimum(left, right, this.distFun,
					Kernel.MIN_PRECISION);
			
			startValResult = adjustRange(startValResult); 
			
			if (this.distFun.evaluate(startValResult) < this.distFun
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
		if (startValResult < this.startParam) { 
			return startValResult + (this.endParam - this.startParam); 
		} 

		if (startValResult > this.endParam) { 
			return startValResult - (this.endParam - this.startParam); 
		} 

		return startValResult; 
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public boolean isClosedPath() {
		return this.isClosedPath;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	final public boolean isTraceable() {
		return true;
	}

	@Override
	final public boolean getTrace() {
		return this.trace;
	}

	// G.Sturr 2010-5-18 get/set spreadsheet trace not needed here
	/*
	 * public void setSpreadsheetTrace(boolean spreadsheetTrace) {
	 * this.spreadsheetTrace = spreadsheetTrace; }
	 * 
	 * public boolean getSpreadsheetTrace() { return spreadsheetTrace; }
	 */

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	/**
	 * Calculates the Cartesian coordinates of this curve for the given
	 * parameter paramVal. The result is written to out.
	 */
	@Override
	public void evaluateCurve(double paramVal, double[] out) {
		out[0] = getFun(0).evaluate(paramVal);
		out[1] = getFun(1).evaluate(paramVal);
	}

	@Override
	public GeoVec2D evaluateCurve(double t) {
		return new GeoVec2D(this.kernel, getFun(0).evaluate(t), getFun(1).evaluate(t));
	}

	/**
	 * Calculates curvature for curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T =
	 * sqrt(a'(t)^2+b'(t)^2)
	 * 
	 * @author Victor Franco, Markus Hohenwarter
	 */
	@Override
	public double evaluateCurvature(double t) {
		Function f1X, f1Y, f2X, f2Y;
		f1X = getFun(0).getDerivative(1, true);
		f1Y = getFun(1).getDerivative(1, true);
		f2X = getFun(0).getDerivative(2, true);
		f2Y = getFun(1).getDerivative(2, true);

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

	@Override
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
	final public void mirror(GeoConic c) {
		if (c.getType() == GeoConicNDConstants.CONIC_CIRCLE) {

			// Mirror point in circle
			double r = c.getHalfAxes()[0];
			GeoVec2D midpoint = c.getTranslationVector();
			double a = midpoint.getX();
			double b = midpoint.getY();
			this.translate(-a, -b);
			ExpressionNode exprX = getFun(0).deepCopy(this.kernel)
					.getExpression();
			ExpressionNode exprY = getFun(1).deepCopy(this.kernel)
					.getExpression();
			MyDouble d2 = new MyDouble(this.kernel, 2);
			ExpressionNode sf = new ExpressionNode(this.kernel, new MyDouble(this.kernel,
					r * r), Operation.DIVIDE, exprX.power(d2).plus(
					exprY.power(d2)));
			ExpressionNode transX = exprX.multiply(sf);
			ExpressionNode transY = exprY.multiply(sf);
			getFun(0).setExpression(transX);
			getFun(1).setExpression(transY);
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
		return MyMath.length(getFun(0).evaluate(t) - p.getX(),
				getFun(1).evaluate(t) - p.getY());
	}
	
	@Override
	public double distance(GeoPointND p) {
		
		if (!p.isGeoElement3D()){
			return distance((GeoPoint) p);
		}
		
		double t = getClosestParameter(p, 0);
		Coords coords = p.getInhomCoordsInD3();
		return MyMath.length(getFun(0).evaluate(t) - coords.getX(),
				getFun(1).evaluate(t) - coords.getY(), coords.getZ());
	}
	
	

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		MyDouble ma00 = new MyDouble(this.kernel, a00);
		MyDouble ma01 = new MyDouble(this.kernel, a01);
		MyDouble ma02 = new MyDouble(this.kernel, a02);
		MyDouble ma10 = new MyDouble(this.kernel, a10);
		MyDouble ma11 = new MyDouble(this.kernel, a11);
		MyDouble ma12 = new MyDouble(this.kernel, a12);
		MyDouble ma20 = new MyDouble(this.kernel, a20);
		MyDouble ma21 = new MyDouble(this.kernel, a21);
		MyDouble ma22 = new MyDouble(this.kernel, a22);

		ExpressionNode exprX = getFun(0).deepCopy(this.kernel)
				.getExpression();
		ExpressionNode exprY = getFun(1).deepCopy(this.kernel)
				.getExpression();
		ExpressionNode transX = exprX.multiply(ma00).plus(exprY.multiply(ma01))
				.plus(ma02);
		ExpressionNode transY = exprX.multiply(ma10).plus(exprY.multiply(ma11))
				.plus(ma12);
		ExpressionNode transZ = exprX.multiply(ma20).plus(exprY.multiply(ma21))
				.plus(ma22);
		getFun(0).setExpression(new ExpressionNode(this.kernel, transX, Operation.DIVIDE,
				transZ));
		getFun(1).setExpression(new ExpressionNode(this.kernel, transY, Operation.DIVIDE,
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

		if (points.length < 2) {
			setUndefined();
			return;
		}
		ExpressionNode enx = new ExpressionNode(this.kernel, new MyDouble(this.kernel,
				points[0].getInhomCoordsInD2().getX()));
		ExpressionNode eny = new ExpressionNode(this.kernel, new MyDouble(this.kernel,
				points[0].getInhomCoordsInD2().getY()));
		FunctionVariable fv = new FunctionVariable(this.kernel, "t");
		double sum = 0;
		double sumY = 0;
		int limit = repeatLast ? points.length + 1 : points.length;
		int nonzeroSegments = 0;
		for (int i = 1; i < limit; i++) {
			int pointIndex = i >= points.length ? 0 : i;
			ExpressionNode greater = new ExpressionNode(this.kernel,
					new ExpressionNode(this.kernel, fv, Operation.MINUS,
							new MyDouble(this.kernel, nonzeroSegments)), Operation.ABS, null);
			Coords c1 = points[pointIndex].getInhomCoordsInD2();
			Coords c2 = points[i-1].getInhomCoordsInD2();
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
			enx = enx.plus(greater.multiply(new MyDouble(this.kernel, coef)));
			eny = eny.plus(greater.multiply(new MyDouble(this.kernel, coefY)));
		}
		enx = enx.plus(new ExpressionNode(this.kernel, fv, Operation.MULTIPLY,
				new MyDouble(this.kernel, cumulative)));
		eny = eny.plus(new ExpressionNode(this.kernel, fv, Operation.MULTIPLY,
				new MyDouble(this.kernel, cumulativeY)));
		enx = enx.plus(new MyDouble(this.kernel, -sum));
		eny = eny.plus(new MyDouble(this.kernel, -sumY));
		Function xFun = new Function(enx, fv);
		Function yFun = new Function(eny, fv);
		this.setFunctionY(yFun);

		this.setFunctionX(xFun);
		this.setInterval(0, nonzeroSegments);
	}

	/**
	 * @return x-function
	 */
	public Function getFunX() {
		return getFun(0);
	}

	/**
	 * @return y-function
	 */
	public Function getFunY() {
		return getFun(1);
	}

	
	
	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public double[] newDoubleArray(){
		return new double[2];
	}
	
	
	@Override
	public double[] getDefinedInterval(double a, double b){
		double[] intervalX = RealRootUtil.getDefinedInterval(
				getRealRootFunctionX(), a, b);
		double[] intervalY = RealRootUtil.getDefinedInterval(
				getRealRootFunctionY(), a, b);
		
		if (intervalX[0] < intervalY[0]){
			intervalX[0] = intervalY[0];
		}
		
		if (intervalX[1] > intervalY[1]){
			intervalX[1] = intervalY[1];
		}
		
		return intervalX;
	}
	
	
	@Override
	public double distanceMax(double[] p1, double[] p2){
		return Math.max(Math.abs(p1[0] - p2[0]), Math.abs(p1[1] - p2[1]));
	}
	
	@Override
	protected GeoCurveCartesianND newGeoCurveCartesian(Construction cons1) {
		return new GeoCurveCartesian(cons1);
	}

	

	@Override
	final public HitType getLastHitType(){
		return HitType.ON_BOUNDARY;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	public ValueType getValueType() {
		return ValueType.PARAMETRIC2D;
	}

}
