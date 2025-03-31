package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * Filter that traverses the expression tree, and applies the filter on every expression node found.
 * Filters return early, meaning that if an expression is found that needs to be filtered,
 * further evaluation halts.
 */
final public class InspectingExpressionFilter implements ExpressionFilter {

	final Inspecting inspecting = new ExpressionNodeInspecting();
	final ExpressionFilter filter;

	/**
	 * Creates a new ExpressionNodeOperationFilter.
	 * @param filter to apply on every expression node.
	 */
	public InspectingExpressionFilter(ExpressionFilter filter) {
		this.filter = filter;
	}

	@Override
	public boolean isAllowed(ValidExpression expression) {
		return !expression.any(inspecting);
	}

	private class ExpressionNodeInspecting implements Inspecting {

		@Override
		public boolean check(ExpressionValue expression) {
			if (expression instanceof ValidExpression) {
				return !filter.isAllowed((ValidExpression) expression);
			}
			return false;
		}
	}
}
