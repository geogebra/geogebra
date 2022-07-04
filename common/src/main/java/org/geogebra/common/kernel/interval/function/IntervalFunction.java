package org.geogebra.common.kernel.interval.function;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.abs;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.acos;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.asin;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.atan;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.cos;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.cosh;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.cot;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.csc;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.difference;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.exp;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.log;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.log10;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.log2;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.multiply;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.nthRoot;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.pow;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sec;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sin;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sinh;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sqrt;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.tan;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.tanh;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.IntervalPowerEvaluator;
import org.geogebra.common.kernel.interval.operators.IntervalOperands;
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
	private final GeoFunction function;

	/**
	 * Constructor
	 *
	 * @param function to evaluate.
	 */
	public IntervalFunction(GeoFunction function) {
		this.function = function;
	}

	/**
	 * Evaluates the function on a given interval.
	 *
	 * @param x interval to evaulate on.
	 * @return function result on x.
	 */
	public Interval evaluate(Interval x) {
		ExpressionNode node = function.getFunctionExpression();
		return evaluate(new Interval(x), node);
	}

	/**
	 * Evaluates an ExpressionValue on interval x.
	 * @param x to evaluate on.
	 * @param ev the expression to evaluate.
	 * @return the result of the evaluation.
	 */
	public static Interval evaluate(Interval x, ExpressionValue ev) {
		if (ev == null) {
			return undefined();
		}

		if (ev instanceof FunctionVariable) {
			return new Interval(x);
		}
		if (!ev.isExpressionNode()) {
			return evaluateDouble(ev);
		}

		ExpressionNode node = ev.wrap();
		IntervalPowerEvaluator evaluator = new IntervalPowerEvaluator(node);
		if (evaluator.isAccepted()) {
			return evaluator.evaluate(x);
		}

		if (!node.containsFreeFunctionVariable(null)) {
			return evaluateDouble(ev);
		}

		Interval left = evaluate(x, node.getLeft());
		Interval right = evaluate(x, node.getRight());
		Operation operation = node.getOperation();
		return evaluate(left, operation, right);
	}

	private static Interval evaluateDouble(ExpressionValue ev) {
		double value = ev.evaluateDouble();
		return Double.isNaN(value)
				? undefined()
				: new Interval(value);
	}

	private static Interval evaluate(Interval left, Operation operation,
			Interval right) {

		switch (operation) {
			case NO_OPERATION:
				return left;
			case PLUS:
				return left.add(right);
			case MINUS:
				return left.subtract(right);
			case MULTIPLY:
				return multiply(left, right);
			case DIVIDE:
				return divide(left, right);
			case POWER:
				return pow(left, right);
			case NROOT:
				return nthRoot(left, right);
			case DIFF:
				return difference(left, right);
			case SIN:
				return sin(left);
			case SEC:
				return sec(left);
			case COS:
				return cos(left);
			case CSC:
				return csc(left);
			case COT:
				return cot(left);
			case SQRT:
				return sqrt(left);
			case TAN:
				return tan(left);
			case EXP:
				return exp(left);
			case LOG:
				return log(left);
			case ARCCOS:
				return acos(left);
			case ARCSIN:
				return asin(left);
			case ARCTAN:
				return atan(left);
			case ABS:
				return abs(left);
			case COSH:
				return cosh(left);
			case SINH:
				return sinh(left);
			case TANH:
				return tanh(left);
			case LOG10:
				return log10(left);
			case LOG2:
				return log2(left);
			default:
				Log.warn("No interval operation for " + operation);
				return undefined();
			}
		}

	private static Interval divide(Interval left, Interval right) {
		return IntervalOperands.divide(left, right);
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

		return isOperationSupported(((GeoFunction) geo).getFunctionExpression());
	}

	static boolean isOperationSupported(ExpressionNode node) {
		if (node == null) {
			return false;
		}

		return !hasMoreVariables(node) && !node.inspect(operatorChecker);
	}

	static boolean hasMoreVariables(ExpressionNode node) {
		if (node == null) {
			return false;
		}
		return node.inspect(new MultipleVariableChecker());
	}

	public GeoFunction getFunction() {
		return function;
	}

}