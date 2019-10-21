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
