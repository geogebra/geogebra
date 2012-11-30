package geogebra.gui.view.probcalculator;

import geogebra.common.kernel.Construction;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCollection {

	private Construction cons;

	public double mean, mean2, sd, sd2, nullHyp, level, me, lower, upper, se,
			testStat, P, df;
	public String tail;
	public boolean pooled;
	public int n, n2, count, count2;

	public String[][] chiSquareData;
	public int rows, columns;

	double[][] observed, expected, diff;
	double[] columnSum, rowSum;
	double total;

	/**
	 * Construct StatisticsCollection
	 */
	StatisticsCollection() {
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

}
