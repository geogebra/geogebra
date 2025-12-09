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
