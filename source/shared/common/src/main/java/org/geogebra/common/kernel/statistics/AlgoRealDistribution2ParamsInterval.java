/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.RealDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

/**
 * Computes  area under normal distribution curve for given interval.
 */
public class AlgoRealDistribution2ParamsInterval extends AlgoDistribution {

	private final ProbabilityCalculatorSettings.Dist dist;

	/**
	 * @param cons construction
	 * @param a first parameter
	 * @param b second parameter
	 * @param c random variable value
	 * @param upperBound interval upper bound
	 * @param dist distribution
	 */
	public AlgoRealDistribution2ParamsInterval(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoNumberValue upperBound,
			ProbabilityCalculatorSettings.Dist dist) {
		super(cons, a, b, c, upperBound, null);
		this.dist = dist;
		compute();
	}

	@Override
	public Commands getClassName() {
		return dist.command;
	}

	@Override
	public final void compute() {
		if (a.isDefined() && b.isDefined()
				&& c.isDefined() && d.isDefined()) {
			double param = a.getDouble();
			double param2 = b.getDouble();
			try {
				RealDistribution dist = getDist(this.dist, param, param2);
				double upper = d.getDouble();
				double lower = c.getDouble();
				if (lower > upper) {
					num.setUndefined();
				} else {
					num.setValue(dist.cumulativeProbability(upper)
							- dist.cumulativeProbability(lower)); // P(c <= T <= d)
				}
			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
