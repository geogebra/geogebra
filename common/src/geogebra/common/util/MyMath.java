/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.util;

//import geogebra.AbstracKernel.AbstracKernel;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.AbstractApplication;

import java.math.BigDecimal;

/**
 * @author Markus Hohenwarter
 */
public final class MyMath {

	public static final double LOG10 = Math.log(10);
	public static final double LOG2 = Math.log(2);
	public static final double ONE_THIRD = 1d / 3d;

	/**
	 * Cubic root
	 * 
	 * @param a
	 * @return cube root
	 */
	final public static double cbrt(double a) {
		if (a > 0.0) {
			return Math.pow(a, ONE_THIRD);
		}
		return -Math.pow(-a, ONE_THIRD);
	}

	final public static double sgn(Kernel AbstracKernel, double a) {

		// bugfix for graph f(x) = sgn(sqrt(1 - x))
		if (Double.isNaN(a))
			return Double.NaN;

		if (Kernel.isZero(a))
			return 0.0;
		else if (a > 0.0)
			return 1.0;
		else
			return -1.0;
	}

	final public static double cosh(double a) {
		return (Math.exp(a) + Math.exp(-a)) * 0.5;
	}

	final public static double sinh(double a) {
		return (Math.exp(a) - Math.exp(-a)) * 0.5;
	}

	final public static double tanh(double a) {
		double e = Math.exp(2.0 * a);
		return (e - 1.0) / (e + 1.0);
	}

	final public static double csch(double a) {
		return 1 / sinh(a);
	}

	final public static double sech(double a) {
		return 1 / cosh(a);
	}

	final public static double coth(double a) {
		double e = Math.exp(2.0 * a);
		return (e + 1.0) / (e - 1.0);
	}

	final public static double acosh(double a) {
		return Math.log(a + Math.sqrt(a * a - 1.0));
	}

	final public static double asinh(double a) {
		return Math.log(a + Math.sqrt(a * a + 1.0));
	}

	final public static double atanh(double a) {
		return Math.log((1.0 + a) / (1.0 - a)) * 0.5;
	}

	final public static double csc(double a) {
		double sin = Math.sin(a);
		if (Kernel.isZero(sin))
			return Double.NaN;

		return 1 / sin;
	}

	final public static double ci(double a) {
		return cisi(a, false);
	}

	final public static double si(double a) {
		return cisi(a, true);
	}

	final public static double sec(double a) {

		// problem with eg sec(270deg)
		double cos = Math.cos(a);
		if (Kernel.isZero(cos))
			return Double.NaN;

		return 1 / cos;
	}

	final public static double cot(double a) {
		double sin = Math.sin(a);
		if (Kernel.isZero(sin))
			return Double.NaN; // not infinity (1/0)
		return Math.cos(a) / sin;
	}

	/*
	 * replaced with Gamma.logGamma from Apache Commons Math // logarithm of
	 * gamma function of xx public static double gammln(double xx) { double
	 * x,y,tmp,ser; int j;
	 * 
	 * y=x=xx; tmp=x+5.5; tmp -= (x+0.5)* Math.log(tmp); ser=1.000000000190015;
	 * for (j=0;j<=5;j++) ser += cof[j]/++y; return
	 * -tmp+Math.log(2.5066282746310005*ser/x); } // coefficients for gammln
	 * private static double [] cof = {76.18009172947146,-86.50532032941677,
	 * 24.01409824083091,-1.231739572450155,
	 * 0.1208650973866179e-2,-0.5395239384953e-5};
	 */

