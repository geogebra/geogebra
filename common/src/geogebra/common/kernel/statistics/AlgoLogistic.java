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

/**
 * Cumulative Logistic[mean, s, val]
 * @author Michael Borcherds
 */

public class AlgoLogistic extends AlgoDistribution {



	@SuppressWarnings("javadoc")
	public AlgoLogistic(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
		super(cons, label, a, b, c, null);
	}

	public AlgoLogistic(Construction cons, NumberValue a,
			NumberValue b, NumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.Logistic;
	}

	@Override
	public final void compute() {


		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
			double mean = a.getDouble();
			double s = b.getDouble();
			double x = c.getDouble();

			num.setValue(1 / (1 + Math.exp((-(x - mean)) / Math.abs(s))));

			// old hack
			//processAlgebraCommand( "1/(1+exp(-("+x+"-("+m+"))/abs("+s+")))", true );


		} else
			num.setUndefined();
	}       


}



