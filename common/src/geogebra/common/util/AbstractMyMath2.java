package geogebra.common.util;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.NumberValue;




public class AbstractMyMath2 {
	public double gammaIncomplete(double a, double x, AbstractKernel kernel) {

			return Double.NaN;

	}
	
	public double gammaIncompleteRegularized(double a, double x) {
		return Double.NaN;
	}
	
	 public double beta(double a, double b) {

		 return Double.NaN;

	}

	 public double betaIncomplete(double a, double b, double x) {

		 return Double.NaN;

	}

	 public double betaIncompleteRegularized(double a, double b,
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
	 public double factorial(double x) {

		 return Double.NaN;
	}

	
	 public double gamma(double x, AbstractKernel kernel) {
		 return Double.NaN;
	}

	 public double erf(double mean, double standardDeviation,
				double x) {
			 return Double.NaN;
		}

	 public double psi(double x) {
			 return Double.NaN;
		}
	 
	 public double polyGamma(NumberValue order, double x) {
			return Double.NaN;
		}

	public double logGamma(double d){
		return Double.NaN;
	}


}
