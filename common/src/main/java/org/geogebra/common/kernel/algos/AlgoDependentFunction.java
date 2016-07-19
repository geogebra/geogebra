/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * This class is only needed to handle dependencies
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentFunction extends AlgoElement implements DependentAlgo {
	/** input */
	protected Function fun;
	/** output */
	protected GeoFunction f;

	private Function expandedFun;
	private ExpressionNode expression;
	private boolean expContainsFunctions; // expression contains functions
	private HashSet<GeoElement> unconditionalInput;
	private boolean fast;

	/**
	 * @param cons
	 *            construction
	 * @param fun
	 *            input function
	 * @param addToConsList
	 *            whether to add this to construction list
	 */
	public AlgoDependentFunction(Construction cons, Function fun,
			boolean addToConsList) {
		this(cons, fun, addToConsList, false);

	}

	/**
	 * @param cons
	 *            construction
	 * @param fun
	 *            input function
	 * @param addToConsList
	 *            whether to add this to construction list
	 * @param fast
	 *            use fast derivatives
	 */
	public AlgoDependentFunction(Construction cons, Function fun,
			boolean addToConsList, boolean fast) {
		super(cons, false);
		fun.initFunction();
		if (addToConsList) {
			cons.addToConstructionList(this, false);
		}
		this.fast = fast
				|| (cons.getApplication().isExam() && !cons.getApplication()
						.getExam().isCASAllowed());
		this.fun = fun;
		f = new GeoFunction(cons, false);
		f.setFunction(fun);
		f.setConstructionDefaults();

		// look for FUNCTION or DERIVATIVE nodes in function
		expression = fun.getExpression();
		expContainsFunctions = containsFunctions(expression);
		if (expContainsFunctions) {
			expandedFun = new Function(fun, kernel);
		}

		setInputOutput(); // for AlgoElement

		compute();
	}

	/**
	 * @param cons
	 *            construction
	 */
	protected AlgoDependentFunction(Construction cons) {
		super(cons);
	}

	@Override
	public Algos getClassName() {
		return Algos.Expression;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = fun.getGeoElementVariables();
		unconditionalInput = fun.getFunctionExpression().getUnconditionalVars();
		super.setOutputLength(1);
		super.setOutput(0, f);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting function
	 */
	public GeoFunction getFunction() {
		return f;
	}

	@Override
	public final void compute() {
		// evaluation of function will be done in view (see
		// geogebra.euclidian.DrawFunction)

		// check if function is defined
		boolean isDefined = inputDefined();

		f.setDefined(isDefined);
		if (isDefined && expContainsFunctions) {
			// expand the functions and derivatives in expression tree
			ExpressionValue ev = null;

			try { // needed for eg f(x)=floor(x) f'(x)

				// boolean internationalizeDigits =
				// Kernel.internationalizeDigits;
				// Kernel.internationalizeDigits = false;
				// TODO: seems that we never read internationalize digits flag
				// here ...
				ev = expandFunctionDerivativeNodes(expression.deepCopy(kernel),
						this.fast, f.getFunctionVariables());
				// Kernel.internationalizeDigits = internationalizeDigits;

			} catch (Exception e) {
				e.printStackTrace();
				Log.debug("derivative failed");
			}
			Log.debug(ev);
			if (ev == null) {
				f.setUndefined();
				return;
			}

			ExpressionNode node;
			if (ev.isExpressionNode())
				node = (ExpressionNode) ev;
			else
				node = new ExpressionNode(kernel, ev);

			expandedFun.setExpression(node);

			f.setFunction(expandedFun);
			// If the label is not set (first run of compute)
			// isFillable will take care of updating ineqs
			if (f.isBooleanFunction() && f.isLabelSet())
				f.resetIneqs();
		} else if (f.isBooleanFunction())
			f.getFunction().updateIneqs();
	}

	private boolean inputDefined() {
		if (this.unconditionalInput == null) {
			for (int i = 0; i < input.length; i++) {
				if (!input[i].isDefined()) {
					return false;
				}
			}
			return true;
		}
		Iterator<GeoElement> it = this.unconditionalInput.iterator();
		while (it.hasNext()) {
			if (!it.next().isDefined()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Expands all FUNCTION and DERIVATIVE nodes in the given expression.
	 * 
	 * @param in
	 *            expression to expand (only ExpressionNodes are affected)
	 * @param fast
	 *            use fast derivatives
	 * @param vars
	 *            function variables
	 * 
	 * @return new ExpressionNode as result
	 */
	public static ExpressionValue expandFunctionDerivativeNodes(
			ExpressionValue in, boolean fast, FunctionVariable[] vars) {
		ExpressionValue ev = expandFunctionDerivativeNodes(in, fast);
		ExpressionNode en = ev.wrap();
		for (int i = 0; i < vars.length; i++) {
			en.replaceVariables(vars[i].getSetVarString(), vars[i]);
		}
		return ev;
	}

	/**
	 * Expands all FUNCTION and DERIVATIVE nodes in the given expression.
	 * 
	 * @param ev
	 *            expression to expand (only ExpressionNodes are affected)
	 * @param fast
	 *            use fast derivatives
	 * 
	 * @return new ExpressionNode as result
	 */
	public static ExpressionValue expandFunctionDerivativeNodes(
			ExpressionValue ev, boolean fast) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) ev;
			ExpressionValue leftValue = node.getLeft().unwrap();

			switch (node.getOperation()) {
			case FUNCTION:
				// could be DERIVATIVE node
				if (leftValue.isExpressionNode()) {
					leftValue = expandFunctionDerivativeNodes(leftValue, fast);
					if (leftValue == null) {
						return null;
					}
					node.setLeft(leftValue);
					if (leftValue.isExpressionNode())
						return node;
				}

				// we do NOT expand GeoFunctionConditional objects in expression
				// tree
				return substituteFunction((Functional) leftValue,
						node.getRight(), fast);
				
			case FUNCTION_NVAR:
				// make sure we expand $ in $A1(x,y)
				if (leftValue.isExpressionNode()) {
					leftValue = expandFunctionDerivativeNodes(leftValue, fast);
					node.setLeft(leftValue);
					if (leftValue.isExpressionNode())
						return node;
				}
				if (!(leftValue instanceof FunctionalNVar)) {
					return null;
				}
				ExpressionValue ret = expandFunctionalNVar(leftValue,
						node.getRight(), 0, fast);
				return ret == null ? ev : ret;
			case DERIVATIVE:
				// don't expand derivative of GeoFunctionConditional
				if (leftValue.isGeoElement()
						&& ((GeoElement) leftValue).isGeoFunctionConditional()) {
					return node;
				}

				int order = (int) Math.round(((NumberValue) node.getRight())
						.getDouble());
				if (leftValue.isExpressionNode()
						&& (((ExpressionNode) leftValue).getOperation() == Operation.$VAR_COL
								|| ((ExpressionNode) leftValue).getOperation() == Operation.$VAR_ROW || ((ExpressionNode) leftValue)
								.getOperation() == Operation.$VAR_ROW_COL))
					leftValue = ((ExpressionNode) leftValue).getLeft();
				if (leftValue instanceof GeoCasCell) {
					return ((GeoCasCell) leftValue).getGeoDerivative(order,
							fast);
				}
				return ((Functional) leftValue).getGeoDerivative(order, fast);
			case ELEMENT_OF:
				  //list(x,x) cannot be expanded	
				ExpressionValue rt = node.getRight().unwrap();
				if (rt instanceof ListValue) {
					ListValue list = (ListValue) rt;
					int constants = list.size();
					for (int i = 0; i < list.size() - 1; i++) {
					  if(list.getListElement(i).wrap().containsFreeFunctionVariable(null)){
							constants = i;
							break;
					  }
					}
					Log.debug(constants + "/" + list.size());
					ExpressionNodeEvaluator expev = ((GeoList) leftValue)
							.getKernel().getExpressionNodeEvaluator();
					ExpressionValue res = expev.handleElementOf(leftValue,
							node.getRight(), 1);
					if (res instanceof Functional
							&& constants >= list.size() - 1) {
						return substituteFunction(((Functional) res),list
								.getListElement(list.size() - 1), fast);
					}
					if (res instanceof FunctionalNVar
							&& constants >= list.size() - ((FunctionalNVar) res)
									.getFunctionVariables().length) {
						ret = expandFunctionalNVar(res, node.getRight(),
								list.size() - ((FunctionalNVar) res)
										.getFunctionVariables().length,
								fast);
						return ret == null ? ev : ret;
					}
					if (!(res instanceof FunctionalNVar)) {
						return res;
					}
					Log.debug("Cannot expand");
				}
				// element of with no-list rhs: weird, don't expand
				return node;
				// remove spreadsheet $ references, i.e. $A1 -> A1
			case $VAR_ROW:
			case $VAR_COL:
			case $VAR_ROW_COL:
				return leftValue;

			default: // recursive calls
				node.setLeft(expandFunctionDerivativeNodes(leftValue, fast));
				node.setRight(
						expandFunctionDerivativeNodes(node.getRight(), fast));
				return node;
			}
		} else if (ev instanceof MyNumberPair) {
			((MyNumberPair) ev)
					.setX(expandFunctionDerivativeNodes(((MyNumberPair) ev)
							.getX(), fast));
			((MyNumberPair) ev)
					.setY(expandFunctionDerivativeNodes(((MyNumberPair) ev)
							.getY(), fast));
			// for f,g,h functions make sure f(g,h) expands to f(g(x),h(x))
		} else if (ev.unwrap() instanceof FunctionalNVar) {
			return ((FunctionalNVar) ev.unwrap()).getFunctionExpression()
					.deepCopy(((FunctionalNVar) ev.unwrap()).getKernel());
		}
		return ev;
	}

	private static ExpressionValue expandFunctionalNVar(
			ExpressionValue leftValue, ExpressionValue right, int offset,
			boolean fast) {

		FunctionNVar funN = ((FunctionalNVar) leftValue).getFunction();
		FunctionVariable[] xy = funN.getFunctionVariables();
		// don't destroy the function
		ExpressionNode funNExpression = funN.getExpression()
				.getCopy(funN.getKernel());
		// with f(A) where A is a point we should not get there, but
		// still
		if (!(right instanceof MyList))
			return null;
		// now replace every x in function by the expanded argument
		for (int i = 0; i < xy.length; i++)
			funNExpression = funNExpression
					.replace(xy[i], expandFunctionDerivativeNodes(
							((MyList) right).getListElement(i + offset), fast))
					.wrap();
		return (funNExpression);
	}

	private static ExpressionValue substituteFunction(Functional leftValue,
			ExpressionValue right, boolean fast) {
		Function fun = leftValue.getFunction();
		FunctionVariable x = fun.getFunctionVariable();
		// don't destroy the function
		ExpressionNode funcExpression = fun.getExpression().getCopy(
				fun.getKernel());
		// now replace every x in function by the expanded argument
		return funcExpression.replace(x,
				expandFunctionDerivativeNodes(right, fast)
				.wrap());
	}

	/**
	 * @param ev
	 *            expression
	 * @return whether given expression contains operation FUNCTION,
	 *         FUNCTION_NVAR or DERIVATIVE
	 */
	public static boolean containsFunctions(ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) ev;
			Operation op = node.getOperation();
			if (op.equals(Operation.FUNCTION_NVAR)
					|| op.equals(Operation.FUNCTION)
					|| op.equals(Operation.DERIVATIVE)) {
				return true;
			}
			// list(1,x) is function dependent, list(1,2) is not
			if (op.equals(Operation.ELEMENT_OF)) {
				return true;
			}
			return containsFunctions(node.getLeft())
					|| containsFunctions(node.getRight());
		}
		return false;
	}

	private StringBuilder sb;

	@Override
	public String toString(StringTemplate tpl) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		if (f.isLabelSet()
				&& !tpl.isHideLHS()
				&& (!f.isBooleanFunction() || tpl
						.hasType(StringType.GEOGEBRA_XML))) {
			sb.append(f.getLabel(tpl));
			sb.append("(");
			sb.append(f.getVarString(tpl));
			sb.append(") = ");
		}
		sb.append(fun.toString(tpl));
		return sb.toString();
	}

	/***
	 * checks to see if this is an nth derivative, and return an appropriate
	 * label eg f''' for 3rd derivative
	 * 
	 * @param fun
	 *            function
	 * @return label
	 */
	public static String getDerivativeLabel(Function fun) {
		ExpressionNode expr = fun.getExpression().unwrap().wrap();
		// f'(x+3) should use default label
		if (expr.getRight() != null
				&& !(expr.getRight().unwrap() instanceof FunctionVariable)) {
			return null;
		}
		// f'(x) should be called f'
		ExpressionValue exprL = expr.getLeft();
		if (exprL.isExpressionNode()) {
			ExpressionNode enLL = (ExpressionNode) expr.getLeft();
			if (enLL.getOperation().equals(Operation.DERIVATIVE)) {
				if (enLL.getLeft().isGeoElement()) {

					GeoElement geo = (GeoElement) enLL.getLeft();

					if (geo.isLabelSet()) {

						ExpressionValue evR = (enLL.getRight());

						if (evR instanceof NumberValue) {
							NumberValue num = (NumberValue) evR;
							double val = num.getDouble();

							if (val > 0d && Kernel.isInteger(val)) {

								// eg f''' if val == 3
								return geo.getLabelSimple()
										+ StringUtil.string("'", (int) val); // eg
																				// f''''

							}
						}

					}
				}
			}
		}
		return null;

	}

	public ExpressionNode getExpression() {
		return expression;
	}

	// TODO Consider locusequability

}
