package org.geogebra.common.util;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.util.mathIT.Riemann;

/*
 * moved functions from MyMath as they use org.apache
 * important for minimal applets
 */
public class MyMath2 {
	final private static int CANTOR_MAX_ITERATIONS = 1000;
	private static double TMIN = 2.0;
	private static int MAXIT = 100; // Maximum number of iterations allowed.

	private static double C2sqrtPi = 1.1283791670955125738961589;
	private static int factorialTop = 4;

	private static double[] factorialTable = new double[33];

	static {
		factorialTable[0] = 1.0;
		factorialTable[1] = 1.0;
		factorialTable[2] = 2.0;
		factorialTable[3] = 6.0;
		factorialTable[4] = 24.0;
	}

	/**
	 * @param a
	 *            parameter
	 * @param x
	 *            x
	 * @return gamma(a,x)
	 */
	final public static double gammaIncomplete(double a, double x) {

		try {
			// see http://mathworld.wolfram.com/RegularizedGammaFunction.html
			// http://en.wikipedia.org/wiki/Incomplete_gamma_function#Regularized_Gamma_functions_and_Poisson_random_variables
			return Gamma.regularizedGammaP(a, x) * gamma(a);
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			return Double.NaN;
		}
	}

	/**
	 * @param a
	 *            parameter
	 * @param x
	 *            x
	 * @return gammaRegularized(a,x)
	 */
	final public static double gammaIncompleteRegularized(double a, double x) {
		try {
			return Gamma.regularizedGammaP(a, x);
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			return Double.NaN;
		}
	}

	/**
	 * @param a
	 *            real number
	 * @param b
	 *            real number
	 * @return beta(a,b)
	 */
	final public static double beta(double a, double b) {
		double ret = Math.exp(Beta.logBeta(a, b));
		if (!MyDouble.isFinite(ret)) {
			// handle negative cases
			return Gamma.gamma(a) * Gamma.gamma(b) / Gamma.gamma(a + b);
		}
		return ret;
	}

	/**
	 * @param a
	 *            parameter
	 * @param b
	 *            parameter
	 * @param x
	 *            x
	 * @return betaIncomplete(a,b,x)
	 */
	final public static double betaIncomplete(double a, double b, double x) {

		try {
			return Beta.regularizedBeta(x, a, b) * beta(a, b);
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			return Double.NaN;
		}
	}

	/**
	 * @param a
	 *            parameter
	 * @param b
	 *            parameter
	 * @param x
	 *            x
	 * @return beta(a,b,x)
	 */
	final public static double betaIncompleteRegularized(double a, double b,
			double x) {

		try {
			return Beta.regularizedBeta(x, a, b);
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			return Double.NaN;
		}
	}

	/**
	 * Factorial function of x. If x is an integer value x! is returned,
	 * otherwise gamma(x + 1) will be returned. For x < 0 Double.NaN is
	 * returned.
	 * 
	 * @param x
	 *            real number
	 * @return factorial
	 */
	final public static double factorial(double x) {

		if (x < 0 || x > 170.624) {
			// infinity, undefined is better
			return Double.NaN;
		}

		// big x or floating point x is computed using gamma function
		if (x < 0 || x > 32 || x - Math.floor(x) > 1E-10) {
			// exp of log(gamma(x+1))
			return Math.exp(Gamma.logGamma(x + 1.0));
		}

		int n = (int) x;
		int j;
		while (factorialTop < n) {
			j = factorialTop++;
			factorialTable[factorialTop] = factorialTable[j] * factorialTop;
		}
		return factorialTable[n];
	}

	/**
	 * @param x
	 *            real number
	 * @return gamma(x)
	 */
	final public static double gamma(double x) {

		// Michael Borcherds 2008-05-04
		if (x <= 0 && DoubleUtil.isEqual(x, Math.round(x))) {
			return Double.NaN; // negative integers
		}

		// added case for x<0 otherwise no results in 3rd quadrant
		if (x >= 0) {
			return Math.exp(Gamma.logGamma(x));
		}
		return -Math.PI
				/ (x * Math.exp(Gamma.logGamma(-x)) * Math.sin(Math.PI * x));
	}

