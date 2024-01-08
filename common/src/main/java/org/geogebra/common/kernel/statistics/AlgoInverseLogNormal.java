/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.special.Erf;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * InverseLogNormal[mean, s, val]
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseLogNormal extends AlgoDistribution {

	@SuppressWarnings("javadoc")
	public AlgoInverseLogNormal(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue c) {
		super(cons, label, a, b, c, null);
	}

	@SuppressWarnings("javadoc")
	public AlgoInverseLogNormal(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseLogNormal;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double mean = a.getDouble();
			double s = b.getDouble();
			double x = c.getDouble();

			if (s <= 0 || x <= 0 || x > 1) {
				num.setValue(0);
			} else {
				num.setValue(Math
						.exp(Erf.erfInv(2 * (x - 0.5)) * Math.sqrt(2) * s
								+ mean));
			}

		} else {
			num.setUndefined();
		}
	}

}
