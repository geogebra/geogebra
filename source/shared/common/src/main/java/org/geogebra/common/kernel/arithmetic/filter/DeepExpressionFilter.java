/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
