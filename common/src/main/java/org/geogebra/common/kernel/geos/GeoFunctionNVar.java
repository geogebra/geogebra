/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import java.util.TreeMap;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.AutoColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMacroInterface;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.kernel.matrix.CoordsDouble3;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;

/**
 * Explicit function in multiple variables, e.g. f(a, b, c) := a^2 + b - 3c.
 * This is actually a wrapper class for FunctionNVar in
 * geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode) it evaluates
 * to a FunctionNVar.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunctionNVar extends GeoElement
		implements FunctionalNVar, CasEvaluableFunction, Region, Transformable,
		Translateable, MatrixTransformable, Dilateable, PointRotateable,
		Mirrorable, SurfaceEvaluable {

	private static final double STRICT_INEQ_OFFSET = 4 * Kernel.MIN_PRECISION;
	private static final int SEARCH_SAMPLES = 70;
	/** helper for pointin region dichotomy */
	public static final int DICHO_FIRST = 0;
	/** helper for pointin region dichotomy */
	public static final int DICHO_LAST = 1;
	/** helper for pointin region dichotomy */
	public static final int DICHO_MID = 2;

	private static final int DICHO_MAX_STEP = 20;

	private static FunctionExpander functionExpander;

	private FunctionNVar fun;
	/** derivative functions */
	private FunctionNVar[] fun1;
	// private List<Inequality> ineqs;
	private Boolean isInequality;
	private boolean isDefined = true;
	private LevelOfDetail levelOfDetail = LevelOfDetail.SPEED;

	/** intervals for plotting, may be null (then interval is R) */
	private double[] from;
	private double[] to;
	private StringBuilder sbToString = new StringBuilder(80);
	private Equation equalityChecker;
	private GeoPoint helper;

	private boolean hasLastHitParameters = false;
	private double[][] xyzf;

	private Coords der1 = new Coords(1, 0, 0);
	private Coords der2 = new Coords(0, 1, 0);
	private Coords normal = new Coords(3);

	private CoordsDouble3 p1 = new CoordsDouble3();
	private CoordsDouble3 p2 = new CoordsDouble3();
	private String shortLHS;

	/**
	 * Creates new GeoFunction
	 * 
	 * @param c
	 *            construction
	 */
	public GeoFunctionNVar(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}

	/**
	 * @param c
	 *            construction
	 * @param defaults
	 *            true to set defaults right away
	 */
	public GeoFunctionNVar(Construction c, boolean defaults) {
		super(c);
		if (defaults) {
			setConstructionDefaults();
		}
	}

	/**
	 * Creates new GeoFunction from Function
	 * 
	 * @param c
	 *            construction
	 * @param f
	 *            function to be wrapped
	 * @param simplifyInt
	 *            whether integers should be simplified eg 2*2 replaced by 4
	 */
	public GeoFunctionNVar(Construction c, FunctionNVar f,
			boolean simplifyInt) {
		this(c, false);
		setFunction(f);
		fun.initFunction(simplifyInt);
		isInequality = fun.initIneqs(this.getFunctionExpression());

		setConstructionDefaults();
	}

	/**
	 * Creates labeled GeoFunction from Function
	 * 
	 * @param c
	 *            construction
	 * @param f
	 *            function to be wrapped
	 */
	public GeoFunctionNVar(Construction c, FunctionNVar f) {
		this(c, f, true);
	}

	/**
	 * @return whether function contains only valid variables
	 */
	public boolean validate() {
		if (!cons.isFileLoading()) {
			if (getFunctionExpression().containsFreeFunctionVariableOtherThan(
					getFunctionVariables())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getTypeString() {
		return ((isInequality != null && isInequality) || isForceInequality()) ? GeoClass.INEQUALITY
				: GeoClass.MULTIVARIABLE_FUNCTION;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.FUNCTION_NVAR;
	}

	/**
	 * copy constructor
	 * 
	 * @param f
	 *            source function
	 */
	public GeoFunctionNVar(GeoFunctionNVar f) {
		this(f.cons);
		set(f);
	}

	@Override
	public GeoElement copy() {
		return new GeoFunctionNVar(this);
	}

	@Override
	public void set(GeoElementND geo) {

		// reset derivatives
		fun1 = null;

		if (geo instanceof GeoNumeric) {
			fun.setExpression(geo.wrap());
			return;
		}
		FunctionalNVar geoFun = (FunctionalNVar) geo;

		if (geo == null || geoFun.getFunction() == null) {
			fun = null;
			isDefined = false;
			return;
		}
		isDefined = geo.isDefined();
		FunctionVariable[] oldVars = fun == null ? null
				: fun.getFunctionVariables();
		setFunction(new FunctionNVar(geoFun.getFunction(), kernel));
		fun.fillVariables(oldVars);
		// macro OUTPUT
		if (geo.getConstruction() != cons && isAlgoMacroOutput()) {
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in its function's
			// expression
			if (!geo.isIndependent()) {
				AlgoMacroInterface algoMacro = (AlgoMacroInterface) getParentAlgorithm();
				algoMacro.initFunction(this.fun);
			}
		}
		if (geo instanceof GeoFunctionNVar) {
			setForceInequality(((GeoFunctionNVar) geo).isForceInequality());
		}
		isInequality = fun.initIneqs(this.getFunctionExpression());
	}

	/**
	 * @param f
	 *            new function
	 */
	public void setFunction(FunctionNVar f) {
		if (fun != null && f != null && fun.isForceInequality()) {
			f.setForceInequality(true);
		}
		fun = f;

		// reset derivatives
		fun1 = null;
	}

	@Override
	public void setDerivatives() {

		// check if derivatives already exist
		if (fun1 != null) {
			return;
		}

		// set derivatives
		FunctionVariable[] vars = fun.getFunctionVariables();
		fun1 = new FunctionNVar[vars.length];

		if (functionExpander == null) {
			functionExpander = new FunctionExpander();
		}
		ValidExpression ve = fun.deepCopy(getKernel());
		ve = (ValidExpression) ve.traverse(functionExpander);
		for (int i = 0; i < vars.length; i++) {
			fun1[i] = new FunctionNVar(ve.derivative(vars[i], kernel).wrap(),
					vars);
		}
	}

	@Override
	public void resetDerivatives() {
		fun1 = null;
	}

	@Override
	final public @CheckForNull FunctionNVar getFunction() {
		return fun;
	}

	/**
	 * @return expression of the wrapped function
	 */
	@Override
	final public ExpressionNode getFunctionExpression() {
		if (fun == null) {
			return null;
		}
		return fun.getExpression();
	}

	/**
	 * Replaces geo and all its dependent geos in this function's expression by
	 * copies of their values.
	 * 
	 * @param geo
	 *            geo to be replaced
	 */
	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		if (fun != null) {
			fun.replaceChildrenByValues(geo);
		}
	}

	/**
	 * Returns this function's value at position.
	 * 
	 * @param vals
	 *            variable values
	 * @return f(vals)
	 */
	@Override
	public double evaluate(double[] vals) {
		// Application.printStacktrace("");
		if (fun == null || !isDefined) {
			return Double.NaN;
		}
		return fun.evaluate(vals);
	}

	/**
	 * @param vals
	 *            variable values
	 * @return value at vals
	 */
	public Coords evaluatePoint(double[] vals) {
		// Application.printStacktrace("");
		if (fun == null) {
			return null;
		}
		return new Coords(vals[0], vals[1], fun.evaluate(vals));
	}

	/**
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return value at (x,y)
	 */
	@Override
	public double evaluate(double x, double y) {
		// Application.printStacktrace("");
		if (fun == null) {
			return Double.NaN;
		}
		return fun.evaluate(x, y);
	}

	/**
	 * #4076 make sure if CAS returns "?" function is undefined so eg
	 * Integral[f,a,b] uses numerical method
	 */
	private void checkDefined() {
		isDefined = fun != null;

		if (fun != null && "?"
				.equals(fun.toValueString(StringTemplate.defaultTemplate))) {
			isDefined = false;
		}

	}

	/**
	 * Sets this function by applying a GeoGebraCAS command to a function.
	 * 
	 * @param ggbCasCmd
	 *            the GeoGebraCAS command needs to include % in all places where
	 *            the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param f
	 *            the function that the CAS command is applied to
	 */
	@Override
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic, MyArbitraryConstant arbconst) {

		// reset derivatives
		fun1 = null;

		GeoFunctionNVar ff = (GeoFunctionNVar) f;

		if (ff.isDefined()) {
			setFunction(ff.fun.evalCasCommand(ggbCasCmd, symbolic, arbconst));
			checkDefined();
		} else {
			isDefined = false;
		}
	}

	@Override
	public boolean isDefined() {
		return isDefined && fun != null && GeoFunction.isFunctionDefined(fun);
	}

	/**
	 * @param defined
	 *            true to make this defined
	 */
	@Override
	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	@Override
	public void setUndefined() {
		isDefined = false;
	}

	@Override
	protected boolean showInEuclidianView() {
		if (fun != null && isInequality == null && isBooleanFunction()) {
			getIneqs();
		}
		return isDefined() && (!isBooleanFunction() || isInequality);
	}

	@Override
	public String getAssignmentLHS(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(tpl.printVariableName(label));
		if (this.getLabelDelimiter() != ':') {
			tpl.appendWithBrackets(sbToString, getVarString(tpl));
		}
		return sbToString.toString();
	}

	/**
	 * @return function description as f(x,y)=... for real and e.g. f:x>4*y for
	 *         bool
	 */
	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		if (isLabelSet()) {
			GeoFunction.initStringBuilder(sbToString, tpl, label, this);
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (isDefined()) {
			return fun.toValueString(tpl);
		}
		return "?";
	}

	@Override
	public String toSymbolicString(StringTemplate tpl) {
		if (isDefined()) {
			return fun.toString(tpl);
		}
		return "?";
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (isDefined()) {
			return fun.toLaTeXString(symbolic, tpl);
		}
		return "?";
	}

	@Override
	public char getLabelDelimiter() {
		return isBooleanFunction() || isForceInequality() || shortLHS != null ? ':' : '=';
	}

	/**
	 * save object in xml format
	 */
	@Override
	public final void getXML(boolean getListenersToo, StringBuilder sb) {

		// an indpendent function needs to add
		// its expression itself
		// e.g. f(a,b) = a^2 - 3*b
		if (isIndependent() && getDefaultGeoType() < 0) {
			sb.append("<expression");
			sb.append(" label=\"");
			sb.append(label);
			sb.append("\" exp=\"");
			StringUtil.encodeXML(sb, toString(StringTemplate.xmlTemplate));
			sb.append("\" type=\"");
			sb.append(getFunctionType());
			sb.append("\"/>\n");
		}

		getElementOpenTagXML(sb);
		getXMLtags(sb);
		getCaptionXML(sb);
		if (getListenersToo) {
			getListenerTagsXML(sb);
		}
		// sb.append(sb);
		sb.append("</element>\n");
	}

	/**
	 * function type
	 * @return type of function (inequality or function)
	 */
	public String getFunctionType() {
		return isForceInequality() ? "inequality"
				: "function";
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean isBooleanFunction() {
		if (fun != null) {
			return fun.isBooleanFunction();
		}
		return false;
	}

	@Override
	public String getVarString(StringTemplate tpl) {
		return fun == null ? "" : fun.getVarString(tpl);
	}

	@Override
	public boolean isEqual(GeoElementND geo) {
		if (!(geo instanceof GeoFunctionNVar) || !isDefined() || !geo.isDefined()) {
			return false;
		}

		// try for polynomials first
		// (avoid loading the CAS if at all possible)
		if (equalityChecker == null) {
			equalityChecker = new Equation(kernel, getFunctionExpression(),
					((GeoFunctionNVar) geo).getFunctionExpression());
		} else {
			equalityChecker.setLHS(getFunctionExpression());
			equalityChecker
					.setRHS(((GeoFunctionNVar) geo).getFunctionExpression());
		}

		try {
			// not polynomial (or some other problem) -> check in CAS
			equalityChecker.initEquation();
		} catch (MyError e) {
			return isDifferenceZeroInCAS(geo);
		}

		if (!equalityChecker.isPolynomial()) {
			// not polynomial -> check in CAS
			return isDifferenceZeroInCAS(geo);
		}

		ExpressionValue[][] coeffs = equalityChecker.getNormalForm().getCoeff();

		for (int i = 0; i < coeffs.length; i++) {
			for (int j = 0; j < coeffs[i].length; j++) {
				ExpressionValue coeff = coeffs[i][j];

				// null -> no term
				if (coeff != null) {

					double coeffVal = coeff.evaluateDouble();
					// Log.debug("coeff is for " + i + " " + j + " is " +
					// coeffVal);
					if (!DoubleUtil.isZero(coeffVal)) {
						// one coefficient different -> definitely not equal
						// polynomials
						return false;
					}
				}
			}
		}

		// poly && all coefficients zero
		return true;
	}

	/**
	 * Returns a representation of geo in currently used CAS syntax. For
	 * example, "a*x^2 + b*y"
	 */
	@Override
	public String getCASString(StringTemplate tpl, boolean symbolic) {
		return fun.getExpression().getCASstring(tpl, symbolic);
	}

	/*
	 * public String getLabelForAssignment() { StringBuilder sb = new
	 * StringBuilder(); sb.append(getLabel()); sb.append("(" );
	 * sb.append(fun.getVarString(kernel.getStringTemplate())); sb.append(")");
	 * return sb.toString(); }
	 */

	// ///////////////////////////////////////
	// INTERVALS
	// ///////////////////////////////////////

	/**
	 * return Double.NaN if none has been set
	 * 
	 * @param index
	 *            of parameter
	 * @return min parameter
	 */
	@Override
	public double getMinParameter(int index) {

		if (from == null) {
			return Double.NaN;
		}

		return from[index];

	}

	/**
	 * return Double.NaN if none has been set
	 * 
	 * @param index
	 *            of parameter
	 * @return max parameter
	 */
	@Override
	public double getMaxParameter(int index) {

		if (to == null) {
			return Double.NaN;
		}

		return to[index];
	}

	/**
	 * Sets the start and end parameters values of this function.
	 * 
	 * @param from
	 *            start param
	 * @param to
	 *            end param
	 */
	public void setInterval(double[] from, double[] to) {

		this.from = from;
		this.to = to;

	}

	// ///////////////////////////////////////
	// For 3D
	// ///////////////////////////////////////

	private double evaluateForDrawSurface(double u, double v) {
		if (isBooleanFunction()) {
			if (fun.evaluateBoolean(u, v)) {
				return 0;
			}
			return Double.NaN;
		}
		if (from != null && to != null) {
			if (u < this.from[0] || u > this.to[0]) {
				return Double.NaN;
			}
			if (v < this.from[1] || v > this.to[1]) {
				return Double.NaN;
			}
		}
		return fun.evaluate(u, v);
	}

	@Override
	public void evaluatePoint(double u, double v, Coords3 p) {
		if ("x".equals(shortLHS)) {
			p.set(evaluateForDrawSurface(u, v), u, v);
		} else if ("y".equals(shortLHS)) {
			p.set(u, evaluateForDrawSurface(u, v), v);
		} else {
			p.set(u, v, evaluateForDrawSurface(u, v));
		}
	}

	/**
	 * 
	 * @return number of vars
	 */
	public int getVarNumber() {
		return fun == null ? 0 : fun.getVarNumber();
	}

	// will be drawn as a surface if can be interpreted as (x,y)->z function
	// or implicit f(x,y,z)=0 function
	@Override
	public boolean hasDrawable3D() {
		return getVarNumber() == 2 || getVarNumber() == 3;
	}

	@Override
	public Coords getLabelPosition() {
		return Coords.O; // TODO
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		if (fun == null) {
			return true;
		}
		return hasDrawable3D();
	}

	@Override
	public boolean hasFillType() {
		return isInequality();
	}

	@Override
	public boolean isInverseFillable() {
		return isInequality();
	}

	/**
	 * Reset all inequalities (slow, involves parser)
	 */
	public void resetIneqs() {
		isInequality = fun.initIneqs(getFunctionExpression());
	}

	/**
	 * @return the ineqs
	 */
	@Override
	public IneqTree getIneqs() {
		if (fun.getIneqs() == null) {
			isInequality = fun.initIneqs(fun.getExpression());
		}
		return fun.getIneqs();
	}

	@Override
	public void update(boolean drag) {
		if (fun != null && fun.isBooleanFunction()) {
			if (fun.getIneqs() == null) {
				fun.initIneqs(fun.getFunctionExpression());
			}
			isInequality = fun.updateIneqs();
		}
		super.update(drag);
	}

	@Override
	public boolean isRegion() {
		return isBooleanFunction() || isRegion3D();
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		if (isBooleanFunction()) {
			P.updateCoords2D();
			return isInRegion(P.getX2D(), P.getY2D());
		}

		// 2 var function
		Coords coords = P.getInhomCoordsInD3();
		double z = fun.evaluate(coords.getX(), coords.getY());
		return DoubleUtil.isEqual(coords.getZ(), z);

	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		return fun.evaluateBoolean(x0, y0);
	}

	@Override
	public void pointChangedForRegion(GeoPointND P) {

		if (isBooleanFunction()) {
			if (!((GeoElement) P).isDefined()) {
				return;
			}
			RegionParameters rp = P.getRegionParameters();
			if (!isInRegion(P)) {
				double bestX = rp.getT1();
				double bestY = rp.getT2();
				double myX = P.getX2D();
				double myY = P.getY2D();
				double bestDist = (bestY - myY) * (bestY - myY)
						+ (bestX - myX) * (bestX - myX);
				if (DoubleUtil.isZero(bestDist)) {
					// not the best distance, since P is not in region
					bestDist = Double.POSITIVE_INFINITY;
				}

				IneqTree ineqs = getIneqs();
				int size = ineqs.getSize();
				for (int i = 0; i < size; i++) {
					Inequality in = ineqs.get(i);
					double px = 0, py = 0;
					if (in.getType() == IneqType.INEQUALITY_PARAMETRIC_Y) {
						px = P.getX2D();
						py = in.getFunBorder().value(px);
						py += in.isAboveBorder() ? STRICT_INEQ_OFFSET
								: -STRICT_INEQ_OFFSET;
					} else if (in
							.getType() == IneqType.INEQUALITY_PARAMETRIC_X) {
						py = P.getY2D();
						px = in.getFunBorder().value(py);
						px += in.isAboveBorder() ? STRICT_INEQ_OFFSET
								: -STRICT_INEQ_OFFSET;
					} else if (in.getType() == IneqType.INEQUALITY_LINEAR) {
						double a = in.getLineBorder().getX();
						double b = in.getLineBorder().getY();
						double c = in.getLineBorder().getZ();
						px = (-a * c + b * b * P.getX2D() - a * b * P.getY2D())
								/ (a * a + b * b);
						py = (-b * c - a * b * P.getX2D() + a * a * P.getY2D())
								/ (a * a + b * b);
						py -= in.isAboveBorder() ? STRICT_INEQ_OFFSET
								: -STRICT_INEQ_OFFSET;
					} else if (in.getType() == IneqType.INEQUALITY_CONIC) {
						if (helper == null) {
							helper = new GeoPoint(cons);
						}
						helper.setCoordsFromPoint(P);
						helper.setPath(in.getConicBorder());
						in.getConicBorder().pointChanged(helper);

						px = helper.getX() / helper.getZ();
						py = helper.getY() / helper.getZ();
					}
					double myDist = (py - myY) * (py - myY)
							+ (px - myX) * (px - myX);

					// check for NaN for first call
					if (Double.isNaN(bestX) || Double.isNaN(bestY)
							|| (myDist < bestDist) && isInRegion(px, py)) {
						bestDist = myDist;
						bestX = px;
						bestY = py;
					}
				}
				if (isInRegion(bestX, bestY)) {
					rp.setT1(bestX);
					rp.setT2(bestY);
					P.setCoords(new Coords(bestX, bestY, 0, 1), false);
				} else {
					tryLocateInEV(P);
				}

			} else {
				rp.setT1(P.getX2D());
				rp.setT2(P.getY2D());
			}
		} else {
			// 2 var function
			Coords coords = P.getInhomCoordsInD3();
			if (hasLastHitParameters()) {
				int step = 0;
				double zScale = Math.sqrt(P.getZScale());
				do {
					stepDicho();
					step++;
				} while (step < DICHO_MAX_STEP
						&& isTooFar(xyzf[DICHO_MID], zScale));

				coords.setX(xyzf[DICHO_MID][0]);
				coords.setY(xyzf[DICHO_MID][1]);
				coords.setZ(xyzf[DICHO_MID][3]);
			} else {
				double z = fun.evaluate(coords.getX(), coords.getY());
				coords.setZ(z);
			}

			RegionParameters rp = P.getRegionParameters();
			rp.setT1(coords.getX());
			rp.setT2(coords.getY());
			Coords n = new Coords(4);
			evaluateNormal(coords.getX(), coords.getY(), n);
			rp.setNormal(n);
			P.setCoords(coords, false);
			P.updateCoords();

			resetLastHitParameters();
		}
	}

	@Override
	public boolean isRegion3D() {
		return getVarNumber() == 2 && !isBooleanFunction();
	}

	/**
	 * 
	 * @return xyzf arrays for dichotomy
	 */
	public double[][] getXYZF() {
		if (xyzf == null) {
			xyzf = new double[3][];
			xyzf[DICHO_FIRST] = new double[4];
			xyzf[DICHO_LAST] = new double[4];
			xyzf[DICHO_MID] = new double[4];
		}

		return xyzf;
	}

	/**
	 * reset last hitted parameters
	 */
	public void resetLastHitParameters() {
		hasLastHitParameters = false;
	}

	/**
	 * set last hitted parameters
	 * 
	 * @param swap
	 *            says if we have to swap first/last
	 * 
	 */
	public void setLastHitParameters(boolean swap) {
		if (swap) {
			double[] xyzfTmp = xyzf[DICHO_FIRST];
			xyzf[DICHO_FIRST] = xyzf[DICHO_LAST];
			xyzf[DICHO_LAST] = xyzfTmp;
		}
		hasLastHitParameters = true;
	}

	private boolean hasLastHitParameters() {
		return hasLastHitParameters;
	}

	final private static boolean isTooFar(double[] xyzf, double zScale) {
		return !DoubleUtil.isEqual(xyzf[2], xyzf[3],
				Kernel.STANDARD_PRECISION_SQRT / zScale);
	}

	/**
	 * 
	 * @param xyzf
	 *            x, y, z, f(x,y) values
	 * @return true if z < f
	 */
	public static boolean isLessZ(double[] xyzf) {
		return xyzf[2] < xyzf[3];
	}

	/**
	 * set x, y, z, f(x,y) values to xyzf
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 * @param xyzf
	 *            set values
	 */
	public void setXYZ(double x, double y, double z, double[] xyzf) {
		xyzf[0] = x;
		xyzf[1] = y;
		xyzf[2] = z;
		xyzf[3] = evaluate(xyzf);
	}

	/**
	 * do a dichotomy step
	 */
	public void stepDicho() {
		setXYZ((xyzf[DICHO_FIRST][0] + xyzf[DICHO_LAST][0]) / 2,
				(xyzf[DICHO_FIRST][1] + xyzf[DICHO_LAST][1]) / 2,
				(xyzf[DICHO_FIRST][2] + xyzf[DICHO_LAST][2]) / 2,
				xyzf[DICHO_MID]);

		// Log.debug("\n" + (xyzf[DICHO_FIRST][3] - xyzf[DICHO_FIRST][2]) + "/"
		// + (xyzf[DICHO_LAST][3] - xyzf[DICHO_LAST][2]) + " >> "
		// + (xyzf[DICHO_MID][3] - xyzf[DICHO_MID][2]));

		if (isLessZ(xyzf[DICHO_MID])) {
			double[] swap = xyzf[DICHO_FIRST];
			xyzf[DICHO_FIRST] = xyzf[DICHO_MID];
			xyzf[DICHO_MID] = swap;
		} else {
			double[] swap = xyzf[DICHO_LAST];
			xyzf[DICHO_LAST] = xyzf[DICHO_MID];
			xyzf[DICHO_MID] = swap;

		}
	}

	/**
	 * We seek for a point in region by desperately testing grid points in
	 * euclidian view. This should be called only when every algorithm fails.
	 * 
	 * @param P
	 *            point
	 */
	private void tryLocateInEV(GeoPointND P) {
		// EuclidianViewInterfaceSlim ev =
		// kernel.getApplication().getEuclidianView();
		boolean found = false;
		double xmin = kernel.getViewsXMin(P);
		double xmax = kernel.getViewsXMax(P);
		double ymin = kernel.getViewsYMin(P);
		double ymax = kernel.getViewsYMax(P);
		for (int i = 0; !found && i < SEARCH_SAMPLES; i++) {
			for (int j = 0; !found && j < SEARCH_SAMPLES; j++) {
				double p = i / (double) SEARCH_SAMPLES;
				double rx = p * xmin + (1 - p) * xmax;
				double q = i / (double) SEARCH_SAMPLES;
				double ry = q * ymin + (1 - q) * ymax;
				if (isInRegion(rx, ry)) {
					P.setCoords(new Coords(rx, ry, 0, 1), false);
					// Application.debug("Desperately found"+rx+","+ry);
					found = true;
				}
			}
		}
		if (!found) {
			P.setUndefined();
		}

	}

	@Override
	public void regionChanged(GeoPointND P) {
		pointChangedForRegion(P);

	}

	/**
	 * @return true if this function consists of valid inequalities
	 */
	public boolean isInequality() {
		return (isInequality != null && isInequality);
	}

	@Override
	public void translate(Coords v) {
		fun.translate(v.getX(), v.getY());
		this.getIneqs();
	}

	/**
	 * Perform 3D translation
	 * 
	 * @param v
	 *            translation vector
	 */
	public void translate3D(Coords v) {
		fun.translate(v.getX(), v.getY(), v.getZ());
	}

	/**
	 * Returns true if the element is translateable
	 * 
	 * @return true
	 */
	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {
		double d = a00 * a11 - a01 * a10;
		if (d == 0) {
			setUndefined();
		} else {
			fun.matrixTransform(a11 / d, -a01 / d, -a10 / d, a00 / d);
		}
		isInequality = null;
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		fun.dilate(r, S);
	}

	/**
	 * @param r
	 *            dilate factor
	 * @param S
	 *            coordinate
	 */
	public void dilate3D(NumberValue r, Coords S) {
		fun.dilate3D(r, S);
	}

	@Override
	public void rotate(NumberValue phi) {
		fun.rotate(phi);
		isInequality = null;
	}

	@Override
	public void rotate(NumberValue phi, GeoPointND point) {
		Coords P = point.getInhomCoords();
		fun.rotate(phi, P);
		isInequality = null;
	}

	@Override
	public void mirror(Coords Q) {
		fun.dilate(new MyDouble(kernel, -1.0), Q);
		isInequality = null;
	}

	/**
	 * @param Q
	 *            coordinate
	 */
	public void mirror3D(Coords Q) {
		dilate3D(new MyDouble(kernel, -1.0), Q);
	}

	/**
	 * @param g1
	 *            line
	 */
	public void mirror3D(GeoLineND g1) {
		if (g1 instanceof GeoLine) {
			mirror(g1);
			return;
		}
		Coords coords = g1.getDirectionInD3().normalize();
		double x = coords.getX();
		double y = coords.getY();
		double z = coords.getZ();
		matrixTransform(x * x - 1, x * y, x * z, x * y, y * y - 1, y * z, x * z,
				y * z, z * z - 1);
	}

	@Override
	public void mirror(GeoLineND g1) {
		fun.mirror((GeoLine) g1);
		isInequality = null;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		fun.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);
		isInequality = null;
	}

	@Override
	public boolean isGeoFunctionNVar() {
		return true;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// needed for inequalities
		if (showLineProperties()) {
			getLineStyleXML(sb);
		}

		// level of detail
		if (hasLevelOfDetail()
				&& (getLevelOfDetail() == LevelOfDetail.QUALITY)) {
			sb.append("\t<levelOfDetailQuality val=\"true\"/>\n");
		}
	}

	// /////////////////////////
	// LEVEL OF DETAIL

	@Override
	public LevelOfDetail getLevelOfDetail() {
		return levelOfDetail;
	}

	@Override
	public void setLevelOfDetail(LevelOfDetail lod) {
		levelOfDetail = lod;
	}

	@Override
	public boolean hasLevelOfDetail() {
		return isFun2Var();
	}

	private final boolean isInequalityOrFun2Var() {
		return isInequality() || ((fun != null) && (fun.getVarNumber() == 2));
	}

	/**
	 * 
	 * @return true if it's a function 2 var (not inequality)
	 */
	public final boolean isFun2Var() {
		return (fun != null) && (fun.getVarNumber() == 2) && !isInequality();
	}

	@Override
	public int getMinimumLineThickness() {
		if (isInequalityOrFun2Var()) {
			return 0;
		}
		return 1;
	}

	@Override
	public FunctionVariable[] getFunctionVariables() {
		return fun == null ? new FunctionVariable[0] : fun.getFunctionVariables();
	}

	/**
	 * @return function variables in list
	 */
	public MyList getFunctionVariableList() {
		MyList ml = new MyList(kernel);
		for (FunctionVariable fv : fun.getFunctionVariables()) {
			ml.addListElement(fv);
		}
		return ml;
	}

	@Override
	public void clearCasEvalMap() {
		if (fun != null) {
			fun.clearCasEvalMap();
		}
	}

	@Override
	public String getFormulaString(StringTemplate tpl,
			boolean substituteNumbers) {

		String ret = "";
		if (isIndependent()) {
			ret = toValueString(tpl);
		} else {

			if (fun == null) {
				ret = "?";
			} else {
				ret = substituteNumbers ? fun.toValueString(tpl)
						: fun.toString(tpl);
			}
		}

		if ("".equals(ret)) {
			ret = toOutputValueString(tpl);
		}

		if (shortLHS != null && tpl.allowShortLhs()) {
			return shortLHS + " = " + ret;
		}

		return ret;
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	private boolean setNormalFromNeighbours(Coords3 p, double u, double v,
			Coords3 n) {

		evaluatePoint(u + SurfaceEvaluable.NUMERICAL_DELTA, v, p1);
		if (!p1.isDefined()) {
			return false;
		}
		evaluatePoint(u, v + SurfaceEvaluable.NUMERICAL_DELTA, p2);
		if (!p2.isDefined()) {
			return false;
		}

		der1.setZ((p1.z - p.getZd()) / SurfaceEvaluable.NUMERICAL_DELTA);
		der2.setZ((p2.z - p.getZd()) / SurfaceEvaluable.NUMERICAL_DELTA);

		normal.setCrossProduct3(der1, der2);
		n.setNormalizedIfPossible(normal);

		return true;
	}

	@Override
	public boolean evaluateNormal(Coords3 p, double u, double v, Coords3 n) {
		double val = evaluateNormal(0, u, v);
		if (Double.isNaN(val)) {
			return setNormalFromNeighbours(p, u, v, n);
		}
		der1.setZ(val);

		val = evaluateNormal(1, u, v);
		if (Double.isNaN(val)) {
			return setNormalFromNeighbours(p, u, v, n);
		}
		der2.setZ(val);

		normal.setCrossProduct3(der1, der2);
		n.setNormalizedIfPossible(normal);

		return true;

	}

	/**
	 * evaluate normal in (x, y) coords (for 2 var function)
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param n
	 *            normal vector
	 */
	public void evaluateNormal(double x, double y, Coords n) {
		double val = evaluateNormal(0, x, y);
		der1.setZ(val);

		val = evaluateNormal(1, x, y);
		der2.setZ(val);

		n.setCrossProduct4(der1, der2);
		n.normalize();

	}

	private double evaluateNormal(int index, double u, double v) {
		if (fun1 == null) {
			return Double.NaN;
		}
		return fun1[index].evaluate(u, v);
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced, boolean setAuxiliaryProperty) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced,
				setAuxiliaryProperty);

		if (hasLevelOfDetail() && geo.hasLevelOfDetail()) {
			levelOfDetail = ((SurfaceEvaluable) geo).getLevelOfDetail();
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.FUNCTION;
	}

	@Override
	public boolean showLineProperties() {
		if (super.showLineProperties()) {
			return true;
		}

		return isInequalityOrFun2Var();

	}

	@Override
	public void printCASEvalMapXML(StringBuilder sb) {
		fun.printCASevalMapXML(sb);
	}

	@Override
	public void updateCASEvalMap(TreeMap<String, String> map) {
		fun.updateCASEvalMap(map);
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (GeoFunction.hideDefinitionInAlgebra(getFunctionExpression())) {
			return DescriptionMode.VALUE;
		}

		return super.getDescriptionMode();
	}

	@Override
	public boolean isMoveable() {
		return false;
	}

	@Override
	public String getShortLHS() {
		return this.shortLHS;
	}

	/**
	 * @param shortLHS
	 *            whether lhs should be just f: y= instead of f(x)=
	 */
	@Override
	public void setShortLHS(String shortLHS) {
		this.shortLHS = shortLHS;
	}

	@Override
	public void setSecret(AlgoElement algo) {
		if (fun != null) {
			fun.setSecret(algo);
		}
	}

	@Override
	public AutoColor getAutoColorScheme() {
		if (isFun2Var()) {
			return AutoColor.SURFACES;
		}
		return super.getAutoColorScheme();
	}

	@Override
	public boolean isForceInequality() {
		return fun != null && fun.isForceInequality();
	}

	@Override
	public void setForceInequality(boolean forceInequality) {
		if (fun != null) {
			fun.setForceInequality(forceInequality);
		}
	}
}
