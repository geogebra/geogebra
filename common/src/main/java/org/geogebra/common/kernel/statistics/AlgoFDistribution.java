/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.FDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoFDistribution extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            dist parameter
	 * @param b
	 *            dist parameter
	 * @param c
	 *            variable value
	 * @param cumulative
	 *            whether this is CDF (null = true)
	 */
	public AlgoFDistribution(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoBoolean cumulative) {
		super(cons, a, b, c, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.FDistribution;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			double param2 = b.getDouble();
			if (c.getDouble() < 0) {
				num.setValue(0);
			} else {
				try {
					FDistribution dist = getFDistribution(param, param2);
					setFromRealDist(dist, c); // P(T <= val)
				} catch (Exception e) {
					num.setUndefined();
				}
			}
		} else {
			num.setUndefined();
		}
	}

}
