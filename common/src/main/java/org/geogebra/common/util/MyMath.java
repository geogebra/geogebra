/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.util;

//import geogebra.AbstracKernel.AbstracKernel;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.geogebra.common.kernel.Kernel;

/**
 * @author Markus Hohenwarter
 */
public final class MyMath {

	/** ln(10) */
	public static final double LOG10 = Math.log(10);
	/** ln(2) */
	public static final double LOG2 = Math.log(2);
	/** 1/3 */
	public static final double ONE_THIRD = 1d / 3d;
	/** one degree */
	public static final double DEG = Math.PI / 180;

	/**
	 * Largest integer
	 */
	final public static double LARGEST_INTEGER = 9007199254740992d;

	/**
	 * Cubic root
	 * 
	 * @param a
	 *            real number
	 * @return cube root of a
	 */
	public static double cbrt(double a) {
		if (a > 0.0) {
			return Math.pow(a, ONE_THIRD);
		}
		return -Math.pow(-a, ONE_THIRD);
	}

	/**
	 * @param a
	 *            real number
	 * @return sign (0, 1 or -1)
	 */
	public static double sgn(double a) {

		// bugfix for graph f(x) = sgn(sqrt(1 - x))
		if (Double.isNaN(a)) {
			return Double.NaN;
		}

		if (DoubleUtil.isZero(a)) {
			return 0.0;
		} else if (a > 0.0) {
			return 1.0;
		} else {
			return -1.0;
		}
	}

	public static double cosh(double a) {
		return Math.cosh(a);
	}

	public static double sinh(double a) {
		return Math.sinh(a);
	}

	public static double tanh(double a) {
		return Math.tanh(a);
	}

	/**
	 * csch(0) should return undefined not inf
	 * 
	 * @param a
	 *            real number
	 * @return csch(a)
	 */
	public static double csch(double a) {

		// don't change this, csch(0.000000000000000000000000000000001)
		// **shouldn't** return NaN
		if (a == 0) {
			return Double.NaN;
		}

		return 1 / sinh(a);
	}

	public static double sech(double a) {
		return 1 / cosh(a);
	}

	/**
	 * need some leeway to make sure cos(90deg) gives 0 not
	 * 6.123233995736766E-17
	 * 
	 * eg Rotate((0,1),90deg,(0,0))
	 * 
	 * @param a
	 *            real number
	 * @return cos(a)
	 */
	public static double cos(double a) {
		double cos = Math.cos(a);

		if (Math.abs(cos) < 1E-16) {
			return 0;
		}

		return cos;
	}

	/**
	 * need some leeway to make sure asin(0.8^2 / sqrt(0.8^4)) works
	 * 
	 * @param a
	 *            real number
	 * @return asin(a)
	 */
	public static double asin(double a) {
		if (a > 1) {
			if (DoubleUtil.isEqual(a, 1, Kernel.MAX_DOUBLE_PRECISION)) {
				return Math.asin(1);
			}
		}

		if (a < -1) {
			if (DoubleUtil.isEqual(a, -1, Kernel.MAX_DOUBLE_PRECISION)) {
				return Math.asin(-1);
			}
		}

		return Math.asin(a);
	}

	/**
	 * need some leeway to make sure acos(0.8^2 / sqrt(0.8^4)) works
	 * 
	 * @param a
	 *            real number
	 * @return acos(a)
	 */
	public static double acos(double a) {
		if (a > 1 && DoubleUtil.isEqual(a, 1, Kernel.MAX_DOUBLE_PRECISION)) {
				return Math.acos(1);
		}

		if (a < -1 && DoubleUtil.isEqual(a, -1, Kernel.MAX_DOUBLE_PRECISION)) {
				return Math.acos(-1);
		}

		return Math.acos(a);
	}

	/**
	 * coth(0) should return undefined not inf
	 * 
	 * @param a
	 *            real number
	 * @return coth(a)
	 */
	public static double coth(double a) {
		return 1 / tanh(a);
	}

