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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Returns the harmonic mean for a list of numbers
 */

public class AlgoRootMeanSquare extends AlgoElement {

	private GeoList inputList; // input
	private GeoNumeric result; // output
	private int size;
	private double sum;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param inputList
	 *            numbers
	 */
	public AlgoRootMeanSquare(Construction cons, String label,
			GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.RootMeanSquare;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return root mean square
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

		// ==========================
		// compute result

		sum = 0;

		// load input value array from geoList
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof NumberValue) {
				double d = geo.evaluateDouble();
				sum += d * d;
			} else {
				result.setUndefined();
				return;
			}
		}

		result.setValue(Math.sqrt(sum / size));
	}

}
