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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoDistancePointObject;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.AlgoMacroInterface;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.properties.TableProperties;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.kernel.roots.RealRootUtil;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Explicit function in one variable ("x"). This is actually a wrapper class for
 * Function in geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode)
 * it evaluates to a Function.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunction extends GeoElement implements VarString, Translateable,
		GeoEvaluatable, FunctionalNVar, GeoFunctionable, Region,
		CasEvaluableFunction, ParametricCurve, Dilateable,
		Transformable, InequalityProperties, SurfaceEvaluable, GeoLocusable,
		Lineable2D {

	/** inner function representation */
	protected Function fun;
	/** true if this function should be considered defined */
	protected boolean isDefined = true;
	private boolean trace;

	// if the function includes a division by var, e.g. 1/x, 1/(2+x)
	private boolean includesDivisionByVar = false;
	private boolean includesNonContinuousIntegral = false;
	private boolean includesFreehandOrDataFunction = false;

	/** function may be limited to interval [a, b] */
	protected boolean interval = false;
	/** lower interval bound */
	protected double intervalMin;
	/** upper interval bound */
	protected double intervalMax;
	private boolean evalSwapped;
	// parent conditional function

	private Boolean isInequality = null;
	private String shortLHS;
	/** implicit poly for composite function */
	GeoImplicit iPoly;
	/** substitute functions for composite function */
	GeoFunction[] substituteFunctions;
	private GeoFunction derivGeoFun;
	private TreeSet<SurfaceEvaluable> surfaceEvaluables;
	private Function includesFreehandOrDataFun = null;
	private Function includesNonContinuousIntegralFun = null;
	private Function includesDivisionByVarFun = null;
	/** StringBuilder for temporary string manipulation */
	protected StringBuilder sbToString = new StringBuilder(80);

	private boolean showOnAxis;

	private double[] bounds;

	private static StringBuilder sbCasCommand;

	// Save a strong reference to the algo that created this
	// function (needed for iOS)
	private AlgoDependentFunction dependentFunction;
	private int tableViewColumn = -1;
	private boolean pointsVisible = true;

	/**
	 * Creates new function
	 * 
	 * @param c
	 *            construction
	 */
	public GeoFunction(Construction c) {
		this(c, true);
	}

	/**
	 * @param c
	 *            construction
	 * @param setDefaults
	 *            true to set defaults
	 */
	public GeoFunction(Construction c, boolean setDefaults) {
		super(c);

		if (setDefaults) {
			setConstructionDefaults(); // init visual settings
		}
		surfaceEvaluables = new TreeSet<>();
	}

	/**
	 * Creates new function
	 * 
	 * @param c
	 *            construction
	 * @param f
	 *            function
	 */
	public GeoFunction(Construction c, Function f) {
		this(c, f, true);
	}

	/**
	 * @param c
	 *            construction
	 * @param f
	 *            function
	 * @param simplifyInt
	 *            whether integer subexperessions should be simplified
	 */
	public GeoFunction(Construction c, Function f, boolean simplifyInt) {
		super(c);
		surfaceEvaluables = new TreeSet<>();
		fun = f;
		// setConstructionDefaults is called from initFunction
		initFunction(simplifyInt);
	}

	// Currently, the composite function is only for internal use
	// The expression is not correct but it is not to be shown anyway.
	/**
	 * Creates composite function iPoly(f(x), g(x))
	 * 
	 * @param c
	 *            construction
	 * @param iPoly
	 *            polynomial
	 * @param f
	 *            function for x
	 * @param g
	 *            function for y
	 */
	public GeoFunction(Construction c, GeoImplicit iPoly, GeoFunction f,
					   GeoFunction g) {
		this(c);
		this.iPoly = iPoly;

		substituteFunctions = new GeoFunction[2];
		substituteFunctions[0] = f;
		substituteFunctions[1] = g;

		if (f == null && g != null) {
			setInterval(g.intervalMin, g.intervalMax);

			fun = new Function(c.getKernel()) {
				@Override
				public double value(double x) {
					return GeoFunction.this.iPoly.evaluateImplicitCurve(x,
							substituteFunctions[1].getFunction().value(x));
				}
			};

			/*
			 * Iterator it = iPoly.poly.getVariables().iterator();
			 * ExpressionNode iPolyEN =
			 * (ExpressionNode)iPoly.poly.deepCopy(kernel); ExpressionNode gEN =
			 * (ExpressionNode)g.getFunctionExpression().deepCopy(kernel);
			 * ExpressionValue varX = null; ExpressionValue varY = null;
			 * ExpressionValue vargX = null; vargX =
			 * g.getFunction().getFunctionVariable(); if (it.hasNext()) varX =
			 * (ExpressionValue)it.next(); if (it.hasNext()) varY =
			 * (ExpressionValue)it.next();
			 * 
			 * if (vargX!= null && varX !=null && varY !=null) {
			 * 
			 * ExpressionNode dummyX = new ExpressionNode(); gEN =
			 * gEN.replaceAndWrap(g.getFunction().getFunctionVariable(),
			 * dummyX); iPolyEN = iPolyEN.replaceAndWrap(varY, gEN); gEN =
			 * gEN.replaceAndWrap(dummyX, vargX);
			 * 
			 * }
			 */

			// }

			// g.getFunction().getFunctionVariable();
			// TODO: set the correct expression
			fun.setExpression(new ExpressionNode(kernel, new GeoNumeric(c, 0)));

		} else if (f != null && g == null) {
			setInterval(f.intervalMin, f.intervalMax);

			fun = new Function(c.getKernel()) {
				@Override
				public double value(double x) {
					return GeoFunction.this.iPoly.evaluateImplicitCurve(
							substituteFunctions[0].getFunction().value(x),
							x);
				}
			};
			// TODO: set the correct expression
			fun.setExpression(new ExpressionNode(kernel, new GeoNumeric(c, 0)));

		} else if (f != null && g != null) {

			setInterval(Math.max(f.intervalMin, g.intervalMin),
					Math.min(f.intervalMax, g.intervalMax));

			fun = new Function(c.getKernel()) {
				@Override
				public double value(double x) {
					return GeoFunction.this.iPoly.evaluateImplicitCurve(
							substituteFunctions[0].getFunction().value(x),
							substituteFunctions[1].getFunction().value(x));
				}
			};

			// TODO: set the correct expression
			fun.setExpression(new ExpressionNode(kernel, new GeoNumeric(c, 0)));

		} // else: error
	}

	/**
	 * @param autoLabel
	 *            whether label was set by
	 * @return whether function contains only valid variables
	 */
	public boolean validate(boolean autoLabel) {
		return validate(autoLabel, cons.isSuppressLabelsActive());
	}

	/**
	 * @param autoLabel
	 *            whether label was set by
	 * @param suppressLabel
	 *            whether labels are suppressed (parsing command argument)
	 * @return whether function contains only valid variables
	 */
	public boolean validate(boolean autoLabel, boolean suppressLabel) {
		if (!cons.isFileLoading() && fun != null) {
			if (getFunctionExpression().containsFreeFunctionVariableOtherThan(
					getFunctionVariables())) {
				return false;
			}
		}
		// If labels are suppressed (processing command arguments) accept y and
		// z as
		// functions
		if (suppressLabel || isBooleanFunction() || isForceInequality()) {
			return true;
		}
		if ((this.isFunctionOfY()
						// needed for GGB-1028
						&& this.getCorrespondingCasCell() == null)
				|| (autoLabel && this.isFunctionOfZ())) {
			return false;
		}
		return true;
	}

	@Override
	public void setVisualStyle(GeoElement g, boolean setAuxiliaryProperty) {
		super.setVisualStyle(g, setAuxiliaryProperty);
		if (g instanceof GeoFunction) {
			setShowOnAxis(((GeoFunction) g).showOnAxis);
		}
	}

	@Override
	public String getTypeString() {
		return ((isInequality != null && isInequality) || isForceInequality()) ? "Inequality"
				: "Function";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.FUNCTION;
	}

	/**
	 * copy constructor
	 * 
	 * @param f
	 *            Function to be copied
	 */
	public GeoFunction(GeoFunction f) {
		this(f.cons);
		set(f);
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param en
	 *            expression
	 * @param fv
	 *            variable
	 */
	public GeoFunction(Kernel kernel, ExpressionNode en,
			FunctionVariable fv) {
		this(kernel.getConstruction(), new Function(en, fv));
	}

	@Override
	public GeoFunction copy() {
		return new GeoFunction(this);
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoFunctionable) {
			Function geoFun = ((GeoFunctionable) geo).getFunction();
			if (geoFun == null) {
				fun = null;
				isDefined = false;
				return;
			}
			if (geo.isGeoNumeric() && fun != null) {
				geoFun = new Function(geoFun.getExpression(),
						fun.getFunctionVariable());
			}
			isDefined = geo.isDefined();
			setFunction(new Function(geoFun, kernel));

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
			if (geo instanceof GeoFunction) {
				setForceInequality(((GeoFunction) geo).isForceInequality());
			}
			isInequality = null;
		} else {
			setUndefined();
		}
	}

	/**
	 * Sets the inner function
	 * 
	 * @param f
	 *            function
	 */
	public void setFunction(Function f) {
		if (fun != null && f != null && fun.isForceInequality()) {
			f.setForceInequality(true);
		}
		fun = f;
		// reset this for garbage collection, also the flag needs update for
		// #5054
		this.includesNonContinuousIntegralFun = null;
		this.includesFreehandOrDataFun = null;
		for (SurfaceEvaluable geo : surfaceEvaluables) {
			geo.resetDerivatives();
		}
	}

	@Override
	public boolean addToUpdateSets(final AlgoElement algorithm) {
		final boolean added = super.addToUpdateSets(algorithm);

		if (added) {
			// store surfaces to reset derivatives if needed
			for (int i = 0; i < algorithm.getOutputLength(); i++) {
				GeoElement geo = algorithm.getOutput(i);
				if (geo instanceof SurfaceEvaluable) {
					surfaceEvaluables.add((SurfaceEvaluable) geo);
				}
			}
		}

		return added;
	}

	@SuppressWarnings("cast")
	@Override
	public boolean removeFromUpdateSets(final AlgoElement algorithm) {
		final boolean removed = super.removeFromUpdateSets(algorithm);

		if (removed) {
			// store surfaces to reset derivatives if needed
			for (int i = 0; i < algorithm.getOutputLength(); i++) {
				GeoElement geo = algorithm.getOutput(i);
				if (geo instanceof SurfaceEvaluable) {

					// the cast here is needed for FindBugs
					// leave as separate statement so it's not automatically removed
					SurfaceEvaluable surface = (SurfaceEvaluable) geo;

					surfaceEvaluables.remove(surface);
				}
			}
		}

		return removed;
	}

	/**
	 * initializes function type; if boolean, uses default style for inequalities
	 * 
	 * @param simplifyInt whether integer sub-expressions should be simplified
	 */
	public void initFunction(boolean simplifyInt) {
		fun.initFunction(simplifyInt);
		// apply inequality style when suitable
		setConstructionDefaults();
	}

	@Override
	public Function getFunction() {
		return fun;
	}

	/**
	 * Sets interval for the function
	 * 
	 * @param a
	 *            lower bound
	 * @param b
	 *            upper bound
	 * @return true if the resulting interval is non-empty
	 */
	final public boolean setInterval(double a, double b) {
		if (a <= b) {
			interval = true;
			this.intervalMin = a;
			this.intervalMax = b;
		} else {
			interval = false;
		}

		return interval;
	}

	/**
	 * Returns function expression
	 * 
	 * @return function expression
	 */
	@Override
	final public ExpressionNode getFunctionExpression() {
		if (getFunction() == null) {
			return null;
		}
		return getFunction().getExpression();
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
	 * Set this function to the n-th derivative of f
	 * 
	 * @param fd
	 *            function to be differenced
	 * @param n
	 *            order of derivative
	 * @param fast
	 *            true -> non-CAS derivative
	 */
	public void setDerivative(CasEvaluableFunction fd, int n, boolean fast) {
		GeoFunction f = (GeoFunction) fd;

		if (f.isDefined()) {
			fun = f.fun.getDerivative(n, fast);

			checkDefined();

		} else {
			isDefined = false;
		}
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
		GeoFunction ff = (GeoFunction) f;

		if (ff.isDefined()) {
			setFunction((Function) ff.fun.evalCasCommand(ggbCasCmd, symbolic,
					arbconst));

			checkDefined();

		} else {
			isDefined = false;
		}
	}

	/**
	 * Returns this function's value at position x.
	 * 
	 * @param x
	 *            point for evaluation
	 * @return f(x)
	 */
	@Override
	public double value(double x) {
		if (fun == null || !isDefined) {
			return Double.NaN;
		}

		return fun.value(x);
	}

	/**
	 * Returns this function's value at position x.
	 * 
	 * @param vals
	 *            array of length 1 containing x
	 * @return f(val[0]) or f(val[1])
	 */
	@Override
	public double evaluate(double[] vals) {
		return value(vals[0]);
	}

	/**
	 * If restricted to interval, returns its minimum
	 * 
	 * @return interval minimum
	 */
	public final double getIntervalMin() {
		return intervalMin;
	}

	/**
	 * If restricted to interval, returns its maximum
	 * 
	 * @return interval maximum
	 */
	public final double getIntervalMax() {
		return intervalMax;
	}

	/**
	 * Iff restricted to interval, returns true
	 * 
	 * @return true iff restricted to interval
	 */
	public final boolean hasInterval() {
		return interval;
	}

	/**
	 * Returns this boolean function's value at position x.
	 * 
	 * @param x
	 *            point for evaluation
	 * @return f(x)
	 */
	final public boolean evaluateBoolean(double x) {
		if (fun == null || !isDefined) {
			return false;
		}

		return fun.evaluateBoolean(x);
	}

	@Override
	public GeoFunction getGeoDerivative(int order, boolean fast) {
		if (derivGeoFun == null) {
			derivGeoFun = new GeoFunction(cons);
		}

		// if (getParentAlgorithm() instanceof AlgoFunctionFreehand)
		// derivGeoFun.setUndefined();
		// else
		derivGeoFun.setDerivative(this, order, fast);
		if (!kernel.getApplication().getSettings().getCasSettings()
				.isEnabled()) {
			derivGeoFun.setSecret(
					new AlgoDerivative(cons, this, true, new EvalInfo(false)));
		}
		return derivGeoFun;
	}

	/**
	 * translate function by vector v
	 */
	@Override
	final public void translate(Coords v) {
		translate(v.getX(), v.getY());
	}

	@Override
	final public boolean isTranslateable() {
		return fun != null && !isBooleanFunction();
	}

	/**
	 * Shifts the function by vx to right and by vy up
	 * 
	 * @param vx
	 *            horizontal shift
	 * @param vy
	 *            vertical shift
	 */
	public void translate(double vx, double vy) {
		if (getParentAlgorithm() instanceof AlgoFunctionFreehand) {
			AlgoFunctionFreehand algo = (AlgoFunctionFreehand) getParentAlgorithm();
			GeoList list = algo.getList();

			// left/right boundaries
			((GeoNumeric) list.get(0))
					.setValue(((GeoNumeric) list.get(0)).getDouble() + vx);
			((GeoNumeric) list.get(1))
					.setValue(((GeoNumeric) list.get(1)).getDouble() + vx);

			// heights
			for (int i = 2; i < list.size(); i++) {
				((GeoNumeric) list.get(i))
						.setValue(((GeoNumeric) list.get(i)).getDouble() + vy);

			}

			algo.compute();

		} else {
			fun.translate(vx, vy);
		}
	}

	/**
	 * Returns true if this function is a polynomial.
	 * 
	 * @return true if this function is a polynomial.
	 * @param forRootFinding
	 *            set to true if you want to allow functions that can be
	 *            factored into polynomial factors for root finding (e.g.
	 *            sqrt(x) could be replaced by x)
	 * @param symbolic
	 *            function's symbolic expression must be a polynomial, e.g. x^2
	 *            is ok but not x^a
	 */
	public boolean isPolynomialFunction(boolean forRootFinding,
			boolean symbolic) {
		// don't do root finding simplification here
		// i.e. don't replace a factor "sqrt(x)" by "x"
		if (!isDefined()) {
			return false;
		}
		return fun.isPolynomialFunction(forRootFinding, symbolic);
	}

	/**
	 * Returns true if this function is a polynomial.
	 * 
	 * @return true if this function is a polynomial.
	 * @param forRootFinding
	 *            set to true if you want to allow functions that can be
	 *            factored into polynomial factors for root finding (e.g.
	 *            sqrt(x) could be replaced by x)
	 */
	@Override
	public boolean isPolynomialFunction(boolean forRootFinding) {
		return isPolynomialFunction(forRootFinding, false);
	}

	/**
	 * Returns whether this function includes a division by variable, e.g. f(x)
	 * = 1/x, 1/(2+x), sin(3/x), ...
	 * 
	 * @return true iff this function includes a division by variable
	 */
	final public boolean includesDivisionByVar() {
		if (includesDivisionByVarFun != fun) {
			includesDivisionByVarFun = fun;
			includesDivisionByVar = fun != null
					&& fun.includesDivisionByVariable();
		}
		return includesDivisionByVar;
	}

	/**
	 * Returns whether this function includes eg abs(), If[] function
	 * 
	 * @return true iff this function includes abs(), If[] etc
	 */
	final public boolean includesFreehandOrData() {
		if (includesFreehandOrDataFun != fun) {
			includesFreehandOrDataFun = fun;
			includesFreehandOrDataFunction = fun != null
					&& fun.includesFreehandOrDataFunction();
		}
		return includesFreehandOrDataFunction;
	}

	/**
	 * Returns whether this function includes eg Freehand, DataFunction
	 * functions
	 * 
	 * @return true iff this function includes abs(), If[] etc
	 */
	final public boolean includesNonContinuousIntegral() {
		if (includesNonContinuousIntegralFun != fun) {
			includesNonContinuousIntegralFun = fun;
			includesNonContinuousIntegral = fun != null
					&& fun.includesNonContinuousIntegral();
		}
		return includesNonContinuousIntegral;
	}

	@Override
	public boolean isDefined() {
		return isDefined && fun != null && isFunctionDefined(fun);
	}

	@Override
	public boolean isFillable() {
		if (fun != null && isInequality == null && isBooleanFunction()) {
			getIneqs();
		}
		return isInequality != null && isInequality;
	}

	@Override
	public boolean isInverseFillable() {
		return isFillable();
	}

	/**
	 * Changes the defined state
	 * 
	 * @param defined
	 *            true iff the function should be considered defined
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

	/**
	 * @return function description as f(x)=... for real and e.g. f:x>4 for bool
	 */
	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		if (isLabelSet()) {
			initStringBuilder(sbToString, tpl, label, this);
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	/**
	 * @param stringBuilder
	 *            string builder
	 * @param tpl
	 *            string template
	 * @param label
	 *            geo label
	 * @param fn
	 *            function; to determine what kind of LHS we want
	 */
	public static void initStringBuilder(StringBuilder stringBuilder,
			StringTemplate tpl, String label,
			FunctionalNVar fn) {
		stringBuilder.append(label);
		if (fn.getShortLHS() != null) {
			stringBuilder.append(": ");
			stringBuilder.append(fn.getShortLHS());
			stringBuilder.append(tpl.getEqualsWithSpace());
		} else if ((fn.isBooleanFunction() || fn.isForceInequality())
				&& !tpl.hasType(StringType.GEOGEBRA_XML)) {
			stringBuilder.append(": ");
		} else {
			String var = fn.getVarString(tpl);
			tpl.appendWithBrackets(stringBuilder, var);
			stringBuilder.append(tpl.getEqualsWithSpace());
		}
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (fun != null && isDefined()) {
			return fun.toValueString(tpl);
		}
		return "?";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see geogebra.kernel.GeoElement#toOutputValueString() needed for eg
	 * KeepIf[x!="h",{"h","k","o"}]
	 */
	@Override
	public String toOutputValueString(StringTemplate tpl) {
		if (isLocalVariable()) {
			return label;
		}
		if (fun != null && isDefined()) {
			return fun.toOutputValueString(tpl);
		}
		return "?";
	}

	@Override
	public String toSymbolicString(StringTemplate tpl) {
		if (fun != null && isDefined()) {
			return fun.toString(tpl);
		}
		return "?";
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		// make sure Freehand Functions have different entries in drop-down
		// lists
		if (isFreehandFunction() && isLabelSet()) {
			return getAssignmentLHS(tpl);
		}
		if (fun != null && isDefined()) {
			return fun.toLaTeXString(symbolic, tpl);
		}
		return "?";
	}

	/**
	 * save object in xml format
	 */
	@Override
	public void getXML(boolean getListenersToo, StringBuilder sbxml) {
		// an independent function needs to add
		// its expression itself
		// e.g. f(x) = x^2 - 3x
		if (isIndependent() && getDefaultGeoType() < 0) {
			sbxml.append("<expression");
			sbxml.append(" label=\"");
			sbxml.append(label);
			sbxml.append("\" exp=\"");
			StringUtil.encodeXML(sbxml, toString(StringTemplate.xmlTemplate));
			sbxml.append("\" type=\"");
			sbxml.append(getFunctionType());
			sbxml.append("\"/>\n");
		}

		getElementOpenTagXML(sbxml);
		getXMLtags(sbxml);
		getCaptionXML(sbxml);
		printCASEvalMapXML(sbxml);
		if (getListenersToo) {
			getListenerTagsXML(sbxml);
		}
		sbxml.append("</element>\n");
	}

	/**
	 * function type
	 * @return type of function (inequality or function)
	 */
	public String getFunctionType() {
		return isForceInequality() ? "inequality"
				: "function";
	}

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sbxml) {
		super.getXMLtags(sbxml);

		// line thickness and type
		getLineStyleXML(sbxml);
		if (showOnAxis()) {
			sbxml.append("<showOnAxis val=\"true\" />");
		}
	}

	/**
	 * 
	 * assumes function in form If[ interval, simple function]
	 * 
	 * @param bounds0
	 *            contains {min, max} on exit
	 */
	public void getInterval(double[] bounds0) {
		bounds0[0] = Double.NEGATIVE_INFINITY;
		bounds0[1] = Double.POSITIVE_INFINITY;

		double bound;
		ExpressionNode inequalityEn = (ExpressionNode) getFunctionExpression()
				.getLeft();
		Operation op = inequalityEn.getOperation();

		switch (op) {
		default:
			Log.error("problem in GeoFunction.getInterval()");
			return;

		case AND_INTERVAL:
			GeoIntervalUtil.updateBoundaries(inequalityEn, bounds0);
			break;

		case LESS:
		case LESS_EQUAL:
		case GREATER:
		case GREATER_EQUAL:

			// make sure 2<x and x>2 both work
			if (inequalityEn.getLeft() instanceof FunctionVariable) {
				bound = inequalityEn.getRight().evaluateDouble();
			} else if (inequalityEn.getRight() instanceof FunctionVariable) {
				bound = inequalityEn.getLeft().evaluateDouble();
				op = op.reverseLeftToRight();
			} else {
				// shouldn't happen
				bound = Double.NaN;
			}

			switch (op) {

			case LESS:
			case LESS_EQUAL:
				bounds0[1] = bound;
				break;
			case GREATER:
			case GREATER_EQUAL:
				bounds0[0] = bound;
				break;
			default:
				break;
			}
		}

	}

	/*
	 * Path interface
	 */
	private void pointChanged(Coords P, boolean closestPoly) {
		if (P.getZ() == 1.0) {
			// P.x = P.x;
		} else {
			P.setX(P.getX() / P.getZ());
		}
		if (!isBooleanFunction()) {
			if (interval) {
				// don't let P move out of interval
				if (P.getX() < intervalMin) {
					P.setX(intervalMin);
				} else if (P.getX() > intervalMax) {
					P.setX(intervalMax);
				}
			} else {
				ExpressionNode exp = fun.getExpression();

				// make sure point can't be dragged to undefined region for eg
				// If[3 <= x <= 5, x^2]
				if (exp.getOperation().isIf()) {
					ExpressionValue inequality = exp.getLeft().unwrap();
					if (inequality.isExpressionNode()) {

						double bound;
						double epsilon = 0;
						ExpressionNode inequalityEn = (ExpressionNode) inequality;
						Operation op = inequalityEn.getOperation();

						switch (op) {
						case AND_INTERVAL:
							if (bounds == null) {
								bounds = new double[2];
							}
							GeoIntervalUtil.updateBoundaries(inequalityEn, bounds);

							if (P.getX() < bounds[0]) {
								P.setX(bounds[0]);
							} else if (P.getX() > bounds[1]) {
								P.setX(bounds[1]);
							}

							break;

						case LESS:
						case LESS_EQUAL:
						case GREATER:
						case GREATER_EQUAL:

							// make sure 2<x and x>2 both work
							if (inequalityEn
									.getLeft() instanceof FunctionVariable) {
								bound = inequalityEn.getRight()
										.evaluateDouble();
							} else if (inequalityEn
									.getRight() instanceof FunctionVariable) {
								bound = inequalityEn.getLeft().evaluateDouble();
								op = op.reverseLeftToRight();
							} else {
								// shouldn't happen
								bound = Double.NaN;
							}

							switch (op) {

							case LESS:
								epsilon = Kernel.MIN_PRECISION;
								// fall through
							case LESS_EQUAL:
								if (P.getX() >= bound) {
									P.setX(bound - epsilon);
								}
								break;
							case GREATER:
								epsilon = Kernel.MIN_PRECISION;
								// fall through
							case GREATER_EQUAL:
								if (P.getX() < bound) {
									P.setX(bound + epsilon);
								}
								break;
							default:
								break;
							}
						default:
							break;
						}
					}
				}
			}

			PolyFunction polyFunction = closestPoly
					? fun.expandToPolyFunction(fun.getExpression(), false, true)
					: null;
			if (polyFunction != null) {
				double val = AlgoDistancePointObject.closestValPoly(
						polyFunction, P.getX(), P.getY(), kernel);
				P.setX(val);
				P.setY(value(val));
			} else {
				P.setY(value(P.getX()));
			}

		} else {
			pointChangedBoolean(true, P);
		}
		P.setZ(1.0);
	}

	@Override
	public void pointChanged(GeoPointND P) {
		pointChanged(P, true);
	}

	private void pointChanged(GeoPointND P, boolean closestPoly) {

		Coords coords = P.getCoordsInD2();
		if (!coords.isDefined() && P.isMoveable()) {
			// TRAC-3494
			coords.setX(0);
		}
		pointChanged(coords, closestPoly);

		// set path parameter for compatibility with
		// PathMoverGeneric
		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());

		PathParameter pp = P.getPathParameter();
		pp.t = coords.getX(); // P.getX();

		P.updateCoordsFrom2D(false, null);
	}

	private void pointChangedBoolean(boolean b, Coords P) {
		double px;
		boolean yfun = isFunctionOfY();
		if (yfun) {
			if (b) {
				P.setX(0.0);
			}
			px = P.getY();
		} else {
			if (b) {
				P.setY(0.0);
			}
			px = P.getX();
		}
		double bestDist = Double.MAX_VALUE;
		getIneqs();
		if (!this.evaluateBoolean(px)) {
			IneqTree ineqs = fun.getIneqs();
			int ineqCount = ineqs.getSize();
			for (int i = 0; i < ineqCount; i++) {
				for (GeoPoint point : ineqs.get(i).getZeros()) {
					if (Math.abs(point.getX() - px) < bestDist) {
						bestDist = Math.abs(point.getX() - px);
						if (yfun) {
							P.setY(point.getX());
						} else {
							P.setX(point.getX());
						}
					}
				}
			}
			if (bestDist == Double.MAX_VALUE) {
				P.setUndefined();
			}
		}
	}

	@Override
	public boolean isOnPath(GeoPointND PI, double eps) {
		GeoPoint P = (GeoPoint) PI;

		if (P.getPath() == this) {
			return true;
		}

		if (!isBooleanFunction()) {
			return isDefined && Math
					.abs(fun.value(P.getInhomX()) - P.getInhomY()) <= eps;
		}
		double px = isFunctionOfY() ? P.getY() : P.getX();
		if (P.getZ() != 1.0) {
			px = px / P.getZ();
		}
		return evaluateBoolean(px);

	}

	@Override
	public void pathChanged(GeoPointND PI) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)) {
			pointChanged(PI);
			return;
		}

		GeoPoint P = (GeoPoint) PI;

		PathParameter pp = P.getPathParameter();
		P.setX(pp.t);
		pointChanged(P, false);
	}

	@Override
	public boolean isPath() {
		return true;
	}

	/**
	 * Returns the smallest possible parameter value for this path (may be
	 * Double.NEGATIVE_INFINITY) see TRAC-3
	 * 
	 * @version 2010-05-14
	 * @return smallest possible parameter value (may be
	 *         Double.NEGATIVE_INFINITY)
	 */
	@Override
	public double getMinParameter() {
		if (interval) {
			return Math.max(kernel.getViewsXMin(this), intervalMin);
		}
		return kernel.getViewsXMin(this);
	}

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY) see TRAC-3
	 * 
	 * @version 2010-05-14
	 * @return largest possible parameter value (may be
	 *         Double.POSITIVE_INFINITY)
	 */
	@Override
	public double getMaxParameter() {
		if (interval) {
			return Math.min(kernel.getViewsXMax(this), intervalMax);
		}
		return kernel.getViewsXMax(this);
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public boolean isClosedPath() {
		return false;
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
	public boolean isTraceable() {
		return true;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	// G.Sturr 2010-5-18 get/set spreadsheet trace not needed here
	/*
	 * public void setSpreadsheetTrace(boolean spreadsheetTrace) {
	 * this.spreadsheetTrace = spreadsheetTrace; }
	 * 
	 * public boolean getSpreadsheetTrace() { return spreadsheetTrace; }
	 */

	@Override
	public GeoFunction getGeoFunction() {
		return this;
	}

	@Override
	public boolean isGeoFunction() {
		if (fun != null) {
			return !fun.isBooleanFunction();
		}
		return true;
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

	@Override
	public boolean isRealValuedFunction() {
		return isGeoFunction();
	}

	@Override
	public boolean isBooleanFunction() {
		if (fun != null) {
			return fun.isBooleanFunction();
		}
		return false;
	}

	@Override
	public boolean isAlgebraViewEditable() {
		return !(getParentAlgorithm() instanceof AlgoFunctionFreehand);
	}

	/**
	 * changes variable interpretation: if swapped, the function is considered
	 * to be x=f(y).
	 */
	public void swapEval() {
		evalSwapped = !evalSwapped;
	}

	@Override
	public void evaluateCurve(double t, double[] out) {
		if (evalSwapped) {
			out[1] = t;
			out[0] = value(t);
		} else {
			out[0] = t;
			out[1] = value(t);
		}
	}

	/**
	 * Evaluates curvature for function: k(x) = f''/T^3, T = sqrt(1+(f')^2)
	 * 
	 * @author Victor Franco Espino, Markus Hohenwarter
	 */
	@Override
	public double evaluateCurvature(double x) {
		if (fun == null) {
			return Double.NaN;
		}
		Function f1 = fun.getDerivative(1, true);
		Function f2 = fun.getDerivative(2, true);
		if (f1 == null || f2 == null) {
			return Double.NaN;
		}

		double f1eval = f1.value(x);
		double t = Math.sqrt(1 + f1eval * f1eval);
		double t3 = t * t * t;
		return f2.value(x) / t3;
	}

	@Override
	final public UnivariateFunction getUnivariateFunctionX() {
		return new UnivariateFunction() {
			@Override
			public double value(double t) {
				return t;
			}

		};
	}

	@Override
	final public UnivariateFunction getUnivariateFunctionY() {
		return new UnivariateFunction() {
			@Override
			public double value(double t) {
				return GeoFunction.this.value(t);
			}

		};
	}

	@Override
	public GeoVec2D evaluateCurve(double t) {
		return new GeoVec2D(kernel, t, value(t));
	}

	@Override
	public String getVarString(StringTemplate tpl) {
		if (fun == null) {
			return tpl.printVariableName("x");
		}
		return fun.getVarString(tpl);
	}

	@Override
	final public boolean isFunctionInX() {
		return true;
	}

	// Michael Borcherds 2009-02-15
	@Override
	public boolean isEqual(GeoElementND geo) {
		// support c==f for Line, Function
		if (geo.isGeoLine()) {
			return ((GeoLine) geo).isEqual(this);
		}

		if (!geo.isGeoFunction()) {
			return false;
		}

		GeoFunction geoFun = (GeoFunction) geo;
		// check equality in two points; avoid discontinuities of common functions (1/x, tan(x))
		if (differAt(this, geoFun, 0.31) || differAt(this, geoFun, 10.89)
				|| !isDefined() || !geoFun.isDefined()) {
			return false;
		}
		PolyFunction poly1 = getFunction()
				.expandToPolyFunction(getFunctionExpression(), false, true);
		if (poly1 != null && isDefined()) {
			PolyFunction poly2 = geoFun.getFunction().expandToPolyFunction(
					geoFun.getFunctionExpression(), false, true);

			if (poly2 != null) {
				return geoFun.isDefined() && poly1.isEqual(poly2);
			}
		}

		// if poly1 and/or poly2 are null they /could/ still be equal
		// polynomials (or equal non-polynomials)
		// eg x^2 + 0*sin(x) == x^2
		// so check with CAS (SLOW)
		return isDifferenceZeroInCAS(geo);
	}

	protected static boolean isFunctionDefined(FunctionNVar fun) {
		// function defined as "?"
		ExpressionValue def = fun.getExpression().unwrap();
		return !(def instanceof MyDouble && def.isConstant()
				&& Double.isNaN(def.evaluateDouble()));
	}

	/*
	 * Check that the functions differ at given point; false negatives if values are
	 * too big, too close to each othe or one is undefined.
	 */
	private static boolean differAt(GeoFunction f1, GeoFunction f2, double x) {
		double v1 = f1.value(x);
		double v2 = f2.value(x);
		if (!MyDouble.isFinite(v1) || Math.abs(v1) > 1E8) {
			return false;
		}
		if (!MyDouble.isFinite(v2) || Math.abs(v2) > 1E8) {
			return false;
		}
		return !DoubleUtil.isEqual(v1, v2, Kernel.MIN_PRECISION);
	}

	/**
	 * Sums two functions and stores the result to another
	 * 
	 * @param resultFun
	 *            resulting function
	 * @param fun1
	 *            first addend
	 * @param fun2
	 *            second addend
	 * @param op
	 *            operation
	 * @return resultFun
	 */
	public static GeoFunction add(GeoFunction resultFun, GeoFunction fun1,
			GeoFunctionable fun2, Operation op) {

		Kernel kernel = fun1.getKernel();

		FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
		Function function2 = fun2.getFunction();
		FunctionVariable x2 = function2.getFunctionVariable();
		FunctionVariable x = new FunctionVariable(kernel);

		ExpressionNode left = fun1.getFunctionExpression().getCopy(kernel);
		ExpressionNode right = function2.getFunctionExpression()
				.getCopy(kernel);

		ExpressionNode sum;

		// improvement for
		// Sum(Shuffle({Polynomial(a x^2), Polynomial(b x), Polynomial(c)}))
		// to give "x^2" not "0 + x^2"
		if (left.isConstant()
				&& MyDouble.exactEqual(left.evaluateDouble(), 0)) {
			sum = right.replace(x2, x).wrap();
		} else if (right.isConstant()
				&& MyDouble.exactEqual(right.evaluateDouble(), 0)) {
			sum = left.replace(x1, x).wrap();
		} else {
			sum = new ExpressionNode(fun1.getKernel(), left.replace(x1, x), op,
					right.replace(x2, x));
		}

		Function f = new Function(sum, x);

		resultFun.setFunction(f);
		resultFun.setDefined(true);

		return resultFun;
	}

	/**
	 * Applies an operation on first and second function and returns the result
	 * 
	 * @param op
	 *            operation
	 * @param lt
	 *            left argument of op
	 * @param rt
	 *            right argument of op
	 * @return resulting GeoFunction or GeFunctionNvar
	 */
	public static FunctionNVar operationSymb(Operation op, FunctionalNVar lt,
			FunctionalNVar rt) {
		Kernel kernel = lt.getFunction().getKernel();
		TreeSet<String> varNames = new TreeSet<>();
		for (int i = 0; i < lt.getFunction().getVarNumber(); i++) {
			varNames.add(lt.getFunction().getVarString(i,
					StringTemplate.defaultTemplate));
		}
		for (int i = 0; i < rt.getFunction().getVarNumber(); i++) {
			varNames.add(rt.getFunction().getVarString(i,
					StringTemplate.defaultTemplate));
		}
		HashMap<String, FunctionVariable> varmap = new HashMap<>();
		for (String name : varNames) {
			varmap.put(name, new FunctionVariable(kernel, name));
		}
		ExpressionNode ltExpr = toExpr(lt, varmap, kernel),
				rtExpr = toExpr(rt, varmap, kernel),
				sum = new ExpressionNode(kernel, ltExpr, op, rtExpr);
		FunctionNVar f = fromExpr(sum, varmap, varNames);

		f.initFunction();

		return f;
		// AlgoDependentFunction adf = new
		// AlgoDependentFunction(fun1.getConstruction(),f);
		// return adf.getFunction();

	}

	/**
	 * @param op
	 *            coord op
	 * @param lt
	 *            curve
	 * @return coord function for the curve
	 */
	public static FunctionNVar operationSymb(Operation op,
			GeoCurveCartesianND lt) {
		Kernel kernel = lt.getKernel();
		FunctionVariable fv = new FunctionVariable(kernel, "t");
		ExpressionNode ex = new ExpressionNode(kernel, lt,
				Operation.VEC_FUNCTION,
				fv).apply(op);
		Function f = new Function(ex, fv);
		f.initFunction();
		return f;
	}

	private static FunctionNVar fromExpr(ExpressionNode sum,
			HashMap<String, FunctionVariable> varmap,
			TreeSet<String> varNames) {
		int size = varmap.size();
		if (size > 1) {
			FunctionVariable[] varArray = new FunctionVariable[size];
			int i = 0;
			for (String name : varNames) {
				varArray[i] = varmap.get(name);
				i++;
			}
			FunctionNVar f = new FunctionNVar(sum, varArray);
			return f;

			// AlgoDependentFunctionNVar adf = new
			// AlgoDependentFunctionNVar(fun1.getConstruction(),null,f);
			// return adf.getFunction();
		}
		Iterator<FunctionVariable> var = varmap.values().iterator();
		return new Function(sum, var.next());
	}

	private static ExpressionNode toExpr(Evaluate2Var lt,
			HashMap<String, FunctionVariable> varMap, Kernel kernel) {
		if (lt instanceof GeoFunction) {
			return new ExpressionNode(kernel, lt, Operation.FUNCTION, varMap
					.get(lt.getVarString(StringTemplate.defaultTemplate)));
		}
		if (lt instanceof GeoFunctionNVar) {
			MyList varList = new MyList(kernel);
			FunctionNVar function = lt.getFunction();
			if (function != null) {
				for (int i = 0; i < function.getVarNumber(); i++) {
					varList.addListElement(varMap.get(function
							.getVarString(i, StringTemplate.defaultTemplate)));
				}
			}
			return new ExpressionNode(kernel, lt, Operation.FUNCTION_NVAR,
					varList);
		}
		if (lt instanceof GeoNumeric) {
			return lt.wrap();
		}
		if (lt instanceof FunctionNVar) {
			ExpressionNode ret = ((FunctionNVar) lt).getExpression();
			for (int i = 0; i < ((FunctionNVar) lt).getVarNumber(); i++) {
				ret = ret
						.replace(((FunctionNVar) lt).getFunctionVariables()[i],
								varMap.get(((FunctionNVar) lt).getVarString(i,
										StringTemplate.defaultTemplate)))
						.wrap();
			}
			return ret;
		}
		return null;
	}

	/**
	 * Applies an operation on this function and number value
	 * 
	 * @param op
	 *            operation
	 * @param fun1
	 *            function on which we want to apply this op
	 * @param ev
	 *            value to apply
	 * @param right
	 *            f op nv for true, nv op f for false
	 * @return resulting function
	 */
	public static FunctionNVar applyNumberSymb(Operation op, Evaluate2Var fun1,
			ExpressionValue ev, boolean right) {
		ExpressionValue nv = ev;

		if (fun1.getFunction() == null) {
			return null;
		}

		Kernel kernel = fun1.getFunction().getKernel();
		TreeSet<String> varNames = new TreeSet<>();
		for (int i = 0; i < fun1.getFunction().getVarNumber(); i++) {
			varNames.add(fun1.getFunction().getVarString(i,
					StringTemplate.defaultTemplate));
		}
		HashMap<String, FunctionVariable> varmap = new HashMap<>();
		FunctionVariable fv1 = null;
		for (String name : varNames) {
			varmap.put(name, fv1 = new FunctionVariable(kernel, name));
		}
		ExpressionNode sum, myExpr;
		myExpr = toExpr(fun1, varmap, kernel);

		if (nv instanceof ExpressionNode) {
			for (String name : varNames) {
				((ExpressionNode) nv).replaceVariables(name, varmap.get(name));
			}
		} else if (nv instanceof FunctionVariable) {
			String nvName = ((FunctionVariable) nv)
					.toString(StringTemplate.defaultTemplate);
			nv = varmap.get(nvName);
			if (nv == null) {
				nv = ev;
				if (!"y".equals(nvName)) {
					myExpr = myExpr.replace(fv1, nv).wrap();
					varNames.clear();
					varNames.add(nvName);
					varmap.clear();
					varmap.put(nvName, (FunctionVariable) nv);
				}
			}
		}

		if (right) {
			sum = new ExpressionNode(kernel, myExpr, op, nv);
		} else {
			sum = new ExpressionNode(kernel, nv, op, myExpr);
		}
		FunctionNVar f = fromExpr(sum, varmap, varNames);
		f.initFunction();
		return f;
	}

	/**
	 * Subtracts two functions and stores the result to another
	 * 
	 * @param resultFun
	 *            resulting function
	 * @param fun1
	 *            minuend
	 * @param fun2
	 *            subtrahend
	 * @return resultFun
	 */
	public static GeoFunction subtract(GeoFunction resultFun,
			GeoFunctionable fun1, GeoFunctionable fun2) {

		Kernel kernel = fun1.getKernel();

		FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
		FunctionVariable x2 = fun2.getFunction().getFunctionVariable();
		FunctionVariable x = new FunctionVariable(kernel);

		ExpressionNode left = fun1.getFunction().getFunctionExpression()
				.getCopy(kernel);
		ExpressionNode right = fun2.getFunction().getFunctionExpression()
				.getCopy(kernel);

		ExpressionNode sum = new ExpressionNode(fun1.getKernel(),
				left.replace(x1, x), Operation.MINUS, right.replace(x2, x));

		Function f = new Function(sum, x);

		resultFun.setFunction(f);
		resultFun.setDefined(true);

		return resultFun;
	}

	/**
	 * Multiplication of number and function. Needed in Fit[&lt;List of
	 * Points&gt; ,&lt;List of Functions&gt;] to make the result a linear
	 * combination of existing functions; fit(x)=a*f(x)+b*g(x)+c*h(x)+..
	 * 
	 * @author Hans-Petter Ulven
	 * @version 2010-02-22
	 * @param resultFun
	 *            Resulting function
	 * @param number
	 *            number
	 * @param fun
	 *            function
	 * @return resultFun
	 * 
	 */
	public static GeoFunction mult(GeoFunction resultFun, double number,
			GeoFunctionable fun) {

		Kernel kernel = fun.getKernel();
		MyDouble num = new MyDouble(kernel, number);

		FunctionVariable xold = fun.getFunction().getFunctionVariable();
		FunctionVariable x = new FunctionVariable(kernel);

		ExpressionNode left = new ExpressionNode(kernel, num);
		ExpressionNode right = fun.getFunction().getFunctionExpression()
				.getCopy(kernel);

		ExpressionNode product = new ExpressionNode(kernel, left,
				Operation.MULTIPLY, right.replace(xold, x).unwrap());

		Function f = new Function(product, x);

		resultFun.setFunction(f);
		resultFun.setDefined(true);

		return resultFun;
	}

	/**
	 * Returns true iff x is in the interval over-ridden in
	 * GeoFunctionConditional
	 * 
	 * @param x
	 *            point for evaluation
	 * @return true iff x is in the interval
	 */
	public boolean evaluateCondition(double x) {
		if (!interval) {
			return true;
		}
		return x > intervalMin && x < intervalMax;
	}

	/**
	 * Returns the limit
	 * 
	 * @param x
	 *            point to evaluate the limit
	 * @param direction
	 *            1 for limit above, -1 for limit below, standard limit
	 *            otherwise
	 * @return the limit
	 */
	public String getLimit(double x, int direction) {
		// get function and function variable string using temp variable
		// prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		String[] funVarStr = getTempVarCASString(false);

		if (sbCasCommand == null) {
			sbCasCommand = new StringBuilder();
		} else {
			sbCasCommand.setLength(0);
		}
		sbCasCommand.setLength(0);
		sbCasCommand.append("Numeric(Limit");
		if (direction == -1) {
			sbCasCommand.append("Above");
		} else if (direction == 1) {
			sbCasCommand.append("Below");
		}
		sbCasCommand.append('(');
		sbCasCommand.append(funVarStr[0]); // function expression
		sbCasCommand.append(',');
		sbCasCommand.append(funVarStr[1]); // function variable
		sbCasCommand.append(',');
		sbCasCommand.append(MyDouble.toString(x));
		sbCasCommand.append("),");
		// increase precision to improve problems like TRAC-2778
		sbCasCommand.append("50)");
		return sbCasCommand.toString();
	}

	/**
	 * Adds vertical asymptotes to the StringBuilder over-ridden in
	 * GeoFunctionConditional
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param verticalSB
	 *            StringBuilder for the result
	 * @param reverse
	 *            if true, we reverse the parent conditional function condition
	 */
	public void getVerticalAsymptotes(GeoFunction f, StringBuilder verticalSB,
			boolean reverse) {
		getVerticalAsymptotesStatic(this, f, verticalSB, reverse);
	}

	/**
	 * Adds horizontal positive asymptotes to the StringBuilder over-ridden in
	 * GeoFunctionConditional
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param SB
	 *            StringBuilder for the result
	 * @return whether asymptote was found
	 */
	public boolean getHorizontalPositiveAsymptote(GeoFunction f,
			StringBuilder SB) {
		return getHorizontalAsymptoteStatic(this, f, SB, true);
	}

	/**
	 * Adds horizontal negative asymptotes to the StringBuilder over-ridden in
	 * GeoFunctionConditional
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param SB
	 *            StringBuilder for the result
	 * @return whether asymptote was found
	 */
	public boolean getHorizontalNegativeAsymptote(GeoFunction f,
			StringBuilder SB) {
		return getHorizontalAsymptoteStatic(this, f, SB, false);
	}

	/**
	 * Adds diagonal positive asymptotes to the StringBuilder over-ridden in
	 * GeoFunctionConditional
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param SB
	 *            StringBuilder for the result
	 */

	public void getDiagonalPositiveAsymptote(GeoFunction f, StringBuilder SB) {
		getDiagonalAsymptoteStatic(this, f, SB, true);
	}

	/**
	 * Adds diagonal negative asymptotes to the StringBuilder over-ridden in
	 * GeoFunctionConditional
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param SB
	 *            StringBuilder for the result
	 */
	public void getDiagonalNegativeAsymptote(GeoFunction f, StringBuilder SB) {
		getDiagonalAsymptoteStatic(this, f, SB, false);
	}

	/**
	 * Adds diagonal asymptotes to the string builder SB
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param parentFunction
	 *            parent function (in case of conditional functions)
	 * @param SB
	 *            StringBuilder for the result
	 * @param positiveInfinity
	 *            if true, we look for limit at positive infinity, for false, we
	 *            use negative infinity
	 */
	protected void getDiagonalAsymptoteStatic(GeoFunction f,
			GeoFunction parentFunction, StringBuilder SB,
			boolean positiveInfinity) {

		try {
			// get first derivative
			GeoFunction deriv = f.getGeoDerivative(1, false);

			// get function and function variable string using temp variable
			// prefixes,
			// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2",
			// "ggbtmpvarx"}
			String[] derivVarStr = deriv.getTempVarCASString(false);
			String[] funVarStr = f.getTempVarCASString(false);

			if (sbCasCommand == null) {
				sbCasCommand = new StringBuilder();
			} else {
				sbCasCommand.setLength(0);
			}

			sbCasCommand.setLength(0);
			sbCasCommand.append("Limit(");
			sbCasCommand.append(derivVarStr[0]); // derivative expression
			sbCasCommand.append(',');
			sbCasCommand.append(derivVarStr[1]); // derivative function variable
			sbCasCommand.append(',');
			if (!positiveInfinity) {
				sbCasCommand.append('-'); // -Infinity
			}
			sbCasCommand.append(Unicode.INFINITY);
			sbCasCommand.append(')');
			String gradientStrMinus = kernel
					.evaluateCachedGeoGebraCAS(
					sbCasCommand.toString(),
					null);
			// Application.debug(sb.toString()+" = "+gradientStrMinus,1);

			double grad;
			try {
				grad = kernel.getAlgebraProcessor()
						.evaluateToDouble(gradientStrMinus, true, null);
			} catch (Exception e) {
				grad = 0;
			}

			if (!GeoFunction.isCASErrorOrInf(gradientStrMinus)
					&& !DoubleUtil.isZero(grad)) {
				sbCasCommand.setLength(0);
				sbCasCommand.append("Limit(");
				sbCasCommand.append(funVarStr[0]); // function expression
				sbCasCommand.append(" - ");
				sbCasCommand.append(gradientStrMinus);
				sbCasCommand.append(" * ");
				sbCasCommand.append(derivVarStr[1]); // derivative function
														// variable
				sbCasCommand.append(',');
				sbCasCommand.append(derivVarStr[1]); // derivative function
														// variable
				sbCasCommand.append(',');
				if (!positiveInfinity) {
					sbCasCommand.append('-'); // -Infinity
				}
				sbCasCommand.append(Unicode.INFINITY);
				sbCasCommand.append(')');

				String interceptStrMinus = kernel
						.evaluateCachedGeoGebraCAS(sbCasCommand.toString(),
								null);
				// Application.debug(sb.toString()+" = "+interceptStrMinus,1);

				if (!GeoFunction.isCASErrorOrInf(interceptStrMinus)) {
					sbCasCommand.setLength(0);
					sbCasCommand.append("y = ");
					sbCasCommand.append(gradientStrMinus);
					sbCasCommand.append(" * x +");
					sbCasCommand.append(interceptStrMinus);

					if (!SB.toString().endsWith(sbCasCommand.toString())) { // not
						// duplicated
						if (SB.length() > 1) {
							SB.append(',');
						}
						SB.append(sbCasCommand);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds horizontal asymptotes to the string builder SB
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param parentFunction
	 *            parent function (in case of conditional functions)
	 * @param SB
	 *            StringBuilder for the result
	 * @param positiveInfinity
	 *            if true, we look for limit at positive infinity, for false, we
	 *            use negative infinity
	 * @return whether asymptote was found
	 */
	protected boolean getHorizontalAsymptoteStatic(GeoFunction f,
			GeoFunction parentFunction, StringBuilder SB,
			boolean positiveInfinity) {
		// get function and function variable string using temp variable
		// prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		String[] funVarStr = f.getTempVarCASString(false);

		if (sbCasCommand == null) {
			sbCasCommand = new StringBuilder();
		} else {
			sbCasCommand.setLength(0);
		}
		sbCasCommand.append("Limit(");
		sbCasCommand.append(funVarStr[0]); // function expression
		sbCasCommand.append(',');
		sbCasCommand.append(funVarStr[1]); // function variable
		sbCasCommand.append(',');
		if (!positiveInfinity) {
			sbCasCommand.append('-'); // -Infinity
		}
		sbCasCommand.append(Unicode.INFINITY);
		sbCasCommand.append(")");

		try {
			String limit = kernel
					.evaluateCachedGeoGebraCAS(sbCasCommand.toString(), null)
					.trim();

			if (!GeoFunction.isCASErrorOrInf(limit)) {

				// check not duplicated
				sbCasCommand.setLength(0);
				sbCasCommand.append("y=");
				sbCasCommand.append(limit);
				if (!SB.toString().endsWith(sbCasCommand.toString())) { // not
																		// duplicated
					if (SB.length() > 1) {
						SB.append(',');
					}
					SB.append(sbCasCommand);
				}
				return true;
			}
		} catch (Throwable t) {
			// nothing to do
		}
		return false;
	}

	@Override
	public char getLabelDelimiter() {
		return isBooleanFunction() || shortLHS != null
				|| isForceInequality() ? ':' : '=';
	}

	/**
	 * Adds vertical asymptotes to the string builder VerticalSB
	 * 
	 * @param f
	 *            function whose asymptotes we are looking for
	 * @param parentFunction
	 *            parent function (in case of conditional functions)
	 * @param verticalSB
	 *            StringBuilder for the result
	 * @param reverseCondition
	 *            if true, we reverse the parent conditional function condition
	 */
	protected void getVerticalAsymptotesStatic(GeoFunction f,
			GeoFunction parentFunction, StringBuilder verticalSB,
			boolean reverseCondition) {
		// get function and function variable string using temp variable
		// prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		StringTemplate tpl = StringTemplate.prefixedDefault;
		ExpressionNode exp = getFunctionExpression();

		exp = getSubexpForAsymptote(exp);
		String[] funVarStr = { exp.getCASstring(tpl, false),
				getVarString(tpl) };

		// solve 1/f(x) == 0 to find vertical asymptotes
		if (sbCasCommand == null) {
			sbCasCommand = new StringBuilder();
		}

		try {
			String verticalAsymptotes = transformedVericalAsymptotes(
					"Simplify(1/(", "))", funVarStr);
			// Application.debug(sb.toString()+" = "+verticalAsymptotes,1);

			// Log.debug("verticalAsymptotes = " + verticalAsymptotes);

			// eg f(x):=2^x / (2^x - 3^x) gives "{?}"
			if (GeoFunction.isCASErrorOrInf(verticalAsymptotes)) {
				verticalAsymptotes = transformedVericalAsymptotes(
						"Denominator(", ")", funVarStr);
			}
			String expAsymptotes = transformedVericalAsymptotes(
					"exp(Numerator(",
					"))", funVarStr);
			if (GeoFunction.isCASErrorOrInf(verticalAsymptotes)
					|| "{}".equals(verticalAsymptotes)) {
				verticalAsymptotes = expAsymptotes;
			} else {
				verticalAsymptotes += "," + expAsymptotes;
			}

			if (!GeoFunction.isCASErrorOrInf(verticalAsymptotes)
					&& verticalAsymptotes.length() > 2) {
				verticalAsymptotes = verticalAsymptotes.replace('{', ' ');
				verticalAsymptotes = verticalAsymptotes.replace('}', ' ');
				// verticalAsymptotes = verticalAsymptotes.replace('(',' '); //
				// eg (-1)
				// verticalAsymptotes = verticalAsymptotes.replace(')',' ');
				verticalAsymptotes = verticalAsymptotes.replaceAll("x==", "");
				verticalAsymptotes = verticalAsymptotes.replaceAll("x =", "");

				String[] verticalAsymptotesArray = verticalAsymptotes
						.split(",");

				// check they are really asymptotes
				TreeMap<Double, String> unique = new TreeMap<>();
				for (String asymptote : verticalAsymptotesArray) {
					try {
						if (!StringUtil.emptyTrim(asymptote)) {
						unique.put(kernel.getAlgebraProcessor()
								.evaluateToNumeric(asymptote,
										ErrorHelper.silent())
								.getDouble(), asymptote);
						}
					} catch (Exception e) {
						Log.warn("Error parsing: " + asymptote);
					}
				}
				for (Entry<Double, String> asymptoteX : unique.entrySet()) {
					// Application.debug(verticalAsymptotesArray[i]);

					boolean isInRange = parentFunction
									.evaluateCondition(asymptoteX.getKey());
					
					if (reverseCondition) {
						isInRange = !isInRange;
					}

					if (isInRange) {

						sbCasCommand.setLength(0);
						sbCasCommand.append("Numeric(Limit(");
						sbCasCommand.append(funVarStr[0]); // function
															// expression with
						// "ggbtmpvarx" as function
						// variable
						sbCasCommand.append(',');
						sbCasCommand.append(funVarStr[1]); // function variable
						// "ggbtmpvarx"
						sbCasCommand.append(",");
						sbCasCommand.append(asymptoteX.getValue());
						sbCasCommand.append("))");

						// Log.debug("sbCasCommand 2 = " + sbCasCommand);

						try {
							String limit = kernel.evaluateCachedGeoGebraCAS(
									sbCasCommand.toString(), null);
							// Log.debug("checking for vertical
							// asymptote: "+sb.toString()+" = "+limit,1);
							if (GeoFunction.isUndefinedOrInf(limit)) {
								if (verticalSB.length() > 1) {
									verticalSB.append(',');
								}
								verticalSB.append("x=");
								verticalSB.append(asymptoteX.getValue());
							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}

				}
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private String transformedVericalAsymptotes(String transform, String suffix,
			String[] funVarStr) {
		sbCasCommand.setLength(0);

		sbCasCommand.append("Solve((");
		sbCasCommand.append(transform);

		// function expression with "ggbtmpvarx" as variable
		sbCasCommand.append(funVarStr[0]);

		sbCasCommand.append(suffix);
		sbCasCommand.append(")=0");
		sbCasCommand.append(",");

		// function variable eg "ggbtmpvarx"
		sbCasCommand.append(funVarStr[1]);

		sbCasCommand.append(")");
		return kernel.evaluateCachedGeoGebraCAS(sbCasCommand.toString(), null);
	}

	private ExpressionNode getSubexpForAsymptote(ExpressionNode exp) {
		if (exp.getOperation() == Operation.EXP) {
			return getSubexpForAsymptote(exp.getLeftTree());
		} else if (exp.getOperation() == Operation.PLUS
				|| exp.getOperation() == Operation.MINUS) {
			if (!exp.getLeftTree().containsFreeFunctionVariable(null)) {
				return getSubexpForAsymptote(exp.getRightTree());
			}
			if (!exp.getRightTree().containsFreeFunctionVariable(null)) {
				return getSubexpForAsymptote(exp.getLeftTree());
			}

		} else if (exp.getOperation() == Operation.POWER
				&& !exp.getLeftTree().containsFreeFunctionVariable(null)) {
			return getSubexpForAsymptote(exp.getRightTree());

		}
		return exp;
	}

	/**
	 * @param str
	 *            CAS output
	 * @return whether output is undefined, infinite or contains unfinished
	 *         coputation
	 */
	final private static boolean isCASErrorOrInf(String str) {
		if (isUndefinedOrInf(str)) {
			return true;
		}
		String str1 = StringUtil.toLowerCaseUS(str);
		if (str1.length() > 6) {
			return str1.startsWith("limit") || str1.startsWith("solve")
					|| str1.startsWith("undefined");
		}
		return false;
	}

	/**
	 * @param str
	 *            CAS output
	 * @return whether output is undefined or infinite
	 */
	private static boolean isUndefinedOrInf(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		if (isUndefined(str)) {
			return true; // undefined/NaN
		}
		// if (str.indexOf("%i") > -1 ) return true; // complex answer

		if (str.charAt(0) == '\'') {
			return true; // maxima error eg 'diff(
		}
		if (str.indexOf(Unicode.INFINITY) > -1) {
			return true;
		}
		return false;
	}

	/**
	 * @param str
	 *            CAS output
	 * @return whether output is undefined
	 */
	static boolean isUndefined(String str) {
		return "?".equals(str) || "{?}".equals(str);
	}

	/**
	 * Returns this function in currently set CAS print form. For example,
	 * "a*x^2"
	 */
	@Override
	public String getCASString(StringTemplate tpl, boolean symbolic) {
		return fun.getExpression().getCASstring(tpl, symbolic);
	}

	/**
	 * Returns geo and its function variable in currently set CAS print form
	 * unsing temp variable prefixes. For example, f(x) = a x^2 returns {
	 * "ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
	 * 
	 * @param symbolic
	 *            true to keep variable names
	 * @return {function string,var string}
	 */
	final public String[] getTempVarCASString(boolean symbolic) {
		StringTemplate tpl = StringTemplate.prefixedDefault;
		String[] ret = { getCASString(tpl, symbolic), getVarString(tpl) };
		return ret;
	}

	/**
	 * Converts this function to cartesian curve and stores result to given
	 * curve
	 * 
	 * @param curve
	 *            Curve to be stored to
	 */
	public void toGeoCurveCartesian(GeoCurveCartesian curve) {
		FunctionVariable t = new FunctionVariable(kernel, "t");
		FunctionVariable x = fun.getFunctionVariable();
		ExpressionNode yExp = (ExpressionNode) getFunction().getExpression()
				.deepCopy(kernel).replace(x, t);
		curve.setFunctionY(new Function(yExp, t));
		Function varFun = new Function(new ExpressionNode(kernel, t), t);
		curve.setFunctionX(varFun);
		if (this.hasInterval()) {
			curve.setInterval(intervalMin, intervalMax);
		} else {
			double min = kernel.getXminForFunctions();
			double max = kernel.getXmaxForFunctions();
			curve.setInterval(min, max);
			curve.setHideRangeInFormula(true);
		}
	}

	/**
	 * Creates a copy of this function with different function variable so that
	 * both functions can be evaluated in separate threads
	 * 
	 * @return copy of this function
	 */
	public GeoFunction threadSafeCopy() {
		if (fun == null) {
			return this;
		}
		FunctionVariable t = new FunctionVariable(kernel, "t");
		FunctionVariable x = getFunction().getFunctionVariable();
		ExpressionNode yExp = (ExpressionNode) getFunction().getExpression()
				.deepCopy(kernel).replace(x, t);
		return yExp.buildFunction(t);
	}

	/**
	 * mirror at point P
	 * 
	 * @param P
	 *            point
	 */
	final public void mirror(Coords P) {
		dilate(new MyDouble(kernel, -1.0), P);
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		double rd = r.getNumber().getDouble(), a = S.getX(), b = S.getY();
		if (DoubleUtil.isZero(rd)) {
			setUndefined();
			return;
		}
		if (fun != null) {
			FunctionVariable oldX = fun.getFunctionVariable();
			ExpressionNode newX = new ExpressionNode(kernel,
					new MyDouble(kernel, 1 / rd), Operation.MULTIPLY,
					new ExpressionNode(kernel, oldX, Operation.PLUS,
							new MyDouble(kernel, a * rd - a)));
			ExpressionNode oldY = fun.getExpression().replace(oldX, newX)
					.wrap();
			if (!isBooleanFunction()) {

				fun.setExpression(new ExpressionNode(kernel,
						new ExpressionNode(kernel, oldY, Operation.MULTIPLY, r),
						Operation.PLUS, new MyDouble(kernel, -b * rd + b)));
			} else {
				fun.setExpression(oldY);
			}
		}
	}

	/*
	 * gets shortest distance to point p for compound paths (returns *vertical*
	 * distance for functions)
	 */
	@Override
	public double distance(GeoPoint p) {
		return Math.abs(value(p.getInhomX()) - p.getInhomY());
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		return isInRegion(P.getX2D(), P.getY2D());
	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		if (isFunctionOfY()) {
			return evaluateBoolean(y0);
		}
		return evaluateBoolean(x0);
	}

	/**
	 * @return true for functions of y (in 4.2 supported for ineqs only)
	 */
	public boolean isFunctionOfY() {
		return getVarString(StringTemplate.defaultTemplate).equals("y");
	}

	/**
	 * @return tru for functuins of z
	 */
	public boolean isFunctionOfZ() {
		return getVarString(StringTemplate.defaultTemplate).equals("z");
	}

	@Override
	public void pointChangedForRegion(GeoPointND PI) {
		Coords P = PI.getCoordsInD2();

		if (P.getZ() == 1.0) {
			// P.x = P.x;
		} else {
			P.setX(P.getX() / P.getZ());
		}

		pointChangedBoolean(false, P);

		P.setZ(1.0);

		PI.setCoords2D(P.getX(), P.getY(), P.getZ());

		// set path parameter for compatibility with
		// PathMoverGeneric
		RegionParameters pp = PI.getRegionParameters();
		pp.setT1(P.getX());
		pp.setT2(P.getY());

		PI.updateCoordsFrom2D(false, null);

	}

	@Override
	public boolean isRegion() {
		return isBooleanFunction();
	}

	@Override
	public void regionChanged(GeoPointND P) {
		pointChangedForRegion(P);
	}

	/**
	 * Reset all inequalities (slow, involves parser)
	 */
	public void resetIneqs() {
		isInequality = fun.initIneqs(getFunctionExpression());
	}

	@Override
	public IneqTree getIneqs() {
		if (fun.getIneqs() == null) {
			isInequality = fun.initIneqs(fun.getExpression());
		} else if (isInequality == null) {
			isInequality = fun.getIneqs().isValid();
		}
		return fun.getIneqs();
	}

	/**
	 * For inequalities.
	 * 
	 * @return true iff should be drawn on x-Axis only
	 */
	@Override
	public boolean showOnAxis() {
		return showOnAxis;
	}

	/**
	 * For inequalities.
	 * 
	 * @param showOnAxis
	 *            true iff should be drawn on x-Axis only
	 */
	@Override
	public void setShowOnAxis(boolean showOnAxis) {
		this.showOnAxis = showOnAxis;
	}

	@Override
	protected boolean canHaveSpecialPoints() {
		return true;
	}

	@Override
	public boolean isGeoFunctionBoolean() {
		return isBooleanFunction() || isForceInequality();
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	@Override
	public boolean isGeoFunctionConditional() {
		ExpressionNode en = getFunctionExpression();
		if (en == null) {
			return false;
		}

		return en.isConditional();
	}

	@Override
	public String getFormulaString(StringTemplate tpl,
			boolean substituteNumbers) {
		String ret = "";
		if (getFunctionExpression() != null
				&& getFunctionExpression().isConditional()) {
			if (tpl.hasType(StringType.LATEX)) {
				ret = conditionalLaTeX(substituteNumbers, tpl);
			}

		} else if (this.isGeoFunction()) {
			if (isIndependent()) {
				ret = toValueString(tpl);
			} else {

				if (getFunction() == null) {
					ret = "?";
				} else {
					ret = substituteNumbers ? getFunction().toValueString(tpl)
							: getParentAlgorithm().getDefinition(tpl);
				}
			}
		} else {
			return super.getFormulaString(tpl, substituteNumbers);
		}

		if ("".equals(ret)) {
			// eg Text[ (1,2), false]
			ret = toOutputValueString(tpl);
		}

		/*
		 * we don't want to deal with list braces in here since
		 * GeoList.toOutputValueString() takes care of it
		 */

		if (tpl.hasType(StringType.LATEX)) {
			if ((Unicode.INFINITY + "").equals(ret)) {
				ret = "\\infty";
			} else if ((Unicode.MINUS_INFINITY_STRING).equals(ret)) {
				ret = "-\\infty";
			}
		}
		if (shortLHS != null && tpl.allowShortLhs()) {
			return shortLHS + " = " + ret;
		}
		return ret;

	}

	@Override
	public int getMinimumLineThickness() {
		return (isInequality != null && isInequality) ? 0 : 1;
	}

	/**
	 * @return whether this function is inequality (more precisely logical
	 *         combination of inequalities)
	 */
	public boolean isInequality() {
		return (isInequality != null && isInequality) ? true : false;
	}

	/**
	 * @return whether this function was obtained via freehand tool/command
	 */
	public boolean isFreehandFunction() {
		return getParentAlgorithm() instanceof AlgoFunctionFreehand;
	}

	/**
	 * @return whether this function is from DataFunction[]
	 */
	public boolean isDataFunction() {
		return fun.getExpression().getOperation() == Operation.DATA;
	}

	@Override
	public FunctionVariable[] getFunctionVariables() {
		if (fun == null) {
			return null;
		}
		return fun.getFunctionVariables();
	}

	@Override
	public void clearCasEvalMap() {
		if (fun != null) {
			fun.clearCasEvalMap();
		}
	}

	/**
	 * @param substituteNumbers
	 *            true to replace names by values
	 * @param tpl
	 *            string template
	 * @return LaTeX description of this function
	 */
	public String conditionalLaTeX(boolean substituteNumbers,
			StringTemplate tpl) {
		StringBuilder sbLaTeX = new StringBuilder();
		ExpressionNode expr = getFunctionExpression();
		if (expr.getOperation().isIf()
				&& !expr.getRight().wrap().isConditional()) {
			if (substituteNumbers) {
				sbLaTeX.append(expr.getRight()
						.toValueString(StringTemplate.latexTemplate));
				sbLaTeX.append(", \\;\\;\\;\\; \\left(");
				sbLaTeX.append(expr.getLeft()
						.toValueString(StringTemplate.latexTemplate));
			} else {
				sbLaTeX.append(
						expr.getRight().toString(StringTemplate.latexTemplate));
				sbLaTeX.append(", \\;\\;\\;\\; \\left(");
				sbLaTeX.append(
						expr.getLeft().toString(StringTemplate.latexTemplate));
			}

			sbLaTeX.append(" \\right)");

		} else {
			ArrayList<ExpressionNode> cases = new ArrayList<>();
			ArrayList<Bounds> conditions = new ArrayList<>();
			boolean complete = Bounds.collectCases(expr, cases, conditions,
					new Bounds(kernel, getFunctionVariables()[0]), false);

			{
				int lastValid = conditions.size() - 1;
				while (lastValid >= 0 && !conditions.get(lastValid).isValid()) {
					lastValid--;
				}
				int firstValid = 0;
				while (firstValid < conditions.size()
						&& !conditions.get(firstValid).isValid()) {
					firstValid++;
				}
				if (firstValid > lastValid) {
					sbLaTeX.append('?');
					return sbLaTeX.toString();

				}
				if (firstValid == lastValid) {
					sbLaTeX.append(cases.get(firstValid)
							.toLaTeXString(!substituteNumbers, tpl));
					if (!complete) {

						sbLaTeX.append(", \\;\\;\\;\\; \\left(");
						sbLaTeX.append(conditions.get(firstValid).toLaTeXString(
								!substituteNumbers, getVarString(tpl), tpl));
						sbLaTeX.append(" \\right)");

					}
					return sbLaTeX.toString();
				}
				sbLaTeX.append("\\left\\{\\begin{array}{ll} ");
				for (int i = firstValid; i <= lastValid; i++) {
					if (conditions.get(i).isValid()) {
						sbLaTeX.append(cases.get(i)
								.toLaTeXString(!substituteNumbers, tpl));
						sbLaTeX.append("& : ");
						if (i == cases.size() - 1 && complete) {
							sbLaTeX.append("\\text{");
							sbLaTeX.append(getLoc().getMenu("otherwise"));
							sbLaTeX.append("}");
						} else {

							sbLaTeX.append(conditions.get(i).toLaTeXString(
									!substituteNumbers, getVarString(tpl),
									tpl));
							if (i != lastValid) {
								sbLaTeX.append("\\\\ ");
							}
						}
					}
				}
				sbLaTeX.append(" \\end{array}\\right. ");
			}
		}

		return sbLaTeX.toString().replace("\\questeq", "=");
	}

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public double[] newDoubleArray() {
		return new double[2];
	}

	@Override
	public double[] getDefinedInterval(double a, double b) {
		return RealRootUtil.getDefinedInterval(getUnivariateFunctionY(), a, b);
	}

	@Override
	public double distanceMax(double[] p1, double[] p2) {
		return Math.max(Math.abs(p1[0] - p2[0]), Math.abs(p1[1] - p2[1]));
	}

	@Override
	public ValueType getValueType() {
		return ValueType.FUNCTION;
	}

	@Override
	public void evaluatePoint(double u, double v, Coords3 point) {
		if (isInRegion(u, v)) {
			point.set(u, v, 0);
		} else {
			point.set(u, v, Double.NaN);
		}
	}

	@Override
	public boolean evaluateNormal(Coords3 p, double u, double v,
			Coords3 normal) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getMinParameter(int i) {
		// TODO Auto-generated method stub
		return Double.NaN;
	}

	@Override
	public double getMaxParameter(int i) {
		// TODO Auto-generated method stub
		return Double.NaN;
	}

	@Override
	public void setDerivatives() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resetDerivatives() {
		// TODO Auto-generated method stub
	}

	@Override
	public LevelOfDetail getLevelOfDetail() {
		// TODO Auto-generated method stub
		return LevelOfDetail.SPEED;
	}

	@Override
	public void setLevelOfDetail(LevelOfDetail lod) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSecret(AlgoElement algo) {
		if (getFunction() != null) {
			getFunction().setSecret(algo);
		}
	}

	@Override
	public void printCASEvalMapXML(StringBuilder sbXML) {
		if (fun != null) {
			fun.printCASevalMapXML(sbXML);
		}
	}

	@Override
	public void updateCASEvalMap(TreeMap<String, String> map) {
		if (fun != null) {
			fun.updateCASEvalMap(map);
		}
	}

	@Override
	public double evaluate(double x, double y) {
		if (isFunctionOfY()) {
			return value(y);
		}
		return fun.value(x);
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (algoParent != null && algoParent.getClassName() == Commands.LineGraph) {
			return DescriptionMode.DEFINITION;
		}
		if (hideDefinitionInAlgebra(getFunctionExpression())) {
			return DescriptionMode.VALUE;
		}

		return super.getDescriptionMode();
	}

	@Override
	public Function getFun(int i) {
		if (i > 1) {
			return new Function(new ExpressionNode(kernel, 0),
					fun.getFunctionVariable());
		}
		return i == 1 ? fun
				: new Function(fun.getFunctionVariable().wrap(),
						fun.getFunctionVariable());
	}

	/**
	 * @param ex
	 *            expression
	 * @return whether to hide first row in AV
	 */
	static boolean hideDefinitionInAlgebra(ExpressionNode ex) {
		return ex == null || Operation.includesFreehandOrData(ex.getOperation())
				|| ex.isSecret();
	}

	@Override
	public boolean isGeoLocusable() {
		return getParentAlgorithm() instanceof AlgoFunctionFreehand;
	}

	/**
	 * from GeoLocusable interface
	 */
	@Override
	public int getPointLength() {
		return ((AlgoFunctionFreehand) getParentAlgorithm()).getPointLength();
	}

	/**
	 * from GeoLocusable interface
	 */
	@Override
	public ArrayList<MyPoint> getPoints() {
		return ((AlgoFunctionFreehand) getParentAlgorithm()).getPoints();
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

	/**
	 * Needed to keep strong reference to parent algo (iOS)
	 * 
	 * @param dependentFunction
	 *            dependent function
	 */
	public void setDependentFunction(AlgoDependentFunction dependentFunction) {
		this.dependentFunction = dependentFunction;
	}

	/**
	 * @return parent algo (if it's dpendent function)
	 */
	public AlgoDependentFunction getDependentFunction() {
		return dependentFunction;
	}

	@Override
	protected boolean mayShowDescriptionInsteadOfDefinitionNoAlgoParent() {
		return false;
	}

	@Override
	public Function getFunctionForRoot() {
		return fun;
	}

	@Override
	public boolean hasTableOfValues() {
		return !isInequality() && super.hasTableOfValues();
	}

	@Override
	public int getTableColumn() {
		return tableViewColumn;
	}

	@Override
	public void setTableColumn(int column) {
		this.tableViewColumn = column;
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced, boolean setAuxiliaryProperty) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced,
				setAuxiliaryProperty);
		if (geo instanceof GeoEvaluatable) {
			TableProperties.transfer(geo, this);
		}
	}

	@Override
	public boolean isPointsVisible() {
		return pointsVisible;
	}

	@Override
	public void setPointsVisible(boolean pointsVisible) {
		this.pointsVisible = pointsVisible;
	}

	@Override
	public double getX() {
		try {
			PolyFunction poly = fun
					.expandToPolyFunction(fun.getExpression(), false, true);
			if (poly.getDegree() <= 1) {
				// gradient of line
				return poly.getCoeffs()[1];
			}
		} catch (Exception e) {
			//
		}

		// not a line
		return Double.NaN;
	}

	@Override
	public double getY() {
		return -1;
	}

	@Override
	protected boolean canBeFunctionOrEquationFromUser() {
		return true;
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

	/**
	 * @return the left bound if this is an interval
	 */
	public double getMin() {
		if (!isDefined()) {
			return Double.NaN;
		}
		double[] minmax = new double[2];
		GeoIntervalUtil.updateBoundaries(fun.getExpression(), minmax);
		return minmax[0];
	}

	/**
	 * @return the right bound if this is an interval
	 */
	public double getMax() {
		if (!isDefined()) {
			return Double.NaN;
		}
		double[] minmax = new double[2];
		GeoIntervalUtil.updateBoundaries(fun.getExpression(), minmax);
		return minmax[1];
	}
}