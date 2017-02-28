/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseChiSquared extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param a
	 *            degrees of freedom
	 * @param b
	 *            variable value
	 */
	public AlgoInverseChiSquared(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b) {
		super(cons, label, a, b, null, null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            degrees of freedom
	 * @param b
	 *            variable value
	 */
	public AlgoInverseChiSquared(Construction cons, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, a, b, null, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseChiSquared;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			double val = b.getDouble();
			try {
				ChiSquaredDistribution dist = getChiSquaredDistribution(param);
				num.setValue(dist.inverseCumulativeProbability(val));

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
