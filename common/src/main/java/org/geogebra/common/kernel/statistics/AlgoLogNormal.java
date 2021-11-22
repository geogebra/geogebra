/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.MyMath2;

/**
 * Cumulative LogNormal[mean, s, val]
 * 
 * @author Michael Borcherds
 */

public class AlgoLogNormal extends AlgoDistribution {

	@SuppressWarnings("javadoc")
	public AlgoLogNormal(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoBoolean cumulative) {
		super(cons, a, b, c, null, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.LogNormal;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double mean = a.getDouble();
			double s = b.getDouble();
			double x = c.getDouble();
			boolean pdf = isCumulative == null || isCumulative.getBoolean();
			if (s <= 0) {
				num.setUndefined();
			} else if (x <= 0) {
				num.setValue(0);
			} else if (pdf) {
				num.setValue(MyMath2.erf(
						(Math.log(x) - mean) / (Math.sqrt(2) * Math.abs(s))) / 2
						+ 0.5);
			} else {
				double prod = x * Math.sqrt(Kernel.PI_2) * Math.abs(s);
				double en = Math.log(x) - mean;
				en = Math.exp(-(en * en) / (s * s * 2)) / prod;
				num.setValue(en);
			}
		} else {
			num.setUndefined();
		}
	}

}
