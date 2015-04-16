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
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.Commands;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoDenominator extends AlgoNumerator {

	/**
	 * Creates new algo for computing denominator
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param func
	 *            rational function
	 */
	public AlgoDenominator(Construction cons, String label, FunctionalNVar func) {
		super(cons, label, func);
	}

	public AlgoDenominator(Construction cons, FunctionalNVar func) {
		super(cons, func);
	}

	@Override
	protected ExpressionValue getPart(ExpressionValue[] node) {
		return node[1];
	}

	@Override
	public Commands getClassName() {
		return Commands.Denominator;
	}
}
