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
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseRealDistribution2Params extends AlgoDistribution {

	private final ProbabilityCalculatorSettings.Dist command;

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            alpha
	 * @param b
	 *            beta
	 * @param c
	 *            variable value
	 */
	public AlgoInverseRealDistribution2Params(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, ProbabilityCalculatorSettings.Dist command) {
		super(cons, null, a, b, c);
		this.command = command;
		compute();
	}

	@Override
	public Commands getClassName() {
		return command.inverse;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double param = a.getDouble();
			double param2 = b.getDouble();
			double val = c.getDouble();
			try {
				RealDistribution dist = getDist(command, param, param2);
				num.setValue(dist.inverseCumulativeProbability(val)); // P(T <=
																		// val)

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
