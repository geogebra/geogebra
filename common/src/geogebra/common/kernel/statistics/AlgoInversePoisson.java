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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;

import org.apache.commons.math.distribution.PoissonDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInversePoisson extends AlgoDistribution {

	
	/**
     * @param cons construction
     * @param label label for output
     * @param a mean
     * @param b variable value
     */
	public AlgoInversePoisson(Construction cons, String label, NumberValue a,
			NumberValue b) {
		super(cons, label, a, b, null, null);
	}
	/**
     * @param cons construction
     * @param a mean
     * @param b variable value
     */
	public AlgoInversePoisson(Construction cons, NumberValue a,
			NumberValue b) {
		super(cons, a, b, null, null);
	}
	
	
	@Override
	public Commands getClassName() {
		return Commands.InversePoisson;
	}
	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			double val = b.getDouble();
			try {
				PoissonDistribution dist = getPoissonDistribution(param);

				double result = dist.inverseCumulativeProbability(val);

				// eg InversePascal[1,1,1] returns 2147483647
				if (result >= Integer.MAX_VALUE)
					num.setUndefined();
				else
					num.setValue(result + 1);

			} catch (Exception e) {
				num.setUndefined();
			}
		} else
			num.setUndefined();
	}

}
