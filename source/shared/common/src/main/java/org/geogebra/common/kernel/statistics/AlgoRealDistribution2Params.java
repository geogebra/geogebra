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

import org.apache.commons.math3.distribution.RealDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoRealDistribution2Params extends AlgoDistribution {

	private final ProbabilityCalculatorSettings.Dist dist;

	/**
	 * @param cons construction
	 * @param a first parameter
	 * @param b second parameter
	 * @param c random variable value
	 * @param cumulative flag for CDF
	 * @param dist distribution
	 */
	public AlgoRealDistribution2Params(Construction cons, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue c, GeoBoolean cumulative, ProbabilityCalculatorSettings.Dist dist) {
		super(cons, cumulative, a, b, c);
		this.dist = dist;
		compute();
	}

	@Override
	public Commands getClassName() {
		return dist.command;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double param = a.getDouble();
			double param2 = b.getDouble();
			try {
				RealDistribution dist = getDist(this.dist, param, param2);
				setFromRealDist(dist, c); // P(T <= val)
			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
