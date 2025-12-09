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
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoTwoNumFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Computes RandomUniform[a, b]
 * 
 * @author Michael Borcherds
 */
public class AlgoRandomUniform extends AlgoTwoNumFunction
		implements SetRandomValue {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            lower bound for the distribution
	 * @param b
	 *            upper bound for the distribution
	 */
	public AlgoRandomUniform(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomUniform;
	}

	@Override
	protected double computeValue(double aVal, double bVal) {
		return aVal + kernel.getApplication().getRandomNumber() * (bVal - aVal);
	}

	@Override
	public boolean setRandomValue(GeoElementND d) {
		num.setValue(Math.max(a.getDouble(), Math.min(d.evaluateDouble(), b.getDouble())));
		return true;
	}

}
