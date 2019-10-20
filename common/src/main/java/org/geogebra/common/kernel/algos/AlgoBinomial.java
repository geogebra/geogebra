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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.MyMath;

/**
 * Computes Binomial[a, b]
 * 
 * @author Michael Borcherds 2007-10-09
 */
public class AlgoBinomial extends AlgoTwoNumFunction {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            number of trials
	 * @param b
	 *            number of successes
	 */
	public AlgoBinomial(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.nCr;
	}

	@Override
	public final double computeValue(double aVal, double bVal) {
		return MyMath.binomial(aVal, bVal);
	}
}
