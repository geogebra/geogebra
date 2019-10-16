/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Area of polygon P[0], ..., P[n]
 *
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
