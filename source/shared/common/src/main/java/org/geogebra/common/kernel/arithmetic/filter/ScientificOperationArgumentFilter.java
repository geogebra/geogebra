package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

public enum ScientificOperationArgumentFilter implements ExpressionFilter, Inspecting {

	INSTANCE;

	@Override
	public boolean isAllowed(ValidExpression expression) {
		return !expression.inspect(this);
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