	/**
	 * @param a
	 *            real number
	 * @return acosh(a)
	 */
	public static double acosh(double a) {
		return FastMath.acosh(a);
	}

	/**
	 * @param a
	 *            real number
	 * @return asinh(a)
	 */
	public static double asinh(double a) {
		return FastMath.asinh(a);
	}

	/**
	 * @param a
	 *            real number
	 * @return atanh(a)
	 */
	public static double atanh(double a) {
		return FastMath.atanh(a);
	}

	/**
	 * @param a
	 *            real number
	 * @return csc(a) = 1/sin(a)
	 */
	public static double csc(double a) {
		double sin = Math.sin(a);
		if (DoubleUtil.isZero(sin)) {
			return Double.NaN;
		}

		return 1 / sin;
	}

	/**
	 * @param a
	 *            real number
	 * @return sec(a) = 1 / cos(a)
	 */
	public static double sec(double a) {
		// problem with eg sec(270deg)
		double cos = Math.cos(a);
		if (DoubleUtil.isZero(cos)) {
			return Double.NaN;
		}

		return 1 / cos;
	}

	/**
	 * @param a
	 *            real number
	 * @return cot(a) = cos(a)/sin(a)
	 */
	public static double cot(double a) {
		double sin = Math.sin(a);
		if (DoubleUtil.isZero(sin)) {
			return Double.NaN; // not infinity (1/0)
		}
		return Math.cos(a) / sin;
	}

	/**
	 * Computes adjoint matrix to {{a00,a01,a02},{a10,a11,a12},{a20,a21,a22}}
	 * 
	 * @param a00
	 *            matrix entry
	 * @param a01
	 *            matrix entry
	 * @param a02
	 *            matrix entry
	 * @param a10
	 *            matrix entry
	 * @param a11
	 *            matrix entry
	 * @param a12
	 *            matrix entry
	 * @param a20
	 *            matrix entry
	 * @param a21
	 *            matrix entry
	 * @param a22
	 *            matrix entry
	 * @return adjoint matrix
	 */
	public static double[][] adjoint(double a00, double a01, double a02,
			double a10, double a11, double a12, double a20, double a21,
			double a22) {

		return new double[][] {
				new double[] { a11 * a22 - a21 * a12,
						-(a10 * a22 - a20 * a12), a10 * a21 - a20 * a11 },
				new double[] { -(a01 * a22 - a02 * a21),
						a00 * a22 - a20 * a02, -(a00 * a21 - a01 * a20) },
				new double[] { a01 * a12 - a02 * a11,
						-(a00 * a12 - a02 * a10), a00 * a11 - a10 * a01 } };
	}

	/**
	 * @param t
	 *            parameter
	 * @param mod
	 *            modulus
	 * @return smallest multiple of modulus greater or equal t
	 */
	public static double nextMultiple(double t, double mod) {
		return Math.ceil(t / mod) * mod;
	}

	/**
	 * @param t
	 *            parameter
	 * @param mod
	 *            modulus
	 * @return similar to nextMultiple, but rounds towards negative infinity for negative numbers
	 */
	public static double signedNextMultiple(double t, double mod) {
		// Today's lesson: NaN * 0 is NaN
		if (DoubleUtil.isZero(mod)) {
			return 0;
		}

		double sign = Math.signum(t) * Math.signum(mod);
		return Math.ceil(Math.abs(t / mod)) * Math.abs(mod) * sign;
	}

	/**
	 * "pretty" numbers are 1,2,5,10,20,50,...
	 * 
	 * @param t
	 *            input number
	 * @param min
	 *            0 to return rational numbers x &gt; 0; 1 to return only
	 *            integers
	 * @return closest bigger pretty integer
	 */
	public static double nextPrettyNumber(double t, double min) {
		if (t < min) {
			return 1;
		}
		double pot = Math.pow(10, Math.floor(Math.log10(t)));
		double n = t / pot;

		if (n > 5) {
			return 10 * pot;
		} else if (n > 2) {
			return 5 * pot;
		} else {
			return 2 * pot;
		}
	}

