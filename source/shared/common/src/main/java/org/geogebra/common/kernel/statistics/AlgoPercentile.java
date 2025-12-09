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

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Returns the percentile for a given percentage in a list of numbers
 */

public class AlgoPercentile extends AlgoElement {

	private GeoList inputList; // input
	private GeoNumeric value; // input
	private GeoNumeric result; // output
	private int size;
	private Percentile percentile;
	private double[] inputArray;
	private double val;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            data
	 * @param value
	 *            percentile value to compute
	 */
	public AlgoPercentile(Construction cons, String label, GeoList inputList,
			GeoNumeric value) {
		super(cons);
		this.inputList = inputList;
		this.value = value;
		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Percentile;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = value;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return estimate of the <code>p</code>th percentile of the values in the input list
	 */
	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {

		// ==========================
		// validation
		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			result.setUndefined();
			return;
		}

		if (value == null) {
			result.setUndefined();
			return;
		}
		val = value.getDouble() * 100;

		if (val <= 0 || val > 100) {
			result.setUndefined();
			return;
		}

		// ==========================
		// compute result

		inputArray = new double[size];

		// load input value array from geoList
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof NumberValue) {
				inputArray[i] = geo.evaluateDouble();
			} else {
				result.setUndefined();
				return;
			}
		}

		if (percentile == null) {
			percentile = new Percentile();
		}

		percentile.setData(inputArray);
		result.setValue(percentile.evaluate(val));
	}

}
