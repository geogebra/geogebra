/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.CauchyDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseCauchy extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param a
	 *            median
	 * @param b
	 *            scale
	 * @param c
	 *            variable value
	 */
	public AlgoInverseCauchy(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c) {
		super(cons, label, a, b, c, null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            median
	 * @param b
	 *            scale
	 * @param c
	 *            variable value
	 */
	public AlgoInverseCauchy(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseCauchy;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double param = a.getDouble();
			double param2 = b.getDouble();
			double val = c.getDouble();
			try {
				CauchyDistribution dist = getCauchyDistribution(param, param2);
				num.setValue(dist.inverseCumulativeProbability(val));

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
