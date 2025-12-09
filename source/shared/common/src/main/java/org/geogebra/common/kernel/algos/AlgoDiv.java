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
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * Computes Div[a, b]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDiv extends AlgoTwoNumFunction {

	public AlgoDiv(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.Div;
	}

	@Override
	public final double computeValue(double aVal, double bVal) {
		double numerator = DoubleUtil.checkInteger(aVal);
		double denominator = DoubleUtil.checkInteger(bVal);

		if (Math.abs(numerator) > MyDouble.LARGEST_INTEGER
				|| Math.abs(denominator) > MyDouble.LARGEST_INTEGER) {
			return Double.NaN;
		}

		double fraction = numerator / denominator;
		double integer = Math.round(fraction);
		if (DoubleUtil.isEqual(fraction, integer)) {
			return integer;
		} else if (denominator > 0) {
			return Math.floor(fraction);
		} else {
			return Math.ceil(fraction);
		}
	}
}
