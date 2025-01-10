/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseBinomial extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            number of trials
	 * @param b
	 *            probability of success
	 * @param c
	 *            variable value
	 */
	public AlgoInverseBinomial(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseBinomial;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			int param = (int) Math.round(a.getDouble());
			double param2 = b.getDouble();
			double val = c.getDouble();
			try {
				BinomialDistribution dist = getBinomialDistribution(param,
						param2);

				// subtract eps to fix eg
				// InverseBinomial[10,1/10,617003001/625000000]
				// https://www.geogebra.org/m/d23dHjw2
				double result = dist.inverseCumulativeProbability(val - 1E-14);

				// eg InversePascal[1,1,1] returns 2147483647
				if (result > param) {
					num.setValue(param);
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
