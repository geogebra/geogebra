package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class TwoVarInferenceModel {
	private int selectedInference = StatisticsModel.INFER_TINT_2MEANS;

	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;
	private String tail = tail_two;

	// input fields
	private double confLevel = .95;
	private double hypMean = 0;

	// statistics
	double t;
	double P;
	double df;
	double lower;
	double upper;
	double se;
	double me;
	double n1;
	double n2;
	double diffMeans;
	double mean1;
	double mean2;
	private TTest tTestImpl;
	private TDistribution tDist;
	private boolean pooled = false;
	private double meanDifference;

	private TwoVarInferenceListener listener;
	private Localization loc;

	public interface TwoVarInferenceListener {

		void setStatTable(int row, String[] rowNames, int length,
				String[] columnNames);

		void setFormattedValueAt(double value, int row, int col);

		GeoList getDataSelected();

		int getSelectedDataIndex(int idx);

		double[] getValueArray(GeoList list);

		void addAltHypItem(String name, String tail, double value);

		void selectAltHyp(int idx);

	}

	/**
	 * Construct a TwoVarInference panel
	 * 
	 * @param app
	 *            application
	 * @param listener
	 *            change listener
	 */
	public TwoVarInferenceModel(App app, TwoVarInferenceListener listener) {
		this.loc = app.getLocalization();
		this.listener = listener;
	}

	/**
	 * @return whether a paired test is selected
	 */
	public boolean isPairedData() {
		return selectedInference == StatisticsModel.INFER_TINT_PAIRED
				|| selectedInference == StatisticsModel.INFER_TTEST_PAIRED;
	}

	/**
	 * @return localized hypothesis name
	 */
	public String getNullHypName() {
		if (selectedInference == StatisticsModel.INFER_TTEST_2MEANS) {
			return loc.getMenu("DifferenceOfMeans.short");
		} else if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED) {
			return loc.getMenu("MeanDifference");
		} else {
			return "";
		}
	}

	/**
	 * @return whether a t-test is selected
	 */
	public boolean isTest() {
		return selectedInference == StatisticsModel.INFER_TTEST_2MEANS
				|| selectedInference == StatisticsModel.INFER_TTEST_PAIRED;
	}

	/**
	 * Update results UI
	 */
	public void setResults() {

		ArrayList<String> list = new ArrayList<>();

		switch (selectedInference) {
		default:
			// do nothing
			break;
		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED) {
				list.add(loc.getMenu("MeanDifference"));
			} else {
				list.add(loc.getMenu("fncInspector.Difference"));
			}

			list.add(loc.getMenu("PValue"));
			list.add(loc.getMenu("TStatistic"));
			list.add(loc.getMenu("StandardError.short"));
			list.add(loc.getMenu("DegreesOfFreedom.short"));
			break;

		case StatisticsModel.INFER_TINT_2MEANS:
		case StatisticsModel.INFER_TINT_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TINT_PAIRED) {
				list.add(loc.getMenu("MeanDifference"));
			} else {
				list.add(loc.getMenu("fncInspector.Difference"));
			}

			list.add(loc.getMenu("MarginOfError.short"));
			list.add(loc.getMenu("LowerLimit"));
			list.add(loc.getMenu("UpperLimit"));
			list.add(loc.getMenu("StandardError.short"));
			list.add(loc.getMenu("DegreesOfFreedom.short"));
			break;
		}

		String[] columnNames = new String[list.size()];
		list.toArray(columnNames);
		listener.setStatTable(1, null, columnNames.length, columnNames);

	}

	/**
	 * Update results UI
	 */
	public void updateResults() {
		boolean ok = evaluate();

		if (!ok) {
			return;
		}

		switch (selectedInference) {
		default:
			// do nothing
			break;
		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED) {
				listener.setFormattedValueAt(meanDifference, 0, 0);
			} else {
				listener.setFormattedValueAt(diffMeans, 0, 0);
			}

			listener.setFormattedValueAt(P, 0, 1);
			listener.setFormattedValueAt(t, 0, 2);
			listener.setFormattedValueAt(se, 0, 3);
			listener.setFormattedValueAt(df, 0, 4);
			break;

		case StatisticsModel.INFER_TINT_2MEANS:
		case StatisticsModel.INFER_TINT_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TINT_PAIRED) {
				listener.setFormattedValueAt(meanDifference, 0, 0);
			} else {
				listener.setFormattedValueAt(diffMeans, 0, 0);
			}

			listener.setFormattedValueAt(me, 0, 1);
			listener.setFormattedValueAt(lower, 0, 2);
			listener.setFormattedValueAt(upper, 0, 3);
			listener.setFormattedValueAt(se, 0, 4);
			listener.setFormattedValueAt(df, 0, 5);

			break;
		}
	}

	/**
	 * Evaluate
	 * 
	 * @return whether evaluation was successful
	 **/
	public boolean evaluate() {

		// get the sample data

		GeoList dataCollection = listener.getDataSelected();

		GeoList dataList1 = (GeoList) dataCollection
				.get(listener.getSelectedDataIndex(0));
		double[] sample1 = listener.getValueArray(dataList1);
		SummaryStatistics stats1 = new SummaryStatistics();
		for (int i = 0; i < sample1.length; i++) {
			stats1.addValue(sample1[i]);
		}

		GeoList dataList2 = (GeoList) dataCollection
				.get(listener.getSelectedDataIndex(1));
		double[] sample2 = listener.getValueArray(dataList2);
		SummaryStatistics stats2 = new SummaryStatistics();
		for (int i = 0; i < sample2.length; i++) {
			stats2.addValue(sample2[i]);
		}

		// exit if paired data is expected and sample sizes are unequal
		if (isPairedData() && stats1.getN() != stats2.getN()) {
			return false;
		}

		if (tTestImpl == null) {
			tTestImpl = new TTest();
		}
		double tCritical;

		try {

			switch (selectedInference) {
			default:
				// do nothing
				break;
			case StatisticsModel.INFER_TTEST_2MEANS:
			case StatisticsModel.INFER_TINT_2MEANS:

				// get statistics
				mean1 = StatUtils.mean(sample1);
				mean2 = StatUtils.mean(sample2);
				diffMeans = mean1 - mean2;
				n1 = stats1.getN();
				n2 = stats2.getN();
				double v1 = stats1.getVariance();
				double v2 = stats2.getVariance();
				df = getDegreeOfFreedom(v1, v2, n1, n2, isPooled());

				if (isPooled()) {
					double pooledVariance = ((n1 - 1) * v1 + (n2 - 1) * v2)
							/ (n1 + n2 - 2);
					se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
				} else {
					se = Math.sqrt((v1 / n1) + (v2 / n2));
				}

				// get confidence interval
				tDist = new TDistribution(df);
				tCritical = tDist.inverseCumulativeProbability(
						(getConfLevel() + 1d) / 2);
				me = tCritical * se;
				upper = diffMeans + me;
				lower = diffMeans - me;

				// get test results
				if (isPooled()) {
					t = tTestImpl.homoscedasticT(sample1, sample2);
					P = tTestImpl.homoscedasticTTest(sample1, sample2);
				} else {
					t = tTestImpl.t(sample1, sample2);
					P = tTestImpl.tTest(sample1, sample2);
				}
				P = adjustedPValue(P, t, tail);

				break;

			case StatisticsModel.INFER_TTEST_PAIRED:
			case StatisticsModel.INFER_TINT_PAIRED:

				// get statistics
				n1 = sample1.length;
				meanDifference = StatUtils.meanDifference(sample1, sample2);
				se = Math.sqrt(StatUtils.varianceDifference(sample1, sample2,
						meanDifference) / n1);
				df = n1 - 1;

				tDist = new TDistribution(df);
				tCritical = tDist.inverseCumulativeProbability(
						(getConfLevel() + 1d) / 2);
				me = tCritical * se;
				upper = meanDifference + me;
				lower = meanDifference - me;

				// get test results
				t = meanDifference / se;
				P = 2.0 * tDist.cumulativeProbability(-Math.abs(t));
				P = adjustedPValue(P, t, tail);

				break;
			}

		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
			return false;
		}

		return true;
	}

	// TODO: Validate !!!!!!!!!!!

	/**
	 * @param p
	 *            input pvalue
	 * @param testStatistic
	 *            test statistic
	 * @param tail
	 *            tail type
	 * @return new p value
	 */
	public static double adjustedPValue(double p, double testStatistic,
			String tail) {

		// two sided test
		if (tail.equals(tail_two)) {
			return p;
		} else if ((tail.equals(tail_right) && testStatistic > 0)
				|| (tail.equals(tail_left) && testStatistic < 0)) {
			return p / 2;
		} else {
			return 1 - p / 2;
		}
	}

	/**
	 * Computes approximate degrees of freedom for 2-sample t-estimate. (code
	 * from Apache commons, TTest class)
	 * 
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param size1
	 *            first sample n
	 * @param size2
	 *            second sample n
	 * @param dataPooled
	 *            whether pooled data is used
	 * @return approximate degrees of freedom
	 */
	public double getDegreeOfFreedom(double v1, double v2, double size1, double size2,
			boolean dataPooled) {

		if (dataPooled) {
			return size1 + size2 - 2;
		}
		return (((v1 / size1) + (v2 / size2)) * ((v1 / size1) + (v2 / size2)))
				/ ((v1 * v1) / (size1 * size1 * (size1 - 1d))
						+ (v2 * v2) / (size2 * size2 * (size2 - 1d)));
	}

	/**
	 * Computes margin of error for 2-sample t-estimate; this is the half-width
	 * of the confidence interval
	 * 
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param size1
	 *            first sample n
	 * @param size2
	 *            second sample n
	 * @param confidenceLevel
	 *            confidence level
	 * @param dataPooled
	 *            whether data is pooled
	 * @return margin of error for 2 mean interval estimate
	 * @throws ArithmeticException
	 *             when parameters are out f range
	 */
	public double getMarginOfError(double v1, double size1, double v2, double size2,
			double confidenceLevel, boolean dataPooled) throws ArithmeticException {

		if (dataPooled) {

			double pooledVariance = ((size1 - 1) * v1 + (size2 - 1) * v2)
					/ (size1 + size2 - 2);
			double se1 = Math.sqrt(pooledVariance * (1d / size1 + 1d / size2));
			tDist = new TDistribution(
					getDegreeOfFreedom(v1, v2, size1, size2, dataPooled));
			double a = tDist.inverseCumulativeProbability((confidenceLevel + 1d) / 2);
			return a * se1;

		}
		double stdE = Math.sqrt((v1 / size1) + (v2 / size2));
		tDist = new TDistribution(
				getDegreeOfFreedom(v1, v2, size1, size2, dataPooled));
		double a = tDist.inverseCumulativeProbability((confidenceLevel + 1d) / 2);
		return a * stdE;

	}

	public void setSelectedInference(int value) {
		selectedInference = value;
	}

	public double getHypMean() {
		return hypMean;
	}

	public void setHypMean(double hypMean) {
		this.hypMean = hypMean;
	}

	public double getConfLevel() {
		return confLevel;
	}

	/**
	 * @param confLevel
	 *            confidence level
	 */
	public void setConfLevel(double confLevel) {
		this.confLevel = confLevel;
	}

	/**
	 * @return whether data is pooled
	 */
	public boolean isPooled() {
		return pooled;
	}

	/**
	 * @param idx
	 *            tail index
	 */
	public void applyTail(int idx) {
		if (idx == 0) {
			tail = tail_right;
		} else if (idx == 1) {
			tail = tail_left;
		} else {
			tail = tail_two;
		}
		updateResults();
	}

	/**
	 * @param pooled
	 *            whether data is pooled
	 */
	public void setPooled(boolean pooled) {
		this.pooled = pooled;
		updateResults();
	}

	/**
	 * Update tail setting UI
	 */
	public void fillAlternateHyp() {
		String nullHypName = getNullHypName();
		listener.addAltHypItem(nullHypName, tail_right, hypMean);
		listener.addAltHypItem(nullHypName, tail_left, hypMean);
		listener.addAltHypItem(nullHypName, tail_two, hypMean);
		if (tail_right.equals(tail)) {
			listener.selectAltHyp(0);
		} else if (tail_left.equals(tail)) {
			listener.selectAltHyp(0);
		} else {
			listener.selectAltHyp(2);

		}
	}
}
