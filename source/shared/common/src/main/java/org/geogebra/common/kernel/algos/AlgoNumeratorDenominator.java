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
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoNumeratorDenominator extends AlgoElement {

	private GeoNumeric f; // input
	private GeoNumeric g; // output
	private Commands type;

	private ExpressionValue[] fraction = new ExpressionValue[2];

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

		ExpressionNode def = f.getDefinition();

		ExpressionValue top = null;
		ExpressionValue bottom = null;

		// check if it's possible to get as an exact fraction!
		if (def != null) {
			if (def.isSimpleFraction()) {

				top = def.getLeft();
				bottom = def.getRight();

			} else {

				def.getFraction(fraction, true);

				if (fraction[0] != null && fraction[1] != null) {
					top = fraction[0];
					bottom = fraction[1];
				}
			}

			if (top != null && bottom != null && DoubleUtil.isInteger(top.evaluateDouble())
					&& DoubleUtil.isInteger(bottom.evaluateDouble())) {
				// cancel down to lowest terms
				long num = (long) top.evaluateDouble();
				long den = (long) bottom.evaluateDouble();
				long gcd = Math.abs(Kernel.gcd(num, den));
				int denSign = den < 0 ? -1 : 1;
				num = num * denSign;
				den = den * denSign;

				long val;
				if (gcd == 0) {
					val = (type == Commands.Numerator) ? num : den;
				} else {
					val = (type == Commands.Numerator) ? num / gcd : den / gcd;
				}

				g.setValue(val);
				return;
			}

		}

		// regular decimal -> find approximate fraction
		double[] frac = AlgoFractionText.decimalToFraction(f.getDouble(),
				Kernel.STANDARD_PRECISION);
		if (frac.length < 2) {
			g.setUndefined();
			return;
		}
		g.setValue(frac[type == Commands.Numerator ? 0 : 1]);

	}

}
