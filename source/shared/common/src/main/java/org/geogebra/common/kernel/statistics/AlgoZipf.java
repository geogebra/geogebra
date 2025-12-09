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

import org.apache.commons.math3.distribution.ZipfDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoZipf extends AlgoDistribution {

	public AlgoZipf(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoBoolean isCumulative) {
		super(cons, a, b, c, isCumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.Zipf;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			int param = (int) a.getDouble();
			double param2 = b.getDouble();
			int val = (int) Math.round(c.getDouble());
			try {
				ZipfDistribution dist = getZipfDistribution(param, param2);
				if (isCumulative.getBoolean()) {
					num.setValue(dist.cumulativeProbability(val)); // P(X <=
																	// val)
				}
				else {
					num.setValue(dist.probability(val)); // P(X = val)
				}

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
