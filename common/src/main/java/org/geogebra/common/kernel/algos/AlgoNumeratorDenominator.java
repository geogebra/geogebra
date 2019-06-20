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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoNumeratorDenominator extends AlgoElement {

	private GeoNumeric f; // input
	private GeoNumeric g; // output
	private Commands type;

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            number
	 * @param type
	 *            numerator or denominator
	 */
	public AlgoNumeratorDenominator(Construction cons, GeoNumeric f,
			Commands type) {
		super(cons);
		this.f = f;
		this.type = type;
		g = new GeoNumeric(cons);

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return type;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { f };

		super.setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return numerator or denominator
	 */
	public GeoElement getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}

		// check if it's already a fraction!
		if (f.getDefinition() != null && f.getDefinition().isSimpleFraction()) {

			ExpressionNode def = f.getDefinition();

			long num = (long) def.getLeft().evaluateDouble();
			long den = (long) def.getRight().evaluateDouble();
			long gcd = Kernel.gcd(num, den);
			
			double val = (type == Commands.Numerator) ? num / gcd : den / gcd;
			
			g.setValue(val);
			return;

		}

		double[] frac = AlgoFractionText.decimalToFraction(f.getDouble(),
				Kernel.STANDARD_PRECISION);
		if (frac.length < 2) {
			g.setUndefined();
			return;
		}
		g.setValue(frac[type == Commands.Numerator ? 0 : 1]);

	}

}
