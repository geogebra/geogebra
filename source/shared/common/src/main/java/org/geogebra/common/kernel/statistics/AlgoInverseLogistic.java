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
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * InverseLogNormal[mean, s, val]
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseLogistic extends AlgoDistribution {

	@SuppressWarnings("javadoc")
	public AlgoInverseLogistic(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseLogistic;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double mean = a.getDouble();
			double s = b.getDouble();
			double x = c.getDouble();

			if (s <= 0 || x <= 0 || x > 1) {
				num.setValue(0);
			} else {
				num.setValue(mean - Math.log(1 / x - 1) * s);
			}

		} else {
			num.setUndefined();
		}
	}

}
