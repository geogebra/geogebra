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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author G. Sturr
 */

public class AlgoBinomialDistList extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param a
	 *            number of trials
	 * @param b
	 *            probability of success
	 * @param c
	 *            value of random variable
	 */
	public AlgoBinomialDistList(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b, GeoList c) {
		super(cons, a, b, c);
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.BinomialDist;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[3];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
		input[2] = list.toGeoElement();
		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			int param = (int) Math.round(a.getDouble());
			double param2 = b.getDouble();
			try {

				BinomialDistribution dist = getBinomialDistribution(param,
						param2);

				double sum = 0;
				for (int i = 0; i < list.size(); i++) {
					sum += dist.probability(
							(int) Math.round(list.get(i).evaluateDouble()));
				}

				num.setValue(sum);
			} catch (Exception e) {
				Log.debug(e.getMessage());
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
