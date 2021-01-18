/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
		super(cons, label, a, b);
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
