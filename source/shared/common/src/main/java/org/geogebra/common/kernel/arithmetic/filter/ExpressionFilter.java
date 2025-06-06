package org.geogebra.common.kernel.arithmetic.filter;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * A filter for expressions (used to restrict what's allowed during exams).
 */
public interface ExpressionFilter {

	/**
	 * @param expressionValue An expression
	 * @return true if the expression is allowed.
	 */
	boolean isAllowed(@Nonnull ExpressionValue expressionValue);
}
