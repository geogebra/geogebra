/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 */
public class AlgoRandom extends AlgoTwoNumFunction implements SetRandomValue {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            min
	 * @param b
	 *            max
	 */
	public AlgoRandom(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
	}

	@Override
	public Commands getClassName() {
		return Commands.Random;
	}

	@Override
	public final double computeValue(double aNum, double bNum) {
		if (!Double.isInfinite(aNum) && !Double.isInfinite(bNum)
				&& !Double.isNaN(aNum) && !Double.isNaN(bNum)) {
			return cons.getApplication().getRandomIntegerBetween(aNum, bNum);
		}

		return Double.NaN;
	}

	@Override
	public boolean setRandomValue(GeoElementND d0) {
		double d = Math.round(d0.evaluateDouble());

		double aVal = a.getDouble();
		double bVal = b.getDouble();
		if (d >= aVal && d <= bVal || d >= bVal && d <= aVal) {
			num.setValue(d);
			return true;
		}
		return false;
	}
}
