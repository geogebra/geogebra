package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

public class IntervalFunctionSupport {
	private static final UnsupportedOperatorChecker
			operatorChecker = new UnsupportedOperatorChecker();

	/**
	 *
	 * @param geo to check.
	 * @return true if the geo is a function
	 * and supported by our interval arithmetic implementation.
	 */
	public static boolean isSupported(GeoElement geo) {
		if (!(geo instanceof GeoFunction)) {
			return false;
		}

		return isOperationSupported(((GeoFunction) geo).getFunctionExpression());
	}

	static boolean isOperationSupported(ExpressionNode node) {
		if (node == null) {
			return false;
		}

		return !hasMoreVariables(node) && !node.inspect(operatorChecker);
	}

	private static boolean hasMoreVariables(ExpressionNode node) {
		if (node == null) {
			return false;
		}
		return node.inspect(new MultipleVariableChecker());
	}
}
