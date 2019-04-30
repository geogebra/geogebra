package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoFunctionable;

public class NoCASDerivativeCache {
	private Function derivative;
	private ExpressionNode lastFunctionExpression;
	private GeoFunctionable f;

	public NoCASDerivativeCache(GeoFunctionable f) {
		this.f = f;
	}
	public void updateDerivative() {
		ExpressionNode currentExpression = f.getFunction().getExpression();
		if (currentExpression != lastFunctionExpression) {
			derivative = f.getFunction().getDerivativeNoCAS(1);
			lastFunctionExpression = currentExpression;
		}
	}

	public double evaluateDerivative(double a) {
		updateDerivative();
		return derivative.value(a);
	}
}
