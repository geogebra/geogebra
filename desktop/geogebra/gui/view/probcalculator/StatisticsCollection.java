package geogebra.gui.view.probcalculator;

import geogebra.common.kernel.Construction;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCollection {

	private Construction cons;

	public double mean, mean2, sd, sd2, nullHyp, me, lower, upper, se,
			testStat, P, df, level, n, n2, count, count2, total;

	public String tail;
	public boolean pooled;

	public String[][] chiSquareData;
	public int rows, columns;

	double[][] observed, expected, diff;
	double[] columnSum, rowSum;

	/**
	 * Construct StatisticsCollection
	 */
	StatisticsCollection() {

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

		n = Math.round(n);
		n2 = Math.round(n2);

		if (level < 0 || level > 1) {
			level = Double.NaN;
		}

	}

}
