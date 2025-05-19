package org.geogebra.common.kernel.arithmetic.filter;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;

/**
 * Operation argument filter for the scientific app.
 */
public enum ScientificOperationArgumentFilter implements ExpressionFilter, Inspecting {

	INSTANCE;

	@Override
	public boolean isAllowed(@Nonnull ExpressionValue expression) {
		return !expression.any(this);
	}

	@Override
	public boolean check(ExpressionValue expression) {
		return expression.isExpressionNode() && !expression.isLeaf()
				&& containsList((ExpressionNode) expression);
	}

	private boolean containsList(ExpressionNode expression) {
		return expression.getLeft().evaluatesToList() || expression.getRight().evaluatesToList();
	}
}
