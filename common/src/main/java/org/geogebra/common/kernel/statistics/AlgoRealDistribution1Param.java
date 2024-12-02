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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoRealDistribution1Param extends AlgoDistribution {

	private final ProbabilityCalculatorSettings.Dist command;

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            degrees of freedom
	 * @param b
	 *            variable value
	 */
	public AlgoRealDistribution1Param(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoBoolean cumulative, ProbabilityCalculatorSettings.Dist command) {
		super(cons, cumulative, a, b, (GeoNumberValue) null);
		this.command = command;
		compute();
	}

	@Override
	public Commands getClassName() {
		return command.command;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			try {
				RealDistribution dist = getDist(command, param, 0);
				setFromRealDist(dist, b); // P(T <= val)
			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
