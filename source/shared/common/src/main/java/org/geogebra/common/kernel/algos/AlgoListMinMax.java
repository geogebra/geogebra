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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Minimum value of a list.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListMinMax extends AlgoElement {

	private final boolean isMin;
	private GeoList geoList; // input
	private GeoList freqList; // input
	private GeoNumeric min; // output

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list
	 */
	public AlgoListMinMax(Construction cons, GeoList geoList, boolean isMin) {
		this(cons, geoList, null, isMin);
	}

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list
	 * @param freqList
	 *            frequencies
	 */
	public AlgoListMinMax(Construction cons, GeoList geoList, GeoList freqList, boolean isMin) {
		super(cons);
		this.geoList = geoList;
		this.freqList = freqList;
		min = new GeoNumeric(cons);
		this.isMin = isMin;
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return isMin ? Commands.Min : Commands.Max;
	}

	@Override
	protected void setInputOutput() {

		if (freqList == null) {
			input = new GeoElement[1];
			input[0] = geoList;
		} else {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = freqList;
		}

		setOnlyOutput(min);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getMin() {
		return min;
	}

	@Override
	public final void compute() {
		int size = geoList.size();
		if (!geoList.isDefined() || size == 0) {
			min.setUndefined();
			return;
		}

		double minVal = isMin ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

		if (freqList == null) {

			for (int i = 0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				if (geo instanceof NumberValue) {
					minVal = minMax(minVal, geo.evaluateDouble());
				} else {
					min.setUndefined();
					return;
				}
			}

		} else {

			if (!freqList.isDefined() || freqList.size() != geoList.size()) {
				min.setUndefined();
				return;
			}

			boolean hasPositiveFrequency = false;
			for (int i = 0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				GeoElement freqGeo = freqList.get(i);

				if (!(geo instanceof NumberValue)
						|| !(freqGeo instanceof NumberValue)) {
					min.setUndefined();
					return;
				}

				// handle bad frequency
				double frequency = freqGeo.evaluateDouble();
				if (frequency < 0) {
					min.setUndefined();
					return;
				} else if (frequency == 0) {
					continue;
				}
				hasPositiveFrequency = true;

				minVal = minMax(minVal, geo.evaluateDouble());
			}

			// make sure not all frequencies are zero
			if (!hasPositiveFrequency) {
				min.setUndefined();
				return;
			}

		}

		min.setValue(minVal);
	}

	private double minMax(double a, double b) {
		return isMin ? Math.min(a, b) : Math.max(a, b);
	}

}
