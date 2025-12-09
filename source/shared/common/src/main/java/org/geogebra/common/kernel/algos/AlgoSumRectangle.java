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
