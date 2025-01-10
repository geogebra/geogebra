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
