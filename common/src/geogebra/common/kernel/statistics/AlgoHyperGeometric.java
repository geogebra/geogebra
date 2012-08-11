/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;

import org.apache.commons.math.distribution.HypergeometricDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoHyperGeometric extends AlgoDistribution {
	/**
     * @param cons construction
     * @param label label for output
     * @param a population size
	 * @param b number of successes
	 * @param c sample size
	 * @param d variable value
	 * @param isCumulative flag for cumulative function
     */
	public AlgoHyperGeometric(Construction cons, String label, NumberValue a,
			NumberValue b, NumberValue c, NumberValue d, GeoBoolean isCumulative) {
		super(cons, label, a, b, c, d, isCumulative);
	}
	/**
     * @param cons construction
     * @param a population size
	 * @param b number of successes
	 * @param c sample size
	 * @param d variable value
	 * @param isCumulative flag for cumulative function
     */
	public AlgoHyperGeometric(Construction cons, NumberValue a, NumberValue b,
			NumberValue c, NumberValue d, GeoBoolean isCumulative) {
		super(cons, a, b, c, d, isCumulative);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoHyperGeometric;
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
				if (isCumulative.getBoolean()) {
					num.setValue(dist.cumulativeProbability(val)); // P(X <=
																	// val)
				} else {
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
