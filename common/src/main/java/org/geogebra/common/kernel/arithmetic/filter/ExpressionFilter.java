package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

public interface ExpressionFilter {

	boolean isAllowed(ExpressionNode expression);
}
