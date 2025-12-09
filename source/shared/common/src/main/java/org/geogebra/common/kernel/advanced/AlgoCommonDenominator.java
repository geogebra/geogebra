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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.algos.AlgoTwoNumFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Computes CommonDenominator[a, b]
 * 
 * @author Zbynek Konecny
 */
public class AlgoCommonDenominator extends AlgoTwoNumFunction {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param a
	 *            first number
	 * @param b
	 *            second number
	 */
	public AlgoCommonDenominator(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.CommonDenominator;
	}

	@Override
	public final double computeValue(double aVal, double bVal) {
		double[] afrac = AlgoFractionText.decimalToFraction(aVal, Kernel.STANDARD_PRECISION);
		double[] bfrac = AlgoFractionText.decimalToFraction(bVal, Kernel.STANDARD_PRECISION);

		if (afrac.length < 2 || bfrac.length < 2 || Double.isNaN(afrac[1])
				|| Double.isNaN(bfrac[1])) {
			return Double.NaN;
		}
		return afrac[1] * bfrac[1] / Kernel.gcd(Math.round(afrac[1]), Math.round(bfrac[1]));
	}
}
