package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.plugin.Operation;

public class ExpressionFilterFactory {

	public static ExpressionFilter createMmsExpressionFilter() {
		return new SimpleOperationFilter(Operation.FRACTIONAL_PART);
	}
}
