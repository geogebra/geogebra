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
 * Cumulative Logistic[mean, s, val]
 * 
 * @author Michael Borcherds
 */

public class AlgoLogistic extends AlgoDistribution {

	public AlgoLogistic(Construction cons, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue c, GeoBoolean cumulative) {
		super(cons, a, b, c, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.Logistic;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double mean = a.getDouble();
			double s = b.getDouble();
			double x = c.getDouble();
			// en = en.subtract(mean).divide(absS).reverseSign().exp();
			boolean pdf = isCumulative == null || isCumulative.getBoolean();
			double exp = Math.exp((-(x - mean)) / Math.abs(s));
			num.setValue(pdf ? 1 / (1 + exp)
					: exp / Math.abs(s) / Math.pow(exp + 1, 2));

			// old hack
			// processAlgebraCommand( "1/(1+exp(-("+x+"-("+m+"))/abs("+s+")))",
			// true );

		} else {
			num.setUndefined();
		}
	}

}
