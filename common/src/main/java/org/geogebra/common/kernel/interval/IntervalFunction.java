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

	static Interval evaluate(Interval x, ExpressionValue ev) throws Exception {
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

		IntervalPowerEvaluator power = new IntervalPowerEvaluator(node);
		if (power.isAccepted()) {
			return power.handle(x);
		}

		if (!node.containsFreeFunctionVariable(null)) {
			return new Interval(ev.evaluateDouble());
		}

		Interval left = evaluate(x, node.getLeft());
		Interval right = evaluate(x, node.getRight());
		Operation operation = node.getOperation();
		return evaluate(left, operation, right);
	}

	private static Interval evaluate(Interval left, Operation operation,
			Interval right) throws Exception {

		switch (operation) {
			case NO_OPERATION:
				return left;
			case PLUS:
				return left.add(right);
			case MINUS:
				return left.subtract(right);
			case MULTIPLY:
				return left.getEvaluate().multiply(right);
			case DIVIDE:
				return divide(left, right);
			case POWER:
				return left.getEvaluate().pow(right);
			case NROOT:
				return left.getEvaluate().nthRoot(right);
			case DIFF:
				return left.getEvaluate().difference(right);
			case SIN:
				return left.getEvaluate().sin();
			case SEC:
				return left.getEvaluate().sec();
			case COS:
				return left.getEvaluate().cos();
			case CSC:
				return left.getEvaluate().csc();
			case COT:
				return left.getEvaluate().cot();
			case SQRT:
				return left.getEvaluate().sqrt();
			case TAN:
				return left.getEvaluate().tan();
			case EXP:
				return left.getEvaluate().exp();
			case LOG:
				return left.getEvaluate().log();
			case ARCCOS:
				return left.getEvaluate().acos();
			case ARCSIN:
				return left.getEvaluate().asin();
			case ARCTAN:
				return left.getEvaluate().atan();
			case ABS:
				return left.getEvaluate().abs();
			case COSH:
				return left.getEvaluate().cosh();
			case SINH:
				return left.getEvaluate().sinh();
			case TANH:
				return left.getEvaluate().tanh();
			case LOG10:
				return left.getEvaluate().log10();
			case LOG2:
				return left.getEvaluate().log2();

			default:
				Log.warn("No interval operation for " + operation);
				return IntervalConstants.empty();
			}
		}

	private static Interval divide(Interval left, Interval right) {
		if (left.isSingleton()) {
			return right.multiplicativeInverse().getEvaluate().multiply(left);
		}
		return left.getEvaluate().divide(right);
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

	public GeoFunction getFunction() {
		return function;
	}
}