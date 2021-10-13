package org.geogebra.common.kernel.interval;

public class IntervalOperands {
	private static final IntervalAlgebra algebra;
	private static final IntervalArithmeticImpl arithmetic;
	private static final IntervalTrigonometric trigonometric;
	private static final IntervalMiscOperandsImpl misc;

	static {
		IntervalMiscOperandsImpl misc1;
		IntervalTrigonometric trigonometric1;
		IntervalArithmeticImpl arithmetic1;
		IntervalAlgebra algebra1;
		try {
			algebra1 = new IntervalAlgebra();
			arithmetic1 = new IntervalArithmeticImpl();
			trigonometric1 = new IntervalTrigonometric();
			misc1 = new IntervalMiscOperandsImpl();
		} catch (Throwable t) {
			algebra1 = null;
			arithmetic1 = null;
			trigonometric1 = null;
			misc1 = null;
		}
		misc = misc1;
		trigonometric = trigonometric1;
		arithmetic = arithmetic1;
		algebra = algebra1;
	}

	public static Interval multiply(Interval interval, Interval other) {
		return arithmetic.multiply(interval, other);
	}

	public static Interval divide(Interval interval, Interval other) {
		return arithmetic.divide(interval, other);
	}

	/**
	 *
	 * @param power of the interval
	 * @return power of the interval
	 */
	public static Interval pow(Interval interval, double power) {
		return algebra.pow(interval, power);
	}

	/**
	 * Power of an interval where power is also an interval
	 * that must be a singleton, ie [n, n]
	 *
	 * @param other interval power.
	 * @return this as result.
	 */
	public static Interval pow(Interval interval, Interval other) {
		return algebra.pow(interval, other);
	}

	/**
	 * Computes the nth root of the interval
	 * if other (=n) is a singleton
	 *
	 * @param other interval
	 * @return nth root of the interval.
	 */
	public static Interval nthRoot(Interval interval, Interval other) {
		return algebra.nthRoot(interval, other);
	}

	/**
	 * Computes x^(1/n)
	 *
	 * @param n the root
	 * @return nth root of the interval.
	 */
	public static Interval nthRoot(Interval interval, double n) {
		return algebra.nthRoot(interval, n);
	}

	public static Interval difference(Interval interval, Interval other)
			throws IntervalsDifferenceException {
		return misc.difference(interval, other);
	}

	/**
	 * Computes x mod y (x - k * y)
	 *
	 * @param other argument.
	 * @return this as result
	 */
	public static Interval fmod(Interval interval, Interval other) {
		algebra.fmod(interval, other);
		return interval;
	}

	/**
	 *
	 * @return cosine of the interval.
	 */
	public static Interval cos(Interval interval) {
		return trigonometric.cos(interval);
	}

	/**
	 *
	 * @return secant of the interval
	 */
	public static Interval sec(Interval interval) {
		return trigonometric.sec(interval);
	}

	/**
	 *
	 * @return 1 / sin(x)
	 */
	public static Interval csc(Interval interval) {
		return trigonometric.csc(interval);
	}

	/**
	 *
	 * @return cotangent of the interval
	 */
	public static Interval cot(Interval interval) {
		return trigonometric.cot(interval);
	}

	/**
	 *
	 * @return sine of the interval.
	 */
	public static Interval sin(Interval interval) {
		return trigonometric.sin(interval);
	}

	/**
	 *
	 * @return tangent of the interval.
	 */
	public static Interval tan(Interval interval) {
		return trigonometric.tan(interval);
	}

	/**
	 *
	 * @return arc sine of the interval
	 */
	public static Interval asin(Interval interval) {
		return trigonometric.asin(interval);
	}

	/**
	 *
	 * @return arc cosine of the interval
	 */
	public static Interval acos(Interval interval) {
		return trigonometric.acos(interval);
	}

	/**
	 *
	 * @return arc tangent of the interval
	 */
	public static Interval atan(Interval interval) {
		return trigonometric.atan(interval);
	}

	/**
	 *
	 * @return hyperbolic sine of the interval
	 */
	public static Interval sinh(Interval interval) {
		return trigonometric.sinh(interval);
	}

	/**
	 *
	 * @return hyperbolic cosine of the interval
	 */
	public static Interval cosh(Interval interval) {
		return trigonometric.cosh(interval);
	}

	/**
	 *
	 * @return hyperbolic tangent of the interval
	 */
	public static Interval tanh(Interval interval) {
		return trigonometric.tanh(interval);
	}

	public static Interval exp(Interval interval) {
		return misc.exp(interval);
	}

	public static Interval log(Interval interval) {
		return misc.log(interval);
	}

	/**
	 *
	 * @return square root of the interval.
	 */
	public static Interval sqrt(Interval interval) {
		return algebra.sqrt(interval);
	}

	public static Interval abs(Interval interval) {
		return misc.abs(interval);
	}

	public static Interval log10(Interval interval) {
		return misc.log10(interval);
	}

	public static Interval log2(Interval interval) {
		return misc.log2(interval);
	}

	public static Interval hull(Interval interval, Interval other) {
		return misc.hull(interval, other);
	}

	public static Interval intersect(Interval interval, Interval other) {
		return misc.intersect(interval, other);
	}

	public static Interval union(Interval interval, Interval other)
			throws IntervalsNotOverlapException {
		return misc.union(interval, other);
	}
}