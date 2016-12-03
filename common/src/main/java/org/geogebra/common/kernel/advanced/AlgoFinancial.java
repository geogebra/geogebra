package org.geogebra.common.kernel.advanced;

import java.util.ArrayList;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.debug.Log;

/**
 * Computes values corresponding to Excel's financial functions Rate, Nper, PMT,
 * PV and FV for problems involving compound interest with periodic payments.
 * Results are found by solving this fundamental formula: <br>
 * <br>
 * 
 * pmt * (1 + rate * pmtType) * ((1 + rate)^n - 1) / (rate) + pv * (1 + rate)^n
 * + fv = 0
 * 
 * https://support.office.com/en-us/article/PV-function-23879d31-0e02-4321-be01-
 * da16e8168cbd
 * 
 * <br>
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
 * Also see Appendix E Formulas Used p251 hp 12c platinum financial calculator
 * User Guide http://h10032.www1.hp.com/ctg/Manual/bpia5184.pdf
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
		if (geoGuess != null) {
			tempList.add(geoGuess);
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
			if (Kernel.isZero(rate)) {
				nper = Kernel.checkInteger(-(pv + fv) / pmt);
			} else {
				double pmt2 = pmt * (1 + rate * pmtType);
				nper = Kernel.checkInteger(
						Math.log((pmt2 - rate * fv)
						/ (pmt2 + rate * pv))
						/ Math.log(1 + rate));
			}

			if (nper <= 0) {
				nper = Double.NaN;
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

	}

	// ================================================
	// Utility functions for compute
	// ================================================

	private double pmtFactor() {
		return (1 + rate * pmtType) * (Math.pow(1 + rate, nper) - 1) / (rate);
	}

	/**
	 * Uses Brent's method to find rate then Newton to polish the root
	 * 
	 * adapted from AlgoRootInterval
	 * 
	 * @return true if calculation successful
	 */
	private boolean computeRate() {

		UnivariateRealSolverFactory fact = UnivariateRealSolverFactory
				.newInstance();
		UnivariateRealSolver rootFinder = fact.newBrentSolver();

		UnivariateRealSolver rootPolisher = fact.newNewtonSolver();

		RateFunction fun = new RateFunction(nper, pv, fv, pmt, pmtType);

		double min = 0;
		double max = 0.001;

		double newtonRoot = Double.NaN;

		if (geoGuess != null) {
			rate = geoGuess.getValue();

			double dx = 0.001;

			// look for sign-change around interval for Brent
			// within [-1,1]
			min = Math.max(-1, rate - dx);
			max = Math.min(1, rate + dx);
			double minSign = Math.signum(value(fun, min));
			double maxSign = Math.signum(value(fun, max));

			// sensible bound on rate (between 0 and 1)
			while (minSign == maxSign && dx < 1) {
				dx *= 2;
				min = Math.max(0, rate - dx);
				max = Math.min(1, rate + dx);
				minSign = Math.signum(value(fun, min));
				maxSign = Math.signum(value(fun, max));
			}

		} else {
			// default starting value if Brent fails
			rate = 0.1;

			// quick and dirty look for interval with sign change
			double minSign = Math.signum(value(fun, min));
			double maxSign = Math.signum(value(fun, max));

			// sensible bound on rate (1)
			while (minSign == maxSign && max < 1) {
				max += 0.05;
				maxSign = Math.signum(value(fun, max));
			}

		}

		// Brent's method (Apache 2.2)
		try {

			// App.error("min = " + min + " max = " + max);

			rate = rootFinder.solve(fun, min, max);
			// App.error("brent rate = " + rate);

		} catch (Exception e) {
			// we will still try Newton in this case
			Log.debug("problem with Brent Solver" + e.getMessage());
		}

		if (Kernel.isEqual(rate, 1) || Double.isInfinite(rate)
				|| Double.isNaN(rate)) {
			rate = 0.1;
		}

		try {
			// Log.debug("trying Newton with starting value " + rate);
			newtonRoot = rootPolisher.solve(fun, min, max, rate);
			if (Math.abs(fun.value(newtonRoot)) < Math.abs(fun.value(rate))) {
				// App.error("polished result from Newton is better: \n" + rate
				// + "\n" + newtonRoot);
				rate = newtonRoot;
			}
		} catch (Exception e) {
			Log.debug("problem with Newton: " + e.getMessage());
			return false;
		}

		return true;

	}

	// =============================================
	// Test/set parameter values
	// =============================================

	private static double value(RateFunction fun, double min) {
		try {
			return fun.value(min);
		} catch (FunctionEvaluationException e) {
			return Double.NaN;
		}
	}

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
		nper = Kernel.checkInteger(geoNper.evaluateDouble());
		// number of periods must be positive

		// check for NaN not needed as NaN > 0 returns false
		return nper > 0;
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

	
}
