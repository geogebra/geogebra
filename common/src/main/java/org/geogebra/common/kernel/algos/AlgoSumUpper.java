/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * upper sum of function f in interval [a, b] with n intervals
 */
public class AlgoSumUpper extends AlgoFunctionAreaSums {

	/**
	 * Creates new upper sum
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
	 */
	public AlgoSumUpper(Construction cons, String label, GeoFunctionable f,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue n) {
		super(cons, label, f, a, b, n, SumType.UPPERSUM);
		cons.registerEuclidianViewCE(this);
	}

	private AlgoSumUpper(GeoNumberValue a, GeoNumberValue b, GeoNumberValue n,
			double[] vals, double[] borders, Construction cons1) {
		super(a, b, n, SumType.UPPERSUM, vals, borders, cons1);
	}

	@Override
	public Commands getClassName() {
		return Commands.UpperSum;
	}

	@Override
	public AlgoSumUpper copy() {
		return new AlgoSumUpper((GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel),
				this.getN().copy(), Cloner.clone(getValues()),
				Cloner.clone(getLeftBorder()), cons);
	}

}
