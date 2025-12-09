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
