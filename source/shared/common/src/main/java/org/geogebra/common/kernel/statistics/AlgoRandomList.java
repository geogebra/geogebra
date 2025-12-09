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
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

public class AlgoRandomList extends AlgoRandomUniformList {

	/**
	 * @param cons construction
	 * @param label output label
	 * @param a lower bound for uniform distribution
	 * @param b upper bound for uniform distribution
	 * @param length of list
	 */
	public AlgoRandomList(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue length) {
		super(cons, label, a, b, length);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Random;
	}

	@Override
	public double getRandomNumber(double a, double b) {
		return cons.getApplication().getRandomIntegerBetween(a, b);
	}
}
