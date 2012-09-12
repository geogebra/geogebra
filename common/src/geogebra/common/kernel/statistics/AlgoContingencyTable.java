/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.Unicode;

import java.util.ArrayList;

/**
 * ContingencyTable[] algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoContingencyTable extends AlgoElement {

	private GeoList list1, list2, rowList, colList, freqMatrix; // input
	private GeoText args; // input

	private GeoText table; // output

	// for compute
	private AlgoFrequency freq;
	private StringBuilder sb = new StringBuilder();
	private boolean isRawData;

	// display option flags
	private boolean showRowPercent, showColPercent, showTotalPercent, showChi,
			showExpected, showTest;

	/**
	 * Constructs a contingency table from raw data
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param args
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
		compute();

		table.isTextCommand = true;
		table.setLaTeX(true, false);
		table.setLabel(label);

	}

	/**
	 * Constructs a contingency table from a given frequency table
	 * 
	 * @param cons
	 * @param label
	 * @param rowList
	 * @param colList
	 * @param freqMatrix
	 * @param args
	 */
	public AlgoContingencyTable(Construction cons, String label,
			GeoList rowList, GeoList colList, GeoList freqMatrix, GeoText args) {

		super(cons);

		isRawData = false;

		this.rowList = rowList;
		this.colList = colList;
		this.freqMatrix = freqMatrix;
		this.args = args;

		freq = new AlgoFrequency(cons, list2, list1, true);
		cons.removeFromConstructionList(freq);

		table = new GeoText(cons);

		setInputOutput();
		compute();

		table.isTextCommand = true;
		table.setLaTeX(true, false);
		table.setLabel(label);

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoContingencyTable;
	}

	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> outList = new ArrayList<GeoElement>();

		if (list1 != null)
			outList.add(list1);
		if (list2 != null)
			outList.add(list2);

		if (rowList != null)
			outList.add(rowList);
		if (colList != null)
			outList.add(colList);
		if (freqMatrix != null)
			outList.add(freqMatrix);

		outList.add(args);

		input = new GeoElement[outList.size()];
		input = outList.toArray(input);

		setOutputLength(1);
		setOutput(0, table);
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

		if (args != null) {
			String optionsStr = args.getTextString();

			if (optionsStr.indexOf("+") > -1)
				showTotalPercent = true;
			if (optionsStr.indexOf("|") > -1)
				showColPercent = true;
			if (optionsStr.indexOf("-") > -1)
				showRowPercent = true;
			if (optionsStr.indexOf("k") > -1)
				showChi = true;
			if (optionsStr.indexOf("e") > -1)
				showExpected = true;
			if (optionsStr.indexOf("=") > -1)
				showTest = true;
		}
	}

	private String[] rowValues;
	private String[] colValues;
	private int[][] freqValues;
	private double[][] expected;
	private double[][] chiCont;
	int[] rowSum;
	int[] colSum;
	int totalSum;

	private void loadValues() {

		rowValues = freq.getContingencyRowValues();
		colValues = freq.getContingencyColumnValues();
		GeoList fr = freq.getResult();

		rowSum = new int[rowValues.length];
		colSum = new int[colValues.length];
		totalSum = 0;

		freqValues = new int[rowValues.length][colValues.length];
		expected = new double[rowValues.length][colValues.length];
		chiCont = new double[rowValues.length][colValues.length];

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

		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				expected[rowIndex][colIndex] = 1.0 * rowSum[rowIndex]
						* colSum[colIndex] / totalSum;
				chiCont[rowIndex][colIndex] = (freqValues[rowIndex][colIndex] - expected[rowIndex][colIndex]);
				chiCont[rowIndex][colIndex] = chiCont[rowIndex][colIndex]
						* chiCont[rowIndex][colIndex]
						/ expected[rowIndex][colIndex];
			}
		}

	}

	@Override
	public final void compute() {

		if (!freq.getResult().isDefined()) {
			table.setUndefined();
			return;
		}

		loadValues();
		parseArgs();

		sb.setLength(0);

		// prepare array
		sb.append("\\begin{array}{|l");
		for (int i = 0; i < colValues.length - 1; i++) {
			sb.append("| ");
		}
		sb.append("| || |}"); // extra column for margin
		sb.append(" \\\\ \\hline ");

		// table header
		addTableRow(sb, 0, app.getMenu("Frequency"), "colValue");
		if (showRowPercent)
			addTableRow(sb, 0, app.getPlain("RowPercent"), "blank");
		if (showColPercent)
			addTableRow(sb, 0, app.getPlain("ColumnPercent"), "blank");
		if (showTotalPercent)
			addTableRow(sb, 0, app.getPlain("TotalPercent"), "blank");
		if (showExpected)
			addTableRow(sb, 0, app.getPlain("ExpectedCount"), "blank");
		if (showChi)
			addTableRow(sb, 0, app.getPlain("ChiSquaredContribution"), "blank");

		sb.append("\\hline ");

		// remaining rows
		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {

			addTableRow(sb, rowIndex, rowValues[rowIndex], "count");
			if (showRowPercent)
				addTableRow(sb, rowIndex, null, "-");
			if (showColPercent)
				addTableRow(sb, rowIndex, null, "|");
			if (showTotalPercent)
				addTableRow(sb, rowIndex, null, "+");
			if (showExpected)
				addTableRow(sb, rowIndex, null, "e");
			if (showChi)
				addTableRow(sb, rowIndex, null, "k");

			sb.append("\\hline ");
		}
		sb.append("\\hline ");

		// table footer
		addTableRow(sb, 0, app.getMenu("Total"), "tableFooter");
		if (showRowPercent)
			addTableRow(sb, 0, null, "rowPercentFooter");
		sb.append("\\hline ");
		sb.append("\\end{array}");

		if (showTest) {

			AlgoChiSquaredTest test = new AlgoChiSquaredTest(cons,
					freq.getResult(), null);
			cons.removeFromConstructionList(test);
			GeoList result = test.getResult();

			sb.append("\\\\");
			sb.append(app.getMenu("ChiSquaredTest"));
			sb.append("\\\\");
			sb.append("\\begin{array}{| | | | |}");
			sb.append(" \\\\ \\hline ");
			sb.append(app.getMenu("DegreesOfFreedom.short") + "&" + Unicode.chi + Unicode.Superscript_2 +  "&" + app.getMenu("PValue"));
			sb.append("\\\\");
			sb.append("\\hline ");
			sb.append(app.getKernel().format((rowValues.length-1)*(colValues.length-1),
					StringTemplate.numericDefault));
			sb.append("&");
			sb.append(result.get(1).toValueString(StringTemplate.numericDefault));
			sb.append("&");
			sb.append(result.get(0).toValueString(StringTemplate.numericDefault));
			sb.append("\\\\");
			sb.append("\\hline ");
			sb.append("\\end{array}");

		}

		table.setTextString(sb.toString());
	}

	private void addTableRow(StringBuilder sb, int rowIndex, String header,
			String type) {

		double x;

		// row header
		if (header == null) {
			sb.append("\\;");
		} else {
			sb.append(header);
		}
		sb.append("&");

		// row elements
		for (int colIndex = 0; colIndex < colValues.length; colIndex++) {

			if (type.equals("blank")) {
				sb.append("\\;");

			} else if (type.equals("colValue")) {
				sb.append(colValues[colIndex]);

			} else if (type.equals("count")) {
				sb.append(freqValues[rowIndex][colIndex]);

			} else if (type.equals("-")) {
				x = 100.0 * freqValues[rowIndex][colIndex] / rowSum[rowIndex];
				sb.append(app.getKernel().format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("|")) {
				x = 100.0 * freqValues[rowIndex][colIndex] / colSum[colIndex];
				sb.append(app.getKernel().format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("+")) {
				x = 100.0 * freqValues[rowIndex][colIndex] / totalSum;
				sb.append(app.getKernel().format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("e")) {
				x = expected[rowIndex][colIndex];
				sb.append(app.getKernel().format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("k")) {
				x = chiCont[rowIndex][colIndex];
				sb.append(app.getKernel().format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("tableFooter")) {
				sb.append(colSum[colIndex]);

			} else if (type.equals("rowPercentFooter")) {
				x = 100.0 * colSum[colIndex] / totalSum;
				sb.append(app.getKernel().format(x,
						StringTemplate.numericDefault));
			}

			sb.append("&");
		}

		// margin
		if (type.equals("count")) {
			sb.append(rowSum[rowIndex]);

		} else if (type.equals("colValue")) {
			sb.append(app.getMenu("Total"));

		} else if (type.equals("|")) {
			x = 100.0 * rowSum[rowIndex] / totalSum;
			sb.append(app.getKernel().format(x, StringTemplate.numericDefault));

		} else if (type.equals("tableFooter")) {
			sb.append(totalSum);

		} else {
			sb.append("\\;");
		}

		sb.append("\\\\");
	}

	// TODO Consider locusequability

}
