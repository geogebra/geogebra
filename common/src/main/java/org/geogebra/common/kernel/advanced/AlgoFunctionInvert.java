/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Inverts a function only works if there is one "x" in the function
 * 
 * works by analyzing the EXpressionNode and reversing it
 * 
 * doesn't take account of domain/range so sin inverts to arcsin, sqrt(x) to x^2
 * 
 * @author Michael Borcherds
 */
public class AlgoFunctionInvert extends AlgoElement {

	private GeoFunction f; // input
	private GeoFunction g; // output
	private boolean numeric;

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param numeric
	 *            whether to keep result secret (NInvert)
	 */
	public AlgoFunctionInvert(Construction cons, GeoFunction f, boolean numeric) {
		super(cons);
		this.f = f;
		this.numeric = numeric;
		g = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return numeric ? Commands.NInvert : Commands.Invert;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		super.setOutputLength(1);
		super.setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return inverted function
	 */
	public GeoFunction getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}

		ExpressionValue root = f.getFunctionExpression();
		if (root == null) {
			// eg f(x) = 2
			g.setUndefined();
			return;
		}
		root = AlgoDependentFunction
				.expandFunctionDerivativeNodes(root.deepCopy(kernel), true)
				.wrap();
		FunctionVariable oldFV = f.getFunction().getFunctionVariable();

		// make sure sin(y) inverts to arcsin(y)
		FunctionVariable x = new FunctionVariable(kernel,
				oldFV.getSetVarString());
		ExpressionNode newRoot = invert(root, oldFV, x, kernel);

