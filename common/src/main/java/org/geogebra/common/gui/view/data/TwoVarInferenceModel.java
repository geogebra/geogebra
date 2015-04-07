package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.inference.TTestImpl;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;

public class TwoVarInferenceModel {
	public interface TwoVarInferenceListener {

		void setStatTable(int row, String[] rowNames, int length, String[] columnNames);

		void setFormattedValueAt(double value, int row, int col);

		GeoList getDataSelected();

		int getSelectedDataIndex(int idx);

		double[] getValueArray(GeoList list);

		void addAltHypItem(String name, String tail, double value);

		void selectAltHyp(int idx);
		
	}
	
	public interface UpdatePanel {
		void updatePanel();
	}
	
	private static final long serialVersionUID = 1L;
	private App app;
	
	private int selectedInference = StatisticsModel.INFER_TINT_2MEANS;

	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;
	private String tail = tail_two;

	// input fields
	private double confLevel = .95, hypMean = 0;

	// statistics
	double t, P, df, lower, upper, mean, se, me, n1, n2, diffMeans, mean1,
			mean2;
	private TTestImpl tTestImpl;
	private TDistributionImpl tDist;
	private boolean pooled = false;
	private double meanDifference;

	private TwoVarInferenceListener listener;
	/**
	 * Construct a TwoVarInference panel
	 */
	public TwoVarInferenceModel(App app, TwoVarInferenceListener listener) {
		this.app = app;
		this.listener = listener;
	}


	public boolean isPairedData() {
		return (selectedInference == StatisticsModel.INFER_TINT_PAIRED || selectedInference == StatisticsModel.INFER_TTEST_PAIRED);
	}

