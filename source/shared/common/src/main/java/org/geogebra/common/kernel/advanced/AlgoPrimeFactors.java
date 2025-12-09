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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Prime factors of a number. Adapted from AlgoMode
 * 
 * @author Michael Borcherds
 */

public class AlgoPrimeFactors extends AlgoElement {

	private GeoNumberValue num; // input
	private GeoList outputList; // output

	private static double LARGEST_INTEGER = 9007199254740992d;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param num
	 *            number
	 */
	public AlgoPrimeFactors(Construction cons, String label,
			GeoNumberValue num) {
		super(cons);
		this.num = num;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.PrimeFactors;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = num.toGeoElement();

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		double n = Math.round(num.getDouble());

		if (n == 1) {
			outputList.clear();
			outputList.setDefined(true);
			return;
		}

		if (n < 2 || n > LARGEST_INTEGER) {
			outputList.setUndefined();
			return;
		}

		outputList.setDefined(true);
		outputList.clear();

		for (int i = 2; i <= n / i; i++) {
			while (n % i == 0) {
				outputList.addNumber(i, this);
				n /= i;
			}
		}
		if (n > 1) {
			outputList.addNumber(n, this);
		}
	}

}
