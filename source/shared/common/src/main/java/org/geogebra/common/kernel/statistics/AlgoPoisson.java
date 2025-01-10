/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author G. Sturr
 */

public class AlgoPoisson extends AlgoDistribution {

	public AlgoPoisson(Construction cons, GeoNumberValue a, GeoNumberValue b,
			GeoBoolean isCumulative) {
		super(cons, a, b, null, isCumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.Poisson;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double param = a.getDouble();
			int val = (int) Math.round(b.getDouble());
			try {
				PoissonDistribution dist = getPoissonDistribution(param);
				if (isCumulative.getBoolean()) {
					num.setValue(dist.cumulativeProbability(val)); // P(X <=
																	// val)
				}
				else {
					num.setValue(dist.probability(val)); // P(X = val)
				}
			} catch (Exception e) {
				Log.debug(e.getMessage());
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
