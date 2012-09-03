/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.statistics.SetRandomValue;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 * @version
 */
public class AlgoRandom extends AlgoTwoNumFunction implements SetRandomValue {

	public AlgoRandom(Construction cons, String label, NumberValue a,
			NumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
}

	@Override
	public Algos getClassName() {
		return Algos.AlgoRandom;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			num.setValue(cons.getApplication().getRandomIntegerBetween(a.getDouble(), b.getDouble()));
		} else
			num.setUndefined();
		
	}

	public void setRandomValue(double d) {
		d = Math.round(Kernel.checkInteger(d));
		
		if (d >= a.getDouble() && d <= b.getDouble()) {
			num.setValue(d);
			num.updateRepaint();
		}
			
	}

}