	/**
	 * Round a double to the given number of digits
	 * 
	 * @param x
	 * @param digits
	 * @return number rounded to given number of digits
	 */
	final public static double truncate(double x, int digits) {
		BigDecimal bd = new BigDecimal(x);
		bd = bd.setScale(digits, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static double[][] adjoint(double a00, double a01, double a02,
			double a10, double a11, double a12, double a20, double a21,
			double a22) {

		return new double[][] {
				new double[] { (a11 * a22 - a21 * a12),
						-(a10 * a22 - a20 * a12), (a10 * a21 - a20 * a11) },
				new double[] { -(a01 * a22 - a02 * a21),
						(a00 * a22 - a20 * a02), -(a00 * a21 - a01 * a20) },
				new double[] { (a01 * a12 - a02 * a11),
						-(a00 * a12 - a02 * a10), (a00 * a11 - a10 * a01) } };
	}

	public static double nextMultiple(double t, double mod) {
		return Math.ceil(t / mod) * mod;

	}

	public static double nextPrettyNumber(double t) {
		if (t < 1)
			return 1;
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

	public static double length(double a, double b) {
		double res;
		double x = Math.abs(a);
		double y = Math.abs(b);

		if (x == 0)
			res = y;
		else if (y == 0)
			res = x;
		else if (x > y) {
			double temp = y / x;
			res = x * Math.sqrt(1.0 + temp * temp);
		} else {
			double temp = x / y;
			res = y * Math.sqrt(1.0 + temp * temp);
		}
		return res;

	}

	/** Euler's constant */
	public static double EULER = 0.57721566;
	private static double TMIN = 2.0;
	private static int MAXIT = 100; // Maximum number of iterations allowed.

	// #define PIBY2 1.5707963 .
	// #define FPMIN 1.0e-30 Close to smallest representable floating-point
	// number.
	// #define TMIN 2.0 Dividing line between using the series and continued
	// frac-
	// #define TRUE 1 tion.
	// #define ONE Complex(1.0,0.0)
	public static double cisi(double a2, boolean sine) {

		int i, k;
		boolean odd;
		double a, err, fact, sign, sum, sumc, sums, t, term;
		double him, hre, bim, bre, cim, cre, dim, dre, delim = 0, delre = 0;
		t = Math.abs(a2);
		if (t == 0.0) {
			return sine ? 0.0 : Double.NEGATIVE_INFINITY;

		}
		if (t > TMIN) {
			bre = 1;
			bim = t;
			cre = 1000000;
			cim = 0;
			// d=h=1/b=1/(bre+ibim)=bre-ibim/(bre^2+bim^2);
			double babs2 = bre * bre + bim * bim;
			dre = bre / babs2;
			dim = -bim / babs2;
			hre = bre / babs2;
			him = -bim / babs2;
			for (i = 2; i <= MAXIT; i++) {
				a = -(i - 1) * (i - 1);
				bre = bre + 2;
				// dinv = a*d+b
				double dinvre = a * dre + bre, dinvim = a * dim + bim, dinvabs2 = dinvre
						* dinvre + dinvim * dinvim;
				// d=1/dinv; Denominators cannot be zero.
				dre = dinvre / dinvabs2;
				dim = -dinvim / dinvabs2;
				// c=b+a/c
				double cabs2 = cre * cre + cim * cim;
				cre = bre + (cabs2 == 0 ? 0 : a * cre / cabs2);
				cim = bim - (cabs2 == 0 ? 0 : a * cim / cabs2);
				// del = c*d
				delre = cre * dre - cim * dim;
				delim = cre * dim + dre * cim;
				// h = h*del
				hre = delre * hre - delim * him;
				him = delre * him + hre * delim;
				//AbstractApplication.debug(Math.abs(delre - 1.0)+ Math.abs(delim));
				if (Math.abs(delre - 1.0) + Math.abs(delim) < Kernel.MIN_PRECISION)
					break;
			}
			if (i > MAXIT)
				return Double.NaN;
			// h = (cos(t)-isin(t))*h
			hre = Math.cos(t) * hre + Math.sin(t) * him;
			him = -Math.sin(t) * hre + Math.cos(t) * him;

			return sine ? Math.signum(a2)*(Kernel.PI_HALF + him) : -hre;
		}
		if (t < Math.sqrt(Kernel.EPSILON)) {
			sumc = 0.0;
			sums = t;
		} else {
			sum = sums = sumc = 0.0;
			sign = fact = 1.0;
			odd = true;
			for (k = 1; k <= MAXIT; k++) {
				fact *= t / k;
				term = fact / k;
				sum += sign * term;
				err = term / Math.abs(sum);
				if (odd) {
					sign = -sign;
					sums = sum;
					sum = sumc;
				} else {
					sumc = sum;
					sum = sums;
				}
				if (err < Kernel.EPSILON)
					break;
				odd = !odd;
			}
			if (k > MAXIT)
				return Double.NaN;
		}
		if (sine)
			return Math.signum(a2)*sums;
		return sumc + Math.log(t) + EULER;

	}
}