	/**
	 * @param fun
	 *            function
	 * @param px
	 *            x-ccord of the point
	 * @param py
	 *            y-coord of the point
	 * @param x
	 *            x-coord on function
	 * @return D(x) = (x - a)^2+(f(x) - b)^2
	 */
	public static double distanceSquaredToFunctionAt(final UnivariateFunction fun,
			final double px, final double py, double x) {
		double dy = fun.value(x) - py;
		return (x - px) * (x - px) + dy * dy ;
	}

	/**
	 * Computes length of a vector
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @return length of vector (x,y)
	 */
	public static double length(double x, double y) {
		double res;
		double absx = Math.abs(x);
		double absy = Math.abs(y);

		if (absx == 0) {
			res = absy;
		} else if (absy == 0) {
			res = absx;
		} else {
			res = lengthAbsNoZero(absx, absy);
		}
		return res;
	}

	private static double lengthAbsNoZero(double absx, double absy) {
		double res;
		if (absx > absy) {
			double temp = absy / absx;
			res = absx * Math.sqrt(1.0 + temp * temp);
		} else {
			double temp = absx / absy;
			res = absy * Math.sqrt(1.0 + temp * temp);
		}
		return res;
	}

	/**
	 * Computes length of a vector
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param z
	 *            z-coordinate
	 * @return length of vector (x,y,z)
	 */
	public static double length(double x, double y, double z) {
		double absx = Math.abs(x);
		double absy = Math.abs(y);
		double absz = Math.abs(z);

		if (absx == 0) {
			if (absy == 0) {
				return absz;
			}
			if (absz == 0) {
				return absy;
			}
			return lengthAbsNoZero(absy, absz);
		}

		if (absy == 0) {
			if (absz == 0) {
				return absx;
			}
			return lengthAbsNoZero(absx, absz);
		}

		if (absz == 0) {
			return lengthAbsNoZero(absx, absy);
		}

		// no zero value

		if (absx > absy) {
			if (absx > absz) { // absx is the highest
				double tempy = absy / absx;
				double tempz = absz / absx;
				return absx * Math.sqrt(1.0 + tempy * tempy + tempz * tempz);
			}

			// absz is the highest
			double tempy = absy / absz;
			double tempx = absx / absz;
			return absz * Math.sqrt(1.0 + tempy * tempy + tempx * tempx);

		}

		if (absy > absz) { // absy is the highest
			double tempx = absx / absy;
			double tempz = absz / absy;
			return absy * Math.sqrt(1.0 + tempx * tempx + tempz * tempz);
		}

		// absz is the highest
		double tempy = absy / absz;
		double tempx = absx / absz;
		return absz * Math.sqrt(1.0 + tempy * tempy + tempx * tempx);

	}

