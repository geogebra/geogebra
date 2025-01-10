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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.Frequency;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.advanced.AlgoUnique;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;

public class AlgoFrequency extends AlgoElement {

	private GeoList dataList; // input
	private GeoList classList; // input
	private GeoBoolean isCumulative; // input
	private GeoBoolean useDensity; // input
	private GeoNumeric density; // input

	private GeoList frequency; // output

	// for compute
	private GeoList value = new GeoList(cons);
	private String[] contingencyRowValues;
	private String[] contingencyColumnValues;
	private Boolean isContingencyTable = false;
	private double scaleFactor;
	private GeoNumeric scale;

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
	public AlgoFrequency(Construction cons, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList) {
		this(cons, isCumulative, classList, dataList, null, null, null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param isCumulative
	 *            cumulative
	 * @param classList
	 *            class boundaries
	 * @param dataList
	 *            data
	 * @param scale
	 *            scale
	 */
	public AlgoFrequency(Construction cons, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoNumeric scale) {
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
	 *            whether to use density
	 * @param density
	 *            density
	 */
	public AlgoFrequency(Construction cons,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric density) {
		this(cons, isCumulative, classList, dataList, useDensity, density, null);
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
	 *            whether to use density
	 * @param density
	 *            density
	 * @param scale
	 *            scale factor
	 */
	AlgoFrequency(Construction cons, GeoBoolean isCumulative, GeoList classList,
			GeoList dataList, GeoBoolean useDensity, GeoNumeric density,
			GeoNumeric scale) {
		super(cons);

		this.classList = classList;
		this.dataList = dataList;
		this.isCumulative = isCumulative;
		this.useDensity = useDensity;
		this.density = density;
		this.scale = scale;

		frequency = new GeoList(cons);
		setInputOutput();
		compute();
	}

	/***************************************************
	 * Contingency table constructor
	 * 
	 * @param cons
	 *            construction
	 * @param list1
	 *            first property of datapoints
	 * @param list2
	 *            second property of datapoints
	 * @param isContingencyTable
	 *            (dummy variable)
	 */
	public AlgoFrequency(Construction cons, GeoList list1, GeoList list2,
			boolean isContingencyTable) {
		super(cons);

		this.isContingencyTable = isContingencyTable;
		this.classList = list1;
		this.dataList = list2;
		frequency = new GeoList(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Frequency;
	}

	@Override
	protected void setInputOutput() {

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

		setOnlyOutput(frequency);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return frequency;
	}

	public GeoList getValue() {
		return value;
	}

	public String[] getContingencyRowValues() {
		return contingencyRowValues;
	}

	public String[] getContingencyColumnValues() {
		return contingencyColumnValues;
	}

	@Override
	public final void compute() {

		if (isContingencyTable) {
			computeContingencyTable();
			return;
		}

		// Validate input arguments
		// =======================================================

		if (!dataList.isDefined() || dataList.size() == 0) {
			frequency.setUndefined();
			return;
		}

		if (!(dataList.getElementType().equals(GeoClass.TEXT)
				|| dataList.getElementType().equals(GeoClass.NUMERIC))) {
			frequency.setUndefined();
			return;
		}

		if (classList != null) {
			if (!classList.getElementType().equals(GeoClass.NUMERIC)
					|| classList.size() < 2) {
				frequency.setUndefined();
				return;
			}
		}

		if (density != null) {
			if (density.getDouble() <= 0) {
				frequency.setUndefined();
				return;
			}
		}

		if (scale != null) {
			if (!scale.isDefined()) {
				frequency.setUndefined();
				return;
			}
			scaleFactor = scale.getValue();
		}

		frequency.setDefined(true);
		frequency.clear();
		if (value != null) {
			value.clear();
		}

		double numMax = 0, numMin = 0;
		boolean doCumulative = isCumulative != null
				&& isCumulative.getBoolean();

		// Load the data into f, an instance of Frequency class
		// =======================================================

		Frequency f = new FrequencyGgb();
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.getElementType().equals(GeoClass.TEXT)) {
				f.addValue(((GeoText) dataList.get(i))
						.toValueString(StringTemplate.defaultTemplate));
			}
			if (dataList.getElementType().equals(GeoClass.NUMERIC)) {
				f.addValue(((GeoNumeric) dataList.get(i)).getDouble());
			}
		}

		// If classList does not exist,
		// get the unique value list and compute frequencies for this list
		// =======================================================

		// handle string data
		if (dataList.getElementType().equals(GeoClass.TEXT)) {

			Iterator<Comparable<?>> itr = f.valuesIterator();
			String strMax = (String) itr.next();
			String strMin = strMax;
			itr = f.valuesIterator();

			while (itr.hasNext()) {
				String s = (String) itr.next();
				if (s.compareTo(strMax) > 0) {
					strMax = s;
				}
				if (s.compareTo(strMin) < 0) {
					strMin = s;
				}
				GeoText text = new GeoText(cons);
				text.setTextString(s);
				value.add(text);
				if (classList == null) {
					if (doCumulative) {
						addValue(f.getCumFreq(s));
					} else {
						addValue(f.getCount(s));
					}
				}
			}
		}

		// handle numeric data
		else {
			Iterator<Comparable<?>> itr = f.valuesIterator();
			numMax = (Double) itr.next();
			numMin = numMax;
			itr = f.valuesIterator();

			while (itr.hasNext()) {
				Double n = (Double) itr.next();
				if (n > numMax) {
					numMax = n.doubleValue();
				}
				if (n < numMin) {
					numMin = n.doubleValue();
				}
				value.add(new GeoNumeric(cons, n));

				if (classList == null) {
					if (doCumulative) {
						addValue(f.getCumFreq(n));
					} else {
						addValue(f.getCount(n));
					}
				}
			}
		}

		// If classList exists, compute frequencies using the classList
		// =======================================================

		if (classList != null) {

			double lowerClassBound = 0;
			double upperClassBound = 0;
			double classFreq = 0;

			// set density conditions
			boolean hasDensity = false;
			if (useDensity != null) {
				hasDensity = useDensity.getBoolean();
			}

			double densityValue = 1; // default density
			if (density != null) {
				densityValue = density.getDouble();
			}

			double cumulativeClassFreq = 0;
			double swap;
			int length = classList.size();
			for (int i = 1; i < length; i++) {

				lowerClassBound = ((GeoNumeric) classList.get(i - 1))
						.getDouble();
				upperClassBound = ((GeoNumeric) classList.get(i)).getDouble();

				// handle round-off error in class list values (this is possible
				// if auto-generated by another cmd)
				lowerClassBound = DoubleUtil.checkDecimalFraction(lowerClassBound);
				upperClassBound = DoubleUtil.checkDecimalFraction(upperClassBound);

				boolean increasing = true;
				if (lowerClassBound > upperClassBound) {
					swap = upperClassBound;
					upperClassBound = lowerClassBound;
					lowerClassBound = swap;
					increasing = false;
				}
				classFreq = f.getCumFreq(upperClassBound)
						- f.getCumFreq(lowerClassBound)
						+ f.getCount(lowerClassBound);
				if ((i != length - 1 && increasing) || (i != 1 && !increasing)) {
					classFreq -= f.getCount(upperClassBound);
				}

				if (doCumulative) {
					cumulativeClassFreq += classFreq;
				}

				// adjust the frequency and add to the output GeoList
				double v = doCumulative ? cumulativeClassFreq : classFreq;
				if (hasDensity) {
					v = densityValue * v / (upperClassBound - lowerClassBound);
				}
				addValue(v);
			}

			// handle the last (highest) class frequency specially
			// it must also count values equal to the highest class bound

		}
	}

	private void addValue(double v) {
		if (scale != null) {
			frequency.add(new GeoNumeric(cons, v * scaleFactor));
		} else {
			frequency.add(new GeoNumeric(cons, v));
		}
	}

	private void computeContingencyTable() {

		// Validate input arguments
		if (!dataList.isDefined() || dataList.size() == 0
				|| !classList.isDefined() || classList.size() == 0) {
			frequency.setUndefined();
			return;
		}

		if (!(dataList.getElementType().equals(GeoClass.TEXT)
				&& classList.getElementType().equals(GeoClass.TEXT))) {
			frequency.setUndefined();
			return;
		}

		if (dataList.size() != classList.size()) {
			frequency.setUndefined();
			return;
		}

		frequency.setDefined(true);
		frequency.clear();

		contingencyRowValues = getUniqueValues(classList);
		contingencyColumnValues = getUniqueValues(dataList);

		List<String> rowList = Arrays.asList(contingencyRowValues);
		List<String> colList = Arrays.asList(contingencyColumnValues);

		int n1 = contingencyRowValues.length;
		int n2 = contingencyColumnValues.length;

		// todo: reuse freqTable? need to init?
		int[][] freqTable = new int[n1][n2];
		for (int i = 0; i < n1; i++) {
			for (int j = 0; j < n2; j++) {
				freqTable[i][j] = 0;
			}
		}

		// compute the frequencies
		for (int index = 0; index < classList.size(); index++) {
			// get ordered pair of strings
			String s1 = ((GeoText) classList.get(index))
					.toValueString(StringTemplate.defaultTemplate);
			String s2 = ((GeoText) dataList.get(index))
					.toValueString(StringTemplate.defaultTemplate);
			// increment frequency element
			freqTable[rowList.indexOf(s1)][colList.indexOf(s2)]++;
		}

		// create the GeoList matrix
		for (int row = 0; row < n1; row++) {
			GeoList l = new GeoList(cons);
			for (int col = 0; col < n2; col++) {
				l.add(new GeoNumeric(cons, freqTable[row][col]));
			}
			frequency.add(l);
		}

	}

	private String[] getUniqueValues(GeoList list) {

		AlgoUnique al = new AlgoUnique(cons, list);
		cons.removeFromConstructionList(al);
		GeoList geo = (GeoList) al.getGeoElements()[0];
		String[] s = new String[geo.size()];
		for (int i = 0; i < geo.size(); i++) {
			String a = geo.get(i).toValueString(StringTemplate.defaultTemplate);
			s[i] = a;
		}
		return s;
	}

}
