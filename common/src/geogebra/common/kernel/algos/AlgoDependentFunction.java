/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.Functional;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;
import geogebra.common.util.StringUtil;

/**
 * This class is only needed to handle dependencies
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentFunction extends AlgoElement {
	/** input */
	protected Function fun;
	/** output */
	protected GeoFunction f; 

	private Function expandedFun;
	private ExpressionNode expression;
	private boolean expContainsFunctions; // expression contains functions

	/** Creates new AlgoDependentFunction 
	 * @param cons construction
	 * @param label label for output
	 * @param fun input function*/
	public AlgoDependentFunction(Construction cons, String label, Function fun) {
		this(cons, fun);

		String derivativeLabel = null;

		// auto label for f'' to be f'' etc
		if (label == null) {
			derivativeLabel = getDerivativeLabel(fun);
		}

		f.setLabel(derivativeLabel != null ? derivativeLabel : label);
	}

	/**
	 * @param cons construction
	 * @param fun input function
	 */
	public AlgoDependentFunction(Construction cons, Function fun) {
		super(cons);
		this.fun = fun;
		f = new GeoFunction(cons);
		f.setFunction(fun);
		f.initFunction();

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
	 * @param cons construction
	 */
	protected AlgoDependentFunction(Construction cons) {
		super(cons);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentFunction;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = fun.getGeoElementVariables();

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
		boolean isDefined = true;
		for (int i = 0; i < input.length; i++) {
			if (!input[i].isDefined()) {
				isDefined = false;
				break;
			}
		}
		f.setDefined(isDefined);
		if (isDefined && expContainsFunctions) {
			// expand the functions and derivatives in expression tree
			ExpressionValue ev = null;

			try { // needed for eg f(x)=floor(x) f'(x)

				//boolean internationalizeDigits = Kernel.internationalizeDigits;
				//Kernel.internationalizeDigits = false;
				//TODO: seems that we never read internationalize digits flag here ...
				ev = expandFunctionDerivativeNodes(expression.deepCopy(kernel));

				//Kernel.internationalizeDigits = internationalizeDigits;

			} catch (Exception e) {
				e.printStackTrace();
				App.debug("derivative failed");
			}

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
			//If the label is not set (first run of compute) 
			//isFillable will take care of updating ineqs
			if (f.isBooleanFunction() && f.isLabelSet())
				f.resetIneqs();
		} else if (f.isBooleanFunction())
			f.getFunction().updateIneqs();
	}

	/**
	 * Expandes all FUNCTION and DERIVATIVE nodes in the given expression.
	 * @param ev expression to expand (only ExpressionNodes are affected)
	 * 
	 * @return new ExpressionNode as result
	 */
	protected static ExpressionValue expandFunctionDerivativeNodes(
			ExpressionValue ev) {
		if (ev != null && ev.isExpressionNode()) {
			ExpressionNode node = (ExpressionNode) ev;
			ExpressionValue leftValue = node.getLeft();

			switch (node.getOperation()) {
			case FUNCTION:
				// could be DERIVATIVE node
				if (leftValue.isExpressionNode()) {
					leftValue = expandFunctionDerivativeNodes(leftValue);
					node.setLeft(leftValue);
					if (leftValue.isExpressionNode())
						return node;
				}

				// we do NOT expand GeoFunctionConditional objects in expression
				// tree
				if (leftValue.isGeoElement()
						&& ((GeoElement) leftValue).isGeoFunctionConditional())
					return node;

				Function fun =  ((Functional) leftValue)
						.getFunction();
				FunctionVariable x = fun.getFunctionVariable();
				// don't destroy the function
				ExpressionNode funcExpression = fun.getExpression().getCopy(
						fun.getKernel());
				// now replace every x in function by the expanded argument
				return funcExpression.replace(x,
						expandFunctionDerivativeNodes(node.getRight())).wrap();
			case FUNCTION_NVAR:
				App.debug("replacing");
				

				FunctionNVar funN =  ((FunctionalNVar) leftValue)
						.getFunction();
				FunctionVariable[] xy = funN.getFunctionVariables();
				// don't destroy the function
				ExpressionNode funNExpression = funN.getExpression().getCopy(
						funN.getKernel());
				// with f(A) where A is a point we should not get there, but still
				if(!(node.getRight() instanceof MyList))
					return ev;
				// now replace every x in function by the expanded argument
				for(int i=0;i<xy.length;i++)
					funNExpression = funNExpression.replace(xy[i],
						expandFunctionDerivativeNodes( ((MyList)node.getRight()).getListElement(i))).wrap();
				return(funNExpression);
			case DERIVATIVE:
				// don't expand derivative of GeoFunctionConditional
				if (leftValue.isGeoElement()
						&& ((GeoElement) leftValue).isGeoFunctionConditional()) {
					return node;
				}

				int order = (int) Math.round(((NumberValue) node.getRight())
						.getDouble());
				if(leftValue.isExpressionNode() && (((ExpressionNode)leftValue).getOperation()==Operation.$VAR_COL
						||((ExpressionNode)leftValue).getOperation()==Operation.$VAR_ROW
						||((ExpressionNode)leftValue).getOperation()==Operation.$VAR_ROW_COL)) 
					leftValue = ((ExpressionNode)leftValue).getLeft();
				return ((Functional) leftValue).getGeoDerivative(order);

				// remove spreadsheet $ references, i.e. $A1 -> A1
			case $VAR_ROW:
			case $VAR_COL:
			case $VAR_ROW_COL:
				return leftValue;

			default: // recursive calls
				node.setLeft(expandFunctionDerivativeNodes(leftValue));
				node.setRight(expandFunctionDerivativeNodes(node.getRight()));
				return node;
			}
		}
		return ev;
	}

	/**
	 * @param ev expression
	 * @return whether given expression contains operation FUNCTION, FUNCTION_NVAR or DERIVATIVE
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
		if (f.isLabelSet() && !f.isBooleanFunction()) {
			sb.append(f.getLabel(tpl));
			sb.append("(");
			sb.append(f.getVarString(tpl));
			sb.append(") = ");
		}
		sb.append(fun.toString(tpl));
		return sb.toString();
	}

	@Override
	public String toRealString(StringTemplate tpl) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		if (f.isLabelSet() && !f.isBooleanFunction()) {
			sb.append(f.getRealLabel(tpl));
			sb.append("(");
			sb.append(f.getVarString(tpl));
			sb.append(") = ");
		}
		sb.append(fun.getExpression().toRealString(tpl));
		return sb.toString();
	}

	/*
	 * checks to see if this is an nth derivative, and return an appropriate
	 * label eg f''' for 3rd derivative
	 */
	private static String getDerivativeLabel(Function fun) {
		ExpressionValue ev = fun.getExpression().getLeft();
		if (ev.isExpressionNode()) {
			ExpressionNode enL = (ExpressionNode) (fun.getExpression()
					.getLeft());
			if (enL.getOperation().equals(Operation.DERIVATIVE)) {
				if (enL.getLeft().isGeoElement()) {

					GeoElement geo = (GeoElement) enL.getLeft();

					if (geo.isLabelSet()) {

						ExpressionValue evR = (enL.getRight());

						if (evR.isNumberValue()) {
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

	// TODO Consider locusequability

}
