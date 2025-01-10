package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * OperationArgumentFilter for the Graphing app.
 */
public enum GraphingOperationArgumentFilter implements ExpressionFilter, Inspecting {

	INSTANCE;

	@Override
	public boolean isAllowed(ValidExpression expression) {
		return !expression.inspect(this);
	}

	@Override
	public boolean check(ExpressionValue expression) {
		if (!expression.isExpressionNode()) {
			return false;
		}
		ExpressionNode node = (ExpressionNode) expression;
		switch (node.getOperation()) {
		case ABS:
			return !allowAbs(node.getLeft());
		case MULTIPLY:
			return isInnerProduct(node.getLeft(), node.getRight());
		case VECTORPRODUCT:
			return true;
		case POWER:
			return isPowerInnerProduct(node.getLeft(), node.getRight());
		default:
			return false;
		}
	}

	private boolean isPowerInnerProduct(ExpressionValue left, ExpressionValue right) {
		return left.evaluatesToNDVector() && right.evaluatesToNumber(true);
	}

	private boolean isInnerProduct(ExpressionValue left,
			ExpressionValue right) {
		return left.evaluatesToNDVector() && right.evaluatesToNDVector();
	}

	private boolean allowAbs(ExpressionValue left) {
		return left.evaluatesToNumber(true)
				|| left instanceof FunctionNVar;
	}
}
