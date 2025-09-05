package org.geogebra.common.kernel.interval.function;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.plugin.Operation;

/**
 * Checker to determine if an operation is supported by the interval arithmetic.
 */
public class UnsupportedOperatorChecker implements Inspecting {

	private GeoFunctionConverter converter = null;

	@Override
	public boolean check(ExpressionValue v) {
		ExpressionNode wrap = v.wrap();
		Operation operation = wrap.getOperation();

		if (operation == Operation.MULTIPLY) {
			return checkMultiply(wrap);
		} else if (operation == Operation.POWER) {
			return checkPower(wrap);
		}
		if (converter == null) {
			converter = wrap.getKernel().getFunctionConverter();
		}
		return !converter.isSupportedOperation(operation);
	}

	private boolean checkMultiply(ExpressionNode node) {
		return isVector(node.getLeft()) ^ isVector(node.getRight());
	}

	private boolean isVector(ExpressionValue node) {
		return node.evaluatesToNDVector() || node.evaluatesToNonComplex2DVector();
	}

	private boolean checkPower(ExpressionNode node) {
		double power = node.getRight().evaluateDouble();
		if (Double.isNaN(power)) {
			return true;
		}

		return power >= 100;
	}
}
