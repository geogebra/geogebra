package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.TTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class OneVarModel {
	private TTest tTestImpl;
	private TDistribution tDist;
	private double testStat;
	private double P;
	private double df;
	private double lower;
	private double upper;
	private double mean;
	private double se;
	private double me;
	private double N;
	// input fields
	public double confLevel = .95;
	public double hypMean = 0;
	public double sigma = 1;
	// test type (tail)
	public static final String tail_left = "<";
	public static final String tail_right = ">";
	public static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;
	public String tail = tail_two;
	public int selectedPlot = StatisticsModel.INFER_TINT;

	/**
	 * Update model
	 * 
	 * @param sample
	 *            sample data
	 */
	public void evaluate(double[] sample) {
		mean = StatUtils.mean(sample);
		N = sample.length;
		NormalDistribution normalDist;
		try {
			switch (selectedPlot) {

			default:
				// do nothing
				break;
			case StatisticsModel.INFER_ZTEST:
			case StatisticsModel.INFER_ZINT:
				normalDist = new NormalDistribution(0, 1);
				se = sigma / Math.sqrt(N);
				testStat = (mean - hypMean) / se;
				P = 2.0 * normalDist.cumulativeProbability(-Math.abs(testStat));
				P = adjustedPValue(P, testStat, tail);

				double zCritical = normalDist
						.inverseCumulativeProbability((confLevel + 1d) / 2);
				me = zCritical * se;
				upper = mean + me;
				lower = mean - me;
				break;

			case StatisticsModel.INFER_TTEST:
			case StatisticsModel.INFER_TINT:
				if (tTestImpl == null) {
					tTestImpl = new TTest();
				}
				se = Math.sqrt(StatUtils.variance(sample) / N);
				df = N - 1;
				testStat = tTestImpl.t(hypMean, sample);
				P = tTestImpl.tTest(hypMean, sample);
				P = adjustedPValue(P, testStat, tail);

				tDist = new TDistribution(N - 1);
				double tCritical = tDist
						.inverseCumulativeProbability((confLevel + 1d) / 2);
				me = tCritical * se;
				upper = mean + me;
				lower = mean - me;
				break;
			}

		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
		}

	}

	private static double adjustedPValue(double p, double testStatistic,
			String tail) {

		// two sided test
		if (tail.equals(OneVarModel.tail_two)) {
			return p;
		} else if ((tail.equals(OneVarModel.tail_right) && testStatistic > 0)
				|| (tail.equals(OneVarModel.tail_left) && testStatistic < 0)) {
			return p / 2;
		} else {
			return 1 - p / 2;
		}
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param expr
	 *            input expression
	 * @return value
	 */
	public double evaluateExpression(Kernel kernel, String expr) {

		NumberValue nv;

		try {
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);
		} catch (Exception | Error e) {
			Log.debug(e);
			return Double.NaN;
		}
		return nv.getDouble();
	}

	/**
	 * @param loc
	 *            localization
	 * @return localized statistic names
	 */
	public ArrayList<String> getNameList(Localization loc) {
		ArrayList<String> nameList = new ArrayList<>();

		switch (selectedPlot) {
		default:
			// do nothing
			break;
		case StatisticsModel.INFER_ZTEST:
			nameList.add(loc.getMenu("PValue"));
			nameList.add(loc.getMenu("ZStatistic"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));

			break;

		case StatisticsModel.INFER_TTEST:
			nameList.add(loc.getMenu("PValue"));
			nameList.add(loc.getMenu("TStatistic"));
			nameList.add(loc.getMenu("DegreesOfFreedom.short"));
			nameList.add(loc.getMenu("StandardError.short"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;

		case StatisticsModel.INFER_ZINT:
			nameList.add(loc.getMenu("Interval"));
			nameList.add(loc.getMenu("LowerLimit"));
			nameList.add(loc.getMenu("UpperLimit"));
			nameList.add(loc.getMenu("MarginOfError"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;

		case StatisticsModel.INFER_TINT:
			nameList.add(loc.getMenu("Interval"));
			nameList.add(loc.getMenu("LowerLimit"));
			nameList.add(loc.getMenu("UpperLimit"));
			nameList.add(loc.getMenu("MarginOfError"));
			nameList.add(loc.getMenu("DegreesOfFreedom.short"));
			nameList.add(loc.getMenu("StandardError.short"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;
		}
		return nameList;
	}

	public double getLower() {
		return lower;
	}

	public double getUpper() {
		return upper;
	}

	public double getP() {
		return P;
	}

	public double getTestStat() {
		return testStat;
	}

	public double getDf() {
		return df;
	}

	public double getMean() {
		return mean;
	}

	public double getSe() {
		return se;
	}

	public double getMe() {
		return me;
	}

	public double getN() {
		return N;
	}

}
