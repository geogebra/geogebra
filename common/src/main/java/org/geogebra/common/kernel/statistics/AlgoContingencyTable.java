/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.TableAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * ContingencyTable[] algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoContingencyTable extends AlgoElement implements TableAlgo {
	// input
	private GeoList list1;
	private GeoList list2;
	private GeoList rowList;
	private GeoList colList;
	private GeoList freqMatrix;
	private GeoText args;
	// output
	private GeoText table;

	// for compute
	private AlgoFrequency freq;
	private StringBuilder tableSb = new StringBuilder();
	private boolean isRawData;

	private String[] rowValues;
	private String[] colValues;
	private int[][] freqValues;
	private double[][] expected;
	private double[][] chiCont;
	private int[] rowSum;
	private int[] colSum;
	private int totalSum;
	private int rowCount;
	private int colCount;
	private int lastRow;

	// display option flags
	private boolean showRowPercent;
	private boolean showColPercent;
	private boolean showTotalPercent;
	private boolean showChi;
	private boolean showExpected;
	private boolean showTest;

	/**************************************************
	 * Constructs a contingency table from raw data
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            list of first property of the datapoints
	 * @param list2
	 *            list of second property of the datapoints
	 * @param args
	 *            table style arguments
	 * 
	 */
	public AlgoContingencyTable(Construction cons, String label, GeoList list1,
			GeoList list2, GeoText args) {

		super(cons);

		isRawData = true;

		this.list1 = list1;
		this.list2 = list2;
		this.args = args;

		freq = new AlgoFrequency(cons, list1, list2, true);
		cons.removeFromConstructionList(freq);

		table = new GeoText(cons);

		setInputOutput();
		// must set isLaTex before computing, #3846
		table.isTextCommand = true;
		table.setLaTeX(true, false);

		compute();
		table.setLabel(label);
	}

	/***************************************************
	 * Constructs a contingency table from a given frequency table
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param rowList
	 *            row headings
	 * @param colList
	 *            column headings
	 * @param freqMatrix
	 *            frequency matrix
	 * @param args
	 *            table style arguments
	 */
	public AlgoContingencyTable(Construction cons, String label,
			GeoList rowList, GeoList colList, GeoList freqMatrix,
			GeoText args) {

		super(cons);

		isRawData = false;

		this.rowList = rowList;
		this.colList = colList;
		this.freqMatrix = freqMatrix;
		this.args = args;

		table = new GeoText(cons);

		setInputOutput();
		// must set isLaTex before computing, #3846
		table.isTextCommand = true;
		table.setLaTeX(true, false);
		compute();

		table.setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.ContingencyTable;
	}

	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> outList = new ArrayList<>();

		if (list1 != null) {
			outList.add(list1);
		}
		if (list2 != null) {
			outList.add(list2);
		}

		if (rowList != null) {
			outList.add(rowList);
		}
		if (colList != null) {
			outList.add(colList);
		}
		if (freqMatrix != null) {
			outList.add(freqMatrix);
		}
		if (args != null) {
			outList.add(args);
		}

		input = new GeoElement[outList.size()];
		input = outList.toArray(input);

		setOnlyOutput(table);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return table;
	}

	private void parseArgs() {

		// set defaults
		showRowPercent = false;
		showColPercent = false;
		showTotalPercent = false;
		showChi = false;
		showExpected = false;
		showTest = false;
		lastRow = 0;
		if (args != null) {
			String optionsStr = args.getTextStringSafe();
			if (optionsStr.contains("_")) {
				showRowPercent = true;
				lastRow = 1;
			}
			if (optionsStr.contains("|")) {
				showColPercent = true;
				lastRow = 2;
			}
			if (optionsStr.contains("+")) {
				showTotalPercent = true;
				lastRow = 3;
			}
			if (optionsStr.contains("e")) {
				showExpected = true;
				lastRow = 4;
			}
			if (optionsStr.contains("k")) {
				showChi = true;
				lastRow = 5;
			}
			if (optionsStr.contains("=")) {
				showTest = true;
			}
		}
	}

	/**
	 * Loads raw data from GeoLists into arrays
	 */
	private boolean loadRawDataValues() {

		if (!freq.getResult().isDefined()) {
			return false;
		}

		rowValues = freq.getContingencyRowValues();
		colValues = freq.getContingencyColumnValues();
		GeoList fr = freq.getResult();

		rowSum = new int[rowValues.length];
		colSum = new int[colValues.length];
		totalSum = 0;

		freqValues = new int[rowValues.length][colValues.length];

		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {
			GeoList rowGeo = (GeoList) fr.get(rowIndex);
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				freqValues[rowIndex][colIndex] = (int) ((GeoNumeric) rowGeo
						.get(colIndex)).getDouble();
				rowSum[rowIndex] += freqValues[rowIndex][colIndex];
				colSum[colIndex] += freqValues[rowIndex][colIndex];
				totalSum += freqValues[rowIndex][colIndex];
			}
		}

		return true;
	}

	/**
	 * Loads prepared frequencies and values from GeoLists into arrays
	 */
	private boolean loadPreparedDataValues() {

		GeoElement geo;

		if (rowList == null || colList == null || freqMatrix == null
				|| !rowList.isDefined() || !colList.isDefined()
				|| !freqMatrix.isDefined() || !freqMatrix.isMatrix()) {
			table.setUndefined();
			return false;
		}

		// TODO: reuse value arrays

		rowCount = rowList.size();
		if (freqMatrix.size() != rowCount) {
			table.setUndefined();
			return false;
		}
		colCount = colList.size();
		rowValues = new String[rowCount];
		colValues = new String[colCount];
		rowSum = new int[rowCount];
		colSum = new int[colCount];

		for (int i = 0; i < rowCount; i++) {
			geo = rowList.get(i);
			if (!geo.isGeoText()) {
				return false;
			}
			rowValues[i] = ((GeoText) geo).getTextString();
		}

		for (int i = 0; i < colCount; i++) {
			geo = colList.get(i);
			if (!geo.isGeoText()) {
				return false;
			}

			colValues[i] = ((GeoText) geo).getTextString();
		}

		freqValues = new int[rowSum.length][colValues.length];

		totalSum = 0;

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			// row element
			GeoList rowGeo = (GeoList) freqMatrix.get(rowIndex);
			for (int colIndex = 0; colIndex < colCount; colIndex++) {
				// geo element
				geo = rowGeo.get(colIndex);
				if (!geo.isGeoNumeric()) {
					return false;
				}

				freqValues[rowIndex][colIndex] = (int) ((GeoNumeric) rowGeo
						.get(colIndex)).getDouble();
				rowSum[rowIndex] += freqValues[rowIndex][colIndex];
				colSum[colIndex] += freqValues[rowIndex][colIndex];
				totalSum += freqValues[rowIndex][colIndex];
			}

		}
		return true;
	}

	/**
	 * Computes expected counts and chi-square contributions
	 */
	private void computeChiTestValues() {

		expected = new double[rowValues.length][colValues.length];
		chiCont = new double[rowValues.length][colValues.length];

		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				expected[rowIndex][colIndex] = 1.0 * rowSum[rowIndex]
						* colSum[colIndex] / totalSum;
				chiCont[rowIndex][colIndex] = (freqValues[rowIndex][colIndex]
						- expected[rowIndex][colIndex]);
				chiCont[rowIndex][colIndex] = chiCont[rowIndex][colIndex]
						* chiCont[rowIndex][colIndex]
						/ expected[rowIndex][colIndex];
			}
		}
	}

	@Override
	public final void compute() {

		boolean dataLoaded;
		if (isRawData) {
			dataLoaded = loadRawDataValues();
		} else {
			dataLoaded = loadPreparedDataValues();
		}

		if (!dataLoaded) {
			table.setUndefined();
			return;
		}

		parseArgs();
		computeChiTestValues();

		tableSb.setLength(0);

		// prepare array
		beginTable();

		// table header
		addTableRow(tableSb, -1,
				handleText(getLoc().getMenu("Frequency")), "colValue",
				lastRow == 0);

		if (showRowPercent) {
			addTableRow(tableSb, 0,
					handleText(
							getLoc().getMenuDefault("RowPercent", "Row %")),
					"blank",
					lastRow == 1);
		}
		if (showColPercent) {
			addTableRow(tableSb, 0,
					handleText(getLoc().getMenuDefault("ColumnPercent",
							"Column %")),
					"blank", lastRow == 2);
		}
		if (showTotalPercent) {
			addTableRow(tableSb, 0,
					handleText(
							getLoc().getMenuDefault("TotalPercent", "Total %")),
					"blank", lastRow == 3);
		}
		if (showExpected) {
			addTableRow(tableSb, 0,
					handleText(getLoc().getMenuDefault("ExpectedCount",
							"Expected Count")),
					"blank", lastRow == 4);
		}
		if (showChi) {
			addTableRow(tableSb, 0,
					handleText(
							getLoc().getMenuDefault("ChiSquaredContribution",
									Unicode.Chi + "" + Unicode.SUPERSCRIPT_2
											+ " Contribution")),
					"blank", lastRow == 5);
		}

		// remaining rows
		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {

			addTableRow(tableSb, rowIndex, rowValues[rowIndex], "count",
					lastRow == 0);
			if (showRowPercent) {
				addTableRow(tableSb, rowIndex, null, "_", lastRow == 1);
			}
			if (showColPercent) {
				addTableRow(tableSb, rowIndex, null, "|", lastRow == 2);
			}
			if (showTotalPercent) {
				addTableRow(tableSb, rowIndex, null, "+", lastRow == 3);
			}
			if (showExpected) {
				addTableRow(tableSb, rowIndex, null, "e", lastRow == 4);
			}
			if (showChi) {
				addTableRow(tableSb, rowIndex, null, "k", lastRow == 5);
			}

		}

		// table footer
		addTableRow(tableSb, -1, getLoc().getMenu("Total"), "tableFooter",
				!showRowPercent);
		if (showRowPercent) {
			addTableRow(tableSb, 0, null, "rowPercentFooter", true);
		}
		endTable(tableSb);

		if (showTest) {
			addChiTest(tableSb);

		}

		table.setTextString(tableSb.toString());
	}

	private static void endTable(StringBuilder sb2) {

		sb2.append("\\end{array}");

	}

	private void addChiTest(StringBuilder sb) {

		AlgoChiSquaredTest test;
		if (isRawData) {
			test = new AlgoChiSquaredTest(cons, freq.getResult(), null);
		} else {
			test = new AlgoChiSquaredTest(cons, freqMatrix, null);
		}
		cons.removeFromConstructionList(test);
		final GeoList result = test.getResult();

		String split = "&";

		sb.append("\\\\ ");

		appendText(sb,
				getLoc().getMenuDefault("ChiSquaredTest", "ChiSquared Test"));
		sb.append("\\\\");

		sb.append("\\begin{array}{|r|r|r|r|}");
		sb.append(" \\hline ");
		sb.append(getLoc().getMenuDefault("DegreesOfFreedom.short", "df"));
		sb.append(split);
		appendText(sb, Unicode.chi + "" + Unicode.SUPERSCRIPT_2);
		sb.append(split);
		appendText(sb, getLoc().getMenuDefault("PValue", "P"));

		sb.append("\\\\");
		sb.append("\\hline ");

		sb.append(kernel.format((rowValues.length - 1) * (colValues.length - 1),
				StringTemplate.numericDefault));

		sb.append(split);
		sb.append(result.get(1).toValueString(StringTemplate.numericDefault));
		sb.append(split);
		sb.append(result.get(0).toValueString(StringTemplate.numericDefault));

		sb.append("\\\\");
		sb.append("\\hline ");
		sb.append("\\end{array}");

	}

	private void beginTable() {
		tableSb.append("\\begin{array}{|l");
		for (int i = 0; i < colValues.length - 1; i++) {
			tableSb.append("|r");
		}
		tableSb.append("|r||r|}"); // extra column for margin

	}

	private void addTableRow(StringBuilder sb, int rowIndex, String header,
			String type, boolean lineBelow) {

		double x;
		startRow(sb, rowIndex == -1);
		// row header
		if (header == null) {
			sb.append("\\;");
		} else {
			appendText(sb, header);
		}
		endCell(sb);

		// row elements
		for (int colIndex = 0; colIndex < colValues.length; colIndex++) {

			if ("blank".equals(type)) {
				sb.append("\\;");

			} else if ("colValue".equals(type)) {
				appendText(sb, colValues[colIndex]);

			} else if ("count".equals(type)) {
				sb.append(freqValues[rowIndex][colIndex]);

			} else if ("_".equals(type)) {
				x = 100.0 * freqValues[rowIndex][colIndex] / rowSum[rowIndex];
				sb.append(kernel.format(x, StringTemplate.numericDefault));

			} else if ("|".equals(type)) {
				x = 100.0 * freqValues[rowIndex][colIndex] / colSum[colIndex];
				sb.append(kernel.format(x, StringTemplate.numericDefault));

			} else if ("+".equals(type)) {
				x = 100.0 * freqValues[rowIndex][colIndex] / totalSum;
				sb.append(kernel.format(x, StringTemplate.numericDefault));

			} else if ("e".equals(type)) {
				x = expected[rowIndex][colIndex];
				sb.append(kernel.format(x, StringTemplate.numericDefault));

			} else if ("k".equals(type)) {
				x = chiCont[rowIndex][colIndex];
				sb.append(kernel.format(x, StringTemplate.numericDefault));

			} else if ("tableFooter".equals(type)) {
				sb.append(colSum[colIndex]);

			} else if ("rowPercentFooter".equals(type)) {
				x = 100.0 * colSum[colIndex] / totalSum;
				sb.append(kernel.format(x, StringTemplate.numericDefault));
			}

			endCell(sb);
		}

		// margin
		if ("count".equals(type)) {
			sb.append(rowSum[rowIndex]);

		} else if ("colValue".equals(type)) {
			appendText(sb, getLoc().getMenu("Total"));

		} else if ("|".equals(type)) {
			x = 100.0 * rowSum[rowIndex] / totalSum;
			sb.append(kernel.format(x, StringTemplate.numericDefault));

		} else if ("tableFooter".equals(type)) {
			sb.append(totalSum);

		} else {
			sb.append("\\;");
		}
		endRow(sb, lineBelow);
	}

	private static void appendText(StringBuilder sb, String str) {
		sb.append("\\text{");
		sb.append(str);
		sb.append("}");
	}

	private static void startRow(StringBuilder sb, boolean lineAbove) {
		if (lineAbove) {
			sb.append("\\hline ");
		}
	}

	private static void endRow(StringBuilder sb, boolean lineBelow) {

		sb.append("\\\\");
		if (lineBelow) {
			sb.append("\\hline ");
		}

	}

	private static void endCell(StringBuilder sb) {
		sb.append("&");

	}

	private static String handleText(String s) {
		return "\\text{" + s + "}";
	}

}
