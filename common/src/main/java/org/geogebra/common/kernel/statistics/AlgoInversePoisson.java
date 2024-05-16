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
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInversePoisson extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            mean
	 * @param b
	 *            variable value
	 */
	public AlgoInversePoisson(Construction cons, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, a, b, null, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InversePoisson;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			double val = b.getDouble();
			try {
				PoissonDistribution dist = getPoissonDistribution(param);

				double result = dist.inverseCumulativeProbability(val);

				// eg InversePascal[1,1,1] returns 2147483647
				if (result >= Integer.MAX_VALUE) {
					num.setUndefined();
				} else {
					num.setValue(result);
				}

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
