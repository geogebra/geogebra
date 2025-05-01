package org.geogebra.common.kernel.interval.operators;

import static org.apache.commons.math3.util.FastMath.nextAfter;

/**
 * Utility class to determine the previous/next numbers
 * for algebra functions.
 *
 * @author Laszlo
 */
public class RMath {

	/**
	 *
	 * @param v reference number
	 * @return previous number of v
	 */
	public static double prev(double v) {
		if (v == Double.POSITIVE_INFINITY) {
			return v;
		}
		return nextAfter(v, Double.NEGATIVE_INFINITY);
	}

	/**
	 *
	 * @param v reference number
	 * @return next number of v
	 */
	public static double next(double v) {
		if (v == Double.NEGATIVE_INFINITY) {
			return v;
		}
		return nextAfter(v, Double.POSITIVE_INFINITY);
	}

	/**
	 *
	 * @param m nominator
	 * @param n denominator
	 * @return the previous number of m/n
	 */
	public static double divLow(double m, double n) {
		return prev(m / n);
	}

	/**
	 *
	 * @param m nominator
	 * @param n denominator
	 * @return the next number of m/n
	 */
	public static double divHigh(double m, double n) {
		return next(m / n);
	}

	/**
	 *
	 * @param m argument
	 * @param n argument
	 * @return the previous number of m * n
	 */
	public static double mulLow(double m, double n) {
		return prev(m * n);
	}

	/**
	 *
	 * @param m argument
	 * @param n argument
	 * @return the next number of m * n
	 */
	public static double mulHigh(double m, double n) {
		return next(m * n);
	}

	/**
	 *
	 * @param n any double.
	 * @param power to raise the number to.
	 * @return the previous number of n^{power}
	 */
	public static double powLow(double n, double power) {
		return prev(Math.pow(n, power));
	}

	/**
	 *
	 * @param n any double.
	 * @param power to raise the number to.
	 * @return the next number of n^{power}
	 */
	public static double powHigh(double n, double power) {
		return next(Math.pow(n, power));
	}

}
