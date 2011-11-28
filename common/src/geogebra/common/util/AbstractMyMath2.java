package geogebra.common.util;

import geogebra.common.kernel.AbstractKernel;



public class AbstractMyMath2 {
	public static double gammaIncomplete(double a, double x, AbstractKernel kernel) {

			return Double.NaN;

	}
	
	public static double gammaIncompleteRegularized(double a, double x) {
		return Double.NaN;
	}
	
	 public static double beta(double a, double b) {

		 return Double.NaN;

	}

	 public static double betaIncomplete(double a, double b, double x) {

		 return Double.NaN;

	}

	 public static double betaIncompleteRegularized(double a, double b,
			double x) {

		 return Double.NaN;
	}

	/**
	 * Factorial function of x. If x is an integer value x! is returned,
	 * otherwise gamma(x + 1) will be returned. For x < 0 Double.NaN is
	 * returned.
	 * @param x 
	 * @return factorial
	 */
	 public static double factorial(double x) {

		 return Double.NaN;
	}

	
	 public static double gamma(double x, AbstractKernel kernel) {
		 return Double.NaN;
	}

	 public static double erf(double mean, double standardDeviation,
			double x) {
		 return Double.NaN;
	}


}
