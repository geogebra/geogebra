package org.geogebra.common.kernel.arithmetic.filter;

import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * Provides a collection of allowed expressions based on the
 * current expression value for a {@link DeepExpressionFilter}.
 * At every step of the expression node tree traversal, {@link DeepExpressionFilter} asks this
 * to provider expressions that are allowed, even if otherwise would be restricted by the filter.
 */
@FunctionalInterface
public interface AllowedExpressionsProvider {

	/**
	 * Provides the allowed child of value to bypass restrictions.
	 * @param value expression value provide allowed values from
	 * @return Optionally a collection of values that are allowed
	 */
	@CheckForNull Collection<ExpressionValue> provideAllowedExpressionValues(@Nonnull ExpressionValue value);
}
