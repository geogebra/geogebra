package org.geogebra.common.kernel.arithmetic.filter;

import java.util.List;

import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * Combines a list of filters into a single ExpressionFilter.
 */
final public class CompositeExpressionFilter implements ExpressionFilter {

	final private List<ExpressionFilter> filters;

	/**
	 * Creates a new CompositeExpressionFilter
	 * @param filters expression filters to combine
	 */
	public CompositeExpressionFilter(List<ExpressionFilter> filters) {
		this.filters = filters;
	}

	@Override
	public boolean isAllowed(ValidExpression expression) {
		return filters.stream().allMatch(filter -> filter.isAllowed(expression));
	}
}
