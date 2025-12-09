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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoFunctionable;

public class NoCASDerivativeCache {
	private Function derivative;
	private ExpressionNode lastFunctionExpression;
	private GeoFunctionable functionable;

	/**
	 * @param functionable
	 *            function
	 */
	public NoCASDerivativeCache(GeoFunctionable functionable) {
		this.functionable = functionable;
	}

	private void updateDerivative() {
		ExpressionNode currentExpression = functionable.getFunction()
				.getExpression();
		if (currentExpression != lastFunctionExpression) {
			derivative = functionable.getFunction().getDerivativeNoCAS(1);
			lastFunctionExpression = currentExpression;
		}
	}

	/**
	 * @param x
	 *            x value for which we want to get the derivative
	 * @return derivative value
	 */
	public double evaluateDerivative(double x) {
		updateDerivative();
		return derivative.value(x);
	}
}
