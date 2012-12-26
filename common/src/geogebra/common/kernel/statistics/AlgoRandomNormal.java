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
import geogebra.common.kernel.SetRandomValue;
import geogebra.common.kernel.algos.AlgoTwoNumFunction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 * @version
 */
public class AlgoRandomNormal extends AlgoTwoNumFunction implements SetRandomValue {

	public AlgoRandomNormal(Construction cons, String label, NumberValue a,
			NumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomNormal;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			if (b.getDouble() < 0)
				num.setUndefined();
			else
				num.setValue(randomNormal(a.getDouble(), b.getDouble()));
		} else
			num.setUndefined();
	}

	private double randomNormal(double mean, double sd) {
		double fac, rsq, v1, v2;
		do {
			v1 = 2.0 * app.getRandomNumber() - 1;
			v2 = 2.0 * app.getRandomNumber() - 1; // two random numbers from -1 to +1
			rsq = v1 * v1 + v2 * v2;
		} while (rsq >= 1.0 || rsq == 0.0); // keep going until they are in the
											// unit circle
		fac = Math.sqrt(-2.0 * Math.log(rsq) / rsq);
		// Application.debug("randomNormal="+(v1*fac));
		return v1 * fac * sd + mean;

	}

	public void setRandomValue(double d) {
		num.setValue(d);
		num.updateRepaint();
			
	}
}