	/**
	 * 
	 * http://en.wikipedia.org/wiki/Cantor_function
	 * 
	 * @param x
	 *            real number
	 * @return cantor(x) (calculated iteratively)
	 */
	final public static double cantor(double x) {
		return cantor(x, 0);
	}

	final private static double cantor(double x, double depth) {
		if (x < 0) {
			return 0;
		}

		if (x > 1) {
			return 1;
		}

		double x3 = 3 * x;

		if (0 <= x3 && x3 <= 1) {
			if (depth > CANTOR_MAX_ITERATIONS) {
				return 0.25;
			}
			return cantor(3 * x, depth + 1) / 2;
		} else if (1 < x3 && x3 < 2) {
			return 0.5;
		}

		if (depth > CANTOR_MAX_ITERATIONS) {
			return 0.75;
		}
		return (cantor(x3 - 2, depth + 1) + 1) / 2;
	}

	/**
	 * @param mean
	 *            mean
	 * @param standardDeviation
	 *            standard deviation
	 * @param x
	 *            real number
	 * @return erf(x) for given distribution
	 */
	final public static double erf(double mean, double standardDeviation,
			double x) {

		try {
			return Erf.erf((x - mean) / (standardDeviation));
		} catch (Exception ex) {
			if (x < (mean - 20 * standardDeviation)) { // JDK 1.5 blows at 38
				return -1.0d;
			} else if (x > (mean + 20 * standardDeviation)) {
				return 1.0d;
			} else {
				return Double.NaN;
			}
		}
	}

	/**
	 * Inverse of the error function Erf.
	 *
	 * Implementation: Inversion by Newton iteration of erf(x). The initial
	 * value x0 = 0. For |z| <= 0.84 (=erf(1)) at most 4 iterations are
	 * necessary.
	 * 
	 * adapted from
	 * http://www.mathematik.uni-bielefeld.de/~sillke/ALGORITHMS/special
	 * -functions/inv_erf.c (in fact needs up to about 20 in extreme cases for
	 * very small z)
	 * 
	 * @param z
	 *            z
	 * @return inverf(z)
	 */
	final public static double inverf(double z) {

		if (z > 1 || z < -1) {
			return Double.NaN;
		}

		/* f(x) = erf(x) - z */
		/* f'(x) = c*exp(-x*x) */
		/* f''(x) = -2 f'(x) */
		double c = C2sqrtPi;
		double f = -z, f1 = c;
		double q = f / f1, x = -q, x0 = 0;

		while (Math.abs(x - x0) > 1e-12 && Math.abs(f) > 1e-14) {
			/* Newton 2nd order: x <- x - f/f'(1 + f*f''/(2 f'^2)) */
			x0 = x;
			f = MyMath2.erf(x) - z;
			f1 = c * Math.exp(-x * x);
			q = f / f1;
			x -= q * (1 - x * q); /* Newton Step 2nd order */
		}

		return x;
	}

	final public static double psi(double x) {
		return Gamma.digamma(x);
	}

	final public static double logGamma(double x) {
		return Gamma.logGamma(x);
	}

	/**
	 * @param order
	 *            polynomial order
	 * @param x
	 *            real number
	 * @return polyGamma_order(x)
	 */
	final public static double polyGamma(NumberValue order, double x) {
		int o = (int) order.getDouble();
		switch (o) {
		case 0:
			return Gamma.digamma(x);
		case 1:
			return Gamma.trigamma(x);
		// case 2:
		// return PolyGamma.tetragamma(x);
		// case 3:
		// return PolyGamma.pentagamma(x);
		// default:
		// return PolyGamma.psigamma(x, o);
		default:
			return Double.NaN;
		}
	}

