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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;

/**
 * Computes Div[a, b]
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class AlgoDiv extends AlgoTwoNumFunction {

	public AlgoDiv(Construction cons, String label, NumberValue a, NumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.Div;
	}

	// calc area of conic c
	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {

			double numerator = Kernel.checkInteger(a.getDouble());
			double denominator = Kernel.checkInteger(b.getDouble());

			if (Math.abs(numerator) > MyDouble.LARGEST_INTEGER
					|| Math.abs(denominator) > MyDouble.LARGEST_INTEGER) {
				num.setUndefined();
				return;
			}

			double fraction = numerator / denominator;
			double integer = Math.round(fraction);
			if (Kernel.isEqual(fraction, integer)) {
				num.setValue(integer);
			} else if (denominator > 0) {
				double div = Math.floor(fraction);
				num.setValue(div);
			} else {
				double div = Math.ceil(fraction);
				num.setValue(div);
			}
		} else {
			num.setUndefined();
		}
	}

}
