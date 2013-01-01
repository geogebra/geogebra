/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoTriangular extends AlgoDistribution {



	/**
	 * returns cumulative probability less than x for triangular distribution
	 * 
	 * @param cons cons
	 * @param label label
	 * @param a lower bound
	 * @param b upper bound
	 * @param mode mode
	 * @param x x
	 */
	public AlgoTriangular(Construction cons, String label, NumberValue a,NumberValue b, NumberValue mode, NumberValue x) {
		super(cons, label, a, b, mode, x, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.Triangular;
	}

	@Override
	public final void compute() {


		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
			double A = a.getDouble();
			double B = b.getDouble();
			double mode = c.getDouble();
			double x = d.getDouble();

			if (Double.isNaN(A) ||Double.isNaN(B) ||Double.isNaN(mode) || Double.isNaN(x) ||
					Double.isInfinite(A) ||Double.isInfinite(B) ||Double.isInfinite(mode) ||Double.isInfinite(x) ||
					mode < A || mode > B) {
				num.setUndefined();
				return;
			}

			if (x <= A) {
				num.setValue(0);
			} else if (x >= B) {
				num.setValue(1);
			} else if (x < mode) {
				num.setValue((x-A)*(x-A)/((B-A)*(mode-A)));
			} else {
				// mode <= x < B
				num.setValue(1 + (x-B)*(x-B)/((B-A)*(mode-B)));
			}

			// old hack
			// processAlgebraCommand( "If["+x+" < "+a+", 0, If["+x+" < "+c+", ("+x+" - ("+a+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+a+")), If["+x+" < "+b+", 1 + ("+x+" - ("+b+"))^2 / ("+b+" - ("+a+")) / ("+c+" - ("+b+")), 1]]]", true );


		} else
			num.setUndefined();
	}       


}



