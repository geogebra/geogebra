package org.geogebra.common.kernel.interval;

import org.geogebra.common.plugin.Operation;

import com.google.j2objc.annotations.Weak;

/**
 * Evaluates expression using interval arithmetic
 */
class IntervalEvaluate {
	@Weak
	private final Interval interval;

	/**
	 *
	 * @param interval to evaluate on.
	 */
	IntervalEvaluate(Interval interval) {
		this.interval = interval;
	}

	/**
	 * Executes the operation on two intervals
	 *
	 * @param operation to execute.
	 * @param other interval as parameter
	 * @return result interval of the operation
	 * @throws Exception division by zero
	 */
	Interval evaluate(Operation operation, Interval other)
			throws Exception {
		switch (operation) {
		case PLUS:
			return interval.add(other);
		case MINUS:
			return interval.subtract(other);
		case MULTIPLY:
			return interval.multiply(other);
		case DIVIDE:
			return interval.divide(other);
		case POWER:
			return interval.pow(other);
		case NROOT:
			return interval.nthRoot(other);
		case INTEGRAL:
		case INVERSE_NORMAL:
			break;
		case DIFF:
			return interval.difference(other);
		}
		return interval;
	}

	/**
	 * Executes unary operation on the interval.
	 *
	 * @param operation to execute
	 * @return the result interval
	 */
	Interval evaluate(Operation operation) {
		switch (operation) {
		case COS:
			return interval.cos();
		case SIN:
			return interval.sin();
		case SEC:
			return interval.sec();
		case COT:
			return interval.cot();
		case TAN:
			return interval.tan();
		case EXP:
			return interval.exp();
		case LOG:
			return interval.log();
		case ARCCOS:
			return interval.acos();
		case ARCSIN:
			return interval.asin();
		case ARCTAN:
			return interval.atan();
		case SQRT:
			return interval.sqrt();
		case SQRT_SHORT:
			break;
		case ABS:
			return interval.abs();
		case COSH:
			return interval.cosh();
		case SINH:
			return interval.sinh();
		case TANH:
			return interval.tanh();
		case LOG10:
			return interval.log10();
		case LOG2:
			return interval.log2();
		default:
			return interval;
		}
		return interval;
	}
}