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
