package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCollection {
	public static final String tail_left = "<";
	public static final String tail_right = ">";
	public static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;

	public double mean;
	public double mean2;
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
	public int rows;
	public int columns;

	public double[][] observed;
	public double[][] expected;
	public double[][] diff;
	public double[] columnSum;
	public double[] rowSum;
	private Procedure selectedProcedure;
	private boolean active;
	public boolean showExpected;
	public boolean showDiff;
	public boolean showColPercent;
	public boolean showRowPercent;

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

	/**
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns
	 */
	public void setChiSqData(int rows, int columns) {
		chiSquareData = new String[rows + 2][columns + 2];
		initComputation(rows, columns);
	}

	/**
	 * @param initRows
	 *            number of rows
	 * @param initColumns
	 *            number of columns
	 */
	public void initComputation(int initRows, int initColumns) {
		this.rows = initRows;
		this.columns = initColumns;
		observed = new double[initRows][initColumns];
		expected = new double[initRows][initColumns];
		diff = new double[initRows][initColumns];

		columnSum = new double[initColumns];
		rowSum = new double[initRows];

	}

	/**
	 * @return proportion of first sample
	 */
	public double getProportion() {
		if (count > n) {
			return Double.NaN;
		}
		return 1.0 * count / n;
	}

	/**
	 * @return proportion of second sample
	 */
	public double getProportion2() {
		if (count2 > n2) {
			return Double.NaN;
		}
		return 1.0 * count2 / n2;
	}

	/**
	 * Reset intermediate values
	 */
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
	public void getXML(XMLStringBuilder sb) {
		boolean hasChildren = chiSquareData != null && chiSquareData.length > 0;
		if (hasChildren) {
			sb.startOpeningTag("statisticsCollection", 0);
		} else {
			sb.startTag("statisticsCollection", 0);
		}
		sb.attrRaw("procedure", getSelectedProcedure().name());
		sb.attr("mean", mean);
		sb.attr("sd", sd);
		sb.attr("n", n);
		sb.attr("count", count);

		sb.attr("mean2", mean2);
		sb.attr("sd2", sd2);
		sb.attr("n2", n2);
		sb.attr("count2", count2);

		sb.attr("nullHyp", nullHyp);
		sb.attr("level", level);
		sb.attrRaw("tail", getTail());
		sb.attr("active", active);
		sb.attr("showExpected", showExpected);
		sb.attr("showDiff", showDiff);
		sb.attr("showRowPercent", showRowPercent);
		sb.attr("showColPercent", showColPercent);
		if (hasChildren) {
			// add(sb, chiSquareData.length, "columns");
			sb.attr("columns", chiSquareData[0].length);
			sb.endTag();
			for (int row = 0; row < chiSquareData.length; row++) {
				addObservedRow(sb, row);
			}
			sb.closeTag("statisticsCollection");
		} else {
			sb.endTag();
		}
	}

	private void addObservedRow(XMLStringBuilder sb, int row) {
		for (int column = 0; column < chiSquareData[0].length; column++) {
			sb.startTag("entry");
			sb.attr("val", chiSquareData[row][column] != null
					? chiSquareData[row][column] : "");
			sb.endTag();
		}
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
