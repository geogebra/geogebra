package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.util.StringUtil;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCollection {
	public static final String tail_left = "<";
	public static final String tail_right = ">";
	public static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;

	public double mean, mean2;
	public double sd;
	public double sd2;
	public double nullHyp;
	public double me;
	public double lower;
	public double upper;
	public double se;
	public double testStat;
	public double P;
	public double df;
	public double level;
	public double n;
	public double n2;
	public double count;
	public double count2;
	/** output: sum of observed */
	public double total;

	private String tail;
	public boolean pooled;

	public String[][] chiSquareData;
	public int rows, columns;

	public double[][] observed, expected, diff;
	public double[] columnSum, rowSum;
	private Procedure selectedProcedure;
	private boolean active;
	public boolean showExpected;
	public boolean showDiff;
	public boolean showColPercent;
	public boolean showRowPercent;

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
		setSelectedProcedure(Procedure.ZMEAN_TEST);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
	public void setChiSqData(int rows, int columns) {
		chiSquareData = new String[rows + 2][columns + 2];
		initComputation(rows, columns);
	}

	public void initComputation(int initRows, int initColumns) {
		this.rows = initRows;
		this.columns = initColumns;
		observed = new double[initRows][initColumns];
		expected = new double[initRows][initColumns];
		diff = new double[initRows][initColumns];

		columnSum = new double[initColumns];
		rowSum = new double[initRows];

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

	/**
	 * Add this to XML string
	 * 
	 * @param sb
	 *            string builder
	 */
	public void getXML(StringBuilder sb) {
		sb.append("<statisticsCollection procedure=\"");
		sb.append(getSelectedProcedure().name());
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
		sb.append("\" tail=\"");
		sb.append(getTail());
		add(sb, active, "active");
		add(sb, showExpected, "showExpected");
		add(sb, showDiff, "showDiff");
		add(sb, showRowPercent, "showRowPercent");
		add(sb, showColPercent, "showColPercent");
		if (chiSquareData != null && chiSquareData.length > 0) {
			// add(sb, chiSquareData.length, "columns");
			add(sb, chiSquareData[0].length, "columns");
			sb.append("\">");
			for (int row = 0; row < chiSquareData.length; row++) {
				addObservedRow(sb, row);
			}
			sb.append("</statisticsCollection>");
		} else {
			sb.append("\"/>");
		}
	}

	private void addObservedRow(StringBuilder sb, int row) {
		for (int column = 0; column < chiSquareData[0].length; column++) {
			sb.append("<entry val=\"");
			if (chiSquareData[row][column] != null) {
				sb.append(StringUtil.encodeXML(chiSquareData[row][column]));
			}
			sb.append("\"/>\n");
		}
	}

	private static void add(StringBuilder sb, double sd3, String string) {
		sb.append("\" ");
		sb.append(string);
		sb.append("=\"");
		sb.append(sd3);
	}

	private static void add(StringBuilder sb, boolean sd3, String string) {
		sb.append("\" ");
		sb.append(string);
		sb.append("=\"");
		sb.append(sd3);
	}

	public Procedure getSelectedProcedure() {
		return selectedProcedure;
	}

	public void setSelectedProcedure(Procedure selectedProcedure) {
		this.selectedProcedure = selectedProcedure;
	}

	public String getTail() {
		return tail;
	}

	/**
	 * @param tail
	 *            one of &lt;, &gt;, !=
	 */
	public void setTail(String tail) {
		if (tail_two.equals(tail) || tail_left.equals(tail)
				|| tail_right.equals(tail)) {
			this.tail = tail;
		}
	}
}
