package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * A filter for expressions (used to restrict what's allowed during exams).
 */
public interface ExpressionFilter {

	/**
	 * @param expression An expression
	 * @return true if the expression is allowed.
	 */
	boolean isAllowed(ValidExpression expression);

	/**
	 * Wraps this filter into an {@link InspectingExpressionFilter}
	 * so that every level of an {@link org.geogebra.common.kernel.arithmetic.ExpressionNode}
	 * is checked
	 * @return wrapped expression filter
	 */
	default ExpressionFilter asInspecting() {
		return new InspectingExpressionFilter(this);
	}
}
