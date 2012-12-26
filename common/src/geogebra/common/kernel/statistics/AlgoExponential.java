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
import geogebra.common.kernel.commands.Commands;

import org.apache.commons.math.distribution.ExponentialDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoExponential extends AlgoDistribution {

	public AlgoExponential(Construction cons, String label, NumberValue a,
			NumberValue b) {
		super(cons, label, a, b, null, null);
	}

	public AlgoExponential(Construction cons, NumberValue a, NumberValue b) {
		super(cons, a, b, null, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.Exponential;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			double val = b.getDouble();
			try {
				ExponentialDistribution dist = getExponentialDistribution(param);
				num.setValue(dist.cumulativeProbability(val)); // P(T <= val)

			} catch (Exception e) {
				num.setUndefined();
			}
		} else
			num.setUndefined();
	}
	
}
