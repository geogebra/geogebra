/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
