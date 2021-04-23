package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
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
	private static final UnsupportedOperatorChecker
			operatorChecker = new UnsupportedOperatorChecker();
	private static final Interval EMPTY = IntervalConstants.empty();
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
		return evaluate(new Interval(x), node);
	}

	private Interval evaluate(Interval x, ExpressionValue ev) throws Exception {
		if (ev == null) {
			return EMPTY;
		}
		if (ev instanceof FunctionVariable) {
			return new Interval(x);
		}
		if (!ev.isExpressionNode()) {
			return new Interval(ev.evaluateDouble());
		}
		ExpressionNode node = ev.wrap();
		if (!node.containsFreeFunctionVariable(null)) {
			return new Interval(ev.evaluateDouble());
		}

		return evaluate(evaluate(x, node.getLeft()),
				node.getOperation(),
				evaluate(x, node.getRight()));
	}

	private Interval evaluate(Interval left, Operation operation,
			Interval right) throws Exception {

		switch (operation) {
			case NO_OPERATION:
				return left;
			case PLUS:
				return left.add(right);
			case MINUS:
				return left.subtract(right);
			case MULTIPLY:
				return left.multiply(right);
			case DIVIDE:
				return divide(left, right);
			case POWER:
				return left.pow(right);
			case NROOT:
				return left.nthRoot(right);
			case DIFF:
				return left.difference(right);
			case SIN:
				return left.sin();
			case SEC:
				return left.sec();
			case COS:
				return left.cos();
			case CSC:
				return left.csc();
			case COT:
				return left.cot();
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
			case LOG10:
				return left.log10();
			case LOG2:
				return left.log2();

			default:
				Log.warn("No interval operation for " + operation);
				return IntervalConstants.empty();
			}
		}

	private Interval divide(Interval left, Interval right) {
		if (left.isSingleton()) {
			return right.multiplicativeInverse().multiply(left);
		}
		return left.divide(right);
	}

	/**
	 *
	 * @param geo to check.
	 * @return true if the geo is a function
	 * and supported by our interval arithmetic implementation.
	 */
	public static boolean isSupported(GeoElement geo) {
		if (!(geo instanceof GeoFunction)) {
			return false;
		}
		GeoFunction function = (GeoFunction) geo;
		boolean operationSupported = isOperationSupported(function);
		boolean moreVariables = hasMoreVariables(function);
		return operationSupported && !moreVariables;
	}

	private static boolean isOperationSupported(GeoFunction function) {
		ExpressionNode expression = function.getFunctionExpression();
		if (expression == null) {
			return false;
		}
		return !expression.inspect(operatorChecker);
	}

	private static boolean hasMoreVariables(GeoFunction function) {
		ExpressionNode expression = function.getFunctionExpression();
		if (expression == null) {
			return false;
		}
		return expression.inspect(new MultipleVariableChecker());
	}
}