package org.geogebra.common.kernel.advanced;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * @author michael
 * 
 *         returns fundamental function
 * 
 *         f(rate) = pmt * (1 + rate * pmtType) * ((1 + rate)^n - 1) / (rate) +
 *         pv * (1 + rate)^n + fv
 *
 * 
 */
@SuppressWarnings("deprecation")
public class RateFunction implements DifferentiableUnivariateFunction {

	private double n;
	private double pv;
	private double fv;
	private double pmt;
	private double pmtType;
	private RateFunctionDerivative deriv;

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
	public RateFunction(double n, double pv, double fv, double pmt,
			double pmtType) {
		this.n = n;
		this.pv = pv;
		this.fv = fv;
		this.pmt = pmt;
		this.pmtType = pmtType;

		this.deriv = new RateFunctionDerivative(n, pv, pmt, pmtType);
	}

	@Override
	public double value(double x) {
		// fv + pmt (1 + x pmtType) ((1 + x)^n - 1) / x + pv (1 + x)^n

		// fill in the "hole" in the function
		// limit of (Math.pow(1 + x, n) - 1) / x
		// as x tends to 0
		// is n
		if (x == 0) {
			return fv + pmt * (1 + x * pmtType) * n + pv * Math.pow(1 + x, n);

		}

		return fv + pmt * (1 + x * pmtType) * (Math.pow(1 + x, n) - 1) / x
				+ pv * Math.pow(1 + x, n);
	}

	@Override
	public UnivariateFunction derivative() {
		return deriv;
	}

}
