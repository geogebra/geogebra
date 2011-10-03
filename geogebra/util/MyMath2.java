package geogebra.util;

import geogebra.kernel.Kernel;

import org.apache.commons.math.MathException;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.special.Erf;
import org.apache.commons.math.special.Gamma;

/*
 * moved functions from MyMath as they use org.apache
 * important for minimal applets
 */
public class MyMath2 {
	final public static double gammaIncomplete(double a, double x, Kernel kernel) {

		try {
			// see http://mathworld.wolfram.com/RegularizedGammaFunction.html
			// http://en.wikipedia.org/wiki/Incomplete_gamma_function#Regularized_Gamma_functions_and_Poisson_random_variables
			return Gamma.regularizedGammaP(a, x) * gamma(a, kernel);
		} catch (MathException e) {
			return Double.NaN;
		}

	}

	final public static double gammaIncompleteRegularized(double a, double x) {

		try {
			return Gamma.regularizedGammaP(a, x);
		} catch (MathException e) {
			return Double.NaN;
		}

	}

	final public static double beta(double a, double b) {

		return Math.exp(Beta.logBeta(a, b));

	}

	final public static double betaIncomplete(double a, double b, double x) {

		try {
			return Beta.regularizedBeta(x, a, b) * beta(a, b);
		} catch (MathException e) {
			return Double.NaN;
		}

	}

	final public static double betaIncompleteRegularized(double a, double b,
			double x) {

		try {
			return Beta.regularizedBeta(x, a, b);
		} catch (MathException e) {
			return Double.NaN;
		}
	}

	/**
	 * Factorial function of x. If x is an integer value x! is returned,
	 * otherwise gamma(x + 1) will be returned. For x < 0 Double.NaN is
	 * returned.
	 * @param x 
	 * @return factorial
	 */
	final public static double factorial(double x) {

		if (x < 0)
			return Double.NaN; // bugfix Michael Borcherds 2008-05-04

		// big x or floating point x is computed using gamma function
		if (x < 0 || x > 32 || x - Math.floor(x) > 1E-10)
			// exp of log(gamma(x+1))
			return Math.exp(Gamma.logGamma(x + 1.0));

		int n = (int) x;
		int j;
		while (factorialTop < n) {
			j = factorialTop++;
			factorialTable[factorialTop] = factorialTable[j] * factorialTop;
		}
		return factorialTable[n];
	}

	private static int factorialTop = 4;
	private static double[] factorialTable = new double[33];
	static {
		factorialTable[0] = 1.0;
		factorialTable[1] = 1.0;
		factorialTable[2] = 2.0;
		factorialTable[3] = 6.0;
		factorialTable[4] = 24.0;
	}

	final public static double gamma(double x, Kernel kernel) {

		// Michael Borcherds 2008-05-04
		if (x <= 0 && Kernel.isEqual(x, Math.round(x)))
			return Double.NaN; // negative integers

		// Michael Borcherds 2007-10-15 BEGIN added case for x<0 otherwise no
		// results in 3rd quadrant
		if (x >= 0)
			return Math.exp(Gamma.logGamma(x));
		else
			return -Math.PI / (x * gamma(-x, kernel) * Math.sin(Math.PI * x));
		// Michael Borcherds 2007-10-15 END
	}

	final public static double erf(double mean, double standardDeviation,
			double x) {
		try {
			return Erf.erf((x - mean) / (standardDeviation));
		} catch (Exception ex) {
			if (x < (mean - 20 * standardDeviation)) { // JDK 1.5 blows at 38
				return 0.0d;
			} else if (x > (mean + 20 * standardDeviation)) {
				return 1.0d;
			} else {
				return Double.NaN;
			}
		}
	}


}
