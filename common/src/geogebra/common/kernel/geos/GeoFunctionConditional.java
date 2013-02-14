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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;

/**
 * Explicit function in one variable ("x") in the form of an If-Then-Else
 * statement
 * 
 * example: If[ x < 2, x^2, x + 2 ] where "x < 2" is a boolean function
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunctionConditional extends GeoFunction {

	// private boolean isDefined = true;

	private GeoFunction condFun, ifFun, elseFun;

	private Function uncondFun;

	/**
	 * Creates new conditional function
	 * 
	 * @param c
	 *            construction
	 */
	public GeoFunctionConditional(Construction c) {
		super(c);
	}

	/**
	 * Creates a new GeoFunctionConditional object.
	 * 
	 * @param c
	 *            construction
	 * @param condFun
	 *            a GeoFunction that evaluates to a boolean value (i.e.
	 *            isBooleanFunction() returns true)
	 * @param ifFun
	 *            function for the if branch
	 * @param elseFun
	 *            function for the else branch (may be null)
	 */
	public GeoFunctionConditional(Construction c, GeoFunction condFun,
			GeoFunction ifFun, GeoFunction elseFun) {
		super(c);
		this.condFun = condFun;
		this.ifFun = ifFun;
		this.elseFun = elseFun;
	}

	/**
	 * Copy constructor
	 * 
	 * @param geo
	 *            function to copy
	 */
	public GeoFunctionConditional(GeoFunctionConditional geo) {
		super(geo.cons);
		set(geo);
	}

	@Override
	public GeoElement copy() {
		return new GeoFunctionConditional(this);
	}

	@Override
	public void set(GeoElement geo) {
		if(!(geo instanceof GeoFunctionConditional) && geo.isGeoFunction()){
			isDefined = geo.isDefined();
			if (ifFun == null) {
				ifFun = (GeoFunction) geo.copyInternal(cons);
			}
			adjustCons(ifFun);
			elseFun = null;
			FunctionVariable x = new FunctionVariable(kernel);
			condFun = new MyBoolean(kernel,true).wrap().buildFunction(x);
			ifFun.set(geo);
			return;
		}
		GeoFunctionConditional geoFunCond = (GeoFunctionConditional) geo;
		isDefined = geoFunCond.isDefined;

		if (condFun == null) {
			condFun = (GeoFunction) geoFunCond.condFun.copyInternal(cons);
		}

		adjustCons(condFun);
		condFun.set(geoFunCond.condFun);

		if (ifFun == null) {
			ifFun = (GeoFunction) geoFunCond.ifFun.copyInternal(cons);
		}
		adjustCons(ifFun);
		ifFun.set(geoFunCond.ifFun);

		if (geoFunCond.elseFun == null) {
			elseFun = null;
		} else {
			if (elseFun == null) {
				elseFun = (GeoFunction) geoFunCond.elseFun.copyInternal(cons);
			}
			adjustCons(elseFun);
			elseFun.set(geoFunCond.elseFun);
		}
		uncondFun = null; // will be evaluated in getFunction()
	}
	private void adjustCons(GeoFunction f){
		if (isAlgoMacroOutput()) {
			f.setAlgoMacroOutput(true);
			f.setParentAlgorithm(getParentAlgorithm());
			f.setConstruction(cons);
		}
	}
	@Override
	public String getTypeString() {
		return "Function";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.FUNCTIONCONDITIONAL;
	}

	@Override
	public boolean isDefined() {
		return isDefined;
	}

	/**
	 * Returns the function which is used, if condition is satisfied
	 * 
	 * @return if branch of function
	 */
	final public GeoFunction getIfFunction() {
		return ifFun;
	}

	/**
	 * Returns condition which determines if "ifFun" or "elseFun" is used
	 * 
	 * @return condition of function
	 */
	final public GeoFunction getCondFunction() {
		return condFun;
	}

	/**
	 * Returns the function which is used, if condition is not satisfied
	 * 
	 * @return else branch of function
	 */
	final public GeoFunction getElseFunction() {
		return elseFun;
	}

	/**
	 * Replaces geo and all its dependent geos in this function's expressions by
	 * copies of their values.
	 */
	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		if (condFun != null) {
			condFun.replaceChildrenByValues(geo);
		}
		if (ifFun != null) {
			ifFun.replaceChildrenByValues(geo);
		}
		if (elseFun != null) {
			elseFun.replaceChildrenByValues(geo);
		}
		uncondFun = null;
	}

	/**
	 * Set this function to the n-th derivative of f
	 * 
	 * @param f
	 *            function
	 * @param n
	 *            order of derivative
	 */
	@Override
	public void setDerivative(CasEvaluableFunction f, int n) {
		GeoFunctionConditional fcond = (GeoFunctionConditional) f;
		ifFun.setDerivative(fcond.ifFun, n);
		if (elseFun != null)
			elseFun.setDerivative(fcond.elseFun, n);
	}

	/**
	 * Returns this function's value at position x.
	 * 
	 * @param x
	 *            position
	 * @return f(x) = condition(x) ? ifFun(x) : elseFun(x)
	 */
	@Override
	final public double evaluate(double x) {
		
		if (condFun == null || !isDefined) {
			return Double.NaN;
		}

		if (interval) {
			// check if x is in interval [a, b]
			if (x < intervalMin || x > intervalMax) {
				return Double.NaN;
			}
		}

		if (condFun.evaluateBoolean(x)) {
			return ifFun.evaluate(x);
		}
		if (elseFun == null) {
			return Double.NaN;
		}
		return elseFun.evaluate(x);

	}

	@Override
	public void translate(double vx, double vy) {
		// translate condition by vx, thus
		// changing every x into (x - vx)
		condFun.translate(vx, 0);

		// translate if and else parts too
		ifFun.translate(vx, vy);
		if (elseFun != null)
			elseFun.translate(vx, vy);
		uncondFun = null;
	}

	@Override
	public void dilate(NumberValue r, GeoPoint S) {
		condFun.dilate(r, S);

		// translate if and else parts too
		ifFun.dilate(r, S);
		if (elseFun != null)
			elseFun.dilate(r, S);
		uncondFun = null;
	}

	/**
	 * Returns non-conditional function f which satisfies f(x)=this(x) if x
	 * satisfies conditional function and f(x)=0 otherwise Sum[Sequence[If[x>k,1,0],k,1,5]]
	 */
	@Override
	public Function getFunction() {
		if (uncondFun == null) {
			
			if (condFun == null) {
				return null;
			}
			FunctionVariable fv = ifFun.getFunctionVariables()[0];
			String fvn = fv.getSetVarString();
			ExpressionNode condFunCopy = condFun.getFunctionExpression().getCopy(kernel);
			condFunCopy.replaceVariables(fvn, fv);
			ExpressionNode en;
			if (elseFun != null){
				ExpressionNode elseFunCopy = elseFun.getFunctionExpression().getCopy(kernel);
				elseFunCopy.replaceVariables(fvn, fv);
				en = new ExpressionNode(kernel, new MyNumberPair(kernel,
						condFunCopy,
						ifFun.getFunctionExpression()), 
						Operation.IF_ELSE,
						elseFunCopy);
			}
			else en = new ExpressionNode(kernel, condFunCopy, 
					Operation.IF,
					ifFun.getFunctionExpression());
			
			return new Function(en,fv);
		}
		return uncondFun;
	}

	/**
	 * Returns the corresponding Function for the given x-value. This is
	 * important for conditional functions where we have two different Function
	 * objects.
	 */
	@Override
	public Function getFunction(double x) {
		if (elseFun == null) {
			return ifFun.getFunction(x);
		}
		if (condFun.evaluateBoolean(x)) {
			return ifFun.getFunction(x);
		}
		return elseFun.getFunction(x);
	}

	@Override
	public GeoFunction getGeoDerivative(int order) {
		if (derivGeoFun == null) {
			derivGeoFun = new GeoFunctionConditional(this);
		}

		derivGeoFun.setDerivative(this, order);
		return derivGeoFun;
	}

	private GeoFunctionConditional derivGeoFun;

	@Override
	public boolean isPolynomialFunction(boolean forRootFinding, boolean symbolic) {
		return false;
	}

	@Override
	public final String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append('(');
			sbToString.append(condFun.getVarString(tpl));
			sbToString.append(") = ");
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl, false);
	}

	@Override
	final public String toSymbolicString(StringTemplate tpl) {
		return toString(tpl, true);
	}

	@Override
	public String getCASString(StringTemplate tpl, boolean symbolic) {
		return toString(tpl, symbolic);
	}

	private String toString(StringTemplate tpl, boolean symbolic) {
		if (!isDefined())
			return app.getPlain("Undefined");

		//no need to deal with CAS, we use toValueString for that

		StringBuilder sb = new StringBuilder(80);
		sb.append(app.getCommand("If"));
		sb.append('[');

		if (symbolic) {
			sb.append(condFun.toSymbolicString(tpl));
			sb.append(", ");
			sb.append(ifFun.toSymbolicString(tpl));
		} else {
			sb.append(condFun.toValueString(tpl));
			sb.append(", ");
			sb.append(ifFun.toValueString(tpl));
		}

		if (elseFun != null) {
			sb.append(", ");
			if (symbolic)
				sb.append(elseFun.toSymbolicString(tpl));
			else
				sb.append(elseFun.toValueString(tpl));
		}
		sb.append(']');

		return sb.toString();
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl, symbolic);
	}

	@Override
	public boolean isGeoFunction() {
		return true;
	}

	@Override
	public boolean isGeoFunctionConditional() {
		return true;
	}

	@Override
	public boolean isBooleanFunction() {
		return false;
	}

	@Override
	final public boolean isEqual(GeoElement geo) {

		if (!geo.getGeoClassType().equals(GeoClass.FUNCTIONCONDITIONAL))
			return false;

		GeoFunctionConditional geoFun = (GeoFunctionConditional) geo;

		// TODO better CAS checking for condFun
		StringTemplate tpl = StringTemplate.defaultTemplate;
		return condFun.toValueString(tpl).equals(
				geoFun.condFun.toValueString(tpl))
				&& ifFun.isEqual(geoFun.ifFun)
				&& (elseFun != null && elseFun.isEqual(geoFun.elseFun));

	}

	@Override
	final public boolean evaluateCondition(double x) {
		return condFun.evaluateBoolean(x);
	}

	@Override
	public String getLimit(double x, int direction) {
		if (evaluateCondition(x - 2 * direction * Kernel.getEpsilon()))
			return ifFun.getLimit(x, direction);
		else if (elseFun != null)
			return elseFun.getLimit(x, direction);
		return null;
	}

	@Override
	public void getVerticalAsymptotes(GeoFunction f, StringBuilder verticalSB,
			boolean reverse) {
		ifFun.getVerticalAsymptotes(this, verticalSB, false);
		if (elseFun != null)
			elseFun.getVerticalAsymptotes(this, verticalSB, true);
	}

	@Override
	public void getDiagonalPositiveAsymptote(GeoFunction f,
			StringBuilder verticalSB) {
		if (evaluateCondition(Double.POSITIVE_INFINITY))
			ifFun.getDiagonalPositiveAsymptote(this, verticalSB);
		else if (elseFun != null)
			elseFun.getDiagonalPositiveAsymptote(this, verticalSB);
	}

	@Override
	public void getDiagonalNegativeAsymptote(GeoFunction f,
			StringBuilder verticalSB) {
		if (evaluateCondition(Double.NEGATIVE_INFINITY))
			ifFun.getDiagonalNegativeAsymptote(this, verticalSB);
		else if (elseFun != null)
			elseFun.getDiagonalNegativeAsymptote(this, verticalSB);
	}

	@Override
	public void getHorizontalPositiveAsymptote(GeoFunction f,
			StringBuilder verticalSB) {
		if (evaluateCondition(Double.POSITIVE_INFINITY))
			ifFun.getHorizontalPositiveAsymptote(this, verticalSB);
		else if (elseFun != null)
			elseFun.getHorizontalPositiveAsymptote(this, verticalSB);

	}

	@Override
	public void getHorizontalNegativeAsymptote(GeoFunction f,
			StringBuilder verticalSB) {
		if (evaluateCondition(Double.NEGATIVE_INFINITY))
			ifFun.getHorizontalNegativeAsymptote(this, verticalSB);
		else if (elseFun != null)
			elseFun.getHorizontalNegativeAsymptote(this, verticalSB);

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
		GeoFunctionConditional ff = (GeoFunctionConditional) f;

		if (ff.ifFun != null) {
			ifFun.setUsingCasCommand(ggbCasCmd, ff.ifFun, symbolic, arbconst);
		} else {
			ifFun = null;
		}

		if (ff.elseFun != null) {
			elseFun.setUsingCasCommand(ggbCasCmd, ff.elseFun, symbolic,
					arbconst);
		} else {
			elseFun = null;
		}
	}


	@Override
	public void toGeoCurveCartesian(GeoCurveCartesian curve) {
		FunctionVariable t = new FunctionVariable(kernel, "t");
		ExpressionNode en = new ExpressionNode(kernel, this,
				Operation.FUNCTION, t);
		Function fn = new Function(en, t);
		curve.setFunctionY(fn);
		Function varFun = new Function(new ExpressionNode(kernel, t), t);
		curve.setFunctionX(varFun);
		double min = kernel.getXminForFunctions();
		double max = kernel.getXmaxForFunctions();
		curve.setInterval(min, max);
		curve.setHideRangeInFormula(true);
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);

	}

	@Override
	public GeoFunction threadSafeCopy() {
		return new GeoFunctionConditional(cons, condFun.threadSafeCopy(),
				ifFun.threadSafeCopy(), elseFun == null ? null
						: elseFun.threadSafeCopy());
	}

	/**
	 * @param elseFun else function
	 */
	public void setElseFunction(GeoFunction elseFun) {
		this.elseFun = elseFun;
		
	}
	/**
	 * 
	 * @param condFun condition as function
	 */
	public void setConditionalFunction(GeoFunction condFun) {
		this.condFun = condFun;
		
	}
	/**
	 * @param ifFun if function
	 */
	public void setIfFunction(GeoFunction ifFun) {
		this.ifFun = ifFun;
		
	}

}
