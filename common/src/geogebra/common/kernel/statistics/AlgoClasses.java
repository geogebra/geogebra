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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;

public class AlgoClasses extends AlgoElement {

	private GeoList dataList; // input
	private GeoNumeric start; // input
	private GeoNumeric width; // input
	private GeoNumeric numClasses; // input

	private GeoList classList; // output

	// for compute

	public AlgoClasses(Construction cons, String label, GeoList dataList,
			GeoNumeric start, GeoNumeric width, GeoNumeric numClasses) {

		this(cons, dataList, start, width, numClasses);
		classList.setLabel(label);
	}

	public AlgoClasses(Construction cons, GeoList dataList, GeoNumeric start,
			GeoNumeric width, GeoNumeric numClasses) {
		super(cons);
		this.dataList = dataList;
		this.start = start;
		this.width = width;
		this.numClasses = numClasses;

		classList = new GeoList(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Classes;
	}

	@Override
	protected void setInputOutput() {
		ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

		tempList.add(dataList);

		if (start != null)
			tempList.add(start);

		if (width != null)
			tempList.add(width);

		if (numClasses != null)
			tempList.add(numClasses);

		input = new GeoElement[tempList.size()];
		input = tempList.toArray(input);

		super.setOutputLength(1);
		super.setOutput(0, classList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return classList;
	}

	@Override
	public final void compute() {

		// Validate input arguments
		// =======================================================

		if (!dataList.isDefined() || dataList.size() == 0) {
			classList.setUndefined();
			return;
		}

		if (!(dataList.getElementType() .equals(GeoClass.TEXT) || dataList
				.getElementType() .equals(GeoClass.NUMERIC))) {
			classList.setUndefined();
			return;
		}

		classList.setDefined(true);
		classList.clear();

		// Get data max and min
		// =======================================================

		double minGeoValue = 0;
		double maxGeoValue = 0;
		String minGeoString;
		String maxGeoString;

		if (dataList.getElementType() .equals(GeoClass.NUMERIC)) {
			minGeoValue = ((GeoNumeric) dataList.get(0)).getDouble();
			maxGeoValue = ((GeoNumeric) dataList.get(0)).getDouble();
			for (int i = 1; i < dataList.size(); i++) {
				double geoValue = ((GeoNumeric) dataList.get(i)).getDouble();
				minGeoValue = Math.min(geoValue, minGeoValue);
				maxGeoValue = Math.max(geoValue, maxGeoValue);
			}

		} else {
			minGeoString = ((GeoText) dataList.get(0)).toValueString(StringTemplate.defaultTemplate);
			maxGeoString = ((GeoText) dataList.get(0)).toValueString(StringTemplate.defaultTemplate);
			for (int i = 1; i < dataList.size(); i++) {
				String geoString = ((GeoText) dataList.get(i)).toValueString(StringTemplate.defaultTemplate);
				if (geoString.compareTo(minGeoString) < 0)
					minGeoString = geoString;
				if (geoString.compareTo(maxGeoString) < 0)
					maxGeoString = geoString;
			}
		}

		// Create class list using number of classes
		// =======================================================

		if (input.length == 2) {

			int n = (int) numClasses.getDouble();
			if (n < 1)
				classList.setUndefined();

			double width = (maxGeoValue - minGeoValue) / n;
			for (int i = 0; i < n; i++) {
				classList.add(new GeoNumeric(cons, minGeoValue + i * width));
			}
			classList.add(new GeoNumeric(cons, maxGeoValue));

		}

		// Create class list using start and width
		// =======================================================
		if (input.length == 3) {
			double value = start.getDouble();
			classList.add(new GeoNumeric(cons, value));
			while (value < maxGeoValue) {
				value = value + width.getDouble();
				// System.out.println("value: " + value + "max: " +
				// maxGeoValue);
				classList.add(new GeoNumeric(cons, value));
			}
			if (classList.size() < 2)
				classList.setUndefined();
		}
	}

	// TODO Consider locusequability

}
