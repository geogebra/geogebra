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
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoTwoNumFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 */
public class AlgoRandomNormal extends AlgoTwoNumFunction
		implements SetRandomValue {

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            mean
	 * @param b
	 *            standard deviation
	 */
	public AlgoRandomNormal(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomNormal;
	}

	@Override
	protected double computeValue(double aVal, double bVal) {
		if (bVal < 0) {
			return Double.NaN;
		}
		return randomNormal(aVal, bVal);
	}

	private double randomNormal(double mean, double sd) {
		double fac, rsq, v1, v2;
		do {
			// two random numbers from -1 to +1
			v1 = 2.0 * kernel.getApplication().getRandomNumber() - 1;
			v2 = 2.0 * kernel.getApplication().getRandomNumber() - 1;
			rsq = v1 * v1 + v2 * v2;
		} while (rsq >= 1.0 || rsq == 0.0); // keep going until they are in the
											// unit circle
		fac = Math.sqrt(-2.0 * Math.log(rsq) / rsq);
		return v1 * fac * sd + mean;
	}

	@Override
	public boolean setRandomValue(GeoElementND d) {
		num.setValue(d.evaluateDouble());
		return true;
	}
}
