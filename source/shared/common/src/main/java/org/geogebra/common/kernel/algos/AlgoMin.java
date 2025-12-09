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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Computes Min[a, b]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoMin extends AlgoTwoNumFunction {

	/**
	 * Creates new min algo
	 * 
	 * @param cons
	 *            construction
	 * @param a
	 *            first number
	 * @param b
	 *            second number
	 */
	public AlgoMin(Construction cons, GeoNumberValue a, GeoNumberValue b) {
		super(cons, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.Min;
	}

	@Override
	public final double computeValue(double aVal, double bVal) {
		return Math.min(aVal, bVal);
	}
}
