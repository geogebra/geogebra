package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * Base class for filtering expression nodes based on some criteria.
 * Look into {@link InspectingExpressionFilter} to filter at every level
 * of an expression tree.
 */
public abstract class ExpressionNodeFilter implements ExpressionFilter {
	@Override
	final public boolean isAllowed(ValidExpression expression) {
		if (!expression.isExpressionNode()) {
			return true;
		}
		return isExpressionNodeAllowed((ExpressionNode) expression);
	}

	/**
	 * Checks whether an expression node is allowed.
	 *
	 * @param expressionNode expression node to test
	 * @return true if expression node is allowed
	 */
	protected abstract boolean isExpressionNodeAllowed(ExpressionNode expressionNode);
}
