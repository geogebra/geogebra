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
import org.geogebra.common.kernel.algos.AlgoMedian;
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
	private GeoList freqList; // input
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
	public AlgoPercentile(Construction cons, String label, GeoList inputList, GeoNumeric value) {
		this(cons, label, inputList, null, value);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            data
	 * @param freqList
	 *            frequencies
	 * @param value
	 *            percentile value to compute
	 */
	public AlgoPercentile(
			Construction cons, String label, GeoList inputList, GeoList freqList, GeoNumeric value) {
		super(cons);
		this.inputList = inputList;
		this.freqList = freqList;
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
		if (freqList == null) {
			input = new GeoElement[2];
			input[0] = inputList;
			input[1] = value;
		} else {
			input = new GeoElement[3];
			input[0] = inputList;
			input[1] = freqList;
			input[2] = value;
		}

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

		if (freqList != null) {
			computeFromFrequencies();
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

	/**
	 * Same estimate as the default (legacy) {@link Percentile}, but on value/frequency
	 * pairs, so the data does not need to be expanded.
	 */
	private void computeFromFrequencies() {
		if (!freqList.isDefined() || size != freqList.size()) {
			result.setUndefined();
			return;
		}

		for (int i = 0; i < size; i++) {
			if (!(inputList.get(i) instanceof NumberValue)
					|| !(freqList.get(i) instanceof NumberValue)
					|| !Double.isFinite(freqList.get(i).evaluateDouble())
					|| freqList.get(i).evaluateDouble() < 0) {
				result.setUndefined();
				return;
			}
		}

		Object[] obj = AlgoMedian.convertValueFreqListToArrays(inputList, freqList);
		Double[] v = (Double[]) obj[0];
		Integer[] f = (Integer[]) obj[1];
		int n = (Integer) obj[2];

		if (n == 0) {
			result.setUndefined();
			return;
		}

		double pos = val / 100 * (n + 1.0);
		if (pos < 1) {
			result.setValue(AlgoMedian.getValueAt(0, v, f));
		} else if (pos >= n) {
			result.setValue(AlgoMedian.getValueAt(n - 1, v, f));
		} else {
			int intPos = (int) Math.floor(pos);
			double lower = AlgoMedian.getValueAt(intPos - 1, v, f);
			double upper = AlgoMedian.getValueAt(intPos, v, f);
			result.setValue(lower + (pos - intPos) * (upper - lower));
		}
	}
}
