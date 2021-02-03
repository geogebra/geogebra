package org.geogebra.common.kernel.interval;

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
	 0*/
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
	 * @param power to raise of.
	 * @return the previous number of n^{power}
	 */
	public static double powLow(double n, double power) {
		return prev(Math.pow(n, power));
	}

	/**
	 *
	 * @param n any double.
	 * @param power to raise of.
	 * @return the next number of n^{power}
	 */
	public static double powHigh(double n, double power) {
		return next(Math.pow(n, power));
	}

	private static double powHigh(double n, int power) {
		double y = (power & 1) == 1 ? n : 1;
		int p = power;
		p >>= 1;
		while (p > 0) {
			double k = mulHigh(n, n);
			if ((p & 1) == 1) {
				y = mulHigh(k, y);
			}
			p >>= 1;
		}
		return y;
	}

	public static double subHigh(double m, double n) {
		return next(m - n);
	}

	public static double cosLow(double x) {
		return prev(Math.cos(x));
	}

	public static double cosHigh(double x) {
		return next(Math.cos(x));
	}

	public static double tanLow(double x) {
		return prev(Math.tan(x));
	}

	public static double tanHigh(double x) {
		return next(Math.tan(x));
	}

	public static double asinLow(double x) {
		return prev(Math.asin(x));
	}

	public static double asinHigh(double x) {
		return prev(Math.asin(x));
	}

	public static double acosLow(double x) {
		return prev(Math.acos(x));
	}

	public static double acosHigh(double x) {
		return next(Math.acos(x));
	}

	public static double atanLow(double x) {
		return prev(Math.atan(x));
	}

	public static double atanHigh(double x) {
		return next(Math.atan(x));
	}

	public static double sinhLow(double x) {
		return prev(Math.sinh(x));
	}

	public static double sinhHigh(double x) {
		return next(Math.sinh(x));
	}

	public static double coshLow(double x) {
		return prev(Math.cosh(x));
	}

	public static double coshHigh(double x) {
		return next(Math.cosh(x));
	}

	public static double tanhLow(double x) {
		return prev(Math.tanh(x));
	}

	public static double tanhHigh(double x) {
		return next(Math.tanh(x));
	}

	public static double expLow(double x) {
		return prev(Math.exp(x));
	}

	public static double expHigh(double x) {
		return next(Math.exp(x));
	}

	public static double logLow(double x) {
		return prev(Math.log(x));
	}

	public static double logHigh(double x) {
		return next(Math.log(x));
	}

	public static double secLow(double x) {
		return prev(1 / Math.cos(x));
	}

	public static double secHigh(double x) {
		return next(1 / Math.cos(x));
	}
}
