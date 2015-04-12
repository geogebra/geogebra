package org.geogebra.common.kernel.advanced;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Computes values corresponding to Excel's financial functions Rate, Nper, PMT,
 * PV and FV for problems involving compound interest with periodic payments.
 * Results are found by solving this fundamental formula: <br>
 * <br>
 * pv * (1-rate)^n + pmt(1+rate * pmtType) * ((1+rate)^n - 1))/rate + fv = 0 <br>
 * If rate is 0, then: (pmt * nper) + pv + fv = 0 <br>
 * <br>
 * rate = interest rate for a compounding period <br>
 * n = number of periods (nper) <br>
 * pmt = payment amount made each period <br>
 * pv = present value <br>
 * fv = future value <br>
 * pmtType specifies whether payments are due at the beginning (pmtType = 0) or
 * end of each period (pmtType = 1). <br>
 * <br>
 * The formula (from Excel's documentation) is solved directly for the values
 * pmt, nper, pv and fv. Rate cannot be found directly and is found instead by
 * an iterative method.
 * 
 * @author G. Sturr
 * 
 */

public class AlgoFinancial extends AlgoElement {

	public enum CalculationType {
		RATE, NPER, PMT, PV, FV
	};

	// input
	private GeoNumeric geoRate, geoNper, geoPmt, geoPV, geoFV, geoPmtType,
			geoGuess;

	// output
	private GeoNumeric result;

	// compute
	private CalculationType calcType;
	private double rate, nper, pmt, pv, fv, pmtType, guess;

	/**
	 * @param cons
	 * @param label
	 * @param rate
	 * @param nper
	 * @param pmt
	 * @param pv
	 * @param fv
	 * @param pmtType
	 * @param calcType
	 */
	public AlgoFinancial(Construction cons, String label, GeoNumeric rate,
			GeoNumeric nper, GeoNumeric pmt, GeoNumeric pv, GeoNumeric fv,
			GeoNumeric pmtType, CalculationType calcType) {
		this(cons, label, rate, nper, pmt, pv, fv, pmtType, null, calcType);
	}

	/**
	 * @param cons
	 * @param label
	 * @param rate
	 * @param nper
	 * @param pmt
	 * @param pv
	 * @param fv
	 * @param pmtType
	 * @param guess
	 * @param calcType
	 */
	public AlgoFinancial(Construction cons, String label, GeoNumeric rate,
			GeoNumeric nper, GeoNumeric pmt, GeoNumeric pv, GeoNumeric fv,
			GeoNumeric pmtType, GeoNumeric guess, CalculationType calcType) {

		super(cons);
		this.geoRate = rate;
		this.geoNper = nper;
		this.geoPmt = pmt;
		this.geoPV = pv;
		this.geoFV = fv;
		this.geoPmtType = pmtType;
		this.geoGuess = guess;
		this.calcType = calcType;

		result = new GeoNumeric(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		switch (calcType) {
		case RATE:
			return Commands.Rate;
		case NPER:
			return Commands.Periods;
		case PMT:
			return Commands.Payment;
		case PV:
			return Commands.PresentValue;
		case FV:
			return Commands.FutureValue;

		default:
			return Commands.Rate;
		}

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

		if (geoRate != null) {
			tempList.add(geoRate);
		}
		if (geoNper != null) {
			tempList.add(geoNper);
		}
		if (geoPmt != null) {
			tempList.add(geoPmt);
		}
		if (geoPV != null) {
			tempList.add(geoPV);
		}
		if (geoFV != null) {
			tempList.add(geoFV);
		}
		if (geoPmtType != null) {
			tempList.add(geoPmtType);
		}

		input = new GeoElement[tempList.size()];
		input = tempList.toArray(input);

		setOutputLength(1);
		setOutput(0, result);
		setDependencies(); // done by AlgoElement

	}

	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {
		switch (calcType) {

		case RATE:
			if (!(setNper() && setPmt() && setPV() && setFV() && setPmtType() && setGuess())) {
				result.setUndefined();
				return;
			}
			if (computeRate()) {
				result.setValue(rate);
			} else {
				result.setUndefined();
			}
			break;

		case NPER:
			if (!(setRate() && setPmt() && setPV() && setFV() && setPmtType())) {
				result.setUndefined();
				return;
			}
			if (rate == 0) {
				nper = -(pv + fv) / pmt;
			} else {
				double pmt2 = pmt * (1 + rate * pmtType);
				nper = Math.log((pmt2 - rate * fv) / (pmt2 + rate * pv))
						/ Math.log(1 + rate);
			}
			result.setValue(nper);
			break;

		case PMT:
			if (!(setRate() && setNper() && setPV() && setFV() && setPmtType())) {
				result.setUndefined();
				return;
			}
			if (rate == 0) {
				pmt = -(pv + fv) / nper;
			} else {
				pmt = (-fv - pv * Math.pow(1 + rate, nper)) / pmtFactor();
			}
			result.setValue(pmt);
			break;

		case PV:
			if (!(setRate() && setNper() && setPmt() && setFV() && setPmtType())) {
				result.setUndefined();
				return;
			}
			if (rate == 0) {
				pv = -pmt * nper - fv;
			} else {
				pv = (-fv - pmt * pmtFactor()) / Math.pow(1 + rate, nper);
			}
			result.setValue(pv);
			break;

		case FV:
			if (!(setRate() && setNper() && setPmt() && setPV() && setPmtType())) {
				result.setUndefined();
				return;
			}
			if (rate == 0) {
				fv = -pmt * nper - pv;
			} else {
				fv = -pmt * pmtFactor() - pv * Math.pow(1 + rate, nper);
			}
			result.setValue(fv);
			break;
		}

		// debug();

	}

	// ================================================
	// Utility functions for compute
	// ================================================

	private double pmtFactor() {
		return (1 + rate * pmtType) * (Math.pow(1 + rate, nper) - 1) / (rate);
	}

	/**
	 * Uses Newton's method to find rate.
	 * 
	 * TODO Guard against values of guess that are nearly zero.
	 * 
	 * @return
	 */
	private boolean computeRate() {

		int maxIterations = 20; // Excel's max
		double y, yPrime;
		rate = guess;

		for (int i = 0; i < maxIterations; i++) {

			y = f(rate);
			if (Math.abs(y) < Kernel.STANDARD_PRECISION) {
				return true;
			}

			yPrime = df(rate);
			// make sure we don't have a small denominator
			if (Math.abs(yPrime) < Kernel.MAX_DOUBLE_PRECISION) {
				return false;
			}

			double rate2 = rate - (y / df(rate));
			if (Math.abs(rate2 - rate) < Kernel.STANDARD_PRECISION) {
				rate = rate2;
				return true;
			}
			rate = rate2;

		}
		return false;

	}

	/**
	 * Computes the fundamental formula as a function of rate.
	 * 
	 * @param x
	 *            the given rate
	 * @return
	 */
	private double f(double x) {
		return pv * Math.pow(1 + x, nper) + pmt * (1 + pmtType * x)
				* (Math.pow(1 + x, nper) - 1) / x + fv;
	}

	/**
	 * Computes the derivative of the fundamental formula when expressed as a
	 * function of rate.
	 * 
	 * @param x
	 *            the given rate
	 * @return
	 */
	private double df(double x) {
		double a = pv;
		double n = nper;
		double b = pmt;
		double c = pmtType;

		// Using the above constants the fundamental formula is:
		// f = a(1 + x)^n + b(1 + cx)((1 + x)^n - 1) / x + d;
		// The derivative of f was found by a CAS and is computed below.

		// TODO Optimize this result (?) Protect against division by small
		// numbers.

		double p = Math.pow(1 + x, n);
		double num = a * n * x * x * p + b
				* (c * n * x * x * p + n * x * p - x * p - p + x + 1);
		return num / (x * x * (x + 1));
	}

	// TODO. Alternate iterative method to compute rate. For reference only,
	// should be removed later. Note that the code guards against a rate guess
	// that is nearly zero.
	//
	// Code adapted from http://www.cflib.org/udf/excelRate
	@SuppressWarnings("unused")
	private boolean computeRate2() {

		double financialPrecision = 1.0e-08;
		double maxIterations = 128;

		double f = 0;
		double i;
		double y;
		double y0;
		double y1;
		double x0;
		double x1;

		rate = guess;

		if (Math.abs(rate) < financialPrecision) {
			y = pv * (1 + nper * rate) + pmt * (1 + rate * pmtType) * nper + fv;
		} else {
			f = Math.exp(nper * Math.log(1 + rate));
			y = pv * f + pmt * (1 / rate + pmtType) * (f - 1) + fv;
		}

		y0 = pv + pmt * nper + fv;
		y1 = pv * f + pmt * (1 / rate + pmtType) * (f - 1) + fv;

		i = 0.0;
		x0 = 0.0;
		x1 = rate;

		while ((Math.abs(y0 - y1) > financialPrecision) && (i < maxIterations)) {
			rate = (y1 * x0 - y0 * x1) / (y1 - y0);
			x0 = x1;
			x1 = rate;

			if (Math.abs(rate) < financialPrecision) {
				y = pv * (1 + nper * rate) + pmt * (1 + rate * pmtType) * nper
						+ fv;
			} else {
				f = Math.exp(nper * Math.log(1 + rate));
				y = pv * f + pmt * (1 / rate + pmtType) * (f - 1) + fv;
			}

			y0 = y1;
			y1 = y;
			i = i++;
		}

		return true;
	}

	// =============================================
	// Test/set parameter values
	// =============================================

	private boolean setRate() {
		if (geoRate == null || !geoRate.isDefined()) {
			return false;
		}
		rate = geoRate.evaluateDouble();
		return !Double.isNaN(rate);
	}

	private boolean setNper() {
		if (geoNper == null || !geoNper.isDefined()) {
			return false;
		}
		nper = geoNper.evaluateDouble();
		// number of periods must be positive
		return !(nper <= 0 && Double.isNaN(nper));
	}

	private boolean setPmt() {
		if (geoPmt == null || !geoPmt.isDefined()) {
			return false;
		}
		pmt = geoPmt.evaluateDouble();
		return !Double.isNaN(pmt);
	}

	private boolean setPV() {
		if (geoPV == null) {
			if (calcType == CalculationType.FV) {
				pv = 0;
				return true;
			}
			return false;
		}
		if (geoPV.isDefined()) {
			pv = geoPV.evaluateDouble();
			return !Double.isNaN(pv);
		}

		return false;
	}

	private boolean setFV() {
		if (geoFV == null) {
			fv = 0;
			return true;
		}
		if (geoFV.isDefined()) {
			fv = geoFV.evaluateDouble();
			return !Double.isNaN(fv);
		}

		return false;
	}

	private boolean setPmtType() {
		if (geoPmtType == null) {
			pmtType = 0;
			return true;
		}
		if (geoPmtType.isDefined()) {
			if (geoPmtType.getDouble() == 1 || geoPmtType.getDouble() == 0) {
				pmtType = geoPmtType.getDouble();
				return true;
			}
		}
		return false;
	}

	private boolean setGuess() {
		if (geoGuess == null) {
			guess = 0.1;
			return true;
		}
		if (geoGuess.isDefined()) {
			guess = geoGuess.evaluateDouble();
			return !Double.isNaN(guess);
		}
		return false;
	}

	// TODO Consider locusequability
}