		if (newRoot == null) { // root not invertible
			g.setUndefined();
			return;
		}
		Function tempFun = new Function(newRoot, x);
		tempFun.initFunction();
		g.setDefined(true);
		g.setFunction(tempFun);
		if (numeric) {
			g.setSecret(this);
		}

	}

	/**
	 * @param root0  root element
	 * @param oldFV  x variable of inverted function
	 * @param x      x variable of target
	 * @param kernel kernel
	 * @return inverted expression
	 */
	public static ExpressionNode invert(ExpressionValue root0,
			FunctionVariable oldFV, FunctionVariable x, Kernel kernel) {
		if (root0.isConstant()) {
			return null;
		}
		boolean fvLeft;
		ExpressionNode newRoot = x.wrap();
		ExpressionValue root = root0.unwrap();

		// f(x)=Simplify(0x+3)
		if (root == null || root instanceof GeoNumeric) {
			return null;
		}

		// f(x)=x
		if (root instanceof FunctionVariable) {
			return newRoot;
		}

		if (root.isLeaf() || !root.isExpressionNode()) {
			Log.debug("Problem with Invert()");
			return null;
		}

		while (root != null && !root.isLeaf() && root.isExpressionNode()) {
			ExpressionValue left = ((ExpressionNode) root).getLeft().unwrap();
			ExpressionValue right = ((ExpressionNode) root).getRight().unwrap();

			Operation op = ((ExpressionNode) root).getOperation();
			switch (op) {
			case SIN:
			case COS:
			case TAN:
			case ARCSIND:
			case ARCSIN:
			case ARCCOS:
			case ARCTAN:
			case SINH:
			case COSH:
			case TANH:
			case ASINH:
			case ACOSH:
			case ATANH:
			case EXP:
			case LOG:

				newRoot = new ExpressionNode(kernel, newRoot,
						Operation.inverse(op), null);
				root = left;
				break;

			case COT:
				// acot(x) can be written as atan(1/x)
				newRoot = new ExpressionNode(kernel,
						new ExpressionNode(kernel, new MyDouble(kernel, 1.0),
								Operation.DIVIDE, newRoot),
						Operation.ARCTAN, null);
				root = left;
				break;

			case SEC:
				// asec(x) can be written as acos(1/x)
				newRoot = new ExpressionNode(kernel,
						new ExpressionNode(kernel, new MyDouble(kernel, 1.0),
								Operation.DIVIDE, newRoot),
						Operation.ARCCOS, null);
				root = left;
				break;

			case CSC:
				// acsc(x) can be written as asin(1/x)
				newRoot = new ExpressionNode(kernel,
						new ExpressionNode(kernel, new MyDouble(kernel, 1.0),
								Operation.DIVIDE, newRoot),
						Operation.ARCSIN, null);
				root = left;
				break;

			case COTH:
				// acoth(x) can be written as atanh(1/x)
				newRoot = new ExpressionNode(kernel,
						new ExpressionNode(kernel, new MyDouble(kernel, 1.0),
								Operation.DIVIDE, newRoot),
						Operation.ATANH, null);
				root = left;
				break;

			case SECH:
				// asech(x) can be written as acosh(1/x)
				newRoot = new ExpressionNode(kernel,
						new ExpressionNode(kernel, new MyDouble(kernel, 1.0),
								Operation.DIVIDE, newRoot),
						Operation.ACOSH, null);
				root = left;
				break;

			case CSCH:
				// acsch(x) can be written as asinh(1/x)
				newRoot = new ExpressionNode(kernel,
						new ExpressionNode(kernel, new MyDouble(kernel, 1.0),
								Operation.DIVIDE, newRoot),
						Operation.ASINH, null);
				root = left;
				break;

			case CBRT:

				newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER,
						new MyDouble(kernel, 3.0));
				root = left;
				break;

			case SQRT:
			case SQRT_SHORT:

				newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER,
						new MyDouble(kernel, 2.0));
				root = left;
				break;

			case LOG2:

				newRoot = new ExpressionNode(kernel, new MyDouble(kernel, 2.0),
						Operation.POWER, newRoot);
				root = left;
				break;

			case LOG10:

				newRoot = new ExpressionNode(kernel, new MyDouble(kernel, 10.0),
						Operation.POWER, newRoot);
				root = left;
				break;

			case LOGB:
				if ((fvLeft = left.contains(oldFV))
						&& (right.contains(oldFV))) {
					return null;
				}
				if (fvLeft) {
					newRoot = new ExpressionNode(kernel, right, Operation.POWER,
							new ExpressionNode(kernel, 1).divide(newRoot));
					root = left;
				} else {
					newRoot = new ExpressionNode(kernel, left, Operation.POWER,
							newRoot);
					root = right;
				}
				break;

			case POWER:
				if (!left.contains(oldFV)) {
					newRoot = new ExpressionNode(kernel, left, Operation.LOGB,
							newRoot);
					root = right;
				} else if (!right.contains(oldFV)) {
					if (right instanceof NumberValue) {
						double index = (((NumberValue) (right
								.evaluate(StringTemplate.maxPrecision)))
										.getDouble());
						if (DoubleUtil.isEqual(index, 3)) {
							// inverse of x^3 is cbrt(x)
							newRoot = new ExpressionNode(kernel, newRoot,
									Operation.CBRT, null);
						} else if (DoubleUtil.isEqual(index, 2)) {
							// inverse of x^2 is sqrt(x)
							newRoot = new ExpressionNode(kernel, newRoot,
									Operation.SQRT, null);
						} else if (DoubleUtil.isEqual(index, -1)) {
							// inverse of x^-1 is x^-1
							newRoot = new ExpressionNode(kernel, newRoot,
									Operation.POWER,
									new MyDouble(kernel, -1.0));
						} else if (right.isExpressionNode()
								&& ((ExpressionNode) right).getOperation()
										.equals(Operation.DIVIDE)) {
							// special case for x^(a/b) convert to x^(b/a)
							// AbstractApplication.debug("special case for
							// x^(a/b) convert to x^(b/a)");

							ExpressionValue num = ((ExpressionNode) right)
									.getLeft();
							ExpressionValue den = ((ExpressionNode) right)
									.getRight();

							newRoot = new ExpressionNode(kernel, newRoot,
									Operation.POWER, new ExpressionNode(kernel,
											den, Operation.DIVIDE, num));
						} else {
							// inverse of x^a is x^(1/a)

							// check if its a rational with small denominator
							// (eg not over 999)
							double[] frac = AlgoFractionText.decimalToFraction(
									index, Kernel.STANDARD_PRECISION);

							// make sure the minus is at the top of the new
							// fraction
							if (frac[0] < 0) {
								frac[0] *= -1;
								frac[1] *= -1;
							}

							if (frac[1] == 0 || frac[0] == 0) {
								return null;
							} else if (frac[0] < 100 && frac[1] < 100) {
								// nice form for x^(23/45)
								newRoot = new ExpressionNode(kernel, newRoot,
										Operation.POWER,
										new ExpressionNode(kernel,
												new MyDouble(kernel, frac[1]),
												Operation.DIVIDE,
												new MyDouble(kernel, frac[0])));
							} else {
								// just use decimals for fractions like 101/43
								newRoot = new ExpressionNode(kernel, newRoot,
										Operation.POWER,
										new MyDouble(kernel, 1.0 / index));
							}
						}
					} else {
						// inverse of x^a is x^(1/a)
						newRoot = new ExpressionNode(kernel, newRoot,
								Operation.POWER,
								new ExpressionNode(kernel,
										new MyDouble(kernel, 1.0),
										Operation.DIVIDE, right));
					}
					root = left;
				} else {
					// AbstractApplication.debug("failed at POWER");
					return null;
				}
				break;

			case PLUS:
			case MULTIPLY:
				if ((fvLeft = left.contains(oldFV))
						&& (right.contains(oldFV))) {
					return null;
				}
				// AbstractApplication.debug("left"+((ExpressionNode)
				// root).getLeft().isConstant());
				// AbstractApplication.debug("right"+((ExpressionNode)
				// root).getRight().isConstant());

				if (!fvLeft) {
					newRoot = new ExpressionNode(kernel, newRoot,
							Operation.inverse(op), left);
					root = right;
				} else {
					newRoot = new ExpressionNode(kernel, newRoot,
							Operation.inverse(op), right);
					root = left;
				}

				break;
			case MINUS:
			case DIVIDE:
				if ((fvLeft = left.contains(oldFV))
						&& (right.contains(oldFV))) {
					return null;
				}
				// AbstractApplication.debug("left"+((ExpressionNode)
				// root).getLeft().isConstant());
				// AbstractApplication.debug("right"+((ExpressionNode)
				// root).getRight().isConstant());

				if (!fvLeft) {
					// inverse of 3-x is 3-x
					newRoot = new ExpressionNode(kernel, left, op, newRoot);
					root = right;
				} else {
					if (op.equals(Operation.DIVIDE)) {
						// inverse of x/3 is 3*x (not x*3)
						newRoot = new ExpressionNode(kernel, right,
								Operation.inverse(op), newRoot);
					} else {
						// inverse of x-3 is x+3
						newRoot = new ExpressionNode(kernel, newRoot,
								Operation.inverse(op), right);
					}
					root = left;
				}

				break;
			case IF:
			case IF_SHORT:
				ExpressionNode inv = invert(right, oldFV, x, kernel);
				if (inv == null) {
					return null;
				}
				inv = inv.replace(x, newRoot).wrap();
				newRoot = new ExpressionNode(kernel,
						left.wrap().deepCopy(kernel).replace(oldFV, inv),
						Operation.IF, inv.deepCopy(kernel));
				root = null;
				break;
			default: // eg ABS, CEIL etc
				// AbstractApplication.debug("failed at"+ ((ExpressionNode)
				// root).getOperation().toString());
				return null;
			}
		}
		return newRoot;
	}

}
