package org.geogebra.common.kernel.arithmetic.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoSymbolic;

/**
 * Filter that traverses the expression tree, and applies the filter on every expression node found.
 * Filters return early, meaning that if an expression is found that needs to be filtered,
 * further evaluation halts.
 */
final public class DeepExpressionFilter implements ExpressionFilter {

	private final ExpressionFilter wrappedFilter;
	private final List<AllowedExpressionsProvider> providers = new ArrayList<>();

	/**
	 * Creates a new ExpressionNodeOperationFilter.
	 * @param wrappedFilter to apply on every expression node.
	 */
	public DeepExpressionFilter(@Nonnull ExpressionFilter wrappedFilter) {
		this.wrappedFilter = wrappedFilter;
	}

	@Override
	public boolean isAllowed(@Nonnull ExpressionValue expression) {
		HashSet<ExpressionValue> allowedExpressionValues = new HashSet<>();

		for (ExpressionValue child : expression) {
			// Provide exceptions to this node
			for (AllowedExpressionsProvider provider : providers) {
				Collection<ExpressionValue> allowedExpressions =
						provider.provideAllowedExpressionValues(child);
				if (allowedExpressions != null) {
					allowedExpressionValues.addAll(allowedExpressions);
				}
			}

			// The expression is restricted if the child expression
			// is restricted and not allowed as an exception.
			if (!wrappedFilter.isAllowed(child) && !allowedExpressionValues.contains(child)) {
				return false;
			}

			// Recursively iterate over the output of the GeoSymbolic object
			if (child instanceof GeoSymbolic) {
				if (!isAllowed(((GeoSymbolic) child).getOutputExpression())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Adds an {@link AllowedExpressionsProvider}. This provider is asked to provide with
	 * a collection of allowed expression, at every step of the tree-traversal
	 * based on the current node and these values will be allowed,
	 * no matter what the wrapped filter reports.
	 * @param provider provider
	 * @return this filter
	 */
	public @Nonnull DeepExpressionFilter allowWhen(@Nonnull AllowedExpressionsProvider provider) {
		providers.add(provider);
		return this;
	}
}
