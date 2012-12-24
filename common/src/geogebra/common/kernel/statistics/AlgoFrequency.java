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
import geogebra.common.kernel.advanced.AlgoUnique;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math.stat.Frequency;

public class AlgoFrequency extends AlgoElement {

	private GeoList dataList; // input
	private GeoList classList; // input
	private GeoBoolean isCumulative; // input
	private GeoBoolean useDensity; // input
	private GeoNumeric density; // input

	private GeoList frequency; // output

	// for compute
	private GeoList value = new GeoList(cons);
	private String[] contingencyRowValues, contingencyColumnValues;
	private Boolean isContingencyTable = false;

	/**
	 * @param cons
	 * @param label
	 * @param isCumulative
	 * @param classList
	 * @param dataList
	 */
	public AlgoFrequency(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList) {
		this(cons, label, isCumulative, classList, dataList, null, null);
	}

	/**
	 * @param cons
	 * @param isCumulative
	 * @param classList
	 * @param dataList
	 */
	public AlgoFrequency(Construction cons, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList) {
		this(cons, isCumulative, classList, dataList, null, null);
	}

	/**
	 * @param cons
	 * @param label
	 * @param isCumulative
	 * @param classList
	 * @param dataList
	 * @param useDensity
	 * @param density
	 */
	public AlgoFrequency(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric density) {
		this(cons, isCumulative, classList, dataList, useDensity, density);
		frequency.setLabel(label);
	}

	/**
	 * @param cons
	 * @param isCumulative
	 * @param classList
	 * @param dataList
	 * @param useDensity
	 * @param density
	 */
	AlgoFrequency(Construction cons, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity,
			GeoNumeric density) {
		super(cons);

		this.classList = classList;
		this.dataList = dataList;
		this.isCumulative = isCumulative;
		this.useDensity = useDensity;
		this.density = density;

		frequency = new GeoList(cons);
		setInputOutput();
		compute();
	}

	/***************************************************
	 * Contingency table constructor
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param isContingencyTable
	 * 
	 */
	public AlgoFrequency(Construction cons, String label, GeoList list1,
			GeoList list2, boolean isContingencyTable) {

		this(cons, list1, list2, isContingencyTable);
		frequency.setLabel(label);
	}

	/***************************************************
	 * Contingency table constructor (no label)
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 * @param isContingencyTable
	 *            (dummy variable)
	 */
	public AlgoFrequency(Construction cons, GeoList list1, GeoList list2,
			boolean isContingencyTable) {
		super(cons);

		this.isContingencyTable = true;
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

		ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

		if (isCumulative != null)
			tempList.add(isCumulative);

		if (classList != null)
			tempList.add(classList);

		tempList.add(dataList);

		if (useDensity != null)
			tempList.add(useDensity);

		if (density != null)
			tempList.add(density);

		input = new GeoElement[tempList.size()];
		input = tempList.toArray(input);

		setOutputLength(1);
		setOutput(0, frequency);
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

		if (!(dataList.getElementType().equals(GeoClass.TEXT) || dataList
				.getElementType().equals(GeoClass.NUMERIC))) {
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

		frequency.setDefined(true);
		frequency.clear();
		if (value != null)
			value.clear();

		double numMax = 0, numMin = 0;
		boolean doCumulative = isCumulative != null
				&& isCumulative.getBoolean();

		// Load the data into f, an instance of Frequency class
		// =======================================================

		Frequency f = new Frequency();
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.getElementType().equals(GeoClass.TEXT))
				f.addValue(((GeoText) dataList.get(i))
						.toValueString(StringTemplate.defaultTemplate));
			if (dataList.getElementType().equals(GeoClass.NUMERIC))
				f.addValue(((GeoNumeric) dataList.get(i)).getDouble());
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
				if (s.compareTo(strMax) > 0)
					strMax = s;
				if (s.compareTo(strMin) < 0)
					strMin = s;
				GeoText text = new GeoText(cons);
				text.setTextString(s);
				value.add(text);
				if (classList == null) {
					if (doCumulative) {
						frequency.add(new GeoNumeric(cons, f.getCumFreq(s)));
					} else {
						frequency.add(new GeoNumeric(cons, f.getCount(s)));
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
				if (n > numMax)
					numMax = n.doubleValue();
				if (n < numMin)
					numMin = n.doubleValue();
				value.add(new GeoNumeric(cons, n));

				if (classList == null)
					if (doCumulative)
						frequency.add(new GeoNumeric(cons, f.getCumFreq(n)));
					else
						frequency.add(new GeoNumeric(cons, f.getCount(n)));
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
			if (useDensity != null)
				hasDensity = useDensity.getBoolean();

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
				if ((i != length - 1 && increasing) || (i != 1 && !increasing))
					classFreq -= f.getCount(upperClassBound);

				// System.out.println(" =================================");
				// System.out.println("class freq: " + classFreq + "   " +
				// density);
				if (hasDensity) {
					classFreq = densityValue * classFreq
							/ (upperClassBound - lowerClassBound);
				}
				if (doCumulative)
					cumulativeClassFreq += classFreq;
				// System.out.println("class freq: " + classFreq);

				// add the frequency to the output GeoList
				frequency.add(new GeoNumeric(cons,
						doCumulative ? cumulativeClassFreq : classFreq));
			}

			// handle the last (highest) class frequency specially
			// it must also count values equal to the highest class bound

		}
	}

	private void computeContingencyTable() {

		// Validate input arguments
		if (!dataList.isDefined() || dataList.size() == 0
				|| !classList.isDefined() || classList.size() == 0) {
			frequency.setUndefined();
			return;
		}

		if (!(dataList.getElementType().equals(GeoClass.TEXT) && classList
				.getElementType().equals(GeoClass.TEXT))) {
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
		for (int i = 0; i < n1; i++)
			for (int j = 0; j < n2; j++)
				freqTable[i][j] = 0;

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

	// TODO Consider locusequability

}
