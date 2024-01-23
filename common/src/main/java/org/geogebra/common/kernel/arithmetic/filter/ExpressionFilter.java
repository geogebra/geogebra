package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ValidExpression;

// TODO move the entire "filter" package out of the "arithmetic" package to
// package org.geogebra.common.kernel.filter?
public interface ExpressionFilter {

	boolean isAllowed(ValidExpression expression);
}
