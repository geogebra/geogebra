/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import java.util.Arrays;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Mode of a list. Adapted from AlgoSort
 * 
 * @author Michael Borcherds
 * @version 2008-02-16
 */

public class AlgoMode extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;

	public AlgoMode(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Mode;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			outputList.setUndefined();
			return;
		}

		double[] sortList = new double[size];

		// copy inputList into an array
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof NumberValue) {
				NumberValue num = (NumberValue) geo;
				sortList[i] = num.getDouble();
			} else {
				outputList.setUndefined();
				return;
			}
		}

		// do the sorting
		Arrays.sort(sortList);

		// check what the longest run of equal numbers is
		int maxRun = 1;
		int run = 1;
		double val = sortList[0];

		for (int i = 1; i < size; i++) {
			if (sortList[i] == val) {
				run++;
			} else {
				if (run >= maxRun)
					maxRun = run;
				run = 1;
				val = sortList[i];
			}
		}
		if (run >= maxRun)
			maxRun = run;

		outputList.setDefined(true);
		outputList.clear();

		if (maxRun == 1)
			return; // no mode, return empty list

		// check which numbers occur maxRun times and put them in a list
		run = 1;
		val = sortList[0];
		int modeNo = 0;

		for (int i = 1; i < size; i++) {
			if (sortList[i] == val) {
				run++;
				if (run == maxRun)
					outputList.addNumber(val, null);
			} else {
				run = 1;
				val = sortList[i];
			}
		}

	}


	

}
