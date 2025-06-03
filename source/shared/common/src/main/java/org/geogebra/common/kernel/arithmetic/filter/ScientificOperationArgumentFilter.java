package org.geogebra.common.kernel.arithmetic.filter;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyList;

/**
 * Operation argument filter for the scientific app.
 */
public enum ScientificOperationArgumentFilter implements ExpressionFilter {

	INSTANCE;

	@Override
	public boolean isAllowed(@Nonnull ExpressionValue expression) {
		return !expression.any(exp ->
				exp.isExpressionNode() && !exp.isLeaf() && containsList((ExpressionNode) exp)
						|| isMatrix(exp));
	}

	private boolean isMatrix(ExpressionValue expression) {
		return expression instanceof MyList && ((MyList) expression).isMatrix();
	}

	private boolean containsList(ExpressionNode expression) {
		return expression.getLeft().evaluatesToList() || expression.getRight().evaluatesToList();
	}
}
