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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;

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
			NumberValue a, NumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.CommonDenominator;
	}

	// calc area of conic c
	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {

			double[] afrac = AlgoFractionText.DecimalToFraction(a.getDouble(),
					Kernel.STANDARD_PRECISION);
			double[] bfrac = AlgoFractionText.DecimalToFraction(b.getDouble(),
					Kernel.STANDARD_PRECISION);

			if (afrac.length < 2 || bfrac.length < 2 || Double.isNaN(afrac[1])
					|| Double.isNaN(bfrac[1])) {
				num.setUndefined();
			} else {
				num.setValue(afrac[1]
						* bfrac[1]
						/ Kernel.gcd(Math.round(afrac[1]), Math.round(bfrac[1])));
			}
		} else {
			num.setUndefined();
		}
	}

}
