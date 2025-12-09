/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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

	/**
	 * Creates a new LogNormal algorithm
	 * @param cons construction
	 * @param mean mean
	 * @param sd standard deviation
	 * @param value probability variable value
	 * @param cumulative true for CDF, false for PDF
	 */
	public AlgoLogNormal(Construction cons, GeoNumberValue mean,
			GeoNumberValue sd, GeoNumberValue value, GeoBoolean cumulative) {
		super(cons, mean, sd, value, null, cumulative);
		compute();
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
