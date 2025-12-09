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

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseBinomial extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            number of trials
	 * @param b
	 *            probability of success
	 * @param c
	 *            variable value
	 */
	public AlgoInverseBinomial(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c) {
		super(cons, a, b, c, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.InverseBinomial;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			int param = (int) Math.round(a.getDouble());
			double param2 = b.getDouble();
			double val = c.getDouble();
			try {
				BinomialDistribution dist = getBinomialDistribution(param,
						param2);

				// subtract eps to fix eg
				// InverseBinomial[10,1/10,617003001/625000000]
				// https://www.geogebra.org/m/d23dHjw2
				double result = dist.inverseCumulativeProbability(val - 1E-14);

				// eg InversePascal[1,1,1] returns 2147483647
				if (result > param) {
					num.setValue(param);
				} else {
					num.setValue(result);
				}

			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
