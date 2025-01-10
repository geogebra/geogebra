/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseHyperGeometric extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            population size
	 * @param b
	 *            number of successes
	 * @param c
	 *            sample size
	 * @param d
	 *            variable value
	 */
	public AlgoInverseHyperGeometric(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoNumberValue d) {
		super(cons, a, b, c, d);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseHyperGeometric;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			int param = (int) Math.round(a.getDouble());
			int param2 = (int) Math.round(b.getDouble());
			int param3 = (int) Math.round(c.getDouble());
			double val = d.getDouble();
			try {
				HypergeometricDistribution dist = getHypergeometricDistribution(
						param, param2, param3);
				// P(T <= val)
				num.setValue(dist.inverseCumulativeProbability(val));
			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
