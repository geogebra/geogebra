package org.geogebra.common.kernel.advanced;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * @author michael
 * 
 *         returns derivative of the fundamental formula with respect to rate
 * 
 *         f(rate) = pmt * (1 + rate * pmtType) * ((1 + rate)^n - 1) / (rate) +
 *         pv * (1 + rate)^n + fv
 *
 */
public class RateFunctionDerivative implements UnivariateRealFunction {

	private double n;
	private double pv;
	private double pmt;
	private double pmtType;

	/**
	 * @param n
	 *            number of payments
	 * @param pv
	 *            Present Value
	 * @param fv
	 *            Future Value
	 * @param pmt
	 *            Payment
	 * @param pmtType
	 *            0 or 1
	 */
	public RateFunctionDerivative(double n, double pv, double fv, double pmt,
			double pmtType) {
		this.n = n;
		this.pv = pv;
		this.pmt = pmt;
		this.pmtType = pmtType;
	}

	public double value(double x) throws FunctionEvaluationException {
		double a = pv;
		// double n = n;
		double b = pmt;
		double c = pmtType;

		// Using the above constants the fundamental formula is:
		// f = a(1 + x)^n + b(1 + cx)((1 + x)^n - 1) / x + d;
		// The derivative of f was found by a CAS and is computed below.

		// fill in the "hole" in the function
		if (x == 0) {
			// Limit[((b (c n x^2 (x + 1)^n + n x (x + 1)^n - x (x + 1)^n - (x +
			// 1)^n + x + 1) + a n x^2 (x + 1)^n) / (x^2 (x + 1))),0]
			// ((1 / 2 * b) * n^(2)) + ((b * c) * n) + (a * n) - ((1 / 2 * b) *
			// n)
			return ((1 / 2 * b) * n * n) + ((b * c) * n) + (a * n)
					- ((1 / 2 * b) * n);
		}

		double p = Math.pow(1 + x, n);
		double x2 = x * x;
		double xp = x * p;

		double num = a * n * x2 * p
				+ b * (c * n * x2 * p + n * xp - xp - p + x + 1);

		return num / (x2 * (x + 1));

	}

}
