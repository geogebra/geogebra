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
 * Computes Max[a, b]
 * 
 * @author Markus Hohenwarterar
 */
public class AlgoMax extends AlgoTwoNumFunction {

	/**
	 * Creates new max algo
	 * 
	 * @param cons
	 *            construction
	 * @param a
	 *            first number
	 * @param b
	 *            second number
	 */
	public AlgoMax(Construction cons, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.Max;
	}

	@Override
	public final double computeValue(double aVal, double bVal) {
		return Math.max(aVal, bVal);
	}
}
