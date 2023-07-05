package org.geogebra.common.kernel.arithmetic.filter;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

public class ScientificOperationArgumentFilter implements OperationArgumentFilter {
	@Override
	public boolean isAllowed(Operation operation, ExpressionValue left, ExpressionValue right) {
		return !(left.evaluatesToList() || right.evaluatesToList());
	}
}
