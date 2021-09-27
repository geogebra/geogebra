package org.geogebra.common.kernel.interval;

import org.geogebra.common.plugin.Operation;

import com.google.j2objc.annotations.Weak;

/**
 * Evaluates expression using interval arithmetic
 */
class IntervalEvaluate {
	@Weak
	private final Interval interval;
	private static final IntervalAlgebra algebra = new IntervalAlgebra();
	private static final IntervalArithmeticImpl arithmetic = new IntervalArithmeticImpl();
	private static final IntervalTrigonometric trigonometric = new IntervalTrigonometric();
	private static final IntervalMiscOperandsImpl misc = new IntervalMiscOperandsImpl();

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
			return multiply(other);
		case DIVIDE:
			return divide(other);
		case POWER:
			return pow(other);
		case NROOT:
			return nthRoot(other);
		case INTEGRAL:
		case INVERSE_NORMAL:
			break;
		case DIFF:
			return difference(other);
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
			return cos();
		case SIN:
			return sin();
		case SEC:
			return sec();
		case COT:
			return cot();
		case TAN:
			return tan();
		case EXP:
			return exp();
		case LOG:
			return log();
		case ARCCOS:
			return acos();
		case ARCSIN:
			return asin();
		case ARCTAN:
			return atan();
		case SQRT:
			return sqrt();
		case SQRT_SHORT:
			break;
		case ABS:
			return abs();
		case COSH:
			return cosh();
		case SINH:
			return sinh();
		case TANH:
			return tanh();
		case LOG10:
			return log10();
		case LOG2:
			return log2();
		default:
			return interval;
		}
		return interval;
	}

	public Interval multiply(Interval other) {
		return arithmetic.multiply(interval, other);
	}

	public Interval divide(Interval other) {
		return arithmetic.divide(interval, other);
	}

	/**
	 *
	 * @param power of the interval
	 * @return power of the interval
	 */
	public Interval pow(double power) {
		return algebra.pow(interval, power);
	}

	/**
	 * Power of an interval where power is also an interval
	 * that must be a singleton, ie [n, n]
	 *
	 * @param other interval power.
	 * @return this as result.
	 */
	public Interval pow(Interval other) {
		return algebra.pow(interval, other);
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 *
	 * @param other interval
	 * @return nth root of the interval.
	 */
	public Interval nthRoot(Interval other) {
		return algebra.nthRoot(interval, other);
	}

	/**
	 * Computes x^(1/n)
	 *
	 * @param n the root
	 * @return nth root of the interval.
	 */
	public Interval nthRoot(double n) {
		return algebra.nthRoot(interval, n);
	}

	public Interval difference(Interval other) throws IntervalsDifferenceException {
		return misc.difference(interval, other);
	}

	/**
	 * Computes x mod y (x - k * y)
	 *
	 * @param other argument.
	 * @return this as result
	 */
	public Interval fmod(Interval other) {
		algebra.fmod(interval, other);
		return interval;
	}

	/**
	 *
	 * @return cosine of the interval.
	 */
	public Interval cos() {
		return trigonometric.cos(interval);
	}

	/**
	 *
	 * @return secant of the interval
	 */
	public Interval sec() {
		return trigonometric.sec(interval);
	}

	/**
	 *
	 * @return 1 / sin(x)
	 */
	public Interval csc() {
		return trigonometric.csc(interval);
	}

	/**
	 *
	 * @return cotangent of the interval
	 */
	public Interval cot() {
		return trigonometric.cot(interval);
	}

	/**
	 *
	 * @return sine of the interval.
	 */
	public Interval sin() {
		return trigonometric.sin(interval);
	}

	/**
	 *
	 * @return tangent of the interval.
	 */
	public Interval tan() {
		return trigonometric.tan(interval);
	}

	/**
	 *
	 * @return arc sine of the interval
	 */
	public Interval asin() {
		return trigonometric.asin(interval);
	}

	/**
	 *
	 * @return arc cosine of the interval
	 */
	public Interval acos() {
		return trigonometric.acos(interval);
	}

	/**
	 *
	 * @return arc tangent of the interval
	 */
	public Interval atan() {
		return trigonometric.atan(interval);
	}

	/**
	 *
	 * @return hyperbolic sine of the interval
	 */
	public Interval sinh() {
		return trigonometric.sinh(interval);
	}

	/**
	 *
	 * @return hyperbolic cosine of the interval
	 */
	public Interval cosh() {
		return trigonometric.cosh(interval);
	}

	/**
	 *
	 * @return hyperbolic tangent of the interval
	 */
	public Interval tanh() {
		return trigonometric.tanh(interval);
	}

	public Interval exp() {
		return misc.exp(interval);
	}

	public Interval log() {
		return misc.log(interval);
	}

	/**
	 *
	 * @return square root of the interval.
	 */
	public Interval sqrt() {
		return algebra.sqrt(interval);
	}

	public Interval abs() {
		return misc.abs(interval);
	}

	public Interval log10() {
		return misc.log10(interval);
	}

	public Interval log2() {
		return misc.log2(interval);
	}

	public Interval hull(Interval other) {
		return misc.hull(interval, other);
	}

	public Interval intersect(Interval other) {
		return misc.intersect(interval, other);
	}

	public Interval union(Interval other) throws IntervalsNotOverlapException {
		return misc.union(interval, other);
	}
}