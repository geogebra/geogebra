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
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.TableAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.DoubleUtil;

/**
 * FrequencyTable[] algorithm based on AlgoFrequency
 * 
 * @author Zbynek Konecny
 * 
 */
public class AlgoFrequencyTable extends AlgoElement implements TableAlgo {

	private enum InputType {
		STANDARD, HISTOGRAM, BARCHART
	}

	private InputType type;

	private GeoList dataList; // input
	private GeoList classList; // input
	private GeoBoolean isCumulative; // input
	private GeoBoolean useDensity; // input
	private GeoNumeric density; // input
	private GeoNumeric scale; // input
	private GeoNumeric chart; // input

	// private GeoList frequency; //output
	private GeoText table; // output
	// for compute
	private AlgoFrequency freq;

	private String[] strHeader = null;
	private String[] strValue = null;
	private String[] strFrequency = null;

	private StringBuilder sb = new StringBuilder();

	private void createTable() {
		table = new GeoText(cons);
		table.setAbsoluteScreenLoc(0, 0);
		table.setAbsoluteScreenLocActive(true);
	}

	/**
	 * @param cons
	 *            construction
	 * @param isCumulative
	 *            cumulative?
	 * @param classList
	 *            class boundaries
	 * @param dataList
	 *            data
	 */
	public AlgoFrequencyTable(Construction cons,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList) {
		this(cons, isCumulative, classList, dataList, null, null, null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param isCumulative
	 *            cumulative?
	 * @param classList
	 *            class boundaries
	 * @param dataList
	 *            data
	 * @param scale
	 *            scale factor
	 */
	public AlgoFrequencyTable(Construction cons,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList,
			GeoNumeric scale) {
		this(cons, isCumulative, classList, dataList, null, null, scale);
	}

	/**
	 * @param cons
	 *            construction
	 * @param isCumulative
	 *            cumulative?
	 * @param classList
	 *            class boundaries
	 * @param dataList
	 *            data
	 * @param useDensity
	 *            use density?
	 * @param density
	 *            density
	 */
	public AlgoFrequencyTable(Construction cons,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric density) {
		this(cons, isCumulative, classList, dataList, useDensity,
				density, null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param isCumulative
	 *            cumulative?
	 * @param classList
	 *            class boundaries
	 * @param dataList
	 *            data
	 * @param useDensity
	 *            use density?
	 * @param density
	 *            density
	 * @param scale
	 *            scale factor
	 */
	public AlgoFrequencyTable(Construction cons, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity,
			GeoNumeric density, GeoNumeric scale) {
		super(cons);

		this.classList = classList;
		this.dataList = dataList;
		this.isCumulative = isCumulative;
		this.useDensity = useDensity;
		this.density = density;
		this.scale = scale;
		freq = new AlgoFrequency(cons, isCumulative, classList, dataList,
				useDensity, density, scale);
		cons.removeFromConstructionList(freq);
		createTable();

		type = InputType.STANDARD;
		setInputOutput();

		compute();
		table.isTextCommand = true;
		table.setLaTeX(true, false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param chart
	 *            barchart or histogram
	 */
	public AlgoFrequencyTable(Construction cons, GeoNumeric chart) {
		super(cons);

		AlgoElement algo = chart.getParentAlgorithm();

		if (algo instanceof AlgoHistogram) {
			type = InputType.HISTOGRAM;
		} else {
			type = InputType.BARCHART;
		}

		this.chart = chart;

		createTable();
		setInputOutput();
		compute();
		table.isTextCommand = true;
		table.setLaTeX(true, false);
	}

	@Override
	public Commands getClassName() {
		return Commands.FrequencyTable;
	}

	@Override
	protected void setInputOutput() {

		switch (type) {
		case HISTOGRAM:
		case BARCHART:
			input = new GeoElement[1];
			input[0] = chart;
			break;

		case STANDARD:

			ArrayList<GeoElement> tempList = new ArrayList<>();

			if (isCumulative != null) {
				tempList.add(isCumulative);
			}

			if (classList != null) {
				tempList.add(classList);
			}

			tempList.add(dataList);

			if (useDensity != null) {
				tempList.add(useDensity);
			}

			if (density != null) {
				tempList.add(density);
			}

			if (scale != null) {
				tempList.add(scale);
			}

			input = new GeoElement[tempList.size()];
			input = tempList.toArray(input);
			break;
		}

		setOnlyOutput(table);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return frequency table
	 */
	public GeoText getResult() {
		return table;
	}

	/**
	 * @return LaTeX encoded table
	 */
	public String[] getValueString() {
		if (!table.isDefined()) {
			return null;
		}
		return strValue;
	}

	/**
	 * @return frequency
	 */
	public String[] getFrequencyString() {
		if (!table.isDefined()) {
			return null;
		}
		return strFrequency;
	}

	/**
	 * @return header
	 */
	public String[] getHeaderString() {
		if (!table.isDefined()) {
			return null;
		}
		return strHeader;
	}

	@Override
	public final void compute() {

		switch (type) {
		case HISTOGRAM:
			AlgoHistogram algoHistogram = (AlgoHistogram) chart
					.getParentAlgorithm();
			if (algoHistogram == null || algoHistogram.getLeftBorder() == null
					|| algoHistogram.getValues() == null) {
				table.setUndefined();
				return;
			}

			strHeader = new String[2];
			strHeader[0] = getLoc().getMenu("Interval");
			if (algoHistogram.getUseDensityGeo() != null
					&& ((GeoBoolean) algoHistogram.getUseDensityGeo())
							.getBoolean()) {
				strHeader[1] = getLoc().getMenu("Frequency");
			} else {
				strHeader[1] = getLoc().getMenu("Count");
			}

			double[] leftBorder = algoHistogram.getLeftBorder();
			double[] f = algoHistogram.getValues();
			strValue = new String[f.length];
			strFrequency = new String[f.length];
			for (int i = 0; i < f.length; i++) {
				strValue[i] = kernel.format(leftBorder[i],
						table.getStringTemplate());
				strFrequency[i] = kernel.format(f[i],
						table.getStringTemplate());
			}

			createLaTeXTable(true);
			break;

		case BARCHART:
			AlgoBarChart algoBarChart = (AlgoBarChart) chart
					.getParentAlgorithm();
			if (algoBarChart == null || algoBarChart.getValue() == null
					|| algoBarChart.getYValue() == null) {
				table.setUndefined();
				return;
			}
			strHeader = new String[2];
			strHeader[0] = getLoc().getMenu("Value");
			strHeader[1] = getLoc().getMenu("Count");

			strValue = algoBarChart.getValue();
			double[] f2 = algoBarChart.getYValue();
			strFrequency = new String[f2.length];
			for (int i = 0; i < f2.length; i++) {
				strFrequency[i] = kernel.format(f2[i],
						table.getStringTemplate());
			}

			createLaTeXTable(false);
			break;

		case STANDARD:

			// validate input arguments
			if (!freq.getResult().isDefined()) {
				table.setUndefined();
				return;
			}

			boolean useDens = useDensity != null && useDensity.getBoolean();
			GeoList fr = freq.getResult();
			int length = fr.size();

			// If classList does not exist,
			// get the unique value list and compute frequencies for this list
			if (classList == null) {

				if (scale != null) {
					useDens = true; // we assume this will be used to compute
									// frequencies
				}

				strHeader = new String[2];
				strHeader[0] = getLoc().getMenu("Value");
				if (useDens) {
					if (scale != null) {
						double scaleValue = scale.getDouble();
						if (DoubleUtil.isEqual(scaleValue, 1.0)) {
							strHeader[1] = getLoc()
									.getMenuDefault("FrequencyTable.Count",
											"Frequency");
						} else if (DoubleUtil.isEqual(scaleValue * dataList.size(),
								1)) {
							strHeader[1] = getLoc().getMenuDefault(
									"FrequencyTable.RelativeFrequency",
									"Relative Frequency");
						} else {
							strHeader[1] = getLoc()
									.getMenuDefault("FrequencyTable.Frequency",
											"Frequency");
						}
					} else {
						strHeader[1] = getLoc()
								.getMenuDefault("FrequencyTable.Frequency",
										"Frequency");
					}
				} else {
					strHeader[1] = getLoc().getMenuDefault(
							"FrequencyTable.Count", "Frequency");
				}

				strValue = new String[length];
				strFrequency = new String[length];
				GeoList va = freq.getValue();

				for (int i = 0; i < length; i++) {
					strValue[i] = va.get(i)
							.toValueString(table.getStringTemplate());
					strFrequency[i] = fr.get(i)
							.toValueString(table.getStringTemplate());
				}
				createLaTeXTable(false);
			}

			// If classList exists, compute frequencies using the classList
			else {
				if (!classList.isDefined()) {
					table.setUndefined();
					return;
				}

				strHeader = new String[2];
				strHeader[0] = getLoc().getMenu("Interval");
				strHeader[1] = useDens ? getLoc().getMenu("Frequency")
						: getLoc().getMenu("Count");

				strValue = new String[length + 1];
				strFrequency = new String[length + 1];
				for (int i = 0; i < length; i++) {
					strValue[i] = classList.get(i)
							.toValueString(table.getStringTemplate());
					strFrequency[i] = fr.get(i)
							.toValueString(table.getStringTemplate());
				}
				// include final class limit
				strValue[length] = classList.get(length)
						.toValueString(table.getStringTemplate());

				createLaTeXTable(true);
			}

			break;
		}

		table.setTextString(sb.toString());
	}

	private void createLaTeXTable(boolean useClassList) {

		sb.setLength(0);
		sb.append("\\begin{array}{c|c}");

		sb.append(strHeader[0]);
		sb.append("&\\text{");
		sb.append(strHeader[1]);
		sb.append("} \\\\\\hline ");
		if (useClassList) {
			for (int i = 0; i < strFrequency.length - 1; i++) {
				sb.append(strValue[i]);
				sb.append("\\text{ -- }");
				sb.append(strValue[i + 1]);
				sb.append("&");
				sb.append(strFrequency[i]);
				sb.append("\\\\");
			}
		} else {
			for (int i = 0; i < strFrequency.length; i++) {
				sb.append(strValue[i]);
				sb.append("&");
				sb.append(strFrequency[i]);
				sb.append("\\\\");
			}
		}
		sb.append("\\end{array}");
	}

}
