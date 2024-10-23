package org.geogebra.common.exam.restrictions.ib;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.plugin.Operation;

/**
 * Restricts derivative expressions over a variable e.g. f'(x), but allows
 * derivatives at point e.g. f'(5).
 */
public final class PointDerivativeFilter implements ExpressionFilter, Inspecting {

	@Override
	public boolean isAllowed(ValidExpression expression) {
		// Inspecting searches for derivatives over a variable
		return !expression.inspect(this);
	}

	@Override
	public boolean check(ExpressionValue v) {
		if (v.isOperation(Operation.FUNCTION) && v instanceof ExpressionNode) {
			ExpressionNode node = (ExpressionNode) v;
			ExpressionValue left = node.getLeft();
			ExpressionValue right = node.getRight();
			return left != null && left.isOperation(Operation.DERIVATIVE) && right != null
					&& right.inspect(e -> e instanceof FunctionVariable);
		}
		return false;
	}
}