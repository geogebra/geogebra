/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoNormal extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            mean
	 * @param b
	 *            sd
	 * @param c
	 *            variable vaue
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoNormal(Construction cons, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue c, GeoBoolean cumulative) {
		super(cons, a, b, c, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.Normal;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double param = a.getDouble();
			double param2 = b.getDouble();
			try {
				NormalDistribution dist = getNormalDistribution(param, param2);
				setFromRealDist(dist, c);

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
