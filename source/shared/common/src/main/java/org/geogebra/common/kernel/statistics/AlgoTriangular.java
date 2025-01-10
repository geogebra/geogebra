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

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoTriangular extends AlgoDistribution {

	/**
	 * returns cumulative probability less than x for triangular distribution
	 * 
	 * @param cons
	 *            cons
	 * @param a
	 *            lower bound
	 * @param b
	 *            upper bound
	 * @param mode
	 *            mode
	 * @param x
	 *            x
	 * @param cumulative
	 *            cumulative?
	 */
	public AlgoTriangular(Construction cons, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue mode, GeoNumberValue x, GeoBoolean cumulative) {
		super(cons, a, b, mode, x, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.Triangular;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined()) {
			double A = a.getDouble();
			double B = b.getDouble();
			double mode = c.getDouble();
			double x = d.getDouble();

			if (Double.isNaN(A) || Double.isNaN(B) || Double.isNaN(mode)
					|| Double.isNaN(x) || Double.isInfinite(A)
					|| Double.isInfinite(B) || Double.isInfinite(mode)
					|| Double.isInfinite(x) || mode < A || mode > B) {
				num.setUndefined();
				return;
			}
			boolean pdf = this.isCumulative == null
					|| this.isCumulative.getBoolean();
			if (x <= A) {
				num.setValue(0);
			} else if (x >= B) {
				num.setValue(pdf ? 1 : 0);
			} else if (x < mode) {
				double halfDensity = (x - A) / ((B - A) * (mode - A));
				num.setValue(pdf ? (x - A) * halfDensity : 2 * halfDensity);
			} else {
				// mode <= x < B
				double halfDensity = (x - B) / ((B - A) * (mode - B));
				num.setValue(
						pdf ? 1 + (x - B) * halfDensity : 2 * halfDensity);
			}

			// old hack
			// processAlgebraCommand(
			// "If["+x+" < "+a+", 0, If["+x+" < "+c+", ("+x+" - ("+a+"))^2 /
			// ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If["+x+" < "+b+", 1 +
			// ("+x+" - ("+b+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+b+")),
			// 1]]]",
			// true );

		} else {
			num.setUndefined();
		}
	}

}