	/**
	 * @param m1
	 *            first matrix
	 * @param m2
	 *            second matrix
	 * @return product of matrices
	 */
	public static double[][] multiply(double[][] m1, double[][] m2) {
		int l1 = m1.length;
		int l2 = m2[0].length;
		int l3 = m1[0].length;
		double[][] result = new double[l1][l2];
		for (int i = 0; i < l1; i++) {
			for (int j = 0; j < l2; j++) {
				result[i][j] = 0;
				for (int k = 0; k < l3; k++) {
					result[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}
		return result;
	}

	/**
	 * @param n0
	 *            n
	 * @param k
	 *            k
	 * @return (n choose k), 0 for non-integers
	 */
	public static double binomial(double n0, double k) {
		try {
			if (n0 == 0d && k == 0d) {
				return 1d;
			}
			double r = k > n0 / 2 ? n0 - k : k;
			if (n0 < 1d || r < 0d || n0 < r) {
				return 0d;
			}
			if (!DoubleUtil.isEqual(Math.round(n0), n0)
					|| !DoubleUtil.isEqual(Math.round(r), r)) {
				return 0d;
			}

			double n = Math.round(n0);
			r = Math.round(r);

			double ncr = binomLog(n, r);
			if (ncr == Double.POSITIVE_INFINITY) {
				return Double.POSITIVE_INFINITY; // check to stop needless slow
													// calculations
			}

			// BinomLog is not exact for some values
			// (determined by trial and error)
			if (n <= 37) {
				return ncr;
			// if (r<2.8+Math.exp((250-n)/100) && n<59000) return ncr;
			}

			// BinomBig is more accurate but slower
			// (but cannot be exact if the answer has more than about 16
			// significant digits)
			return binomBig(n, r);
		} catch (Exception e) {
			return Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * Assumes that r < n-r
	 */
	private static double binomBig(double n, double r) {

		BigInteger ncr = BigInteger.ONE, dd = BigInteger.ONE, nn, rr;
		// nn=BigInteger.valueOf((long)n);
		// rr=BigInteger.valueOf((long)r);

		// need a long-winded conversion in case n>10^18
		String nnn = Double.toString(n);
		String rrr = Double.toString(r);
		nn = (new BigDecimal(nnn)).toBigInteger();
		rr = (new BigDecimal(rrr)).toBigInteger();

		while (dd.compareTo(rr) <= 0) {
			ncr = ncr.multiply(nn);
			ncr = ncr.divide(dd); // dd is guaranteed to divide exactly into ncr
									// here
			nn = nn.subtract(BigInteger.ONE);
			dd = dd.add(BigInteger.ONE);
		}
		return ncr.doubleValue();
	}

	private static double binomLog(double n, double r) {
		// exact for n<=37
		// also if r<2.8+Math.exp((250-n)/100) && n<59000
		// eg Binom2(38,19) is wrong

		return Math.floor(0.5 + Math.exp(MyMath2.logGamma(n + 1d)
				- MyMath2.logGamma(r + 1) - MyMath2.logGamma((n - r) + 1)));
	}

	/**
	 * @param data
	 *            numbers
	 * @return biggest number in the input
	 */
	public static double max(double[] data) {
		double max = data[0];
		for (int i = 0; i < data.length; i++) {
			if (data[i] > max) {
				max = data[i];
			}
		}
		return max;
	}

	/**
	 * @param data
	 *            numbers
	 * @return smallest number in the input
	 */
	public static double min(double[] data) {
		double min = data[0];
		for (int i = 0; i < data.length; i++) {
			if (data[i] < min) {
				min = data[i];
			}
		}
		return min;
	}

	/**
	 * @param v0
	 *            first input
	 * @param v1
	 *            second input
	 * @return whether both inputs have different sign
	 */
	public static boolean changedSign(double v0, double v1) {
		return (v0 < 0 && v1 >= 0) || (v0 > 0 && v1 <= 0);
	}

	public static int max(int a, int b, int c) {
		return Math.max(a, Math.max(b, c));
	}

	/**
	 * @param dx1
	 *            x component of first vector
	 * @param dy1
	 *            y component of first vector
	 * @param dx2
	 *            x component of second vector
	 * @param dy2
	 *            y component of second vector
	 * @return angle between vectors
	 */
	public static double angle(double dx1, double dy1, double dx2, double dy2) {
		return Math.acos((dx1 * dx2 + dy1 * dy2) / Math.hypot(dx1, dy1)
				/ Math.hypot(dx2, dy2));
	}

	/**
	 * @param s1
	 *            first interval start
	 * @param e1
	 *            first intrval end
	 * @param s2
	 *            second interval start
	 * @param e2
	 *            second interval end
	 * @return whether intervals have an intersection
	 */
	public static boolean intervalsIntersect(double s1, double e1, double s2,
			double e2) {
		return (s1 <= s2 && s2 <= e1) || (s1 <= e2 && e2 <= e1)
				|| (s2 <= s1 && s1 <= e2);
	}

	/**
	 * limits a number between two bounds
	 * 
	 * @param val
	 *            number
	 * @param min
	 *            smallest value possible
	 * @param max
	 *            biggest value possible
	 * @return clamped value
	 */
	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}
}
