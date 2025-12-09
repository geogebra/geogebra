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

import org.apache.commons.math3.distribution.PoissonDistribution;
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

public class AlgoPoissonDistList extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param a
	 *            number of trials
	 * @param b
	 *            probability of success
	 */
	public AlgoPoissonDistList(Construction cons, String label,
			GeoNumberValue a, GeoList b) {
		super(cons, a, b);
		num.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Poisson;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[2];
		input[0] = a.toGeoElement();
		input[1] = list.toGeoElement();
		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			try {

				PoissonDistribution dist = getPoissonDistribution(param);

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
