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

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Left sum of function f in interval [a, b] with n intervals
 */
public class AlgoSumLeft extends AlgoFunctionAreaSums {

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
	public AlgoSumLeft(Construction cons, String label, GeoFunctionable f,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue n) {
		super(cons, label, f, a, b, n, SumType.LEFTSUM);
		cons.registerEuclidianViewCE(this);
	}

	private AlgoSumLeft(GeoNumberValue a, GeoNumberValue b, GeoNumberValue n,
			double[] vals, double[] borders, Construction cons1) {
		super(a, b, n, SumType.LEFTSUM, vals, borders, cons1);
	}

	@Override
	public AlgoSumLeft copy() {
		return new AlgoSumLeft((GeoNumberValue) this.getA().deepCopy(kernel),
				(GeoNumberValue) this.getB().deepCopy(kernel),
				this.getN().copy(), Cloner.clone(getValues()),
				Cloner.clone(getLeftBorder()), cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.LeftSum;
	}

}
