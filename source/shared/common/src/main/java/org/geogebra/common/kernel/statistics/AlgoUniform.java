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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoUniform extends AlgoDistribution {

	public AlgoUniform(Construction cons, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue c, GeoBoolean cumulative) {
		super(cons, a, b, c, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.Uniform;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double A = a.getDouble();
			double B = b.getDouble();
			double x = c.getDouble();
			try {
				boolean cdf = this.isCumulative == null
						|| this.isCumulative.getBoolean();
				if (A >= B) {
					num.setUndefined();
				} else if (x > B) {
					num.setValue(cdf ? 1 : 0);
				} else if (x < A) {
					num.setValue(0);
				} else { // A < x < B
					num.setValue(cdf ? (x - A) / (B - A) : 1 / (B - A));
				}

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
