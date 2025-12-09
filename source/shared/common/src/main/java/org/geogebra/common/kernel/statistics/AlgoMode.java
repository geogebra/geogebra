/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param inputList
	 *            data
	 */
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

	/**
	 * @return mode
	 */
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
				sortList[i] = geo.evaluateDouble();
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
				if (run >= maxRun) {
					maxRun = run;
				}
				run = 1;
				val = sortList[i];
			}
		}
		if (run >= maxRun) {
			maxRun = run;
		}

		outputList.setDefined(true);
		outputList.clear();

		if (maxRun == 1) {
			return; // no mode, return empty list
		}

		// check which numbers occur maxRun times and put them in a list
		run = 1;
		val = sortList[0];

		for (int i = 1; i < size; i++) {
			if (sortList[i] == val) {
				run++;
				if (run == maxRun) {
					outputList.addNumber(val, null);
				}
			} else {
				run = 1;
				val = sortList[i];
			}
		}

	}

}