	private static Complex cisi(double a2) {

		int i, k;
		boolean odd;
		double a, err, fact, sign, sum, sumc, sums, t, term;
		// double him, hre, bim, bre, cim, cre, dim, dre, delim = 0, delre = 0;
		Complex h, b, c, d, del, one, two;
		one = new Complex(1, 0);
		two = new Complex(2, 0);
		t = Math.abs(a2);
		if (t == 0.0) {
			return new Complex(Double.NEGATIVE_INFINITY, 0);

		}
		if (t > TMIN) {
			b = new Complex(1, t);
			c = new Complex(1000, 0);
			d = one.divide(b);
			h = one.divide(b);
			// d=h=1/b=1/(bre+ibim)=bre-ibim/(bre^2+bim^2);

			for (i = 2; i <= MAXIT; i++) {
				a = -(i - 1) * (i - 1);
				b = b.add(two);
				// dinv = a*d+b
				// d=1/dinv; Denominators cannot be zero.
				d = one.divide(b.add(d.multiply(a)));
				// c=b+a/c
				c = b.add(one.divide(c).multiply(a));
				del = c.multiply(d);
				// del = c*d
				h = h.multiply(del);

				// AbstractApplication.debug(Math.abs(delre - 1.0)+
				// Math.abs(delim));
				if (Math.abs(del.getReal() - 1.0)
						+ Math.abs(del.getImaginary()) < Kernel.MIN_PRECISION) {
					break;
				}
			}
			// if (i > MAXIT)
			// return new Complex(Double.NaN,Double.NaN);
			// h = (cos(t)-isin(t))*h
			h = h.multiply(new Complex(Math.cos(t), -Math.sin(t)));

			return new Complex(-h.getReal(),
					Math.signum(a2) * (Kernel.PI_HALF + h.getImaginary()));
		}
		if (t < Math.sqrt(Kernel.STANDARD_PRECISION)) {
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
				if (err < Kernel.STANDARD_PRECISION) {
					break;
				}
				odd = !odd;
			}
			if (k > MAXIT) {
				return new Complex(Double.NaN, Double.NaN);
			}
		}

		return new Complex(sumc + Math.log(t) + MyDouble.EULER_GAMMA,
				Math.signum(a2) * sums);

	}

	/**
	 * Returns cosine integral of given number, for negative values returns
	 * undefined
	 * 
	 * @param a
	 *            number
	 * @return cosine integral of given number
	 */
	final public static double ci(double a) {
		if (a < 0) {
			return Double.NaN;
		}
		return cisi(a).getReal();
	}

	/**
	 * Returns sine integral of given number, for negative values returns
	 * undefined
	 * 
	 * @param a
	 *            number
	 * @return sine integral of given number
	 */
	final public static double si(double a) {
		return cisi(a).getImaginary();
	}

	/**
	 * Returns exponential integral of given number, for negative values returns
	 * undefined
	 * 
	 * @param a
	 *            number
	 * @return exponential integral of given number
	 *         http://mathworld.wolfram.com/ExponentialIntegral.html
	 */
	final public static double ei(double a) {
		double ret = MyDouble.EULER_GAMMA + Math.log(Math.abs(a)) + a;
		double add = a;
		for (int i = 2; i < MAXIT; i++) {
			add = add * a * (i - 1) / i / i;
			ret = ret + add;
		}
		return ret;
	}

	/**
	 * @param d
	 *            real number
	 * @return erf for normal distibution with mean 0 and SD=1
	 */
	public static double erf(double d) {
		return erf(0, 1, d);
	}

	/**
	 * Rieman zeta function (for reals)
	 * 
	 * @param val
	 *            argument
	 * @return rieman zeta of val
	 */
	public static double zeta(double val) {
		if (val < 0 && DoubleUtil.isInteger(val / 2)) {
			return 0;
		}

		double[] s = { val, 0 };
		return Riemann.zeta(s)[0];
	}
}
