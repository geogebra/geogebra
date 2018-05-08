/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.MyMath2;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoErlang extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            distribution parameter
	 * @param b
	 *            distribution parameter
	 * @param x
	 *            variable value
	 * @param cumulative
	 *            whether to compute PDF, null for true
	 */
	public AlgoErlang(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue x, GeoBoolean cumulative) {
		super(cons, a, b, x, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.Erlang;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double k = a.getDouble();
			double l = b.getDouble();
			double x = c.getDouble();
			boolean pdf = isCumulative == null || isCumulative.getBoolean();
			if (x < 0) {
				num.setValue(0);
			} else if (pdf) {
				num.setValue(MyMath2.gammaIncomplete(k, l * x)
						/ MyMath2.factorial(k - 1));
			} else {
				num.setValue(Math.pow(l, k) * Math.pow(x, k - 1)
						* Math.exp(-l * x) / MyMath2.factorial(k - 1));
			}

			// old hack
			// command = "If[x<0,0,gamma("+k+",("+l+")x)/("+k+"-1)!]";

		} else {
			num.setUndefined();
		}
	}

}
