package org.geogebra.common.gui.view.probcalculator;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCollection {


	public double mean, mean2, sd, sd2, nullHyp;
	public double me, lower, upper, se, testStat;
	public double P, df, level, n, n2, count, count2;
	/** output: sum of observed */
	public double total;

	public String tail;
	public boolean pooled;

	public String[][] chiSquareData;
	public int rows, columns;

	public double[][] observed, expected, diff;
	public double[] columnSum, rowSum;
	public Procedure selectedProcedure;

	/***/
	public enum Procedure {
		ZMEAN_TEST, ZMEAN2_TEST, TMEAN_TEST, TMEAN2_TEST, ZPROP_TEST, ZPROP2_TEST, ZMEAN_CI, ZMEAN2_CI, TMEAN_CI, TMEAN2_CI, ZPROP_CI, ZPROP2_CI, GOF_TEST, CHISQ_TEST
	}

	/**
	 * Construct StatisticsCollection
	 */
	public StatisticsCollection() {

		mean = Double.NaN;
		mean2 = Double.NaN;
		sd = Double.NaN;
		sd2 = Double.NaN;
		nullHyp = Double.NaN;
		me = Double.NaN;
		lower = Double.NaN;
		upper = Double.NaN;
		se = Double.NaN;
		testStat = Double.NaN;
		P = Double.NaN;
		df = Double.NaN;
		n = Double.NaN;
		n2 = Double.NaN;
		count = Double.NaN;
		count2 = Double.NaN;

		level = .95;
		selectedProcedure = Procedure.ZMEAN_TEST;
	}

	public void setChiSqData(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;

		chiSquareData = new String[rows + 2][columns + 2];
		observed = new double[rows][columns];
		expected = new double[rows][columns];
		diff = new double[rows][columns];

		columnSum = new double[columns];
		rowSum = new double[rows];
	}

	public double getProportion() {
		if (count > n) {
			return Double.NaN;
		}
		return 1.0 * count / n;
	}

	public double getProportion2() {
		if (count2 > n2) {
			return Double.NaN;
		}

		return 1.0 * count2 / n2;
	}

	public void validate() {

		if (sd < 0) {
			sd = Double.NaN;
		}
		if (sd2 < 0) {
			sd2 = Double.NaN;
		}

		if (n < 0) {
			n = Double.NaN;
		}
		if (n2 < 0) {
			n2 = Double.NaN;
		}

		if (!Double.isNaN(n)) {
			n = Math.round(n);
		}
		if (!Double.isNaN(n2)) {
			n2 = Math.round(n2);
		}

		if (level < 0 || level > 1) {
			level = Double.NaN;
		}
	}

	public void getXML(StringBuilder sb) {
		sb.append("<statisticsCollection procedure=\"");
		sb.append(selectedProcedure.name());
		add(sb, mean,"mean");
		add(sb, sd, "sd");
		add(sb, n, "n");
		add(sb, count, "count");

		add(sb, mean2,"mean2");
		add(sb, sd2,"sd2");
		add(sb, n2, "n2");
		add(sb, count2, "count2");

		add(sb, nullHyp, "nullHyp");
		add(sb, level, "level");
		sb.append("\"/>");
	}

	private void add(StringBuilder sb, double sd3, String string) {
		sb.append("\" ");
		sb.append(string);
		sb.append("=\"");
		sb.append(sd3);
	}

}
