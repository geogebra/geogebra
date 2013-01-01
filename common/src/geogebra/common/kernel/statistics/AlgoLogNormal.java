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
import geogebra.common.util.MyMath2;

/**
 * Cumulative LogNormal[mean, s, val]
 * @author Michael Borcherds
 */

public class AlgoLogNormal extends AlgoDistribution {



	@SuppressWarnings("javadoc")
	public AlgoLogNormal(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
		super(cons, label, a, b, c, null);
	}

	public AlgoLogNormal(Construction cons, NumberValue a,
			NumberValue b, NumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.LogNormal;
	}

	@Override
	public final void compute() {


		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
			double mean = a.getDouble();
			double s = b.getDouble();
			double x = c.getDouble();

				if (s <= 0) {
					num.setUndefined();
				} else if (x < 0) {
					num.setValue(0);
				} else  {
					num.setValue(MyMath2.erf((Math.log(x) - mean) / (Math.sqrt(2) * Math.abs(s)))/2 + 0.5);
				}

				// old hack
				//processAlgebraCommand( "1/2 erf((ln(If["+x+"<0,0,"+x+"])-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1/2", true );

		} else
			num.setUndefined();
	}       


}



