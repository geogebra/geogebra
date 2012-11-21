/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.CASParserInterface;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.Unicode;

import java.util.ArrayList;

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
			condFun = new GeoFunction(cons,new Function(new ExpressionNode(kernel,new MyBoolean(kernel,true))));
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
	 * satisfies conditional function and f(x)=0 otherwise
	 */
	@Override
	public Function getFunction() {
		if (uncondFun == null) {
			
			if (condFun == null) {
				return null;
			}
			
			ExpressionNode en = new ExpressionNode(kernel,
					condFun.getFunctionExpression(), Operation.MULTIPLY,
					ifFun.getFunctionExpression());
			if (elseFun != null)
				en = new ExpressionNode(kernel, en, Operation.PLUS,
						new ExpressionNode(kernel, new ExpressionNode(kernel,
								condFun.getFunctionExpression(), Operation.NOT,
								null), Operation.MULTIPLY,
								elseFun.getFunctionExpression()));
			ExpressionNode en2 = en.getCopy(kernel);
			en2.replace(condFun.getFunction().getFunctionVariable(),
					ifFun.getFunction().getFunctionVariable());
			if (elseFun != null)
				en2.replace(elseFun.getFunction().getFunctionVariable(),
						ifFun.getFunction().getFunctionVariable()).wrap();
			uncondFun = new Function(en2, ifFun.getFunction()
					.getFunctionVariable());
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

		// for CAS, translate to CAS format :)
		if (tpl.hasType(StringType.MATH_PIPER)
				|| tpl.hasType(StringType.MAXIMA)
				|| tpl.hasType(StringType.MPREDUCE)) {
			// TODO: implement if in mpreduce
			CASParserInterface cas = kernel.getGeoGebraCAS().getCASparser();
			String cmd = cas.getTranslatedCASCommand(elseFun == null ? "If.2"
					: "If.3");
			if (symbolic) {
				cmd = cmd.replace("%0", condFun.toSymbolicString(tpl));
				cmd = cmd.replace("%1", ifFun.toSymbolicString(tpl));
				if (elseFun != null)
					cmd = cmd.replace("%2", elseFun.toSymbolicString(tpl));
			} else {
				cmd = cmd.replace("%0", condFun.toValueString(tpl));
				cmd = cmd.replace("%1", ifFun.toValueString(tpl));
				if (elseFun != null)
					cmd = cmd.replace("%2", elseFun.toValueString(tpl));
			}

			return cmd;
		}

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

	/**
	 * @param substituteNumbers
	 *            true to replace names by values
	 * @param tpl
	 *            string template
	 * @return LaTeX description of this function
	 */
	public String conditionalLaTeX(boolean substituteNumbers, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		if (getElseFunction() == null && !ifFun.isGeoFunctionConditional()) {
			sb.append(getIfFunction().getFormulaString(
					StringTemplate.latexTemplate, substituteNumbers));
			sb.append(" \\;\\;\\;\\; \\left(");
			sb.append(getCondFunction().getFormulaString(
					StringTemplate.latexTemplate, substituteNumbers));
			sb.append(" \\right)");

		} else {
			ArrayList<ExpressionNode> cases = new ArrayList<ExpressionNode>();
			ArrayList<Bounds> conditions = new ArrayList<Bounds>();
			boolean complete = collectCases(cases, conditions, new Bounds());
			sb.append("\\left\\{\\begin{array}{ll} ");
			for (int i = 0; i < cases.size(); i++) {
				sb.append(cases.get(i).toLaTeXString(!substituteNumbers, tpl));
				sb.append("& : ");
				if (i == cases.size() - 1 && complete) {
					sb.append("\\text{");
					sb.append(app.getPlain("otherwise"));
					sb.append("}");
				} else {
					sb.append(conditions.get(i).toLaTeXString(
							!substituteNumbers, getVarString(tpl), tpl));
					if (i != cases.size() - 1)
						sb.append("\\\\ ");
				}
			}
			sb.append(" \\end{array}\\right. ");
		}

		return sb.toString();
	}

	/**
	 * eg <piecewise><piece><cn> 0 </cn><apply> <lt/> <ci> x </ci> <cn> 0 </cn>
	 * </apply> </piece> <otherwise> <ci> x </ci> </otherwise> </piecewise>
	 * 
	 * @param substituteNumbers
	 *            true to replace names by values
	 * @param tpl
	 *            string template
	 * @return MathML description of this function, eg
	 */
	public String conditionalMathML(boolean substituteNumbers,
			StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		if (getElseFunction() == null && !ifFun.isGeoFunctionConditional()) {
			sb.append("<piecewise><piece>");
			sb.append(getIfFunction().getFormulaString(
					StringTemplate.latexTemplate, substituteNumbers));
			sb.append(getCondFunction().getFormulaString(
					StringTemplate.latexTemplate, substituteNumbers));
			sb.append("</piece></piecewise>");

		} else {
			sb.append("<piecewise>");
			ArrayList<ExpressionNode> cases = new ArrayList<ExpressionNode>();
			ArrayList<Bounds> conditions = new ArrayList<Bounds>();
			boolean complete = collectCases(cases, conditions, new Bounds());
			for (int i = 0; i < cases.size(); i++) {
				if (i == cases.size() - 1 && complete) {
					sb.append("<otherwise>");
					sb.append(cases.get(i).toLaTeXString(!substituteNumbers,
							tpl));
					sb.append("</otherwise>");
				} else {
					sb.append("<piece>");
					sb.append(cases.get(i).toLaTeXString(!substituteNumbers,
							tpl));
					sb.append(conditions.get(i).toLaTeXString(
							!substituteNumbers, getVarString(tpl), tpl));
					sb.append("</piece>");
				}
			}
			sb.append("</piecewise>");
		}

		return sb.toString();
	}

	private boolean collectCases(ArrayList<ExpressionNode> cases,
			ArrayList<Bounds> conditions, Bounds parentCond) {
		boolean complete = elseFun != null;
		Bounds positiveCond = parentCond.addRestriction(condFun
				.getFunctionExpression());
		Bounds negativeCond = parentCond.addRestriction(condFun
				.getFunctionExpression().negation());
		if (ifFun instanceof GeoFunctionConditional) {
			complete &= ((GeoFunctionConditional) ifFun).collectCases(cases,
					conditions, positiveCond);
		} else {
			cases.add(ifFun.getFunctionExpression());
			conditions.add(positiveCond);
		}

		if (elseFun instanceof GeoFunctionConditional) {
			complete &= ((GeoFunctionConditional) elseFun).collectCases(cases,
					conditions, negativeCond);
		} else if (elseFun != null) {
			cases.add(elseFun.getFunctionExpression());
			conditions.add(negativeCond);
		}
		return complete;
	}

	/**
	 * Container for condition tripples (upper bound, lower bound, other
	 * conditions)
	 * 
	 * @author kondr
	 * 
	 */
	class Bounds {
		private boolean lowerSharp, upperSharp;
		private Double lower, upper;
		private ExpressionNode condition;

		/**
		 * Adds restrictions from the expression to current bounds
		 * 
		 * @param e
		 *            expression
		 * @return new bounds
		 */
		public Bounds addRestriction(ExpressionNode e) {
			if (e.getOperation().equals(Operation.AND)) {
				return addRestriction(e.getLeftTree()).addRestriction(
						e.getRightTree());
			}
			Bounds b = new Bounds();
			b.lower = lower;
			b.upper = upper;
			b.lowerSharp = lowerSharp;
			b.upperSharp = upperSharp;
			b.condition = condition;//If[x==1,1,If[x==2,3,4]]
		
			boolean simple = e.getOperation() == Operation.GREATER
					|| e.getOperation() == Operation.GREATER_EQUAL
					|| e.getOperation() == Operation.LESS
					|| e.getOperation() == Operation.LESS_EQUAL
					|| e.getOperation() == Operation.EQUAL_BOOLEAN;

			if (simple && e.getLeft() instanceof FunctionVariable
					&& e.getRight() instanceof MyDouble) {
				double d = ((MyDouble) e.getRight()).getDouble();
				if (e.getOperation() == Operation.GREATER
						&& (lower == null || lower <= d))// x > d
				{
					b.lower = d;
					b.lowerSharp = true;
				} else if ((e.getOperation() == Operation.GREATER_EQUAL || e
						.getOperation() == Operation.EQUAL_BOOLEAN)
						&& (lower == null || lower < d))// x > d
				{
					b.lower = d;
					b.lowerSharp = false;
				} else if (e.getOperation() == Operation.LESS
						&& (upper == null || upper >= d))// x > d
				{
					b.upper = d;
					b.upperSharp = true;
				}
				if ((e.getOperation() == Operation.LESS_EQUAL || e
						.getOperation() == Operation.EQUAL_BOOLEAN)
						&& (upper == null || upper > d))// x > d
				{
					b.upper = d;
					b.upperSharp = false;
				}
			} else if (simple && e.getRight() instanceof FunctionVariable
					&& e.getLeft() instanceof MyDouble) {
				double d = ((MyDouble) e.getLeft()).getDouble();
				if (e.getOperation() == Operation.LESS
						&& (lower == null || lower <= d))// x > d
				{
					b.lower = d;
					b.lowerSharp = true;
				} else if ((e.getOperation() == Operation.LESS_EQUAL || e
						.getOperation() == Operation.EQUAL_BOOLEAN)
						&& (lower == null || lower < d))// x > d
				{
					b.lower = d;
					b.lowerSharp = false;
				} else if (e.getOperation() == Operation.GREATER
						&& (upper == null || upper >= d))// x > d
				{
					b.upper = d;
					b.upperSharp = true;
				}
				if ((e.getOperation() == Operation.GREATER_EQUAL || e
						.getOperation() == Operation.EQUAL_BOOLEAN)
						&& (upper == null || upper > d))// x > d
				{
					b.upper = d;
					b.upperSharp = false;
				}
			} else {
				if (condition == null)
					b.condition = e;
				else
					b.condition = condition.and(e);
			}
			//If[x==1,2,If[x==3,4,5]]
			if(b.upper!=null && b.lower!=null && (b.condition!=null) && 
					Kernel.isEqual(b.upper.doubleValue(),b.lower.doubleValue())){
				getCondFunction().getFunction().getFunctionVariable().set(b.upper);
				ExpressionValue v = b.condition.evaluate(StringTemplate.defaultTemplate);
				if(v instanceof BooleanValue && ((BooleanValue)v).getBoolean())
					b.condition = null;
			}
			//If[x==1,2,If[x>3,4,5]]
			if(b.condition!=null && b.condition.getOperation() == Operation.NOT_EQUAL){
				if (b.condition.getLeft() instanceof FunctionVariable
						&& b.condition.getRight() instanceof MyDouble){
					double d= ((MyDouble)b.condition.getRight()).getDouble();
					if((b.lower!=null && d<b.lower)||(b.upper!=null && d>b.upper))
						b.condition = null;
				}
				else if ( b.condition.getRight() instanceof FunctionVariable
						&& b.condition.getLeft() instanceof MyDouble){
					double d= ((MyDouble)b.condition.getLeft()).getDouble();
					if((b.lower!=null && d<b.lower)||(b.upper!=null && d>b.upper))
						b.condition = null;
				}
			}
			return b;
		}

		/**
		 * @param symbolic
		 *            true to keep variable names
		 * @param varString
		 *            variable string
		 * @param tpl
		 *            string template
		 * @return LaTeX string
		 */
		public String toLaTeXString(boolean symbolic, String varString,
				StringTemplate tpl) {
			StringBuilder ret = new StringBuilder();

			if (tpl.hasType(StringType.LATEX)) {

				if (upper == null && lower != null) {
					ret.append(varString);
					ret.append(" ");
					ret.append(lowerSharp ? ">" : Unicode.GREATER_EQUAL);
					ret.append(" ");
					ret.append(kernel.format(lower, tpl));
				} else if (lower == null && upper != null) {
					ret.append(varString);
					ret.append(" ");
					ret.append(upperSharp ? "<" : Unicode.LESS_EQUAL);
					ret.append(" ");
					ret.append(kernel.format(upper, tpl));
				} else if (lower != null && upper != null) {
					if (Kernel.isEqual(lower, upper) && !lowerSharp
							&& !upperSharp) {
						ret.append(varString);
						ret.append(" = ");
						ret.append(kernel.format(lower, tpl));
					} else {
						ret.append(kernel.format(lower, tpl));
						ret.append(" ");
						ret.append(lowerSharp ? "<" : Unicode.LESS_EQUAL);
						ret.append(" ");
						ret.append(varString);
						ret.append(" ");
						ret.append(upperSharp ? "<" : Unicode.LESS_EQUAL);
						ret.append(" ");
						ret.append(kernel.format(upper, tpl));
					}
				}
				//upper and lower are null, we only retrn condition right here
				else if (condition != null) {
					return condition.toLaTeXString(symbolic, tpl);
				}
				//we may still need to append condition
				if (condition != null) {
					ret.insert(0, "(");
					ret.append(")\\wedge \\left(");
					ret.append(condition.toLaTeXString(symbolic, tpl));
					ret.append("\\right)");
				}

			} else {
				// StringType.MATHML
				// <apply><lt/><ci>x</ci><cn>3</cn></apply>

				if (upper == null && lower != null) {
					ret.append("<apply>");
					ret.append(lowerSharp ? "<gt/>" : "<geq/>");
					ret.append("<ci>");
					ret.append(varString);
					ret.append("</ci><cn>");
					ret.append(kernel.format(lower, tpl));
					ret.append("</cn></apply>");
				} else if (lower == null && upper != null) {
					ret.append("<apply>");
					ret.append(upperSharp ? "<lt/>" : "<leq/>");
					ret.append("<ci>");
					ret.append(varString);
					ret.append("</ci><cn>");
					ret.append(kernel.format(upper, tpl));
					ret.append("</cn></apply>");
				} else if (lower != null && upper != null) {
					if (Kernel.isEqual(lower, upper) && !lowerSharp
							&& !upperSharp) {
						ret.append("<apply>");
						ret.append("<eq/>");
						ret.append("<ci>");
						ret.append(varString);
						ret.append("</ci><cn>");
						ret.append(kernel.format(lower, tpl));
						ret.append("</cn></apply>");
					} else {

						if (lowerSharp == upperSharp) {
							ret.append("<apply>");
							ret.append(lowerSharp ? "<lt/>" : "<leq/>");
							ret.append("<cn>");
							ret.append(kernel.format(lower, tpl));
							ret.append("</cn>");
							ret.append("<ci>");
							ret.append(varString);
							ret.append("</ci>");
							ret.append("<cn>");
							ret.append(kernel.format(upper, tpl));
							ret.append("</cn>");
							ret.append("</apply>");
						} else {
							// more complex for eg 3 < x <= 5

							ret.append("<apply>");// <apply>
							ret.append("<and/>");// <and/>
							ret.append("<apply>");// <apply>
							ret.append(lowerSharp ? "<lt/>" : "<leq/>");// <lt/>
							ret.append("<cn>");
							ret.append(kernel.format(lower, tpl));
							ret.append("</cn>");// <cn>3</cn>
							ret.append("<ci>");
							ret.append(varString);
							ret.append("</ci>");// <ci>x</ci>
							ret.append("</apply>");// </apply>
							ret.append("<apply>");// <apply>
							ret.append(upperSharp ? "<lt/>" : "<leq/>");// <leq/>
							ret.append("<ci>");
							ret.append(varString);
							ret.append("</ci>");// <ci>x</ci>
							ret.append("<cn>");
							ret.append(kernel.format(upper, tpl));
							ret.append("</cn>"); // <cn>5</cn>
							ret.append("</apply>");// </apply>
							ret.append("</apply>");// </apply>
						}

					}
				}
				//upper and lower are null, just return condition
				else if (condition != null) {
					return condition.toLaTeXString(symbolic, tpl);
				} 
				//we may still need to append condition
				if (condition != null) {

					// prepend
					ret.insert(0, "<apply><and/>");
					ret.append(condition.toLaTeXString(symbolic, tpl));
					ret.append("</apply>");

				}

			}

			return ret.toString();
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
