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
}
