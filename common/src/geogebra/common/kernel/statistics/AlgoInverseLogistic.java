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

/**
 * InverseLogNormal[mean, s, val]
 * @author Michael Borcherds
 */

public class AlgoInverseLogistic extends AlgoDistribution {



	@SuppressWarnings("javadoc")
	public AlgoInverseLogistic(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
		super(cons, label, a, b, c, null);
	}

	@SuppressWarnings("javadoc")
	public AlgoInverseLogistic(Construction cons, NumberValue a,
			NumberValue b, NumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseLogistic;
	}

	@Override
	public final void compute() {


		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
			double mean = a.getDouble();
			double s = b.getDouble();
			double x = c.getDouble();

			if (s <= 0 || x <= 0 || x > 1) {
				num.setValue(0);
			} else  {
				num.setValue(mean - Math.log(1 / x - 1) * s);
			}

		} else
			num.setUndefined();
	}       

}



