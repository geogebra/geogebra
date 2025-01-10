/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Rectangle sum of function f in interval [a, b] with n intervals and dividing
 * point d. ( [0,1] from start to end of interval.)
 */
public class AlgoSumRectangle extends AlgoFunctionAreaSums {

	/**
	 * Creates new rectangle sum
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param f
	 *            function
	 * @param a
	 *            lower bound
	 * @param b
	 *            upper bound
	 * @param n
	 *            number of columns
	 * @param d
	 *            0 for left sum, 1 for right sum
	 */
	public AlgoSumRectangle(Construction cons, String label, GeoFunctionable f,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue n,
			GeoNumberValue d) {
		super(cons, label, f, a, b, n, d, SumType.RECTANGLESUM);
	}

	private AlgoSumRectangle(GeoFunction f, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue n, GeoNumberValue d) {
		super(f, a, b, n, d);
	}

	@Override
	public Commands getClassName() {
		return Commands.RectangleSum;
	}

	@Override
	public AlgoSumRectangle copy() {
		return new AlgoSumRectangle((GeoFunction) this.getF().copy(),
				(GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel),
				this.getN().copy(),
				(GeoNumberValue) this.getD().deepCopy(kernel));
	}

}
