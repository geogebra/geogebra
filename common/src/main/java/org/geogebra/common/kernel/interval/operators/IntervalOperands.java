package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

public final class IntervalOperands {
	private static final IntervalAlgebra algebra;
	private static final IntervalMultiply multiply;
	private static final IntervalRoot nroot;
	private static final IntervalTrigonometric trigonometric;
	private static final IntervalMiscOperandsImpl misc;
	private static final IntervalDivide divide;

	static {
		algebra = new IntervalAlgebra();
		multiply = new IntervalMultiply();
		trigonometric = new IntervalTrigonometric();
		misc = new IntervalMiscOperandsImpl();
		divide = new IntervalDivide();
		nroot = new IntervalRoot();
	}

	public static Interval multiply(Interval interval, Interval other) {
		return multiply.compute(interval, other);
	}

	public static Interval divide(Interval interval, Interval other) {
		return divide.compute(interval, other);
	}

	public static Interval inverse(Interval interval) {
		return divide.compute(IntervalConstants.one(), interval);
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
		return nroot.compute(interval, other);
	}

	/**
	 * Computes x^(1/n)
	 *
	 * @param n the root
	 * @return nth root of the interval.
	 */
	public static Interval nthRoot(Interval interval, double n) {
		return nroot.compute(interval, n);
	}

	public static Interval difference(Interval interval, Interval other) {
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
		return inverse(cos(interval));
	}

	/**
	 *
	 * @return 1 / sin(x)
	 */
	public static Interval csc(Interval interval) {
		return inverse(sin(interval));
	}

	/**
	 *
	 * @return cotangent of the interval
	 */
	public static Interval cot(Interval interval) {
		Interval copy = new Interval(interval);
		return divide(cos(interval), sin(copy));
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
		Interval copy = new Interval(interval);
		return divide(sin(interval), cos(copy));

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
		return nroot.compute(interval, 2);
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

	public static Interval union(Interval interval, Interval other) {
		return misc.union(interval, other);
	}

	static Interval computeInverted(Interval result1, Interval result2) {
		if (result1.equals(result2) || result1.isPositive() && isNegativeOrEmpty(result2)) {
			return result1;
		}

		if (isNegativeOrEmpty(result1) && result2.isPositive()) {
			return result2;
		}

		if (isNegativeOrEmpty(result1) && isNegativeOrEmpty(result2)) {
			return undefined();
		}

		return new Interval(result1.getHigh(), result2.getLow()).invert();
	}

	private static boolean isNegativeOrEmpty(Interval interval) {
		return interval.isNegative() || interval.isUndefined();
	}

	private IntervalOperands() {
		throw new IllegalStateException("Utility class");
	}
}