	public String getNullHypName() {

		if (selectedInference == StatisticsModel.INFER_TTEST_2MEANS) {
			return app.getMenu("DifferenceOfMeans.short");
		}
		else if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED) {
			return app.getMenu("MeanDifference");
		}
		else {
			return "";
		}

	}
	
	public boolean isTest() {
		return (selectedInference == StatisticsModel.INFER_TTEST_2MEANS || selectedInference == StatisticsModel.INFER_TTEST_PAIRED);
	}

	public void setResults() {

		ArrayList<String> list = new ArrayList<String>();

		switch (selectedInference) {
		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED)
				list.add(app.getMenu("MeanDifference"));
			else
				list.add(app.getPlain("fncInspector.Difference"));

			list.add(app.getMenu("PValue"));
			list.add(app.getMenu("TStatistic"));
			list.add(app.getMenu("StandardError.short"));
			list.add(app.getMenu("DegreesOfFreedom.short"));
			break;

		case StatisticsModel.INFER_TINT_2MEANS:
		case StatisticsModel.INFER_TINT_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TINT_PAIRED)
				list.add(app.getMenu("MeanDifference"));
			else
				list.add(app.getPlain("fncInspector.Difference"));

			list.add(app.getMenu("MarginOfError.short"));
			list.add(app.getMenu("LowerLimit"));
			list.add(app.getMenu("UpperLimit"));
			list.add(app.getMenu("StandardError.short"));
			list.add(app.getMenu("DegreesOfFreedom.short"));
			break;
		}

		String[] columnNames = new String[list.size()];
		list.toArray(columnNames);
		listener.setStatTable(1, null, columnNames.length, columnNames);

	}

	public void updateResults() {

		boolean ok = evaluate();

		if (!ok) {
			return;
		}

		switch (selectedInference) {
		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED)
				listener.setFormattedValueAt(meanDifference, 0, 0);
			else
				listener.setFormattedValueAt(diffMeans, 0, 0);

			listener.setFormattedValueAt(P, 0, 1);
			listener.setFormattedValueAt(t, 0, 2);
			listener.setFormattedValueAt(se, 0, 3);
			listener.setFormattedValueAt(df, 0, 4);
			break;

		case StatisticsModel.INFER_TINT_2MEANS:
		case StatisticsModel.INFER_TINT_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TINT_PAIRED)
				listener.setFormattedValueAt(meanDifference, 0, 0);
			else
				listener.setFormattedValueAt(diffMeans, 0, 0);

			listener.setFormattedValueAt(me, 0, 1);
			listener.setFormattedValueAt(lower, 0, 2);
			listener.setFormattedValueAt(upper, 0, 3);
			listener.setFormattedValueAt(se, 0, 4);
			listener.setFormattedValueAt(df, 0, 5);

			break;
		}

	}


	// ============================================================
	// Evaluate
	// ============================================================

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
		if (isPairedData() && stats1.getN() != stats2.getN())
			return false;

		if (tTestImpl == null)
			tTestImpl = new TTestImpl();
		double tCritical;

		try {

			switch (selectedInference) {
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
				} else
					se = Math.sqrt((v1 / n1) + (v2 / n2));

				// get confidence interval
				tDist = new TDistributionImpl(df);
				tCritical = tDist
						.inverseCumulativeProbability((getConfLevel() + 1d) / 2);
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

				tDist = new TDistributionImpl(df);
				tCritical = tDist
						.inverseCumulativeProbability((getConfLevel() + 1d) / 2);
				me = tCritical * se;
				upper = meanDifference + me;
				lower = meanDifference - me;

				// get test results
				t = meanDifference / se;
				P = 2.0 * tDist.cumulativeProbability(-Math.abs(t));
				P = adjustedPValue(P, t, tail);

				break;
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (MathException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	// TODO: Validate !!!!!!!!!!!

	public double adjustedPValue(double p, double testStatistic, String tail) {

		// two sided test
		if (tail.equals(tail_two))
			return p;

		// one sided test
		else if ((tail.equals(tail_right) && testStatistic > 0)
				|| (tail.equals(tail_left) && testStatistic < 0))
			return p / 2;
		else
			return 1 - p / 2;
	}

	/**
	 * Computes approximate degrees of freedom for 2-sample t-estimate. (code
	 * from Apache commons, TTestImpl class)
	 * 
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param n1
	 *            first sample n
	 * @param n2
	 *            second sample n
	 * @return approximate degrees of freedom
	 */
	public double getDegreeOfFreedom(double v1, double v2, double n1,
			double n2, boolean pooled) {

		if (pooled)
			return n1 + n2 - 2;

		else
			return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2)))
					/ ((v1 * v1) / (n1 * n1 * (n1 - 1d)) + (v2 * v2)
							/ (n2 * n2 * (n2 - 1d)));
	}

	/**
	 * Computes margin of error for 2-sample t-estimate; this is the half-width
	 * of the confidence interval
	 * 
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param n1
	 *            first sample n
	 * @param n2
	 *            second sample n
	 * @param confLevel
	 *            confidence level
	 * @return margin of error for 2 mean interval estimate
	 * @throws MathException
	 */
	public double getMarginOfError(double v1, double n1, double v2, double n2,
			double confLevel, boolean pooled) throws MathException {

		if (pooled) {

			double pooledVariance = ((n1 - 1) * v1 + (n2 - 1) * v2)
					/ (n1 + n2 - 2);
			double se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
			tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2,
					pooled));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d) / 2);
			return a * se;

		} else {

			double se = Math.sqrt((v1 / n1) + (v2 / n2));
			tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2,
					pooled));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d) / 2);
			return a * se;
		}

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


	public void setConfLevel(double confLevel) {
		this.confLevel = confLevel;
	}


	public boolean isPooled() {
		return pooled;
	}


	public void applyTail(int idx) {
		if (idx == 0) {
			tail = tail_right;
		}
		else if (idx == 1) {
			tail = tail_left;
		}
		else {
			tail = tail_two;
		}
		updateResults();
	}
	public void setPooled(boolean pooled) {
		this.pooled = pooled;
	}

	public void fillAlternateHyp() {
		String nullHypName = getNullHypName();
		listener.addAltHypItem(nullHypName, tail_right, hypMean);
		listener.addAltHypItem(nullHypName, tail_left, hypMean);
		listener.addAltHypItem(nullHypName, tail_two, hypMean);
			if (tail == tail_right) {
			listener.selectAltHyp(0);
		} else if (tail == tail_left) {
			listener.selectAltHyp(0);
		} else {
			listener.selectAltHyp(2);
			
		}	
	}
}
