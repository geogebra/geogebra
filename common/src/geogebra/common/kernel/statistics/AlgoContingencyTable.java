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
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;

import java.util.ArrayList;

/**
 * FrequencyTable[] algorithm based on AlgoFrequency
 * 
 * @author Zbynek Konecny
 * 
 */
public class AlgoContingencyTable extends AlgoElement {

	private GeoList dataList1, dataList2, rowList, colList, freqMatrix; // input
	private GeoText args; // input

	private GeoText table; // output

	// for compute
	private AlgoFrequency freq;
	private StringBuilder sb = new StringBuilder();
	private boolean isRawData;

	// display option flags
	boolean showRowPercent;
	boolean showColPercent;
	boolean showTotalPercent;
	boolean showChi;
	boolean showExpected;

	/**
	 * Constructs a contingency table
	 * 
	 * @param cons
	 * @param label
	 * @param textList1
	 * @param textList2
	 */
	public AlgoContingencyTable(Construction cons, String label,
			GeoList textList1, GeoList textList2, GeoText args) {

		super(cons);

		isRawData = true;

		this.dataList2 = textList1;
		this.dataList1 = textList2;
		this.args = args;

		freq = new AlgoFrequency(cons, dataList2, dataList1, true);
		cons.removeFromConstructionList(freq);

		table = new GeoText(cons);

		setInputOutput();
		compute();

		table.isTextCommand = true;
		table.setLaTeX(true, false);
		table.setLabel(label);

	}

	/**
	 * Constructs a contingency table
	 * 
	 * @param cons
	 * @param label
	 * @param textList1
	 * @param textList2
	 */
	public AlgoContingencyTable(Construction cons, String label,
			GeoList rowList, GeoList colList, GeoList freqMatrix, GeoText args) {

		super(cons);

		isRawData = false;

		this.rowList = rowList;
		this.colList = colList;
		this.freqMatrix = freqMatrix;
		this.args = args;

		freq = new AlgoFrequency(cons, dataList2, dataList1, true);
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

		if (dataList1 != null)
			outList.add(dataList1);
		if (dataList2 != null)
			outList.add(dataList2);

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
		}
	}

	@Override
	public final void compute() {

		if (!freq.getResult().isDefined()) {
			table.setUndefined();
			return;
		}
		
		parseArgs();

		sb.setLength(0);

		String[] rowValues = freq.getContingencyRowValues();
		String[] colValues = freq.getContingencyColumnValues();
		GeoList fr = freq.getResult();

		// prepare array
		sb.append("\\begin{array}{|l");
		for (int i = 0; i < colValues.length - 1; i++) {
			sb.append("| ");
		}
		sb.append("| |}");
		sb.append(" \\\\ \\hline ");

		// first row
		sb.append(app.getMenu("Count") + "&");
		for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
			sb.append(colValues[colIndex]);
			if (colIndex < colValues.length - 1)
				sb.append("&");
		}

		if (showRowPercent) {
			sb.append("\\\\");
			sb.append(app.getMenu("Row") + " &");
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				sb.append("\\;");
				if (colIndex < colValues.length - 1)
					sb.append("&");
			}
		}
		if (showColPercent) {
			sb.append("\\\\");
			sb.append(app.getMenu("Column") + " &");
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				sb.append("\\;");
				if (colIndex < colValues.length - 1)
					sb.append("&");
			}
		}
		if (showTotalPercent) {
			sb.append("\\\\");
			sb.append(app.getMenu("Total") + " &");
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				sb.append("\\;");
				if (colIndex < colValues.length - 1)
					sb.append("&");
			}
		}

		if (showChi) {
			sb.append("\\\\");
			sb.append(app.getMenu("ChiSquareContribution") + " &");
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				sb.append("\\;");
				if (colIndex < colValues.length - 1)
					sb.append("&");
			}
		}
		if (showExpected) {
			sb.append("\\\\");
			sb.append(app.getMenu("Expected Count") + " &");
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				sb.append("\\;");
				if (colIndex < colValues.length - 1)
					sb.append("&");
			}
		}

		sb.append("\\\\");
		sb.append("\\hline ");

		// remaining rows
		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {
			sb.append(rowValues[rowIndex]);
			sb.append("&");
			GeoList rowGeo = (GeoList) fr.get(rowIndex);
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				sb.append(rowGeo.get(colIndex).toValueString(
						table.getStringTemplate()));
				if (colIndex < colValues.length - 1)
					sb.append("&");
			}

			if (showRowPercent) {
				sb.append("\\\\");
				sb.append("\\; &");
				for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
					sb.append(rowGeo.get(colIndex).toValueString(
							table.getStringTemplate()));
					if (colIndex < colValues.length - 1)
						sb.append("&");
				}
			}

			sb.append("\\\\");
			sb.append("\\hline ");
		}

		sb.append("\\end{array}");
		table.setTextString(sb.toString());
	}

	// TODO Consider locusequability

}
