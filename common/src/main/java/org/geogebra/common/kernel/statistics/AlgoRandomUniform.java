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
 * Computes RandomUniform[a, b]
 * 
 * @author Michael Borcherds
 */
public class AlgoRandomUniform extends AlgoTwoNumFunction
		implements SetRandomValue {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            lower bound for the distribution
	 * @param b
	 *            upper bound for the distribution
	 */
	public AlgoRandomUniform(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomUniform;
	}

	@Override
	protected double computeValue(double aVal, double bVal) {
		return aVal + kernel.getApplication().getRandomNumber() * (bVal - aVal);
	}

	@Override
	public boolean setRandomValue(GeoElementND d) {
		num.setValue(Math.max(a.getDouble(), Math.min(d.evaluateDouble(), b.getDouble())));
		return true;
	}

}
