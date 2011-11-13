/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoTwoNumFunction;
import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.NumberValue;

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

	public String getClassName() {
		return "AlgoRandom";
	}

	protected final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			num.setValue(random(a.getDouble(), b.getDouble()));
		} else
			num.setUndefined();
		
	}

	private double random(double a, double b) {
		// make sure 4.000000001 is not rounded up to 5
		a = kernel.checkInteger(a);
		b = kernel.checkInteger(b);
		
		// Math.floor/ceil to make sure
		// RandomBetween[3.2, 4.7] is between 3.2 and 4.7
		double min = Math.ceil(Math.min(a, b));
		double max = Math.floor(Math.max(a, b));
		return Math.floor(Math.random()*(max - min +1)) + min;

	}

	public void setRandomValue(double d) {
		d = Math.round(kernel.checkInteger(d));
		
		if (d >= a.getDouble() && d <= b.getDouble()) {
			num.setValue(d);
			num.updateRepaint();
		}
			
	}

}
