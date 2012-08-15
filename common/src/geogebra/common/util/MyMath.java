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

	public static double[][] multiply(double[][] m1, double[][] m2) {
		int l1 = m1.length;
		int l2 = m2[0].length;
		int l3 = m1[0].length;
		double[][] result = new double[l1][l2];
		for(int i=0;i<l1;i++)
			for(int j=0;j<l2;j++){
				result[i][j] = 0;
				for(int k =0;k < l3;k++)
					result[i][j] += m1[i][k]*m2[k][j];
			}
		return result;
	}

    private static double Z_MAX = 6;                    // Maximum ±z value

    /*  The following JavaScript functions for calculating normal and
        chi-square probabilities and critical values were adapted by
        John Walker from C implementations
        written by Gary Perlman of Wang Institute, Tyngsboro, MA
        01879.  Both the original C code and this JavaScript edition
        are in the public domain.  */

    /**  POZ  --  probability of normal z value

        Adapted from a polynomial approximation in:
                Ibbetson D, Algorithm 209
                Collected Algorithms of the CACM 1963 p. 616
        Note:
                This routine has six digit accuracy, so it is only useful for absolute
                z values <= 6.  For z values > to 6.0, poz() returns 0.0.
     * @param z 
     * @return 
    */
    public static double poz( double z) {
        double y, x, w;
        
        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y > (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                         - 0.001075204047) * w + 0.005198775019) * w
                         - 0.019198292004) * w + 0.059054035642) * w
                         - 0.151968751364) * w + 0.319152932694) * w
                         - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                               + 0.000152529290) * y - 0.000019538132) * y
                               - 0.000676904986) * y + 0.001390604284) * y
                               - 0.000794620820) * y - 0.002034254874) * y
                               + 0.006549791214) * y - 0.010557625006) * y
                               + 0.011630447319) * y - 0.009279453341) * y
                               + 0.005353579108) * y - 0.002141268741) * y
                               + 0.000535310849) * y + 0.999936657524;
            }
        }
        return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
    }


}
