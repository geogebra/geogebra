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
