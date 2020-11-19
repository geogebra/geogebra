package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Class to evaluate a GeoFunction on a given interval
 * using interval arithmetic.
 *
 * @author laszlo
 */
 public class IntervalFunction {
	private final GeoFunction function;

	/**
	 * Constructor
	 *
	 * @param function to evaluate.
	 */
	IntervalFunction(GeoFunction function) {
		this.function = function;
	}

	/**
	 * Evaluates the function on a given interval.
	 *
	 * @param x interval to evaulate on.
	 * @return function result on x.
	 * @throws Exception that occurs on operands (divide by zero, power is not singleton, etc)
	 */
	public Interval evaluate(Interval x) throws Exception {
		ExpressionNode node = function.getFunctionExpression();
		if (node == null) {
			return IntervalConstants.empty();
		}

		return evaluate(new Interval(x), node);
	}

	private Interval evaluate(Interval x, ExpressionNode node) throws Exception {
		Operation operation = node.getOperation();
		if (node.isLeaf()) {
			return evaluateLeaf(x, node, operation);
		}

		return evaluate(evaluate(x, node.getLeftTree()),
				operation,
				evaluate(x, node.getRightTree()));
	}

	private Interval evaluateLeaf(Interval x, ExpressionNode node, Operation operation)
			throws Exception {
		if (node.isConstant()) {
			return new Interval(node.evaluateDouble());
		}
		return x.evaluate(operation);
	}

	private Interval evaluate(Interval left, Operation operation,
			Interval right) throws Exception {

		switch (operation) {
			case PLUS:
				return left.add(right);
			case MINUS:
				return left.subtract(right);
			case MULTIPLY:
				return left.multiply(right);
			case DIVIDE:
				return left.divide(right);
			case POWER:
				return left.pow(right);
			case NROOT:
				return left.nthRoot(right);
			case DIFF:
				return left.difference(right);
			case SIN:
				return left.sin();
			case COS:
				return left.cos();
			case SQRT:
				return left.sqrt();
			case TAN:
				return left.tan();
			case EXP:
				return left.exp();
			case LOG:
				return left.log();
			case ARCCOS:
				return left.acos();
			case ARCSIN:
				return left.asin();
			case ARCTAN:
				return left.atan();
			case ABS:
				return left.abs();
			case COSH:
				return left.cosh();
			case SINH:
				return left.sinh();
			case TANH:
				return left.tanh();
			case ACOSH:
				return left.acos();
			case LOG10:
				return left.log10();
			case LOG2:
				return left.log2();

			default:
				Log.warn("No interval operation for " + operation);
				return IntervalConstants.empty();
			}
		}

	/**
	 *
	 * @param function to examine.
	 * @return if x variable occurs more, than once in expression tree of the function.
	 */
	public static boolean hasMoreX(GeoFunction function) {
		ExpressionNode expression = function.getFunctionExpression();
		if (expression == null) {
			return false;
		}

		return containsX(expression.getLeftTree())
				&& containsX(expression.getRightTree());
	}

	private static boolean containsX(ExpressionNode node) {
		return node != null && node.containsFreeFunctionVariable("x");
	}
}