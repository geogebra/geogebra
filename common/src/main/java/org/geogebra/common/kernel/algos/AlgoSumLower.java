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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.Cloner;

/**
 * upper sum of function f in interval [a, b] with n intervals
 */
public class AlgoSumLower extends AlgoFunctionAreaSums {

	/**
	 * Creates lower sum
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
	public AlgoSumLower(Construction cons, String label, GeoFunction f,
			NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, SumType.LOWERSUM);
		cons.registerEuclidianViewCE(this);
	}

	private AlgoSumLower(GeoNumberValue a, GeoNumberValue b, NumberValue n,
			double[] vals, double[] borders, Construction cons1) {
		super(a, b, n, SumType.LOWERSUM, vals, borders, cons1);
		cons.registerEuclidianViewCE(this);
	}

	public AlgoSumLower copy() {
		return new AlgoSumLower((GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel), this.getN()
						.copy(), Cloner.clone(getValues()),
				Cloner.clone(getLeftBorder()), cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.LowerSum;
	}

}
